/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.N_YMD_VS;

import com.hulist.logic.BaseImporter;
import com.hulist.logic.DataImporter;
import com.hulist.logic.RunParams;
import com.hulist.util.Misc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.joda.time.LocalDate;

/**
 *
 * @author Aleksander
 */
public class N_YMD_VSImporter extends BaseImporter implements DataImporter<N_YMD_VSDataContainer> {

    private ArrayList<N_YMD_VSDataContainer> data;

    public N_YMD_VSImporter(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<N_YMD_VSDataContainer> getData(File f) throws FileNotFoundException, IOException {
        this.data = new ArrayList<>(1);
        N_YMD_VSSeriesDataContainer cont = new N_YMD_VSSeriesDataContainer(data);

        InputStream fis;
        BufferedReader br;
        String line;
        String currStation = "";

        N_YMD_VSDataContainer d = null;

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("") && !line.startsWith("#")) {

                try {

                    String[] elems = line.trim().split("[\\s\\t]+");
                    String station = elems[0];

                    if (!station.equals(currStation)) { // new station!
                        if (d != null) {  // not first station in a file
                            cont.add(d);
                        }
                        d = new N_YMD_VSDataContainer(f, station);

                        currStation = station;
                    }

                    int year = Integer.parseInt(elems[1].substring(0, 4));
                    if (year < startYear || year > endYear) {
                        lineCounter++;
                        continue;
                    }
                    int month = Integer.parseInt(elems[1].substring(4, 6));
                    int day = Integer.parseInt(elems[1].substring(6, 8));
                    LocalDate date = new LocalDate(year, month, day);
                    String val = null;
                    switch (rp.getDailyColumnType()) {
                        case PREC:
                            val = elems[2];
                            break;
                        case TEMP:
                            val = elems[3];
                            break;
                    }
                    boolean isExcluded = false;
                    for (String excluded : rp.getExcludedValues()) {
                        if (val.equals(excluded)) {
                            // TODO: jeśli będzie za mało kolumn w pliku
                            // to tutaj wyskoczy błąd!
                            isExcluded = true;
                            break;
                        }
                    }
                    if (isExcluded) {
                        lineCounter++;
                        continue;
                    }
                    double value = Double.parseDouble(val);

                    N_YMD_VSLineContainer lineCont = new N_YMD_VSLineContainer(station, date, value);
                    d.add(lineCont);

                } catch (Exception e) {
                    String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), lineCounter);
                    log.warn(msg);
                    log.trace(msg);
                    throw new IOException(msg);
                }
            }
            lineCounter++;
        }

        if (d != null) {
            cont.add(d);
        }

        return this.data;
    }

}
