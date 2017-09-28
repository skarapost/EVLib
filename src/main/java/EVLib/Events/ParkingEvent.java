package EVLib.Events;

import EVLib.EV.ElectricVehicle;
import EVLib.Station.ChargingStation;
import EVLib.Station.ParkingSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingEvent {

    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private long parkingTime;
    private final String chargingStationName;
    private ElectricVehicle vehicle;
    private final ChargingStation station;
    private long remainingParkingTime;
    private long chargingTime;
    private long remainingChargingTime;
    private double amountOfEnergy;
    private double energyToBeReceived;
    private int parkingSlotId;
    private long timestamp1;
    private long timestamp2;
    private String condition;
    private double cost;
    public static final List<ParkingEvent> parkLog = new ArrayList<>();

    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.vehicle = vehicle;
        this.parkingSlotId = -1;
        this.condition = "arrived";
        this.parkingTime = parkingTime;
        this.chargingStationName = station.getName();
    }

    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime, double amountOfEnergy)
    {
        this.id = idGenerator.incrementAndGet();
        this.vehicle = vehicle;
        this.station = station;
        this.amountOfEnergy = amountOfEnergy;
        this.parkingSlotId = -1;
        this.condition = "arrived";
        this.parkingTime = parkingTime;
        this.chargingStationName = station.getName();
    }

    /**
     * @return The ElectricVehicle object.
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
     * Executes the preprocessing phase. Checks for any ParkingSlot,
     * calculates the energy to be given to the ElectricVehicle and calculates the charging time.
     * If there is not any empty ParkingSlot the ChargingEvent is charecterized as "nonExecutable".
     */
    public void preProcessing()
    {
        int qwe = station.checkParkingSlots();
        if ((qwe != -1) && (qwe != -2)) {
            parkingSlotId = qwe;
            ParkingSlot ps = station.searchParkingSlot(parkingSlotId);
            ps.setParkingEvent(this);
            if (ps.getInSwitch()&&(vehicle.getBattery().getActive())) {
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
                    if (energyToBeReceived == 0) {
                        setCondition("ready");
                        return;
                    }
                }
                chargingTime = (long) ((energyToBeReceived) / station.getInductiveRatio());
                if (chargingTime > parkingTime) {
                    energyToBeReceived = parkingTime * station.getInductiveRatio();
                    chargingTime = parkingTime;
                }
                setCondition("ready");
                cost = station.calculatePrice(this);
            }
            else
                setCondition("ready");
        }
        else
            setCondition("nonExecutable");
    }

    /**
     * Executes the parking/charging phase of a vehicle.
     */
    public void execution()
    {
        if (condition.equals("ready"))
            if(chargingTime != 0 ) {
                setCondition("charging");
                station.searchParkingSlot(parkingSlotId).parkingVehicle();
            }
            else {
                setCondition("parking");
                station.searchParkingSlot(parkingSlotId).parkingVehicle();
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
        if ((chargingTime - diff >= 0)&&(!condition.equals("arrived")))
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
     * Sets the id of the ParkingSlot the ParkingEvent is going to be executed.
     * @param id The id of ParkingSlot.
     */
    public void setParkingSlotId(int id)
    {
        this.parkingSlotId = id;
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
     * @return The remaining time the vehicle will be parking/charging.
     */
    public long getReaminingParkingTime()
    {

        long diff = System.currentTimeMillis() - timestamp2;
        if ((parkingTime - diff >= 0)&&(!condition.equals("arrived")))
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
     * Sets the cost for this ParkingEvent.
     * @param cost Teh cost to be set.
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
     * Sets the id for this ParkingEvent.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}