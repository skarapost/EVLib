package Sources;

import Station.ChargingStation;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EnergySource
{
    private int id;
    private ChargingStation station;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public EnergySource(ChargingStation station)
    {
        this.id = this.idGenerator.incrementAndGet();
        this.station = station;
    }

    public abstract double popAmount();


    public abstract void insertAmount(double am);

    /**
     * @return The id of the EnergySource.
     */
    public int reId()
    {
        return id;
    }

    /**
     * @return The ChagingStation of the EnergySource.
     */
    public ChargingStation reStation()
    {
        return station;
    }
}
