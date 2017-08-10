package Station;

import EV.Battery;
import Events.ChargingEvent;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeHandler
{
    private int id;
    private ChargingStation station;
    private ChargingEvent e;
    private boolean busy;
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private long commitTime;
    private long timestamp;

    public ExchangeHandler(ChargingStation station)
    {
        this.id = idGenerator.getAndIncrement();
        this.station = station;
        e = null;
    }

    /**
     * @return The id of the ExchangeHandler.
     */
    public int reId()
    {
        return id;
    }

    /**
     * Links a ChargingEvent with the ExchangeHandler.
     * @param e The ChargingEvent to be linked.
     */
    public void joinChargingEvent(ChargingEvent e)
    {
        this.e = e;
    }

    /**
     * @return The ChargingEvent of the ExchangeHandler.
     */
    public ChargingEvent reChargingEvent()
    {
        return e;
    }

    /**
     * Executes the ChargingEvent(exchange of battery). It lasts as much as ChargingEvent's
     * exchange time demands. Removes the Battery of the ElectricVehicle and it adds to the
     * batteries linked with the ChargingStation. Takes the st2 Battery from those which are linked
     * with the ChargingStation in the ArrayList structure and puts in the ElectricVehicle.
     * The condition of ChargingEvent gets "finished". In the end if the automatic queue's handling
     * is activated, the ExchangeHandler checks the list.
     * @param st2 The slot of the Battery in the ArrayList structure with the batteries
     * that is going to be used.
     */
    public void executeExchange(int st2)
    {
        new Thread (() -> {
            StopWatch d1 = new StopWatch();
            d1.start ();
            long st = d1.getTime();
            long en;
            Battery temp;
            temp = e.reElectricVehicle().reBattery();
            e.reElectricVehicle().vehicleJoinBattery(station.reBatteries().get(st2));
            station.reBatteries().remove(st2);
            station.joinBattery(temp);
            e.reElectricVehicle().reDriver().setDebt(e.reElectricVehicle().reDriver().reDebt() + station.calculatePrice(e));
            StopWatch d2 = new StopWatch();
            d2.start();
            do
            {
                en = d2.getTime();
            }while(en - st < e.reChargingTime());
            System.out.println ("The exchange took place successfully");
            e.setCondition("finished");
            if (station.reUpdateMode())
                station.checkForUpdate();
            changeSituation();
            joinChargingEvent(null);
            setCommitTime(0);
            if (station.reQueueHandling())
                handleQueueEvents();
        }).start ();
    }

    /**
     * Handles the list. It executes the first(if any) element of the list.
     */
    public void handleQueueEvents()
    {
        if (station.reExchange().reSize() != 0)
                station.reExchange().removeFirst().execution();
    }

    /**
     * @return If it is busy or not.
     */
    public boolean reBusy()
    {
        return busy;
    }

    /**
     * Changes the situation of the ExchangeHandler. It works like a switch.
     */
    public void changeSituation()
    {
        busy = !busy;
    }

    /**
     * @return The time that the ExchangeHandler is going to be busy.
     */
    public long reElapsedCommitTime() {
        long diff = station.getTime() - timestamp;
        if (commitTime - diff >= 0)
            return commitTime - diff;
        else
            return 0;
    }

    /**
     * Sets the time the ExchangeHandler is going to be busy.
     * @param time The commit time.
     */
    public void setCommitTime(long time) {
        timestamp = station.getTime();
        commitTime = time;
    }
}