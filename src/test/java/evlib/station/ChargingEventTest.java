package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import evlib.sources.Solar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChargingEventTest {
    private ChargingStation station = new ChargingStation("Miami");
    private Solar solar = new Solar();
    private Charger charger = new Charger(station, "slow");

    @Test
    void preProcessing() throws InterruptedException {
        station.addEnergySource(solar);
        solar.insertAmount(1500);
        station.addCharger(charger);
        station.updateStorage();
        station.setUnitPrice(100);
        station.setChargingRateSlow(800);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ChargingEvent event = new ChargingEvent(station, vehicle, 20, "slow");
        event.preProcessing();

        assertEquals(event.getChargingTime(), 90000);
        assertEquals(event.getCondition(), "ready");
        assertEquals(event.getEnergyToBeReceived(), 20);
        assertEquals(event.getCost(), 2000);
        assertEquals(event.getMaxWaitingTime(), 0);
        assertEquals(event.getWaitingTime(), 0);
        assertEquals(event.getRemainingChargingTime(), 0);
        assertEquals(event.getAmountOfEnergy(), 20);
        assertEquals(station.getTotalEnergy(), 1480);
        assertEquals(station.getSpecificAmount("Solar"), 1480);
        assertEquals(station.getCurrentPrice(), 100);

        event.execution();

        Thread.sleep(100);

        event = new ChargingEvent(station, vehicle, 20, "slow");
        event.setWaitingTime(2000000);
        event.preProcessing();
        assertEquals(event.getChargingTime(), 0);
        assertEquals(event.getCondition(), "wait");
        assertEquals(event.getEnergyToBeReceived(), 0);
        assertEquals(event.getCost(), 0);
        assertTrue(event.getMaxWaitingTime() > 19000);
        assertEquals(event.getWaitingTime(), 2000000);
        assertEquals(event.getRemainingChargingTime(), 0);
        assertEquals(event.getAmountOfEnergy(), 20);
        assertEquals(station.getSlow().getSize(), 1);
        assertEquals(station.getSlow().get(0), event);
        assertEquals(station.getTotalEnergy(), 1480);
        assertEquals(station.getSpecificAmount("Solar"), 1480);
        assertEquals(station.getCurrentPrice(), 100);
    }

    @Test
    void execution() {
        station.addEnergySource(solar);
        solar.insertAmount(1500);
        station.addCharger(charger);
        station.updateStorage();
        station.setUnitPrice(100);
        station.setChargingRateSlow(800);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ChargingEvent event = new ChargingEvent(station, vehicle, 20, "slow");
        event.execution();
        assertEquals(event.getCondition(), "arrived");

        event.preProcessing();
        event.execution();
        assertEquals(event.getCondition(), "charging");
        assertEquals(event.getElectricVehicle().getBattery().getNumberOfChargings(), 1);
    }

    @Test
    void getRemainingChargingTime() {
        station.addEnergySource(solar);
        solar.insertAmount(1500);
        station.addCharger(charger);
        station.updateStorage();
        station.setUnitPrice(100);
        station.setChargingRateSlow(800);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);
        ChargingEvent event = new ChargingEvent(station, vehicle, 20, "slow");

        assertEquals(event.getRemainingChargingTime(), 0);

        event.setChargingTime(90000);
        assertEquals(event.getRemainingChargingTime(), 0);

        event.setCondition("charging");
        event.setChargingTime(90000);
        assertTrue(event.getRemainingChargingTime() > 14000);
    }

}
