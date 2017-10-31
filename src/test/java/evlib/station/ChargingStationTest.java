package evlib.station;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import evlib.sources.*;
import evlib.station.*;
import evlib.ev.*;

class ChargingStationTest {
    private double[][] amounts = {{150, 500, 800, 900, 450}, {800, 560, 780, 900, 450}};
    private String[] kinds = {"slow", "fast", "slow", "slow"};
    private String[] sources = {"Solar", "Geothermal"};
    private ChargingStation station = new ChargingStation("Miami", kinds, sources, amounts);

    @Test
    void updateQueue() {
        ChargingEvent event = new ChargingEvent(station, null, 500, "slow");
        station.updateQueue(event);
        assertEquals(station.getSlow().getSize(), 1);
        assertEquals(station.getSlow().get(0), event);

        ChargingEvent event1 = new ChargingEvent(station, null, 500, "fast");
        station.updateQueue(event1);
        assertEquals(station.getFast().getSize(), 1);
        assertEquals(station.getFast().get(0), event1);
    }

    @Test
    void updateDisChargingQueue() {
        DisChargingEvent event = new DisChargingEvent(station, null, 500);
        station.updateDisChargingQueue(event);
        assertEquals(station.getDischarging().getSize(), 1);
        assertEquals(station.getDischarging().get(0), event);
    }

    @Test
    void assignCharger() {
        ChargingEvent event = new ChargingEvent(station, null, 500, "slow");
        assertEquals(station.assignCharger(event), station.getChargers()[0]);
        assertEquals(station.getChargers()[0].getChargingEvent(), event);
    }

    @Test
    void assignDisCharger() {
        DisChargingEvent disEvent = new DisChargingEvent(station, null, 500);
        assertNull(station.assignDisCharger(disEvent));

        DisCharger discharger = new DisCharger(station);
        station.addDisCharger(discharger);
        assertEquals(station.assignDisCharger(disEvent), discharger);
        assertEquals(discharger.getDisChargingEvent(), disEvent);
    }

    @Test
    void assignExchangeHandler() {
        ChargingEvent event = new ChargingEvent(station, null);
        assertNull(station.assignExchangeHandler(event));

        ExchangeHandler handler = new ExchangeHandler(station);
        station.addExchangeHandler(handler);
        assertEquals(station.assignExchangeHandler(event), handler);
        assertEquals(handler.getChargingEvent(), event);
    }

    @Test
    void assignParkingSlot() {
        ParkingEvent event = new ParkingEvent(station, null, 15000);
        assertNull(station.assignParkingSlot(event));

        ParkingSlot slot = new ParkingSlot(station);
        station.addParkingSlot(slot);
        assertEquals(station.assignParkingSlot(event), slot);
        assertEquals(slot.getParkingEvent(), event);
    }

    @Test
    void assignBattery() {
        assertNull(station.assignBattery());

        Battery battery = new Battery(1500, 6000);
        station.joinBattery(battery);
        assertEquals(station.assignBattery(), battery);
        assertEquals(station.getBatteries().length, 0);
    }

    @Test
    void addCharger(){
        Charger charger = new Charger(station, "slow");
        station.addCharger(charger);
        assertEquals(station.getChargers().length, 5);
        assertEquals(station.SLOW_CHARGERS, 4);
        assertEquals(station.getChargers()[station.getChargers().length-1], charger);
    }

    @Test
    void addDisCharger() {
        DisCharger discharger = new DisCharger(station);
        station.addDisCharger(discharger);
        assertEquals(station.getDisChargers().length, 1);
        assertEquals(station.getDisChargers()[0], discharger);
    }

    @Test
    void addExchangeHandler() {
        ExchangeHandler handler = new ExchangeHandler(station);
        station.addExchangeHandler(handler);
        assertEquals(station.getExchangeHandlers().length, 1);
        assertEquals(station.getExchangeHandlers()[0], handler);
    }

    @Test
    void addParkingSlot() {
        ParkingSlot slot = new ParkingSlot(station);
        station.addParkingSlot(slot);
        assertEquals(station.getParkingSlots().length, 1);
        assertEquals(station.getParkingSlots()[0], slot);
    }

    @Test
    void addEnergySource() {
        assertFalse(station.getMap().containsKey("Wind"));
        EnergySource source = new Wind();
        station.addEnergySource(source);
        assertNotNull(station.getEnergySource("Wind"));
    }

    @Test
    void deleteEnergySource() {
        EnergySource source = new Wind();
        station.addEnergySource(source);
        assertTrue(station.getMap().containsKey("Wind"));
        station.deleteEnergySource(source);
        assertNull(station.getEnergySource("Wind"));
    }

    @Test
    void deleteCharger() {
        assertEquals(station.getChargers().length, 4);
        Charger charger = station.getChargers()[1];
        station.deleteCharger(charger);
        assertNotEquals(station.getChargers()[1].getKindOfCharging(), "fast");
    }

    @Test
    void deleteDisCharger() {
        DisCharger dsch = new DisCharger(station);
        station.addDisCharger(dsch);
        assertEquals(station.getDisChargers()[0], dsch);
        station.deleteDisCharger(dsch);
        assertEquals(station.getDisChargers().length, 0);
    }

    @Test
    void deleteExchangeHandler() {
        ExchangeHandler handler = new ExchangeHandler(station);
        station.addExchangeHandler(handler);
        assertEquals(station.getExchangeHandlers()[0], handler);
        station.deleteExchangeHandler(handler);
        assertEquals(station.getExchangeHandlers().length, 0);
    }

    @Test
    void deleteParkingSlot() {
        ParkingSlot slot = new ParkingSlot(station);
        station.addParkingSlot(slot);
        assertEquals(station.getParkingSlots()[0], slot);
        station.deleteParkingSlot(slot);
        assertEquals(station.getParkingSlots().length, 0);
    }

    @Test
    void customEnergySorting() {
        String[] actual = {"Solar", "Geothermal", "DisCharging"};
        String[] energies = {"Geothermal", "DisCharging", "Solar"};
        assertArrayEquals(station.getSources(), actual);
        station.customEnergySorting(energies);
        assertArrayEquals(station.getSources(), energies);
    }

    @Test
    void joinBattery() {
        assertEquals(station.getBatteries().length, 0);
        Battery battery = new Battery(1500, 1500);
        station.joinBattery(battery);
        assertEquals(station.getBatteries().length, 1);
        assertEquals(station.getBatteries()[0], battery);
    }

    @Test
    void deleteBattery() {
        Battery bat = new Battery(1500, 1500);
        station.joinBattery(bat);
        assertEquals(station.getBatteries()[0], bat);
        station.deleteBattery(bat);
        assertEquals(station.getBatteries().length, 0);
    }

    @Test
    void batteriesCharging() throws InterruptedException {
        station.updateStorage();

        Thread.sleep(500);

        Battery bat = new Battery(1450, 1500);
        station.joinBattery(bat);
        station.batteriesCharging("fast");

        Thread.sleep(6000);

        assertEquals(station.getBatteries()[0].getRemAmount(), 1500);
    }

    @Test
    void updateStorage() {
        station.updateStorage();
        assertEquals(station.getSpecificAmount("Solar"), 150);
        assertEquals(station.getSpecificAmount("Geothermal"), 800);

        station.updateStorage();
        assertEquals(station.getSpecificAmount("Solar"), 650);
        assertEquals(station.getSpecificAmount("Geothermal"), 1360);

    }

    @Test
    void getCurrentPrice() throws InterruptedException {
        assertEquals(station.getCurrentPrice(), 0);

        station.setUnitPrice(50);
        assertEquals(station.getCurrentPrice(), 50);

        double[] prices = {150, 25, 160};
        PricingPolicy policy = new PricingPolicy(2000, prices);
        station.setPricingPolicy(policy);

        Thread.sleep(1900);

        assertEquals(station.getCurrentPrice(), 150);

        long[] spaces = {2500, 6000, 9000};
        policy = new PricingPolicy(spaces, prices);
        station.setPricingPolicy(policy);

        assertEquals(station.getCurrentPrice(), 150);

        Thread.sleep(2500);

        assertEquals(station.getCurrentPrice(), 25);

        Thread.sleep(20000);

        assertEquals(station.getCurrentPrice(), 50);
    }

    @Test
    void calculatePrice() throws InterruptedException {
        ChargingEvent event = new ChargingEvent(station, null, 150, "slow");
        event.setEnergyToBeReceived(150);

        station.setUnitPrice(20);

        assertEquals(station.calculatePrice(event), 3000);

        double[] prices = {150, 25, 160};
        PricingPolicy policy = new PricingPolicy(2000, prices);
        station.setPricingPolicy(policy);

        assertEquals(station.calculatePrice(event), 22500);

        long[] spaces = {2500, 6000, 9000};
        policy = new PricingPolicy(spaces, prices);
        station.setPricingPolicy(policy);

        Thread.sleep(3500);

        assertEquals(station.calculatePrice(event), 3750);
    }
}
