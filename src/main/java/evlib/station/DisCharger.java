package evlib.station;

import java.util.concurrent.atomic.AtomicInteger;

public class DisCharger
{
    private final ChargingStation station;
    private int id;
    private DisChargingEvent e;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile Thread running;
    private String name;

    /**
     * Creates a new DisCharger instance.
     * @param station The ChargingStation instance linked with.
     */
    public DisCharger(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.name = "Discharger" + String.valueOf(id);
    }

    /**
     * Executes the DisChargingEvent. It lasts as much as the assigned DisChargingEvent's
     * discharging time is set. Next, the condition of the DisChargingEvent gets "finished".
     * In the end if the queue's handling is automatic, the DisCharger calls the method for the
     * management of the waiting list.
     */
    public void startDisCharger() {
        running = new Thread(() -> {
            try {
                e.setDisChargingTime(e.getDisChargingTime());
                Thread.sleep(e.getDisChargingTime());
                e.getElectricVehicle().getBattery().setRemAmount(e.getElectricVehicle().getBattery().getRemAmount() - e.getAmountOfEnergy());
                if (e.getElectricVehicle().getDriver() != null)
                    e.getElectricVehicle().getDriver().setProfit(e.getElectricVehicle().getDriver().getProfit() + e.getProfit());
                double energy = station.getMap().get("Discharging") + e.getAmountOfEnergy();
                station.setSpecificAmount("Discharging", energy);
                if (e.getElectricVehicle().getDriver() == null && e.getElectricVehicle().getBrand() == null)
                    System.out.println("Discharging " + e.getId() + ", " + e.getStation().getName() + ", OK");
                else
                    System.out.println("Discharging " + e.getId() + ", " + e.getElectricVehicle().getDriver().getName() + ", " + e.getElectricVehicle().getBrand() + ", " + e.getStation().getName() + ", OK");
                e.setCondition("finished");
                setDisChargingEvent(null);
                if (station.getQueueHandling())
                    handleQueueEvents();
            } catch(InterruptedException e1) {
                System.out.println(name + " stopped");
                setDisChargingEvent(null);
            } catch(NullPointerException e2) {
                System.out.println("not processed");
            }
        });
        if(station.getDeamon())
            running.setDaemon(true);
        running.setName("Discharger" + String.valueOf(id));
        running.start();
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
