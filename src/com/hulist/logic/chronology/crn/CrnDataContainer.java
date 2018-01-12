/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.crn;

import com.hulist.logic.FileDataContainer;
import com.hulist.logic.chronology.tabs.TabsImporter;
import com.hulist.util.Misc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class CrnDataContainer extends FileDataContainer {

    protected final ArrayList<CrnLineContainer> data = new ArrayList<>();

    public CrnDataContainer(File sourceFile) {
        super(sourceFile);
    }

    public CrnLineContainer getLine(int year) {
        return data.get(year);
    }

    public void addLine(CrnLineContainer line) {
        data.add(line);
        updateMinMax(line.getYear());
        updateMinMax(line.getYear() + line.getHowManyYearsInLine() - 1);
    }

    public ArrayList<CrnLineContainer> getData() {
        return this.data;
    }

    /**
     *
     * @param type
     * @return
     */
    public double[] getArray(CrnColumnTypes type) {
        throw new NotImplementedException();
//        return getArray(type, getYearMin(), getYearMax());
    }

    public double[] getArray(CrnColumnTypes type, int yearStart, int yearEnd) throws IOException {
        double[] arr = new double[yearEnd - yearStart + 1];
        int counter = 0;
        boolean end = false;
        for (CrnLineContainer line : data) {
            if (end) {
                break;
            }
            if (yearStart - line.getYear() < 10 && line.getType().equals(type)) {
                for (int i = 0; i < 10; i++) {
                    if (line.data[i][0] == CrnImporter.END_VAL || counter+i>=arr.length) {
                        end = true;
                        break;
                    }

                    if (line.data[i][1] >= TabsImporter.NUM_MIN) {
                        arr[counter + i] = line.data[i][0];
                    } else {
                        String msg = String.format(Misc.getInternationalized("BŁĘDNY FORMAT PLIKU %S W LINII %S."), getSourceFile().getName(), counter);
                        log.warn(msg);
                        log.trace(msg);
                        throw new IOException(msg);
                    }
                }
                counter += 10;
            }
        }
        return arr;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

}
