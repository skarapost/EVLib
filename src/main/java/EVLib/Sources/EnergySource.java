package EVLib.Sources;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class EnergySource
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public EnergySource()
    {
        this.id = idGenerator.incrementAndGet();
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
     * Sets the id for this EnergySource.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
