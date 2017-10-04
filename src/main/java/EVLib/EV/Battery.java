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
     * @return True if the Battery is active(available), or false for not available.
     */
    public boolean getActive()
    {
        return active;
    }

    /**
     * Sets if the Battery's status.
     * @param active The value for the condition of the battery.
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * @return The battery's remaining amount of energy.
     */
    public double getRemAmount()
    {
        return remAmount;
    }

    /**
     * Sets the remaining amount of energy in the Battery.
     * @param r The remaining amount of energy.
     */
    public void setRemAmount(double r) {
        remAmount = r;
    }

    /**
     * @return The capacity of the Battery.
     */
    public double getCapacity()
    {
        return capacity;
    }

    /**
     * Sets Battery's capacity.
     * @param capacity The capacity to be inserted.
     */
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    /**
     * @return The number of chargings a Battery can have.
     */
    public int getNumberOfChargings()
    {
        return numberOfChargings;
    }

    /**
     * @return The maximum number of times a battery can be fully charged.
     */
    public int getMaxNumberOfChargings()
    {
        return maxNumberOfChargings;
    }

    /**
     * Sets the number a battery has been charged until now. If the number is greater than the maximum number
     * of chargings then the number of chargings is set to be equal to the maximum.
     * @param numberOfChargings The number of chargings.
     */
    public void setNumberOfChargings(int numberOfChargings)
    {
        if (numberOfChargings < this.maxNumberOfChargings)
            this.numberOfChargings = numberOfChargings;
        else {
            this.numberOfChargings = this.maxNumberOfChargings;
            this.active = false;
        }
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
     * Increases the number of chargings of a Battery by one.
     */
    public void addCharging()
    {
        if (numberOfChargings < maxNumberOfChargings)
            ++numberOfChargings;
        if (numberOfChargings == maxNumberOfChargings)
            active = false;
    }

    /**
     * Sets a number of full chargings a battery can have.
     * @param maxNumberOfChargings The number of chargings
     */
    public void setMaxNumberOfChargings(int maxNumberOfChargings)
    {
        if (numberOfChargings < maxNumberOfChargings)
            this.maxNumberOfChargings = maxNumberOfChargings;
        else {
            this.maxNumberOfChargings = maxNumberOfChargings;
            this.numberOfChargings = maxNumberOfChargings;
            this.active = false;
        }
    }
}