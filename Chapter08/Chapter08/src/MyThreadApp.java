import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Custom thread base class
 *
 */
public class MyThreadApp extends Thread{
    public static final String DEFAULT_NAME = "MyThreadApp";
    private static volatile boolean debugLifeCycle = false;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();

    public MyThreadApp(Runnable r){
        this(r, DEFAULT_NAME);
    }

    public MyThreadApp(Runnable runnable, String name){
        super(runnable, name + "_" + created.incrementAndGet());
        setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            public void uncaughtException(Thread t, Throwable e){
                log.log(Level.SEVERE, "UNCAUGHT in thread " + t.getName(), e);
            }
        });
    }

    public void run(){
        /**Copy debug flag to ensure consistent value throughout
         *
         */
        boolean debug = debugLifeCycle;
        if (debug)
            log.log(Level.FINE, "Created " + getName());
        try {
            alive.incrementAndGet();
            super.run();
        }finally {
            alive.decrementAndGet();
            if (debug)
                log.log(Level.FINE, "Exiting " + getName());
        }
    }

    public static int getThreadsCreated(){
        return created.get();
    }

    public static int getThreadsAlive(){
        return alive.get();
    }

    public static boolean getDebug(){
        return debugLifeCycle;
    }

    public static void setDebug(boolean b){
        debugLifeCycle = b;
    }
}
