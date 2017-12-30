package com.hulist.logic;

import com.hulist.logic.correlation.Correlator;
import com.hulist.logic.correlation.PearsonCorrelation;
import com.hulist.util.Debug;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.MonthDay;

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
                double done = 0;
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
                    
                    done = (++counter/size);
                    if (Debug.IS_DUBUGGGING) {
                        System.out.println("Correlation: "+done*100+" %");
                    }
                    this.wp.getProgress().setCurrentJobProgress(done);
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
                // log.log(Level.SEVERE, String.format(Misc.getInternationalized("ZBYT DUŻE PRZESUNIĘCIE LAT: %S"), month.toString()));
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
        boolean isSignificance = wp.getSettings().isStatisticalSignificance();
        boolean isBootstrap = wp.getSettings().isBootstrapSampling();
        int bootstrapRepetitions = wp.getSettings().getBootstrapSamples();
        double alpha = wp.getSettings().getSignificanceLevelAlpha();
        if (wp.getSettings().isTwoTailedTest()) {
            alpha /= 2;
        }

        c.setIsBootstrapped(isBootstrap);
        c.setIsSignificanceLevels(isSignificance);
        c.setBootstrapValues(alpha, bootstrapRepetitions);

        if (Debug.IS_DUBUGGGING) {
            if (p.getFirst().equals(new MonthDay(1, 11)) && p.getSecond().equals(new MonthDay(1, 11))) {
                int a = 5;
            }
            if (p.getFirst().equals(new MonthDay(1, 19)) && p.getSecond().equals(new MonthDay(2, 29))) {
                int a = 5;
            }
        }
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
        //String logMsg = String.format("%s %-70.70s\n%- 10.10f", Misc.getInternationalized("KORELACJA"), " " + primaryName + " / " + climateName + date, correlation.getCorrelation());

        /*
         *   SIGNIFICANCE LEVEL
         */
        if (isSignificance) {
            switch (wp.getRunType()) {
                case MONTHLY:
                    results.climateMap.get(month).settTestValue(correlation.gettTestValue());
                    // TODO
                    //logMsg += "\n" + Misc.getInternationalized("poziom istotności T-Studenta") + ": " + correlation.gettTestValue();

                    results.climateMap.get(month).settTestCritVal(correlation.gettTestCritVal());
                    // TODO
                    //logMsg += ", " + Misc.getInternationalized("poziom krytyczny") + ": " + correlation.gettTestCritVal();
                    break;
                case DAILY:
                    results.dailyMap.get(p).settTestValue(correlation.gettTestValue());
                    // TODO
                    //logMsg += "\n" + Misc.getInternationalized("poziom istotności T-Studenta") + ": " + correlation.gettTestValue();

                    results.dailyMap.get(p).settTestCritVal(correlation.gettTestCritVal());
                    // TODO
                    //logMsg += ", " + Misc.getInternationalized("poziom krytyczny") + ": " + correlation.gettTestCritVal();
                    break;
            }

            results.setIsTTest(true);
        }

        // TODO
        //log.log(Level.INFO, logMsg);

        /*
         *   RUNNING CORRELATION
         */
        if (wp.getSettings().isRunningCorrelation() && wp.getRunType().equals(RunType.MONTHLY)) {
            int windowSize = wp.getSettings().getRunningCorrWindowSize();

            if (windowSize >= readyChrono.length) {
                log.warn(String.format(Misc.getInternationalized("zbyt duże okno korelacji kroczącej")));
            } else {
                results.setIsRunningCorr(true);
                results.setWindowSize(windowSize);
                double[] chronoSmall = new double[windowSize];
                double[] climSmall = new double[windowSize];
                for (int i = 0; i <= readyChrono.length - windowSize; i++) {
                    // TODO równolegle!
                    System.arraycopy(readyChrono, i, chronoSmall, 0, windowSize);
                    System.arraycopy(readySecondary, i, climSmall, 0, windowSize);
                    MetaCorrelation meta = c.correlate(chronoSmall, climSmall);
                    if (!results.runningCorrMap.containsKey(month) || results.runningCorrMap.get(month) == null) {
                        results.runningCorrMap.put(month, new TreeMap<>());
                    }
                    results.runningCorrMap.get(month).put(yearMin + i, meta);
                }
                log.info(String.format(Misc.getInternationalized("korel kroczaca obliczona dla"), yearMin, yearMax, windowSize, month));
            }
        }

        return results;
    }

    //private liczenie korelacji zwracajacej istotnosc
}
