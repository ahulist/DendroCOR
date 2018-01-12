/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.crn;

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
public class CrnImporter extends BaseImporter implements DataImporter<CrnDataContainer> {

    public static final int END_VAL = 9990;
    public static final int NUM_MIN = 5;

    public CrnImporter(RunParams rp) {
        super(rp);
    }
    
    @Override
    public ArrayList<CrnDataContainer> getData(File f) throws FileNotFoundException, IOException {
        InputStream fis;
        BufferedReader br;
        String line;
        CrnDataContainer t = new CrnDataContainer(f);
//        ArrayList<CrnDataContainer> tArr = new ArrayList<>();

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

        int localCounter = 0, counter = 0;
        while ((line = br.readLine()) != null) {
            counter++;
            localCounter++;
            CrnLineContainer crnLine = new CrnLineContainer();
            
            try {
                if (line.trim().isEmpty() || line.trim().startsWith("#") || localCounter < 4) {     // ommit comments, empty lines, 3 header lines
                    continue;
                }
                crnLine.ID = line.substring(0, 6).trim();
                crnLine.year = Integer.parseInt(line.substring(6, 10).trim());
                for (int i = 0; i < 10; i++) {
                    int value = Integer.parseInt(line.substring(10 + i * 7, 14 + i * 7).trim());
                    if (value != END_VAL) {
                        crnLine.data[i][0] = value/1000.0;
                        crnLine.data[i][1] = Integer.parseInt(line.substring(14 + i * 7, 18 + i * 7).trim());
                    }else{
                        crnLine.setHowManyYearsInLine(i);
                        localCounter = 0;
                        break;
                    }
                }
                crnLine.setType(line.substring(81, line.length()).trim());
            } catch (NumberFormatException | AssertionError e) {
                String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S W LINII %S."), f.getName(), counter);
                log.warn(msg);
                log.trace(msg + "\n" + e);
                throw new IOException(msg);
            }
            t.addLine(crnLine);
            
//            if (endSerie) {
//                tArr.add(t);
//                t = new CrnDataContainer(f);
//            }
        }
        
        br.close();
        
        ArrayList<CrnDataContainer> tArr = new ArrayList<>(1);
        tArr.add(t);
        return tArr;
    }

}
