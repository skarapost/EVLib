package Events;


import EV.ElectricVehicle;
import Station.ChargingStation;

/**
 *
 * @author Sotiris Karapostolakis
 */

public class DisChargingEvent
{
    private ElectricVehicle vehicle;
    private ChargingStation station;
    private float amEnerg;
    private long disChargingTime;
    private String condition;
    private int disChargerId;
    private long dateArrival;
    private long waitingTime;
    private long maxWaitingTime;
    private long startTime;

    public DisChargingEvent(ChargingStation station, ElectricVehicle vehicle, float amEnerg)
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
     * Returns the time the ElectricVehicle can wait.
     * @return The time the ElectricVehicle can wait.
     */
    public long reWaitingTime()
    {
        return waitingTime;
    }

    /**
     * Returns the time the ElectricVehicle arrives to the ChargingStaion.
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
     * Returns the ElectricVehicle of the ChargingEvent.
     * @return The ElectricVehicle of the ChargingEvent.
     */
    public ElectricVehicle reElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Executes the pre-proccessing method. Checks for any DisCharger
     * and calculates the discharging time. If there is not any empty DisCharger
     * the DisChargingEvent object is inserted in the waiting list.
     */
    public void preProcessing()
    {
        if ((condition.equals("arrived"))||(condition.equals("wait")))
        {
            int qwe = station.checkDisChargers();
            if ((qwe != -1)&&(qwe != -753159))
            {
                setCondition("ready");
                disChargerId = qwe;
                station.searchDischarger(disChargerId).setDisChargingEvent(this);
                station.searchDischarger(disChargerId).changeSituation();
                station.setTotalEnergy(-amEnerg);
                disChargingTime = (int) (amEnerg/station.reDisChargingRatio());
            }
            else if (qwe == -753159)
                setCondition("nonExecutable");
            else
            {
                long time = station.calDisWaitingTime();
                maxWaitingTime = time;
                if (time < waitingTime)
                {
                    if (!condition.equals("wait"))
                        station.updateDisChargingQueue(this);
                    setCondition("wait");
                }
                else
                    setCondition("nonExecutable");
            }
        }
    }

    /**
     * It starts the execution of the DisChargingEvent object. The function modifies
     * the dischargingarray. Checks if there needs to become any update storage. Starts the
     * discharging of the ElectricVehicle. If the DisChargingEvent object is in the waiting
     * list then this method does not do anything.
     */
    public void execution()
    {
        if (condition.equals("ready"))
        {
            station.checkForUpdate();
            station.searchDischarger(disChargerId).setBusyTime(disChargingTime);
            setStartTime (station.getTime());
            station.searchDischarger(disChargerId).executeDisChargingEvent();
        }
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
     * Returns the ChargingStation the DisChargingEvent visited.
     * @return The ChargingStation object.
     */
    public ChargingStation reStation()
    {
        return station;
    }

    /**
     * Returns the amount of energy the ElectricVehicle gives.
     * @return The amount of energy to be given.
     */
    public float reAmEnerg()
    {
        return amEnerg;
    }

    /**
     * Returns the condition of the DisChargingEvent.
     * @return The condition of the DisChargingEvent.
     */
    public String reCondition()
    {
        return condition;
    }

    /**
     * Returns the discharging time.
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
     * Returns the amount of time the ElectricVehicle has to wait.
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