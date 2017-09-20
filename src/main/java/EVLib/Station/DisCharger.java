package EVLib.Station;

import EVLib.Events.DisChargingEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class DisCharger
{
    private final ChargingStation station;
    private int id;
    private DisChargingEvent e;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile boolean running = true;

    public DisCharger(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
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
        Thread dsch = new Thread(() -> {
            long timestamp1 = System.currentTimeMillis();
            long timestamp2;
            do {
                timestamp2 = System.currentTimeMillis();
            }while(running&&(timestamp2-timestamp1<e.getDisChargingTime()));
            synchronized(this) {
                e.getElectricVehicle().getBattery().setRemAmount(e.getElectricVehicle().getBattery().getRemAmount() - e.getAmountOfEnergy());
                e.getElectricVehicle().getDriver().setProfit(e.getElectricVehicle().getDriver().getProfit() + e.getProfit());
                double energy = station.getMap().get("discharging") + e.getAmountOfEnergy();
                station.setSpecificAmount("discharging", energy);
                System.out.println("The discharging " + e.getId() + " completed succesfully");
                e.setCondition("finished");
                DisChargingEvent.dischargingLog.add(e);
                setDisChargingEvent(null);
            }
            if (station.getQueueHandling())
                handleQueueEvents();
        });
        if(station.getDeamon())
            dsch.setDaemon(true);
        dsch.setName("DisCharger: " + String.valueOf(id));
        dsch.start();
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
     * @return The DisChargingEvent od the DisCharger.
     */
    public DisChargingEvent getDisChargingEvent()
    {
        return e;
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
    private void handleQueueEvents()
    {
        if (station.getDischarging().getSize() != 0) {
            DisChargingEvent e = (DisChargingEvent) station.getDischarging().moveFirst();
            e.preProcessing();
            e.execution();
        }
    }

    /**
     * Sets the id for this DisCharger.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Stops the operation of the DisCharger.
     */
    public void stopDisCharger()
    {
        running = false;
    }
}