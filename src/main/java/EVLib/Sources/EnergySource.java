package EVLib.Sources;

import EVLib.Station.ChargingStation;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class EnergySource
{
    private int id;
    private ChargingStation station;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public EnergySource(ChargingStation station)
    {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
    }

    public abstract double popAmount();


    public abstract void insertAmount(double amount);

    /**
     * @return The id of the EnergySource.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The ChagingStation of the EnergySource.
     */
    public ChargingStation getStation()
    {
        return station;
    }
}
