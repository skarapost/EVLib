package evlib.ev;

import java.util.concurrent.atomic.AtomicInteger;

public class ElectricVehicle
{
    private Battery battery;
    private Driver driver;
    private String brand;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private int id;

    /**
     * Constructs a new ElectricVehicle object, asking only for the brand.
     * @param brand The brand of the ElectricVehicle.
     */
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
     * @param battery The Battery to be set.
    */
    public void setBattery(Battery battery)
    {
        this.battery = battery;
    }

    /**
    * Links the ElectricVehicle with a Driver.
     * @param driver The Driver to be linked with.
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
     * Sets the brand for the ElectricVehicle.
     * @param brand The brand of the ElectricVehicle.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return The id of the Driver.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for the ElectricVehicle.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }

}
