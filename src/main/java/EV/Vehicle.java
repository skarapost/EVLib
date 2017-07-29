package EV;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class Vehicle 
{
    private int id;
    private String brand;
    private int cubism;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * Constructor of Vehicle class.
     * @param brand The brand of the Vehicle.
     * @param cubism The cubism of the Vehicle.
     */

    public Vehicle(String brand,int cubism)
    {
        this.id = idGenerator.getAndIncrement();
        this.brand = brand;
        this.cubism = cubism;
    }
}