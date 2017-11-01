package evlib.station;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import evlib.sources.*;
import evlib.station.*;
import evlib.ev.*;

class DisChargerTest {

    private ChargingStation station = new ChargingStation("Miami");
    private DisCharger disCharger = new DisCharger(station);

    @Test
    void executeDisChargingEvent() throws InterruptedException {
        station.addDisCharger(disCharger);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(new Driver("Petros"));
        vehicle.setBattery(new Battery(150, 500));
        DisChargingEvent disEvent = new DisChargingEvent(station, vehicle, 15);
        disEvent.preProcessing();

        disCharger.executeDisChargingEvent();

        Thread.sleep(2000);

        assertEquals("finished", disEvent.getCondition());
        assertTrue(DisChargingEvent.dischargingLog.contains(disEvent));
        assertNull(disCharger.getDisChargingEvent());
        assertEquals(disEvent.getElectricVehicle().getDriver().getProfit(), 0);
        assertEquals(disEvent.getDisChargingTime(), 1500);
    }
}