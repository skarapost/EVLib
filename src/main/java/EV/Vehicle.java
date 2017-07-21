package EV;

/**
 *
 * @author Sotiris Karapostolakis
 */
public abstract class Vehicle 
{
    private int id;
    private String brand;
    private int cubism;

    /**
     * Constructor of Vehicle class.
     * @param id The id of the Vehicle.
     * @param brand The brand of the Vehicle.
     * @param cubism The cubism of the Vehicle.
     */

    public Vehicle(int id,String brand,int cubism)
    {
        this.id = id;
        this.brand = brand;
        this.cubism = cubism;
    }
}