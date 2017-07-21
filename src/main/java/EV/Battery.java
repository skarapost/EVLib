package EV;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class Battery
{
    private int id;
    private float remAmount;
    private float batteryCapacity;

    public Battery(int id, float remAmount, float batteryCapacity)
    {
        this.id = id;
        this.remAmount = remAmount;
        this.batteryCapacity = batteryCapacity;
    }

    public Battery(int id)
    {
        this.id = id;
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
     * Sets a Battery capacity.
     * @param u Battery capacity to be inserted.
     */
    public void setBatteryCapacity(float u)
    {
        batteryCapacity = u;
    }

    /**
     * Returns the remaining amount of Battery.
     * @return Remaining Amount of battery.
     */
    public float reRemAmount()
    {
        return remAmount;
    }

    /**
     * Returns Battery capacity.
     * @return Battery capacity of the Battery.
     */
    public float reBatteryCapacity()
    {
        return batteryCapacity;
    }
}