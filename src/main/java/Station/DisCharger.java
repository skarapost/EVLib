package Station;


import Events.DisChargingEvent;
import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author Sotiris Karapostolakis
 */

public class DisCharger
{
    private ChargingStation station;
    private int id;
    private DisChargingEvent e;
    private boolean busy;
    private long busyTime;

    public DisCharger(int id, ChargingStation station)
    {
        this.id = id;
        this.busy = false;
        this.busyTime = 0;
        this.station = station;
        this.e = null;
        if (station.reSpecificAmount("discharging") == 0f)
            station.setSpecificAmount("discharging", 0f);
    }

    private void startRun()
    {
        StopWatch d1 = new StopWatch();
        d1.start ();
        long st = d1.getTime();
        long en;
        e.reElectricVehicle().reBattery().setRemAmount(e.reElectricVehicle().reBattery().reRemAmount() - e.reAmEnerg());
        e.reElectricVehicle().reDriver().setProfit(e.reElectricVehicle().reDriver().reProfit() + e.reAmEnerg()*station.reDisUnitPrice());
        float energy = (float) station.reMap().get("discharging") + e.reAmEnerg();
        station.setSpecificAmount("discharging", energy);
        StopWatch d2 = new StopWatch();
        d2.start();
        do
        {
            en = d2.getTime();
        }while(en - st < e.reDisChargingTime());
        System.out.println ("The discharging took place succesfully");
        e.setCondition("finished");
        station.checkForUpdate();
        setBusyTime(busyTime);
        changeSituation();
        setDisChargingEvent(null);
        if (station.reQueueHandling())
            handleQueueEvents();
    }

    /**
     * Executes the DisChargingEvent. It lasts as much as DisChargingEvent's
     * discharging time demands. The energy that the discharging event needs is
     * subtracted from the total energy of the charging station. The condition of
     * DisChargingEvent gets "finished". In the end if the automatic queue's handling
     * is activated, the DisCharger checks the waiting list.
     */
    public void executeDisChargingEvent()
    {
        new Thread(() -> {
            StopWatch d1 = new StopWatch();
            d1.start();
            long st = d1.getTime();
            long en;
            e.reElectricVehicle().reBattery().setRemAmount(e.reElectricVehicle().reBattery().reRemAmount() - e.reAmEnerg());
            e.reElectricVehicle().reDriver().setProfit(e.reElectricVehicle().reDriver().reProfit() + e.reAmEnerg() * station.reDisUnitPrice());
            float energy = (float) station.reMap().get("discharging") + e.reAmEnerg();
            station.setSpecificAmount("discharging", energy);
            StopWatch d2 = new StopWatch();
            d2.start();
            do {
                en = d2.getTime();
            } while (en - st < e.reDisChargingTime());
            System.out.println("The discharging took place succesfully");
            e.setCondition("finished");
            station.checkForUpdate();
            setBusyTime(busyTime);
            changeSituation();
            setDisChargingEvent(null);
            if (station.reQueueHandling())
                handleQueueEvents();
        }).start();
    }

    /**
     * Changes the situation of the DisCharger.
     */
    public void changeSituation()
    {
        this.busy = !busy;
    }

    /**
     * Returns the situation of the DisCharger.
     * @return Returns true if it is busy, false if it is not busy.
     */
    public boolean reBusy()
    {
        return busy;
    }

    /**
     * Sets a DisChargingEvent to the DisCharger.
     * @param e The DisChargingEvent that is going to be linked with the DisCharger.
     */
    public void setDisChargingEvent(DisChargingEvent e)
    {
        this.e = e;
    }

    /**
     * Sets the time the DisCharger is going to be occupied.
     * @param time The busy time.
     */
    public void setBusyTime(long time)
    {
        this.busyTime = time;
    }

    /**
     * Returns the DisChargingEvent the DisCharger is linked with.
     * @return The DisChargingEvent od the DisCharger.
     */
    public DisChargingEvent reDisChargingEvent()
    {
        return e;
    }

    /**
     * Returns the busy time.
     * @return The busy time.
     */
    public long reBusyTime()
    {
        return busyTime;
    }

    /**
     * Returns the id of the DisCharger.
     * @return The id of the DisCharger.
     */
    public int reId()
    {
        return id;
    }

    /**
     * Handles the list for the discharging. Takes the first DisChargingEvent
     * executes the preProcessing function and then if the mode is 2 runs
     * the execution function.
     */
    public void handleQueueEvents()
    {
        if (station.reDischarging().rSize() != 0)
        {
            station.reDischarging().reFirst().preProcessing();
            if (station.reDischarging().reFirst().reCondition().equals("ready"))
                station.reDischarging().moveFirst().execution();
        }
    }
}