package com.github.jarod.exocet.time;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.jarod.exocet.concurrent.ExocetExecutor;

public class TimeUtil {
	private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);
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

	/** YYYY-MM-dd HH:mm:ss */
	public static final DateTimeFormatter FORMATTER_YMD_HMS = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
	/** YYYY-MM-dd */
	public static final DateTimeFormatter FORMATTER_YMD = DateTimeFormatter.ofPattern("YYYY-MM-dd");

	private static final long systemTimeTick = Long.parseLong(System.getProperty("systime.time.tick", "200"));

	private volatile static long _dayBeginTime;

	static {
		calDayBeginTime();
	}

	private static void calDayBeginTime() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.clear(Calendar.MILLISECOND);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MINUTE);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		_dayBeginTime = calendar.getTimeInMillis();
		final long delay = (_dayBeginTime + TimeUtil.MILLIS_PER_DAY) - _systemTime;
		ExocetExecutor.schedule(() -> calDayBeginTime(), delay, TimeUnit.MILLISECONDS);
	}

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

	public static long todayBeginTime() {
		return _dayBeginTime;
	}

	public static LocalDateTime localDateTimeNow() {
		final long second = _systemTime / MILLIS_PER_SECOND;
		final int nano = (int) (_systemTime % MILLIS_PER_SECOND) * 1_000_000;
		return LocalDateTime.ofEpochSecond(second, nano, ZONE_OFFSET);
	}

	private static final WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);

	public static int weekOfYear() {
		return weekOfYear(localDateTimeNow());
	}

	public static int weekOfYear(final TemporalAccessor accessor) {
		return accessor.get(weekFields.weekOfYear());
	}
}
