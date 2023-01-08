import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**shutdown service with poison pill
 *
 */
public class IndexingService {
    private static final File POISON = new File("");
    private final IndexerThread consumer = new IndexerThread();
    private final CrawlerThread producer = new CrawlerThread();
    private final BlockingQueue<File> queue;
    private final FileFilter fileFilter;
    private final File root;

    public IndexingService(BlockingQueue<File> queue, FileFilter fileFilter, File root) {
        this.queue = queue;
        this.fileFilter = fileFilter;
        this.root = root;
    }

    /**
     * Producer thread for IndexingService
     */
    class CrawlerThread extends Thread {
        //Listing 7.18
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                //fall through
            } finally {
                while (true) {
                    try {
                        queue.put(POISON);
                        break;
                    } catch (InterruptedException e1) {
                        //retry
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException {
            //...
        }
    }

    /**
     * Consumer Thread for IndexingService
     */
    class IndexerThread extends Thread {
        //Listing 7.19
        public void run() {
            try {
                while (true) {
                    File file = queue.take();
                    if (file == POISON)
                        break;
                    else {
                        //   indexFile(file);
                    }
                }
            } catch (InterruptedException consumed) {

            }
        }
    }

    public void start() {
        producer.start();
        consumer.start();
    }

    public void stop() {
        producer.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }

    /**
     * Using a private Executor whose lifetime is bounded
     * by a method call
     */
    boolean checkMail(Set<String> hosts, long timeOut, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (final String host : hosts)
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (checkMail(hosts, timeOut + 1, null))//different
                                hasNewMail.set(true);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        } finally {
            exec.shutdown();
            exec.awaitTermination(timeOut, unit);
        }
        return hasNewMail.get();
    }
}

    /**UncaughtExceptionHandler that logs thte exception
     *
     */
class UELogger implements Thread.UncaughtExceptionHandler{
        public void uncaughtException(Thread t, Throwable e){
            Logger logger = Logger.getAnonymousLogger();
            logger.log(Level.SEVERE, "Thread terminated with exception: " + t.getName(), e);
        }
}
