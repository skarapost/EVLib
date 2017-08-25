package Sources;

import Station.ChargingStation;
import java.util.ArrayList;

public class Wave extends EnergySource
{
    private ArrayList<Double> energyAmount;

    public Wave(ChargingStation station, double[] energyAmoun)
    {
        super(station);
        energyAmount = new ArrayList<>();
        for(int i=0; i<energyAmoun.length; i++)
            energyAmount.add(energyAmoun[i]);
    }

    public Wave(ChargingStation station)
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