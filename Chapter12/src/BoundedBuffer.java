import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Semaphore;

/**Bounded buffer using Semaphore
 *
 * @param <E>
 */
@ThreadSafe
public class BoundedBuffer <E>{
    private final Semaphore availableItems;
    private final Semaphore getAvailableSpaces;
    @GuardedBy("this") private final E[] items;
    @GuardedBy("this") private int putPosition = 0;
    @GuardedBy("this") private int takePosition = 0;

    public BoundedBuffer(int capacity){
        availableItems = new Semaphore(0);
        getAvailableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty(){
        return availableItems.availablePermits() == 0;
    }

    public boolean isFull(){
        return getAvailableSpaces.availablePermits() == 0;
    }

    public void put(E x) throws InterruptedException{
        getAvailableSpaces.acquire();
        doInsert(x);
        availableItems.release();
    }

    public E take() throws InterruptedException{
        availableItems.acquire();
        E item = doExtract();
        getAvailableSpaces.release();
        return item;
    }

    private synchronized void doInsert(E x){
        int i = putPosition;
        items[i] = x;
        putPosition = (++i == items.length) ? 0 : i;
    }

    private synchronized E doExtract(){
        int i = takePosition;
        E x = items[i];
        takePosition = (i++ == items.length) ? 0 : i;
        return x;
    }

    /**Random number generator
     **/
    public static int xorShift(int y){
        y ^= (y << 6);//signed left shift operator
        y ^= (y >>> 21);// unsigned right shift operator
        y ^= (y << 7);
        return y;
    }
}
