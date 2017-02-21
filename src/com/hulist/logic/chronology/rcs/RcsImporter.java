/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.rcs;

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


/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class RcsImporter extends BaseImporter implements DataImporter<RcsDataContainer> {

    public static final int NUM_MIN = 5;

    public RcsImporter(RunParams rp) {
        super(rp);
    }

    @Override
    public ArrayList<RcsDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        RcsDataContainer t = new RcsDataContainer(f);

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int counter = 2;
        if ((line = br.readLine()) != null) {           // for omitting first line
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {     // ommit comments and empty lines
                    counter++;
                    continue;
                }
                String[] data = line.trim().split("[\\s\\t]+");
                try {
//                    assert data.length == 8;
                    if (!(data.length == 13)) {
                        throw new IOException();
                    }
                    int num = Integer.parseInt(data[1].replace(".", ""));
                    if ((allYears
                            || (!allYears
                            && Integer.parseInt(data[0]) >= startYear
                            && Integer.parseInt(data[0]) <= endYear))
                            && num >= NUM_MIN) {

                        t.addLine(Integer.parseInt(data[0]),
                                Integer.parseInt(data[1].replace(".", "")),
                                Double.parseDouble(data[2]),
                                Double.parseDouble(data[3]),
                                Double.parseDouble(data[4]),
                                Double.parseDouble(data[5]),
                                Double.parseDouble(data[6]),
                                Double.parseDouble(data[7]));
                    }
                } catch (IOException | NumberFormatException | AssertionError e) {
                    String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), counter);
                    log.warn(msg);
                    log.trace(msg);
                    throw new IOException(msg);
                }

                counter++;
            }
        }

        br.close();

        ArrayList<RcsDataContainer> tArr = new ArrayList<>(1);
        tArr.add(t);
        return tArr;
    }

}
