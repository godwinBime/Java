import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;

import static junit.framework.Assert.assertEquals;

/**Producer-Consumer Test Program for BoundedBuffer
 **/
public class PutTakeTest {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final AtomicInteger putSum = new AtomicInteger();
    private final AtomicInteger takeSum = new AtomicInteger();
    private final CyclicBarrier barrier;
    private final BoundedBuffer<Integer> bb;
    private final BarrierTime timer;
    private final int nTrials;
    private final int nPairs;

    public PutTakeTest(int capacity, int nPairs, int nTrials){
        this.bb = new BoundedBuffer<>(capacity);
        this.nTrials = nTrials;
        this.nPairs = nPairs;
        this.barrier = new CyclicBarrier(nPairs * 2 + 1);
        timer = new BarrierTime();
    }
    public static void main(String[] args)throws Exception{
        int tpt = 100000; //trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10){
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs <= 128; pairs *= 2){
                PutTakeTest t = new PutTakeTest(cap, pairs, tpt);
                System.out.print("Pairs: " + pairs + "\t");
                t.test();
                System.out.print("\t");
                Thread.sleep(1000);
                t.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        pool.shutdown();
    }

    /**
    public void test(){
        try {
            for (int i = 0; i < nPairs; i++){
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await(); //Wait for all threads to be ready
            barrier.await(); //Wait for all threads to finish
            assertEquals(putSum.get(), takeSum.get());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
     */

    /**
     * Testing with a barrier-based timer
     */

    /**Inner class of PutTakeTest
     **/
    public void test(){
        try {
            timer.clear();
            for (int i = 0; i < nPairs; i++){
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await();
            barrier.await();
            long nsPerItem = timer.getTime() / (nPairs * (long)nTrials);
            System.out.print("Throughput: " + nsPerItem + " ns/time");
            assertEquals(putSum.get(), takeSum.get());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    class Producer implements Runnable{
        public void run(){
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await();
                for (int i = nTrials; i > 0; --i){
                    bb.put(seed);
                    sum += seed;
                    seed = bb.xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable{
        public void run(){
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; --i)
                    sum += bb.take();
                takeSum.getAndAdd(sum);
                barrier.await();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
