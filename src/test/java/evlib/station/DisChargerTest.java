package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DisChargerTest {

    private ChargingStation station = new ChargingStation("Miami");
    private DisCharger disCharger = new DisCharger(station);

    @Test
    void executeDisChargingEvent() throws InterruptedException {
        station.addDisCharger(disCharger);
        station.setDisChargingRate(800);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(new Driver("Petros"));
        vehicle.setBattery(new Battery(150, 500));
        DisChargingEvent disEvent = new DisChargingEvent(station, vehicle, 20);
        disEvent.preProcessing();

        disCharger.startDisCharger();

        Thread.sleep(100000);

        assertEquals("finished", disEvent.getCondition());
        assertTrue(DisChargingEvent.getDischargingLog().contains(disEvent));
        assertNull(disCharger.getDisChargingEvent());
        assertEquals(disEvent.getElectricVehicle().getDriver().getProfit(), 0);
        assertEquals(disEvent.getDisChargingTime(), 90000);
    }
}
