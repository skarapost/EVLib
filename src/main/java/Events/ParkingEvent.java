package Events;

import EV.ElectricVehicle;
import Station.ChargingStation;
import Station.ParkingSlot;

public class ParkingEvent {
    private long parkingTime;
    private ElectricVehicle vehicle;
    private ChargingStation station;
    private long timeOfCharging;
    private double amountOfEnergy;
    private double energyToBeReceived;
    private int parkingSlotId;
    private long timestamp1;
    private long timestamp2;
    private String condition;

    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime)
    {
        setParkingTime(parkingTime);
        this.station = station;
        this.vehicle = vehicle;
        this.parkingSlotId = -1;
        this.condition = "arrived";
    }

    public ParkingEvent(ChargingStation station, ElectricVehicle vehicle, long parkingTime, double amountOfEnergy)
    {
        this.vehicle = vehicle;
        this.station = station;
        setParkingTime(parkingTime);
        this.amountOfEnergy = amountOfEnergy;
        this.parkingSlotId = -1;
        this.condition = "arrived";
    }

    /**
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
            ParkingSlot ps = station.searchParkingSlot(parkingSlotId);
            if ((amountOfEnergy != 0)&&(ps.reInSwitch())&&(vehicle.reBattery().reActive())) {
                if (amountOfEnergy < station.reTotalEnergy()) {
                    if (amountOfEnergy <= (vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount()))
                        energyToBeReceived = amountOfEnergy;
                    else
                        energyToBeReceived = vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount();
                    station.setTotalEnergy(energyToBeReceived);
                    timeOfCharging = (long) ((energyToBeReceived) / station.reInductiveRatio());
                } else {
                    amountOfEnergy = station.reTotalEnergy();
                    station.setTotalEnergy(station.reTotalEnergy());
                    if (energyToBeReceived == 0) {
                        setCondition("parking");
                        setParkingTime(parkingTime);
                        ps.setParkingEvent(this);
                        ps.changeSituation();
                        ps.setCommitTime(parkingTime);
                        station.checkForUpdate();
                        ps.parkingVehicle();
                        return;
                    }
                }
            }
            else
            {
                setCondition("parking");
                setParkingTime(parkingTime);
                ps.setParkingEvent(this);
                ps.changeSituation();
                ps.setCommitTime(parkingTime);
                station.checkForUpdate();
                ps.parkingVehicle();
                return;
            }
            ps.setParkingEvent(this);
            ps.changeSituation();
            ps.setCommitTime(parkingTime);
            setParkingTime(parkingTime);
            ps.setChargingTime(timeOfCharging);
            setCondition("charging");
            station.checkForUpdate();
            ps.parkingVehicle();
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
     * @return The energy to be given to the ElectricVehicle.
     */
    public double reEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the value of e, as the energy to be given in the ElectricVehicle.
     * @param e Energy to be given.
     */
    public void setEnergyToBeReceived(double e) {
        energyToBeReceived = e;
    }

    /**
     * @return The elapsed charging time of the ParkingEvent.
     */
    public long reElapsedChargingTime()
    {
        long diff = station.getTime() - timestamp1;
        if (timeOfCharging - diff >= 0)
            return timeOfCharging - diff;
        else
            return 0;
    }

    /**
     * @return The charging time of the ElectricVehicle.
     */
    public long reChargingTime()
    {
        return timeOfCharging;
    }

    /**
     * @return The parking time.
     */
    public long reParkingTime()
    {
        return parkingTime;
    }

    /**
     * Sets the charging time of the ParkingEvent.
     * @param time The charging time.
     */
    public void setChargingTime(int time)
    {
        timestamp1 = station.getTime();
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

    /**
     * @return The elapsed time the vehicle will be parking/charging.
     */
    public long reElapsedParkingTime()
    {
        long diff = station.getTime() - timestamp2;
        if (parkingTime - diff >= 0)
            return parkingTime - diff;
        else
            return 0;
    }

    /**
     * Sets the parking time for the ElectricVehicle.
     * @param parkingTime The parking time.
     */
    public void setParkingTime(long parkingTime)
    {
        timestamp2 = station.getTime();
        this.parkingTime = parkingTime;
    }
}