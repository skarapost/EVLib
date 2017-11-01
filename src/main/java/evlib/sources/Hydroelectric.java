package evlib.sources;

import java.util.ArrayList;

public class Hydroelectric extends EnergySource{
    private final ArrayList<Double> energyAmount;

    /**
     * Constructor of a new Hydroelectric instance, with some energy packages inside.
     * @param energyAmoun An array of energy packages.
     */
    public Hydroelectric(double[] energyAmoun){
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun) energyAmount.add(anEnergyAmoun);
    }

    /**
     * Constructs a new Hydroelectric instance, with no energy packages.
     */
    public Hydroelectric() { energyAmount = new ArrayList<>(); }

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
