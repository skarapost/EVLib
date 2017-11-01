package evlib.sources;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class EnergySource
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * Constructor of a new EnergySource object.
     */
    public EnergySource()
    {
        this.id = idGenerator.incrementAndGet();
    }

    /**
     * @return The first package of energy to be given to the EnergySource.
     */
    public abstract double popAmount();

    /**
     * Inserts a package of energy to the queue for update of the storage.
     * @param amount The package's amount of energy.
     */
    public abstract void insertAmount(double amount);

    /**
     * @return The id of the EnergySource.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for the EnergySource.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
