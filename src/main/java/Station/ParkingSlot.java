package Station;

import Events.ParkingEvent;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSlot {
    private int id;
    private boolean busy;
    private long commitTime;
    private long chargingTime;
    private ParkingEvent e;
    private ChargingStation station;
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private boolean inSwitch;
    private long timestamp;

    public ParkingSlot(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.busy = false;
        inSwitch = true;
    }

    /**
     * Executes the inductive charging phase of a parking slot. It works like the ChargingEvent.
     */
    public void parkingVehicle() {
        if (inSwitch&&(e.getChargingTime()!=0))
        {
            new Thread(() ->
            {
                double sdf;
                long en;
                StopWatch d2 = new StopWatch();
                d2.start();
                do {
                    en = d2.getTime();
                } while (en < e.getChargingTime());
                sdf = e.getEnergyToBeReceived();
                HashMap<String, Double> keys = new HashMap<>(station.getMap());
                for (HashMap.Entry<String, Double> energy : keys.entrySet()) {
                    if (e.getEnergyToBeReceived() < station.getMap().get(energy.getKey())) {
                        double ert = station.getMap().get(energy.getKey()) - sdf;
                        e.getStation().setSpecificAmount(energy.getKey(), ert);
                        break;
                    } else {
                        sdf -= energy.getValue();
                        e.getStation().setSpecificAmount(energy.getKey(), 0);
                    }
                }
                e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
                if (e.getElectricVehicle().getDriver() != null)
                    e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + station.calculatePrice(e));
                System.out.println("The inductive charging " + e.getId() + " completed successfully");
                e.setCondition("parking");
                long diff = e.getParkingTime() - e.getChargingTime();
                d2.reset();
                d2.start();
                do {
                    en = d2.getTime();
                }while(en < diff);
                System.out.println("The parking " + e.getId() + " completed successfully");
                e.setCondition("finished");
                changeSituation();
                setParkingEvent(null);
                commitTime = 0;
                chargingTime = 0;
            }).start();
        }
    }

    /**
     * Enables or disables the inductive charging of a parking slot.
     */
    public void changeInSwitch()
    {
        this.inSwitch = !inSwitch;
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
     * Changes the situation of the Charger.
     */
    public void changeSituation() {
        this.busy = !busy;
    }

    /**
     * @return True if it is busy, false if it is not busy.
     */
    public boolean getBusy() {
        return busy;
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
     * Sets the time the vehicle will use the parking slot for charging.
     * @param chargingTime The time the vehicle will use the parking slot for charging.
     */
    public void setChargingTime(long chargingTime)
    {
        this.chargingTime = chargingTime;
    }

    /**
     * @return The chargingTime the vehicle will charge.
     */
    public long getElapsedChargingTime()
    {
        return chargingTime;
    }

    /**
     * Sets the time the vehicle will park.
     * @param parkingTime The time the vehicle will be parked.
     */
    public void setCommitTime(long parkingTime)
    {
        timestamp = station.getTime();
        this.commitTime = parkingTime;
    }

    /**
     * @return The time the vehicle will be parked.
     */
    public long getElapsedCommitTime()
    {
        long diff = station.getTime() - timestamp;
        if (commitTime - diff >= 0)
            return commitTime - diff;
        else
            return 0;
    }
}
