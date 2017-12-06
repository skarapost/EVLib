package evlib.station;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Charger {
    private int id;
    private final String kindOfCharging;
    private String name;
    private ChargingEvent e;
    private ChargingStation station;
    ArrayList<Integer> planEvent = new ArrayList<Integer>();
    ArrayList<Long> planTime = new ArrayList<Long>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private volatile Thread running;

    /**
     * Creates a new Charger instance.
     * @param station The ChargingStation object the Charger is linked with.
     * @param kindOfCharging The kind of charging the Charger supports.
     */
    public Charger(ChargingStation station, String kindOfCharging) {
        this.id = idGenerator.incrementAndGet();
        this.kindOfCharging = kindOfCharging;
        this.station = station;
        this.name = "Charger" + String.valueOf(id);
    }

    /**
     * Sets a name for the Charger.
     * @param name The name to be set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return The name of the Charger.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Executes the ChargingEvent. It lasts as much as ChargingEvent's charging time. When the charging time passes,
     * the condition of the ChargingEvent becomes "finished". The event is recorded in the history array.
     * The cost of the ChargingEvent is assigned to the Driver. The amount of energy to be given is added to the
     * battery's remaining amount. In the end, if the automatic queue's handling is activated the Charger checks
     * the waiting list.
     */
    public void startCharger() {
        running = new Thread(() -> {
            try {
                e.setChargingTime(e.getChargingTime());
                Thread.sleep(e.getChargingTime());
                if ((planEvent.size() == 0) && (planTime.size() == 0)) {
                    e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
                    if (e.getElectricVehicle().getDriver() != null)
                        e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + e.getCost());
                    if (e.getElectricVehicle().getDriver() == null && e.getElectricVehicle().getBrand() == null)
                        System.out.println("Charging " + e.getId() + ", " + e.getChargingStationName() + ", OK");
                    else
                        System.out.println("Charging " + e.getId() + ", " + e.getElectricVehicle().getDriver().getName() + ", " + e.getElectricVehicle().getBrand() + ", " + e.getChargingStationName() + ", OK");
                    e.setCondition("finished");
                    setChargingEvent(null);
                    if (station.getQueueHandling())
                        handleQueueEvents();
                } else {
                    if (!planEvent.contains(station.events.indexOf(e) + 1))
                        if (e.getKindOfCharging() != null) {
                            e.setCondition("finished");
                            e.setChargingTime(e.accumulatorOfChargingTime);
                        } else
                            e.setCondition("interrupted");
                    if (planEvent.size() != 0) {
                        if (planEvent.get(0) != -1) {
                            setChargingEvent(station.events.get(planEvent.remove(0) - 1));
                            e.setChargingTime(planTime.remove(0));
                            e.accumulatorOfChargingTime += e.getChargingTime();
                        } else {
                            ChargingEvent e = new ChargingEvent(station, null, 0, null);
                            e.setChargingTime(planTime.remove(0));
                            planEvent.remove(0);
                            setChargingEvent(e);
                        }
                    } else {
                        setChargingEvent(null);
                        planTime.clear();
                        planEvent.clear();
                        System.out.println(name + " plan, OK");
                        boolean flag = true;
                        for (int i : station.numberOfChargers)
                            if (station.getChargers()[i].getChargingEvent() != null)
                                flag = false;
                        if (flag) {
                            System.out.println("Plan, OK");
                            station.execEvents = false;
                            station.numberOfChargers.clear();
                            station.events.clear();
                        }
                    }
                }
            } catch (InterruptedException e1) {
                setChargingEvent(null);
                System.out.println(name + " stopped");
            } catch (NullPointerException e2) {
                System.out.println("not processed");
            }
        });
        if (station.getDeamon())
            running.setDaemon(true);
        running.setName("Charger" + String.valueOf(id));
        running.start();
    }

    /**
     * @return The kind of charging the Charger supports.
     */
    public String getKindOfCharging() {
        return kindOfCharging;
    }

    /**
     * Handles the waiting list. It executes (if any) the first element of the list.
     */
    private void handleQueueEvents() {
        ChargingEvent e;
        if ("fast".equalsIgnoreCase(getKindOfCharging())) {
            if (station.getFast().getSize() != 0) {
                e = (ChargingEvent) station.getFast().moveFirst();
                e.preProcessing();
                e.execution();
            }
        } else if ("slow".equalsIgnoreCase(getKindOfCharging())) {
            if (station.getSlow().getSize() != 0) {
                e = (ChargingEvent) station.getSlow().moveFirst();
                e.preProcessing();
                e.execution();
            }
        }
    }

    /**
     * @return The ChargingEvent that is linked with the Charger.
     */
    public ChargingEvent getChargingEvent() {
        return e;
    }

    /**
     * Sets a ChargingEvent to the Charger.
     *
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    void setChargingEvent(ChargingEvent ev) {
        this.e = ev;
    }

    /**
     * @return The id of the Charger.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for this Charger.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
