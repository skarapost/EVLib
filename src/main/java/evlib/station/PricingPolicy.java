package evlib.station;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class PricingPolicy {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private int id;
    private long space;
    private LinkedList<Double> prices;
    private LinkedList<Long> spaces;
    private final short option;

    /**
     * Creates a new PricingPolicy instance. The time frame for each price is fixed.
     * @param space The time space each price we desire to endure.
     * @param prices An array of prices for the PricingPolicy object.
     */
    public PricingPolicy(long space, double[] prices) {
        this.id = idGenerator.incrementAndGet();
        this.space = space;
        this.prices = new LinkedList<>();
        for (double price : prices)
            this.prices.add(price);
        this.option = 1;
    }

    /**
     * Creates a new PricingPolicy object. The time space for each price may change.
     * The constructor takes two arrays as parameters. Both arrays' sizes need to be equal.
     * @param spaces The time spaces for which the prices will endure measured in milliseconds.
     * @param prices An array with the prices of the PricingPolicy object.
     */
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
     * @param position The time frame for which we want the price to last in milliseconds.
     * @return The price for this time space of the PricingPolicy.
     */
    public double getSpecificPrice(int position) {
        try {
            return this.prices.get(position);
        }
        catch (Exception ex)
        { return 0; }
    }

    /**
     * @param position The time space we want.
     * @return The duration of the time space in milliseconds.
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
     * @param position The time space of the PricingPolicy.
     * @param timeSpace The time space for which the price is valid in milliseconds.
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
     * @param position The price we want to change.
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
     * @return The time space for every different price in milliseconds.
     */
    public long getSpace() {
        return space;
    }

    /**
     * @return The time duration of the PricingPolicy in milliseconds.
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
     * @return The id of the PricingPolicy.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for the PricingPolicy.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
