package com.hulist.logic;

import com.hulist.gui.MainWindow;
import com.hulist.logic.correlation.Correlator;
import com.hulist.logic.correlation.PearsonCorrelation;
import com.hulist.util.Concurrent;
import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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

    public Results go(int yearMin, int yearMax) throws InterruptedException, ExecutionException {
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
//                List<Pair<MonthDay, MonthDay>> pairs = new ArrayList<>(data.daily.keySet().size());

                int size = data.daily.keySet().size();
                float counter = 0;
                for (Pair<MonthDay, MonthDay> p : data.daily.keySet()) {

//                    pairs.add(p);

                    readyChrono = data.daily.get(p).getFirst().getData();
                    readyDaily = data.daily.get(p).getSecond().getData();
                    if (readyChrono.length > 1 && readyDaily.length > 1) {
                        Results r = doRest(readyChrono, readyDaily, 0, yearMin, yearMax, null, p);
                        if (results == null) {
                            results = r;
                        } else {
                            results.dailyMap.putAll(r.dailyMap);
                        }
                    }
                    
                    System.out.println("Correlation: "+(++counter/size)*100+" %");
                }

                /*//////////////////
                int maxChunk = 5000;
                List<Future<List<Results>>> list = new ArrayList<>();
                List<Pair<Integer, Integer>> indices = new ArrayList<>();
                int modulo = pairs.size() / maxChunk;
                for (int i = 0; i < modulo; i++) {
                    indices.add(new Pair<>(i, i + maxChunk));
                }
                indices.add(new Pair<>(modulo * maxChunk, pairs.size() - 1));
                for (final Pair<Integer, Integer> index : indices) {
                    Future<List<Results>> f = Concurrent.es.submit(() -> {

                        List<Results> resCall = new ArrayList<>();
                        for (int i = index.getFirst(); i <= index.getSecond(); i++) {
                            double[] readyChronoCall = data.daily.get(pairs.get(i)).getFirst().getData();
                            double[] readyDailyCall = data.daily.get(pairs.get(i)).getSecond().getData();
                            Results r = new Results(wp);
                            if (readyChronoCall.length > 1 && readyDailyCall.length > 1) {
                                r = doRest(readyChronoCall, readyDailyCall, 0, yearMin, yearMax, null, pairs.get(i));
                            }
                            resCall.add(r);
                        }
                        return resCall;

                    });
                    list.add(f);
                }
                for (Future<List<Results>> r1 : list) {
                    for (Results r2 : r1.get()) {
                        if (results==null) {
                            results=r2;
                        }else{
                            results.dailyMap.putAll(r2.dailyMap);
                        }
                    }
                }

                ///////////////*/
                break;
        }

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
