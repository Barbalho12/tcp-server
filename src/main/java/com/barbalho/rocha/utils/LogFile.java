package com.barbalho.rocha.utils;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {

    private static final String OUTPUT_LOG = "SERVER_LOG.txt";
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

    synchronized public static void log(String row) {

        String text = format.format(new Date())+"\t"+row +"\n";
        try (FileOutputStream outputStream = new FileOutputStream(OUTPUT_LOG, true)){
            byte[] strToBytes = text.getBytes();
            outputStream.write(strToBytes);
            // outputStream.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    
}