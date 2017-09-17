package EVLib.Station;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitList<T>
{
    private ArrayList<T> list;
    private int id;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public WaitList()
    {
        this.id = idGenerator.incrementAndGet();
        list = new ArrayList<>();
    }

    /**
     * Returns the object in the given place.
     * @param index The place of the object in the list.
     * @return The asked object.
     */
    public T get(int index)
    {
        return list.get(index);
    }

    /**
     * Inserts an object in the list.
     * @param object The object to be inserted.
     */
    public void add(T object)
    {
        list.add(object);
    }

    /**
     * Deletes an object from the list.
     * @param object The object to be deleted.
     * @return True if the deletion was successfull, false if it was not.
     */
    public boolean delete(T object)
    {
        return list.remove(object);
    }

    /**
     * Returns the first ChargingEvent of the list.
     * @return The first ChargingEvent.
     */
    public T takeFirst()
    {
        return list.get(0);
    }

    /**
     * Removes the first object of the list.
     * @return True if it was successfull, false if it was not.
     */
    public T moveFirst()
    {
        return list.remove(0);
    }

    /**
     * @return The id of the WaitingList.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return The size of the WaitingList.
     */
    public int getSize()
    {
        return list.size();
    }
}