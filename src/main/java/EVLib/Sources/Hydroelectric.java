package EVLib.Sources;

import java.util.ArrayList;

public class Hydroelectric extends EnergySource{
    private final ArrayList<Double> energyAmount;

    public Hydroelectric(double[] energyAmoun){
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun) energyAmount.add(anEnergyAmoun);
    }

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