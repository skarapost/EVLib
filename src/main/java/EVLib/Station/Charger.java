package EVLib.Station;

import EVLib.Events.ChargingEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class Charger {
    private int id;
    private String kindOfCharging;
    private boolean busy;
    private long commitTime;
    private ChargingEvent e;
    private ChargingStation station;
    private long timestamp;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public Charger(ChargingStation station, String kindOfCharging) {
        this.id = idGenerator.incrementAndGet();
        this.kindOfCharging = kindOfCharging;
        this.busy = false;
        this.station = station;
        this.e = null;
    }

    /**
     * Executes the ChargingEvent. It lasts as much as ChargingEvent's charging time demands.
     * The energy that the ChargingEvent needs is subtracted from the total energy of the ChargingStation.
     * The condition of charging event gets finished. At the end if the automatic queue's handling is activated,
     * the Charger checks the waiting list.
     */
    public void executeChargingEvent() {
        Thread ch = new Thread(() ->
        {
            try {
                Thread.sleep(e.getChargingTime());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            synchronized (this) {
                e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
                if (e.getElectricVehicle().getDriver() != null)
                    e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + e.getCost());
                System.out.println("The charging " + e.getId() + " completed succesfully");
                e.setCondition("finished");
                changeSituation();
                setChargingEvent(null);
                commitTime = 0;
                ChargingEvent.chargingLog.add(e);
            }
            if (station.getQueueHandling())
                handleQueueEvents();
        });
        if(station.getDeamon())
            ch.setDaemon(true);
        ch.start();
    }


    /**
     * Changes the situation of the Charger.
     */
    public void changeSituation() {
        this.busy = !busy;
    }

    /**
     * @return True if it is busy, false if it is not busy.
     */
    public boolean getBusy() {
        return busy;
    }

    /**
     * @return The kind of charging the Charger supports.
     */
    public String getKindOfCharging() {
        return kindOfCharging;
    }

    /**
     * Sets a ChargingEvent in the Charger.
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    public void setChargingEvent(ChargingEvent ev) {
        this.e = ev;
    }

    /**
     * Handles the list. It executes (if any) the first element of the list.
     */
    private void handleQueueEvents() {
        ChargingEvent e;
        if ("fast".equals(getKindOfCharging())) {
            if (station.getFast().getSize() != 0) {
                e = (ChargingEvent) station.getFast().moveFirst();
                e.preProcessing();
                e.execution();
            }
        } else if ("slow".equals(getKindOfCharging())) {
            if (station.getSlow().getSize() != 0) {
                e = (ChargingEvent) station.getSlow().moveFirst();
                e.preProcessing();
                e.execution();
            }
        }
    }

    /**
     * @return The ChargingEvent which is linked with the Charger.
     */
    public ChargingEvent getChargingEvent() {
        return e;
    }

    /**
     * @return The time that the Charger is going to be busy.
     */
    public long reElapsedCommitTime() {
        long diff = System.currentTimeMillis() - timestamp;
        if (commitTime - diff >= 0)
            return commitTime - diff;
        else
            return 0;
    }

    /**
     * Sets the time the Charger is going to be busy.
     * @param time The commit time.
     */
    public void setCommitTime(long time) {
        timestamp = System.currentTimeMillis();
        commitTime = time;
    }

    /**
     * @return The id of Charger.
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