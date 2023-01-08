import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

@ThreadSafe
class PrimeGenerator implements Runnable{
    @GuardedBy("this")
    private final List<BigInteger> primes = new ArrayList<>();
    private volatile boolean cancelled;

    public void run(){
        BigInteger p = BigInteger.ONE;
        while (!cancelled){
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel(){
        cancelled = true;
    }

    public synchronized List<BigInteger> get(){
        return new ArrayList<>(primes);
    }

    List<BigInteger> aSecondOfPrimes() throws InterruptedException{
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator);
        try {
            SECONDS.sleep(1);
        }finally {
            generator.cancel();
        }
        return generator.get();
    }
}

class PrimeProducer extends Thread{
    private final BlockingQueue<BigInteger> queue;

    PrimeProducer(BlockingQueue<BigInteger> queue){
        this.queue = queue;
    }

    public void run(){
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()){
                queue.put(p = p.nextProbablePrime());
            }
        }catch (InterruptedException consumed){
            /**Allow thread to exit**/
        }
    }

    public void cancel(){
        interrupt();
    }

    /**Cancelling a task using Future**/
    public static void timedRun(final Runnable r, long timeout, TimeUnit unit)throws InterruptedException{
       // Future<?> task = taskExec.submit(r);
    }

}