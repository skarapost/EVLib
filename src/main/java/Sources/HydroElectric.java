package Sources;

import Station.ChargingStation;
import java.util.ArrayList;

public class HydroElectric extends EnergySource{
    private ArrayList<Float> energyAmount;

    public HydroElectric(ChargingStation station, float[] energyAmoun){
        super(station);
        energyAmount = new ArrayList<Float>();
        for(int i=0; i<energyAmoun.length; i++)
            energyAmount.add(energyAmoun[i]);
    }

    public HydroElectric(ChargingStation station)
    {
        super(station);
        energyAmount = new ArrayList<Float>();
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