package EV;

import java.util.concurrent.atomic.AtomicInteger;

public class Driver
{
    private int id;
    private String name;
    private double debt;
    private double profit;
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
    public void setProfit(double profit)
    {
        this.profit = profit;
    }

    /**
     * Returns the profit of the Driver.
     * @return The profit.
     */
    public double reProfit()
    {
        return profit;
    }

    /**
     * Sets the debt a Driver has.
     * @param debt The debt.
     */
    public void setDebt(double debt)
    {
        this.debt = debt;
    }

    /**
     * Returns the debt of the Driver.
     * @return The debt.
     */
    public double reDebt()
    {
        return debt;
    }
}