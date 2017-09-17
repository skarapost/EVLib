package EVLib.Station;

import EVLib.EV.Battery;
import EVLib.Events.ChargingEvent;

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
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        e = null;
    }

    /**
     * @return The id of the ExchangeHandler.
     */
    public int getId()
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
    public ChargingEvent getChargingEvent()
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
     *
     * @param bat The battery to be given to the station.
     */
    public void executeExchange(Battery bat)
    {
        Thread ch = new Thread (() -> {
            try {
                Thread.sleep(e.getChargingTime());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            station.joinBattery(bat);
            synchronized(this) {
                e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + station.calculatePrice(e));
                System.out.println("The exchange " + e.getId() + " completed successfully");
                e.setCondition("finished");
                changeSituation();
                joinChargingEvent(null);
                commitTime = 0;
            }
            if (station.getQueueHandling())
                handleQueueEvents();
        });
        if(station.getDeamon())
            ch.setDaemon(true);
        ch.start();
    }

    /**
     * Handles the list. It executes the first(if any) element of the list.
     */
    public void handleQueueEvents() {
        if (station.getExchange().getSize() != 0) {
            ChargingEvent e = (ChargingEvent) station.getExchange().moveFirst();
            e.execution();
        }
    }

    /**
     * @return If it is busy or not.
     */
    public boolean getBusy()
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
    public long getElapsedCommitTime() {
        long diff = System.currentTimeMillis() - timestamp;
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
        timestamp = System.currentTimeMillis();
        commitTime = time;
    }
}