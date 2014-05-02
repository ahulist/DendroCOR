/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.tabs;

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
import java.util.logging.Level;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TabsImporter extends BaseImporter implements DataImporter<TabsDataContainer> {

    public static final int NUM_MIN = 5;

    public TabsImporter(boolean isAllYears, int startYear, int endYear) {
        super(isAllYears, startYear, endYear);
    }

    @Override
    public ArrayList<TabsDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        TabsDataContainer t = new TabsDataContainer(f);

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int counter = 2;
        if( (line = br.readLine()) != null ){           // for omitting first line
            while( (line = br.readLine()) != null ) {
                String[] data = line.trim().split("[\\s\\t]+");
                try {
                    assert data.length == 8;
                    int num = Integer.parseInt(data[1].replace(".", ""));
                    if( (allYears
                            || (!allYears
                            && Integer.parseInt(data[0]) >= startYear
                            && Integer.parseInt(data[0]) <= endYear))
                            && num >= NUM_MIN ){

                        t.addLine(Integer.parseInt(data[0]),
                                Integer.parseInt(data[1].replace(".", "")),
                                Double.parseDouble(data[2]),
                                Double.parseDouble(data[3]),
                                Double.parseDouble(data[4]),
                                Double.parseDouble(data[5]),
                                Double.parseDouble(data[6]),
                                Double.parseDouble(data[7]));
                    }
                } catch( NumberFormatException | AssertionError e ) {
                    String msg = String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), counter);
                    log.log(Level.WARNING, msg);
                    log.log(Level.FINEST, msg);
                    throw new IOException(msg);
                } 

                counter++;
            }
        }

        br.close();

        ArrayList<TabsDataContainer> tArr = new ArrayList<>(1);
        tArr.add(t);
        return tArr;
    }

}
