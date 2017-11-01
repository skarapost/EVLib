package evlib.station;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitList<T>
{
    private final ArrayList<T> list;
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private Lock lock = new ReentrantLock();

    /**
     * Creates a new WaitingList object.
     */
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
    public void add(T object) {
        lock.lock();
        try {
            list.add(object);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deletes an object from the list.
     * @param object The object to be deleted.
     * @return True if the deletion was successfull, false if it was not.
     */
    public boolean delete(T object) {
        lock.lock();
        try {
            return list.remove(object);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the first object in the list.
     * @return The first object.
     */
    public T takeFirst()
    {
        lock.lock();
        try {
            return list.get(0);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the first object of the list.
     * @return The removed object.
     */
    public T moveFirst() {
        lock.lock();
        try {
            return list.remove(0);
        } finally {
            lock.unlock();
        }
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

    /**
     * Sets the id for the WaitingList.
     * @param id The id to be set.
     */
    public void setId(int id) { this.id = id; }
}
