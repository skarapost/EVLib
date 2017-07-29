package Station;

import EV.Battery;
import Events.ChargingEvent;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class ExchangeHandler
{
    private int id;
    private ChargingStation station;
    private ChargingEvent e;
    private boolean busy;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public ExchangeHandler(ChargingStation station)
    {
        this.id = idGenerator.getAndIncrement();
        this.station = station;
        e = null;
    }


    public int reId()
    {
        return id;
    }

    /**
     * Links a ChargingEvent with the ExchangeHandler.
     * @param e The ChargingEvent to be linked.
     */
    public void joinChargingEvent(ChargingEvent e)
    {
        this.e = e;
    }

    /**
     * Returns the ChargingEvent of the ExchangeHandler.
     * @return The ChargingEvent of the ExchangeHandler.
     */
    public ChargingEvent reChargingEvent()
    {
        return e;
    }

    /**
     * Executes the ChargingEvent(exchange of battery). It lasts as much as ChargingEvent's
     * exchange time demands. Removes the Battery of the ElectricVehicle and it adds to the
     * batteries linked with the ChargingStation. Takes the st2 Battery from those which are linked
     * with the ChargingStation in the ArrayList structure and puts in the ElectricVehicle.
     * The condition of ChargingEvent gets "finished". In the end if the automatic queue's handling
     * is activated, the ExchangeHandler checks the list.
     * @param st2 The slot of the Battery in the ArrayList structure with the batteries
     * that is going to be used.
     */
    public void executeExchange(int st2)
    {
        new Thread (() -> {
            StopWatch d1 = new StopWatch();
            d1.start ();
            long st = d1.getTime();
            long en;
            Battery temp = null;
            temp = e.reElectricVehicle().reBattery();
            e.reElectricVehicle().vehicleJoinBattery(station.reBatteries().get(st2));
            station.reBatteries().remove(st2);
            station.joinBattery(temp);
            e.reElectricVehicle().reDriver().setDebt(e.reElectricVehicle().reDriver().reDebt() + station.calculatePrice(e));
            StopWatch d2 = new StopWatch();
            d2.start();
            do
            {
                en = d2.getTime();
            }while(en - st < e.reChargingTime());
            System.out.println ("The exchange took place successfully");
            e.setCondition("finished");
            station.checkForUpdate();
            changeSituation();
            joinChargingEvent(null);
            if (station.reQueueHandling())
                handleQueue();
        }).start ();
    }

    /**
     * Handles the list. It executes the first(if any) element of the list.
     */
    public void handleQueue()
    {
        if (station.reExchange().reSize() != 0)
        {
            station.reExchange().takeFirst().preProcessing();
            if (station.reExchange().takeFirst().reCondition().equals("ready"))
                station.reExchange().removeFirst().execution();
        }
    }

    public boolean reBusy()
    {
        return busy;
    }

    public void changeSituation()
    {
        busy = !busy;
    }
}