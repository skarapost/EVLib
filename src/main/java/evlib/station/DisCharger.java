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
     * @param stat The ChargingStation instance linked with.
     */
    public DisCharger(final ChargingStation stat)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = stat;
        this.name = "Discharger" + String.valueOf(id);
        this.e = null;
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
                synchronized(this) {
                    setDisChargingEvent(null);
                }
                if (station.getQueueHandling())
                    handleQueueEvents();
            } catch (InterruptedException e1) {
                System.out.println(name + " stopped");
                synchronized(this) {
                    setDisChargingEvent(null);
                }
            } catch (NullPointerException e2) {
                System.out.println("not processed");
            }
        });
        if (station.getDeamon())
            running.setDaemon(true);
        running.setName("Discharger" + String.valueOf(id));
        running.start();
    }

    /**
     * @return The DisChargingEvent linked with the DisCharger.
     */
    public synchronized DisChargingEvent getDisChargingEvent()
    {
        return e;
    }

    /**
     * Sets a DisChargingEvent to the DisCharger.
     * @param ev The DisChargingEvent to be linked with the DisCharger.
     */
    synchronized void setDisChargingEvent(final DisChargingEvent ev) {
        this.e = ev;
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
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }

    /**
     * Sets a name for the DisCharger.
     * @param nam The name to be set.
     */
    public void setName(final String nam)
    {
        this.name = nam;
    }

    /**
     * @return The name of the DisCharger.
     */
    public String getName()
    {
        return name;
    }
}
