package EVLib.Events;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class PricingPolicy {
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private int id;
    private long space;
    private LinkedList prices;
    private short option;

    public PricingPolicy(long space, double[] prices) {
        this.id = idGenerator.incrementAndGet();
        this.space = space;
        this.prices = new LinkedList();
        for (double price : prices)
            this.prices.add(price);
        this.option = 1;
    }

    public PricingPolicy(double[][] prices) {
        this.id = idGenerator.incrementAndGet();
        this.prices = new LinkedList<Pair>();
        for (double price[] : prices) {
            Pair pair = new Pair(price[0], price[1]);
            this.prices.add(pair);
        }
        this.option = 2;
    }

    /**
     * @param position The position of the price in the hierarchy.
     * @return The value of the price in this position of the pricing policy.
     */
    public double getSpecificPrice(int position) {
        try {
            if (option == 2) {
                Pair t = (Pair) this.prices.get(position);
                return (double) t.getR();
            } else
                return (double) prices.get(position);
        }
        catch(Exception ex) { return 0; }
    }

    /**
     * @param position The position in the hierarchy of the timeSpace.
     * @return The time duration of the timeSpace.
     */
    public double getSpecificTimeSpace(int position)
    {
        try {
            if (option == 2) {
                Pair t = (Pair) this.prices.get(position);
                return (double) t.getL();
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
            Pair t = (Pair) this.prices.get(position);
            t.setL(timeSpace);
            t.setR(price);
            this.prices.remove(position);
            this.prices.add(position, t);
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

    private class Pair<L, R> {
        private L l;
        private R r;

        Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        L getL() {
            return l;
        }

        void setL(L l) {
            this.l = l;
        }

        R getR() {
            return r;
        }

        void setR(R r) {
            this.r = r;
        }
    }

    /**
     * Returns the time duration of this policy. If the policy has a fixed time space then it returns -1, because
     * it cannot be counted.
     * @return The time duration of the policy.
     */
    public double getDurationOfPolicy()
    {
        double counter = -1;
        if(option == 2) {
            counter = 0;
            for (Object price : prices) {
                Pair t = (Pair) price;
                counter += (double) t.getR();
            }
        }
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
