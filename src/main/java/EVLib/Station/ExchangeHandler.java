package EVLib.Station;

import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeHandler
{
    private int id;
    private final ChargingStation station;
    private ChargingEvent e;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile boolean running = true;
    private String name;

    public ExchangeHandler(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.name = "ExchangeHandler " + String.valueOf(id);
    }

    /**
     * Sets a name for the ExchangeHandler.
     * @param name The name to be set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return The name of the ExchangeHandler.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return The id of the ExchangeHandler.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for this ExchangeHandler.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Links a ChargingEvent with the ExchangeHandler.
     * @param e The ChargingEvent to be linked.
     */
    void setChargingEvent(ChargingEvent e)
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
     * It executes the swapping battery phase.
     * In the end, if the automatic queue handling is enabled checks the waiting list.
     */
    void executeExchange()
    {
        running = true;
        Thread ch = new Thread (() -> {
            e.setChargingTime(station.getTimeOfExchange());
            long timestamp1 = System.currentTimeMillis();
            long timestamp2;
            do {
                timestamp2 = System.currentTimeMillis();
            } while (running && (timestamp2 - timestamp1 < e.getChargingTime()));
            station.joinBattery(e.getElectricVehicle().getBattery());
            e.getElectricVehicle().setBattery(e.givenBattery);
            e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + station.calculatePrice(e));
            System.out.println("The exchange " + e.getId() + " completed successfully");
            e.setCondition("finished");
            ChargingEvent.exchangeLog.add(e);
            setChargingEvent(null);
            if (station.getQueueHandling())
                handleQueueEvents();
        });
        if(station.getDeamon())
            ch.setDaemon(true);
        ch.setName("ExchangeHandler: " + String.valueOf(id));
        ch.start();
    }

    /**
     * Handles the list. It executes the first(if any) element of the list.
     */
    private void handleQueueEvents() {
        if (station.getExchange().getSize() != 0) {
            ChargingEvent e = (ChargingEvent) station.getExchange().moveFirst();
            e.preProcessing();
            e.execution();
        }
    }

    /**
     * Stops the operation of the ExchangeHandler.
     */
    public void stopExchangeHandler()
    {
        running = false;
    }
}