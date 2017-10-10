package evlib.station;

import evlib.ev.Battery;
import evlib.ev.Driver;
import evlib.ev.ElectricVehicle;
import evlib.sources.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();
    private Lock lock3 = new ReentrantLock();
    private Lock lock4 = new ReentrantLock();
    private Lock lock5 = new ReentrantLock();
    private Lock lock6 = new ReentrantLock();
    private Lock lock7 = new ReentrantLock();
    private Lock lock8 = new ReentrantLock();
    public int FAST_CHARGERS;
    public int SLOW_CHARGERS;

    private class checkUpdate extends TimerTask {
        public void run() {
            updateStorage();
        }
    }

    public ChargingStation(String name, String[] kinds, String[] source, double[][] energyAmounts) {
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
        this.batteries = new ArrayList<>();
        for (int q = 0; q < source.length; q++)
            sources.add(source[q]);
        this.sources.add("DisCharging");
        setSpecificAmount("DisCharging", 0.0);
        this.chargingRatioSlow = 1;
        this.chargingRatioFast = 2;
        this.disChargingRatio = 1;
        this.inductiveChargingRatio = 0.5;
        for (int i = 0; i < source.length; i++) {
            switch (source[i]) {
                case "Solar":
                    n.add(i, new Solar(energyAmounts[i]));
                    setSpecificAmount("Solar", 0.0);
                    break;
                case "Wind":
                    n.add(i, new Wind(energyAmounts[i]));
                    setSpecificAmount("Wind", 0.0);
                    break;
                case "Geothermal":
                    n.add(i, new Geothermal(energyAmounts[i]));
                    setSpecificAmount("Geothermal", 0.0);
                    break;
                case "Wave":
                    n.add(i, new Wave(energyAmounts[i]));
                    setSpecificAmount("Wave", 0.0);
                    break;
                case "Hydroelectric":
                    n.add(i, new Hydroelectric(energyAmounts[i]));
                    setSpecificAmount("Hydroelectric", 0.0);
                    break;
                case "Nonrenewable":
                    n.add(i, new Nonrenewable(energyAmounts[i]));
                    setSpecificAmount("Nonrenewable", 0.0);
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
        for (int q = 0; q < source.length; q++)
            sources.add(source[q]);
        this.sources.add("DisCharging");
        setSpecificAmount("DisCharging", 0.0);
        this.chargingRatioSlow = 1;
        this.chargingRatioFast = 2;
        this.disChargingRatio = 1;
        this.inductiveChargingRatio = 0.5;
        for (int i = 0; i < source.length; i++) {
            switch (source[i]) {
                case "Solar":
                    n.add(i, new Solar());
                    setSpecificAmount("Solar", 0.0);
                    break;
                case "Wind":
                    n.add(i, new Wind());
                    setSpecificAmount("Wind", 0.0);
                    break;
                case "Geothermal":
                    n.add(i, new Geothermal());
                    setSpecificAmount("Geothermal", 0.0);
                    break;
                case "Wave":
                    n.add(i, new Wave());
                    setSpecificAmount("Wave", 0.0);
                    break;
                case "Hydroelectric":
                    n.add(i, new Hydroelectric());
                    setSpecificAmount("Hydroelectric", 0.0);
                    break;
                case "Nonrenewable":
                    n.add(i, new Nonrenewable());
                    setSpecificAmount("Nonrenewable", 0.0);
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
        setSpecificAmount("DisCharging", 0.0);
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
     * @return The id of the ChargingStation.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id for the ChargingStation.
     * @param id The id to be set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Adds a ChargingEvent to the corresponding waiting list.
     * @param event The ChargingEvent to be added.
     */
    public void updateQueue(ChargingEvent event) {
        lock2.lock();
        try {
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
        } finally {
            lock2.unlock();
        }
    }

    /**
     * Adds a DisChargingEvent in the waiting list.
     * @param event The DisChargingEvent to be added.
     */
    public void updateDisChargingQueue(DisChargingEvent event) {
        lock3.lock();
        try {
            discharging.add(event);
        } finally {
            lock3.unlock();
        }
    }

    /**
     * @return The WaitngList for fast charging.
     */
    public WaitList getFast() {
        return fast;
    }

    /**
     * @return The WaitingList for slow charging.
     */
    public WaitList getSlow() {
        return slow;
    }

    /**
     * @return The WaitingList for battery exchange.
     */
    public WaitList getExchange() {
        return exchange;
    }

    /**
     * @return The WaitingList for discharging.
     */
    public WaitList getDischarging() {
        return discharging;
    }

    /**
     * Looks for an empty Charger. If there is one, the event is assigned to it.
     * @param event The event that looks for a Charger.
     * @return The Charger that was assigned, or null if not any available Charger found.
     */
    public Charger assignCharger(ChargingEvent event) {
        lock4.lock();
        int i = 0;
        Charger ch = null;
        boolean flag = false;
        try {
            if (chargers.size() != 0)
                while (!flag && i < chargers.size()) {
                    if (event.getKindOfCharging().equals(chargers.get(i).getKindOfCharging()))
                        if (chargers.get(i).getChargingEvent() == null) {
                            chargers.get(i).setChargingEvent(event);
                            flag = true;
                            ch = chargers.get(i);
                        }
                    ++i;
                }
        } finally {
            lock4.unlock();
            return ch;
        }
    }

    /**
     * Looks for an empty DisCharger. If there is one, the event is assigned to it.
     * @param event The event that looks for a DisCharger.
     * @return The DisCharger that was assigned, or null if not any available DisCharger found.
     */
    public DisCharger assignDisCharger(DisChargingEvent event) {
        lock5.lock();
        int i = 0;
        DisCharger dsch = null;
        boolean flag = false;
        try {
            if (dischargers.size() != 0)
                while (!flag && i < dischargers.size()) {
                    if (dischargers.get(i).getDisChargingEvent() == null) {
                        dischargers.get(i).setDisChargingEvent(event);
                        flag = true;
                        dsch = dischargers.get(i);
                    }
                    ++i;
                }
        } finally {
            lock5.unlock();
            return dsch;
        }
    }

    /**
     * Looks for an empty ExchangeHandler. If there is one, the event is assigned to it.
     * @param event The event that looks for an ExchangeHandler.
     * @return The ExchangeHandler that was assigned, or null if not any available ExchangeHandler found.
     */
    public ExchangeHandler assignExchangeHandler(ChargingEvent event) {
        lock6.lock();
        int i = 0;
        ExchangeHandler ch = null;
        boolean flag = false;
        try {
            if (exchangeHandlers.size() != 0)
                while (!flag && i < exchangeHandlers.size()) {
                    if (exchangeHandlers.get(i).getChargingEvent() == null) {
                        exchangeHandlers.get(i).setChargingEvent(event);
                        flag = true;
                        ch = exchangeHandlers.get(i);
                    }
                    ++i;
                }
        } finally {
            lock6.unlock();
            return ch;
        }
    }

    /**
     * Looks for any empty ParkingSlot. If there is one, the event is assigned to it.
     * @param event The event that looks for a ParkingSlot.
     * @return The ParkingSLot that was assigned, or null if not any available ParkingSlot found.
     */
    public ParkingSlot assignParkingSlot(ParkingEvent event) {
        lock7.lock();
        int i = 0;
        ParkingSlot ch = null;
        boolean flag = false;
        try {
            if (parkingSlots.size() != 0)
                while (!flag && i < parkingSlots.size()) {
                    if (parkingSlots.get(i).getParkingEvent() == null) {
                        parkingSlots.get(i).setParkingEvent(event);
                        flag = true;
                        ch = parkingSlots.get(i);
                    }
                    ++i;
                }
        } finally {
            lock7.unlock();
            return ch;
        }
    }

    /**
     * Looks for any available Battery. If there is one and the remaining amount is greater than 0,
     * the battery is returned.
     * @return The assigned Battery, or null if no Battery found.
     */
    public Battery assignBattery() {
        lock8.lock();
        int i = 0;
        Battery bat = null;
        boolean flag = false;
        try {
            if (batteries.size() != 0)
                while (!flag && i < batteries.size()) {
                    if (batteries.get(i).getRemAmount() > 0) {
                        flag = true;
                        bat = batteries.remove(i);
                    }
                    ++i;
                }
        } finally {
            lock8.unlock();
            return bat;
        }
    }

    /**
     * @return Returns all the ExchangeHandler.
     */
    public ExchangeHandler[] getExchangeHandlers() {
        ExchangeHandler[] g = new ExchangeHandler[exchangeHandlers.size()];
        for (int i = 0; i < exchangeHandlers.size(); i++)
            g[i] = exchangeHandlers.get(i);
        return g;
    }

    /**
     * Adds a Charger to the ChargingStation.
     * @param charger The Charger to be added.
     */
    public void addCharger(Charger charger) {
        chargers.add(charger);
        if (charger.getKindOfCharging().equals("slow"))
            ++SLOW_CHARGERS;
        else if (charger.getKindOfCharging().equals("fast"))
            ++FAST_CHARGERS;
    }

    /**
     * Adds a Discharger to the ChargingStation.
     * @param discharger The DisCharger to be added.
     */
    public void addDisCharger(DisCharger discharger) {
        dischargers.add(discharger);
    }

    /**
     * @return Returns all the ParkingSlot.
     */
    public ParkingSlot[] getParkingSlots() {
        ParkingSlot[] g = new ParkingSlot[parkingSlots.size()];
        for (int i = 0; i < parkingSlots.size(); i++)
            g[i] = parkingSlots.get(i);
        return g;
    }

    /**
     * Inserts a ParkingSlot in the ChargingStation
     * @param slot The ParkingSlot to be added.
     */
    public void addParkingSlot(ParkingSlot slot)
    {
        parkingSlots.add(slot);
    }

    /**
     * Adds a new EnergySource to the ChargingStation.
     * @param source The EnergySource to be added.
     */
    public void addEnergySource(EnergySource source) {
        n.add(source);
        if (source instanceof Solar) {
            sources.add("Solar");
            setSpecificAmount("Solar", 0.0);
        } else if (source instanceof Wave) {
            sources.add("Wave");
            setSpecificAmount("Wave", 0.0);
        } else if (source instanceof Wind) {
            sources.add("Wind");
            setSpecificAmount("Wind", 0.0);
        } else if (source instanceof Hydroelectric) {
            sources.add("Hydroelectric");
            setSpecificAmount("Hydroelectric", 0.0);
        } else if (source instanceof Geothermal) {
            sources.add("Geothermal");
            setSpecificAmount("Geothermal", 0.0);
        } else if (source instanceof Nonrenewable) {
            sources.add("Nonrenewable");
            setSpecificAmount("Nonrenewable", 0.0);
        }
    }

    /**
     * Deletes an EnergySource from the ChargingStation.
     * @param source The EnergySource to be removed.
     */
    public void deleteEnergySource(EnergySource source) {
        n.remove(source);
        if (source instanceof Solar) {
            amounts.remove("Solar");
            sources.remove("Solar");
        } else if (source instanceof Wave) {
            amounts.remove("Wave");
            sources.remove("Wave");
        } else if (source instanceof Wind) {
            amounts.remove("Wind");
            sources.remove("Wind");
        } else if (source instanceof Hydroelectric) {
            amounts.remove("Hydroelectric");
            sources.remove("Hydroelectric");
        } else if (source instanceof Nonrenewable) {
            amounts.remove("Nonrenewable");
            sources.remove("Nonrenewable");
        } else if (source instanceof Geothermal) {
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
     * Inserts a new ExchangeHandler in the ChargingStation.
     * @param handler The ExchangeHandler to be added.
     */
    public void addExchangeHandler(ExchangeHandler handler) {
        exchangeHandlers.add(handler);
    }

    /**
     * Adds a Battery to the ChargingStation for the battery exchange function.
     * @param battery The Battery is going to be added.
     */
    public void joinBattery(Battery battery) {
        batteries.add(battery);
    }

    /**
     * Sorts the energies sources according to an order defined by the user.
     * @param energies It is a String array that defines the energies' order.
     */
    public void customEnergySorting(String[] energies) {
        sources.clear();
        for (int i = 0; i < energies.length; i++)
            sources.add(i, energies[i]);
    }

    /**
     * @return An array with the Battery for the battery exchange function.
     */
    public Battery[] getBatteries() {
        Battery[] g = new Battery[batteries.size()];
        batteries.forEach(bat -> g[batteries.indexOf(bat)] = bat);
        return g;
    }

    /**
     * Deletes a Battery from the batteries for the battery exchange function.
     * @param battery The battery to be removed.
     * @return True if the deletion was successfull, false if it was unsuccessfull.
     */
    public boolean deleteBattery(Battery battery) {
        return batteries.remove(battery);
    }

    /**
     * @return Returns an array with all the DisCharger of the ChargingStation.
     */
    public DisCharger[] getDisChargers() {
        DisCharger[] g = new DisCharger[dischargers.size()];
        for (int i = 0; i < dischargers.size(); i++)
            g[i] = dischargers.get(i);
        return g;
    }

    /**
     * @return An array with all the Charger.
     */
    public Charger[] getChargers() {
        Charger[] g = new Charger[chargers.size()];
        for (int i = 0; i < chargers.size(); i++)
            g[i] = chargers.get(i);
        return g;
    }

    /**
     * @return An array with all sources that give energy to the ChargingStation.
     */
    public String[] getSources() {
        String[] g = new String[sources.size()];
        for (int i = 0; i < sources.size(); i++)
            g[i] = sources.get(i);
        return g;
    }

    /**
     * @return A HashMap with the amounts of each energy source.
     */
    public HashMap<String, Double> getMap() {
        return amounts;
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
     * Sets an amount in a specific source.
     * @param source The source the energy will be added.
     * @param amount The amount of energy to be added.
     */
    public void setSpecificAmount(String source, double amount) {
        lock1.lock();
        try {
            amounts.put(source, amount);
        } finally {
            lock1.unlock();
        }
    }

    /**
     * @return The total energy of the ChargingStation.
     */
    public double getTotalEnergy() {
        double counter = 0;
        for (String energy: getSources())
            counter += getMap().get(energy);
        return counter;
    }

    /**
     * @return The slow charging ratio of the ChargingStation.
     */
    public double getChargingRatioSlow() {
        return chargingRatioSlow;
    }

    /**
     * Sets the charging ratio of the fast charging.
     * @param chargingRatio The fast charging ratio.
     */
    public void setChargingRatioFast(double chargingRatio) {
        chargingRatioFast = chargingRatio;
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
     * Sets a discharging ratio.
     * @param disChargingRatio The discharging ratio.
     */
    public void setDisChargingRatio(double disChargingRatio) {
        this.disChargingRatio = disChargingRatio;
    }

    /**
     * @return The fast charging ratio of the ChargingStation.
     */
    public double getChargingRatioFast() {
        return chargingRatioFast;
    }

    /**
     * Sets the ratio of inductive charging.
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
     * @return The discharging ratio of the ChargingStation.
     */
    public double getDisChargingRatio() {
        return disChargingRatio;
    }

    /**
     * @return The price of an energy unit for the inductive charging.
     */
    public double getInductivePrice() {
        return inductivePrice;
    }

    /**
     * Sets the price for an energy unit during the inductive charging.
     *
     * @param price The price of an energy unit.
     */
    public void setInductivePrice(double price) {
        inductivePrice = price;
    }

    /**
     * Searches for the EnergySource of the given source.
     * @param source The source the EnergySource object is asked.
     * @return The asking EnergySource.
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
     * @return The energy unit price of the ChargingStation.
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the default price for an energy unit. This price stands for each charging, given that
     * no PricingPolicy is linked with the ChargingStation.
     * @param price The default price for each energy unit.
     */
    public void setUnitPrice(double price) {
        this.unitPrice = price;
    }

    /**
     * @return The price of the energy unit for the discharging function.
     */
    public double getDisUnitPrice() {
        return disUnitPrice;
    }

    /**
     * Sets the default price for the energy unit for each discharging. The price always stands, since there is
     * no PricingPolicy for the discharging operation.
     * @param disUnitPrice The price for every energy unit.
     */
    public void setDisUnitPrice(double disUnitPrice) {
        this.disUnitPrice = disUnitPrice;
    }

    /**
     * Sets the price for a battery exchange.
     * @param price The price the exchange costs.
     */
    public void setExchangePrice(double price) {
        exchangePrice = price;
    }

    /**
     * @return The price of a battery exchange function.
     */
    public double getExchangePrice() {
        return exchangePrice;
    }

    /**
     * Sets the management of the WaitingList.
     * @param value The choice of queue handling's. If true the WaitingList is handled
     * automatic by the library. If false the waiting list should be handled manually.
     */
    public void setAutomaticQueueHandling(boolean value) {
        automaticQueueHandling = value;
    }

    /**
     * @return True if the WaitingList is handled automatic by the library.
     * False if the waiting list is handled manually.
     */
    public boolean getQueueHandling() {
        return automaticQueueHandling;
    }

    /**
     * @return The time among each energy storage update.
     */
    public int getUpdateSpace() {
        return updateSpace;
    }

    /**
     * Sets the space for the next energy storage update.
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
     * Checks the batteries which are for battery exchange to confirm which of them
     * need charging. Then, it charges as many as available Charger objects there are.
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

    /**
     * Calculates the waiting time the ElectricVehicle should wait.
     * @param kind The function for which the waiting time has to be calculated. The keywords are: "slow" for
     * slow charging, "fast" for fast charging, "exchange" for battery exchange function, "discharging" for
     * the discharging function.
     * @return The time an ElectricVehicle should wait, to be served.
     */
    public long getWaitingTime(String kind) {
        long[] counter1 = new long[chargers.size()];
        long[] counter2 = new long[exchangeHandlers.size()];
        long[] counter3 = new long[dischargers.size()];
        long min = 1000000000;
        int index = 1000000000;
        boolean accessed = false;
        if (Objects.equals("slow", kind)||Objects.equals("fast", kind))
            for (int i = 0; i < chargers.size(); i++) {
                if (Objects.equals(kind, chargers.get(i).getKindOfCharging())) {
                    if(chargers.get(i).getChargingEvent() != null) {
                        long diff = chargers.get(i).getChargingEvent().getRemainingChargingTime();
                        if (min > diff) {
                            min = diff;
                            index = i;
                            accessed = true;
                        }
                        counter1[i] = diff;
                    } else
                        return 0;
                }
            }
        else if(Objects.equals("exchange", kind))
            for (int i = 0; i<exchangeHandlers.size(); i++) {
                if (exchangeHandlers.get(i).getChargingEvent() != null) {
                    long diff = exchangeHandlers.get(i).getChargingEvent().getRemainingChargingTime();
                    if (min > diff) {
                        min = diff;
                        index = i;
                        accessed = true;
                    }
                    counter2[i] = diff;
                } else
                    return 0;
            }
        else if(Objects.equals("discharging", kind))
            for (int i = 0; i<dischargers.size(); i++) {
                if (dischargers.get(i).getDisChargingEvent() != null) {
                    long diff = dischargers.get(i).getDisChargingEvent().getRemainingDisChargingTime();
                    if (min > diff) {
                        min = diff;
                        index = i;
                        accessed = true;
                    }
                    counter3[i] = diff;
                } else
                    return 0;
            }
        else
            return 0;
        if (!accessed)
            return 0;
        ChargingEvent e;
        DisChargingEvent ey;
        if ("slow".equals(kind)) {
            WaitList o = this.slow;
            for (int i = 0; i < o.getSize() ; i++) {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/chargingRatioSlow));
                for(int j=0; j<chargers.size(); j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("fast".equals(kind)) {
            WaitList o = this.fast;
            for(int i = 0; i < o.getSize() ; i++) {
                e = (ChargingEvent) o.get(i);
                counter1[index] = counter1[index] + ((long) (e.getAmountOfEnergy()/chargingRatioFast));
                for(int j=0; j<chargers.size(); j++)
                    if ((counter1[j]<counter1[index])&&(counter1[j]!=0))
                        index = j;
            }
            return counter1[index];
        }
        if ("exchange".equals(kind)) {
            for(int i = 0; i < this.exchange.getSize(); i++) {
                counter2[index] = counter2[index] + timeOfExchange;
                for (int j = 0; j < exchangeHandlers.size(); j++)
                    if ((counter2[j]<counter2[index])&&(counter2[j]!=0))
                        index = j;
            }
            return counter2[index];
        }
        if ("discharging".equals(kind)) {
            WaitList o = this.discharging;
            for(int i = 0; i < o.getSize() ; i++) {
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
     * @return An array with the EnergySource objects of the ChargingStation.
     */
    public EnergySource[] getEnergySources() {
        EnergySource[] g = new EnergySource[n.size()];
        for (int i = 0; i < n.size(); i++)
            g[i] = n.get(i);
        return g;
    }

    /**
     * Sets the time a battery exchange function endures.
     * @param time The time the battery exchange endures.
     */
    public void setTimeofExchange(long time) {
        timeOfExchange = time;
    }

    /**
     * @return The duration of the battery exchange.
     */
    public long getTimeOfExchange() {
        return timeOfExchange;
    }

    /**
     * Updates the storage of the ChargingStation with the new amounts of energy for each source.
     * The amount of energy is subtracted from the energy inventory for each EnergySource.
     * For each source if the addition is non-zero, then in the next report will be a line.
     * The line shows the EnergySource, the given amount of energy and the date the addition was
     * made to the station.
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
                    setSpecificAmount("Solar", energy);
                } else if (getEnergySources()[j] instanceof Geothermal) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Geothermal, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Geothermal");
                    setSpecificAmount("Geothermal", energy);
                } else if (getEnergySources()[j] instanceof Nonrenewable) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Nonrenewable, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Nonrenewable");
                    setSpecificAmount("Nonrenewable", energy);
                } else if (getEnergySources()[j] instanceof Hydroelectric) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Hydroelectric, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Hydroelectric");
                    setSpecificAmount("Hydroelectric", energy);
                } else if (getEnergySources()[j] instanceof Wave) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Wave, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Wave");
                    setSpecificAmount("Wave", energy);
                } else if (getEnergySources()[j] instanceof Wind) {
                    Calendar calendar = Calendar.getInstance();
                    statistics.addEnergy("Wind, " + energy + ", " + dateFormat.format(calendar.getTime()));
                    energy += getSpecificAmount("Wind");
                    setSpecificAmount("Wind", energy);
                }
            }
        }
    }

    /**
     * @return The current standing price for the charging function.
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
     * Calculates the cost for a charging.
     * @param event The ChargingEvent to calculate the cost.
     * @return The cost of the charging.
     */
    public double calculatePrice(ChargingEvent event)
    {
        if (policy == null)
            if (!"exchange".equals(event.getKindOfCharging()))
                return event.getEnergyToBeReceived() * getUnitPrice();
            else
                return getExchangePrice();
        else if (policy.getDurationOfPolicy() < System.currentTimeMillis() - timestamp)
            if (!"exchange".equals(event.getKindOfCharging()))
                return event.getEnergyToBeReceived() * getUnitPrice();
            else
                return getExchangePrice();
        else {
            long diff = System.currentTimeMillis() - timestamp;
            if (policy.getSpace() != 0) {
                return event.getEnergyToBeReceived() * policy.getSpecificPrice((int) (diff / policy.getSpace()));
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
                return event.getEnergyToBeReceived() * policy.getSpecificPrice(counter);
            }
        }
    }

    /**
     * @return The PricingPolicy of the ChargingStation.
     */
    public PricingPolicy getPricingPolicy() {
        return policy;
    }

    /**
     * Links a PricingPolicy with the ChargingStation.
     * @param policy The policy to be linked with.
     */
    public void setPricingPolicy(PricingPolicy policy)
    {
        timestamp = System.currentTimeMillis();
        this.policy = policy;
    }

    /**
     * Sets the way the energy storage will become. If the update becomes automatically, then a Timer object
     * starts. The Timer calls the updateStorage() function every "updateSpace" milliseconds.
     * @param update The way the update will become. False means manually, true means automatic.
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
     * @return The way the energy storage updateis made. True if automatically, false for manually.
     */
    public boolean getUpdateMode() {
        return automaticUpdate;
    }

    /**
     * Generates a report with all the recent traffic in the charging station.
     * It also records the current situation of the station.
     * @param filePath The absolute path where the user wants to save the report. The file has to be .txt.
     */
    public void genReport(String filePath) {
        statistics.generateReport(filePath);
    }

    /**
     * @return True if the created threads are deamons, false if not.
     */
    public boolean getDeamon()
    {
        return deamon;
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
     * Sets if the created threads are deamons or not.
     * @param deamon The value to be set. True means deamon, false not deamons.
     */
    public void setDeamon(boolean deamon) {
        this.deamon = deamon;
    }
}