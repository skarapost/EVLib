package EVLib.Station;

import java.util.concurrent.atomic.AtomicInteger;

public class Charger {
    private int id;
    private final String kindOfCharging;
    private String name;
    private ChargingEvent e;
    private final ChargingStation station;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile boolean running = true;

    public Charger(ChargingStation station, String kindOfCharging) {
        this.id = idGenerator.incrementAndGet();
        this.kindOfCharging = kindOfCharging;
        this.station = station;
        this.name = "Charger " + String.valueOf(id);
    }

    /**
     * Sets a name for the Charger.
     * @param name The name to be set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return The name of the Charger.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Executes the ChargingEvent. It lasts as much as ChargingEvent's charging time. When the charging time passes,
     * the condition of the ChargingEvent becomes "finished". The event is recorded in the history array.
     * The cost of the ChargingEvent is assigned to the Driver. The amount of energy to be given is added to the
     * battery's remaining amount. In the end, if the automatic queue's handling is activated the Charger checks
     * the waiting list.
     */
    void executeChargingEvent() {
        running = true;
        Thread ch = new Thread(() ->
        {
            e.setChargingTime(e.getChargingTime());
            long timestamp1 = System.currentTimeMillis();
            long timestamp2;
            do {
                timestamp2 = System.currentTimeMillis();
            } while (running && (timestamp2 - timestamp1 < e.getChargingTime()));
            e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
            if (e.getElectricVehicle().getDriver() != null)
                e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + e.getCost());
            System.out.println("The charging " + e.getId() + " completed succesfully");
            e.setCondition("finished");
            ChargingEvent.chargingLog.add(e);
            setChargingEvent(null);
            if (station.getQueueHandling())
                handleQueueEvents();
        });
        if(station.getDeamon())
            ch.setDaemon(true);
        ch.setName("Charger: " + String.valueOf(id));
        ch.start();
    }

    /**
     * @return The kind of charging the Charger supports.
     */
    public String getKindOfCharging() {
        return kindOfCharging;
    }

    /**
     * Handles the waiting list. It executes (if any) the first element of the list.
     */
    private void handleQueueEvents() {
        ChargingEvent e;
        if ("fast".equals(getKindOfCharging())) {
            if (station.getFast().getSize() != 0) {
                e = (ChargingEvent) station.getFast().moveFirst();
                e.preProcessing();
                e.execution();
            }
        } else if ("slow".equals(getKindOfCharging())) {
            if (station.getSlow().getSize() != 0) {
                e = (ChargingEvent) station.getSlow().moveFirst();
                e.preProcessing();
                e.execution();
            }
        }
    }

    /**
     * @return The ChargingEvent that is linked with the Charger.
     */
    public ChargingEvent getChargingEvent() {
        return e;
    }

    /**
     * Sets a ChargingEvent to the Charger.
     *
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    void setChargingEvent(ChargingEvent ev) {
        this.e = ev;
    }

    /**
     * @return The id of the Charger.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for this Charger.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Stops the operation of the Charger.
     */
    public void stopCharger()
    {
        running = false;
    }
}