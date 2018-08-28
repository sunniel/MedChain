package medsession.client.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Daniel
 */
public class SimpleRejectedExecutionHandler implements RejectedExecutionHandler {
	/**
	 * @param r
	 * @param executor
	 * @return
	 */
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		System.out
				.println("The job <" + r.toString() + "> is rejected by the Thread Pool <" + executor.toString() + ">");
		System.out.println("The pool size is: " + executor.getPoolSize());
		System.out.println("The current number of threads in the pool is: " + executor.getPoolSize());
		System.out.println("The number of active tasks running in the pool is: " + executor.getActiveCount());
		System.out.println(
				"It is because no more threads or queue slots are available because their bounds would be exceeded, "
						+ "or upon shutdown of the Executor");
	}
}
