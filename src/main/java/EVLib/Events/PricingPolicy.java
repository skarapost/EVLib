package EVLib.Events;

import EVLib.Station.ChargingStation;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class PricingPolicy {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private int id;
    private long space;
    private LinkedList<Double> prices;
    private LinkedList<Long> spaces;
    private final short option;

    public PricingPolicy(long space, double[] prices) {
        this.id = idGenerator.incrementAndGet();
        this.space = space;
        this.prices = new LinkedList<>();
        for (double price : prices)
            this.prices.add(price);
        this.option = 1;
    }

    public PricingPolicy(long[] spaces, double[] prices) {
        this.id = idGenerator.incrementAndGet();
        this.prices = new LinkedList<>();
        this.spaces = new LinkedList<>();
        for (int i=0; i<prices.length; i++) {
            this.spaces.add(spaces[i]);
            this.prices.add(prices[i]);
        }
        this.option = 2;
    }

    /**
     * @param position The position of the price in the hierarchy.
     * @return The value of the price in this position of the pricing policy.
     */
    public double getSpecificPrice(int position) {
        try {
            return this.prices.get(position);
        }
        catch (Exception ex)
        { return 0; }
    }

    /**
     * @param position The position in the hierarchy of the timeSpace.
     * @return The time duration of the timeSpace.
     */
    public long getSpecificTimeSpace(int position)
    {
        try {
            if (option == 2) {
                return spaces.get(position);
            }
            else
                return space;
        }
        catch(Exception ex) { return 0; }
    }

    /**
     * Sets the time space the price will be valid and the value of the price.
     * @param position The position in the hierarchy of the pricing policy.
     * @param timeSpace The time space the price will be valid.
     * @param price The value of the price.
     */
    public void setSpecificSpacePrice(int position, long timeSpace, double price) {
        if (option == 2) {
            this.spaces.remove(position);
            this.spaces.add(position, timeSpace);
            this.prices.remove(position);
            this.prices.add(position, price);
        }
    }

    /**
     * Sets the price for a specific time space. This function is valid only when the time space is fixed.
     * @param position The time space the price corresponds to.
     * @param price The price of the time space.
     */
    public void setSpecificPrice(int position, double price)
    {
        if (option == 1) {
            this.prices.remove(position);
            this.prices.add(position, price);
        }
    }

    /**
     * Sets the time space between each change in price.
     * @param timeSpace The time space in milliseconds.
     */
    public void setTimeSpace(long timeSpace) {
        if (option == 1)
            this.space = timeSpace;
    }

    /**
     * @return The time space for every different price.
     */
    public long getSpace() {
        return space;
    }

    /**
     * Returns the time duration of this policy.
     * @return The time duration of the policy.
     */
    public long getDurationOfPolicy()
    {
        long counter = 0;
        if(option == 2)
            for (long space : spaces)
                counter += space;
        else
            counter = prices.size() * space;
        return counter;
    }

    /**
     * @return The id of this pricing policy.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for this PricingPolicy.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
