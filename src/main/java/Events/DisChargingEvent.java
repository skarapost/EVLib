package Events;


import EV.ElectricVehicle;
import Station.ChargingStation;

public class DisChargingEvent
{
    private ElectricVehicle vehicle;
    private ChargingStation station;
    private double amEnerg;
    private long disChargingTime;
    private String condition;
    private int disChargerId;
    private long dateArrival;
    private long waitingTime;
    private long maxWaitingTime;
    private long startTime;

    public DisChargingEvent(ChargingStation station, ElectricVehicle vehicle, double amEnerg)
    {
        this.amEnerg = amEnerg;
        this.station = station;
        this.vehicle = vehicle;
        this.disChargerId = -1;
        this.condition = "arrived";
        this.dateArrival = station.getTime();
        this.startTime = 0;
    }

    /**
     * Sets the time the ElectricVehicle can wait, in case it is inserted in the queue.
     * @param time The time to wait.
     */
    public void setWaitingTime(long time)
    {
        this.waitingTime = time;
    }

    /**
     * @return The time the ElectricVehicle can wait.
     */
    public long reWaitingTime()
    {
        return waitingTime;
    }

    /**
     * @return The time arrival.
     */
    public long reDateArrival()
    {
        return dateArrival;
    }

    public void setStartTime(long time)
    {
        this.startTime = time;
    }

    public long reStartTime()
    {
        return startTime;
    }

    /**
     * @return The ElectricVehicle of the ChargingEvent.
     */
    public ElectricVehicle reElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Executes the dis-charging phase. Checks for any DisCharger
     * and calculates the discharging time. If there is not any empty DisCharger
     * the DisChargingEvent object is inserted in the waiting list. In the end calls
     * the executeDisChargingEvent() function of the assigned DisCharger object
     * to implement the discharging.
     */
    public void execution()
    {
        if (reElectricVehicle().reBattery().reActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                int qwe = station.checkDisChargers();
                if ((qwe != -1) && (qwe != -2)) {
                    station.setTotalEnergy(-amEnerg);
                    setCondition("discharging");
                    disChargingTime = (int) (amEnerg / station.reDisChargingRatio());
                    disChargerId = qwe;
                    setStartTime (station.getTime());
                    station.searchDischarger(disChargerId).setDisChargingEvent(this);
                    station.searchDischarger(disChargerId).changeSituation();
                    station.checkForUpdate();
                    station.searchDischarger(disChargerId).setBusyTime(disChargingTime);
                    station.searchDischarger(disChargerId).executeDisChargingEvent();
                } else if (qwe == -2)
                    setCondition("nonExecutable");
                else {
                    long time = station.calDisWaitingTime();
                    maxWaitingTime = time;
                    if (time < waitingTime) {
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
     * Sets a value to the condition of the DisChargingEvent.
     * @param condition The value of the condition.
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    /**
     * @return The ChargingStation object.
     */
    public ChargingStation reStation()
    {
        return station;
    }

    /**
     * @return The amount of energy to be given.
     */
    public double reAmEnerg()
    {
        return amEnerg;
    }

    /**
     * @return The condition of the DisChargingEvent.
     */
    public String reCondition()
    {
        return condition;
    }

    /**
     * @return The discharging time.
     */
    public long reDisChargingTime()
    {
        return disChargingTime;
    }

    /**
     * Sets the id of DisCharger that is going to be discharged.
     * @param id The id of the DisCharger in which the DisChargingEvent is attached.
     */
    public void setDisChargerId(int id)
    {
        disChargerId = id;
    }

    /**
     * @return The waiting time.
     */
    public long reMaxWaitingTime()
    {
        return maxWaitingTime;
    }

    /**
     * Sets the time the ElectricVehicle has to wait in the list, until it can
     * be discharged.
     * @param time The waiting time.
     */
    public void setMaxWaitingTime(long time)
    {
        this.maxWaitingTime = time;
    }
}