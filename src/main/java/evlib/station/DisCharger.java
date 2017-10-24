package evlib.station;

import java.util.concurrent.atomic.AtomicInteger;

public class DisCharger
{
    private final ChargingStation station;
    private int id;
    private DisChargingEvent e;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile boolean running = true;
    private String name;

    public DisCharger(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.e = null;
        this.name = "DisCharger " + String.valueOf(id);
    }

    /**
     * Executes the DisChargingEvent. It lasts as much as the assigned DisChargingEvent's
     * discharging time is set. Next, the condition of the DisChargingEvent gets "finished".
     * In the end if the queue's handling is automatic, the DisCharger calls the method for the
     * management of the waiting list.
     */
    void executeDisChargingEvent()
    {
        running = true;
        Thread dsch = new Thread(() -> {
            e.setDisChargingTime(e.getDisChargingTime());
            long timestamp1 = System.currentTimeMillis();
            long timestamp2;
            do {
                timestamp2 = System.currentTimeMillis();
            } while (running && (timestamp2 - timestamp1 < e.getDisChargingTime()));
            e.getElectricVehicle().getBattery().setRemAmount(e.getElectricVehicle().getBattery().getRemAmount() - e.getAmountOfEnergy());
            if (e.getElectricVehicle().getDriver() != null)
                e.getElectricVehicle().getDriver().setProfit(e.getElectricVehicle().getDriver().getProfit() + e.getProfit());
            double energy = station.getMap().get("DisCharging") + e.getAmountOfEnergy();
            station.setSpecificAmount("DisCharging", energy);
            System.out.println("Discharging " + e.getId() + ", " + e.getChargingStationName() + ", OK");
            e.setCondition("finished");
            DisChargingEvent.dischargingLog.add(e);
            setDisChargingEvent(null);
            if (station.getQueueHandling())
                handleQueueEvents();
        });
        if(station.getDeamon())
            dsch.setDaemon(true);
        dsch.setName("DisCharger: " + String.valueOf(id));
        dsch.start();
    }

    /**
     * @return The DisChargingEvent linked with the DisCharger.
     */
    public DisChargingEvent getDisChargingEvent()
    {
        return e;
    }

    /**
     * Sets a DisChargingEvent to the DisCharger.
     *
     * @param event The DisChargingEvent to be linked with the DisCharger.
     */
    void setDisChargingEvent(DisChargingEvent event) {
        this.e = event;
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
     * executes the preProcessing() function and then calls the execution function.
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
     * Sets the id for the DisCharger.
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

    /**
     * Sets a name for the DisCharger.
     * @param name The name to be set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return The name of the DisCharger.
     */
    public String getName()
    {
        return name;
    }
}