package evlib.station;

import evlib.ev.ElectricVehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingEvent {

    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private long parkingTime;
    private ElectricVehicle vehicle;
    private final ChargingStation station;
    private ParkingSlot parkingSlot;
    private long remainingParkingTime;
    private long chargingTime;
    private long remainingChargingTime;
    private double amountOfEnergy;
    private double energyToBeReceived;
    private long timestamp1;
    private long timestamp2;
    private String condition;
    private double cost;
    private static final List<ParkingEvent> parkLog = new ArrayList<>();

    /**
     * Constructs a new ParkingEvent object. It sets the condition of the event to "arrived".
     * @param stat The ChargingStation object the event visited.
     * @param veh The ElectricVehicle of the event.
     * @param parkTime The time the event wants to park. It is counted in milliseconds.
     */
    public ParkingEvent(final ChargingStation stat, final ElectricVehicle veh, final long parkTime) {
        this.id = idGenerator.incrementAndGet();
        this.station = stat;
        this.vehicle = veh;
        this.condition = "arrived";
        this.parkingTime = parkTime;
        this.parkLog.add(this);
        this.parkingSlot = null;
    }

    /**
     * Constructs a new ParkingEvent object. It sets the condition of the event to "arrived".
     * This constructor is for the objects that desire to charge inductively, as well. If the energy
     * demands greater time than the parking time, then the vehicle will charge until the end of the parking time,
     * taking the respective amount of energy.
     * @param stat The ChargingStation object the event visited.
     * @param veh The ElectricVehicle of the event.
     * @param parkTime The time the event wants to park. It is counted in milliseconds.
     * @param amountOfEnerg The amount of energy the event wants to take inductively.
     */
    public ParkingEvent(final ChargingStation stat, final ElectricVehicle veh, final long parkTime, final double amountOfEnerg)
    {
        this.id = idGenerator.incrementAndGet();
        this.vehicle = veh;
        this.station = stat;
        this.amountOfEnergy = amountOfEnerg;
        this.condition = "arrived";
        this.parkingTime = parkTime;
        this.parkLog.add(this);
    }

    /**
     * @return The ElectricVehicle of the ParkingEvent.
     */
    public ElectricVehicle getElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Sets the vehicle of the ParkingEvent.
     * @param veh The vehicle to be set.
     */
    public void setElectricVehicle(final ElectricVehicle veh) { this.vehicle = veh; }

    /**
     * Executes the pre-processing phase. Checks for any ParkingSLot and assignes to it if any.
     * It calculates the energy to be given to the ElectricVehicle and calculates the charging time.
     * If there is not any empty ParkingSlot the ParkingEvent's condition is set "nonExecutable".
     **/
    public void preProcessing() {
        if (station.getParkingSlots().length == 0) {
            setCondition("nonExecutable");
            return;
        }
        if (vehicle.getBattery().getActive()) {
            if (condition.equals("arrived")) {
                station.assignParkingSlot(this);
                if (parkingSlot != null) {
                    if (amountOfEnergy < station.getTotalEnergy()) {
                        if (amountOfEnergy <= (vehicle.getBattery().getCapacity() - vehicle.getBattery().getRemAmount()))
                            energyToBeReceived = amountOfEnergy;
                        else
                            energyToBeReceived = vehicle.getBattery().getCapacity() - vehicle.getBattery().getRemAmount();
                    } else {
                        if (station.getTotalEnergy() <= (vehicle.getBattery().getCapacity() - vehicle.getBattery().getRemAmount()))
                            energyToBeReceived = station.getTotalEnergy();
                        else
                            energyToBeReceived = vehicle.getBattery().getCapacity() - vehicle.getBattery().getRemAmount();
                    }
                    chargingTime = (long) ((energyToBeReceived) / station.getInductiveRate());
                    if (chargingTime > parkingTime) {
                        energyToBeReceived = parkingTime * station.getInductiveRate();
                        chargingTime = parkingTime;
                    }
                    setCondition("ready");
                    cost = station.getInductivePrice() * energyToBeReceived;
                    double sdf;
                    sdf = energyToBeReceived;
                    HashMap<String, Double> keys = new HashMap<>(station.getMap());
                    for (HashMap.Entry<String, Double> energy : keys.entrySet()) {
                        if (energyToBeReceived < station.getMap().get(energy.getKey())) {
                            double ert = station.getMap().get(energy.getKey()) - sdf;
                            station.setSpecificAmount(energy.getKey(), ert);
                            break;
                        } else {
                            sdf -= energy.getValue();
                            station.setSpecificAmount(energy.getKey(), 0);
                        }
                    }
                } else
                    setCondition("nonExecutable");
            }
        }
        else
            setCondition("nonExecutable");
    }

    /**
     * The condition of the ParkingEvent is set to "charging" or "parking". Then, it executes the parking/charging of the ElectricVehicle.
     */
    public void execution()
    {
        if (condition.equals("ready"))
            if (chargingTime != 0) {
                setCondition("charging");
                vehicle.getBattery().addCharging();
                parkingSlot.startParkingSlot();
            }
            else {
                setCondition("parking");
                parkingSlot.startParkingSlot();
            }
    }

    /**
     * @return The ChargingStation the event is going to be executed.
     */
    public ChargingStation getStation()
    {
        return station;
    }

    /**
     * @return The energy to be given to the ElectricVehicle.
     */
    public double getEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the energy to be received by the ParkingEvent.It also calculates the charging time.
     * @param energy The energy to be received.
     */
    public void setEnergyToBeReceived(final double energy) { this.energyToBeReceived = energy; }

    /**
     * @return The remaining charging time of the ParkingEvent in milliseconds.
     */
    public long getRemainingChargingTime() {
        long diff = System.currentTimeMillis() - timestamp1;
        if ((chargingTime - diff >= 0) && (condition.equals("charging")))
            this.remainingChargingTime = chargingTime - diff;
        else
            return 0;
        return remainingChargingTime;
    }

    /**
     * @return The charging time in milliseconds.
     */
    public long getChargingTime()
    {
        return chargingTime;
    }

    /**
     * @return The parking time in milliseconds.
     */
    public long getParkingTime()
    {
        return parkingTime;
    }

    /**
     * Sets the charging time of the ParkingEvent in milliseconds.
     * @param time The charging time in milliseconds.
     */
    public void setChargingTime(final long time) {
        timestamp1 = System.currentTimeMillis();
        this.chargingTime = time;
    }

    /**
     * Sets the condition of ParkingEvent.
     * @param cond Value of the condition.
     */
    public void setCondition(final String cond)
    {
        this.condition = cond;
    }

    /**
     * @return The condition of ParkingEvent.
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * @return The remaining time the vehicle will be parked measured in milliseconds.
     */
    public long getRemainingParkingTime() {
        long diff = System.currentTimeMillis() - timestamp2;
        if ((parkingTime - diff >= 0) && (condition.equals("parking")))
            remainingParkingTime = parkingTime - diff;
        else
            return 0;
        return remainingParkingTime;
    }

    /**
     * Sets the parking time for the ElectricVehicle in milliseconds.
     * @param parkTime The parking time in milliseconds.
     */
    public void setParkingTime(final long parkTime) {
        timestamp2 = System.currentTimeMillis();
        this.parkingTime = parkTime;
    }

    /**
     * @return The id of this ParkingEvent.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The energy the ElectricVehicle asked.
     */
    public double getAmountOfEnergy()
    {
       return amountOfEnergy;
    }

    /**
     * Sets the amount of energy the ParkingEvent demands.
     * @param energy The energy to be set.
     */
    public void setAmountOfEnergy(final double energy){
        this.amountOfEnergy = energy;
    }

    /**
     * Sets the cost for the ParkingEvent.
     * @param cos The cost to be set.
     */
    public void setCost(final double cos) { this.cost = cos; }

    /**
     * @return The cost of this ParkingEvent.
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Sets the id for the ParkingEvent.
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }

    /**
     * Returns the list with created parking events.
     * @return The list with created parking events.
     */
    public static List<ParkingEvent> getParkLog() {
        return parkLog;
    }

    /**
     * Sets a parking slot for the event.
     * @param slot The parkign slot to be assigned.
     */
    void setParkingSlot(ParkingSlot slot) {
        this.parkingSlot = slot;
    }
}
