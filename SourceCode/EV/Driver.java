package EV;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class Driver {
    private int id;
    private String name;
    private float debt;
    private float profit;

    /**
     * Constructor of Driver.
     * @param id The id of the Driver.
     * @param name The name of the Driver.
     */
    public Driver(int id,String name)
    {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor of Driver class.
     * @param id The id of Driver.
     */
    public Driver(int id)
    {
        this.id = id;
        name = "Unknown Driver";
    }

    /**
     * Sets a profit for the Driver.
     * @param profit The profit the Driver has.
     */
    public final void setProfit(float profit)
    {
        this.profit = profit;
    }

    /**
     * Returns the profit of the Driver. 
     * @return The profit.
     */
    public final float reProfit()
    {
        return profit;
    }

    /**
     * Sets the debt a Driver has.
     * @param debt The debt.
     */
    public final void setDebt(float debt)
    {
        this.debt = debt;
    }

    /**
     * Returns the debt of the Driver.
     * @return The debt.
     */
    public final float reDebt()
    {
        return debt;
    }
}