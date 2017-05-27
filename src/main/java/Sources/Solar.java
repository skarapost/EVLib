package Sources;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Station.ChargingStation;
import EV.*;
import java.util.ArrayList;

public class Solar extends EnergySource
{
    private ArrayList<Float> energyAmount;

    /**
     * Constructor of Solar class.
     * @param id The id of Solar.
     * @param station The ChargingStation the Solar belongs.
     * @param energyAmoun The array with the energies to be given to the ChargingStation
     * of Solar source.
     */
    public Solar(int id,ChargingStation station,float[] energyAmoun)
    {
        super(id,station);
        energyAmount = new ArrayList<Float>();
        for (int i=0;i<energyAmoun.length;i++)
        {
            energyAmount.add(i, energyAmoun[i]);
        }
    }

    /**
     * Constructor of Solar class.
     * @param id The id of Solar.
     * @param station The ChargingStation the Solar belongs.
     */
    public Solar(int id,ChargingStation station)
    {
        super(id,station);
        energyAmount = new ArrayList<Float>();
    }
    @Override
    public final float reAmount(int num)
    {
        if ((energyAmount == null)||(energyAmount.size() <= num))
            return 0;
        else
            return energyAmount.get(num);
    }
    @Override
    public final void modifySpecificAmount(int num, float am)
    {
        if (energyAmount.size() > num)
            energyAmount.remove(num);
        energyAmount.add(num,am);
    }
}