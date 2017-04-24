package Station;

/**
 *
 * @author Sotiris Karapostolakis
 */
import Events.DisChargingEvent;
import Events.ChargingEvent;
import java.util.LinkedList;

public class Queue
{
    private LinkedList<ChargingEvent> list1;
    private LinkedList<DisChargingEvent> list2;

    /**
     * Constructor of Queue class.
     * @param n The kind of queue. If "charging" it creates a list to store 
     * ChargingEvent objects. If "discharging" it creates a list to store 
     * DisChargingEvent objects.
      */
    public Queue(String n)
    {
        if ("charging".equals(n))
            list1 = new LinkedList<ChargingEvent>();
        else if ("discharging".equals(n))
            list2 = new LinkedList<DisChargingEvent>();
    }

    /**
     * Returns the ChargingEvent in the given place.
     * @param index The place of the ChargingEvent in the list.
     * @return The ChargingEvent object.
     */
    public ChargingEvent peek(int index)
    {
        return list1.get(index);
    }

    /**
     * Returns the DisChargingEvent in the given place.
     * @param index The place of the DisChargingEvent in the list.
     * @return The DisChargingEvent object.
     */
    public final DisChargingEvent get(int index)
    {
        return list2.get(index);
    }

    /**
     * Inserts a ChargingEvent in the list.
     * @param p The ChargingEvent to be inserted.
     * @return True if the insertion was successfull, false if it was not.
     */
    public final boolean insertElement(ChargingEvent p)
    {
        return list1.add(p);
    }

    /**
     * Deletes a ChargingEvent from the list.
     * @param m The ChargingEvent to be deleted.
     * @return True if the deletion was successfull, false if it was not.
     */
    public final boolean deleteElement(ChargingEvent m)
    {
        return list1.remove(m);
    }

    /**
     * Returns the first ChargingEvent of the list.
     * @return The first ChargingEvent.
     */
    public final ChargingEvent takeFirst()
    {
        return list1.getFirst();
    }

    /**
     * Removes the first ChargingEvent.
     * @return True if the deletion was successfull, false if it was not.
     */
    public final ChargingEvent removeFirst()
    {
        return list1.removeFirst();
    }

    /**
     * Returns the size of the list for the ChargingEvent.
     * @return The size of ChargingEvent list.
     */
    public final int reSize()
    {
        return list1.size();
    }

    /**
     * Inserts a DisChargingEvent to the list.
     * @param p The DisChargingEvent to be inserted.
     * @return True if the insertion was successfull, false if it was not.
     */
    public final boolean insertElement(DisChargingEvent p)
    {
        return list2.add(p);
    }

    /**
     * Deletes a DisChargingEvent from the list.
     * @param m The DisChargingEvent to be deleted.
     * @return True if it was successfull, false if it was not.
     */
    public final boolean deleteElement(DisChargingEvent m)
    {
        return list2.remove(m);
    }

    /**
     * Returns the first DisChargingEvent of the list.
     * @return The first DisChargingEvent of the list.
     */
    public final DisChargingEvent reFirst()
    {
        return list2.getFirst();
    }

    /**
     * Removes the first DisChargingEvent of the list.
     * @return True if it was successfull, false if it was not.
     */
    public final DisChargingEvent moveFirst()
    {
        return list2.removeFirst();
    }

    /**
     * Returns the size of the DisChargingEvent list.
     * @return The size of the DisChargingEvent list.
     */
    public final int rSize()
    {
        return list2.size();
    }
    
    /**
     * Removes the ChargingEvent in the given position.
     * @param index The position of the ChargingEvent.
     * @return True if the deletion was successfull, false if it was not.
     */
    public final ChargingEvent removeChargingEvent(int index)
    {
        return list1.remove(index);
    }
    
    /**
     * Removes the DisChargingEvent int he given position.
     * @param index The position of the DisChargingEvent.
     * @return True if the deletion was successfull, false if it was not.
     */
    public final DisChargingEvent removeDisChargingEvent(int index)
    {
        return list2.remove(index);
    }
}