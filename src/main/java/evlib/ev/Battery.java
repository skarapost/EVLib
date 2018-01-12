package evlib.ev;

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

    /**
     * Creates a new Battery object.
     * @param remAmoun The remaining amount of energy in the Battery.
     * @param capacit The capacity of the Battery.
     */
    public Battery(final double remAmoun, final double capacit) {
        this.id = idGenerator.incrementAndGet();
        this.remAmount = remAmoun;
        this.capacity = capacit;
        this.maxNumberOfChargings = 100;
        this.active = true;
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
     * @param activ The value for the condition of the battery.
     */
    public void setActive(final boolean activ)
    {
        this.active = activ;
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
    public void setRemAmount(final double r) {
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
     * @param capacit The capacity to be inserted.
     */
    public void setCapacity(final double capacit) {
        this.capacity = capacit;
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
     * @param numberOfChar The number of chargings.
     */
    public void setNumberOfChargings(final int numberOfChar) {
        if (numberOfChar < this.maxNumberOfChargings)
            this.numberOfChargings = numberOfChar;
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
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }

    /**
     * Increases the number of chargings of a Battery by one.
     */
    public void addCharging() {
        if (numberOfChargings < maxNumberOfChargings)
            ++numberOfChargings;
        if (numberOfChargings == maxNumberOfChargings)
            this.active = false;
    }

    /**
     * Sets a number of full chargings a battery can have.
     * @param maxNumberOfChar The number of chargings
     */
    public void setMaxNumberOfChargings(final int maxNumberOfChar) {
        if (numberOfChargings < maxNumberOfChar)
            this.maxNumberOfChargings = maxNumberOfChar;
        else {
            this.maxNumberOfChargings = maxNumberOfChar;
            this.numberOfChargings = this.maxNumberOfChargings;
            this.active = false;
        }
    }
}
