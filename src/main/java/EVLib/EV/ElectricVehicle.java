package EVLib.EV;

import java.util.concurrent.atomic.AtomicInteger;

public class ElectricVehicle
{
    private Battery battery;
    private Driver driver;
    private String brand;
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private int id;

    public ElectricVehicle(String brand)
    {
        this.id = idGenerator.incrementAndGet();
        this.brand = brand;
        this.battery = null;
        this.driver = null;
    }

    /**
    * @return The Battery of the ElectricVehicle.
    */
    public Battery getBattery()
    {
        return this.battery;
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
        return this.driver;
    }

    /**
     * @return The brand of the ElectricVehicle
     */
    public String getBrand()
    {
        return this.brand;
    }

    /**
     * Sets the brand for a ElectricVehicle.
     * @param brand The brand of the ElectricVehicle.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return The id of this Driver.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for this ElectricVehicle.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

}