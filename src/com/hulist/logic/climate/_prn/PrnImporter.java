/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.climate._prn;

import com.hulist.logic.BaseImporter;
import com.hulist.logic.DataImporter;
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
public class PrnImporter extends BaseImporter implements DataImporter<PrnDataContainer> {

    public static final double PRN_VALUE_MAX = Double.MAX_VALUE;
    public static final double PRN_VALUE_MIN = Double.MIN_VALUE;

    public PrnImporter(boolean isAllYears, int startYear, int endYear) {
        super(isAllYears, startYear, endYear);
    }

    @Override
    public ArrayList<PrnDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        PrnDataContainer container = new PrnDataContainer(f);

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        while( (line = br.readLine()) != null ) {
            String[] data = line.trim().split("[\\s\\t]+");
            try {
                if( allYears
                        || (!allYears
                        && Integer.parseInt(data[0]) >= startYear
                        && Integer.parseInt(data[0]) <= endYear) ){

                    assert data.length == 13;
                    int year = Integer.parseInt(data[0]);
                    PrnLineContainer lineData = new PrnLineContainer(year);
                    int counter = 1;
                    for( Months month : Months.values() ) {
                        double value = Double.parseDouble(data[counter]);
                        if( value < PRN_VALUE_MIN || value > PRN_VALUE_MAX ){
                            StringBuilder sb = new StringBuilder();
                            if( value < PRN_VALUE_MIN ){
                                sb.append(java.util.ResourceBundle.getBundle("com/hulist/bundle/Importers").getString("ODCZYTANA WARTOŚĆ < ")).append(PRN_VALUE_MIN);
                            }
                            if( value > PRN_VALUE_MAX ){
                                sb.append(java.util.ResourceBundle.getBundle("com/hulist/bundle/Importers").getString("ODCZYTANA WARTOŚĆ > ")).append(PRN_VALUE_MAX);
                            }
                            sb.append(java.util.ResourceBundle.getBundle("com/hulist/bundle/Importers").getString(", W PLIKU ")).append(f.getCanonicalPath()).append(java.util.ResourceBundle.getBundle("com/hulist/bundle/Importers").getString(" DLA ROKU ")).append(year).append(java.util.ResourceBundle.getBundle("com/hulist/bundle/Importers").getString(", DLA MIESIĄCA ")).append(month);
                            throw new IllegalArgumentException(sb.toString());
                        }
                        lineData.addMonthlyData(month, value);
                        counter++;
                    }
                    container.addYearlyData(year, lineData);
                }
            } catch( AssertionError | IOException | NumberFormatException e ) {
                String msg = String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/Importers").getString("BŁĘDNY FORMAT PLIKU %S."), f.getName());
                log.log(Level.WARNING, msg);
                log.log(Level.FINEST, msg);
                throw new IOException(msg);
            } catch( IllegalArgumentException e ) {
                log.log(Level.WARNING, e.getMessage());
                log.log(Level.FINEST, e.getMessage());
                throw new IOException();
            }

            lineCounter++;
        }

        br.close();

        ArrayList<PrnDataContainer> containerArr = new ArrayList<>(1);
        containerArr.add(container);
        return containerArr;
    }

}
