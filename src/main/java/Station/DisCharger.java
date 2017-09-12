package Station;

import Events.DisChargingEvent;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;

public class DisCharger
{
    private ChargingStation station;
    private int id;
    private DisChargingEvent e;
    private boolean busy;
    private long commitTime;
    private long timestamp;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public DisCharger(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.busy = false;
        this.station = station;
        this.e = null;
        if (station.getSpecificAmount("discharging") == 0f)
            station.setSpecificAmount("discharging", 0f);
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
            StopWatch d2 = new StopWatch();
            d2.start();
            do {
                en = d2.getTime();
            } while (en - st < e.getDisChargingTime());
            e.getElectricVehicle().getBattery().setRemAmount(e.getElectricVehicle().getBattery().getRemAmount() - e.getAmountOfEnergy());
            e.getElectricVehicle().getDriver().setProfit(e.getElectricVehicle().getDriver().getProfit() + e.getProfit());
            double energy = station.getMap().get("discharging") + e.getAmountOfEnergy();
            station.setSpecificAmount("discharging", energy);
            System.out.println("The discharging " + e.getId() + " completed succesfully");
            e.setCondition("finished");
            changeSituation();
            setDisChargingEvent(null);
            commitTime = 0;
            if (station.getQueueHandling())
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
     * @return Returns true if it is busy, false if it is not busy.
     */
    public boolean getBusy()
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
    public void setCommitTime(long time)
    {
        timestamp = station.getTime();
        this.commitTime = time;
    }

    /**
     * @return The DisChargingEvent od the DisCharger.
     */
    public DisChargingEvent getDisChargingEvent()
    {
        return e;
    }

    /**
     * @return The busy time.
     */
    public long getElapsedCommitTime()
    {
        long diff = station.getTime() - timestamp;
        if (commitTime - diff >= 0)
            return commitTime - diff;
        else
            return 0;
    }

    /**
     * @return The id of the DisCharger.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Handles the list for the discharging. Takes the first DisChargingEvent
     * executes the preProcessing function and then runs
     * the execution function.
     */
    public void handleQueueEvents()
    {
        if (station.getDischarging().getSize() != 0)
        {
            DisChargingEvent e = (DisChargingEvent) station.getDischarging().moveFirst();
            e.preProcessing();
            e.execution();
        }
    }
}