package Events;

/**
 *
 * @author Sotiris Karapostolakis
 */

import EV.ElectricVehicle;
import Station.ChargingStation;

public class ChargingEvent
{
    private ChargingStation station;
    private float amEnerg;
    private String kindOfCharging;
    private long waitingTime;
    private ElectricVehicle vehicle;
    private long chargingTime;
    private String condition;
    private int chargerId;
    private int numberOfBattery;
    private float energyToBeReceived;
    private long dateArrival;
    private float stock;
    private long maxWaitingTime;
    private long startTime;

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, float amEnerg, String kindOfCharging)
    {
        this.station = station;
        this.amEnerg = amEnerg;
        this.kindOfCharging = kindOfCharging;
        this.chargerId = -1;
        this.vehicle = vehicle;
        this.condition = "arrived";
        this.energyToBeReceived = 0;
        if ("fast".equals(kindOfCharging))
            this.chargingTime = (long) (amEnerg/station.reChargingRatioFast());
        else
            this.chargingTime = (long) (amEnerg/station.reChargingRatioSlow());
        this.dateArrival = station.getTime();
        this.startTime = 0;
        this.stock = station.reTotalEnergy();
    }

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, String kindOfCharging, float money)
    {
        this.station = station;
        this.vehicle = vehicle;
        this.kindOfCharging = kindOfCharging;
        this.chargerId = -1;
        this.condition = "arrived";
        this.energyToBeReceived = 0;
        this.startTime = 0;
        this.dateArrival = station.getTime();
        this.stock = station.reTotalEnergy();
        if (money/station.reUnitPrice() <= station.reTotalEnergy())
            this.amEnerg = money/station.reUnitPrice();
        else
            this.amEnerg = station.reTotalEnergy();
        if ("fast".equals(kindOfCharging))
            this.chargingTime = (long) (amEnerg/station.reChargingRatioFast());
        else
            this.chargingTime = (long) (amEnerg/station.reChargingRatioSlow());
    }

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, String kindOfCharging)
    {
        this.station = station;
        this.kindOfCharging = kindOfCharging;
        this.chargerId = -1;
        this.startTime = 0;
        this.vehicle = vehicle;
        this.chargingTime = station.reTimeOfExchange();
        this.condition = "arrived";
        this.energyToBeReceived = 0;
        this.dateArrival = station.getTime();
        this.stock = station.reTotalEnergy();
    }

    /**
     * Executes the pre-proccessing method. Checks for any Charger or exchange slot,
     * calculates the energy to be given to the Vehicle
     * and calculates the charging time. If there is not any empty Charger or exchange
     * slot the ChargingEvent is inserted in the respectively waiting list.
     */
    public void preProcessing()
    {
        if ((condition.equals("arrived"))||(condition.equals("wait")))
        {
            if (!"exchange".equals(kindOfCharging))
            {
                int qwe = station.checkChargers(kindOfCharging);
                if ((qwe != -1)&&(qwe != -753159))
                {
                    chargerId = qwe;
                    station.searchCharger(chargerId).setChargingEvent(this);
                    setCondition("ready");
                    station.searchCharger(chargerId).changeSituation();
                    if (amEnerg < station.reTotalEnergy())
                    {
                        if (amEnerg <= (vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount()))
                            energyToBeReceived = amEnerg;
                        else
                            energyToBeReceived = vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount();
                        station.setTotalEnergy(energyToBeReceived);
                        if ("fast".equals(kindOfCharging))
                            chargingTime = (long) ((energyToBeReceived)/station.reChargingRatioFast());
                        else
                            chargingTime = (long) (energyToBeReceived/station.reChargingRatioSlow());
                    }
                    else
                    {
                        station.energyDistribution(dateArrival);
                        station.setTotalEnergy(station.reTotalEnergy());
                        if (energyToBeReceived == 0)
                        {
                            station.searchCharger(chargerId).changeSituation();
                            station.searchCharger (chargerId).setChargingEvent (null);
                            setCondition("nonExecutable");
                        }
                    }
                }
                else if (qwe == -753159)
                    setCondition("nonExecutable");
                else
                {
                    long time = station.calWaitingTime(this);
                    maxWaitingTime = time;
                    if (time < waitingTime)
                    {
                        if (!condition.equals("wait"))
                            station.updateQueue(this);
                        setCondition("wait");
                    }
                    else
                        setCondition("nonExecutable");
                }
            }
            else
            {
                int qwe = station.checkExchangeHandlers();
                if ((qwe != -1)&&(qwe != -753159))
                {
                    chargerId = qwe;
                    station.searchExchangeHandler(chargerId).joinChargingEvent(this);
                    setCondition("ready");
                    station.searchExchangeHandler(chargerId).changeSituation();
                    int state2 = station.checkBatteries();
                    if ((state2 != -1)&&(state2 != -753159))
                    {
                        setCondition("ready");
                        numberOfBattery = state2;
                    }
                    else if(state2 == -1)
                    {
                        if (station.calWaitingTime(this) < waitingTime)
                        {
                            if (!condition.equals("wait"))
                                station.updateQueue(this);
                            setCondition("wait");
                        }
                        else
                            setCondition("nonExecutable");
                    }
                    else
                        setCondition("nonExecutable");
                }
                else if (qwe == -753159)
                    setCondition("nonExecutable");
                else
                {
                    long time = station.calWaitingTime(this);
                    maxWaitingTime = time;
                    if (time < waitingTime)
                    {
                        setCondition("wait");
                        station.updateQueue(this);
                    }
                    else
                        setCondition("nonExecutable");
                }
            }
        }
    }

    /**
     * It starts the execution of the ChargingEvent.
     * The function modifies the table depends the kind of function that is going to be
     * executed(charging, exchange). Checks if there needs to become any update storage.
     * Starts the charging. If the ChargingEvent was in the waiting list, then this
     * method does not do anything.
     */
    public void execution()
    {
        if (condition.equals("ready"))
        {
            if (!"exchange".equals(kindOfCharging))
            {
                station.checkForUpdate();
                station.searchCharger(chargerId).setCommitTime(chargingTime);
                setStartTime (station.getTime());
                station.searchCharger(chargerId).executeChargingEvent();
            }
            else
            {
                station.checkForUpdate();
                setStartTime (station.getTime());
                station.searchExchangeHandler(chargerId).executeExchange(numberOfBattery);
            }
        }
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
     * Sets the condition of ChargingEvent.
     * @param condition Value of the condition.
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    /**
     * Returns the kind of charging the ChargingEvent wants.
     * @return The kind of charging of ChargingEvent.
     */
    public String reKind()
    {
        return kindOfCharging;
    }

    /**
     * Returns the ChargingStation the ChargingEvent visited.
     * @return The ChargingStation.
     */
    public ChargingStation reStation()
    {
        return station;
    }

    /**
     * Returns the energy that is going to be received by the ElectricVehicle.
     * @return The energy to be received by ElectricVehicle.
     */
    public float reEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the value of e, as the energy to be given in the ElectricVehicle.
     * @param e Energy to be set.
     */
    public void setEnergyToBeReceived(float e)
    {
        energyToBeReceived = e;
    }

    /**
     * Returns the amount of energy the ElectricVehicle asks.
     * @return The amount of energy the ElectricVehicle asks.
     */
    public float reEnergyAmount()
    {
        return amEnerg;
    }

    /**
     * Sets the waiting time the Driver is able to wait.
     * @param w The waiting time.
     */
    public void setWaitingTime(long w)
    {
        this.waitingTime = w;
    }

    /**
     * Returns the waiting time the Driver of the ElectricVehicle can wait.
     * @return The waiting time.
     */
    public long reWaitingTime()
    {
        return waitingTime;
    }

    /**
     * Returns the time that the charging is going to take place.
     * @return The charging time of the ChargingEvent.
     */
    public long reChargingTime()
    {
        return chargingTime;
    }

    /**
     * Sets the charging time of the ChargingEvent.
     * @param time The charging time.
     */
    public void setChargingTime(int time)
    {
        this.chargingTime = time;
    }

    /**
     * Sets the id of the Charger the ChargingEvent is going to be executed.
     * @param id The id of Charger.
     */
    public void setChargerId(int id)
    {
        chargerId = id;
    }

    /**
     * Returns the time the ElectricVehicle arrived.
     * @return The the time that the ChargingEvent created.
     */
    public long reDateArrival()
    {
        return dateArrival;
    }

    public void setStartTime(long time)
    {
        startTime = time;
    }

    public long reStartTime()
    {
        return startTime;
    }

    /**
     * Returns the amount of energy there was in the ChargingStation when the ChargingEvent created.
     * @return The amount of energy the ChargingStation has.
     */
    public float reStock()
    {
        return stock;
    }

    /**
     * Returns the condition of the ChargingEvent.
     * @return The condition of ChargingEvent.
     */
    public String reCondition()
    {
        return condition;
    }

    /**
     * Sets the time the ElectricVehicle have to wait to be charged, in case it was inserted in the list.
     * @param time The maximum time that is going to wait until an inserted ChargingEvent is going to be charged.
     */
    public void setMaxWaitingTime(long time)
    {
        this.maxWaitingTime = time;
    }

    /**
     * Returns the maximum time an ElectricVehicle has to wait until it can be charged.
     * @return The maximum wait time.
     */
    public long reMaxWaitingTime()
    {
        return maxWaitingTime;
    }
}