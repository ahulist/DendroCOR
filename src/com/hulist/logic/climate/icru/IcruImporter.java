/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.climate.icru;

import com.hulist.gui.MainWindow;
import com.hulist.logic.BaseImporter;
import com.hulist.logic.DataImporter;
import com.hulist.logic.RunParams;
import com.hulist.util.Misc;
import com.hulist.util.Months;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class IcruImporter extends BaseImporter implements DataImporter<IcruDataContainer> {

    public static final double ICRU_VALUE_MAX = 998;//Double.MAX_VALUE;
    public static final double ICRU_VALUE_MIN = -998;//-Double.MAX_VALUE;

    public IcruImporter(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<IcruDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        IcruDataContainer container = new IcruDataContainer(f);

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        while( (line = br.readLine()) != null ) {
            if( !line.startsWith("#") && !line.trim().equals("") ){
                String[] data = line.trim().split("[\\s\\t]+");
                try {
                    if( allYears
                            || (!allYears
                            && Integer.parseInt(data[0]) >= startYear
                            && Integer.parseInt(data[0]) <= endYear) ){

                        //assert data.length == 13;
                        if (!(data.length == 13)) {
                            throw new IOException();
                        }
                        int year = Integer.parseInt(data[0]);
                        IcruLineContainer lineData = new IcruLineContainer(year);
                        int counter = 1;
                        for( Months month : Months.values() ) {
                            double value = Double.parseDouble(data[counter]);
                            if( value < ICRU_VALUE_MIN || value > ICRU_VALUE_MAX ){
                                StringBuilder sb = new StringBuilder();
                                if( value < ICRU_VALUE_MIN ){
                                    sb.append(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ODCZYTANA WARTOŚĆ < ")).append(ICRU_VALUE_MIN);
                                }
                                if( value > ICRU_VALUE_MAX ){
                                    sb.append(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ODCZYTANA WARTOŚĆ > ")).append(ICRU_VALUE_MAX);
                                }
                                sb.append(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString(", W PLIKU ")).append(f.getCanonicalPath()).append(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString(" DLA ROKU ")).append(year).append(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString(", DLA MIESIĄCA ")).append(month);
                                throw new IllegalArgumentException(sb.toString());
                            }
                            lineData.addMonthlyData(month, value);
                            counter++;
                        }
                        container.addYearlyData(year, lineData);
                    }
                } catch( AssertionError e ) {
                    String msg = String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("błędna liczba kolumn"), f.getName(), lineCounter);
                    log.log(Level.WARNING, msg);
                    log.log(Level.FINEST, Misc.stackTraceToString(e));
                    throw new IOException(msg);
                } catch( NumberFormatException e ) {
                    String msg = String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("błędny format liczby"), f.getName(), lineCounter);
                    log.log(Level.WARNING, msg);
                    log.log(Level.FINEST, Misc.stackTraceToString(e));
                    throw new IOException(msg);
                } catch( IllegalArgumentException e ) {
                    String msg = e.getMessage();
                    log.log(Level.WARNING, msg);
                    log.log(Level.FINEST, Misc.stackTraceToString(e));
                    throw new IOException();
                } catch( IOException e ) {
                    String msg = String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĘDNY FORMAT PLIKU %S W LINII %D."), f.getName(), lineCounter);
                    log.log(Level.WARNING, msg);
                    log.log(Level.FINEST, Misc.stackTraceToString(e));
                    throw new IOException(msg);
                }

            }
            lineCounter++;
        }

        br.close();

        ArrayList<IcruDataContainer> containerArr = new ArrayList<>(1);
        containerArr.add(container);
        return containerArr;
    }

}
