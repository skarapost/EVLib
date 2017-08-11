package Events;

import Station.DisCharger;
import EV.ElectricVehicle;
import Station.ChargingStation;
import Station.WaitList;

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
    private long timestamp;

    public DisChargingEvent(ChargingStation station, ElectricVehicle vehicle, double amEnerg)
    {
        this.amEnerg = amEnerg;
        this.station = station;
        this.vehicle = vehicle;
        this.disChargerId = -1;
        this.condition = "arrived";
        this.dateArrival = station.getTime();
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
     * @return The ElectricVehicle of the ChargingEvent.
     */
    public ElectricVehicle reElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Executes the pre-processing phase. Checks for any DisCharger
     * and calculates the discharging time. If there is not any empty DisCharger
     * the DisChargingEvent object is inserted in the WaitingList.
     */
    public void preProcessing()
    {
        if (reElectricVehicle().reBattery().reActive()) {
            if ((condition.equals("arrived")) || (condition.equals("wait"))) {
                int qwe = station.checkDisChargers();
                if ((qwe != -1) && (qwe != -2)) {
                    station.setTotalEnergy(-amEnerg);
                    setCondition("discharging");
                    disChargingTime = (int) (amEnerg / station.reDisChargingRatio());
                    disChargerId = qwe;
                    DisCharger dsc = station.searchDischarger(disChargerId);
                    dsc.setDisChargingEvent(this);
                    dsc.changeSituation();
                } else if (qwe == -2)
                    setCondition("nonExecutable");
                else {
                    long time = calDisWaitingTime();
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
     * It starts the execution of the DisChargingEvent.
     * If the DisChargingEvent is in the WaitingList it does not do anything.
     */
    public void execution()
    {
        if(condition == "discharging")
        {
            station.checkForUpdate();
            dsc.setCommitTime(disChargingTime);
            dsc.executeDisChargingEvent();
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
     * @return The ChargingStation object.
     */
    public ChargingStation reStation()
    {
        return station;
    }

    /**
     * @return The amount of energy to be given.
     */
    public double reEnergyAmount()
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
    public long reElapsedDisChargingTime()
    {
        long diff = station.getTime() - timestamp;
        if (disChargingTime - diff >= 0)
            return disChargingTime - diff;
        else
            return 0;
    }

    /**
     * Sets the time of the discharging.
     * @param disChargingTime The time of discharging.
     */
    public void setDisChargingTime(long disChargingTime){
        timestamp = station.getTime();
        this.disChargingTime = disChargingTime;
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

    /**
     * @return The discharging time of the DisChargingEvent
     */
    public long reDisChargingTime()
    {
        return disChargingTime;
    }

    /**
     * @return The time the ElectricVehicle has to wait.
     */
    private long calDisWaitingTime()
    {
        long[] counter1 = new long[station.reDisChargers().length];
        long min = 1000000000;
        int index = 1000000000;
        for (int i = 0; i<station.reDisChargers().length; i++)
        {
            long diff = station.reDisChargers()[i].reDisChargingEvent().reElapsedDisChargingTime();
            if (min > diff)if (min > diff) {
                min = diff;
                index = i;
            }
            counter1[i] = diff;
        }
        WaitList o = station.reDischarging();
        for(int i = 0; i < o.rSize() ;i++)
        {
            counter1[index] = counter1[index] + o.get(i).reDisChargingTime();
            for(int j=0; j<station.reDisChargers().length; j++)
                if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                    index = j;
        }
        return counter1[index];
    }
}