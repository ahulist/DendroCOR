/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.Y_M_D_V;

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
public class Y_M_D_VImporter extends BaseImporter implements DataImporter<Y_M_D_VDataContainer> {

    private ArrayList<Y_M_D_VDataContainer> data;

    public Y_M_D_VImporter(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<Y_M_D_VDataContainer> getData(File f) throws FileNotFoundException, IOException {
        this.data = new ArrayList<>(1);
        Y_M_D_VSeriesDataContainer cont = new Y_M_D_VSeriesDataContainer(data);

        InputStream fis;
        BufferedReader br;
        String line;

        Y_M_D_VDataContainer d = new Y_M_D_VDataContainer(f);

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("") && !line.trim().startsWith("#")) {
                String[] elems = line.trim().split("[\\s\\t]+");

                int year = Integer.parseInt(elems[0]);
                if (year < startYear || year > endYear) {
                    lineCounter++;
                    continue;
                }
                int month = Integer.parseInt(elems[1]);
                int day = Integer.parseInt(elems[2]);
                LocalDate date = new LocalDate(year, month, day);

                String val = elems[3];

                boolean isExcluded = false;
                for (String excluded : rp.getExcludedValues()) {
                    if (val.equals(excluded)) {
                        isExcluded = true;
                        break;
                    }
                }

                if (isExcluded) {
                    lineCounter++;
                    continue;
                }
                double value = Double.parseDouble(val);

                Y_M_D_VLineContainer lineCont = new Y_M_D_VLineContainer(date, value);
                d.add(lineCont);
            }
            lineCounter++;
        }

        cont.add(d);

        return this.data;
    }

}
