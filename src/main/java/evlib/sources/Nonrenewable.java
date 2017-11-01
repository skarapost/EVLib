package evlib.sources;

import java.util.ArrayList;

public class Nonrenewable extends EnergySource{
    private final ArrayList<Double> energyAmount;

    /**
     * Creates a new Nonrenewable object.
     * @param energyAmoun An array with some energy packages.
     */
    public Nonrenewable(double[] energyAmoun)
    {
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun)
            energyAmount.add(anEnergyAmoun);
    }

    /**
     * Creates a new Nonrenewable object with no energy packages.
     */
    public Nonrenewable()
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
