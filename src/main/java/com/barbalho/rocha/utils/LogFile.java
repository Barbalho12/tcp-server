package com.barbalho.rocha.utils;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Used to log into file
 * 
 * @author Felipe Barbalho
 *
 */
public class LogFile {

	private static final String OUTPUT_LOG = "SERVER_LOG.txt";
	private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

	/**
	 * Save the received argument to the OUTPUT_LOG file. Adding the registration
	 * time at the beginning
	 * 
	 * @param row Conteúdo do registro
	 */
	synchronized public static void log(String row) {
		String text = format.format(new Date()) + "\t" + row + "\n";
		try (FileOutputStream outputStream = new FileOutputStream(OUTPUT_LOG, true)) {
			byte[] strToBytes = text.getBytes();
			outputStream.write(strToBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}