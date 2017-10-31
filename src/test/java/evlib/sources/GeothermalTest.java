package evlib.sources;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GeothermalTest {
    private double[] amounts = {150, 190, 600, 18000};
    private Geothermal geo = new Geothermal(amounts);
    private Geothermal geo1 = new Geothermal();

    @Test
    void popAmount() {
        assertEquals(150, geo.popAmount());
        assertEquals(0, geo1.popAmount());
    }
}
