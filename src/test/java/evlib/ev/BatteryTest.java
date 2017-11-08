package evlib.ev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatteryTest {

    private Battery battery = new Battery(500, 100);

    @Test
    void addCharging() {
        battery.addCharging();
        assertEquals(1, battery.getNumberOfChargings());
        assertEquals(true, battery.getActive());

        battery.setMaxNumberOfChargings(5);
        for(int i=0; i<4; i++)
            battery.addCharging();
        assertEquals(battery.getActive(), false);
    }

    @Test
    void setMaxNumberOfChargings() {
        battery.setMaxNumberOfChargings(50);
        assertEquals(50, battery.getMaxNumberOfChargings());

        battery.setNumberOfChargings(10);
        battery.setMaxNumberOfChargings(5);
        assertEquals(5, battery.getMaxNumberOfChargings());
        assertEquals(battery.getActive(), false);
    }

    @Test
    void setNumberOfChargings() {
        battery.setNumberOfChargings(50);
        assertEquals(50, battery.getNumberOfChargings());

        battery.setMaxNumberOfChargings(10);
        battery.setNumberOfChargings(15);
        assertEquals(10, battery.getNumberOfChargings());
        assertEquals(battery.getActive(), false);
    }

}
