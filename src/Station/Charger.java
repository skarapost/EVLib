package Station;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Station.ChargingStation;
import Events.ChargingEvent;
import java.util.Date;

public class Charger
{
    private final int id;
    private final String kindOfCharging;
    private boolean busy;
    private int commitTime;
    private ChargingEvent event;
    private ChargingStation station;

    /**
     * Constructor of Charger class.
     * @param id Id of the Charger.
     * @param station The ChargingStation in which the Charger belongs.
     * @param kindOfCharging The kind of charging the Charger supports.
     */
    public Charger(int id,ChargingStation station,String kindOfCharging)
    {
        this.id = id;
        this.kindOfCharging = kindOfCharging;
        busy = false;
        commitTime = 0;
        this.station = station;
        event = null;
    }

    /**
     * Executes the ChargingEvent. It lasts as much as ChargingEvent's charging time demands. 
     * The energy that the ChargingEvent needs is subtracted from the total energy of the ChargingStation.
     * The mode of charging event gets 4. At the end if the automatic queue's handling is activated, 
     * the Charger checks the list.
     * @param ev The ChargingEvent that is going to be executed.
     */
    public final void executeChargingEvent(ChargingEvent ev)
    {
        new Thread()
        {
        @Override
        public void run(){
        float sdf;
        Date d1 = new Date();
        int st = (int) (d1.getTime()/1000);
        int en;
        sdf = ev.reEnergyToBeReceived();
        ev.reElectricVehicle().reBattery().setRemAmount(sdf + ev.reElectricVehicle().reBattery().reRemAmount());
        if (ev.reElectricVehicle().reDriver() != null)
        {
            ev.reElectricVehicle().reDriver().setDebt(ev.reElectricVehicle().reDriver().reDebt() + station.reChargingStationHandler().calculatePrice(ev));
        }
        for (int i = 0; i < ev.reStation().reSources().length; i++)
        {
            if (ev.reEnergyToBeReceived() < ev.reStation().reMap().get(ev.reStation().reSource(i)))
            {
                float ert = ev.reStation().reMap().get(ev.reStation().reSource(i)) - sdf;
                ev.reStation().setSpecificAmount(ev.reStation().reSource(i), ert);
                break;
            }
            else
            {
                sdf = ev.reEnergyToBeReceived() - ev.reStation().reMap().get(ev.reStation().reSource(i));
                ev.reStation().setSpecificAmount(ev.reStation().reSource(i), 0);
            }
        }
        do
        {
            Date d2 = new Date();
            en = (int) (d2.getTime()/1000);
        }while(en-st<ev.reChargingTime());
        ev.setMode(4);
        if (station.reUpdateMode() == 0)
            station.reChargingStationHandler().checkUpdatePredefinedSpace();
        else
            station.reChargingStationHandler().checkUpdateMadeSpace();
        changeSituation();
        System.out.println("The charging took place successfully");
        setChargingEvent(null);
        if (station.reQueueHandling())
        {
            handleQueueEvents();
        }
            }
                }.start();
    }

    /**
     * Changes the situation of the Charger.
     */
    public final void changeSituation()
    {
        busy = !busy;
    }

    /**
     * Returns the situation of the Charger.
     * @return True if it is busy, false if it is not busy.
     */
    public final boolean reBusy()
    {
        return busy;
    }

    /**
     * Returns the kind of charging.
     * @return The kind of charging the Charger supports.
     */
    public final String reKind()
    {
        return kindOfCharging;
    }

    /**
     * Sets a ChargingEvent in the Charger.
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    public final void setChargingEvent(ChargingEvent ev)
    {
        event = ev;
    }

    /**
     * Handles the list. It executes (if any) the first element of the list.
     */
    public final void handleQueueEvents()
    {
        if ("fast".equals(reKind()))
        {
            if (station.reFast().reSize() != 0)
            {
                station.reFast().takeFirst().preProcessing();
                if (station.reFast().takeFirst().reMode() == 2)
                    station.reFast().removeFirst().execution();
            }
        }
        else if ("slow".equals(reKind()))
        {
            if (station.reSlow().reSize() != 0)
            {
                station.reSlow().takeFirst().preProcessing();
                if (station.reSlow().takeFirst().reMode() == 2)
                    station.reSlow().removeFirst().execution();
            }
        }
    }

    /**
     * Returns the ChargingEvent of the Charger.
     * @return The ChargingEvent which is linked with the Charger.
     */
    public final ChargingEvent reChargingEvent()
    {
        return event;
    }

    /**
     * Returns the amount of time the Charger is going to be busy.
     * @return The time that the Charger is going to be busy.
     */
    public final long reCommitTime()
    {
        return commitTime;
    }

    /**
     * Sets the time the Charger is going to be busy.
     * @param time The commit time.
     */
    public final void setCommitTime(int time)
    {
        commitTime = time;
    }

    /**
     * Returns the id of the Charger.
     * @return The id of Charger.
     */
    public final int reId()
    {
        return id;
    }
}