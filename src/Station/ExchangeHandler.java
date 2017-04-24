package Station;

import EV.Battery;
import Station.ChargingStation;
import Events.ChargingEvent;
import java.util.Date;

/**
 *
 * @author Sotiris Karapostolakis
 */
public class ExchangeHandler
{
    private int id;
    private final ChargingStation station;
    private ChargingEvent event;

    /**
     * Constructor of ExchangeHandler class.
     * @param id The id of ExchangeHandler.
     * @param station The ChargingStation of the ExchangeHandler.
     */
    public ExchangeHandler(int id,ChargingStation station)
    {
        this.id = id;
        this.station = station;
        event = null;
    }

    /**
     * Links a ChargingEvent with the ExchangeHandler.
     * @param event The ChargingEvent to be linked.
     */
    public final void joinChargingEvent(ChargingEvent event)
    {
        this.event = event;
    }

    /**
     * Returns the ChargingEvent of the ExchangeHandler.
     * @return The ChargingEvent of the ExchangeHandler.
     */
    public final ChargingEvent reChargingEvent()
    {
        return event;
    }

     /**
     * Executes the ChargingEvent(exchange of battery). It lasts as much as ChargingEvent's
     * exchange time demands. Removes the Battery of the ElectricVehicle and it adds to the 
     * batteries linked with the ChargingStation. Takes the st2 Battery from those which are linked 
     * with the ChargingStation in the ArrayList structure and puts in the ElectricVehicle.
     * The mode of ChargingEvent gets 4. At the end if the automatic queue's handling 
     * is activated, the ExchangeHandler checks the list.
     * @param st2 The slot of the Battery in the ArrayList structure with the batteries
     * that is going to be used.
     */
    public final void executeExchange(int st2)
    {
        new Thread()
        {
            @Override
            public void run(){
        Date d1 = new Date();
        int st = (int) (d1.getTime()/1000);
        int en;
        Battery temp = null;
        temp = event.reElectricVehicle().reBattery();
        event.reElectricVehicle().vehicleJoinBattery(station.reBatteries().get(st2));
        station.reBatteries().remove(st2);
        station.joinBattery(temp);
        event.reElectricVehicle().reDriver().setDebt(event.reElectricVehicle().reDriver().reDebt() + station.reChargingStationHandler().calculatePrice(event));
        do{
            Date d2 = new Date();
            en = (int) (d2.getTime()/1000);
        }while(en-st<event.reChargingTime());
        event.setMode(4);
        station.setExchangeSlot(id, false);
        if (station.reUpdateMode() == 0)
            station.reChargingStationHandler().checkUpdatePredefinedSpace();
        else
            station.reChargingStationHandler().checkUpdateMadeSpace();
        System.out.println("The exchange function took place successfully.");
        joinChargingEvent(null);
        if (station.reQueueHandling())
        {
            handleQueue();
        }
            }
        }.start();
    }

    /**
     * Handles the list. It executes the first(if any) element of the list.
     */
    public final void handleQueue()
    {
        if (station.reExchange().reSize() != 0)
        {
            station.reExchange().takeFirst().preProcessing();
            if (station.reExchange().takeFirst().reMode() == 2)
                station.reExchange().removeFirst().execution();
        }
    }
}