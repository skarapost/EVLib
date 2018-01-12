package evlib.ev;

import java.util.concurrent.atomic.AtomicInteger;

public class Driver
{
    private int id;
    private String name;
    private double debt;
    private double profit;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * Creates a new Driver object, asking only for the name.
     * @param nam The name of the Driver.
     */
    public Driver(final String nam)
    {
        this.id = idGenerator.incrementAndGet();
        this.name = nam;
    }

    /**
     * Crates a new Driver object.
     */
    public Driver()
    {
        this.id = idGenerator.incrementAndGet();
        name = "Unknown Driver";
    }

    /**
     * Sets a profit for the Driver.
     * @param prof The profit to be set.
     */
    public void setProfit(final double prof)
    {
        this.profit = prof;
    }

    /**
     * @return The profit of the Driver.
     */
    public double getProfit()
    {
        return profit;
    }

    /**
     * Sets the debt of a Driver.
     * @param deb The debt to be set.
     */
    public void setDebt(final double deb)
    {
        this.debt = deb;
    }

    /**
     * @return The debt of the Driver.
     */
    public double getDebt()
    {
        return debt;
    }

    /**
     * @return The name of the Driver.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets a name for the Driver.
     * @param nam The name to be set.
     */
    public void setName(final String nam) { this.name = nam; }

    /**
     * @return The id of the Driver.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id of the Driver.
     * @param d The id to be set.
     */
    public void setId(final int d) { this.id = d; }
}
