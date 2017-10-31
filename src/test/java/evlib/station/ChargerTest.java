package evlib.station;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import evlib.sources.*;
import evlib.station.*;
import evlib.ev.*;

class ChargerTest {

    private ChargingStation station = new ChargingStation("Miami");
    private Solar solar = new Solar();
    private Charger charger = new Charger(station, "fast");

    @Test
    void executeChargingEvent() throws InterruptedException {
        station.addEnergySource(solar);
        solar.insertAmount(1500);
        station.addCharger(charger);
        station.updateStorage();
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ChargingEvent event = new ChargingEvent(station, vehicle, 20, "fast");
        event.preProcessing();
        charger.executeChargingEvent(false);

        Thread.sleep(2500);

        assertEquals("finished", event.getCondition());
        assertTrue(ChargingEvent.chargingLog.contains(event));
        assertNull(charger.getChargingEvent());
        assertEquals(driver.getDebt(), 0);
        assertEquals(battery.getRemAmount(), 170);
        assertEquals(event.getChargingTime(), 2000);
    }

}
