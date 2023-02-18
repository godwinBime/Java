import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**Basic Unit Tests for BoundedBuffer
 **/
public class BoundedBufferTest extends TestCase {
    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private final int CAPACITY = 10;
    public void testIsEmptyWhenConstructed(){
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        assertTrue(bb.isEmpty());
        assertFalse(bb.isFull());
    }

    public void testIsFullAfterPuts()throws InterruptedException{
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        for (int i = 0; i < 10; i++){
            bb.put(i);
        }
        assertTrue(bb.isFull());
        assertFalse(bb.isEmpty());
    }

    /**Testing blocking and responsiveness to interrupt
     **/
    public void testTakeBlockWhenEmpty(){
        final BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        Thread taker = new Thread(){
            public void run(){
                try {
                    int unUsed = bb.take();
                    fail();//if we get here, it's an error
                    System.out.println("Failed....");
                }catch (InterruptedException success){

                }
            }
        };
        try {
            taker.start();
            Thread.sleep(5000);
            taker.interrupt();
            taker.join(5000);
            assertFalse(taker.isAlive());
        }catch (Exception unExpected){
            fail();
        }
    }

    /**Testing for resource leaks
     *
     * @throws InterruptedException
     *
    class Big{
        double[] data = new double[100000];
    }

    public void testLeak() throws InterruptedException{
        BoundedBuffer<Big> bb = new BoundedBuffer<>(CAPACITY);
        int heapSize1; //Snapshot heap
        for (int i = 0; i < CAPACITY; i++)
            bb.put(new Big());
        for (int i = 0; i < CAPACITY; i++)
            bb.take();
        int heapSize2; //Snapshot heap
       // assertTrue(Math.abs(heapSize1 - heapSize2) < THRESHOLD);
    }*/

    /**
     * ThreadFactory for testing ThreadPoolExecutor
     */
    class TestingThreadFactory implements ThreadFactory{
        public final AtomicInteger numCreated = new AtomicInteger();
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r){
            numCreated.incrementAndGet();
            return factory.newThread(r);
        }
    }
}
