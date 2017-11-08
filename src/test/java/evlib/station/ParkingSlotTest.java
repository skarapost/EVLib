package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import evlib.sources.Solar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSlotTest {
    private ChargingStation station = new ChargingStation("Miami");
    private Solar solar = new Solar();
    private ParkingSlot slot = new ParkingSlot(station);

    @Test
    void parkingVehicle() throws InterruptedException {
        station.addParkingSlot(slot);
        station.addEnergySource(solar);
        solar.insertAmount(1500);
        station.updateStorage();
        station.setInductivePrice(10);
        station.setInductiveChargingRatio(0.1);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ParkingEvent event = new ParkingEvent(station, vehicle, 15000, 250);
        event.preProcessing();

        event.execution();

        Thread.sleep(16000);

        assertEquals("finished", event.getCondition());
        assertTrue(ParkingEvent.parkLog.contains(event));
        assertNull(slot.getParkingEvent());
        assertEquals(driver.getDebt(), 2500);
        assertEquals(battery.getRemAmount(), 400);
        assertEquals(event.getChargingTime(), 2500);
        assertEquals(event.getParkingTime(), 15000);
    }
}
