package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExchangeHandlerTest {

    private ChargingStation station = new ChargingStation("Miami");
    private Battery battery = new Battery(1500, 1500);
    private ExchangeHandler handler = new ExchangeHandler(station);

    @Test
    void executeExchange() throws InterruptedException {
        station.joinBattery(battery);
        station.addExchangeHandler(handler);
        station.setTimeofExchange(100);
        Driver driver = new Driver("Petros");
        Battery battery = new Battery(150, 500);
        ElectricVehicle vehicle = new ElectricVehicle("Fiat");
        vehicle.setDriver(driver);
        vehicle.setBattery(battery);

        ChargingEvent event = new ChargingEvent(station, vehicle);
        event.preProcessing();
        event.setCondition("swapping");
        station.getExchangeHandlers()[0].startExchangeHandler();

        Thread.sleep(150);

        assertEquals("finished", event.getCondition());
        assertEquals(ChargingEvent.getExchangeLog().get(0), event);
        assertNull(station.getExchangeHandlers()[0].getChargingEvent());
        assertEquals(driver.getDebt(), 0);
        assertEquals(event.getElectricVehicle().getBattery().getRemAmount(), 1500);
        assertEquals(station.getExchangeHandlers()[0].getName(), "ExchangeHandler1");
        assertEquals(event.getChargingTime(), 100);
    }

}
