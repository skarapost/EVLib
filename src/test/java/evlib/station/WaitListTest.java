package evlib.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaitListTest {
    ChargingStation station = new ChargingStation("Miami");
    WaitList<ChargingEvent> list = new WaitList<>();
    ChargingEvent event = new ChargingEvent(station, null, 0, "slow");
    ChargingEvent event1 = new ChargingEvent(station, null, 0, "fast");
    ChargingEvent event2 = new ChargingEvent(station, null, 0, "fast");

    @Test
    void get() {
        list.add(event);
        assertEquals(list.get(0), event);
    }

    @Test
    void add() {
        list.add(event);
        assertEquals(list.getSize(), 1);
    }

    @Test
    void delete() {
        list.add(event);
        assertEquals(list.getSize(), 1);
        assertEquals(list.get(0), event);
        assertTrue(list.delete(event));
        assertEquals(list.getSize(), 0);
    }

    @Test
    void takeFirst() {
        list.add(event);
        list.add(event1);
        list.add(event2);
        assertEquals(list.takeFirst(), event);
    }

    @Test
    void moveFirst() {
        list.add(event);
        list.add(event1);
        list.add(event2);
        assertEquals(list.moveFirst(), event);
        assertEquals(list.getSize(), 2);
    }

}
