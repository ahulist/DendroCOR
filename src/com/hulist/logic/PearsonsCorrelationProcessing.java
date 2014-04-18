/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.util.MonthsPair;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class PearsonsCorrelationProcessing {

    private final DataToCorrelate data;  // column being correlated to all other columns
    private final Logger log;
    private final WindowParams wp;

    PearsonsCorrelationProcessing(WindowParams wp, DataToCorrelate dataToCorrelate) {
        this.data = dataToCorrelate;
        this.wp = wp;

        this.log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);
    }

    public Results go(int yearMin, int yearMax) {
        PearsonsCorrelation c = new PearsonsCorrelation();
        Results results = new Results(wp);

        for( MonthsPair month : wp.getMonthsColumns() ) {
            double res;

            if( month.yearsShift > 0 ){
                if( data.primary.getData().length - month.yearsShift < 2 ){
                    log.log(Level.SEVERE, String.format("Zbyt du\u017ce przesuni\u0119cie lat: %s", month.toString()));
                    return null;
                }

                double[] primaryCol = data.primary.getData();
                double[] shortPrimaryCol = new double[primaryCol.length - month.yearsShift];
                System.arraycopy(primaryCol, month.yearsShift, shortPrimaryCol, 0, shortPrimaryCol.length);

                double[] climateCol = data.columns.get(month).getData();
                double[] shortClimateCol = new double[climateCol.length - month.yearsShift];
                System.arraycopy(climateCol, 0, shortClimateCol, 0, shortClimateCol.length);

                res = c.correlation(shortPrimaryCol, shortClimateCol);
            } else {
                double[] chrono = data.primary.getData();
                double[] clim = data.columns.get(month).getData();
                res = c.correlation(chrono, clim);
            }
            results.map.put(month, res);

            String date = " (" + yearMin + "-" + yearMax + " " + month + ")";
            String primaryName = data.primary.getName();
            if( primaryName.length() > 21 ){
                primaryName = primaryName.substring(0, 19)+"...";
            }
            String climateName = data.columns.get(month).getName();
            if( climateName.length() > 21 ){
                climateName = climateName.substring(0, 19)+"...";
            }
            log.log(Level.INFO, String.format("%-80.80s\n%- 10.10f", "korelacja " + primaryName + " / " + climateName + date, res));
        }

        return results;
    }
}
