package EV;


import java.util.concurrent.atomic.AtomicInteger;

public class ElectricVehicle extends Vehicle
{
    private Battery battery;
    private Driver driver;

    public ElectricVehicle(String brand, int cubism)
    {
        super(brand, cubism);
        battery = null;
        driver = null;
    }

    /**
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
     */
    public void setDriver(Driver p)
    {
        this.driver = p;
    }

    /**
    * @return The Driver of the ElectricVehicle.
    */
    public Driver reDriver()
    {
        return driver;
    }

}