package com.github.jarod.exocet.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExocetExecutor {
	private static class ExocetThreadFactory implements ThreadFactory {
		private static final AtomicInteger threadId = new AtomicInteger();

		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(r);
			t.setName("exocet-" + threadId.getAndIncrement());
			return t;
		}
	}

	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2,
			new ExocetThreadFactory());

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executor.shutdown();
			}
		});
	}

	public static ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay,
			final long period, final TimeUnit unit) {
		return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	public static <T> Future<T> submit(final Callable<T> task) {
		return executor.submit(task);
	}

	public static ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
		return executor.schedule(command, delay, unit);
	}
	
	public static void execute(Runnable command) {
		executor.execute(command);
	}
}
