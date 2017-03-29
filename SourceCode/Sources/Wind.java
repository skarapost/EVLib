package Sources;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Station.ChargingStation;
import EV.*;
import java.util.ArrayList;

public class Wind extends EnergySource
{
    private ArrayList<Float> energyAmount;

    /**
     * Constructor of Wind class.
     * @param id The id of Wind.
     * @param station The ChargingStation the Wind belongs.
     * @param energyAmoun The array with the energies to be given to the ChargingStation
     * of Wind source.
     */
    public Wind(int id,ChargingStation station,float[] energyAmoun)
    {
        super(id,station);
        energyAmount = new ArrayList<Float>();
        for(int i=0;i<energyAmoun.length;i++)
        {
            energyAmount.add(i, energyAmoun[i]);
        }
    }

    /**
     * Constructor of Wind class.
     * @param id The id of Wind.
     * @param station The ChargingStation the Wind belongs.
     */
    public Wind(int id,ChargingStation station)
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
