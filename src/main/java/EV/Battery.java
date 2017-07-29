package EV;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class Battery
{
    private int id;
    private float remAmount;
    private float batteryCapacity;
    private int numberOfChargings;
    private int maxNumberOfChargings;
    private boolean active;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public Battery(float remAmount, float batteryCapacity)
    {
        this.id = idGenerator.getAndIncrement();
        this.remAmount = remAmount;
        this.batteryCapacity = batteryCapacity;
        this.numberOfChargings = 0;
        this.maxNumberOfChargings = 100;
        active = true;
    }

    public Battery()
    {
        this.id = idGenerator.getAndIncrement();
    }

    /**
     * Sets the remaining amount of energy in the Battery.
     * @param r Remaining amount.
     */
    public void setRemAmount(float r)
    {
        remAmount = r;
    }

    /**
     *
     * @return If it is active(available) or not.
     */
    public boolean reActive()
    {
        return active;
    }
    /**
     * Sets a Battery capacity.
     * @param u Battery capacity to be inserted.
     */
    public void setBatteryCapacity(float u)
    {
        batteryCapacity = u;
    }

    /**
     * @return Remaining Amount of battery.
     */
    public float reRemAmount()
    {
        return remAmount;
    }

    /**
     * @return Battery capacity of the Battery.
     */
    public float reBatteryCapacity()
    {
        return batteryCapacity;
    }

    /**
     * @return The number a battery can be charged
     */
    public int reNumberOfChargings()
    {
        return numberOfChargings;
    }

    /**
     * Increases the number of chargings of a battery by one
     */
    public void addCharging()
    {
        if (numberOfChargings < maxNumberOfChargings)
            ++numberOfChargings;
        else if(numberOfChargings == maxNumberOfChargings)
            active = false;
    }

    /**
     *
     * @return The maximum number of times a battery can be fully charged
     */
    public int reMaxNumberOfCharging()
    {
        return maxNumberOfChargings;
    }

    /**
     * Sets a number of full chargings a battery can have
     * @param maxNumberOfChargings The number of chargings
     */
    public void setMaxNumberOfChargings(int maxNumberOfChargings)
    {
        if (numberOfChargings < maxNumberOfChargings)
            this.maxNumberOfChargings = maxNumberOfChargings;
        else
            this.maxNumberOfChargings = numberOfChargings;
    }
}