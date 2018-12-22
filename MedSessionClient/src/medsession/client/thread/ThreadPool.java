package medsession.client.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel
 */
public class ThreadPool {
	private static int defaultMaxThread = 1000;
	private static long defaultDelayToStart = 500L;
	private static int defaultTryToTerminate = 10;

	private ScheduledThreadPoolExecutor executor;
	private int tryToTerminate;
	private long delayToStart;

	public ThreadPool() {
		executor = null;
		tryToTerminate = -1;
		delayToStart = -1L;
	}

	public void setDelayToStart(long time) {
		this.delayToStart = time;
	}

	/**
	 * 
	 */
	public void close() {
		System.out.println("Shuting down the thread pool...............");
		int counter = tryToTerminate;
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
		}
		if (counter == -1) {
			counter = defaultTryToTerminate;
		}
		while (executor.isTerminating() && counter > 0) {
			counter--;
			// wait for another 1 second
			try {
				System.out.println("Sleep for 1 second to wait for all tasks stop");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean canClose() {
		int active = executor.getActiveCount();
		System.out.println("active count: " + active);
		return active == 0;
	}
	
	public int active() {
		return executor.getActiveCount();
	}

	/**
	 * 
	 */
	public void init() {
		System.out.println("Maximum thread pool Size: " + defaultMaxThread);
		System.out.println("Maximum thread delayed to start time in millisecond: " + defaultDelayToStart);
		executor = new ScheduledThreadPoolExecutor(defaultMaxThread, Executors.defaultThreadFactory(),
				new SimpleRejectedExecutionHandler());
	}

	/**
	 * 
	 * @param maxThread
	 */
	public void init(int maxThread) {
		if (maxThread != 0) {
			System.out.println("Max Thread Pool Size: " + maxThread);
			System.out.println("Max thread delayed to start time in millisecond: " + defaultDelayToStart);
			executor = new ScheduledThreadPoolExecutor(maxThread, Executors.defaultThreadFactory(),
					new SimpleRejectedExecutionHandler());
		} else {
			System.out.println("Maximum thread pool Size: " + defaultMaxThread);
			System.out.println("Maximum thread delayed to start time in millisecond: " + defaultDelayToStart);
			executor = new ScheduledThreadPoolExecutor(defaultMaxThread, Executors.defaultThreadFactory(),
					new SimpleRejectedExecutionHandler());
		}
	}

	/**
	 * produce a thread waiting to run
	 * 
	 * @param job
	 */
	public void spawn(Runnable job) {
		if (delayToStart != -1L) {
			executor.schedule(job, delayToStart, TimeUnit.MILLISECONDS);
		} else {
			executor.schedule(job, defaultDelayToStart, TimeUnit.MILLISECONDS);
		}
	}
}
