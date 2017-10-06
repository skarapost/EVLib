package EVLib.Station;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSlot {
    private int id;
    private ParkingEvent e;
    private final ChargingStation station;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private boolean inSwitch;
    private volatile boolean running = true;
    private String name;

    public ParkingSlot(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        inSwitch = true;
        this.name = "ParkingSlot " + String.valueOf(id);
    }

    /**
     * Sets a name for the ParkingSlot.
     * @param name The name to be set.
     */
    public void setName(String name)
    {
        this.name = name;
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
    void parkingVehicle() {
        running = true;
        Thread ch = new Thread(new Runnable()
        {
            @Override
            public void run() {
                e.setParkingTime(e.getParkingTime());
                if (e.getCondition().equals("charging")) {
                    e.setChargingTime(e.getChargingTime());
                    long timestamp1 = System.currentTimeMillis();
                    long timestamp2;
                    do {
                        timestamp2 = System.currentTimeMillis();
                    } while (running && (timestamp2 - timestamp1 < e.getChargingTime()));
                    synchronized (this) {
                        e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
                        if (e.getElectricVehicle().getDriver() != null)
                            e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + e.getEnergyToBeReceived() * station.getInductivePrice());
                        System.out.println("The inductive charging " + e.getId() + " completed successfully");
                    }
                }
                e.setCondition("parking");
                long diff = e.getParkingTime() - e.getChargingTime();
                long timestamp1 = System.currentTimeMillis();
                long timestamp2;
                do {
                    timestamp2 = System.currentTimeMillis();
                } while (running && (timestamp2 - timestamp1 < diff));
                synchronized (this) {
                    System.out.println("The parking " + e.getId() + " completed successfully");
                    e.setCondition("finished");
                    ParkingEvent.parkLog.add(e);
                    setParkingEvent(null);
                }
            }
        });
        if(station.getDeamon())
            ch.setDaemon(true);
        ch.setName("ParkingSlot: " + String.valueOf(id));
        ch.start();
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
     * @param inSwitch The value to be set. True, means the ParkingSlot supports the inductive charging, false
     *                 means not.
     */
    public void setInSwitch(boolean inSwitch) {
        this.inSwitch = inSwitch;
    }

    /**
     * @return The ParkingEvent which is linked with the ParkingSlot.
     */
    public ParkingEvent getParkingEvent() {
        return e;
    }

    /**
     * Sets a ParkingEvent to the ParkingSlot.
     * @param event The ParkingEvent to be linked with the ParkingSlot.
     */
    void setParkingEvent(ParkingEvent event) {
        this.e = event;
    }

    /**
     * @return The id of the ParkingSlot.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for the ParkingSlot.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Stops the operation of the ParkingSlot.
     */
    public void stopParkingSlot()
    {
        running = false;
    }
}
