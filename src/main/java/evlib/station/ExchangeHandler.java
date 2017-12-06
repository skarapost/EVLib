package evlib.station;

import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeHandler
{
    private int id;
    private ChargingStation station;
    private ChargingEvent e;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile Thread running;
    private String name;

    /**
     * Creates a new ExchangeHandler object.
     * @param station The ChargingStation object the ExchangeHandler is linked with.
     */
    public ExchangeHandler(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.name = "ExchangeHandler" + String.valueOf(id);
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
     * Sets the id for the ExchangeHandler.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Links a ChargingEvent with the ExchangeHandler.
     * @param event The ChargingEvent to be linked.
     */
    void setChargingEvent(ChargingEvent event)
    {
        this.e = event;
    }

    /**
     * @return The ChargingEvent of the ExchangeHandler.
     */
    public ChargingEvent getChargingEvent()
    {
        return e;
    }

    /**
     * Executes the ChargingEvent. It lasts as much as the predefined battery exchange duration. When this time passes,
     * the condition of the ChargingEvent becomes "finished". The event is recorded in the history array.
     * The cost of the ChargingEvent is assigned to the Driver. The battery to be given is added to the ElectricVehicle.
     * In the end, if the automatic queue's handling is activated the ExchangeHandler checks the WaitingList.
     */
    public void startExchangeHandler() {
        running = new Thread(() -> {
            try {
                e.setChargingTime(station.getTimeOfExchange());
                Thread.sleep(e.getChargingTime());
                station.joinBattery(e.getElectricVehicle().getBattery());
                e.getElectricVehicle().setBattery(e.givenBattery);
                if (e.getElectricVehicle().getDriver() != null)
                    e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + station.calculatePrice(e));
                if (e.getElectricVehicle().getDriver() == null && e.getElectricVehicle().getBrand() == null)
                    System.out.println("Battery exchange " + e.getId() + ", " + e.getChargingStationName() + ", OK");
                else
                    System.out.println("Battery exchange " + e.getId() + ", " + e.getElectricVehicle().getDriver().getName() + ", " + e.getElectricVehicle().getBrand() + ", " + e.getChargingStationName() + ", OK");
                e.setCondition("finished");
                setChargingEvent(null);
                if (station.getQueueHandling())
                    handleQueueEvents();
            } catch (InterruptedException e1) {
                setChargingEvent(null);
                System.out.println(name + " stopped");
            } catch (NullPointerException e2) {
                System.out.println("not processed");
            }
        });
        if (station.getDeamon())
            running.setDaemon(true);
        running.setName("ExchangeHandler" + String.valueOf(id));
        running.start();
    }

    /**
     * Handles the list. It executes the first(if any) ChargingEvent of the WaitingList.
     */
    private void handleQueueEvents() {
        if (station.getExchange().getSize() != 0) {
            ChargingEvent e = (ChargingEvent) station.getExchange().moveFirst();
            e.preProcessing();
            e.execution();
        }
    }
}
