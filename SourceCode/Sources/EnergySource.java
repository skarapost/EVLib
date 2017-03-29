package Sources;

import Station.ChargingStation;
import EV.*;


/**
 *
 * @author Sotiris Karapostolakis
 */
public abstract class EnergySource 
{
    private int id;
    private ChargingStation station;

    /**
     * Constructor of EnergySource.
     * @param id The id of the EnergySource.
     * @param station The ChargingStation that belongs.
     */
    public EnergySource(int id,ChargingStation station)
    {
        this.id = id;
        this.station = station;
    }

    /**
     * Returns an amount of energy that is going to be given to the 
     * ChargingStation in the num-th update storage. 
     * @param num The amount of energy.
     * @return The asking amount of energy. 
     */
    public abstract float reAmount(int num);

    /**
     * Modifies a specific amount of energy that is going to be given in the num-th
     * update storage.
     * @param num The amount to be modified.
     * @param am The new amount.
     */
    public abstract void modifySpecificAmount(int num, float am);
    
    /**
     * Returns the id of the EnergySource.
     * @return The id of the EnergySource.
     */
    public final int reId()
    {
        return id;
    }
    
    /**
     * Returns the ChargingStation of the EnergySource.
     * @return The ChagingStation of the EnergySource.
     */
    public final ChargingStation reStation()
    {
        return station;
    }
}
