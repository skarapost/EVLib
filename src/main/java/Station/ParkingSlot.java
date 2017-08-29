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
        if (inSwitch&&(e.reChargingTime()!=0))
        {
            new Thread(() ->
            {
                double sdf;
                long en;
                StopWatch d2 = new StopWatch();
                d2.start();
                do {
                    en = d2.getTime();
                } while (en < e.reChargingTime());
                sdf = e.reEnergyToBeReceived();
                HashMap<String, Double> keys = new HashMap<>(station.reMap());
                for (HashMap.Entry<String, Double> energy : keys.entrySet()) {
                    if (e.reEnergyToBeReceived() < station.reMap().get(energy.getKey())) {
                        double ert = station.reMap().get(energy.getKey()) - sdf;
                        e.reStation().setSpecificAmount(energy.getKey(), ert);
                        break;
                    } else {
                        sdf -= energy.getValue();
                        e.reStation().setSpecificAmount(energy.getKey(), 0);
                    }
                }
                e.reElectricVehicle().reBattery().setRemAmount(e.reEnergyToBeReceived() + e.reElectricVehicle().reBattery().reRemAmount());
                if (e.reElectricVehicle().reDriver() != null)
                    e.reElectricVehicle().reDriver().setDebt(e.reElectricVehicle().reDriver().reDebt() + station.calculatePrice(e));
                System.out.println("The inductive charging " + e.reId() + " completed successfully");
                e.setCondition("parking");
                long diff = e.reParkingTime() - e.reChargingTime();
                d2.reset();
                d2.start();
                do {
                    en = d2.getTime();
                }while(en < diff);
                System.out.println("The parking " + e.reId() + " completed successfully");
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
    public boolean reInSwitch()
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
    public boolean reBusy() {
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
    public ParkingEvent reParkingEvent() {
        return e;
    }

    /**
     * @return The id of the ParkingSlot object.
     */
    public int reId() {
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
    public long reElapsedChargingTime()
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
    public long reElapsedCommitTime()
    {
        long diff = station.getTime() - timestamp;
        if (commitTime - diff >= 0)
            return commitTime - diff;
        else
            return 0;
    }
}