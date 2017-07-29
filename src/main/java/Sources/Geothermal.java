package Sources;

import Station.ChargingStation;
import java.util.ArrayList;

public class Geothermal extends EnergySource
{
    private ArrayList<Float> energyAmount;

    public Geothermal(ChargingStation station, float[] energyAmoun)
        {
            super(station);
            energyAmount = new ArrayList<>();
            for (float anEnergyAmoun : energyAmoun)
                energyAmount.add(anEnergyAmoun);
        }

    public Geothermal(ChargingStation station)
    {
        super(station);
        energyAmount = new ArrayList<>();
    }

    public float popAmount()
    {
        if ((energyAmount == null)||(energyAmount.size() == 0))
            return 0;
        else
            return energyAmount.get(0);
    }

    public void insertAmount(float am)
    {
        energyAmount.add(am);
    }
}
