package evlib.station;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSlot {
    private int id;
    private ParkingEvent e;
    private ChargingStation station;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private boolean inSwitch;
    private String name;

    /**
     * Creates a new ParkingSlot object. It also activates the switch for inductive charging.
     * @param stat The ChargingStation object the ParkingSlot is linked with.
     */
    public ParkingSlot(final ChargingStation stat)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = stat;
        inSwitch = true;
        this.name = "ParkingSlot" + String.valueOf(id);
        this.e = null;
    }

    /**
     * Sets a name for the ParkingSlot.
     * @param nam The name to be set.
     */
    public void setName(final String nam)
    {
        this.name = nam;
    }

    /**
     * @return The name of the ParkingSlot.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Its primary job is the excution fo the ParkingEvent. At first, checks if the event will be charged.
     * The charging lasts as the charging time. Then, the charging time is subtracted from the parking time.
     * The remaining time the vehicle is considered to be parking. In the end of the parking, the condition
     * is set "finished", the event is recorded in the history. The last thing to do is the method for the
     * management of the waiting list.
     */
    public void startParkingSlot() {
        Thread running = new Thread(() -> {
            try {
                e.setParkingTime(e.getParkingTime());
                if (e.getCondition().equals("charging")) {
                    e.setChargingTime(e.getChargingTime());
                    Thread.sleep(e.getChargingTime());
                    e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
                    if (e.getElectricVehicle().getDriver() != null)
                        e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + e.getEnergyToBeReceived() * station.getInductivePrice());
                    if (e.getElectricVehicle().getDriver() == null && e.getElectricVehicle().getBrand() == null)
                        System.out.println("Charging " + e.getId() + ", " + e.getStation().getName() + ", OK");
                    else
                        System.out.println("Charging " + e.getId() + ", " + e.getElectricVehicle().getDriver().getName() + ", " + e.getElectricVehicle().getBrand() + ", " + e.getStation().getName() + ", OK");
                }
                e.setCondition("parking");
                long diff = e.getParkingTime() - e.getChargingTime();
                Thread.sleep(diff);
                if (e.getElectricVehicle().getDriver() == null && e.getElectricVehicle().getBrand() == null)
                    System.out.println("Parking " + e.getId() + ", " + e.getStation().getName() + ", OK");
                else
                    System.out.println("Parking " + e.getId() + ", " + e.getElectricVehicle().getDriver().getName() + ", " + e.getElectricVehicle().getBrand() + ", " + e.getStation().getName() + ", OK");
                e.setCondition("finished");
                synchronized (this) {
                    setParkingEvent(null);
                }
            } catch (InterruptedException e1) {
                synchronized (this) {
                    setParkingEvent(null);
                }
                System.out.println(name + " stopped");
            } catch (NullPointerException e2) {
                System.out.println("not processed");
            }
        });
        if (station.getDeamon())
            running.setDaemon(true);
        running.setName("ParkingSlot" + String.valueOf(id));
        running.start();
    }

    /**
     *
     * @return If the inductive charging is enabled or not, for this ParkingSlot.
     */
    public boolean getInSwitch()
    {
        return inSwitch;
    }

    /**
     * Sets if the ParkingSlot is able to charge a vehicle inductively.
     *
     * @param inSwit The value to be set. True, means the ParkingSlot supports the inductive charging, false
     *                 means not.
     */
    public void setInSwitch(final boolean inSwit) {
        this.inSwitch = inSwit;
    }

    /**
     * @return The ParkingEvent which is linked with the ParkingSlot.
     */
    public synchronized ParkingEvent getParkingEvent() { return e; }

    /**
     * Sets a ParkingEvent to the ParkingSlot.
     * @param ev The ParkingEvent to be linked with the ParkingSlot.
     */
    synchronized void setParkingEvent(final ParkingEvent ev) {
        this.e = ev;
    }

    /**
     * @return The id of the ParkingSlot.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for the ParkingSlot.
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }
}
