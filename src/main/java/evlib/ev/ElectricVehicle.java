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
     * @param bran The brand of the ElectricVehicle.
     */
    public ElectricVehicle(final String bran)
    {
        this.id = idGenerator.incrementAndGet();
        this.brand = bran;
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
     * @param batter The Battery to be set.
    */
    public void setBattery(final Battery batter)
    {
        this.battery = batter;
    }

    /**
    * Links the ElectricVehicle with a Driver.
     * @param driv The Driver to be linked with.
     */
    public void setDriver(final Driver driv)
    {
        this.driver = driv;
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
     * @param bran The brand of the ElectricVehicle.
     */
    public void setBrand(final String bran) {
        this.brand = bran;
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
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }

}
