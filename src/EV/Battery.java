package EV;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class Battery {
    private int id;
    private float remAmount;
    private float batteryCapacity;

    /**
     * Constructor of Battery class.
     * @param id Id of the Battery.
     * @param remAmount Remaining amount of the Battery.
     * @param batteryCapacity Battery capacity. 
     */
    public Battery(int id, float remAmount, float batteryCapacity)
    {
        this.id = id;
        this.remAmount = remAmount;
        this.batteryCapacity = batteryCapacity;
    }

    /**
     * Constructor of Battery class.
     * @param id Id of the Battery.
     */
    public Battery(int id)
    {
        this.id = id;
    }

    /**
     * Sets the remaining amount of energy in the Battery.
     * @param r Remaining amount.
     */
    public final void setRemAmount(float r)
    {
        remAmount = r;
    }

    /**
     * Sets a Battery capacity.
     * @param u Battery capacity to be inserted.
     */
    public final void setBatteryCapacity(float u)
    {
        batteryCapacity = u;
    }

    /**
     * Returns the remaining amount of Battery.
     * @return Remaining Amount of battery.
     */
    public final float reRemAmount()
    {
        return remAmount;
    }

    /**
     * Returns Battery capacity.
     * @return Battery capacity of the Battery.
     */
    public final float reBatteryCapacity()
    {
        return batteryCapacity;
    }
}