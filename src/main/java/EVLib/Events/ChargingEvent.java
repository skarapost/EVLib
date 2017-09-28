package EVLib.Events;

import EVLib.EV.ElectricVehicle;
import EVLib.Station.Charger;
import EVLib.Station.ChargingStation;
import EVLib.Station.ExchangeHandler;
import EVLib.Station.WaitList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ChargingEvent
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private final ChargingStation station;
    private final String chargingStationName;
    private double amountOfEnergy;
    private String kindOfCharging;
    private long waitingTime;
    private ElectricVehicle vehicle;
    private long chargingTime;
    private long remainingChargingTime;
    private String condition;
    private int chargerId;
    private int numberOfBattery;
    private double energyToBeReceived;
    private long maxWaitingTime;
    private long timestamp;
    private double cost;
    public static final List<ChargingEvent> chargingLog = new ArrayList<>();
    public static final List<ChargingEvent> exchangeLog = new ArrayList<>();

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, double amEnerg, String kindOfCharging)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.amountOfEnergy = amEnerg;
        this.kindOfCharging = kindOfCharging;
        this.chargerId = -1;
        this.vehicle = vehicle;
        this.condition = "arrived";
        this.chargingStationName = station.getName();
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
        this.chargingStationName = station.getName();
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
        this.chargingStationName = station.getName();
    }

    /**
     * Executes the charging phase. Checks for any Charger or exchange slot,
     * calculates the energy to be given to the ElectricVehicle and calculates the charging time.
     * If there is not any empty Charger or exchange slot the ChargingEvent is inserted
     * in the respectively waiting list.
     **/
    public void preProcessing()
    {
        if (vehicle.getBattery().getActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                if (!"exchange".equals(kindOfCharging)) {
                    int qwe = station.checkChargers(kindOfCharging);
                    if ((qwe != -1) && (qwe != -2)) {
                        chargerId = qwe;
                        Charger ch = station.searchCharger(chargerId);
                        ch.setChargingEvent(this);
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
                        if (energyToBeReceived == 0) {
                            setCondition("nonExecutable");
                            ch.setChargingEvent(null);
                            return;
                        }
                        if ("fast".equals(kindOfCharging))
                            chargingTime = ((long) (energyToBeReceived / station.getChargingRatioFast()));
                        else
                            chargingTime = ((long) (energyToBeReceived / station.getChargingRatioSlow()));
                        this.cost = station.calculatePrice(this);
                        setCondition("ready");
                    } else if (qwe == -2)
                        setCondition("nonExecutable");
                    else
                        if(!condition.equals("wait")) {
                            maxWaitingTime = calWaitingTime();
                            if (maxWaitingTime < waitingTime) {
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
                        } else if (state2 == -1)
                            if(!condition.equals("wait"))
                            {
                                maxWaitingTime = calWaitingTime();
                                if (maxWaitingTime < waitingTime) {
                                    if (!condition.equals("wait"))
                                        station.updateQueue(this);
                                    setCondition("wait");
                                } else {
                                    setCondition("nonExecutable");
                                    return;
                                }
                            }
                        else {
                            setCondition("nonExecutable");
                            return;
                        }
                        chargingTime = station.getTimeOfExchange();
                        this.cost = station.getExchangePrice();
                        eh.setChargingEvent(this);
                        setCondition("ready");
                    } else if (qwe == -2)
                        setCondition("nonExecutable");
                    else
                        if(!condition.equals("wait"))
                        {
                            maxWaitingTime = calWaitingTime();
                            if (maxWaitingTime < waitingTime) {
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
        if ((condition.equals("ready")))
        {
            if (!kindOfCharging.equals("exchange"))
            {
                setCondition("charging");
                station.searchCharger(chargerId).executeChargingEvent();
            }
            else
            {
                setCondition("swapping");
                station.searchExchangeHandler(chargerId).executeExchange(numberOfBattery);
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
     * Sets a vehicle to the ChargingEvent.
     * @param vehicle The vehicle to be set.
     */
    public void setElectricVehicle(ElectricVehicle vehicle) { this.vehicle = vehicle; }

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
    public String getKindOfCharging()
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
     * @return The name of the ChargingStation.
     */
    public String getChargingStationName() {
        return chargingStationName;
    }

    /**
     * @return The energy to be received by ElectricVehicle.
     */
    public double getEnergyToBeReceived()
    {
        return energyToBeReceived;
    }

    /**
     * Sets the energy to be received by the vehicle. It also calculates the charging time.
     * @param energyToBeReceived The energy to be set.
     */
    public void setEnergyToBeReceived(double energyToBeReceived)
    {
        this.energyToBeReceived = energyToBeReceived;
    }

    /**
     * Sets the maximum time a ChargingEvent has to wait in the waiting list.
     *
     * @param maxWaitingTime The time to be set.
     */
    public void setMaxWaitingTime(long maxWaitingTime) { this.maxWaitingTime = maxWaitingTime; }

    /**
     * @return The amount of energy the ElectricVehicle asks.
     */
    public double getAmountOfEnergy()
    {
        return amountOfEnergy;
    }

    /**
     * Sets the amount of energy the ChargingEvent demands.
     * @param energy The energy to be set.
     */
    public void setAmountOfEnergy(double energy) { this.amountOfEnergy = energy; }

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
     * @return The remaining charging time of the ChargingEvent.
     */
    public long getRemainingChargingTime()
    {
        long diff = System.currentTimeMillis() - timestamp;
        if ((chargingTime - diff >= 0)&&(!condition.equals("arrived")))
            this.remainingChargingTime = chargingTime - diff;
        else
            return 0;
        return remainingChargingTime;
    }

    /**
     * Sets the charging time of the ChargingEvent.
     * @param time The charging time.
     */
    public void setChargingTime(long time)
    {
        timestamp = System.currentTimeMillis();
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
     * can be charged. This calculation happens in case an ElectricVehicle adds has to
     * be added in the WaitingList.
     * @return The waiting time.
     */
    private long calWaitingTime()
    {
        long[] counter1 = new long[station.getChargers().length];
        long[] counter2 = new long[station.getExchangeHandlers().length];
        long min = 1000000000;
        int index = 1000000000;
        if (!Objects.equals("exchange", getKindOfCharging()))
            for (int i = 0; i < station.getChargers ().length; i++) {
                if (Objects.equals(getKindOfCharging(), station.getChargers()[i].getKindOfCharging())) {
                    long diff = station.getChargers()[i].getChargingEvent().getRemainingChargingTime();
                    if (min > diff) {
                        min = diff;
                        index = i;
                    }
                    counter1[i] = diff;
                }
            }
        else
            for (int i = 0; i < station.getExchangeHandlers().length; i++) {
                long diff = station.getExchangeHandlers()[i].getChargingEvent().getRemainingChargingTime();
                if (min > diff) {
                    min = diff;
                    index = i;
                }
                counter2[i] = diff;
            }
        ChargingEvent e;
        if ("slow".equals(getKindOfCharging()))
        {
            WaitList o = station.getSlow();
            for (int i = 0;i < o.getSize() ;i++)
            {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/station.getChargingRatioSlow()));
                for(int j=0; j<station.getChargers().length; j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("fast".equals(getKindOfCharging()))
        {
            WaitList o = station.getFast();
            for(int i = 0; i < o.getSize() ;i++)
            {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/station.getChargingRatioFast()));
                for(int j=0; j<station.getChargers().length; j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("exchange".equals(getKindOfCharging()))
        {
            for(int i = 0; i < station.getExchange().getSize();i++)
            {
                counter2[index] = counter2[index] + station.getTimeOfExchange();
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

    /**
     * Sets the cost for this ChargingEvent.
     * @param cost The cost to be set.
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     * Sets the id for this ChargingEvent.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}