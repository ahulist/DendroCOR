/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class LogsSaver {

    private final static LogsSaver INSTANCE = new LogsSaver();
    private final static String LOGS_DIR = ".\\logs";
    private final static int DAYS_LOGGED = 7;
    private final static String NAME_PREFIX = "log_";
    private final static String NAME_SUFFIX = ".txt";

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static boolean isFirstRun = true;

    private class DatesComparator implements Comparator<Date> {

        @Override
        public int compare(Date o1, Date o2) {
            return o1.compareTo(o2);
        }
    }

    private LogsSaver() {
    }

    public static LogsSaver getInstance() {
        if( isFirstRun ){
            if( INSTANCE.isLogsDirExist() ){
                INSTANCE.cleanupDir();
            } else {
                INSTANCE.createLogsDir();
            }
            SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
            INSTANCE.log(sd.format(new Date()) + " ===================================================\n");
            isFirstRun = false;
        }

        return INSTANCE;
    }

    public void log(String msg) {
        try( PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOGS_DIR + "\\" + NAME_PREFIX + df.format(new Date()) + NAME_SUFFIX, true))) ) {
            out.println(msg);
        } catch( IOException e ) {
            log.log(Level.WARNING, java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("BŁĄD PODCZAS ZAPISU DANYCH DO LOGU."));
            log.log(Level.FINEST, e.getLocalizedMessage());
        }
    }

    private void cleanupDir() {
        SortedMap<Date, File> dates = new TreeMap<>(new DatesComparator());
        File folder = new File(LOGS_DIR);
        for( File file : folder.listFiles() ) {
            if( file.isFile() ){
                String name = file.getName();
                if( name.startsWith(NAME_PREFIX) && name.endsWith(NAME_SUFFIX) ){
                    String dateString = name.substring(NAME_PREFIX.length(), name.length() - NAME_SUFFIX.length());
                    Date date = null;
                    try {
                        date = df.parse(dateString);
                    } catch( ParseException ex ) {
                    }
                    if( date != null ){
                        dates.put(date, file);
                    }
                }
            }
        }

        int counter = 0;
        int filesToDelete = dates.size() > DAYS_LOGGED ? dates.size() - DAYS_LOGGED : 0;
        for( Map.Entry<Date, File> entry : dates.entrySet() ) {
            if( counter < filesToDelete ){
                File file = entry.getValue();
                if( !file.delete() ){
                    log.log(Level.WARNING, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("BŁĄD PODCZAS USUWANIA LOGU %S"), file.getName()));
                }
                counter++;
            } else {
                break;
            }
        }
    }

    private boolean isLogsDirExist() {
        File f = new File(LOGS_DIR);
        return f.exists() && f.isDirectory();
    }

    private boolean createLogsDir() {
        return new File(LOGS_DIR).mkdir();
    }

}
