/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.climate.ao;

import com.hulist.gui2.GUIMain;
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
import java.util.ResourceBundle;


/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class AoImporter extends BaseImporter implements DataImporter<AoDataContainer> {

    public AoImporter(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<AoDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        AoDataContainer container = new AoDataContainer(f);

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        while( (line = br.readLine()) != null ) {
            if( !line.startsWith("#") ){
                String[] data = line.trim().split("[\\s\\t]+");
                try {
                    if( allYears
                            || (!allYears
                            && Integer.parseInt(data[0]) >= startYear
                            && Integer.parseInt(data[0]) <= endYear) ){

//                        assert data.length == 3;
                        if (!(data.length == 3)) {
                            throw new IOException();
                        }
                        int year = Integer.parseInt(data[0]);
                        Months month = Months.getMonth(Integer.parseInt(data[1]));
                        double val = Double.parseDouble(data[2]);
                        /*int counter = 1;
                         for( Months month : Months.values() ) {
                         double value = Double.parseDouble(data[counter]);
                         if( value < ICRU_VALUE_MIN || value > ICRU_VALUE_MAX ){
                         StringBuilder sb = new StringBuilder();
                         if( value < ICRU_VALUE_MIN ){
                         sb.append(Misc.getInternationalized("ODCZYTANA WARTOŚĆ < ")).append(ICRU_VALUE_MIN);
                         }
                         if( value > ICRU_VALUE_MAX ){
                         sb.append(Misc.getInternationalized("ODCZYTANA WARTOŚĆ > ")).append(ICRU_VALUE_MAX);
                         }
                         sb.append(Misc.getInternationalized(", W PLIKU ")).append(f.getCanonicalPath()).append(Misc.getInternationalized(" DLA ROKU ")).append(year).append(Misc.getInternationalized(", DLA MIESIĄCA ")).append(month);
                         throw new IllegalArgumentException(sb.toString());
                         }
                         lineData.addMonthlyData(month, value);
                         counter++;
                         }*/
                        container.addLine(year, month, val);
                    }
                } catch( IOException | AssertionError | NumberFormatException e ) {
                    String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), lineCounter);
                    log.warn(msg);
                    log.trace(Misc.stackTraceToString(e));
                    throw new IOException(msg);
                } catch( IllegalArgumentException e ) {
                    String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S."), f.getName());
                    log.warn(msg);
                    log.trace(Misc.stackTraceToString(e));
                    throw new RuntimeException();
                }

            }
            lineCounter++;
        }

        br.close();

        ArrayList<AoDataContainer> containerArr = new ArrayList<>(1);
        containerArr.add(container);
        return containerArr;
    }

}
