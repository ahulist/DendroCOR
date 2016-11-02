/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.type1;

import com.hulist.logic.BaseImporter;
import com.hulist.logic.DataImporter;
import com.hulist.logic.RunParams;
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
public class Type1Importer extends BaseImporter implements DataImporter<Type1DataContainer> {

    private ArrayList<Type1DataContainer> data;

    public Type1Importer(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<Type1DataContainer> getData(File f) throws FileNotFoundException, IOException {
        this.data = new ArrayList<>(1);
        Type1SeriesDataContainer cont = new Type1SeriesDataContainer(data);

        InputStream fis;
        BufferedReader br;
        String line;
        String currStation = "";

        Type1DataContainer d = null;

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("") && !line.startsWith("#")) {
                String[] elems = line.trim().split("[\\s\\t]+");
                String station = elems[0];

                if (!station.equals(currStation)) { // new station!
                    if (d != null) {  // not first station in a file
                        cont.add(d);
                    }
                    d = new Type1DataContainer(f, station);

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

                Type1LineContainer lineCont = new Type1LineContainer(station, date, value);
                d.add(lineCont);
            }
            lineCounter++;
        }

        if (d != null) {
            cont.add(d);
        }

        return this.data;
    }

}
