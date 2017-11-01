package evlib.sources;

import java.util.ArrayList;

public class Geothermal extends EnergySource
{
    private final ArrayList<Double> energyAmount;

    /**
     * Constructs a new Geothermal instance taking some energy packages.
     * @param energyAmoun An array with some energy packages.
     */
    public Geothermal(double[] energyAmoun) {
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun)
            energyAmount.add(anEnergyAmoun);
    }

    /**
     * Constructs a new Geothermal instance with no energy packages.
     */
    public Geothermal() { energyAmount = new ArrayList<>(); }

    public double popAmount() {
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
