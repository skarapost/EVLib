package EVLib.Station;

import EVLib.Events.ChargingEvent;

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
        this.e = null;
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
     * Executes the ChargingEvent. It lasts as much as ChargingEvent's charging time demands.
     * The energy that the ChargingEvent needs is subtracted from the total energy of the ChargingStation.
     * The condition of charging event gets finished. At the end if the automatic queue's handling is activated,
     * the Charger checks the waiting list.
     */
    public void executeChargingEvent() {
        running = true;
        Thread ch = new Thread(() ->
        {
            e.setChargingTime(e.getChargingTime());
            long timestamp1 = System.currentTimeMillis();
            long timestamp2;
            do {
                timestamp2 = System.currentTimeMillis();
            }while(running&&(timestamp2-timestamp1<e.getChargingTime()));
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
     * Sets a ChargingEvent in the Charger.
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    public void setChargingEvent(ChargingEvent ev) {
        this.e = ev;
    }

    /**
     * Handles the list. It executes (if any) the first element of the list.
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
     * @return The ChargingEvent which is linked with the Charger.
     */
    public ChargingEvent getChargingEvent() {
        return e;
    }

    /**
     * @return The id of Charger.
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