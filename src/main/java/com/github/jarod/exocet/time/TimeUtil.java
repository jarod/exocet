package com.github.jarod.exocet.time;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.jarod.exocet.concurrent.ExocetExecutor;

public class TimeUtil {
	protected volatile static long _systemTime = System.currentTimeMillis();

	public static final long MILLIS_PER_SECOND = 1000L;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60L;
	public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60L;
	public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24L;

	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
	public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
	public static final int SECONDS_PER_WEEK = SECONDS_PER_DAY * 7;
	public static final int SECONDS_PER_MONTH = SECONDS_PER_DAY * 30;

	private static final long systemTimeTick = Long.parseLong(System.getProperty("systime.time.tick", "200"));

	static {
		ExocetExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				_systemTime = System.currentTimeMillis();
			}
		}, systemTimeTick, systemTimeTick, TimeUnit.MILLISECONDS);
	}

	public static Date dateNow() {
		return new java.sql.Date(_systemTime);
	}

	/**
	 * A cached version of System.currentTimeMillis， updated every 200ms by default. Can be changed by setting value of
	 * system property <i>systime.time.tick</i>.
	 */
	public static long systemTime() {
		return _systemTime;
	}

	public static int systemTimeSec() {
		return (int) (_systemTime / MILLIS_PER_SECOND);
	}

	public static Timestamp timestampNow() {
		return new Timestamp(_systemTime);
	}
}
