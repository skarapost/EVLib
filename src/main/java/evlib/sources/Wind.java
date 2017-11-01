package evlib.sources;

import java.util.ArrayList;

public class Wind extends EnergySource
{
    private final ArrayList<Double> energyAmount;

    /**
     * Creates a new Wind instance with some energy packages inside.
     * @param energyAmoun An array of energy packages.
     */
    public Wind(double[] energyAmoun)
    {
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun)
            energyAmount.add(anEnergyAmoun);
    }

    /**
     * Creates a new Wind object with no energy packages.
     */
    public Wind()
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
