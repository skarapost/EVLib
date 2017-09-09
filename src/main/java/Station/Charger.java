package Station;

import Events.ChargingEvent;
import org.apache.commons.lang3.time.StopWatch;
import java.util.HashMap;
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
        new Thread(() ->
        {
            double sdf;
            long en;
            sdf = e.getEnergyToBeReceived();
            HashMap<String, Double> keys = new HashMap<>(station.getMap());
            for (HashMap.Entry<String, Double> energy : keys.entrySet()) {
                if (sdf < energy.getValue()) {
                    double ert = station.getMap().get(energy.getKey()) - sdf;
                    e.getStation().setSpecificAmount(energy.getKey(), ert);
                    break;
                } else {
                    sdf -= energy.getValue();
                    e.getStation().setSpecificAmount(energy.getKey(), 0);
                }
            }
            StopWatch d2 = new StopWatch();
            d2.start();
            do {
                en = d2.getTime();
            } while (en < e.getChargingTime());
            e.getElectricVehicle().getBattery().setRemAmount(e.getEnergyToBeReceived() + e.getElectricVehicle().getBattery().getRemAmount());
            if (e.getElectricVehicle().getDriver() != null)
                e.getElectricVehicle().getDriver().setDebt(e.getElectricVehicle().getDriver().getDebt() + e.getCost());
            System.out.println("The charging " + e.getId() + " completed succesfully");
            e.setCondition("finished");
            changeSituation();
            setChargingEvent(null);
            commitTime = 0;
            if (station.getQueueHandling())
                handleQueueEvents();
        }).start();
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
    public void handleQueueEvents() {
        if ("fast".equals(getKindOfCharging())) {
            if (station.getFast().size() != 0)
            {
                station.getFast().takeFirst().preProcessing();
                station.getFast().removeFirst().execution();
            }
        } else if ("slow".equals(getKindOfCharging())) {
            if (station.getSlow().size() != 0) {
                station.getSlow().takeFirst().preProcessing();
                station.getSlow().removeFirst().execution();
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
        long diff = station.getTime() - timestamp;
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
        timestamp = station.getTime();
        commitTime = time;
    }

    /**
     * @return The id of Charger.
     */
    public int getId() {
        return id;
    }
}