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
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public Charger(ChargingStation station, String kindOfCharging) {
        this.id = idGenerator.getAndIncrement();
        this.kindOfCharging = kindOfCharging;
        this.busy = false;
        this.commitTime = 0;
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
            float sdf;
            long en;
            sdf = e.reEnergyToBeReceived();
            e.reElectricVehicle().reBattery().setRemAmount(sdf + e.reElectricVehicle().reBattery().reRemAmount());
            if (e.reElectricVehicle().reDriver() != null)
                e.reElectricVehicle().reDriver().setDebt(e.reElectricVehicle().reDriver().reDebt() + station.calculatePrice(e));
            HashMap<String, Float> keys = new HashMap<>(station.reMap());
            for (HashMap.Entry<String, Float> energy : keys.entrySet()) {
                if (e.reEnergyToBeReceived() < station.reMap().get(energy.getKey())) {
                    float ert = station.reMap().get(energy.getKey()) - sdf;
                    e.reStation().setSpecificAmount(energy.getKey(), ert);
                    break;
                } else {
                    sdf = e.reEnergyToBeReceived() - station.reMap().get(energy);
                    e.reStation().setSpecificAmount(energy.getKey(), 0);
                }
            }
            StopWatch d2 = new StopWatch();
            d2.start();
            do {
                en = d2.getTime();
            } while (en < e.reChargingTime());
            System.out.println("The charging took place succesfully");
            e.setCondition("finished");
            station.checkForUpdate();
            changeSituation();
            setChargingEvent(null);
            if (station.reQueueHandling())
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
     * Returns the situation of the Charger.
     *
     * @return True if it is busy, false if it is not busy.
     */
    public boolean reBusy() {
        return busy;
    }

    /**
     * Returns the kind of charging.
     *
     * @return The kind of charging the Charger supports.
     */
    public String reKind() {
        return kindOfCharging;
    }

    /**
     * Sets a ChargingEvent in the Charger.
     *
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    public void setChargingEvent(ChargingEvent ev) {
        this.e = ev;
    }

    /**
     * Handles the list. It executes (if any) the first element of the list.
     */
    public void handleQueueEvents() {
        if ("fast".equals(reKind())) {
            if (station.reFast().reSize() != 0) {
                station.reFast().takeFirst().preProcessing();
                if (station.reFast().takeFirst().reCondition().equals("ready"))
                    station.reFast().removeFirst().execution();
            }
        } else if ("slow".equals(reKind())) {
            if (station.reSlow().reSize() != 0) {
                station.reSlow().takeFirst().preProcessing();
                if (station.reSlow().takeFirst().reCondition().equals("ready"))
                    station.reSlow().removeFirst().execution();
            }
        }
    }

    /**
     * Returns the ChargingEvent of the Charger.
     *
     * @return The ChargingEvent which is linked with the Charger.
     */
    public ChargingEvent reChargingEvent() {
        return e;
    }

    /**
     * Returns the amount of time the Charger is going to be busy.
     *
     * @return The time that the Charger is going to be busy.
     */
    public long reCommitTime() {
        return commitTime;
    }

    /**
     * Sets the time the Charger is going to be busy.
     *
     * @param time The commit time.
     */
    public void setCommitTime(long time) {
        commitTime = time;
    }

    /**
     * Returns the id of the Charger.
     *
     * @return The id of Charger.
     */
    public int reId() {
        return id;
    }
}