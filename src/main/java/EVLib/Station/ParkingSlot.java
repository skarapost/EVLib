package EVLib.Station;

import EVLib.Events.ParkingEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSlot {
    private int id;
    private ParkingEvent e;
    private final ChargingStation station;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private boolean inSwitch;
    private volatile boolean running = true;

    public ParkingSlot(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        inSwitch = true;
    }

    /**
     * Executes the inductive charging phase of a parking slot. It works like the ChargingEvent.
     */
    public void parkingVehicle() {
        Thread ch = new Thread(() ->
        {
            if(e.getCondition().equals("charging")) {
                long timestamp1 = System.currentTimeMillis();
                long timestamp2;
                do {
                    timestamp2 = System.currentTimeMillis();
                }while(running&&(timestamp2-timestamp1<e.getChargingTime()));
                synchronized (this) {
                    e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
                    if (e.getElectricVehicle().getDriver() != null)
                        e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + station.calculatePrice(e));
                    System.out.println("The inductive charging " + e.getId() + " completed successfully");
                }
            }
            e.setCondition("parking");
            long diff = e.getParkingTime() - e.getChargingTime();
            long timestamp1 = System.currentTimeMillis();
            long timestamp2;
            do {
                timestamp2 = System.currentTimeMillis();
            }while(running&&(timestamp2-timestamp1<diff));
            synchronized (this) {
                System.out.println("The parking " + e.getId() + " completed successfully");
                e.setCondition("finished");
                ParkingEvent.parkLog.add(e);
                setParkingEvent(null);
            }
        });
        if(station.getDeamon())
            ch.setDaemon(true);
        ch.setName("ParkingSlot: " + String.valueOf(id));
        ch.start();
    }

    /**
     * Enables or disables the inductive charging of a parking slot.
     */
    public void setInSwitch(boolean inSwitch)
    {
        this.inSwitch = inSwitch;
    }

    /**
     *
     * @return If the inductive charging is enabled or not, for this parking slot.
     */
    public boolean getInSwitch()
    {
        return inSwitch;
    }

    /**
     * Sets a ParkingEvent in the Charger.
     * @param ev The ParkingEvent to be linked with the ParkingSlot.
     */
    public void setParkingEvent(ParkingEvent ev) { this.e = ev; }

    /**
     * @return The ParkingEvent which is linked with the ParkingSlot.
     */
    public ParkingEvent getParkingEvent() {
        return e;
    }

    /**
     * @return The id of the ParkingSlot object.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for this ParkingSlot.
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
