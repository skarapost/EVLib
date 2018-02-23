package evlib.station;

import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeHandler
{
    private int id;
    private ChargingStation station;
    private ChargingEvent e;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private String name;

    /**
     * Creates a new ExchangeHandler object.
     * @param stat The ChargingStation object the ExchangeHandler is linked with.
     */
    public ExchangeHandler(final ChargingStation stat) {
        this.id = idGenerator.incrementAndGet();
        this.station = stat;
        this.name = "ExchangeHandler" + String.valueOf(id);
        this.e = null;
    }

    /**
     * Sets a name for the ExchangeHandler.
     * @param nam The name to be set.
     */
    public void setName(final String nam)
    {
        this.name = nam;
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
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }

    /**
     * Links a ChargingEvent with the ExchangeHandler.
     * @param event The ChargingEvent to be linked.
     */
    synchronized void setChargingEvent(final ChargingEvent event)
    {
        this.e = event;
    }

    /**
     * @return The ChargingEvent of the ExchangeHandler.
     */
    public synchronized ChargingEvent getChargingEvent()
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
        Thread running = new Thread(() -> {
            try {
                e.setChargingTime(station.getTimeOfExchange());
                Thread.sleep(e.getChargingTime());
                station.joinBattery(e.getElectricVehicle().getBattery());
                e.getElectricVehicle().setBattery(e.getGivenBattery());
                if (e.getElectricVehicle().getDriver() != null)
                    e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + station.calculatePrice(e));
                if (e.getElectricVehicle().getDriver() == null && e.getElectricVehicle().getBrand() == null)
                    System.out.println("Battery exchange " + e.getId() + ", " + e.getStation().getName() + ", OK");
                else
                    System.out.println("Battery exchange " + e.getId() + ", " + e.getElectricVehicle().getDriver().getName() + ", " + e.getElectricVehicle().getBrand() + ", " + e.getStation().getName() + ", OK");
                e.setCondition("finished");
                synchronized (this) {
                    setChargingEvent(null);
                }
                if (station.getQueueHandling())
                    handleQueueEvents();
            } catch (InterruptedException e1) {
                synchronized (this) {
                    setChargingEvent(null);
                }
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
