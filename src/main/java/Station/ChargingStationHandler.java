package Station;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Sources.*;
import Events.ChargingEvent;
import java.util.Date;
import java.util.Calendar;

public class ChargingStationHandler {
    private ChargingStation station;
    private Calendar calendar;
    private Calendar calendar1;
    private int current1;
    private int current2;
    private int current3;
    private int current4;
    private int current5;
    private int current6;
    private int current7;
    private int counter;
    private int count;
    private long[][] timeOfCharging;
    private long[][] timeOfDischarging;
    private long[][] timeOfExchange;
    private boolean[] p;
    /**
     * Constructor of ChargingStationHandler.
     * @param station The ChargingStation object this object is attached.
     */
    public ChargingStationHandler(ChargingStation station)
    {
        this.station = station;
        counter = 0;
        calendar = Calendar.getInstance();
        current1 = calendar.get(Calendar.MONTH);
        current2 = calendar.get(Calendar.YEAR);
        current3 = calendar.get(Calendar.WEEK_OF_YEAR);
        current4 = calendar.get(Calendar.DAY_OF_YEAR);
        current5 = calendar.get(Calendar.HOUR_OF_DAY);
        current6 = calendar.get(Calendar.MINUTE);
        current7 = calendar.get(Calendar.SECOND);
        p = new boolean[500/station.reUpdateSpace()];
        for (int i = 0; i < p.length; i++)
        {
            p[i] = false;
        }
        updateStorage(0);
    }

    /**
     * Create a 2-dimention array. The first dimention is the number of Charger 
     * objects and the second is 500. Every charging which takes place in a Charger
     * object of the ChargingStation is represented in this array.
     */
    public final void creationChargingTable()
    {
        timeOfCharging = new long[station.reChargers().length][500];
    }

    /**
     * Create a 2-dimention array. The first dimention is the number of DisCharger 
     * objects and the second is 500. Every discharging which takes place in a DisCharger
     * object of the ChargingStation is represented in this array.
     */
    public final void creationDisChargingTable()
    {
        timeOfDischarging = new long[station.reDisChargers().length][500];
    }

    /**
     * Create a 2-dimention array. The first dimention is the number of Exchange
     * Handler objects the ChargingStation has. The second is 500. Every battery
     * exchange which takes place in an ExchangeHandler object is represented in 
     * this array.
     */
    public final void creationExchangeTable()
    {
        timeOfExchange = new long[station.reExchangeSlots().length][500];
    }

    /**
     * Modifies the value of  a cell in the ChargingArray.
     * @param index1 The value of the first dimention.
     * @param index2 The value of the second dimention.
     */
    public final void modifyChargingArray(int index1,int index2)
    {
        timeOfCharging[index1][index2] = 1;
    }

    /**
     * Modifies the value of a cell in the DisChargingArray.
     * @param index1 The value of the first dimention.
     * @param index2 The value of the second dimention.
     */
    public final void modifyDisChargingArray(int index1,int index2)
    {
        timeOfDischarging[index1][index2] = 1;
    }

    /**
     * Modifies the value of a cell in the ExchangeArray.
     * @param index1 The value of the first dimention.
     * @param index2 The value of the second dimention.
     */
    public final void modifyExchangeArray(int index1,int index2)
    {
        timeOfExchange[index1][index2] = 1;
    }

    /**
     * Returns the ChargingArray.
     * @return The ChargingArray.
     */
    public final long[][] reChargingTable()
    {
        return timeOfCharging;
    }
    
    /**
     * Returns the ExchangeArray.
     * @return The ExchangeArray.
     */
    public final long[][] reExchangeTable()
    {
        return timeOfExchange;
    }
    
    /**
     * Returns the DisChargingArray.
     * @return The DisChargingArray.
     */
    public final long[][] reDisChargingTable()
    {
        return timeOfDischarging;
    }

    /**
     * Replaces the ChargingArray with another one.
     * @param timeOfCharging The array that is going to replace the old one. 
     */
    public final void setChargingTable(long[][] timeOfCharging)
    {
        this.timeOfCharging = timeOfCharging;
    }
    
    /**
     * Replaces the DisChargingArray with another one.
     * @param timeOfDischarging The array that is going to replace the old one.
     */
    public final void setDisChargingTable(long[][] timeOfDischarging)
    {
        this.timeOfDischarging = timeOfDischarging;
    }

    /**
     * If the updateMode equals 0 then this function is called every time the library 
     * wants to check if an update storage is needed. The function accordingly 
     * to the choice of the user(month, day, hour, minute) calculates how many n
     * spaces have passed since the last update storage. 
     * Then calls the updateStorage function n times.
     */
    public final void checkUpdatePredefinedSpace()
    {
        if ("month".equals(station.reApartment()))
        {
            calendar1 = Calendar.getInstance();
            int year = calendar1.get(Calendar.YEAR);
            int month = calendar1.get(Calendar.MONTH);
            for (int i=0;i<=year-current2;i++)
            {
                if (((current1 != month)&&(current2 == year))||((current1 == month)&&(current2 != year))||((current1 != month)&&(current2 != year)))
                {
                    if (month > current1)
                    {
                        for (int s=counter+1; s<=counter+Math.abs(month-current1); s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + Math.abs(month - current1);
                        current1 = month;
                        current2 = year;
                    }
                    else
                    {
                        for (int s=counter+1; s<=counter+(11-month); s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + (11 - month);
                        for (int s=counter+1; s<=counter + month; s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + month;
                        current1 = month;
                        current2 = year;
                    }
                }
            }
        }
        else if ("day".equals(station.reApartment()))
        {
            calendar1 = Calendar.getInstance();
            int day = calendar1.get(Calendar.DAY_OF_YEAR);
            if (current4 == day)
            {
                if (day > current4)
                {
                    int f = 0;
                    for (int s=counter+1; s<=counter+Math.abs(day-current4); s++)
                    {
                        updateStorage(s);
                    }
                    counter = counter + Math.abs(day - current4);
                    current4 = day;
                }
                else
                {
                    for (int s=counter+1; s <= counter+(365-day); s++)
                    {
                        updateStorage(s);
                    }
                    counter = counter+(365-day);
                    for (int s=counter+1; s<counter+day+1; s++)
                    {
                        updateStorage(s);
                    }
                    counter = counter + Math.abs(day-current4);
                    current4 = day;
                }
            }
        }
        else if ("hour".equals(station.reApartment()))
        {
            calendar1 = Calendar.getInstance();
            int day = calendar1.get(Calendar.DAY_OF_YEAR);
            int hour = calendar1.get(Calendar.HOUR_OF_DAY);
            for (int i=0;i<=day-current4;i++)
            {
                if (((current4 != day)&&(current5 == hour))||((current4 == day)&&(current5 != hour))||((current4 != day)&&(current5 != hour)))
                {
                    if (hour > current5)
                    {
                        for (int s=counter+1; s<=counter+Math.abs(hour-current5); s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + Math.abs(hour - current5);
                        current4 = day;
                        current5 = hour;
                    }
                    else
                    {
                        for (int s=counter+1; s<=counter+(23-hour); s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + (23 - hour);
                        for (int s=counter+1; s<=counter + hour; s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + hour;
                        current4 = day;
                        current5 = hour;
                    }
                }
            }
        }
        else if ("minute".equals(station.reApartment()))
        {
            calendar1 = Calendar.getInstance();
            int minute = calendar1.get(Calendar.MINUTE);
            int hour = calendar1.get(Calendar.HOUR_OF_DAY);
            for (int i=0;i<=hour-current5;i++)
            {
                if (((current6 != minute)&&(current5 == hour))||((current6 == minute)&&(current5 != hour))||((current6 != minute)&&(current5 != hour)))
                {
                    if (minute > current6)
                    {
                        int f = 0;
                        for (int s=counter+1; s<=counter+Math.abs(minute-current6); s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + Math.abs(minute - current6);
                        current6 = minute;
                        current5 = hour;
                    }
                    else
                    {
                        int f = 0;
                        for (int s=counter+1; s<=counter+(59-current6); s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + (59 - current6);
                        for (int s=counter+1; s<=counter + minute; s++)
                        {
                            updateStorage(s);
                        }
                        counter = counter + minute;
                        current5 = hour;
                        current6 = minute;
                    }
                }
            }
        }
    }

    /**
     * If the updateMode equals 1 then this function is called every time the library 
     * wants to check if an update storage is needed. The function accordingly 
     * to the choice(updateSpace) of the user calculates how many n spaces have 
     * passed since th last update storage. Then calls the updateStorage function n times.
     */
    public final void checkUpdateMadeSpace()
    {
        Date date = new Date();
        long seconds = date.getTime()/1000;
        int diff = (int) ((seconds - station.reCreationDate())/(station.reTimeMoment()*station.reUpdateSpace()));
        diff = diff - count;
        for(int i = count+1; i <= count + diff; i++)
        {
            updateStorage(i);
        }
        count = count + diff;
    }
    
    /**
     * Update the storage of the ChargingStation with the new amounts of energy 
     * of each source.
     * @param point The number of update. The function reads the cell with this value 
     * of the array with the future energies of each EnergySource object and adds 
     * it to the storage of the ChargingStation. At the same each amount of energy 
     * it is added to the according source of energy in the ChargingStation.
     */
    public final void updateStorage(int point)
    {
        if (!p[point])
        {
            p[point] = true;
            float counter = 0;
            for (int j=0; j<station.reEnergySources().length; j++)
            {
                counter = counter + station.reEnergySource(j).reAmount(point);
                if (station.reEnergySource(j) instanceof Solar)
                    station.setSpecificAmount("solar", (station.reSpecificAmount("solar") + station.searchEnergySource("solar").reAmount(point)));
                else if (station.reEnergySource(j) instanceof Geothermal)
                    station.setSpecificAmount("geothermal", (station.reSpecificAmount("geothermal") + station.searchEnergySource("geothermal").reAmount(point)));
                else if (station.reEnergySource(j) instanceof NonRenewable)
                    station.setSpecificAmount("nonrenewable", (station.reSpecificAmount("nonrenewable") + station.searchEnergySource("nonrenewable").reAmount(point)));
                else if (station.reEnergySource(j) instanceof HydroElectric)
                    station.setSpecificAmount("hydroelectric", (station.reSpecificAmount("hydroelectric") + station.searchEnergySource("hydroelectric").reAmount(point)));
                else if (station.reEnergySource(j) instanceof Wave)
                    station.setSpecificAmount("wave", (station.reSpecificAmount("wave") + station.searchEnergySource("wave").reAmount(point)));
                else if (station.reEnergySource(j) instanceof Wind)
                    station.setSpecificAmount("wind", (station.reSpecificAmount("wind") + station.searchEnergySource("wind").reAmount(point)));
            }
            station.setTotalEnergy(-counter);
        }
    }


    /**
     * Returns the value of a cell of the ChargingArray.
     * @param index1 Value of the first dimention.
     * @param index2 Value of the secong dimention.
     * @return 0 if there is not any charging taking place in the given time, or 1
     * if there is a charging taking place in the given time.
     */
    public final long reTimeOfCharging(int index1,int index2)
    {
        return timeOfCharging[index1][index2];
    }

    /**
     * Returns the value of a cell of the DisChargingArray.
     * @param index1 Value of the first dimention.
     * @param index2 Value of the secong dimention.
     * @return 0 if there is not any discharging taking place in the given time, or 1
     * if there is a discharging taking place in the given time.
     */
    public final long reTimeOfDisCharging(int index1,int index2)
    {
        return timeOfDischarging[index1][index2];
    }

    /**
     * Returns the value of a cell of the ExchangeArray.
     * @param index1 Value of the first dimention.
     * @param index2 Value of the secong dimention.
     * @return 0 if there is not any exchange taking place in the given time, or 1
     * if there is a exchange taking place in the given time.
     */
    public final long reTimeOfExchange(int index1,int index2)
    {
        return timeOfExchange[index1][index2];
    }

    /**
     * Distribute the ebergy across the ElectricVehicle objects. In case some 
     * ElectricVehicle objects arrive together and the ChargingStation's energy
     * is not enough, then this function is called and distribute the energy. 
     * Each ElectricVehicle takes a percentage of the total amount. 
     * The percentage is the energy the ElectricVehicle demands to the total 
     * energy of all the ElectricVehicle objects.
     * @param date The time the ElectricVehicle object arrived.
     */
    public final void energyDistribution(long date)
    {
        float counter = 0;
        int counter1 = 0;
        boolean[] g;
        g = new boolean[station.reChargers().length];
        float max = -1;
        for (int i = 0;i < station.reChargers().length;i++)
        {
            if (station.reCharger(i).reChargingEvent() != null)
            {
                if ((station.reCharger(i).reChargingEvent().reStartTime() == date)&&(station.reCharger(i).reChargingEvent().reMode() != 4))
                {
                    counter = counter + station.reCharger(i).reChargingEvent().reEnergyAmount();
                    g[i] = true;
                    counter1++;
                    if (max < station.reCharger(i).reChargingEvent().reStock())
                        max = station.reCharger(i).reChargingEvent().reStock();
                }
                else
                    g[i] = false;
            }
        }
        if (counter1 == 1)
        {
            return;
        }
        for(int i = 0;i < g.length;i++)
        {
            if (g[i])
            {
                station.reCharger(i).reChargingEvent().setEnergyToBeReceived(max * (station.reCharger(i).reChargingEvent().reEnergyAmount()/counter));
                if ("fast".equals(station.reCharger(i).reChargingEvent().reKind()))
                    station.reCharger(i).reChargingEvent().setChargingTime((int) (station.reCharger(i).reChargingEvent().reEnergyToBeReceived()/station.reChargingRatioFast()) + 1);
                else
                    station.reCharger(i).reChargingEvent().setChargingTime((int) ((station.reCharger(i).reChargingEvent().reEnergyToBeReceived() + 1)/station.reChargingRatioSlow()));
            }
        }
    }

    /**
     * Calculates the cost of a charging.
     * @param w The ChargingEvent that executed.
     * @return The cost of the charging.
     */
    public final float calculatePrice(ChargingEvent w)
    {
        if (!"exchange".equals(w.reKind()))
        {
            float t = w.reEnergyToBeReceived()*station.reUnitPrice();
            return t;
        }
        else
            return station.reExchangePrice();
    }

    /**
     * Calculates the amount of time a Driver has to wait until his ElectricVehicle
     * can be charged. This calculation happens in case a Vehicle inserts has to 
     * be inserted in the list.
     * @param y The ChargingEvent that has to wait.
     * @return The waiting time.
     */
    public final int calWaitingTime(ChargingEvent y)
    {
        int[] counter1 = new int[station.reChargers().length];
        int[] counter2 = new int[station.reChargers().length];
        Date u = new Date();
        long r = u.getTime()/1000;
        int min = 10000000;
        if (!"exchange".equals(y.reKind()))
        {
            for (int i = 0; i<station.reChargers().length; i++)
            {
                if (y.reKind() == station.reCharger(i).reKind())
                {
                    int diff = (int) (r - station.reCharger(i).reChargingEvent().reStartTime()); 
                    if (min > station.reCharger(i).reChargingEvent().reChargingTime() - diff)
                        min = i;
                    counter1[i]= station.reCharger(i).reChargingEvent().reChargingTime() - diff;
                }
            }
        }
        else
        {
            for (int i = 0; i<station.reExchangeSlots().length; i++)
            {
                int diff = (int) (r - station.reExchangeHandler(i).reChargingEvent().reStartTime());
                if (min > station.reExchangeHandler(i).reChargingEvent().reChargingTime() - diff)
                    min = i;
                counter2[i] = station.reExchangeHandler(i).reChargingEvent().reChargingTime() - diff;
            }
        }
        if ("slow".equals(y.reKind()))
        {
            Queue o = station.reSlow();
            for (int i = 0;i < o.reSize();i++)
            {
                counter1[min] = counter1[min] + o.peek(i).reChargingTime();
                for(int j=0;j<station.reChargers().length;j++)
                {
                    if ((counter1[j]<counter1[min])&&(counter1[j]!=0))
                        min = j;
                }
            }
            return counter1[min];
        }
        if ("fast".equals(y.reKind()))
        {
            Queue o = station.reFast();
            for(int i = 0; i < o.reSize();i++)
            {
                counter1[min] = counter1[min] + o.peek(i).reChargingTime();
                for(int j=0;j<station.reChargers().length;j++)
                {
                    if ((counter1[j]<counter1[min])&&(counter1[j]!=0))
                        min = j;
                }
            }
            return counter1[min];
        }
        if ("exchange".equals(y.reKind()))
        {
            Queue o = station.reExchange();
            for(int i = 0; i < o.reSize();i++)
            {
                counter2[min] = counter2[min] + o.peek(i).reChargingTime();
                for(int j=0;j<station.reChargers().length;j++)
                {
                    if ((counter2[j]<counter2[min])&&(counter2[j]!=0))
                        min = j;
                }
            }
            return counter2[min];
        }
        return 0;
    }

    /**
     * Calculates the waiting time of a DisChargingEvent.
     * @return The time the ElectricVehicle has to wait.
     */
    public final int calDisWaitingTime()
    {
        int[] counter1 = new int[station.reDisChargers().length];
        Date u = new Date();
        long r = u.getTime()/1000;
        int min = 10000000;
        for (int i = 0; i<station.reDisChargers().length; i++)
        {
            int diff = (int) (r - station.reDisCharger(i).reDisChargingEvent().reStartTime());
            if (min > station.reDisCharger(i).reDisChargingEvent().reDisChargingTime() - diff)
                min = station.reDisCharger(i).reDisChargingEvent().reDisChargingTime() - diff;
            counter1[i] = station.reDisCharger(i).reDisChargingEvent().reDisChargingTime() - diff;
        }
        Queue o = station.reDischarging();
        for (int i = 0; i < o.rSize(); i++)
        {
            counter1[min] = counter1[min] + o.get(i).reDisChargingTime();
            for(int j=0;j<station.reDisChargers().length;j++)
            {
                if ((counter1[j]<min)&&(counter1[j]!=0))
                    min = j;
            }
        }
        return min;
    }
}