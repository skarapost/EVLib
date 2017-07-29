package EV;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Sotiris Karapostolakis
 */

public class Driver
{
    private int id;
    private String name;
    private float debt;
    private float profit;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public Driver(String name)
    {
        this.id = idGenerator.getAndIncrement();
        this.name = name;
    }

    public Driver()
    {
        this.id = idGenerator.getAndIncrement();
        name = "Unknown Driver";
    }

    /**
     * Sets a profit for the Driver.
     * @param profit The profit the Driver has.
     */
    public void setProfit(float profit)
    {
        this.profit = profit;
    }

    /**
     * Returns the profit of the Driver.
     * @return The profit.
     */
    public float reProfit()
    {
        return profit;
    }

    /**
     * Sets the debt a Driver has.
     * @param debt The debt.
     */
    public void setDebt(float debt)
    {
        this.debt = debt;
    }

    /**
     * Returns the debt of the Driver.
     * @return The debt.
     */
    public float reDebt()
    {
        return debt;
    }
}