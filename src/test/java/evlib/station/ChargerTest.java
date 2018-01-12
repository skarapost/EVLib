package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import evlib.sources.Solar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        station.setChargingRateFast(0.01);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ChargingEvent event = new ChargingEvent(station, vehicle, 20, "fast");
        event.preProcessing();
        charger.startCharger();

        Thread.sleep(2500);

        assertEquals("finished", event.getCondition());
        assertTrue(ChargingEvent.getChargingLog().contains(event));
        assertNull(charger.getChargingEvent());
        assertEquals(driver.getDebt(), 0);
        assertEquals(battery.getRemAmount(), 170);
        assertEquals(event.getChargingTime(), 2000);
    }

}
