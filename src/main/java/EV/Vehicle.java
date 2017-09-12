package EV;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class Vehicle 
{
    private int id;
    private String brand;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * Constructor of Vehicle class.
     * @param brand The brand of the Vehicle.
     */

    public Vehicle(String brand)
    {
        this.id = idGenerator.incrementAndGet();
        this.brand = brand;
    }

    /**
     * @return The brand of the Vehicle
     */
    public String getBrand()
    {
        return brand;
    }

    /**
     * @return The id of this Vehicle.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the brand for a Vehicle.
     * @param brand The brand of the Vehicle.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }
}