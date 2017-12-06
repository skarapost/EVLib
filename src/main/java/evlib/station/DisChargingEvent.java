package evlib.station;

import evlib.ev.ElectricVehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DisChargingEvent
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private ElectricVehicle vehicle;
    private final ChargingStation station;
    private final String chargingStationName;
    DisCharger disCharger;
    private double amountOfEnergy;
    private long disChargingTime;
    private long remainingDisChargingTime;
    private String condition;
    private long waitingTime;
    private long maxWaitingTime;
    private long timestamp;
    private double profit;
    public static final List<DisChargingEvent> dischargingLog = new ArrayList<>();

    /**
     * Creates a new DisChargingEvent object. It assigns the value of "arrived" to the condition of the event.
     * @param station The ChargingStation object the event visited.
     * @param vehicle The ElectricVehicle of the event.
     * @param amEnerg The amount of energy the events asks. 
     */
    public DisChargingEvent(ChargingStation station, ElectricVehicle vehicle, double amEnerg)
    {
        this.id = idGenerator.incrementAndGet();
        this.amountOfEnergy = amEnerg;
        this.station = station;
        this.vehicle = vehicle;
        this.condition = "arrived";
        this.chargingStationName = station.getName();
        this.dischargingLog.add(this);
    }

    /**
     * Sets the time the ElectricVehicle can wait, in case of it is inserted in the queue.
     * @param time The time to wait.
     */
    public void setWaitingTime(long time)
    {
        this.waitingTime = time;
    }

    /**
     * @return The time the ElectricVehicle can wait.
     */
    public long getWaitingTime()
    {
        return waitingTime;
    }

    /**
     * @return The ElectricVehicle of the ChargingEvent.
     */
    public ElectricVehicle getElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Sets the vehicle of the DisChargingEvent.
     * @param vehicle The vehicle to be set.
     */
    public void setElectricVehicle(ElectricVehicle vehicle) { this.vehicle = vehicle; }

    /**
     * Executes the pre-processing phase. Checks for any DisCharger
     * and calculates the discharging time. If there is not any empty DisCharger
     * the DisChargingEvent object is inserted in the WaitingList.
     */
    public synchronized void preProcessing()
    {
        if (getElectricVehicle().getBattery().getActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                station.assignDisCharger(this);
                if (disCharger != null) {
                    disChargingTime = (long) (amountOfEnergy / station.getDisChargingRatio());
                    setCondition("ready");
                    profit = amountOfEnergy * station.getDisUnitPrice();
                }
                else
                    if(!condition.equals("wait")) {
                        maxWaitingTime = calDisWaitingTime();
                        if (maxWaitingTime < waitingTime && maxWaitingTime > -1) {
                            if (!condition.equals("wait"))
                                station.updateDisChargingQueue(this);
                            setCondition("wait");
                        } else
                            setCondition("nonExecutable");
                    }
            }
        }
        else
            setCondition("nonExecutable");
    }

    /**
     * It starts the execution of the DisChargingEvent.
     * If the DisChargingEvent is in the WaitingList it does not do anything.
     */
    public synchronized void execution()
    {
        if(condition.equals("ready")) {
            setCondition("discharging");
            disCharger.startDisCharger();
        }
    }

    /**
     * Sets the condition of the DisChargingEvent.
     * @param condition The condition to be set.
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    /**
     * @return The ChargingStation the event to be executed.
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
     * @return The amount of energy to be given.
     */
    public double getAmountOfEnergy()
    {
        return amountOfEnergy;
    }

    /**
     * Sets the amount of energy the DisChargingEvent will give.
     * @param energy The amount of energy to be set.
     */
    public void setAmountOfEnergy(double energy) { this.amountOfEnergy = energy; }

    /**
     * @return The condition of the DisChargingEvent.
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * @return The remaining discharging time.
     */
    public long getRemainingDisChargingTime()
    {
        long diff = System.currentTimeMillis() - timestamp;
        if ((disChargingTime - diff >= 0) && (condition.equals("discharging")))
            this.remainingDisChargingTime = disChargingTime - diff;
        else
            return 0;
        return remainingDisChargingTime;
    }

    /**
     * Sets the time of the discharging.
     * @param disChargingTime The time of discharging.
     */
    public void setDisChargingTime(long disChargingTime){
        timestamp = System.currentTimeMillis();
        this.disChargingTime = disChargingTime;
    }

    /**
     * @return The time the ElectricVehicle should wait in the waiting list.
     */
    public long getMaxWaitingTime() { return maxWaitingTime; }

    /**
     * Sets the maximum time the DisChargingEvent should wait to be discharged.
     * @param maxWaitingTime The waiting time to be set.
     */
    public void setMaxWaitingTime(long maxWaitingTime) { this.maxWaitingTime = maxWaitingTime; }

    /**
     * @return The discharging time of the DisChargingEvent.
     */
    public long getDisChargingTime()
    {
        return disChargingTime;
    }

    /**
     * @return The time the ElectricVehicle should wait or -1 if the ChargingStation
     * has no available DisCharger.
     */
    private long calDisWaitingTime()
    {
        if (station.getDisChargers().length == 0)
            return -1;
        long[] counter1 = new long[station.getDisChargers().length];
        long min = 1000000000;
        int index = 1000000000;
        for (int i = 0; i<station.getDisChargers().length; i++)
        {
            long diff = station.getDisChargers()[i].getDisChargingEvent().getRemainingDisChargingTime();
            if (min > diff)if (min > diff) {
                min = diff;
                index = i;
            }
            counter1[i] = diff;
        }
        WaitList o = station.getDischarging();
        DisChargingEvent e;
        for(int i = 0; i < o.getSize() ;i++)
        {
            e = (DisChargingEvent) o.get(i);
            counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/station.getDisChargingRatio()));
            for(int j=0; j<station.getDisChargers().length; j++)
                if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                    index = j;
        }
        return counter1[index];
    }

    /**
     * @return The id of the DisChargingEvent.
     */
    public int getId()
    {
       return id;
    }

    /**
     * Sets the id for the DisChargingEvent.
     * @param id The id to be set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The profit of the DisChargingEvent.
     */
    public double getProfit()
    {
        return profit;
    }

    /**
     * Sets the profit for the DisChargingEvent.
     * @param profit The profit to be set.
     */
    public void setProfit(double profit) { this.profit = profit; }
}
