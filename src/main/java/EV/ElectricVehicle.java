package EV;

/**
 *
 * @author Sotiris Karapostolakis
 */

public class ElectricVehicle extends Vehicle
{
    private Battery battery;
    private Driver driver;

    public ElectricVehicle(int id, String brand, int cubism)
    {
        super(id, brand, cubism);
        battery = null;
        driver = null;
    }

    /**
    * Returns the Battery of the ElectricVehicle.
    * @return The Battery of the ElectricVehicle.
    */
    public Battery reBattery()
    {
        return battery;
    }

    /**
    * Links the ElectricVehicle with a Battery.
    * @param k The Battery.
    */
    public void vehicleJoinBattery(Battery k)
    {
        battery = k;
    }

    /**
    * Removes the Battery from the ElectricVehicle.
    */
    public void vehicleDeleteBattery()
    {
        battery = null;
    }

    /**
    * Links the ElectricVehicle with a Driver.
    * @param p The Driver to be linked.
     * */
    public void setDriver(Driver p)
    {
        this.driver = p;
    }

    /**
    * Returns the Driver of the ElectricVehicle.
    * @return THe Driver.
    */
    public Driver reDriver()
    {
        return driver;
    }
}