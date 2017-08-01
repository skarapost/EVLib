package Events;

import EV.ElectricVehicle;
import Station.ChargingStation;

public class ParkingEvent {
    private long parkingTime;
    private ElectricVehicle vehicle;
    private ChargingStation station;
    private long timeOfCharging;
    private double amountOfEnergy;
    private double energyToBeReceived;
    private int parkingSlotId;
    private long dateArrival;
    private long startTime;
    private String condition;

    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime)
    {
        this.parkingTime = parkingTime;
        this.station = station;
        this.vehicle = vehicle;
        this.parkingSlotId = -1;
        this.condition = "parking";
    }

    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime, double amountOfEnergy)
    {
        this.vehicle = vehicle;
        this.station = station;
        this.parkingTime = parkingTime;
        this.amountOfEnergy = amountOfEnergy;
        this.parkingSlotId = -1;
        this.condition = "parking";
    }

    /**
     * Returns the ElectricVehicle that is going to be charged.
     * @return The ElectricVehicle object.
     */
    public ElectricVehicle reElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Executes the charging phase. Checks for any ParkingSlot,
     * calculates the energy to be given to the Vehicle and calculates the charging time.
     * If there is not any empty ParkingSlot the ChargingEvent is charecterized as "nonExecutable".
     * In the end, the function calls the chargingVehicle()
     * function of the corresponding ParkingSlot object to implement the charging, or the parking 
     */
    public void execution()
    {
        int qwe = station.checkParkingSlots();
        if ((qwe != -1) && (qwe != -2)) {
            parkingSlotId = qwe;
            if ((station.searchParkingSlot(parkingSlotId).reInSwitch())&&(vehicle.reBattery().reActive())) {
                if (amountOfEnergy < station.reTotalEnergy()) {
                    if (amountOfEnergy <= (vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount()))
                        energyToBeReceived = amountOfEnergy;
                    else
                        energyToBeReceived = vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount();
                    station.setTotalEnergy(energyToBeReceived);
                    timeOfCharging = (long) ((energyToBeReceived) / station.reInductiveRatio());
                } else {
                    station.energyDistribution(dateArrival);
                    station.setTotalEnergy(station.reTotalEnergy());
                    if (energyToBeReceived == 0) {
                        setCondition("parking");
                        station.searchParkingSlot(parkingSlotId).setParkingEvent(this);
                        station.searchParkingSlot(parkingSlotId).changeSituation();
                        return;
                    }
                }
            }
            else
            {
                setCondition("parking");
                station.searchParkingSlot(parkingSlotId).setParkingEvent(this);
                station.searchParkingSlot(parkingSlotId).changeSituation();
                return;
            }
            station.searchParkingSlot(parkingSlotId).setParkingEvent(this);
            station.searchParkingSlot(parkingSlotId).changeSituation();
            station.searchParkingSlot(parkingSlotId).setChargingTime(timeOfCharging);
            setStartTime (station.getTime());
            setCondition("charging");
            station.checkForUpdate();
            station.searchParkingSlot(parkingSlotId).chargingVehicle();
        }
        else
            setCondition("nonExecutable");
    }

    /**
     * @return The ChargingStation the event is going to be executed.
     */
    public ChargingStation reStation()
    {
        return station;
    }

    /**
     * @return The energy to be received by ElectricVehicle.
     */
    public double reEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the value of e, as the energy to be given in the ElectricVehicle.
     * @param e Energy to be set.
     */
    public void setEnergyToBeReceived(double e) { energyToBeReceived = e; }

    /**
     * @return The charging time of the ParkingEvent.
     */
    public long reChargingTime()
    {
        return timeOfCharging;
    }

    /**
     * Sets the charging time of the ParkingEvent.
     * @param time The charging time.
     */
    public void setChargingTime(int time)
    {
        this.timeOfCharging = time;
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
     * @return The the time that the ParkingEvent created.
     */
    public long reDateArrival()
    {
        return dateArrival;
    }

    /**
     * Sets the starting time of an event.
     * @param time The time moment an event starts.
     */
    public void setStartTime(long time)
    {
        startTime = time;
    }

    /**
     * @return The starting time of the event.
     */
    public long reStartTime()
    {
        return startTime;
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
    public String reCondition()
    {
        return condition;
    }
}