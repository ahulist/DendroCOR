package com.hulist.logic;

import com.hulist.gui.MainWindow;
import com.hulist.logic.correlation.Correlator;
import com.hulist.logic.correlation.PearsonCorrelation;
import com.hulist.util.MonthsPair;
import java.util.ResourceBundle;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class CorrelationProcessing {

    private final DataToCorrelate data;  // column being correlated to all other columns
    private final Logger log = LoggerFactory.getLogger(CorrelationProcessing.class);
    private final RunParams wp;
    private final Correlator c;

    CorrelationProcessing(RunParams wp, DataToCorrelate dataToCorrelate) {
        this.data = dataToCorrelate;
        this.wp = wp;
        this.c = new Correlator(new PearsonCorrelation(), wp);
    }

    public Results go(int yearMin, int yearMax) {
        Results results = new Results(wp);
        SignificanceLevel sl = new SignificanceLevel();

        for (MonthsPair month : wp.getMonthsColumns()) {
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
                    log.error(String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ZBYT DUŻE PRZESUNIĘCIE LAT: %S"), month.toString()));
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

            /*
             *   CORRELATION / BOOTSTRAP
             */
            boolean isSignificance = wp.getPrefs().isIsStatisticalSignificance();
            boolean isBootstrap = wp.getPrefs().isIsBootstrapSampling();
            int bootstrapRepetitions = wp.getPrefs().getBootstrapSamples();
            double alpha = wp.getPrefs().getSignificanceLevelAlpha();
            if (wp.getPrefs().isIsTwoTailedTest()) {
                alpha /= 2;
            }
            
            c.setIsBootstrapped(isBootstrap);
            c.setIsSignificanceLevels(isSignificance);
            c.setBootstrapValues(alpha, bootstrapRepetitions);

            MetaCorrelation correlation = c.correlate(readyChrono, readyClim);
            results.map.put(month, correlation);

            String logMsg = String.format("%s %-70.70s\n%- 10.10f", ResourceBundle.getBundle(MainWindow.BUNDLE).getString("KORELACJA"), " " + primaryName + " / " + climateName + date, correlation.getCorrelation());            

            /*
             *   SIGNIFICANCE LEVEL
             */
            if (isSignificance) {
                results.map.get(month).settTestValue(correlation.gettTestValue());
                logMsg += "\n" + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom istotności T-Studenta") + ": " + correlation.gettTestValue();

                results.map.get(month).settTestCritVal(correlation.gettTestCritVal());
                logMsg += ", " + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom krytyczny") + ": " + correlation.gettTestCritVal();

                results.setIsTTest(true);
            }

            log.info(logMsg);

            /*
             *   RUNNING CORRELATION
             */
            if (wp.getPrefs().isIsRunningCorrelation()) {
                int windowSize = wp.getPrefs().getRunningCorrWindowSize();

                if (windowSize >= readyChrono.length) {
                    log.warn(String.format(ResourceBundle.getBundle(MainWindow.BUNDLE).getString("zbyt duże okno korelacji kroczącej")));
                } else {
                    results.setIsRunningCorr(true);
                    results.setWindowSize(windowSize);
                    double[] chronoSmall = new double[windowSize];
                    double[] climSmall = new double[windowSize];
                    for (int i = 0; i <= readyChrono.length - windowSize; i++) {
                        // TODO równolegle!
                        System.arraycopy(readyChrono, i, chronoSmall, 0, windowSize);
                        System.arraycopy(readyClim, i, climSmall, 0, windowSize);
                        double resRunn = c.correlate(chronoSmall, climSmall).getCorrelation();
                        if (!results.runningCorrMap.containsKey(month) || results.runningCorrMap.get(month) == null) {
                            results.runningCorrMap.put(month, new TreeMap<>());
                        }
                        results.runningCorrMap.get(month).put(yearMin + i, resRunn);
                    }
                    log.info(String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("korel kroczaca obliczona dla"), yearMin, yearMax, windowSize, month));
                }
            }
        }

        return results;
    }

    //private liczenie korelacji zwracajacej istotnosc
}
