package evlib.sources;

import java.util.ArrayList;

public class Wave extends EnergySource
{
    private final ArrayList<Double> energyAmount;

    /**
     * Constructor of a Wave object with some energy packages inside.
     * @param energyAmoun An array with energy packages.
     */
    public Wave(double[] energyAmoun)
    {
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun) energyAmount.add(anEnergyAmoun);
    }

    /**
     * Constructs a new Wave object.
     */
    public Wave()
    {
        energyAmount = new ArrayList<>();
    }

    public double popAmount()
    {
        if ((energyAmount == null)||(energyAmount.size() == 0))
            return 0;
        else
            return energyAmount.remove(0);
    }

    public void insertAmount(double am)
    {
        energyAmount.add(am);
    }
}
