package EVLib.EV;

public class ElectricVehicle extends Vehicle
{
    private Battery battery;
    private Driver driver;

    public ElectricVehicle(String brand)
    {
        super(brand);
        battery = null;
        driver = null;
    }

    /**
    * @return The Battery of the ElectricVehicle.
    */
    public Battery getBattery()
    {
        return battery;
    }

    /**
    * Links the ElectricVehicle with a Battery.
    * @param battery The Battery.
    */
    public void setBattery(Battery battery)
    {
        this.battery = battery;
    }

    /**
    * Links the ElectricVehicle with a Driver.
    * @param driver The Driver to be linked.
     */
    public void setDriver(Driver driver)
    {
        this.driver = driver;
    }

    /**
    * @return The Driver of the ElectricVehicle.
    */
    public Driver getDriver()
    {
        return driver;
    }

}