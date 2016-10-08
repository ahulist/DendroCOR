/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.tabs_multicol;

import com.hulist.gui.MainWindow;
import com.hulist.logic.BaseImporter;
import com.hulist.logic.DataImporter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TabsMulticolImporter extends BaseImporter implements DataImporter<TabsMulticolDataContainer> {

    public TabsMulticolImporter(boolean isAllYears, int startYear, int endYear) {
        super(isAllYears, startYear, endYear);
    }

    @Override
    public ArrayList<TabsMulticolDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        ArrayList<TabsMulticolDataContainer> res = null;
        int dataColumnsCount = -1;

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int row = 1;
        while ((line = br.readLine()) != null) {
            String[] data = line.trim().split("[\\s\\t]+");
            //String[] data = line.split("[\\s\\t]",-1);
            try {
                if (row==1) {
                    dataColumnsCount = data.length-1;
//                    assert dataColumnsCount>0;
                    if (!(dataColumnsCount>0)) {
                        throw new IOException();
                    }
                    res = new ArrayList<>(dataColumnsCount);
                    for (int i = 0; i < dataColumnsCount; i++) {
                        res.add(new TabsMulticolDataContainer(f, i+1));
                    }
                }
                
                if ((allYears
                        || (!allYears
                        && Integer.parseInt(data[0]) >= startYear
                        && Integer.parseInt(data[0]) <= endYear))) {

                    int year = Integer.parseInt(data[0]);
                    for (int i = 0; i < dataColumnsCount; i++) {
                        res.get(i).addValue(year, Double.parseDouble(data[i+1]));
                    }
                }
            } catch (IOException | NumberFormatException | AssertionError e) {
                String msg = String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), row);
                log.warn(msg);
                log.trace(msg);
                throw new IOException(msg);
            }

            row++;
        }

        br.close();

        return res;
    }

}
