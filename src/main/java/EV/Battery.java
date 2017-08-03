package EV;

import java.util.concurrent.atomic.AtomicInteger;

public class Battery
{
    private int id;
    private double remAmount;
    private double batteryCapacity;
    private int numberOfChargings;
    private int maxNumberOfChargings;
    private boolean active;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public Battery(double remAmount, double batteryCapacity)
    {
        this.id = idGenerator.getAndIncrement();
        this.remAmount = remAmount;
        this.batteryCapacity = batteryCapacity;
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
    public void setRemAmount(double r)
    {
        remAmount = r;
    }

    /**
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
    public void setBatteryCapacity(double u)
    {
        batteryCapacity = u;
    }

    /**
     * @return Remaining Amount of battery.
     */
    public double reRemAmount()
    {
        return remAmount;
    }

    /**
     * @return Battery capacity of the Battery.
     */
    public double reBatteryCapacity()
    {
        return batteryCapacity;
    }

    /**
     * @return The number of chargings a battery can afford.
     */
    public int reNumberOfChargings()
    {
        return numberOfChargings;
    }

    /**
     * Increases the number of chargings of a battery by one.
     */
    public void addCharging()
    {
        if (numberOfChargings < maxNumberOfChargings)
            ++numberOfChargings;
        else if(numberOfChargings == maxNumberOfChargings)
            active = false;
    }

    /**
     * @return The maximum number of times a battery can be fully charged.
     */
    public int reMaxNumberOfCharging()
    {
        return maxNumberOfChargings;
    }

    /**
     * Sets a number of full chargings a battery can have.
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