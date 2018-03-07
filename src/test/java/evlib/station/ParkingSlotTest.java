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
        station.setInductiveChargingRate(8000);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ParkingEvent event = new ParkingEvent(station, vehicle, 15000, 20);
        event.preProcessing();

        event.execution();

        Thread.sleep(16000);

        assertEquals("finished", event.getCondition());
        assertTrue(ParkingEvent.getParkLog().contains(event));
        assertNull(slot.getParkingEvent());
        assertEquals(driver.getDebt(), 200);
        assertEquals(battery.getRemAmount(), 170);
        assertEquals(event.getChargingTime(), 9000);
        assertEquals(event.getParkingTime(), 15000);
    }
}
