package com.hulist.logic;

import com.hulist.gui.MainWindow;
import com.hulist.logic.correlation.Correlator;
import com.hulist.logic.correlation.PearsonCorrelation;
import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class CorrelationProcessing {

    private final DataToCorrelate data;  // column being correlated to all other columns
    private final Logger log;
    private final RunParams wp;
    private final Correlator c;

    CorrelationProcessing(RunParams wp, DataToCorrelate dataToCorrelate) {
        this.data = dataToCorrelate;
        this.wp = wp;
        this.c = new Correlator(new PearsonCorrelation(), wp);

        this.log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);
    }

    public Results go(int yearMin, int yearMax) {
        Results results = null;
        SignificanceLevel sl = new SignificanceLevel();

        double[] readyChrono, readyDaily, readyClim;

        switch (wp.getRunType()) {
            case MONTHLY:
                readyChrono = data.primary.getData();
                for (MonthsPair month : wp.getMonthsColumns()) {
                    readyClim = data.climateColumns.get(month).getData();
                    if (results == null) {
                        results = doRest(readyChrono, readyClim, month.yearsShift, yearMin, yearMax, month, null);
                    } else {
                        Results tmp = doRest(readyChrono, readyClim, month.yearsShift, yearMin, yearMax, month, null);
                        results.climateMap.putAll(tmp.climateMap);
                        results.runningCorrMap.putAll(tmp.runningCorrMap);
                    }
                }
                break;
            case DAILY:
                for (Pair<MonthDay, MonthDay> p : data.daily.keySet()) {
                    if (p.getFirst().getMonthOfYear()==1 && p.getSecond().getMonthOfYear()==12) {
                        int a = 3;
                    }
                    readyChrono = data.daily.get(p).getFirst().getData();
                    readyDaily = data.daily.get(p).getSecond().getData();
                    if (readyChrono.length > 1 && readyDaily.length > 1) {
                        if (results == null) {
                            results = doRest(readyChrono, readyDaily, 0, yearMin, yearMax, null, p);
                        } else {
                            results.dailyMap.putAll(doRest(readyChrono, readyDaily, 0, yearMin, yearMax, null, p).dailyMap);
                        }
                    }
                }
                break;
        }

        // *******************************
        /*for (MonthsPair month : wp.getMonthsColumns()) {
         double[] readyChrono, readyClim;

         String date = " (" + yearMin + "-" + yearMax + " " + month + ")";
         String primaryName = data.primary.getName();
         if (primaryName.length() > 21) {
         primaryName = primaryName.substring(0, 19) + "...";
         }
         String climateName = data.climateColumns.get(month).getName();
         if (climateName.length() > 21) {
         climateName = climateName.substring(0, 19) + "...";
         }

         /*
         dendro data:    oldest year(s) being removed
         climatic data:  youngest year(s) being removed
         */
        /*if (month.yearsShift > 0) {
         if (data.primary.getData().length - month.yearsShift < 2) {
         log.log(Level.SEVERE, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ZBYT DUŻE PRZESUNIĘCIE LAT: %S"), month.toString()));
         return null;
         }

         double[] primaryCol = data.primary.getData();
         readyChrono = new double[primaryCol.length - month.yearsShift];
         System.arraycopy(primaryCol, month.yearsShift, readyChrono, 0, readyChrono.length);

         double[] climateCol = data.climateColumns.get(month).getData();
         readyClim = new double[climateCol.length - month.yearsShift];
         System.arraycopy(climateCol, 0, readyClim, 0, readyClim.length);

         } else {
         readyChrono = data.primary.getData();
         readyClim = data.climateColumns.get(month).getData();
         }

         /*
         *   CORRELATION / BOOTSTRAP
         */
        /*boolean isSignificance = wp.getPreferencesFrame().getCheckBoxSignificance().isSelected();
         boolean isBootstrap = wp.getPreferencesFrame().getCheckBoxBootstrap().isSelected();
         int bootstrapRepetitions = Integer.parseInt(wp.getPreferencesFrame().getBootstrapTextField().getText());
         double alpha = Double.parseDouble(wp.getPreferencesFrame().getSignificanceTextField().getText());
         if (wp.getPreferencesFrame().getCheckBoxTwoSidedTest().isSelected()) {
         alpha /= 2;
         }

         c.setIsBootstrapped(isBootstrap);
         c.setIsSignificanceLevels(isSignificance);
         c.setBootstrapValues(alpha, bootstrapRepetitions);

         MetaCorrelation correlation = c.correlate(readyChrono, readyClim);
         results.climateMap.put(month, correlation);

         String logMsg = String.format("%s %-70.70s\n%- 10.10f", ResourceBundle.getBundle(MainWindow.BUNDLE).getString("KORELACJA"), " " + primaryName + " / " + climateName + date, correlation.getCorrelation());

         /*
         *   SIGNIFICANCE LEVEL
         */
        /*if (isSignificance) {
         results.climateMap.get(month).settTestValue(correlation.gettTestValue());
         logMsg += "\n" + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom istotności T-Studenta") + ": " + correlation.gettTestValue();

         results.climateMap.get(month).settTestCritVal(correlation.gettTestCritVal());
         logMsg += ", " + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom krytyczny") + ": " + correlation.gettTestCritVal();

         results.setIsTTest(true);
         }

         log.log(Level.INFO, logMsg);

         /*
         *   RUNNING CORRELATION
         */
        /*if (wp.getPreferencesFrame().getCheckBoxRunCorr().isSelected()) {
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
         double resRunn = c.correlate(chronoSmall, climSmall).getCorrelation();
         if (!results.runningCorrMap.containsKey(month) || results.runningCorrMap.get(month) == null) {
         results.runningCorrMap.put(month, new TreeMap<>());
         }
         results.runningCorrMap.get(month).put(yearMin + i, resRunn);
         }
         log.log(Level.INFO, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("korel kroczaca obliczona dla"), yearMin, yearMax, windowSize, month));
         }
         }
         }
         */
        return results;
    }

    private Results doRest(double[] readyChrono, double[] readySecondary, int shift, int yearMin, int yearMax, MonthsPair month, Pair<MonthDay, MonthDay> p) {
        Results results = new Results(wp);
        SignificanceLevel sl = new SignificanceLevel();

        if (shift > 0) {
            if (readyChrono.length - shift < 2) {
                // TODO
                // log.log(Level.SEVERE, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ZBYT DUŻE PRZESUNIĘCIE LAT: %S"), month.toString()));
                return null;
            }

            double[] newReadyChrono = new double[readyChrono.length - shift];
            System.arraycopy(readyChrono, shift, newReadyChrono, 0, newReadyChrono.length);

            double[] newReadyClim = new double[readySecondary.length - shift];
            System.arraycopy(readySecondary, 0, newReadyClim, 0, newReadyClim.length);

            readyChrono = newReadyChrono;
            readySecondary = newReadyClim;

            /* nie wiem po co jest ten kod (błędy korelacji z macierzy?), raczej niepotrzebny
             double[][] dataToCorrelate = {readyChrono, readyClim};
             c = new PearsonsCorrelation(dataToCorrelate);
             RealMatrix corr = c.getCorrelationMatrix();*/
        }

        /*
         *   CORRELATION / BOOTSTRAP
         */
        boolean isSignificance = wp.getPreferencesFrame().getCheckBoxSignificance().isSelected();
        boolean isBootstrap = wp.getPreferencesFrame().getCheckBoxBootstrap().isSelected();
        int bootstrapRepetitions = Integer.parseInt(wp.getPreferencesFrame().getBootstrapTextField().getText());
        double alpha = Double.parseDouble(wp.getPreferencesFrame().getSignificanceTextField().getText());
        if (wp.getPreferencesFrame().getCheckBoxTwoSidedTest().isSelected()) {
            alpha /= 2;
        }

        c.setIsBootstrapped(isBootstrap);
        c.setIsSignificanceLevels(isSignificance);
        c.setBootstrapValues(alpha, bootstrapRepetitions);

        MetaCorrelation correlation = c.correlate(readyChrono, readySecondary);
        switch (wp.getRunType()) {
            case MONTHLY:
                results.climateMap.put(month, correlation);
                break;
            case DAILY:
                results.dailyMap.put(p, correlation);
                break;
        }

        // TODO
        //String logMsg = String.format("%s %-70.70s\n%- 10.10f", ResourceBundle.getBundle(MainWindow.BUNDLE).getString("KORELACJA"), " " + primaryName + " / " + climateName + date, correlation.getCorrelation());

        /*
         *   SIGNIFICANCE LEVEL
         */
        if (isSignificance) {
            switch (wp.getRunType()) {
                case MONTHLY:
                    results.climateMap.get(month).settTestValue(correlation.gettTestValue());
                    // TODO
                    //logMsg += "\n" + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom istotności T-Studenta") + ": " + correlation.gettTestValue();

                    results.climateMap.get(month).settTestCritVal(correlation.gettTestCritVal());
                    // TODO
                    //logMsg += ", " + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom krytyczny") + ": " + correlation.gettTestCritVal();
                    break;
                case DAILY:
                    results.dailyMap.get(p).settTestValue(correlation.gettTestValue());
                    // TODO
                    //logMsg += "\n" + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom istotności T-Studenta") + ": " + correlation.gettTestValue();

                    results.dailyMap.get(p).settTestCritVal(correlation.gettTestCritVal());
                    // TODO
                    //logMsg += ", " + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("poziom krytyczny") + ": " + correlation.gettTestCritVal();
                    break;
            }

            results.setIsTTest(true);
        }

        // TODO
        //log.log(Level.INFO, logMsg);

        /*
         *   RUNNING CORRELATION
         */
        if (wp.getPreferencesFrame().getCheckBoxRunCorr().isSelected() && wp.getRunType().equals(RunType.MONTHLY)) {
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
                    System.arraycopy(readySecondary, i, climSmall, 0, windowSize);
                    double resRunn = c.correlate(chronoSmall, climSmall).getCorrelation();
                    if (!results.runningCorrMap.containsKey(month) || results.runningCorrMap.get(month) == null) {
                        results.runningCorrMap.put(month, new TreeMap<>());
                    }
                    results.runningCorrMap.get(month).put(yearMin + i, resRunn);
                }
                log.log(Level.INFO, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("korel kroczaca obliczona dla"), yearMin, yearMax, windowSize, month));
            }
        }

        return results;
    }

    //private liczenie korelacji zwracajacej istotnosc
}
