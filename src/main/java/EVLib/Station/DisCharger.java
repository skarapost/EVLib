package EVLib.Station;

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
        this.name = "DisCharger" + String.valueOf(id);
    }

    /**
     * Executes the DisChargingEvent. It lasts as much as DisChargingEvent's
     * discharging time demands. The energy that the discharging event needs is
     * subtracted from the total energy of the charging station. The condition of
     * DisChargingEvent gets "finished". In the end if the automatic queue's handling
     * is activated, the DisCharger checks the waiting list.
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
            e.getElectricVehicle().getDriver().setProfit(e.getElectricVehicle().getDriver().getProfit() + e.getProfit());
            double energy = station.getMap().get("DisCharging") + e.getAmountOfEnergy();
            station.setSpecificAmount("DisCharging", energy);
            System.out.println("The discharging " + e.getId() + " completed succesfully");
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
     * Sets a DisChargingEvent to the DisCharger.
     * @param e The DisChargingEvent that is going to be linked with the DisCharger.
     */
    void setDisChargingEvent(DisChargingEvent e)
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

    /**
     * Sets a name for this DisCharger.
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