package evlib.station;

import evlib.ev.ElectricVehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DisChargingEvent {
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private ElectricVehicle vehicle;
    private final ChargingStation station;
    private DisCharger disCharger;
    private double amountOfEnergy;
    private long disChargingTime;
    private String condition;
    private long waitingTime;
    private long maxWaitingTime;
    private long timestamp;
    private double profit;
    private static final List<DisChargingEvent> dischargingLog = new ArrayList<>();

    /**
     * Creates a new DisChargingEvent object. It assigns the value of "arrived" to the condition of the event.
     * @param stat The ChargingStation object the event visited.
     * @param veh The ElectricVehicle of the event.
     * @param amEnerg The amount of energy the events asks. 
     */
    public DisChargingEvent(final ChargingStation stat, final ElectricVehicle veh, final double amEnerg) {
        this.id = idGenerator.incrementAndGet();
        this.amountOfEnergy = amEnerg;
        this.station = stat;
        this.vehicle = veh;
        this.condition = "arrived";
        dischargingLog.add(this);
        this.disCharger = null;
    }

    /**
     * Sets the time the ElectricVehicle can wait, in case of it is inserted in the queue.
     * @param time The time to wait.
     */
    public void setWaitingTime(final long time)
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
     * @param veh The vehicle to be set.
     */
    public void setElectricVehicle(final ElectricVehicle veh) { this.vehicle = veh; }

    /**
     * Executes the pre-processing phase. Checks for any DisCharger
     * and calculates the discharging time. If there is not any empty DisCharger
     * the DisChargingEvent object is inserted in the WaitingList.
     */
    public void preProcessing()
    {
        if (station.getDisChargers().length == 0) {
            setCondition("nonExecutable");
            return;
        }
        if (getElectricVehicle().getBattery().getActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                station.assignDisCharger(this);
                if (disCharger != null) {
                    disChargingTime = (long) (amountOfEnergy * 3600000 / station.getDisChargingRate());
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
    public void execution()
    {
        if (condition.equals("ready")) {
            setCondition("discharging");
            disCharger.startDisCharger();
        }
    }

    /**
     * Sets the condition of the DisChargingEvent.
     * @param cond The condition to be set.
     */
    public void setCondition(final String cond) {
        this.condition = cond;
    }

    /**
     * @return The ChargingStation the event to be executed.
     */
    public ChargingStation getStation()
    {
        return station;
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
    public void setAmountOfEnergy(final double energy) { this.amountOfEnergy = energy; }

    /**
     * @return The condition of the DisChargingEvent.
     */
    public String getCondition() { return condition; }

    /**
     * @return The remaining discharging time in milliseconds.
     */
    public long getRemainingDisChargingTime() {
        long diff = System.currentTimeMillis() - timestamp;
        long remainingDisChargingTime;
        if ((disChargingTime - diff >= 0) && (condition.equals("discharging")))
            remainingDisChargingTime = disChargingTime - diff;
        else
            return 0;
        return remainingDisChargingTime;
    }

    /**
     * Sets the time of the discharging in milliseconds.
     * @param disTime The time of discharging in milliseconds.
     */
    public void setDisChargingTime(final long disTime){
        timestamp = System.currentTimeMillis();
        this.disChargingTime = disTime;
    }

    /**
     * @return The time the ElectricVehicle should wait in the waiting list in milliseconds.
     */
    public long getMaxWaitingTime() { return maxWaitingTime; }

    /**
     * Sets the maximum time the DisChargingEvent should wait to be discharged in milliseconds.
     * @param maxTime The waiting time to be set in milliseconds.
     */
    public void setMaxWaitingTime(final long maxTime) { this.maxWaitingTime = maxTime; }

    /**
     * @return The discharging time of the DisChargingEvent in milliseconds.
     */
    public long getDisChargingTime() { return disChargingTime; }

    /**
     * @return The time the ElectricVehicle should wait or -1 if the ChargingStation
     * has no available DisCharger. The result is measured in milliseconds.
     */
    private long calDisWaitingTime() {
        if (station.getDisChargers().length == 0)
            return -1;
        long[] counter1 = new long[station.getDisChargers().length];
        long min = -1;
        int index = -1;
        for (int i = 0; i < station.getDisChargers().length; i++) {
            if (station.getDisChargers()[i].getDisChargingEvent() != null) {
                if (min == -1) {
                    min = station.getDisChargers()[i].getDisChargingEvent().getRemainingDisChargingTime();
                    index = i;
                }
                long diff = station.getDisChargers()[i].getDisChargingEvent().getRemainingDisChargingTime();
                if (min > diff) {
                    min = diff;
                    index = i;
                }
                counter1[i] = diff;
            }
            else
                return 0;
        }
        WaitList o = station.getDischarging();
        DisChargingEvent e;
        for (int i = 0; i < o.getSize(); i++) {
            e = (DisChargingEvent) o.get(i);
            counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy() * 3600000 / station.getDisChargingRate()));
            for (int j = 0; j < station.getDisChargers().length; j++)
                if ((counter1[j] < counter1[index]) && (counter1[j] != 0))
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
     * @param d The id to be set.
     */
    public void setId(final int d) {
        this.id = d;
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
     * @param prof The profit to be set.
     */
    public void setProfit(final double prof) { this.profit = prof; }

    /**
     * Returns the list with all created discharging events.
     * @return The list with all created discharging events.
     */
    public static List<DisChargingEvent> getDischargingLog() {
        return dischargingLog;
    }

    /**
     * Sets a discharger to the event.
     * @param dsch The discharger to be assigned.
     */
    void setDisCharger(DisCharger dsch) {
        this.disCharger = dsch;
    }
}
