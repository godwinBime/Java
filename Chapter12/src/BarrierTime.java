/**Barrier based timer
 **/
public class BarrierTime implements Runnable{
    private boolean started;
    private long startTime;
    private long endTime;

    public BarrierTime(){}

    public synchronized void run(){
        long t = System.nanoTime();
        if (!started){
            started = true;
            startTime = t;
        }else
            endTime = t;
    }

    public synchronized void clear(){
        started = false;
    }

    public synchronized long getTime(){
        return endTime - startTime;
    }
}
