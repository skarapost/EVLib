package Events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import Station.ChargingStation;

public class PricingPolicy {
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private int id;
    private ChargingStation station;
    private long space;
    private LinkedList prices;
    private Pair pair;
    private short option;

    public PricingPolicy(ChargingStation station, long space, double[] prices) {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.space = space;
        this.prices = new LinkedList();
        for (double price : prices)
            this.prices.add(price);
        this.option = 1;
    }

    public PricingPolicy(ChargingStation station, double[][] prices) {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.prices = new LinkedList<Pair>();
        for (double price[] : prices) {
            pair = new Pair(price[0], price[1]);
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
        catch(Exception ex)
        {
            return station.getUnitPrice();
        }
    }


    public long getSpecificTimeSpace(int position)
    {
        try {
            if (option == 2) {
                Pair t = (Pair) this.prices.get(position);
                return (long) t.getL();
            }
            else
                return space;
        }
        catch(Exception ex)
        {
            return 0;
        }
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
        if (option == 1)
        {
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

        public Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        public L getL() {
            return l;
        }

        public void setL(L l) {
            this.l = l;
        }

        public R getR() {
            return r;
        }

        public void setR(R r) {
            this.r = r;
        }
    }

    /**
     * @return The id of this pricing policy.
     */
    public int getId()
    {
        return id;
    }
}
