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
    private final String chargingStationName;
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
    public static final List<ParkingEvent> parkLog = new ArrayList<>();

    /**
     * Constructs a new ParkingEvent object. It sets the condition of the event to "arrived".
     * @param station The ChargingStation object the event visited.
     * @param vehicle The ElectricVehicle of the event.
     * @param parkingTime The time the event wants to park. It is counted in milliseconds.
     */
    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.vehicle = vehicle;
        this.condition = "arrived";
        this.parkingTime = parkingTime;
        this.chargingStationName = station.getName();
    }

    /**
     * Constructs a new ParkingEvent object. It sets the condition of the event to "arrived".
     * This constructor is for the objects that desire to charge inductively, as well. If the energy
     * demands greater time than the parking time, then the vehicle will charge until the end of the parking time,
     * taking the respective amount of energy.
     * @param station The ChargingStation object the event visited.
     * @param vehicle The ElectricVehicle of the event.
     * @param parkingTime The time the event wants to park. It is counted in milliseconds.
     * @param amountOfEnergy The amount of energy the event wants to take inductively.
     */
    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime, double amountOfEnergy)
    {
        this.id = idGenerator.incrementAndGet();
        this.vehicle = vehicle;
        this.station = station;
        this.amountOfEnergy = amountOfEnergy;
        this.condition = "arrived";
        this.parkingTime = parkingTime;
        this.chargingStationName = station.getName();
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
     * @param vehicle The vehicle to be set.
     */
    public void setElectricVehicle(ElectricVehicle vehicle) { this.vehicle = vehicle; }

    /**
     * Executes the pre-processing phase. Checks for any ParkingSLot and assignes to it if any.
     * It calculates the energy to be given to the ElectricVehicle and calculates the charging time.
     * If there is not any empty ParkingSlot the ParkingEvent's condition is set "nonExecutable".
     **/
    public synchronized void preProcessing()
    {
        if (vehicle.getBattery().getActive()) {
            if (condition.equals("arrived")) {
                parkingSlot = station.assignParkingSlot(this);
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
                    chargingTime = (long) ((energyToBeReceived) / station.getInductiveRatio());
                    if (chargingTime > parkingTime) {
                        energyToBeReceived = parkingTime * station.getInductiveRatio();
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
    public synchronized void execution()
    {
        if (condition.equals("ready"))
            if(chargingTime != 0 ) {
                setCondition("charging");
                vehicle.getBattery().addCharging();
                try {
                    parkingSlot.parkingVehicle();
                } catch (NullPointerException ex) {
                    System.out.println("No processed");
                }
            }
            else {
                setCondition("parking");
                parkingSlot.parkingVehicle();
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
     * @return The name of the ChargingStation.
     */
    public String getChargingStationName() {
        return chargingStationName;
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
     * @param energyToBeReceived The energy to be received.
     */
    public void setEnergyToBeReceived(double energyToBeReceived) { this.energyToBeReceived = energyToBeReceived; }

    /**
     * @return The remaining charging time of the ParkingEvent.
     */
    public long getRemainingChargingTime()
    {
        long diff = System.currentTimeMillis() - timestamp1;
        if ((chargingTime - diff >= 0) && (condition.equals("charging")))
            this.remainingChargingTime = chargingTime - diff;
        else
            return 0;
        return remainingChargingTime;
    }

    /**
     * @return The charging time.
     */
    public long getChargingTime()
    {
        return chargingTime;
    }

    /**
     * @return The parking time.
     */
    public long getParkingTime()
    {
        return parkingTime;
    }

    /**
     * Sets the charging time of the ParkingEvent.
     * @param time The charging time.
     */
    public void setChargingTime(long time)
    {
        timestamp1 = System.currentTimeMillis();
        this.chargingTime = time;
    }

    /**
     * Sets the condition of ParkingEvent.
     * @param condition Value of the condition.
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    /**
     * @return The condition of ParkingEvent.
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * @return The remaining time the vehicle will be parked.
     */
    public long getRemainingParkingTime()
    {

        long diff = System.currentTimeMillis() - timestamp2;
        if ((parkingTime - diff >= 0) && (condition.equals("parking")))
            remainingParkingTime = parkingTime - diff;
        else
            return 0;
        return remainingParkingTime;
    }

    /**
     * Sets the parking time for the ElectricVehicle.
     * @param parkingTime The parking time.
     */
    public void setParkingTime(long parkingTime)
    {
        timestamp2 = System.currentTimeMillis();
        this.parkingTime = parkingTime;
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
    public void setAmountOfEnergy(double energy){
        this.amountOfEnergy = energy;
    }

    /**
     * Sets the cost for the ParkingEvent.
     * @param cost The cost to be set.
     */
    public void setCost(double cost) { this.cost = cost; }

    /**
     * @return The cost of this ParkingEvent.
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Sets the id for the ParkingEvent.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
