package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import evlib.sources.Solar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingEventTest {
    private ChargingStation station = new ChargingStation("Miami");
    private Solar solar = new Solar();
    private Charger charger = new Charger(station, "slow");

    @Test
    void preProcessing() throws InterruptedException {
        station.addEnergySource(solar);
        station.addCharger(charger);
        solar.insertAmount(1500);
        station.updateStorage();
        station.addParkingSlot(new ParkingSlot(station));
        station.setInductivePrice(100);
        station.setInductiveChargingRate(8000);

        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ParkingEvent event = new ParkingEvent(station, vehicle, 10000, 20);
        event.preProcessing();

        assertEquals(event.getEnergyToBeReceived(), 20);
        assertEquals(event.getParkingTime(), 10000);
        assertEquals(event.getChargingTime(), 9000);
        assertEquals(event.getCondition(), "ready");
        assertEquals(event.getCost(), 2000);
        assertEquals(event.getRemainingChargingTime(), 0);
        assertEquals(event.getRemainingParkingTime(), 0);
        assertEquals(event.getAmountOfEnergy(), 20);
        assertEquals(station.getTotalEnergy(), 1480);
        assertEquals(station.getSpecificAmount("Solar"), 1480);

        event.execution();

        Thread.sleep(100);

        event = new ParkingEvent(station, vehicle, 5000, 20);
        event.preProcessing();
        assertEquals(event.getChargingTime(), 0);
        assertEquals(event.getCondition(), "nonExecutable");
        assertEquals(event.getEnergyToBeReceived(), 0);
        assertEquals(event.getCost(), 0);
        assertEquals(event.getRemainingChargingTime(), 0);
        assertEquals(event.getAmountOfEnergy(), 20);
        assertEquals(station.getSpecificAmount("Solar"), 1480);
    }

    @Test
    void execution() {
        station.addEnergySource(solar);
        station.addCharger(charger);
        solar.insertAmount(1500);
        station.updateStorage();
        station.addParkingSlot(new ParkingSlot(station));
        station.setInductivePrice(100);
        station.setInductiveChargingRate(800);

        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ParkingEvent event = new ParkingEvent(station, vehicle, 10000, 20);
        event.execution();

        event.preProcessing();
        event.execution();
        assertEquals(event.getCondition(), "charging");
        assertEquals(event.getElectricVehicle().getBattery().getNumberOfChargings(), 1);
    }

    @Test
    void getRemainingChargingTime() {
        station.addEnergySource(solar);
        station.addCharger(charger);
        solar.insertAmount(1500);
        station.updateStorage();
        station.addParkingSlot(new ParkingSlot(station));
        station.setInductivePrice(100);
        station.setInductiveChargingRate(8000);

        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ParkingEvent event = new ParkingEvent(station, vehicle, 10000, 20);

        assertEquals(event.getRemainingChargingTime(), 0);

        event.setChargingTime(9000);
        assertEquals(event.getRemainingChargingTime(), 0);

        event.setCondition("charging");
        event.setChargingTime(9000);
        assertTrue(event.getRemainingChargingTime() > 1400);
    }

    @Test
    void getRemainingParkingTime() {
        station.addEnergySource(solar);
        station.addCharger(charger);
        solar.insertAmount(1500);
        station.updateStorage();
        station.addParkingSlot(new ParkingSlot(station));
        station.setInductivePrice(100);
        station.setInductiveChargingRate(8000);

        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ParkingEvent event = new ParkingEvent(station, vehicle, 10000, 20);

        assertEquals(event.getRemainingParkingTime(), 0);

        event.setParkingTime(10000);
        assertEquals(event.getRemainingParkingTime(), 0);

        event.setCondition("parking");
        event.setParkingTime(10000);
        assertTrue(event.getRemainingParkingTime() > 1900);
    }
}
