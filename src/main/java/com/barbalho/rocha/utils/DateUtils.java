package com.barbalho.rocha.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class with utilities for working with dates
 * 
 * @author Felipe Barbalho
 *
 */
public class DateUtils {

	public static byte[] getDateTimeByFuse(final String fuse) {
		final byte[] bytes = new byte[6];
		final Date date = new Date();
		final LocalDateTime localDate = date.toInstant().atZone(ZoneId.of(fuse)).toLocalDateTime();
		bytes[0] = (byte) localDate.getYear();
		bytes[1] = (byte) localDate.getMonthValue();
		bytes[2] = (byte) localDate.getDayOfMonth();
		bytes[3] = (byte) localDate.getHour();
		bytes[4] = (byte) localDate.getMinute();
		bytes[5] = (byte) localDate.getSecond();
		return bytes;
	}

}