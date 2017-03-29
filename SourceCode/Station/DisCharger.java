package Station;


import Station.ChargingStation;
import Events.DisChargingEvent;
import java.util.Date;

/**
 *
 * @author Sotiris Karapostolakis
 */

public class DisCharger {
    private ChargingStation station;
    private int id;
    private DisChargingEvent event;
    private boolean busy;
    private long busyTime;

    /**
     * Constructor of DisCharger class.
     * @param id The id of the DisCharger.
     * @param station The ChargingStation the DisCharger belongs.
     */
    public DisCharger(int id,ChargingStation station)
    {
        this.id = id;
        busy = false;
        busyTime = 0;
        this.station = station;
        event = null;
        if (station.reSpecificAmount("discharging") == 0f)
            station.setSpecificAmount("discharging", 0f);
    }

    /**
     * Executes the DisChargingEvent. It lasts as much as DisChargingEvent's 
     * discharging time demands. The energy that the discharging event needs is 
     * subtracted from the total energy of the charging station. The mode of
     * DisChargingEvent gets 4. At the end if the automatic queue's handling 
     * is activated, the DisCharger checks the list.
     * @param h The DisChargingEvent that is going to be executed.
     */
    public final void executeDisChargingEvent(DisChargingEvent h)
    {
        new Thread(){
            @Override
            public void run(){
        Date d1 = new Date();
        int st = (int) (d1.getTime()/1000);
        int en;
        h.reElectricVehicle().reBattery().setRemAmount(h.reElectricVehicle().reBattery().reRemAmount() - h.reAmEnerg());
        h.reElectricVehicle().reDriver().setProfit(h.reElectricVehicle().reDriver().reProfit() + h.reAmEnerg()*station.reDisUnitPrice());
        float energy = station.reMap().get("discharging") + h.reAmEnerg();
        station.setSpecificAmount("discharging", energy);
        do{
            Date d2 = new Date();
            en = (int) (d2.getTime()/1000);
        }while(en-st<h.reDisChargingTime());
        h.setMode(4);
        if (station.reUpdateMode() == 0)
            station.reChargingStationHandler().checkUpdatePredefinedSpace();
        else
            station.reChargingStationHandler().checkUpdateMadeSpace();
        setBusyTime(busyTime);
        changeSituation();
        System.out.println("The discharging took place successfully.");
        setDisChargingEvent(null);
        if (station.reQueueHandling())
            handleQueueEvents();
            }
        }.start();
    }

    /**
     * Changes the situation of the DisCharger.
     */
    public final void changeSituation()
    {
        busy = !busy;
    }

    /**
     * Returns the situation of the DisCharger.
     * @return Returns true if it is busy, false if it is not busy.
     */
    public final boolean reBusy()
    {
        return busy;
    }

    /**
     * Sets a DisChargingEvent to the DisCharger.
     * @param f The DisChargingEvent that is going to be linked with the DisCharger.
     */
    public final void setDisChargingEvent(DisChargingEvent f)
    {
        event = f;
    }

    /**
     * Sets the time the DisCharger is going to be occupied.
     * @param time The busy time. 
     */
    public final void setBusyTime(long time)
    {
        busyTime = time;
    }

    /**
     * Returns the DisChargingEvent the DisCharger is linked with.
     * @return The DisChargingEvent od the DisCharger.
     */
    public final DisChargingEvent reDisChargingEvent()
    {
        return event;
    }

    /**
     * Returns the busy time.
     * @return The busy time.
     */
    public final long reBusyTime()
    {
        return busyTime;
    }

    /**
     * Returns the id of the DisCharger.
     * @return The id of the DisCharger.
     */
    public final int reId()
    {
        return id;
    }

    /**
     * Handles the list for the discharging. Takes the first DisChargingEvent 
     * executes the preProcessing function and then if the mode is 2 runs 
     * the execution function.
     */
    public final void handleQueueEvents()
    {
        if (station.reDischarging().rSize() != 0)
        {
            station.reDischarging().reFirst().preProcessing();
            if (station.reDischarging().reFirst().reMode() == 2)
                station.reDischarging().moveFirst().execution();
        }
    }
}
