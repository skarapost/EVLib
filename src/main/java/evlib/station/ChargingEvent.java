package evlib.station;

import evlib.ev.Battery;
import evlib.ev.ElectricVehicle;

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
    Battery givenBattery;
    private Charger charger;
    private double energyToBeReceived;
    private long maxWaitingTime;
    private long timestamp;
    private double cost;
    private ExchangeHandler exchange;
    long accumulatorOfChargingTime = 0;
    public static final List<ChargingEvent> chargingLog = new ArrayList<>();
    public static final List<ChargingEvent> exchangeLog = new ArrayList<>();

    public ChargingEvent(ChargingStation station, ElectricVehicle vehicle, double amEnerg, String kindOfCharging)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.amountOfEnergy = amEnerg;
        this.kindOfCharging = kindOfCharging;
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
        this.vehicle = vehicle;
        this.chargingTime = station.getTimeOfExchange();
        this.condition = "arrived";
        this.chargingStationName = station.getName();
    }

    /**
     * Executes the pre-processing phase. Checks for any Charger or exchange slot and assignes to it if any.
     * It calculates the energy to be given to the ElectricVehicle and calculates the charging time.
     * If there is not any empty Charger or exchange slot the ChargingEvent is inserted
     * in the respectively waiting list, if the waiting time is less than the set waiting time of the Driver.
     **/
    public synchronized void preProcessing()
    {
        if (vehicle.getBattery().getActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                if (!"exchange".equals(kindOfCharging)) {
                    charger = station.assignCharger(this);
                    if (charger != null) {
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
                        if ("fast".equals(kindOfCharging))
                            chargingTime = ((long) (energyToBeReceived / station.getChargingRatioFast()));
                        else
                            chargingTime = ((long) (energyToBeReceived / station.getChargingRatioSlow()));
                        this.cost = station.calculatePrice(this);
                        setCondition("ready");
                        double sdf;
                        sdf = energyToBeReceived;
                        for (String s : station.getSources()) {
                            if (sdf < station.getMap().get(s)) {
                                double ert = station.getMap().get(s) - sdf;
                                station.setSpecificAmount(s, ert);
                                break;
                            } else {
                                sdf -= station.getMap().get(s);
                                station.setSpecificAmount(s, 0);
                            }
                        }
                    }
                    else
                        if(!condition.equals("wait")) {
                            maxWaitingTime = calWaitingTime();
                            if ((maxWaitingTime < waitingTime) && (maxWaitingTime > -1)) {
                                if (!condition.equals("wait"))
                                    station.updateQueue(this);
                                setCondition("wait");
                            } else
                                setCondition("nonExecutable");
                        }
                } else {
                    exchange = station.assignExchangeHandler(this);
                    givenBattery = station.assignBattery();
                    if (givenBattery == null) {
                        setCondition("nonExecutable");
                        return;
                    }
                    if (exchange != null) {
                        chargingTime = station.getTimeOfExchange();
                        this.cost = station.getExchangePrice();
                        setCondition("ready");
                    }
                    else
                        if(!condition.equals("wait"))
                        {
                            maxWaitingTime = calWaitingTime();
                            if (maxWaitingTime < waitingTime && maxWaitingTime > -1) {
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
     * It starts the execution of the ChargingEvent. Increases the number of chargings of the Battery by one.
     * The pre-condition for the execution is the condition of the event to be "ready".
     */
    public synchronized void execution()
    {
        if (condition.equals("ready"))
            if (!kindOfCharging.equals("exchange"))
            {
                setCondition("charging");
                vehicle.getBattery().addCharging();
                try {
                    charger.executeChargingEvent(false);
                } catch (NullPointerException ex) {
                    System.out.println("No processed");
                }
            }
            else
            {
                setCondition("swapping");
                try {
                    exchange.executeExchange();
                } catch (NullPointerException ex) {
                    System.out.println("No processed");
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
     * Sets an ElectricVehicle to the ChargingEvent.
     * @param vehicle The ElectricVehicle to be set.
     */
    public void setElectricVehicle(ElectricVehicle vehicle) { this.vehicle = vehicle; }

    /**
     * @return The kind of charging of the ChargingEvent.
     */
    public String getKindOfCharging()
    {
        return kindOfCharging;
    }

    /**
     * @return The ChargingStation the ChargingEvent wants to be executed.
     */
    public ChargingStation getStation()
    {
        return station;
    }

    /**
     * Sets the energy to be received by the vehicle.
     *
     * @param energyToBeReceived The energy to be set.
     */
    public void setEnergyToBeReceived(double energyToBeReceived) {
        this.energyToBeReceived = energyToBeReceived;
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
     * @return The amount of energy the ElectricVehicle asks for.
     */
    public double getAmountOfEnergy() {
        return amountOfEnergy;
    }

    /**
     * Sets the maximum time a ChargingEvent has to wait in the waiting list.
     * @param maxWaitingTime The time to be set.
     */
    public void setMaxWaitingTime(long maxWaitingTime) { this.maxWaitingTime = maxWaitingTime; }

    /**
     * Sets the waiting time the Driver can wait.
     * @param w The waiting time.
     */
    public void setWaitingTime(long w) {
        this.waitingTime = w;
    }

    /**
     * Sets the amount of energy the ChargingEvent demands.
     *
     * @param energy The energy to be set.
     */
    public void setAmountOfEnergy(double energy) {
        this.amountOfEnergy = energy;
    }

    /**
     * @return The condition of the ChargingEvent.
     */
    public String getCondition() {
        return condition;
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
        if ((chargingTime - diff >= 0) && (condition.equals("charging") || condition.equals("swapping")))
            this.remainingChargingTime = chargingTime - diff;
        else
            return 0;
        return remainingChargingTime;
    }

    /**
     * Sets the condition of the ChargingEvent.
     * @param condition The condition to be set.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return The maximum time the vehicle should wait.
     */
    public long getMaxWaitingTime()
    {
        return maxWaitingTime;
    }

    /**
     * @return The charging time of the ChargingEvent.
     */
    public long getChargingTime() {
        return chargingTime;
    }

    /**
     * Sets the charging time of the ChargingEvent. It also starts counting the reamining time of the charging.
     *
     * @param time The charging time.
     */
    public void setChargingTime(long time) {
        timestamp = System.currentTimeMillis();
        this.chargingTime = time;
    }

    /**
     * Calculates the amount of time a Driver has to wait until his ElectricVehicle
     * will be charged. This calculation happens in case an ElectricVehicle should
     * be added in the WaitingList.
     * @return The waiting time.
     */
    private long calWaitingTime()
    {
        if (station.getChargers().length == 0)
            return -1;
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
     * @return The id of the ChargingEvent.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for the ChargingEvent.
     *
     * @param id The id to be set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The cost of the ChargingEvent.
     */
    public double getCost()
    {
        return this.cost;
    }

    /**
     * Sets the cost for the ChargingEvent.
     * @param cost The cost to be set.
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }
}