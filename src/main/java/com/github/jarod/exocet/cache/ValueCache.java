package com.github.jarod.exocet.cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import com.github.jarod.exocet.concurrent.ExocetExecutor;
import com.github.jarod.exocet.time.TimeUtil;

public class ValueCache<T> {
	private volatile long nextUpdateTime;
	private final long expire;
	private final ValueLoader<T> loader;
	private final AtomicReference<T> value = new AtomicReference<T>();

	private class LatResult implements Result<T> {
		CountDownLatch lat;

		@Override
		public void set(T v) {
			value.set(v);
			if (lat != null) {
				lat.countDown();
			}
		}

	}

	public ValueCache(final long expireMillis, final ValueLoader<T> loader) {
		this.expire = expireMillis;
		this.loader = loader;
	}

	public T get() {
		final long updateTime = nextUpdateTime;
		final long now = TimeUtil.systemTime();
		if (now > updateTime) {
			nextUpdateTime = now + expire;
			try {
				LatResult result = new LatResult();
				if (updateTime == 0L) {
					result.lat = new CountDownLatch(1);
				}
				ExocetExecutor.execute(() -> {
					try {
						loader.load(result);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				});
				if (updateTime == 0L) {
					result.lat.await();
				}
			} catch (final Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return value.get();
	}
}
