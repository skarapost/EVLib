package evlib.station;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import evlib.sources.*;
import evlib.station.*;
import evlib.ev.*;

class PricingPolicyTest {
    private long space = 1000;
    private long[] spaces = {1000, 5000, 4000, 8000, 4500, 12000, 7800};
    private double[] prices = {12, 46, 89, 41, 23, 65, 25};

    PricingPolicy policy1 = new PricingPolicy(space, prices);
    PricingPolicy policy2 = new PricingPolicy(spaces, prices);

    @Test
    void getSpecificPrice() {
        assertEquals(policy1.getSpecificPrice(5), 65);
        assertEquals(policy1.getSpecificPrice(8), 0);

        assertEquals(policy2.getSpecificPrice(5), 65);
        assertEquals(policy2.getSpecificPrice(8), 0);
    }

    @Test
    void getSpecificTimeSpace() {
        assertEquals(policy1.getSpecificTimeSpace(5), 1000);
        assertEquals(policy1.getSpecificTimeSpace(15), 1000);

        assertEquals(policy2.getSpecificTimeSpace(5), 12000);
        assertEquals(policy2.getSpecificTimeSpace(12), 0);

    }

    @Test
    void setSpecificSpacePrice() {
        policy2.setSpecificSpacePrice(1, 1500, 30);
        assertEquals(policy2.getSpecificPrice(1), 30);
        assertEquals(policy2.getSpecificTimeSpace(1), 1500);
    }

    @Test
    void setSpecificPrice() {
        policy1.setSpecificPrice(5, 100);
        assertEquals(policy1.getSpecificPrice(5), 100);
    }

    @Test
    void setTimeSpace() {
        policy1.setTimeSpace(1500);
        policy2.setTimeSpace(5000);

        assertEquals(policy1.getSpace(), 1500);
        assertEquals(policy2.getSpace(), 0);
    }

    @Test
    void getDurationOfPolicy() {
        assertEquals(policy1.getDurationOfPolicy(), 7000);
        assertEquals(policy2.getDurationOfPolicy(), 42300);
    }

}
