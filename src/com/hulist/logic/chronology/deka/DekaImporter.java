/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.deka;

import com.hulist.gui2.GUIMain;
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
import java.util.ResourceBundle;


/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class DekaImporter extends BaseImporter implements DataImporter<DekaSerie> {

    public DekaImporter(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<DekaSerie> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        DekaSeriesDataContainer d = new DekaSeriesDataContainer();

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int lineCounter = 1;
        DekaSerie serie = null;
        while ((line = br.readLine()) != null) {
            /*
             if chrono code consists of 8 characters, it's 'glued' together with year
             add space just to be sure it does not happen
             */
            line = new StringBuilder(line).insert(8, ' ').toString();

            String[] data = line.trim().split("[\\s\\t]+");
//            assert data.length > 2;
            try {
                if (!(data.length > 2)) {
                    throw new IOException();
                }
                String chronoCode = data[0];
                int yearStart = Integer.parseInt(data[1]);
//                boolean newChrono = false;
                if (serie == null) {
                    serie = new DekaSerie(f, chronoCode);
                }
                if (!chronoCode.equals(serie.getChronoCode())) {
//                    newChrono = true;
                    d.add(serie);
                    serie = new DekaSerie(f, chronoCode);
                }
                for (int i = 2; i < data.length; i++) {
                    double val = Double.parseDouble(data[i]);
                    if (val == 999 || val == -9999) {   // TODO: instead it could read the last value in file -> it is the serie termination value
                        /*
                         what if there is a value == -999 inside a serie (like in 09TRWres.rwl)? it's silently accepted
                         */
                        break;
                    }

                    if (allYears || (!allYears && yearStart + i - 2 >= startYear && yearStart + i - 2 <= endYear)) {
                        serie.addYear(yearStart + i - 2, val);
                    }
                }
            } catch (IOException | NumberFormatException | AssertionError e) {
                String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), lineCounter);
                log.warn(msg);
                log.trace(Misc.stackTraceToString(e));
                throw new IOException(msg);
            }

            lineCounter++;
        }
        d.add(serie);

        br.close();

        return d.getSeries();
    }

}
