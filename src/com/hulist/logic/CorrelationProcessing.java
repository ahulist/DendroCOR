package com.hulist.logic;

import com.hulist.gui.MainWindow;
import com.hulist.logic.correlation.Correlator;
import com.hulist.logic.correlation.PearsonCorrelation;
import com.hulist.util.MonthsPair;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class CorrelationProcessing {

    private final DataToCorrelate data;  // column being correlated to all other columns
    private final Logger log;
    private final RunParams wp;
    private final Correlator c;
    private final static HashMap<Double, Double> T_TEST_CRIT_LOOKUP_CACHE = new HashMap<>();

    CorrelationProcessing(RunParams wp, DataToCorrelate dataToCorrelate) {
        this.data = dataToCorrelate;
        this.wp = wp;
        this.c = new Correlator(new PearsonCorrelation(), wp);

        this.log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);
    }

    public Results go(int yearMin, int yearMax) {
        Results results = new Results(wp);

        for (MonthsPair month : wp.getMonthsColumns()) {
            double correlation;
            double[] readyChrono, readyClim;

            String date = " (" + yearMin + "-" + yearMax + " " + month + ")";
            String primaryName = data.primary.getName();
            if (primaryName.length() > 21) {
                primaryName = primaryName.substring(0, 19) + "...";
            }
            String climateName = data.columns.get(month).getName();
            if (climateName.length() > 21) {
                climateName = climateName.substring(0, 19) + "...";
            }

            /*
             dendro data:    oldest year(s) being removed
             climatic data:  youngest year(s) being removed
             */
            if (month.yearsShift > 0) {
                if (data.primary.getData().length - month.yearsShift < 2) {
                    log.log(Level.SEVERE, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ZBYT DUŻE PRZESUNIĘCIE LAT: %S"), month.toString()));
                    return null;
                }

                double[] primaryCol = data.primary.getData();
                readyChrono = new double[primaryCol.length - month.yearsShift];
                System.arraycopy(primaryCol, month.yearsShift, readyChrono, 0, readyChrono.length);

                double[] climateCol = data.columns.get(month).getData();
                readyClim = new double[climateCol.length - month.yearsShift];
                System.arraycopy(climateCol, 0, readyClim, 0, readyClim.length);

                /* nie wiem po co jest ten kod (błędy korelacji z macierzy?), raczej niepotrzebny
                 double[][] dataToCorrelate = {readyChrono, readyClim};
                 c = new PearsonsCorrelation(dataToCorrelate);
                 RealMatrix corr = c.getCorrelationMatrix();*/
            } else {
                readyChrono = data.primary.getData();
                readyClim = data.columns.get(month).getData();
            }
            correlation = c.correlate(readyChrono, readyClim);
            results.map.put(month, new MetaCorrelation(correlation));

            String logMsg = String.format("%s %-70.70s\n%- 10.10f", ResourceBundle.getBundle(MainWindow.BUNDLE).getString("KORELACJA"), " " + primaryName + " / " + climateName + date, correlation);

            /*
             *   SIGNIFICANCE LEVEL
             */
            if (wp.getPreferencesFrame().getCheckBoxSignificance().isSelected()) {
                double tTestVal = correlation / (FastMath.sqrt((1 - correlation * correlation) / (readyChrono.length - 2)));
                results.map.get(month).settTestValue(tTestVal);
                logMsg += "\n"+ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom istotności T-Studenta") + ": " + tTestVal;

                TDistribution tdist = new TDistribution(2 * readyChrono.length - 2);
                double alpha = Double.parseDouble(wp.getPreferencesFrame().getSignificanceTextField().getText());
                if (wp.getPreferencesFrame().getCheckBoxTwoSidedTest().isSelected()) {
                    alpha /= 2;
                }
                
                double tTestCritVal;
                if (T_TEST_CRIT_LOOKUP_CACHE.get(1-alpha)==null) {
                    tTestCritVal = tdist.inverseCumulativeProbability(1 - alpha);
                    T_TEST_CRIT_LOOKUP_CACHE.put(1-alpha, tTestCritVal);
                }else{
                    tTestCritVal = T_TEST_CRIT_LOOKUP_CACHE.get(1-alpha);
                }
                
                results.map.get(month).settTestCritVal(tTestCritVal);
                logMsg += ", "+ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom krytyczny") + ": " + tTestCritVal;
                
                results.setIsTTest(true);
            }

            log.log(Level.INFO, logMsg);

            /*
             *   RUNNING CORRELATION
             */
            if (wp.getPreferencesFrame().getCheckBoxRunCorr().isSelected()) {
                int windowSize = wp.getPreferencesFrame().getSliderCorrVal();

                if (windowSize >= readyChrono.length) {
                    log.log(Level.WARNING, String.format(ResourceBundle.getBundle(MainWindow.BUNDLE).getString("zbyt duże okno korelacji kroczącej")));
                } else {
                    results.setIsRunningCorr(true);
                    results.setWindowSize(windowSize);
                    double[] chronoSmall = new double[windowSize];
                    double[] climSmall = new double[windowSize];
                    for (int i = 0; i <= readyChrono.length - windowSize; i++) {
                        // TODO równolegle!
                        System.arraycopy(readyChrono, i, chronoSmall, 0, windowSize);
                        System.arraycopy(readyClim, i, climSmall, 0, windowSize);
                        double resRunn = c.correlate(chronoSmall, climSmall);
                        if (!results.runningCorrMap.containsKey(month) || results.runningCorrMap.get(month) == null) {
                            results.runningCorrMap.put(month, new TreeMap<>());
                        }
                        results.runningCorrMap.get(month).put(yearMin + i, resRunn);
                    }
                    log.log(Level.INFO, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("korel kroczaca obliczona dla"), yearMin, yearMax, windowSize, month));
                }
            }
        }

        return results;
    }

    //private liczenie korelacji zwracajacej istotnosc
}
