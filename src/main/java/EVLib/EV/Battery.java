package EVLib.EV;

import java.util.concurrent.atomic.AtomicInteger;

public class Battery
{
    private int id;
    private double remAmount;
    private double capacity;
    private int numberOfChargings;
    private int maxNumberOfChargings;
    private boolean active;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public Battery(double remAmount, double capacity)
    {
        this.id = idGenerator.incrementAndGet();
        this.remAmount = remAmount;
        this.capacity = capacity;
        this.maxNumberOfChargings = 100;
        active = true;
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
    public boolean getActive()
    {
        return active;
    }

    /**
     * Sets a Battery capacity.
     * @param capacity Battery capacity to be inserted.
     */
    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    /**
     * @return Remaining Amount of battery.
     */
    public double getRemAmount()
    {
        return remAmount;
    }

    /**
     * @return Battery capacity of the Battery.
     */
    public double getCapacity()
    {
        return capacity;
    }

    /**
     * @return The number of chargings a battery can afford.
     */
    public int getNumberOfChargings()
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
    public int getMaxNumberOfChargings()
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

    /**
     * @return The id of this Battery.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for this Battery.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets either the battery is active or not.
     * @param active The value for the condition of the battery.
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * Sets the number a battery has been charged until now.
     * @param numberOfChargings The number of chargings.
     */
    public void setNumberOfChargings(int numberOfChargings)
    {
        this.numberOfChargings = numberOfChargings;
    }
}