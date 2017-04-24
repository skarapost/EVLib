package Events;


import Station.ChargingStation;
import EV.ElectricVehicle;
import java.util.Date;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class DisChargingEvent 
{
    private ElectricVehicle vehicle;
    private ChargingStation station;
    private float amEnerg;
    private int disChargingTime;
    private int mode;
    private int disChargerId;
    private Date date;
    private int dateArrival;
    private float waitingTime;
    private int startTime;
    private int maxWaitingTime;
    
    /**
     * Constructor of DisChargingEvent class.
     * @param station The ChargingStation the ElectricVehicle visits. 
     * @param vehicle The ElectricVehicle that needs discharging.
     * @param amEnerg The energy the ElectricVehicle wants to give.
     */
    public DisChargingEvent(ChargingStation station, ElectricVehicle vehicle, float amEnerg)
    {
        this.amEnerg = amEnerg;
        this.station = station;
        this.vehicle = vehicle;
        disChargerId = -1;
        mode = 1;
        
        date = new Date();
        dateArrival = (int) (date.getTime()/1000);
    }

    /**
     * Sets the time the ElectricVehicle can wait, in case it is inserted in the queue.
     * @param time The time to wait.
     */
    public void setWaitingTime(float time)
    {
        waitingTime = time;
    }

    /**
     * Returns the time the ElectricVehicle can wait.
     * @return The time the ElectricVehicle can wait.
     */
    public final float reWaitingTime()
    {
        return waitingTime;
    }

    /**
     * Returns the time the ElectricVehicle arrives to the ChargingStaion.
     * @return The time arrival.
     */
    public final int reDateArrival()
    {
        return dateArrival;
    }

    /**
     * Returns the ElectricVehicle of the ChargingEvent.
     * @return The ElectricVehicle of the ChargingEvent.
     */
    public final ElectricVehicle reElectricVehicle()
    {
        return vehicle;
    }

    /**
     * Executes the pre-proccessing method. Checks for any DisCharger 
     * and calculates the discharging time. If there is not any empty DisCharger
     * the DisChargingEvent object is inserted in the list.
     */
    public final void preProcessing()
    {
        if ((mode == 1)||(mode == 3))
        {
            int qwe = station.checkDisChargers();
            if ((qwe != -1)&&(qwe != -753159))
            {
                setMode(2);
                disChargerId = qwe;
                station.searchDischarger(disChargerId).setDisChargingEvent(this);
                station.searchDischarger(disChargerId).changeSituation();
                station.setTotalEnergy(-amEnerg);
                disChargingTime = (int) (amEnerg/station.reDisChargingRatio()) + 1;
            }
            else if (qwe == -753159)
            {
                setMode(5);
            }
            else
            {
                int time = station.reChargingStationHandler().calDisWaitingTime();
                maxWaitingTime = time;
                if (time < waitingTime)
                {
                    if (mode != 3)
                        station.updateDisChargingQueue(this);
                    setMode(3);
                }
                else
                    setMode(5);
            }
        }
    }

    /**
     * It starts the execution of the DisChargingEvent object. The function modifies 
     * the dischargingarray. Checks if there needs to become any update storage. Starts the 
     * discharging of the ElectricVehicle.
     * If the DisChargingEvent object is in the list
     * then this method does not do anything.
     */
    public final void execution()
    {
        if (mode == 2)
        {
            Date now = new Date();
            int now1 = (int) (now.getTime()/1000);
            long diff = (now1-station.reCreationDate())/station.reTimeMoment();
            long l;
            float x1;
            x1 = ((float)(now1 - station.reCreationDate())/(float) station.reTimeMoment()) + (float) disChargingTime/(float) station.reTimeMoment();
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
                l = diff + disChargingTime/station.reTimeMoment();
            for(long i=diff;i<l;i++)
            {
                station.reChargingStationHandler().modifyDisChargingArray(station.searchDischargerPlace(disChargerId),(int) i);
            }
            if (station.reUpdateMode() == 0)
                station.reChargingStationHandler().checkUpdatePredefinedSpace();
            else
                station.reChargingStationHandler().checkUpdateMadeSpace();
            startTime = now1;
            station.searchDischarger(disChargerId).setBusyTime(disChargingTime);
            station.searchDischarger(disChargerId).executeDisChargingEvent(this);
        }
    }

    /**
     * Returns the start time of the DisChargingEvent.
     * @return The start time.
     */
    public final int reStartTime()
    {
        return startTime;
    }

    /**
     * Sets a value to the mode of the DisChargingEvent.
     * @param h The value of the mode.
     */
    public final void setMode(int h)
    {
        mode = h;
    }

    /**
     * Returns the ChargingStation the DisChargingEvent visited.
     * @return The ChargingStation object.
     */
    public final ChargingStation reStation()
    {
        return station;
    }

    /**
     * Returns the amount of energy the ElectricVehicle gives.
     * @return The amount of energy to be given.
     */
    public final float reAmEnerg()
    {
        return amEnerg;
    }
    
    /**
     * Returns the mode of the DisChargingEvent.
     * @return The mode of the DisChargingEvent.
     */
    public final int reMode()
    {
        return mode;
    }

    /**
     * Returns the discharging time.
     * @return The discharging time.
     */
    public final int reDisChargingTime()
    {
        return disChargingTime;
    }

    /**
     * Sets the id of DisCharger that is going to be discharged.
     * @param id The id of the DisCharger in which the DisChargingEvent is attached.
     */
    public final void setDisChargerId(int id)
    {
        disChargerId = id;
    }
    
    /**
     * Returns the amount of time the ElectricVehicle has to wait.
     * @return The waiting time.
     */
    public final int reMaxWaitingTime()
    {
        return maxWaitingTime;
    }
    
    /**
     * Sets the time the ElectricVehicle has to wait in the list, until it can 
     * be discharged.
     * @param time The waiting time.
     */
    public final void setMaxWaitingTime(int time)
    {
        maxWaitingTime = time;
    }
}