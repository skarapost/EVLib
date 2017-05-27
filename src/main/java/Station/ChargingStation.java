package Station;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Sources.*;
import EV.Battery;
import EV.ElectricVehicle;
import Events.DisChargingEvent;
import Events.ChargingEvent;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Date;

public class ChargingStation 
{
    private final int id;
    private final String name;
    private final Queue slow;
    private final Queue fast;
    private final Queue discharging;
    private final Queue exchange;
    private float chargingRatioSlow;
    private float chargingRatioFast;
    private float disChargingRatio;
    private final ArrayList<Charger> chargers;
    private final ArrayList<EnergySource> n;
    private ArrayList<DisCharger> dischargers;
    private ArrayList<Battery> batteries;
    private boolean[] exchangeSlots;
    private ExchangeHandler iop;
    private ArrayList<ExchangeHandler> wer;
    private Charger r;
    private DisCharger z;
    private EnergySource  c;
    private final LinkedHashMap<String,Float> amounts;
    private float totalEnergy;
    private ArrayList<String> sources;
    private String apartment;
    private int numOfSlots;
    private ChargingStationHandler m;
    private long creationDate;
    private float unitPrice;
    private float disUnitPrice;
    private float exchangePrice;
    private boolean automaticQueueHandling;
    private int timeMoment;
    private int numOfMoments;
    private int updateSpace;
    private int updateMode;
    private int timeOfExchange;

    /**
     * Constructor of ChargingStation class.
     * @param id The id of ChargingStation.
     * @param name The name of ChargingStation.
     * @param kinds It is a String array with the kinds of charging the ChargingStation supports. The arguments 
     * can to be "slow" for slow charging, or "fast" for fast charging. Accordingly the length of the array it creates 
     * so much Charger objects.
     * @param source It is a String array with the kinds of energy the ChargingStation supports. The arguments can to be
     * "solar" for solar energy, "wind" for wind energy, "wave" for wave energy, "hydroelectric" for hydroelectric energy,
     * "geothermal" for geothermal energy or "nonrenewable" foe nonrenewable energy sources. Accordingly the length of the array
     * and the value of each cell, an object of the corresponding source of energy is created.
     * @param energAm An 2-dimention array. It contains the energies each kind of energy source is going to give to the ChargingStation 
     * at every update of the storage. The kinds of energy are refered in the same order that was given in the source array.
     */
    public ChargingStation(int id,String name,String[] kinds,String[] source,float[][] energAm)
    {
        Date date = new Date();
        creationDate = date.getTime()/1000;
        amounts = new LinkedHashMap<String,Float>();
        this.id = id;
        this.name = name;
        automaticQueueHandling = true;
        slow = new Queue("charging");
        fast = new Queue("charging");
        exchange = new Queue("charging");
        discharging = new Queue("discharging");
        chargers = new ArrayList<Charger>();
        dischargers = new ArrayList<DisCharger>();
        wer = new ArrayList<ExchangeHandler>();
        n = new ArrayList<EnergySource>();
        sources = new ArrayList<String>();
        batteries = new ArrayList<Battery>();
        apartment = "minute";
        updateMode = 0;
        numOfSlots = 0;
        timeMoment = 1;
        for (int q=0;q<source.length;q++)
        {
            sources.add(q, source[q]);
        }
        totalEnergy = 0;
        chargingRatioSlow = 1;
        chargingRatioFast = 2;
        disChargingRatio = 1;
        unitPrice = 0;
        exchangePrice = 0;
        disUnitPrice = 0;
        numOfMoments = 500;
        updateSpace = 1;
        for (int i=0;i<source.length;i++)
        {
            if (source[i].equals("solar"))
            {
                c = new Solar(i,this,energAm[i]);
                n.add(i,c);
                amounts.put("solar", 0f);
            }
            else if (source[i].equals("wind"))
            {
                c = new Wind(i,this,energAm[i]);
                n.add(i,c);
                amounts.put("wind", 0f);
            }
            else if (source[i].equals("geothermal"))
            {
                c = new Geothermal(i,this,energAm[i]);
                n.add(i,c);
                amounts.put("geothermal",0f);
            }
            else if (source[i].equals("wave"))
            {
                c = new Wave(i,this,energAm[i]);
                n.add(i,c);
                amounts.put("wave", 0f);
            }
            else if (source[i].equals("hydroelectric"))
            {
                c = new HydroElectric(i,this,energAm[i]);
                n.add(i,c);
                amounts.put("hydroelectric", 0f);
            }
            else if (source[i].equals("nonrenewable"))
            {
                c = new NonRenewable(i,this,energAm[i]);
                n.add(i,c);
                amounts.put("nonrenewable",0f);
            }
        }
        m = new ChargingStationHandler(this);
        for (int i=0;i<kinds.length;i++)
        {
            if (!kinds[i].equals("exchange"))
            {
                r = new Charger(i,this,kinds[i]);
                chargers.add(i,r);
            }
        }
        m.creationChargingTable();
    }

    /**
     * Constructor of ChargingStation class.
     * @param id The id of ChargingStation.
     * @param name The name of ChargingStation.
     * @param kinds It is a String array with the kinds of charging the ChargingStation supports. The arguments 
     * can to be "slow" for slow charging, or "fast" for fast charging. Accordingly the length of the array it creates 
     * so much Charger objects.
     * @param source It is a String array with the kinds of energy the ChargingStation supports. The arguments can to be
     * "solar" for solar energy, "wind" for wind energy, "wave" for wave energy, "hydroelectric" for hydroelectric energy,
     * "geothermal" for geothermal energy or "nonrenewable" for nonrenewable energy sources. Accordingly the length of the array
     * and the value of each cell, an object of the corresponding energy is created.
     */
    public ChargingStation(int id,String name,String[] kinds,String[] source)
    {
        Date date = new Date();
        creationDate = date.getTime()/1000;
        amounts = new LinkedHashMap<String,Float>();
        this.id = id;
        this.name = name;
        slow = new Queue("charging");
        fast = new Queue("charging");
        exchange = new Queue("charging");
        discharging = new Queue("discharging");
        automaticQueueHandling = true;
        chargers = new ArrayList<Charger>();
        dischargers = new ArrayList<DisCharger>();
        batteries = new ArrayList<Battery>();
        wer = new ArrayList<ExchangeHandler>();
        n = new ArrayList<EnergySource>();
        sources = new ArrayList<String>();
        for (int q=0;q<source.length;q++)
        {
            sources.add(q, source[q]);
        }
        numOfSlots = 0;
        timeMoment = 1;
        updateMode = 0;
        totalEnergy = 0;
        chargingRatioSlow = 1;
        chargingRatioFast = 2;
        disChargingRatio = 1;
        unitPrice = 0;
        disUnitPrice = 0;
        exchangePrice = 0;
        numOfMoments = 500;
        updateSpace = 1;
        for (int i=0;i<source.length;i++)
        {
            if (source[i].equals("solar"))
            {
                c = new Solar(i,this);
                n.add(i,c);
                amounts.put("solar",0f);
            }
            else if (source[i].equals("wind"))
            {
                c = new Wind(i,this);
                n.add(i,c);
                amounts.put("wind", 0f);
            }
            else if (source[i].equals("geothermal"))
            {
                c = new Geothermal(i,this);
                n.add(i,c);
                amounts.put("geothermal", 0f);
            }
            else if (source[i].equals("wave"))
            {
                c = new Wave(i,this);
                n.add(i,c);
                amounts.put("wave",0f);
            }
            else if (source[i].equals("hydroelectric"))
            {
                c = new HydroElectric(i,this);
                n.add(i,c);
                amounts.put("hydroelectric",0f);
            }
            else if (source[i].equals("nonrenewable"))
            {
                c = new NonRenewable(i,this);
                n.add(i,c);
                amounts.put("nonrenewable", 0f);
            }
        }
        m = new ChargingStationHandler(this);
        for (int i=0;i<kinds.length;i++)
        {
            if (!kinds[i].equals("exchange"))
            {
                r = new Charger(i,this,kinds[i]);
                chargers.add(i,r);
            }
        }
        m.creationChargingTable();
    }

    /**
     * Constructor of ChargingStation class.
     * @param id The id of ChargingStation.
     * @param name The name of ChargingStation.
     */
    public ChargingStation(int id, String name)
    {
        Date date = new Date();
        creationDate = date.getTime()/1000;
        this.id = id;
        this.name = name;
        slow = new Queue("charging");
        fast = new Queue("charging");
        exchange = new Queue("charging");
        discharging = new Queue("discharging");
        numOfSlots = 0;
        amounts = new LinkedHashMap<String,Float>();
        chargers = new ArrayList<Charger>();
        dischargers = new ArrayList<DisCharger>();
        batteries = new ArrayList<Battery>();
        wer = new ArrayList<ExchangeHandler>();
        n = new ArrayList<EnergySource>();
        sources = new ArrayList<String>();
        apartment = "minute";
        updateMode = 0;
        automaticQueueHandling = true;
        chargingRatioSlow = 1;
        chargingRatioFast = 2;
        disChargingRatio = 1;
        unitPrice = 0;
        exchangePrice = 0;
        disUnitPrice = 0;
        timeMoment = 1;
        numOfMoments = 500;
        updateSpace = 1;
        m = new ChargingStationHandler(this);
        m.creationChargingTable();
    }

    /**
     * Inserts a ChargingEvent in the corresponding list. 
     * @param y The ChargingEvent that is going to be inserted.
     */
    public final void updateQueue(ChargingEvent y)
    {
        switch (y.reKind()) 
        {
            case "exchange":
                exchange.insertElement(y);
                break;
            case "slow":
                slow.insertElement(y);
                break;
            case "fast":
                fast.insertElement(y);
                break;
            default:
        }
    }

    /**
     * Inserts a DisChargingEvent in the list.
     * @param d The DisChargingEvent that is going to be inserted.
     */
    public final void updateDisChargingQueue(DisChargingEvent d)
    {
        discharging.insertElement(d);
    }

    /**
     * Returns the Queue(list) for the fast charging.
     * @return The Queue(list) for the fast charging.
     */
    public final Queue reFast()
    {
        return fast;
    }

     /**
     * Returns the Queue(list) for the slow charging.
     * @return The Queue(list) for the slow charging.
     */
    public final Queue reSlow()
    {
        return slow;
    }

     /**
     * Returns the Queue(list) for the exchange battery.
     * @return The Queue(list) for the exchange battery.
     */
    public final Queue reExchange()
    {
        return exchange;
    }

     /**
     * Returns the Queue(list) for the discharging.
     * @return The Queue(list) for the discharging.
     */
    public final Queue reDischarging()
    {
        return discharging;
    }

    /**
     * Checks for empty Charger according to the kind that is given.
     * @param k The kind of Charger that is asked.
     * @return Returns the id of the Charger in case there is any empty Charger
     * object. 
     * Returns -1 if all the Charger are busy.
     */
    public final int checkChargers(String k)
    {
        for(int i=0;i<reChargers().length;i++)
        {
            if (k.equals(reCharger(i).reKind()))
            {
                if (!reCharger(i).reBusy())
                    return reCharger(i).reId();
            }
        }
        return -1;  
    }
    
    /**
     * Checks for any empty Discharger.
     * @return The id of the empty DisCharger , or -1 if there is not any empty, 
     * or -753159 if the ChargingStation does not 
     * supports discharging function.
     */
    public final int checkDisChargers()
    {
        for(int i=0;i<reDisChargers().length;i++)
        {
            if(!reDisCharger(i).reBusy())
            {
                return reDisCharger(i).reId();
            }
        }
        if (reDisChargers().length == 0)
            return -753159;
        return -1;  
    }
    
    /**
     * Checks for any empty exchange slot.
     * @return The number of the slot, or -1 if there is not any empty slot, 
     * or -753159 if the ChargingStation does not 
     * supports exchange battery.
     */
    public final int checkExchangeSlots()
    {
        for (int i=0;i<reExchangeSlots().length;i++)
        {
            if (!reExchangeSlots()[i])
                return i;
        }
        if (reExchangeSlots().length == 0)
            return -753159;
        return -1;
    }
    
    /**
     * Checks for any Battery that is not completely empty.
     * @return Returns the position of the Battery, or -1 if there is not any Battery.
     */
    public final int checkBatteries()
    {
        for (int i=0;i<reBatteries().size();i++)
        {
            if (reBatteries().get(i).reRemAmount()!=0)
                return i;
        }
        if (reBatteries().isEmpty())
            return -753159;
        return -1;
    }
    
    /**
     * Sets the number of battery exchanges can the ChargingStation simultaneously support.
     * @param slots The number of exchanges.
     */
    public final void setExchangeSlots(int slots)
    {
        exchangeSlots = new boolean[slots];
        for (int j=0;j<slots;j++)
        {
            exchangeSlots[j] = false;
            iop = new ExchangeHandler(j,this);
            wer.add(j,iop);
        }
        m.creationExchangeTable();
    }
    
    /**
     * Returns the array which depicts whether an exchange slot is empty or busy.
     * @return The array of battery exchange slots.
     */
    public final boolean[] reExchangeSlots()
    {
        return exchangeSlots;
    }
    
    /**
     * Sets a value to a specific cell.
     * @param key The exchange slot is going to change value.
     * @param value The situation of exchange slot.
     */
    public final void setExchangeSlot(int key,boolean value)
    {
        exchangeSlots[key] = value;
    }
    
    /**
     * Inserts a Charger to the ChargingStation.
     * @param y The Charger to be inserted.
     */
    public final void insertCharger(Charger y)
    {
        chargers.add(y);
        long[][] temp = new long[chargers.size()][numOfMoments];
        long[][] timeofcharging = m.reChargingTable();
        for (int i=0;i<chargers.size() - 1;i++)
        {
            for(int j = 0; j < numOfMoments; j++)
            {
                temp[i][j] = timeofcharging[i][j];
            }
        }
        m.setChargingTable(temp);
    }
    
    /**
     * Inserts a Discharger to the ChargingStation.
     * @param y The DisCharger to be inserted.
     */
    public final void insertDisCharger(DisCharger y)
    {
        dischargers.add(y);
        long[][] temp = new long[dischargers.size()][numOfMoments];
        long[][] timeofdischarging = m.reDisChargingTable();
        for (int i=0;i<dischargers.size() - 1;i++)
        {
            for(int j = 0; j < numOfMoments; j++)
            {
                temp[i][j] = timeofdischarging[i][j];
            }
        }
        m.setDisChargingTable(temp);
    }
    
    /**
     * Inserts a new EnergySource to the ChargingStation.
     * @param z The EnergySource is going to be inserted.
     */
    public final void insertEnergySource(EnergySource z)
    {
        n.add(z);
        if (z instanceof Solar)
        {
            sources.add("solar");
            amounts.put("solar", 0f);
        }
        else if (z instanceof Wave)
        {
            sources.add("wave");
            amounts.put("wave", 0f);
        }
        else if (z instanceof Wind)
        {
            sources.add("wind");
            amounts.put("wind",0f);
        }
        else if (z instanceof HydroElectric)
        {
            sources.add("hydroElectric");
            amounts.put("hydroelectric",0f);
        }
        else if (z instanceof Geothermal)
        {
            sources.add("geothermal");
            amounts.put("geothermal", 0f);
        }
        else if (z instanceof NonRenewable)
        {
            sources.add("nonrenewable");
            amounts.put("nonrenewable",0f);
        }
    }
    
    /**
     * Deletes an EnergySource from the ChargingStation.
     * @param z The EnergySource is going to be removed.
     */
    public final void deleteEnergySource(EnergySource z)
    {
        n.remove(z);
        if (z instanceof Solar)
        {
            amounts.remove("solar");
            sources.remove("solar");
        }
        else if (z instanceof Wave)
        {
            amounts.remove("wave");
            sources.remove("wave");
        }
        else if (z instanceof Wind)
        {
            amounts.remove("wind");
            sources.remove("wind");
        }
        else if (z instanceof HydroElectric)
        {
            amounts.remove("hydroelectric");
            sources.remove("hydroelectric");
        }
        else if (z instanceof NonRenewable)
        {
            amounts.remove("nonrenewable");
            sources.remove("nonrenewable");
        }
        else if (z instanceof Geothermal)
        {
            amounts.remove("geothermal");
            sources.remove("geothermal");
        }
    }

    /**
     * Sorts the energies sources according to the desire of the user.
     * @param energies It is a String array that defines the energies order.
     */
    public final void customEnergySorting(String[] energies)
    {
        sources.clear();
        for(int i = 0; i < energies.length; i++)
        {
            sources.add(i, energies[i]);
        }
    }
    
    /**
     * Sets from a predefined set of choices, how often a storage update is going to happen.
     * @param ap The choice how often the user wants an update storage to be happened. 
     * It takes "month" if the user wants every month, "day" if the user wants every day, 
     * "hour" if the user wants every hour and "minute" if the user wants every minute.
     */
    public final void setApartment(String ap)
    {
        apartment = ap;
        updateMode = 0;
    }
    
    /**
     * Returns the choice for the storage update.
     * @return The update choice from them the ChargingStation offers.
     */
    public final String reApartment()
    {
        return apartment;
    }
    
    /**
     * Sets the number of slots for the discharging function. As much as the
     * slots, so much Dischargers are created.
     * @param slots The number of DisCharger objects that is going to be created.
     */
    public final void setNumOfSlots(int slots)
    {
        sources.add("discharging");
        numOfSlots = slots;
        dischargers = new ArrayList<DisCharger>();
        for (int i=0;i<slots;i++)
        {
            z = new DisCharger(i,this);
            dischargers.add(i,z);
        }
        m.creationDisChargingTable();
    }
    
    /**
     * Returns the Exchangehandler in the given position.
     * @param qwe The position of the EchangeHandler that is going to be returned.
     * @return The ExchangeHandler that is asked.
     */
    public final ExchangeHandler reExchangeHandler(int qwe)
    {
        return wer.get(qwe);
    }

    /**
     * Inserts a Battery to the ChargingStation for the battery exchange function.
     * @param hj The Battery is going to be inserted.
     */
    public final void joinBattery(Battery hj)
    {
        batteries.add(hj);
    }

    /**
     * Returns the batteries that are destined to be used for battery exchange.
     * @return An ArrayList with the Battery objects.
     */
    public final ArrayList<Battery> reBatteries()
    {
        return batteries;
    }

    /**
     * Deletes a Battery from the batteries for the battery exchange function.
     * @param hj The battery that will be removed.
     * @return True if the deletion was successfull, false if it was unsuccessfull.
     */
    public final boolean deleteBattery(Battery hj)
    {
        boolean remove = batteries.remove(hj);
        return remove;
    }

    /**
     * Return the DisCharger which is in the slot given by the argument in the ArrayList structure.
     * @param xc The slot in which the DisCharger is.
     * @return The DisCharger.
     */
    public final DisCharger reDisCharger(int xc)
    {
        return dischargers.get(xc);
    }

    /**
     * Returns the ArrayList structure with the DisCharger objects.
     * @return Returns the DisCharger objects of the ChargingStation.
     */
    public final DisCharger[] reDisChargers()
    {
        DisCharger[] g = new DisCharger[dischargers.size()];
        for (int i = 0; i < dischargers.size(); i++)
        {
            g[i] = dischargers.get(i);
        }
        return g;
    }
    
    /**
     * Returns the number of DisChargers in the ChargingStation.
     * @return The number of the DisCharger in the ChargingStation.
     */
    public final int reNumOfSlots()
    {
        return numOfSlots;
    }

    /**
     * Search for the Charger based on the given id.
     * @param id The id of the Charger which is asked.
     * @return The Charger.
     */
    public final Charger searchCharger(int id)
    {
        Charger y = null;
        for(int i=0;i<chargers.size();i++)
        {
            if (chargers.get(i).reId() == id)
                y = chargers.get(i);
        }
        return y;
    }

    /**
     * Search for the place of the Charger in the ArrayList structure based on the given id. 
     * @param id The id of the Charger.
     * @return The position of the Charger in the ArrayList structure.
     */
    public final int searchChargerPlace(int id)
    {
        int y = 0x0;
        for(int i=0;i<chargers.size();i++)
        {
            if (chargers.get(i).reId() == id)
                y = i;
        }
        return y;
    }

    /**
     * Search for the DisCharger based on the given id.
     * @param id The id of the DisCharger object which is asked.
     * @return The DisCharger object.
     */
    public final DisCharger searchDischarger(int id)
    {
        DisCharger y = null;
        for(int i=0;i<dischargers.size();i++)
        {
            if (dischargers.get(i).reId() == id)
                y = dischargers.get(i);
        }
        return y;
    }

    /**
     * Search for the place of the DisCharger in the ArrayList structure with the 
     * id which is given by the argument id. 
     * @param id The id of the DisCharger.
     * @return The position of the DisCharger in the ArrayList structure.
     */
    public final int searchDischargerPlace(int id)
    {
        int y = 0x0;
        for(int i=0;i<chargers.size();i++)
        {
            if (chargers.get(i).reId() == id)
                y = i;
        }
        return y;
    }
    
    /**
     * Returns the Charger which in the slot of the ArrayList structure with the 
     * Charger objects, which is given by the argument xcv.
     * @param xcv The position in which the Charger object is.
     * @return The Charger.
     */
    public final Charger reCharger(int xcv)
    {
        return chargers.get(xcv);
    }
    
    /**
     * Returns the ArrayList structure with the Charger objects.
     * @return The ArrayList structure with the Charger objects.
     */
    public final Charger[] reChargers()
    {
        Charger[] g = new Charger[chargers.size()];
        for(int i = 0; i< chargers.size();i++)
        {
            g[i] = chargers.get(i);
        }
        return g;
    }
    
    /**
     * Returns the ArrayList structure with the sources of energy.
     * @return The ArrayList structure with the kind of energies.
     */
    public final String[] reSources()
    {
        String[] g = new String[sources.size()];
        for(int i = 0;i<sources.size();i++)
        {
            g[i] = sources.get(i);
        }
        return g;
    }
    
    /**
     * Returns a specific kind of energy from the ArrayList structure.
     * @param i The position which is the source which is asked.
     * @return The kind of energy.
     */
    public final String reSource(int i)
    {
        return sources.get(i);
    }
    
    /**
     * Returns the LinkedHashMap structure which has as keys the sources of energy 
     * and as values the energy which has each of them.
     * @return The LinkedHashMap structure with the amounts of each kind of energy.
     */
    public final LinkedHashMap<String,Float> reMap()
    {
        return amounts;
    }
    
    /**
     * Sets an amount of energy in a specific kind of energy.
     * @param v The kind of energy in which the energy is added.
     * @param q The amount of energy which is added.
     */
    public final void setSpecificAmount(String v, float q)
    {
        amounts.put(v,q);
    }
    
    /**
     * Returns the amount of energy a source has.
     * @param y The source of energy.
     * @return The energy of the source.
     */
    public final float reSpecificAmount(String y)
    {
        if (amounts.get(y) == null)
            return 0f;
        return amounts.get(y);
    }
    
    /**
     * Remove an amount of energy from the total energy of the ChargingStation.
     * @param energ The amount of energy which is going to be removed.
     */
    public final void setTotalEnergy(float energ)
    {
        totalEnergy = totalEnergy - energ;
    }
    
    /**
     * Returns the total energy of the ChargingStation.
     * @return The total energy of this ChargingStation.
     */
    public final float reTotalEnergy()
    {
        return totalEnergy;
    }
    
    /**
     * Sets a charging ratio for the slow charging.
     * @param chargingRatio The charging ratio.
     */
    public final void setChargingRatioSlow(float chargingRatio)
    {
        chargingRatioSlow = chargingRatio;
    }
    
    /**
     * Returns the charging ratio of the slow charging.
     * @return The slow charging ratio of this ChargingStation.
     */
    public final float reChargingRatioSlow()
    {
        return chargingRatioSlow;
    }

    /**
     * Sets the charging ratio of the fast charging.
     * @param chargingRatio The fast charging ratio.
     */
    public final void setChargingRatioFast(float chargingRatio)
    {
        chargingRatioFast = chargingRatio;
    }

    /**
     * Returns the charging ratio of the fast charging.
     * @return The fast charging ratio of this ChargingStation.
     */
    public final float reChargingRatioFast()
    {
        return chargingRatioFast;
    }

    /**
     * Sets a discharging ratio.
     * @param disChargingRatio The discharging ratio.
     */
    public final void setDisChargingRatio(float disChargingRatio)
    {
        this.disChargingRatio = disChargingRatio;
    }

    /**
     * Returns the discharging ratio.
     * @return The discharging ratio of this ChargingStation.
     */
    public final float reDisChargingRatio()
    {
        return disChargingRatio;
    }
    
    /**
     * Returns the assisant of the ChargingStation.
     * @return The ChargingStationHandler object which is refered to this ChargingStation.
     */
    public final ChargingStationHandler reChargingStationHandler()
    {
        return m;
    }

    /**
     * Returns the EnergySource in the given position.
     * @param spot The position of the EnergySource in the ArrayList structure.
     * @return The EnergySource.
     */
    public final EnergySource reEnergySource(int spot)
    {
        return n.get(spot);
    }

    /**
     * Searches for the EnergySource of the given source.
     * @param source The source for which the EnergySource object is asked.
     * @return The EnergySource.
     */
    public final EnergySource searchEnergySource(String source)
    {
        if ("solar".equals(source))
        {
            for (int i=0;i<sources.size();i++)
            {
                if (n.get(i) instanceof Solar)
                    return n.get(i);
            }
        }
        else if ("wind".equals(source))
        {
            for (int i=0;i<sources.size();i++)
            {
                if (n.get(i) instanceof Wind)
                    return n.get(i);
            }
        }
        else if ("wave".equals(source))
        {
            for (int i=0;i<sources.size();i++)
            {
                if (n.get(i) instanceof Wave)
                    return n.get(i);
            }
        }
        else if ("hydroelectric".equals(source))
        {
            for (int i=0;i<sources.size();i++)
            {
                if (n.get(i) instanceof HydroElectric)
                    return n.get(i);
            }
        }
        else if ("geothermal".equals(source))
        {
            for (int i=0;i<sources.size();i++)
            {
                if (n.get(i) instanceof Geothermal)
                    return n.get(i);
            }
        }
        else if ("nonrenewable".equals(source))
        {
            for (int i=0;i<sources.size();i++)
            {
                if (n.get(i) instanceof NonRenewable)
                    return n.get(i);
            }
        }
        return null;
    }

    /**
     * Sets a price for the energy unit.
     * @param price The price.
     */
    public final void setUnitPrice(float price)
    {
        unitPrice = price;
    }

    /**
     * Returns the price of the energy unit.
     * @return The price of the energy unit of this ChargingStation.
     */
    public final float reUnitPrice()
    {
        return unitPrice;
    }

    /**
     * Sets a price for the energy unit in a DischargingEvent.
     * @param disUnitPrice The price of energy unit.
     */
    public final void setDisUnitPrice(float disUnitPrice)
    {
        this.disUnitPrice = disUnitPrice;
    }

    /**
     * Returns the price of the energy unit during a DischargingEvent.
     * @return The price of the energy unit.
     */
    public final float reDisUnitPrice()
    {
        return disUnitPrice;
    }

    /**
     * Returns the moment which this ChargingStation was created.
     * @return The time of creation of this ChargingStation.
     */
    public final long reCreationDate()
    {
        return creationDate;
    }

    /**
     * Returns the price of an exchange battery service.
     * @return The price of a battery exchange.
     */
    public final float reExchangePrice()
    {
        return exchangePrice;
    }

    /**
     * Sets the price for a battery exchange.
     * @param price The price the exchange costs.
     */
    public final void setExchangePrice(float price)
    {
        exchangePrice = price;
    }

    /**
     * Adjust the management of the list.
     * @param value The choice of queue handling's. If true the list is handled
     * automatic by the library. If false the user have to handle the list.
     */
    public final void setAutomaticQueueHandling(boolean value)
    {
        automaticQueueHandling = value;
    }

    /**
     * Returns which handles the list.
     * @return True if the list is handled automatic by the library.
     * False if the user has to handle the list.
     */
    public final boolean reQueueHandling()
    {
        return automaticQueueHandling;
    }

    /**
     * Returns the duration of a time moment. The ChargingStation is designed to 
     * support its functions for 500 time moments. By default a time moment lasts
     * 1 second. That means that the ChargingStation will last 500*1(time moment)
     * seconds.
     * @return The time moment duration.
     */
    public final int reTimeMoment()
    {
        return timeMoment;
    }

    /**
     * Sets the duration of each time moment.
     * @param timeMoment The duration of each time moment.
     */
    public final void setTimeMoment(int timeMoment)
    {
        this.timeMoment = timeMoment;
    }

    /**
     * Sets the space which will be among two storage's updates. The updateMode gets 1.
     * @param updateSpace The time space.
     */
    public final void setUpdateSpace(int updateSpace)
    {
        this.updateSpace = updateSpace;
        updateMode = 1;
    }

    /**
     * Returns the time of each update storage.
     * @return The time among each storage update.
     */
    public final int reUpdateSpace()
    {
        return updateSpace;
    }
    
    /**
     * Checks the batteries which are for battery exchange to confirm which of them
     * need charging. It returns an ArrayList structure with so ChargingEvent 
     * objects as the empty slots in the ChargingStation. The ChargingEvent objects 
     * do not need preProcessing function, only execution.
     * @param kind The kind of charging the user wants to charge the batteries.
     * @return An ArrayList structure with the ChargingEvent objects ready to be 
     * executed(only method execution() is needed).
     */
    public final ArrayList<ChargingEvent> batteriesCharging(String kind)
    {
        ChargingEvent e;
        ElectricVehicle r;
        ArrayList<ChargingEvent> events = new ArrayList<ChargingEvent>();
        int counter = 0;
        for(int i=0;i<batteries.size();i++)
        {
            if (batteries.get(i).reRemAmount() < batteries.get(i).reBatteryCapacity())
            {
                r = new ElectricVehicle(1,null,0);
                r.vehicleJoinBattery(batteries.get(i));
                e = new ChargingEvent(this,r,batteries.get(i).reBatteryCapacity() - batteries.get(i).reRemAmount(),kind);
                if (checkChargers(e.reKind()) != -1)
                {
                    e.preProcessing();
                    events.add(counter, e);
                    counter++;
                }
                else
                {
                    break;
                }
            }
        }
        return events;
    }

    /**
     * Returns an array with all the EnergySource objects of the ChargingStation.
     * @return The array with the EnergySource objects of theChargingStation.
     */
    public final EnergySource[] reEnergySources()
    {
        EnergySource[] g = new EnergySource[n.size()];
        for(int i = 0; i<n.size(); i++)
        {
            g[i] = n.get(i);
        }
        return g;
    }
    
    /**
     * Returns the updateMode of the ChargingStation. If 0 the user has chosen an 
     * update space for update storage from the choices the ChargingStation offers. 
     * If 1 the user has chosen an own time space.
     * @return 0 for a choice from the given,1 for another choice of update.
     */
    public final int reUpdateMode()
    {
        return updateMode;
    }
    
    /**
     * Sets the time a battery exchange service lasts.
     * @param time The time the battery exchange lasts.
     */
    public final void setTimeofExchange(int time)
    {
        timeOfExchange = time;
    }
    
    /**
     * Returns the time the battery exchange lasts.
     * @return The time of the battery exchange.
     */
    public final int reTimeOfExchange()
    {
        return timeOfExchange;
    }
}