package EVLib.Station;

import EVLib.EV.Battery;
import EVLib.EV.Driver;
import EVLib.EV.ElectricVehicle;
import EVLib.Events.ChargingEvent;
import EVLib.Events.DisChargingEvent;
import EVLib.Events.ParkingEvent;
import EVLib.Events.PricingPolicy;
import EVLib.Sources.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChargingStation {
    private int id;
    private String name;
    private final WaitList slow;
    private final WaitList fast;
    private final WaitList discharging;
    private final WaitList exchange;
    private double chargingRatioSlow;
    private double chargingRatioFast;
    private double disChargingRatio;
    private final ArrayList<Charger> chargers;
    private final ArrayList<EnergySource> n;
    private ArrayList<DisCharger> dischargers;
    private final ArrayList<Battery> batteries;
    private final ArrayList<ExchangeHandler> exchangeHandlers;
    private final ArrayList<ParkingSlot> parkingSlots;
    private final HashMap<String, Double> amounts;
    private final ArrayList<String> sources;
    private double unitPrice;
    private double disUnitPrice;
    private double inductivePrice;
    private double exchangePrice;
    private boolean automaticQueueHandling;
    private int updateSpace;
    private long timeOfExchange;
    private double inductiveChargingRatio;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private long timestamp;
    private PricingPolicy policy;
    private boolean automaticUpdate;
    private final Statistics statistics = new Statistics();
    private Timer timer;
    private boolean deamon;
    public int FAST_CHARGERS;
    public int SLOW_CHARGERS;

    private class checkUpdate extends TimerTask {
        public void run() {
            updateStorage();
        }
    }

    public ChargingStation(String name, String[] kinds, String[] source, double[][] energAm) {
        this.amounts = new HashMap<>();
        this.id = idGenerator.incrementAndGet();
        this.name = name;
        this.automaticQueueHandling = true;
        this.slow = new WaitList<ChargingEvent>();
        this.fast = new WaitList<ChargingEvent>();
        this.exchange = new WaitList<ChargingEvent>();
        this.discharging = new WaitList<DisChargingEvent>();
        this.chargers = new ArrayList<>();
        this.dischargers = new ArrayList<>();
        this.exchangeHandlers = new ArrayList<>();
        this.parkingSlots = new ArrayList<>();
        this.n = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.sources.add("DisCharging");
        this.amounts.put("DisCharging", 0.0);
        this.batteries = new ArrayList<>();
        for (int q = 0; q < source.length; q++)
            sources.add(q, source[q]);
        this.chargingRatioSlow = 1;
        this.chargingRatioFast = 2;
        this.disChargingRatio = 1;
        this.inductiveChargingRatio = 0.5;
        for (int i = 0; i < source.length; i++) {
            switch (source[i]) {
                case "Solar":
                    n.add(i, new Solar(energAm[i]));
                    amounts.put("Solar", 0.0);
                    break;
                case "Wind":
                    n.add(i, new Wind(energAm[i]));
                    amounts.put("Wind", 0.0);
                    break;
                case "Geothermal":
                    n.add(i, new Geothermal(energAm[i]));
                    amounts.put("Geothermal", 0.0);
                    break;
                case "Wave":
                    n.add(i, new Wave(energAm[i]));
                    amounts.put("Wave", 0.0);
                    break;
                case "Hydroelectric":
                    n.add(i, new Hydroelectric(energAm[i]));
                    amounts.put("Hydroelectric", 0.0);
                    break;
                case "Nonrenewable":
                    n.add(i, new Nonrenewable(energAm[i]));
                    amounts.put("Nonrenewable", 0.0);
                    break;
            }
        }
        for (String kind : kinds) {
            switch (kind) {
                case "slow":
                case "fast":
                    chargers.add(new Charger(this, kind));
                    if (kind.equals("slow"))
                        ++SLOW_CHARGERS;
                    else
                        ++FAST_CHARGERS;
                    break;
                case "exchange":
                    exchangeHandlers.add(new ExchangeHandler(this));
                    break;
                case "park":
                    parkingSlots.add(new ParkingSlot(this));
                    break;
            }
        }
    }

    public ChargingStation(String name, String[] kinds, String[] source) {
        this.amounts = new HashMap<>();
        this.id = idGenerator.incrementAndGet();
        this.name = name;
        this.slow = new WaitList<ChargingEvent>();
        this.fast = new WaitList<ChargingEvent>();
        this.exchange = new WaitList<ChargingEvent>();
        this.discharging = new WaitList<DisChargingEvent>();
        this.automaticQueueHandling = true;
        this.chargers = new ArrayList<>();
        this.dischargers = new ArrayList<>();
        this.batteries = new ArrayList<>();
        this.exchangeHandlers = new ArrayList<>();
        this.parkingSlots = new ArrayList<>();
        this.n = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.sources.add("DisCharging");
        this.amounts.put("DisCharging", 0.0);
        for (int q = 0; q < source.length; q++)
            sources.add(q, source[q]);
        this.chargingRatioSlow = 1;
        this.chargingRatioFast = 2;
        this.disChargingRatio = 1;
        this.inductiveChargingRatio = 0.5;
        for (int i = 0; i < source.length; i++) {
            switch (source[i]) {
                case "Solar":
                    n.add(i, new Solar());
                    amounts.put("Solar", 0.0);
                    break;
                case "Wind":
                    n.add(i, new Wind());
                    amounts.put("Wind", 0.0);
                    break;
                case "Geothermal":
                    n.add(i, new Geothermal());
                    amounts.put("Geothermal", 0.0);
                    break;
                case "Wave":
                    n.add(i, new Wave());
                    amounts.put("Wave", 0.0);
                    break;
                case "Hydroelectric":
                    n.add(i, new Hydroelectric());
                    amounts.put("Hydroelectric", 0.0);
                    break;
                case "Nonrenewable":
                    n.add(i, new Nonrenewable());
                    amounts.put("Nonrenewable", 0.0);
                    break;
            }
        }
        for (String kind : kinds) {
            switch (kind) {
                case "slow":
                case "fast":
                    chargers.add(new Charger(this, kind));
                    break;
                case "exchange":
                    exchangeHandlers.add(new ExchangeHandler(this));
                    break;
                case "park":
                    parkingSlots.add(new ParkingSlot(this));
                    break;
            }
        }
    }

    public ChargingStation(String name) {
        this.id = idGenerator.incrementAndGet();
        this.name = name;
        this.slow = new WaitList<ChargingEvent>();
        this.fast = new WaitList<ChargingEvent>();
        this.exchange = new WaitList<ChargingEvent>();
        this.discharging = new WaitList<DisChargingEvent>();
        this.parkingSlots = new ArrayList<>();
        this.amounts = new HashMap<>();
        this.chargers = new ArrayList<>();
        this.dischargers = new ArrayList<>();
        this.batteries = new ArrayList<>();
        this.exchangeHandlers = new ArrayList<>();
        this.dischargers = new ArrayList<>();
        this.n = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.sources.add("DisCharging");
        this.amounts.put("DisCharging", 0.0);
        this.automaticQueueHandling = true;
        this.chargingRatioSlow = 1;
        this.chargingRatioFast = 2;
        this.disChargingRatio = 1;
        this.inductiveChargingRatio = 0.5;
    }

    /**
     * Sets a name to the ChargingStation.
     * @param name The name to be set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return The id of the ChargingStation object
     */
    public int getId() {
        return this.id;
    }

    /**
     * Adds a ChargingEvent in the corresponding waiting list.
     *
     * @param event The ChargingEvent that is going to be added.
     */
    public void updateQueue(ChargingEvent event) {
        switch (event.getKindOfCharging()) {
            case "exchange":
                exchange.add(event);
                break;
            case "slow":
                slow.add(event);
                break;
            case "fast":
                fast.add(event);
                break;
        }
    }

    /**
     * Adds a DisChargingEvent in the waiting list.
     * @param event The DisChargingEvent that is going to be added.
     */
    public void updateDisChargingQueue(DisChargingEvent event) {
        discharging.add(event);
    }

    /**
     * @return The WaitngList object for the fast charging.
     */
    public WaitList getFast() {
        return fast;
    }

    /**
     * @return The WaitingList object for the slow charging.
     */
    public WaitList getSlow() {
        return slow;
    }

    /**
     * @return The WaitingList object for the exchange battery.
     */
    public WaitList getExchange() {
        return exchange;
    }

    /**
     * @return The WaitingList object for the discharging.
     */
    public WaitList getDischarging() {
        return discharging;
    }

    /**
     * Checks for empty Charger according to the kind that is given.
     *
     * @param k The kind of Charger that is asked.
     * @return Returns the id of the Charger in case there is any empty Charger
     * object, -2 if the charging station is not linked with any Charger object or -1 if all the Charger are busy.
     */
    public int checkChargers(String k) {
        for (int i = 0; i < getChargers().length; i++) {
            if (k.equals(getChargers()[i].getKindOfCharging()))
                if (getChargers()[i].getChargingEvent() == null)
                    return getChargers()[i].getId();
        }
        if (getChargers().length == 0)
            return -2;
        return -1;
    }

    /**
     * Checks for any empty Discharger.
     *
     * @return The id of the empty DisCharger, or -1 if there is not any empty,
     * or -2 if the charging station is not linked with any DisCharger object
     */
    public int checkDisChargers() {
        for (int i = 0; i < getDisChargers().length; i++) {
            if (getDisChargers()[i].getDisChargingEvent() == null)
                return getDisChargers()[i].getId();
        }
        if (getDisChargers().length == 0)
            return -2;
        return -1;
    }

    /**
     * Checks for any empty exchange slot.
     *
     * @return The number of the slot, -1 if there is not any empty slot,
     * or -2 if the charging station is not linked with any ExchangeHandler object.
     */
    public int checkExchangeHandlers() {
        for (int i = 0; i < getExchangeHandlers().length; i++) {
            if (getExchangeHandlers()[i].getChargingEvent() == null)
                return getExchangeHandlers()[i].getId();
        }
        if (getExchangeHandlers().length == 0)
            return -2;
        return -1;
    }

    /**
     * Checks for any empty ParkingSlot.
     *
     * @return The id of the ParkingSlot, -1 if there is not any empty slot, or -2 if the ChargingStation
     * is not linked with any ParkingSlot.
     */
    public int checkParkingSlots() {
        for (ParkingSlot p : getParkingSlots()) {
            if (p.getParkingEvent() == null)
                return p.getId();
        }
        if (parkingSlots.size() == 0)
            return -2;
        return -1;
    }

    /**
     * @return Returns the position of the Battery, or -1 if there is not any Battery.
     */
    public int checkBatteries() {
        for (int i = 0; i < getBatteries().size(); i++) {
            if (getBatteries().get(i).getRemAmount() != 0)
                return i;
        }
        if (getBatteries().size() == 0)
            return -2;
        return -1;
    }


    /**
     * @return Returns all the ExchangeHandler objects.
     */
    public ExchangeHandler[] getExchangeHandlers() {
        ExchangeHandler[] g = new ExchangeHandler[exchangeHandlers.size()];
        for (int i = 0; i < exchangeHandlers.size(); i++)
            g[i] = exchangeHandlers.get(i);
        return g;
    }

    /**
     * @return Returns all the ParkingSlot objects.
     */
    public ParkingSlot[] getParkingSlots() {
        ParkingSlot[] g = new ParkingSlot[parkingSlots.size()];
        for (int i = 0; i < parkingSlots.size(); i++)
            g[i] = parkingSlots.get(i);
        return g;
    }

    /**
     * Adds a Charger to the ChargingStation.
     *
     * @param y The Charger to be added.
     */
    public void addCharger(Charger y) {
        chargers.add(y);
        if(y.getKindOfCharging().equals("slow"))
            ++SLOW_CHARGERS;
        else if(y.getKindOfCharging().equals("fast"))
            ++FAST_CHARGERS;
    }

    /**
     * Adds a Discharger to the ChargingStation.
     *
     * @param y The DisCharger to be added.
     */
    public void addDisCharger(DisCharger y) {
        dischargers.add(y);
    }

    /**
     * Inserts a new ExchangeHandler in the charging station.
     *
     * @param y The ExchangeHandler object to be added.
     */
    public void addExchangeHandler(ExchangeHandler y) {
        exchangeHandlers.add(y);
    }

    /**
     * Inserts a ParkingSlot in the ChargingStation
     * @param y The ParkingSlot to be added.
     */
    public void addParkingSlot(ParkingSlot y)
    {
        parkingSlots.add(y);
    }

    /**
     * Adds a new EnergySource to the ChargingStation.
     *
     * @param z The EnergySource is going to be added.
     */
    public void addEnergySource(EnergySource z) {
        n.add(z);
        if (z instanceof Solar) {
            sources.add("Solar");
            amounts.put("Solar", 0.0);
        } else if (z instanceof Wave) {
            sources.add("Wave");
            amounts.put("Wave", 0.0);
        } else if (z instanceof Wind) {
            sources.add("Wind");
            amounts.put("Wind", 0.0);
        } else if (z instanceof Hydroelectric) {
            sources.add("Hydroelectric");
            amounts.put("Hydroelectric", 0.0);
        } else if (z instanceof Geothermal) {
            sources.add("Geothermal");
            amounts.put("Geothermal", 0.0);
        } else if (z instanceof Nonrenewable) {
            sources.add("Nonrenewable");
            amounts.put("Nonrenewable", 0.0);
        }
    }

    /**
     * Deletes an EnergySource from the ChargingStation.
     *
     * @param z The EnergySource is going to be removed.
     */
    public void deleteEnergySource(EnergySource z) {
        n.remove(z);
        if (z instanceof Solar) {
            amounts.remove("Solar");
            sources.remove("Solar");
        } else if (z instanceof Wave) {
            amounts.remove("Wave");
            sources.remove("Wave");
        } else if (z instanceof Wind) {
            amounts.remove("Wind");
            sources.remove("Wind");
        } else if (z instanceof Hydroelectric) {
            amounts.remove("Hydroelectric");
            sources.remove("Hydroelectric");
        } else if (z instanceof Nonrenewable) {
            amounts.remove("Nonrenewable");
            sources.remove("Nonrenewable");
        } else if (z instanceof Geothermal) {
            amounts.remove("Geothermal");
            sources.remove("Geothermal");
        }
    }

    /**
     * Removes a specific Charger.
     * @param charger The Charger to be removed.
     */
    public void deleteCharger(Charger charger)
    {
        chargers.remove(charger);
    }

    /**
     * Removes a specific DisCharger.
     * @param disCharger The DisCharger to be removed.
     */
    public void deleteDisCharger(DisCharger disCharger)
    {
        dischargers.remove(disCharger);
    }

    /**
     * Removes a specific ExchangeHandler.
     * @param exchangeHandler The ExchangeHandler to be removed.
     */
    public void deleteExchangeHandler(ExchangeHandler exchangeHandler)
    {
        exchangeHandlers.remove(exchangeHandler);
    }

    /**
     * Removes a specific ParkingSlot.
     * @param parkingSlot The ParkingSLot to be removed.
     */
    public void deleteParkingSlot(ParkingSlot parkingSlot)
    {
        parkingSlots.remove(parkingSlot);
    }

    /**
     * Sorts the energies sources according to the desire of the user.
     *
     * @param energies It is a String array that defines the energies order.
     */
    public void customEnergySorting(String[] energies) {
        sources.clear();
        for (int i = 0; i < energies.length; i++)
            sources.add(i, energies[i]);
    }

    /**
     * Adds a Battery to the ChargingStation for the battery exchange function.
     *
     * @param battery The Battery is going to be added.
     */
    public void joinBattery(Battery battery) {
        batteries.add(battery);
    }

    /**
     * @return A ArrayList with the Battery objects.
     */
    public ArrayList<Battery> getBatteries() {
        return batteries;
    }

    /**
     * Deletes a Battery from the batteries for the battery exchange function.
     *
     * @param battery The battery that will be removed.
     * @return True if the deletion was successfull, false if it was unsuccessfull.
     */
    public boolean deleteBattery(Battery battery) {
        return batteries.remove(battery);
    }

    /**
     * @return Returns the DisCharger objects of the ChargingStation.
     */
    public DisCharger[] getDisChargers() {
        DisCharger[] g = new DisCharger[dischargers.size()];
        for (int i = 0; i < dischargers.size(); i++)
            g[i] = dischargers.get(i);
        return g;
    }

    /**
     * Search for the Charger based on the given id.
     *
     * @param id The id of the Charger which is asked.
     * @return The Charger object.
     */
    public Charger searchCharger(int id) {
        Charger y = null;
        for (Charger charger : chargers) {
            if (charger.getId() == id)
                y = charger;
        }
        return y;
    }

    /**
     * Search for a DisCharger object based on the given id.
     *
     * @param id The id of the DisCharger object which is asked.
     * @return The DisCharger object.
     */
    public DisCharger searchDischarger(int id) {
        DisCharger y = null;
        for (DisCharger discharger : dischargers) {
            if (discharger.getId() == id)
                y = discharger;
        }
        return y;
    }

    /**
     * Search for an ExchangeHandler object based on the given id.
     *
     * @param id The id of the ExchangeHandler object which is asked.
     * @return The ExchangeHandler object.
     */
    public ExchangeHandler searchExchangeHandler(int id) {
        ExchangeHandler y = null;
        for (ExchangeHandler exchangeHandler : exchangeHandlers) {
            if (exchangeHandler.getId() == id)
                y = exchangeHandler;
        }
        return y;
    }

    /**
     * Search for an ParkingSlot object based on the given id.
     *
     * @param id The id of the ParkingSlot object which is asked.
     * @return The ParkingSlot object.
     */
    public ParkingSlot searchParkingSlot(int id) {
        ParkingSlot y = null;
        for (ParkingSlot parkingSlot : parkingSlots) {
            if (parkingSlot.getId() == id)
                y = parkingSlot;
        }
        return y;
    }

    /**
     * @return The array with the Charger objects.
     */
    public Charger[] getChargers() {
        Charger[] g = new Charger[chargers.size()];
        for (int i = 0; i < chargers.size(); i++)
            g[i] = chargers.get(i);
        return g;
    }

    /**
     * @return The array with the kind of energies.
     */
    public String[] getSources() {
        String[] g = new String[sources.size()];
        for (int i = 0; i < sources.size(); i++)
            g[i] = sources.get(i);
        return g;
    }

    /**
     * @return A HashMap object with the amounts of each kind of energy.
     */
    public HashMap<String, Double> getMap() {
        return amounts;
    }

    /**
     * Sets an amount of energy in a specific kind of energy.
     * @param source The kind of energy in which the energy will be added.
     * @param amount The amount of energy will be added.
     */
    public synchronized void setSpecificAmount(String source, double amount) {
        amounts.put(source, amount);
    }

    /**
     * @param source The source of energy.
     * @return The energy of the source.
     */
    public double getSpecificAmount(String source) {
        if (!amounts.containsKey(source))
            return 0.0;
        return amounts.get(source);
    }

    /**
     * @return The total energy of this ChargingStation.
     */
    public double getTotalEnergy() {
        double counter = 0;
        for (String energy: getSources())
            counter += getMap().get(energy);
        return counter;
    }

    /**
     * Sets a charging ratio for the slow charging.
     *
     * @param chargingRatio The charging ratio.
     */
    public void setChargingRatioSlow(double chargingRatio) {
        chargingRatioSlow = chargingRatio;
    }

    /**
     * @return The slow charging ratio of this ChargingStation.
     */
    public double getChargingRatioSlow() {
        return chargingRatioSlow;
    }

    /**
     * Sets the charging ratio of the fast charging.
     *
     * @param chargingRatio The fast charging ratio.
     */
    public void setChargingRatioFast(double chargingRatio) {
        chargingRatioFast = chargingRatio;
    }

    /**
     * @return The fast charging ratio of this ChargingStation.
     */
    public double getChargingRatioFast() {
        return chargingRatioFast;
    }

    /**
     * Sets a discharging ratio.
     *
     * @param disChargingRatio The discharging ratio.
     */
    public void setDisChargingRatio(double disChargingRatio) {
        this.disChargingRatio = disChargingRatio;
    }

    /**
     * @return The discharging ratio of this ChargingStation.
     */
    public double getDisChargingRatio() {
        return disChargingRatio;
    }

    /**
     * Sets the ratio of inductive charging.
     *
     * @param inductiveChargingRatio The ratio of charging during inductive charging.
     */
    public void setInductiveChargingRatio(double inductiveChargingRatio) {
        this.inductiveChargingRatio = inductiveChargingRatio;
    }

    /**
     * @return The ratio of charging during inductive charging.
     */
    public double getInductiveRatio() {
        return inductiveChargingRatio;
    }

    /**
     * Sets the price of an energy unit for the inductive charging.
     * @param price The price of an energy unit.
     */
    public void setInductivePrice(double price)
    {
        inductivePrice = price;
    }

    /**
     * @return The price of an energy unit for the inductive charging.
     */
    public double getInductivePrice()
    {
        return inductivePrice;
    }

    /**
     * Searches for the EnergySource of the given source.
     *
     * @param source The source for which the EnergySource object is asked.
     * @return An EnergySource object.
     */
    public EnergySource getEnergySource(String source) {
        if ("Solar".equals(source)) {
            for (EnergySource aN : n)
                if (aN instanceof Solar)
                    return aN;
        }
        else if ("Wind".equals(source)) {
            for (EnergySource aN : n)
                if (aN instanceof Wind)
                    return aN;
        }
        else if ("Wave".equals(source)) {
            for (EnergySource aN : n)
                if (aN instanceof Wave)
                    return aN;
        }
        else if ("Hydroelectric".equals(source)) {
            for (EnergySource aN : n)
                if (aN instanceof Hydroelectric)
                    return aN;
        }
        else if ("Geothermal".equals(source)) {
            for (EnergySource aN : n)
                if (aN instanceof Geothermal)
                    return aN;
        }
        else if ("Nonrenewable".equals(source)) {
            for (EnergySource aN : n)
                if (aN instanceof Nonrenewable)
                    return aN;
        }
        return null;
    }

    /**
     * Sets a price for the energy unit.
     *
     * @param price The price.
     */
    public void setUnitPrice(double price) {
        this.unitPrice = price;
    }

    /**
     * @return The price of the energy unit of this ChargingStation.
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets a price for the energy unit in a DischargingEvent.
     *
     * @param disUnitPrice The price of energy unit.
     */
    public void setDisUnitPrice(double disUnitPrice) {
        this.disUnitPrice = disUnitPrice;
    }

    /**
     * @return The price of the energy unit.
     */
    public double getDisUnitPrice() {
        return disUnitPrice;
    }

    /**
     * @return The price of a battery exchange.
     */
    public double getExchangePrice() {
        return exchangePrice;
    }

    /**
     * Sets the price for a battery exchange.
     *
     * @param price The price the exchange costs.
     */
    public void setExchangePrice(double price) {
        exchangePrice = price;
    }

    /**
     * Adjust the management of the WaitingList.
     *
     * @param value The choice of queue handling's. If true the WaitingList is handled
     *              automatic by the library. If false the user have to handle the WaitingList.
     */
    public void setAutomaticQueueHandling(boolean value) {
        automaticQueueHandling = value;
    }

    /**
     * @return True if the WaitingList is handled automatic by the library.
     * False if the user has to handle the WaitingList.
     */
    public boolean getQueueHandling() {
        return automaticQueueHandling;
    }

    /**
     * Sets the space which will be among two storage's updates.
     *
     * @param updateSpace The time space.
     */
    public void setUpdateSpace(int updateSpace) {
        if(timer != null) {
            timer.cancel();
            timer.purge(); }
        if(getUpdateMode() && updateSpace != 0) {
            this.updateSpace = updateSpace;
            timer = new Timer(true);
            timer.schedule(new checkUpdate(), 0, this.updateSpace); }
        else
            this.updateSpace = 0;
    }

    /**
     * @return The time among each storage update.
     */
    public int getUpdateSpace() {
        return updateSpace;
    }

    /**
     * Checks the batteries which are for battery exchange to confirm which of them
     * need charging. Then, it charges as the empty Chargers.
     * @param kind The kind of charging the user wants to charge the batteries.
     **/
    public void batteriesCharging(String kind) {
        ChargingEvent e;
        ElectricVehicle r;
        Driver driver;
        for (Battery battery : batteries)
            if (battery.getRemAmount() < battery.getCapacity()) {
                r = new ElectricVehicle("Station");
                r.setBattery(battery);
                driver = new Driver("Station");
                r.setDriver(driver);
                e = new ChargingEvent(this, r, battery.getCapacity() - battery.getRemAmount(), kind);
                e.preProcessing();
                e.execution();
            }
    }

    public long getWaitingTime(String kind)
    {
        long[] counter1 = new long[chargers.size()];
        long[] counter2 = new long[exchangeHandlers.size()];
        long[] counter3 = new long[dischargers.size()];
        long min = 1000000000;
        int index = 1000000000;
        if (Objects.equals("slow", kind)||Objects.equals("fast", kind))
            for (int i = 0; i < chargers.size(); i++) {
                if (Objects.equals(kind, chargers.get(i).getKindOfCharging())) {
                    if(chargers.get(i).getChargingEvent() != null) {
                        long diff = chargers.get(i).getChargingEvent().getRemainingChargingTime();
                        if (min > diff) {
                            min = diff;
                            index = i;
                        }
                        counter1[i] = diff;
                    }
                    else
                        return 0;
                }
            }
        else if(Objects.equals("exchange", kind))
            for (int i = 0; i<exchangeHandlers.size(); i++)
            {
                if (exchangeHandlers.get(i).getChargingEvent() != null) {
                    long diff = exchangeHandlers.get(i).getChargingEvent().getRemainingChargingTime();
                    if (min > diff) {
                        min = diff;
                        index = i;
                    }
                    counter2[i] = diff;
                }
                else
                    return 0;
            }
        else if(Objects.equals("discharging", kind))
            for (int i = 0; i<dischargers.size(); i++)
            {
                if (dischargers.get(i).getDisChargingEvent() != null) {
                    long diff = dischargers.get(i).getDisChargingEvent().getRemainingDisChargingTime();
                    if (min > diff) {
                        min = diff;
                        index = i;
                    }
                    counter3[i] = diff;
                }
                else
                    return 0;
            }
        else
            return 0;
        ChargingEvent e;
        DisChargingEvent ey;
        if ("slow".equals(kind))
        {
            WaitList o = this.slow;
            for (int i = 0;i < o.getSize() ;i++)
            {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/chargingRatioSlow));
                for(int j=0; j<chargers.size(); j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("fast".equals(kind))
        {
            WaitList o = this.fast;
            for(int i = 0; i < o.getSize() ;i++)
            {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/chargingRatioFast));
                for(int j=0; j<chargers.size(); j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("exchange".equals(kind))
        {
            for(int i = 0; i < this.exchange.getSize();i++)
            {
                counter2[index] = counter2[index] + timeOfExchange;
                for(int j=0; j < chargers.size(); j++)
                    if ((counter2[j]<counter2[index])&&(counter2[j]!=0))
                        index = j;
            }
            return counter2[index];
        }
        if ("discharging".equals(kind))
        {
            WaitList o = this.discharging;
            for(int i = 0; i < o.getSize() ;i++)
            {
                ey = (DisChargingEvent) o.get(i);
                counter3[index] = counter3[index] + ((long) (ey.getAmountOfEnergy()/disChargingRatio));
                for(int j=0; j<dischargers.size(); j++)
                    if ((counter3[j]<counter3[index])&&(counter3[j]!=0))
                        index = j;
            }
            return counter3[index];
        }
        return 0;
    }

    /**
     * @return The array with the EnergySource objects of the ChargingStation.
     */
    public EnergySource[] getEnergySources() {
        EnergySource[] g = new EnergySource[n.size()];
        for (int i = 0; i < n.size(); i++)
            g[i] = n.get(i);
        return g;
    }

    /**
     * Sets the time a battery exchange service lasts.
     *
     * @param time The time the battery exchange lasts.
     */
    public void setTimeofExchange(long time) {
        timeOfExchange = time;
    }

    /**
     * @return The time of the battery exchange.
     */
    public long getTimeOfExchange() {
        return timeOfExchange;
    }

    /**
     * Update the storage of the ChargingStation with the new amounts of energy
     * of each source.
     */
    public void updateStorage() {
        double energy;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (int j = 0; j < getEnergySources().length; j++) {
            energy = getEnergySources()[j].popAmount();
            if (energy != 0) {
                if (getEnergySources()[j] instanceof Solar) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Solar, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Solar");
                    amounts.put("Solar", energy);
                } else if (getEnergySources()[j] instanceof Geothermal) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Geothermal, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Geothermal");
                    amounts.put("Geothermal", energy);
                } else if (getEnergySources()[j] instanceof Nonrenewable) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Nonrenewable, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Nonrenewable");
                    amounts.put("Nonrenewable", energy);
                } else if (getEnergySources()[j] instanceof Hydroelectric) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Hydroelectric, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Hydroelectric");
                    amounts.put("Hydroelectric", energy);
                } else if (getEnergySources()[j] instanceof Wave) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Wave, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Wave");
                    amounts.put("Wave", energy);
                } else if (getEnergySources()[j] instanceof Wind) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Wind, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Wind");
                    amounts.put("Wind", energy);
                }
            }
        }
    }


    /**
     * @return The price per energy unit a ChargingEvent will be charged.
     */
    public double getCurrentPrice()
    {
        double diff = System.currentTimeMillis() - timestamp;
        if (getPricingPolicy() == null)
            return unitPrice;
        else if (diff > policy.getDurationOfPolicy())
            return unitPrice;
        else
            if(policy.getSpace() != 0)
                return policy.getSpecificPrice((int) (diff / policy.getSpace()));
            else
            {
                double accumulator = 0;
                int counter = 0;
                while (accumulator <= diff) {
                    accumulator += policy.getSpecificTimeSpace(counter);
                    if (accumulator <= diff)
                        counter++;
                }
                return policy.getSpecificPrice(counter);
            }
    }

    /**
     * Calculates the cost of a charging.
     *
     * @param w The ChargingEvent that executed.
     * @return The cost of the charging.
     */
    public double calculatePrice(ChargingEvent w)
    {
        if (policy == null)
            if (!"exchange".equals(w.getKindOfCharging()))
                return w.getEnergyToBeReceived() * getUnitPrice();
            else
                return getExchangePrice();
        else if (policy.getDurationOfPolicy() < System.currentTimeMillis() - timestamp)
            if (!"exchange".equals(w.getKindOfCharging()))
                return w.getEnergyToBeReceived() * getUnitPrice();
            else
                return getExchangePrice();
        else {
            long diff = System.currentTimeMillis() - timestamp;
            if (policy.getSpace() != 0) {
                return w.getEnergyToBeReceived() * policy.getSpecificPrice((int) (diff / policy.getSpace()));
            }
            else {
                double accumulator = 0;
                int counter = 0;
                while (accumulator <= diff)
                {
                    accumulator += policy.getSpecificTimeSpace(counter);
                    if (accumulator <= diff)
                        counter++;
                }
                return w.getEnergyToBeReceived() * policy.getSpecificPrice(counter);
            }
        }
    }

    /**
     * Calculates the cost of a parking.
     *
     * @param w The ParkingEvent that executed.
     * @return The cost of the charging.
     */
    public double calculatePrice(ParkingEvent w) {
        return w.getEnergyToBeReceived() * getInductivePrice();
    }

    /**
     * Links a pricing policy with the charging station.
     *
     * @param policy The policy to be linked with.
     */
    public void setPricingPolicy(PricingPolicy policy)
    {
        timestamp = System.currentTimeMillis();
        this.policy = policy;
    }

    /**
     * @return The PricingPolicy of the Charging Station.
     */
    public PricingPolicy getPricingPolicy() {
        return policy;
    }

    /**
     * Sets the update of the energy storage will become either automatic or by the user.
     * @param update The way the update will become. False means by the user, true means automatic.
     */
    public void setAutomaticUpdateMode(boolean update) {
        if (!update && !this.automaticUpdate)
            this.automaticUpdate = false;
        else if(!update)
        {
            this.automaticUpdate = false;
            if(timer != null) {
                timer.cancel();
                timer.purge();
            }
        }
        else
            this.automaticUpdate = true;
    }

    /**
     * @return The update mode of the energy storage. True for automatic, false for not automatic.
     */
    public boolean getUpdateMode() {
        return automaticUpdate;
    }

    /**
     * Generates a report with all the recent traffic in the charging station. It also records the current situation of the station.
     *
     * @param filePath The absolute path where the user wants to save the report. The file has to be .txt.
     */
    public void genReport(String filePath) {
        statistics.generateReport(filePath);
    }

    /**
     * @return If the created threads are deamons or not.
     */
    public boolean getDeamon()
    {
        return deamon;
    }

    /**
     * Sets either the created threads are going to be deamons or not.
     * @param deamon The value to be set.
     */
    public void setDeamon(boolean deamon)
    {
        this.deamon = deamon;
    }

    /**
     * @return The name of the ChargingStation.
     */
    public String getName()
    {
        return name;
    }

    private class Statistics {
        private final List<String> energyLog;

        Statistics() {
            energyLog = new ArrayList<>();
        }

        void addEnergy(String energy) {
            energyLog.add(energy);
        }

        void generateReport(String filePath) {
            List<String> content = new ArrayList<>();
            content.add("***********************************");
            content.add("");
            content.add("Id: " + id);
            content.add("Name: " + name);
            content.add("Remaining energy: " + getTotalEnergy());
            content.add("Number of chargers: " + getChargers().length);
            content.add("Number of dischargers: " + getDisChargers().length);
            content.add("Number of exchange handlers: " + getExchangeHandlers().length);
            content.add("Number of parking slots: " + getParkingSlots().length);
            content.add("Number of chargings: " + ChargingEvent.chargingLog.size());
            content.add("Number of dischargings: " + DisChargingEvent.dischargingLog.size());
            content.add("Number of battery swappings: " + ChargingEvent.exchangeLog.size());
            content.add("Number of vehicles waiting for fast charging: " + fast.getSize());
            content.add("Number of vehicles waiting for slow charging: " + slow.getSize());
            content.add("Number of vehicles waiting for discharging: " + discharging.getSize());
            content.add("Number of vehicles waiting for battery swapping: " + exchange.getSize());
            content.add("Energy sources: ");
            for (String s : getSources())
                content.add("  " + s + ": " + getSpecificAmount(s));
            content.add("");
            content.add("***Charging events***");
            for (ChargingEvent ev : ChargingEvent.chargingLog) {
                content.add("");
                content.add("Electric vehicle: " + ev.getElectricVehicle().getBrand());
                content.add("Energy: " + ev.getEnergyToBeReceived());
                content.add("Charging time: " + ev.getChargingTime());
                content.add("Waiting time: " + ev.getMaxWaitingTime());
                content.add("Cost: " + ev.getCost());
            }
            content.add("");
            content.add("***DisCharging events***");
            for (DisChargingEvent ev : DisChargingEvent.dischargingLog) {
                content.add("");
                content.add("Electric vehicle: " + ev.getElectricVehicle().getBrand());
                content.add("Energy: " + ev.getAmountOfEnergy());
                content.add("Charging time: " + ev.getDisChargingTime());
                content.add("Waiting time: " + ev.getMaxWaitingTime());
                content.add("Profit: " + ev.getProfit());
            }
            content.add("");
            content.add("***Exchange events***");
            for (ChargingEvent ev : ChargingEvent.exchangeLog) {
                content.add("");
                content.add("Electric vehicle: " + ev.getElectricVehicle().getBrand());
                content.add("Waiting time: " + ev.getMaxWaitingTime());
                content.add("Cost: " + ev.getCost());
            }
            content.add("");
            content.add("***Energy additions***");
            for(String s: energyLog) {
                content.add("");
                content.add(s);
            }
            content.add("");
            content.add("***********************************");
            Writer writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
                for (String line : content) {
                    line += System.getProperty("line.separator");
                    writer.write(line);
                }
            } catch (IOException ignored) {

            }
            finally {
                if (writer != null)
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    /**
     * Sets the id for this ChargingStation.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}