package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisChargingEventTest {

    private ChargingStation station = new ChargingStation("Miami");
    private DisCharger disCharger = new DisCharger(station);

    @Test
    void preProcessing() throws InterruptedException {
        station.addDisCharger(disCharger);
        station.setDisUnitPrice(100);
        station.setDisChargingRate(0.1);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(new Driver("Petros"));
        vehicle.setBattery(new Battery(150, 500));
        DisChargingEvent disEvent = new DisChargingEvent(station, vehicle, 1500);
        disEvent.preProcessing();

        assertEquals(disEvent.getDisChargingTime(), 15000);
        assertEquals(disEvent.getCondition(), "ready");
        assertEquals(disEvent.getProfit(), 150000);
        assertEquals(disEvent.getMaxWaitingTime(), 0);
        assertEquals(disEvent.getWaitingTime(), 0);
        assertEquals(disEvent.getRemainingDisChargingTime(), 0);
        assertEquals(disEvent.getAmountOfEnergy(), 1500);
        assertEquals(station.getTotalEnergy(), 0);

        disEvent.execution();

        Thread.sleep(100);

        disEvent = new DisChargingEvent(station, vehicle, 50);
        disEvent.setWaitingTime(2000000);
        disEvent.preProcessing();
        assertEquals(disEvent.getDisChargingTime(), 0);
        assertEquals(disEvent.getCondition(), "wait");
        assertEquals(disEvent.getProfit(), 0);
        assertTrue(disEvent.getMaxWaitingTime() > 14000);
        assertEquals(disEvent.getWaitingTime(), 2000000);
        assertEquals(disEvent.getRemainingDisChargingTime(), 0);
        assertEquals(disEvent.getAmountOfEnergy(), 50);
        assertEquals(station.getDischarging().getSize(), 1);
        assertEquals(station.getDischarging().get(0), disEvent);
        assertEquals(station.getTotalEnergy(), 0);
    }

    @Test
    void execution() {
        station.addDisCharger(disCharger);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(new Driver("Petros"));
        vehicle.setBattery(new Battery(150, 500));
        DisChargingEvent disEvent = new DisChargingEvent(station, vehicle, 1500);
        disEvent.execution();
        assertEquals(disEvent.getCondition(), "arrived");

        disEvent.preProcessing();
        disEvent.execution();
        assertEquals(disEvent.getCondition(), "discharging");
        assertEquals(disEvent.getElectricVehicle().getBattery().getNumberOfChargings(), 0);
    }

    @Test
    void getRemainingDisChargingTime() {
        station.addDisCharger(disCharger);
        station.setDisChargingRate(0.1);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(new Driver("Petros"));
        vehicle.setBattery(new Battery(150, 500));

        DisChargingEvent event = new DisChargingEvent(station, vehicle, 1500);

        assertEquals(event.getRemainingDisChargingTime(), 0);

        event.setDisChargingTime(150000);
        assertEquals(event.getRemainingDisChargingTime(), 0);

        event.setCondition("discharging");
        event.setDisChargingTime(150000);
        assertTrue(event.getRemainingDisChargingTime() > 140000);
    }

}
