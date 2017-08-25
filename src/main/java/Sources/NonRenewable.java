package Sources;

import Station.ChargingStation;
import java.util.ArrayList;

public class NonRenewable extends EnergySource{
    private ArrayList<Double> energyAmount;

    public NonRenewable(ChargingStation station, double[] energyAmoun)
    {
        super(station);
        energyAmount = new ArrayList<>();
        for (double anEnergyAmoun : energyAmoun)
            energyAmount.add(anEnergyAmoun);
    }

    public NonRenewable(ChargingStation station)
    {
        super(station);
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