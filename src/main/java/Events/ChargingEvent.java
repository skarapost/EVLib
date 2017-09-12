package Events;

import EV.ElectricVehicle;
import Station.ChargingStation;
import Station.Charger;
import Station.ExchangeHandler;
import Station.WaitList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ChargingEvent
{
    private int id;
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private ChargingStation station;
    private double amountOfEnergy;
    private String kindOfCharging;
    private long waitingTime;
    private ElectricVehicle vehicle;
    private long chargingTime;
    private String condition;
    private int chargerId;
    private int numberOfBattery;
    private double energyToBeReceived;
    private long maxWaitingTime;
    private long timestamp;
    private double cost;
    public static List<ChargingEvent> chargingLog = new ArrayList<>();
    public static List<ChargingEvent> exchangeLog = new ArrayList<>();

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, double amEnerg, String kindOfCharging)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.amountOfEnergy = amEnerg;
        this.kindOfCharging = kindOfCharging;
        this.chargerId = -1;
        this.vehicle = vehicle;
        this.condition = "arrived";
        if ("fast".equals(kindOfCharging))
            chargingTime = (long) (this.amountOfEnergy / station.getChargingRatioFast());
        else
            chargingTime = (long) (this.amountOfEnergy / station.getChargingRatioSlow());
    }

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, String kindOfCharging, double money)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.vehicle = vehicle;
        this.kindOfCharging = kindOfCharging;
        this.chargerId = -1;
        this.condition = "arrived";
        if (money/station.getUnitPrice() <= station.getTotalEnergy())
            this.amountOfEnergy = money/station.getUnitPrice();
        else
            this.amountOfEnergy = station.getTotalEnergy();
        if ("fast".equals(kindOfCharging))
            chargingTime = (long) (this.amountOfEnergy / station.getChargingRatioFast());
        else
            chargingTime = (long) (this.amountOfEnergy / station.getChargingRatioSlow());
    }

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.kindOfCharging = "exchange";
        this.chargerId = -1;
        this.vehicle = vehicle;
        this.chargingTime = station.getTimeOfExchange();
        this.condition = "arrived";
    }

    /**
     * Executes the charging phase. Checks for any Charger or exchange slot,
     * calculates the energy to be given to the Vehicle and calculates the charging time.
     * If there is not any empty Charger or exchange slot the ChargingEvent is inserted
     * in the respectively waiting list.
     **/
    public void preProcessing()
    {
        if (getElectricVehicle().getBattery().getActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                if (!"exchange".equals(kindOfCharging)) {
                    int qwe = station.checkChargers(kindOfCharging);
                    if ((qwe != -1) && (qwe != -2)) {
                        chargerId = qwe;
                        Charger ch = station.searchCharger(chargerId);
                        if ((amountOfEnergy != 0)&&(vehicle.getBattery().getActive())) {
                            if (amountOfEnergy < station.getTotalEnergy()) {
                                if (amountOfEnergy <= (vehicle.getBattery().getBatteryCapacity() - vehicle.getBattery().getRemAmount()))
                                    energyToBeReceived = amountOfEnergy;
                                else
                                    energyToBeReceived = vehicle.getBattery().getBatteryCapacity() - vehicle.getBattery().getRemAmount();
                            } else {
                                if (station.getTotalEnergy() <= (vehicle.getBattery().getBatteryCapacity() - vehicle.getBattery().getRemAmount()))
                                    energyToBeReceived = station.getTotalEnergy();
                                else
                                    energyToBeReceived = vehicle.getBattery().getBatteryCapacity() - vehicle.getBattery().getRemAmount();
                                if (energyToBeReceived == 0) {
                                    setCondition("nonExecutable");
                                    return;
                                }
                            }
                        }
                        if ("fast".equals(kindOfCharging))
                            setChargingTime((long) (energyToBeReceived / station.getChargingRatioFast()));
                        else
                            setChargingTime((long) (energyToBeReceived / station.getChargingRatioSlow()));
                        this.cost = station.calculatePrice(this);
                        ch.setChargingEvent(this);
                        ch.changeSituation();
                        setCondition("charging");
                    } else if (qwe == -2)
                        setCondition("nonExecutable");
                    else {
                        long time = calWaitingTime();
                        maxWaitingTime = time;
                        if (time < waitingTime) {
                            if (!condition.equals("wait"))
                                station.updateQueue(this);
                            setCondition("wait");
                        } else
                            setCondition("nonExecutable");
                    }
                } else {
                    int qwe = station.checkExchangeHandlers();
                    if ((qwe != -1) && (qwe != -2)) {
                        chargerId = qwe;
                        ExchangeHandler eh = station.searchExchangeHandler(chargerId);
                        int state2 = station.checkBatteries();
                        if ((state2 != -1) && (state2 != -2)) {
                            numberOfBattery = state2;
                        } else if (state2 == -1) {
                            if (calWaitingTime() < waitingTime) {
                                if (!condition.equals("wait"))
                                    station.updateQueue(this);
                                setCondition("wait");
                            } else {
                                setCondition("nonExecutable");
                                return;
                            }
                        } else {
                            setCondition("nonExecutable");
                            return;
                        }
                        this.cost = station.getExchangePrice();
                        eh.joinChargingEvent(this);
                        eh.changeSituation();
                        setCondition("swapping");
                        timestamp = station.getTime();
                    } else if (qwe == -2)
                        setCondition("nonExecutable");
                    else {
                        long time = calWaitingTime();
                        maxWaitingTime = time;
                        if (time < waitingTime) {
                            setCondition("wait");
                            station.updateQueue(this);
                        } else
                            setCondition("nonExecutable");
                    }
                }
            }
        }
        else
            setCondition("nonExecutable");
    }

    /**
     * It starts the execution of the ChargingEvent.
     * If the ChargingEvent is in the WaitingList it does not do anything.
     */
    public void execution()
    {
        if ((condition.equals("charging"))||(condition.equals("swapping")))
        {
            if (!kindOfCharging.equals("exchange"))
            {
                station.searchCharger(chargerId).setCommitTime(chargingTime);
                station.searchCharger(chargerId).executeChargingEvent();
                chargingLog.add(this);
            }
            else
            {
                station.searchExchangeHandler(chargerId).setCommitTime(chargingTime);
                station.searchExchangeHandler(chargerId).executeExchange(numberOfBattery);
                exchangeLog.add(this);
            }
        }
    }

    /**
     * @return The ElectricVehicle of the event.
     */
    public ElectricVehicle getElectricVehicle()
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
     * @return The kind of charging of ChargingEvent.
     */
    public String getKind()
    {
        return kindOfCharging;
    }

    /**
     * @return The ChargingStation the event is going to be executed.
     */
    public ChargingStation getStation()
    {
        return station;
    }

    /**
     * @return The energy to be received by ElectricVehicle.
     */
    public double getEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the value of e, as the energy to be given in the ElectricVehicle.
     * @param e Energy to be set.
     */
    public void setEnergyToBeReceived(double e)
    {
        energyToBeReceived = e;
    }

    /**
     * @return The amount of energy the ElectricVehicle asks.
     */
    public double getAmountOfEnergy()
    {
        return amountOfEnergy;
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
     * @return The waiting time of the ChargingEvent.
     */
    public long getWaitingTime()
    {
        return waitingTime;
    }

    /**
     * @return The charging time of the ChargingEvent.
     */
    public long getElapsedChargingTime()
    {
        long diff = station.getTime() - timestamp;
        if (chargingTime - diff >= 0)
            return chargingTime - diff;
        else
            return 0;
    }

    /**
     * Sets the charging time of the ChargingEvent.
     * @param time The charging time.
     */
    public void setChargingTime(long time)
    {
        timestamp = station.getTime();
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
     * @return The condition of ChargingEvent.
     */
    public String getCondition()
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
     * @return The maximum time the vehicle can wait.
     */
    public long getMaxWaitingTime()
    {
        return maxWaitingTime;
    }

    /**
     * @return The charging time of this ChargingEvent.
     */
    public long getChargingTime()
    {
        return chargingTime;
    }

    /**
     * Calculates the amount of time a Driver has to wait until his ElectricVehicle
     * can be charged. This calculation happens in case a Vehicle adds has to
     * be added in the WaitingList.
     * @return The waiting time.
     */
    private long calWaitingTime()
    {
        long[] counter1 = new long[station.getChargers().length];
        long[] counter2 = new long[station.getExchangeHandlers().length];
        long min = 1000000000;
        int index = 1000000000;
        if (!Objects.equals("exchange", getKind()))
            for (int i = 0; i < station.getChargers ().length; i++) {
                if (Objects.equals(getKind(), station.getChargers()[i].getKindOfCharging())) {
                    long diff = station.getChargers()[i].getChargingEvent().getElapsedChargingTime();
                    if (min > diff) {
                        min = diff;
                        index = i;
                    }
                    counter1[i] = diff;
                }
            }
        else
            for (int i = 0; i<station.getExchangeHandlers().length; i++)
            {
                long diff = station.getExchangeHandlers()[i].getChargingEvent().getElapsedChargingTime();
                if (min > diff) {
                    min = diff;
                    index = i;
                }
                counter2[i] = diff;
            }
        ChargingEvent e = null;
        if ("slow".equals(getKind()))
        {
            WaitList o = station.getSlow();
            for (int i = 0;i < o.getSize() ;i++)
            {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + e.getChargingTime();
                for(int j=0; j<station.getChargers().length; j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("fast".equals(getKind()))
        {
            WaitList o = station.getFast();
            for(int i = 0; i < o.getSize() ;i++)
            {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + e.getChargingTime();
                for(int j=0; j<station.getChargers().length; j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("exchange".equals(getKind()))
        {
            WaitList o = station.getExchange();
            for(int i = 0; i < o.getSize();i++)
            {
                e = (ChargingEvent) o.get(i);
                counter2[index] = counter2[index] + e.getChargingTime();
                for(int j=0; j < station.getChargers().length; j++)
                    if ((counter2[j]<counter2[index])&&(counter2[j]!=0))
                        index = j;
            }
            return counter2[index];
        }
        return 0;
    }

    /**
     * @return The id of this ChargingEvent.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The cost of this ChargingEvent.
     */
    public double getCost()
    {
        return this.cost;
    }
}