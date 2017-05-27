package Events;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Station.ChargingStation;
import EV.ElectricVehicle;
import java.util.Date;

public class ChargingEvent 
{
    private ChargingStation station;
    private float amEnerg;
    private String kindOfCharging;
    private float waitingTime;
    private ElectricVehicle vehicle;
    private int chargingTime;
    private int mode; 
    private int chargerId;
    private int numberOfBattery;
    private float energyToBeReceived;
    private Date date;
    private int dateArrival;
    private float stock;
    private long startTime;
    private int maxWaitingTime;

    /**
     * Constructor of ChargingEvent class.
     * @param station The ChargingStation the ChargingEvent visited.
     * @param vehicle The Vehicle that is going to be charged.
     * @param amEnerg The energy the Vehicle is asking for.
     * @param kindOfCharging The kind of charging that is asking the Vehicle.
     */
    public ChargingEvent(ChargingStation station,ElectricVehicle vehicle,float amEnerg,String kindOfCharging)
    {
        this.station = station;
        this.amEnerg = amEnerg;
        this.kindOfCharging = kindOfCharging;
        chargerId = -1;
        this.vehicle = vehicle;
        mode = 1;
        energyToBeReceived = 0;
        date = new Date();
        if ("fast".equals(kindOfCharging))
            chargingTime = (int) (amEnerg/station.reChargingRatioFast()) + 1;
        else
            chargingTime = (int) (amEnerg/station.reChargingRatioSlow()) + 1;
        dateArrival =(int) (date.getTime()/1000);
        stock = station.reTotalEnergy();
    }

    /**
     * Constructor of ChargingEvent class.
     * @param station The ChargingStation object the ChargingEvent visited.
     * @param vehicle The Vehicle object that is going to be charged.
     * @param kindOfCharging The kind of charging that is asking the Vehicle.
     * @param money The money for which the Vehicle is asking for the respectively energy.
     */
    public ChargingEvent(ChargingStation station,ElectricVehicle vehicle,String kindOfCharging,float money)
    {
        this.station = station;
        this.vehicle = vehicle;
        this.kindOfCharging = kindOfCharging;
        chargerId = -1;
        energyToBeReceived = 0;
        date = new Date();
        dateArrival = (int) (date.getTime()/1000);
        stock = station.reTotalEnergy();
        mode = 1;
        if (money/station.reUnitPrice() <= station.reTotalEnergy())
            amEnerg = money/station.reUnitPrice();
        else
            amEnerg = station.reTotalEnergy();
        if ("fast".equals(kindOfCharging))
            chargingTime = (int) (amEnerg/station.reChargingRatioFast()) + 1;
        else
            chargingTime = (int) (amEnerg/station.reChargingRatioSlow()) + 1;
    }

    /**
     * Constructor of ChargingEvent class. It is used for a battery exchange.
     * @param station The ChargingStation the ChargingEvent visited.
     * @param vehicle The Vehicle object is going to be charged.
     * @param kindOfCharging The kind of charging that is asking the Vehicle.
     */
    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle,String kindOfCharging)
    {
        this.station = station;
        this.kindOfCharging = kindOfCharging;
        chargerId = -1;
        this.vehicle = vehicle;
        chargingTime = station.reTimeOfExchange();
        date = new Date();
        mode = 1;
        energyToBeReceived = 0;
        dateArrival = (int) (date.getTime()/1000);
        stock = station.reTotalEnergy();
    }

    /**
     * Executes the pre-proccessing method. Checks for any Charger or exchange slot, 
     * calculates the energy to be given to the Vehicle 
     * and calculates the charging time. If there is not any empty Charger or exchange 
     * slot the ChargingEvent is inserted in the respectively list.
     */
    public final void preProcessing()
    {
        if ((mode == 1)||(mode == 3))
        {
            if (!"exchange".equals(kindOfCharging))
            {
                int qwe = station.checkChargers(kindOfCharging);
                if (qwe != -1)
                {
                    chargerId = qwe;
                    station.searchCharger(chargerId).setChargingEvent(this);
                    setMode(2);
                    station.searchCharger(chargerId).changeSituation();
                    if (amEnerg < station.reTotalEnergy())
                    {
                        if (amEnerg <= (vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount()))
                            energyToBeReceived = amEnerg;
                        else 
                            energyToBeReceived = vehicle.reBattery().reBatteryCapacity() - vehicle.reBattery().reRemAmount();
                        station.setTotalEnergy(energyToBeReceived);
                        if ("fast".equals(kindOfCharging))
                            chargingTime = (int) ((energyToBeReceived)/station.reChargingRatioFast()) + 1;
                        else
                            chargingTime = (int) (energyToBeReceived/station.reChargingRatioSlow()) + 1;
                    }
                    else
                    {
                        station.reChargingStationHandler().energyDistribution(startTime);
                        station.setTotalEnergy(station.reTotalEnergy());
                        if (energyToBeReceived == 0)
                        {
                            station.searchCharger(chargerId).changeSituation();
                            setMode(5);
                        }
                    }
                }
                else
                {
                    int time = station.reChargingStationHandler().calWaitingTime(this);
                    maxWaitingTime = time;
                    if (time < waitingTime)
                    {
                        if (mode != 3)
                            station.updateQueue(this);
                        setMode(3);
                    }
                    else
                        setMode(5);
                }
            }
            else
            {
                int qwe = station.checkExchangeSlots();
                if ((qwe != -1)&&(qwe != -753159))
                {
                    setMode(2);
                    chargerId = qwe;
                    station.reExchangeHandler(qwe).joinChargingEvent(this);
                    int state2 = station.checkBatteries();
                    if ((state2 != -1)&&(state2 != -753159))
                    {
                        setMode(2);
                        numberOfBattery = state2;
                        station.setExchangeSlot(qwe, true);
                    }
                    else if(state2 == -1)
                    {
                        if (station.reChargingStationHandler().calWaitingTime(this) < waitingTime)
                        {
                            if (mode != 3)
                                station.updateQueue(this);
                            setMode(3);
                        }
                        else
                            setMode(5);
                    }
                    else
                        setMode(5);
                }
                else if (qwe == -753159)
                {
                    setMode(5);
                }
                else
                {
                    int time = station.reChargingStationHandler().calWaitingTime(this);
                    maxWaitingTime = time;
                    if (time < waitingTime)
                    {
                        setMode(3);
                        station.updateQueue(this);
                    }
                    else
                        setMode(5);
                }
            }
        }
    }

    /**
     * It starts the execution of the ChargingEvent. 
     * The function modifies the table depends the kind of function that is going to be 
     * executed(charging, exchange). Checks if there needs to become any update storage. 
     * Starts the charging. If the ChargingEvent was in the list, then this 
     * method does not do anything.
     */
    public final void execution()
    {
        if (mode == 2)
        {
            if (!"exchange".equals(kindOfCharging))
            {
                Date now = new Date();
                long now1 =  now.getTime()/1000;
                startTime = now1;
                long diff = (now1-station.reCreationDate())/station.reTimeMoment();
                long l;
                float x1;
                x1 = ((float)(now1 - station.reCreationDate())/(float) station.reTimeMoment()) + (float) chargingTime/(float) station.reTimeMoment();
                int x2 = (int) x1;
                if (x1/x2 != 1)
                {
                    x1 = x2 + 1;
                }
                if (station.reTimeMoment() != 1)
                {
                    l = (long) x1;
                }
                else
                    l = diff + chargingTime/station.reTimeMoment();
                for(long i = diff;i<l;i++)
                {
                    station.reChargingStationHandler().modifyChargingArray(station.searchChargerPlace(chargerId),(int) i);
                }
                if (station.reUpdateMode() == 0)
                    station.reChargingStationHandler().checkUpdatePredefinedSpace();
                else
                    station.reChargingStationHandler().checkUpdateMadeSpace();
                station.searchCharger(chargerId).setCommitTime(chargingTime);
                station.searchCharger(chargerId).executeChargingEvent(this);
            }
            else
            {
                Date now = new Date();
                long now1 =  now.getTime()/1000;
                startTime = now1;
                long diff = (now1-station.reCreationDate())/station.reTimeMoment();
                long l;
                float x1;
                x1 = ((float)(now1 - station.reCreationDate())/(float) station.reTimeMoment()) + (float) chargingTime/(float) station.reTimeMoment();
                int x2 = (int) x1;
                if (x1/x2 != 1)
                {
                    x1 = x2 + 1;
                }
                if (station.reTimeMoment() != 1)
                {
                    l = (long) x1;
                }
                else
                    l = diff + chargingTime/station.reTimeMoment();
                for(long i = diff;i<l;i++)
                {
                    station.reChargingStationHandler().modifyExchangeArray(chargerId,(int) i);
                }
                if (station.reUpdateMode() == 0)
                    station.reChargingStationHandler().checkUpdatePredefinedSpace();
                else
                    station.reChargingStationHandler().checkUpdateMadeSpace();
                station.reExchangeHandler(chargerId).executeExchange(numberOfBattery);
            }
        }
    }

    /**
     * Returns the time the ChargingEvent starts charging.
     * @return The start time.
     */
    public final long reStartTime()
    {
        return startTime;
    }

    /**
     * Returns the ElectricVehicle that is going to be charged.
     * @return The ElectricVehicle.
     */
    public final ElectricVehicle reElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Sets the mode of ChargingEvent.
     * @param h Value of the mode.
     */
    public void setMode(int h)
    {
        mode = h;
    }

    /**
     * Returns the kind of charging the ChargingEvent wants.
     * @return The kind of charging of ChargingEvent.
     */
    public final String reKind()
    {
        return kindOfCharging;
    }

    /**
     * Returns the ChargingStation the ChargingEvent visited.
     * @return The ChargingStation.
     */
    public final ChargingStation reStation()
    {
        return station;
    }

    /**
     * Returns the energy that is going to be received by the ElectricVehicle.
     * @return The energy to be received by ElectricVehicle.
     */
    public final float reEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the value of e, as the energy to be given in the ElectricVehicle.
     * @param e Energy to be set.
     */
    public final void setEnergyToBeReceived(float e)
    {
        energyToBeReceived = e;
    }

    /**
     * Returns the amount of energy the ElectricVehicle asks.
     * @return The amount of energy the ElectricVehicle asks.
     */
    public final float reEnergyAmount()
    {
        return amEnerg;
    }

    /**
     * Sets the waiting time the Driver is able to wait.
     * @param w The waiting time.
     */
    public final void setWaitingTime(float w)
    {
        waitingTime = w;
    }

    /**
     * Returns the waiting time the Driver of the ElectricVehicle can wait.
     * @return The waiting time.
     */
    public final float reWaitingTime()
    {
        return waitingTime;
    }

    /**
     * Returns the time that the charging is going to take place.
     * @return The charging time of the ChargingEvent.
     */
    public final int reChargingTime()
    {
        return chargingTime;
    }

    /**
     * Sets the charging time of the ChargingEvent.
     * @param time The charging time.
     */
    public final void setChargingTime(int time)
    {
        chargingTime = time;
    }

    /**
     * Sets the id of the Charger the ChargingEvent is going to be executed.
     * @param id The id of Charger.
     */
    public final void setChargerId(int id)
    {
        chargerId = id;
    }

    /**
     * Returns the time the ElectricVehicle arrived.
     * @return The the time that the ChargingEvent created.
     */
    public final int reDateArrival()
    {
        return dateArrival;
    }

    /**
     * Returns the amount of energy there was in the ChargingStation when the ChargingEvent created.
     * @return The amount of energy the ChargingStation has.
     */
    public final float reStock()
    {
        return stock;
    }

    /**
     * Returns the mode of the ChargingEvent.
     * @return The mode of ChargingEvent.
     */
    public final int reMode()
    {
        return mode;
    }

    /**
     * Sets the time the ElectricVehicle have to wait to be charged, in case it was inserted in the list.
     * @param time The maximum time that is going to wait until an inserted ChargingEvent is going to be charged.
     */
    public final void setMaxWaitingTime(int time)
    {
        maxWaitingTime = time;
    }

    /**
     * Returns the maximum time an ElectricVehicle has to wait until it can be charged.
     * @return The maximum wait time.
     */
    public final int reMaxWaitingTime()
    {
        return maxWaitingTime;
    }
}