import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class LogService {
    /**Adding reliable cancellation to LogWriter**/
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;

    public LogService(BlockingQueue<String> queue, LoggerThread loggerThread, PrintWriter writer){
        this.queue = queue;
        this.loggerThread = loggerThread;
        this.writer = writer;
    }

    @GuardedBy("this")
    private boolean isShutdown;

    @GuardedBy("this")
    private int reservations;

    public void start(){
        loggerThread.start();
    }

    /**Registering a shutdown Hook to stop the Logging Service
     *
     */
    public void start1(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                LogService.this.stop();
            }
        });
    }

    public void stop(){
        synchronized (this){
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg)throws InterruptedException{
        synchronized (this){
            if (isShutdown)
                throw new IllegalStateException();
            ++reservations;
        }
        queue.put(msg);
    }



    private class LoggerThread extends Thread{
        public void run(){
            try {
                while (true){
                    try {
                        synchronized (LogService.this){
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    }catch (InterruptedException e){
                        /**retry**/
                    }
                }
            }finally {
                writer.close();
            }
        }
    }
}
