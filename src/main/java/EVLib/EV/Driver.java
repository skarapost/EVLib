package EVLib.EV;

import java.util.concurrent.atomic.AtomicInteger;

public class Driver
{
    private int id;
    private String name;
    private double debt;
    private double profit;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public Driver(String name)
    {
        this.id = idGenerator.incrementAndGet();
        this.name = name;
    }

    public Driver()
    {
        this.id = idGenerator.incrementAndGet();
        name = "Unknown Driver";
    }

    /**
     * Sets a profit for the Driver.
     * @param profit The profit to be set.
     */
    public void setProfit(double profit)
    {
        this.profit = profit;
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
     * @param debt The debt to be set.
     */
    public void setDebt(double debt)
    {
        this.debt = debt;
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
     * @param name The name to be set.
     */
    public void setName(String name) { this.name = name; }

    /**
     * @return The id of the Driver.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id of the Driver.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}