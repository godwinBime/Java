import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**Interface to be implemented for Encapsulating non-standard cancellation in a task with NewTaskFor
 *
 * @param <T>
 */
public interface CancellableTask<T> extends Callable<T> {
    void cancel();
    RunnableFuture<T> newTask();
}
