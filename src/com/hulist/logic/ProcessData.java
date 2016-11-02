/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.logic.chronology.deka.DekaImporter;
import com.hulist.logic.chronology.deka.DekaSerie;
import com.hulist.logic.chronology.deka.DekaSeriesDataContainer;
import com.hulist.logic.chronology.tabs.TabsDataContainer;
import com.hulist.logic.chronology.tabs.TabsImporter;
import com.hulist.logic.chronology.tabs_multicol.TabsMulticolDataContainer;
import com.hulist.logic.chronology.tabs_multicol.TabsMulticolImporter;
import com.hulist.logic.climate._prn.PrnDataContainer;
import com.hulist.logic.climate._prn.PrnImporter;
import com.hulist.logic.climate.ao.AoDataContainer;
import com.hulist.logic.climate.ao.AoImporter;
import com.hulist.logic.climate.icru.IcruDataContainer;
import com.hulist.logic.climate.icru.IcruImporter;
import com.hulist.logic.daily.DailyResult;
import com.hulist.logic.daily.YearlyCombinations;
import com.hulist.logic.daily.type1.Type1DataContainer;
import com.hulist.logic.daily.type1.Type1Importer;
import com.hulist.logic.daily.type1.Type1SeriesDataContainer;
import com.hulist.util.Debug;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class ProcessData implements Runnable {
    
    public static BooleanProperty isAnyComputationRunning = new SimpleBooleanProperty(false);

    RunParams runParams = null;
    Thread.UncaughtExceptionHandler handler = null;

    private final Logger log = LoggerFactory.getLogger(ProcessData.class);
    private Thread computationThread;

    private final ArrayList<FileDataContainer> chronologyDataContainer = new ArrayList<>();
    private final ArrayList<FileDataContainer> climateDataContainer = new ArrayList<>();
    private final ArrayList<FileDataContainer> dailyDataContainer = new ArrayList<>();
    private final DataToCorrelate dataToCorrelate = new DataToCorrelate();
    private final ArrayList<Results> results = new ArrayList<>();

    public ProcessData(RunParams wp) {
        this.runParams = wp;
        this.computationThread = new Thread(this);
    }

    public ProcessData(RunParams wp, Thread.UncaughtExceptionHandler handler) {
        this(wp);
        this.handler = handler;
    }

    private void getTabsExt() throws IOException, NullPointerException {
        for (File file : runParams.getChronologyFiles()) {
            TabsMulticolImporter tabsImporter = new TabsMulticolImporter(runParams);
            chronologyDataContainer.addAll(tabsImporter.getData(file));
        }
    }

    private void getTabs() throws IOException, NullPointerException {
        for (File file : runParams.getChronologyFiles()) {
            TabsImporter tabsImporter = new TabsImporter(runParams);
            TabsDataContainer tabsCont = tabsImporter.getData(file).get(0);

            chronologyDataContainer.add(tabsCont);
        }
    }

    private void getDeka() throws IOException {
        for (File file : runParams.getChronologyFiles()) {
            DekaImporter dekaImporter = new DekaImporter(runParams);
            DekaSeriesDataContainer dekaCont = new DekaSeriesDataContainer(dekaImporter.getData(file));

            chronologyDataContainer.addAll(dekaCont.getSeries());
            /*dekaCont.getSeries().stream().forEach((serie) -> {
             chronologyDataContainer.add(serie);
             });*/
        }
    }

    private void getIcru() throws IOException {
        for (File file : runParams.getClimateFiles()) {
            IcruImporter icruImporter = new IcruImporter(runParams);
            IcruDataContainer icruCont = icruImporter.getData(file).get(0);

            climateDataContainer.add(icruCont);
        }
    }

    private void getType1() throws IOException {
        for (File file : runParams.getDailyFile()) {
            Type1Importer type1Importer = new Type1Importer(runParams);
            Type1SeriesDataContainer seriesCont = new Type1SeriesDataContainer(type1Importer.getData(file));

            dailyDataContainer.addAll(seriesCont.getData());
        }
    }

    private void getAo() throws IOException {
        for (File file : runParams.getClimateFiles()) {
            AoImporter aoImporter = new AoImporter(runParams);
            AoDataContainer aoCont = aoImporter.getData(file).get(0);

            climateDataContainer.add(aoCont);
        }
    }

    private void getPrn() throws IOException {
        for (File file : runParams.getClimateFiles()) {
            PrnImporter prnImporter = new PrnImporter(runParams);
            PrnDataContainer prnCont = prnImporter.getData(file).get(0);

            climateDataContainer.add(prnCont);
        }
    }

    private void process() throws IOException, InterruptedException, ExecutionException {
        log.info(Misc.getInternationalized("rozpoczeto obliczenia"));
        
        runParams.getProgress().resetFilesProgress();
        switch(runParams.getRunType()){
            case MONTHLY:
                runParams.getProgress().setHowManyFiles(chronologyDataContainer.size()*climateDataContainer.size());
                break;
            case DAILY:
                runParams.getProgress().setHowManyFiles(chronologyDataContainer.size()*dailyDataContainer.size());
                break;
        }
        
        int fileCounter = 0;
        
        for (FileDataContainer chronology : chronologyDataContainer) {
            String primaryColumnNameStart = chronology.getSourceFile().getName();
            String primaryColumnName = "";

            switch (runParams.getRunType()) {
                case MONTHLY:
                    runParams.getProgress().resetJobsProgress();
                    runParams.getProgress().setHowManyJobs(1);

                    for (FileDataContainer climate : climateDataContainer) {
                        fileCounter++;
                        
                        int commonYearStartLimit = Math.max(chronology.getYearMin(), climate.getYearMin());
                        int commonYearEndLimit = Math.min(chronology.getYearMax(), climate.getYearMax());

                        if (commonYearStartLimit > commonYearEndLimit) {
                            throw new DataException(Misc.getInternationalized("niepokrywające się lata"));
                        }

                        double[] primaryColumnData = null;

                        switch (runParams.getChronologyFileType()) {
                            case TABS:
                                primaryColumnData = ((TabsDataContainer) chronology).getArray(runParams.getChronologyColumn(), commonYearStartLimit, commonYearEndLimit);
                                primaryColumnName = primaryColumnNameStart + " (" + runParams.getChronologyColumn() + ")";
                                break;
                            case DEKADOWY:
                                primaryColumnData = ((DekaSerie) chronology).getArrayData(commonYearStartLimit, commonYearEndLimit);
                                primaryColumnName = primaryColumnNameStart + " (" + ((DekaSerie) chronology).getChronoCode() + ")";
                                break;
                            case TABS_MULTICOL:
                                primaryColumnData = ((TabsMulticolDataContainer) chronology).getArray(commonYearStartLimit, commonYearEndLimit);
                                primaryColumnName = primaryColumnNameStart + " (" + ((TabsMulticolDataContainer) chronology).getColumnNumber() + ". column)";
                        }
                        dataToCorrelate.primary = new Column(primaryColumnName, primaryColumnData);

                        String climateColumnsName = climate.getSourceFile().getName();
                        for (MonthsPair months : runParams.getMonthsColumns()) {
                            switch (runParams.getClimateFileType()) {
                                case ICRU:
                                    double[] icruData = ((IcruDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                                    Column icruC = new Column(climateColumnsName, icruData);
                                    dataToCorrelate.climateColumns.put(months, icruC);
                                    break;
                                /*case PRN:
                                 double[] prnData = ((PrnDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                                 Column prnC = new Column(climateColumnsName, prnData);
                                 dataToCorrelate.columns.put(months, prnC);
                                 break;*/
                                case AO:
                                    double[] aoData = ((AoDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                                    Column aoC = new Column(climateColumnsName, aoData);
                                    dataToCorrelate.climateColumns.put(months, aoC);
                                    break;
                            }
                        }

                        if (chronology.isEmpty() || climate.isEmpty()) {
                            if (chronology.isEmpty()) {
                                log.error(String.format(Misc.getInternationalized("CHRONOLOGIA %S NIE MIEŚCI SIĘ W ZAKRESIE DAT."), primaryColumnName));
                            }
                            if (climate.isEmpty()) {
                                log.error(String.format(Misc.getInternationalized("DANE KLIMATYCZNE %S NIE MIESZCZĄ SIĘ W ZAKRESIE DAT."), climateColumnsName));
                            }
                            break;
                        }

                        CorrelationProcessing pearsons = new CorrelationProcessing(runParams, dataToCorrelate);
                        Results result = pearsons.go(commonYearStartLimit, commonYearEndLimit);
                        if (result != null) {
                            result.yearStart = commonYearStartLimit;
                            result.yearEnd = commonYearEndLimit;
                            result.chronoTitle = primaryColumnName;
                            result.climateTitle = climateColumnsName;
                            results.add(result);
                        }
                        runParams.getProgress().setFilesDone(fileCounter);
                    }
                    break;
                case DAILY:
                    for (FileDataContainer daily : dailyDataContainer) {
                        fileCounter++;
                        
                        runParams.getProgress().resetJobsProgress();
                        runParams.getProgress().setHowManyJobs(3);
                        int commonYearStartLimit = Math.max(chronology.getYearMin(), daily.getYearMin());
                        int commonYearEndLimit = Math.min(chronology.getYearMax(), daily.getYearMax());

                        if (commonYearStartLimit > commonYearEndLimit) {
                            throw new DataException(Misc.getInternationalized("niepokrywające się lata"));
                        }

                        double[] primaryColumnData = null;

                        switch (runParams.getChronologyFileType()) {
                            case TABS:
                                primaryColumnData = ((TabsDataContainer) chronology).getArray(runParams.getChronologyColumn(), commonYearStartLimit, commonYearEndLimit);
                                primaryColumnName = primaryColumnNameStart + " (" + runParams.getChronologyColumn() + ")";
                                break;
                            case DEKADOWY:
                                primaryColumnData = ((DekaSerie) chronology).getArrayData(commonYearStartLimit, commonYearEndLimit);
                                primaryColumnName = primaryColumnNameStart + " (" + ((DekaSerie) chronology).getChronoCode() + ")";
                                break;
                            case TABS_MULTICOL:
                                primaryColumnData = ((TabsMulticolDataContainer) chronology).getArray(commonYearStartLimit, commonYearEndLimit);
                                primaryColumnName = primaryColumnNameStart + " (" + ((TabsMulticolDataContainer) chronology).getColumnNumber() + ". column)";
                        }

                        Type1DataContainer d = ((Type1DataContainer) daily);
                        // bottleneck 1:
                        long b1s = System.currentTimeMillis();
                        d.setProgress(runParams.getProgress());
                        d.populateYearlyCombinations(runParams);
                        String secondaryName = d.getSourceFile().getName() + ": "
                                + d.getStation() + " in years " + commonYearStartLimit
                                + "-" + commonYearEndLimit;
                        int max = YearlyCombinations.getCombinations().size();
                        float curr = 1;
                        double b1e = (System.currentTimeMillis() - b1s) / 1000.0;
                        log.debug("Bottleneck 1 (averaging): " + b1e + " ms");
                        runParams.getProgress().currentJobFinished();

                        // bottleneck 2:
                        long b2s = System.currentTimeMillis();
                        double done = 0;
                        for (Pair<MonthDay, MonthDay> p : YearlyCombinations.getCombinations()) {

                            done = curr / max * 100;
                            if (Debug.IS_DUBUGGGING) {
                                System.out.println("Values prep.: " + done + " %");
                            }
                            runParams.getProgress().setCurrentJobProgress(done/100);

                            ArrayList<Double> priCol = new ArrayList<>(commonYearEndLimit - commonYearStartLimit);
                            ArrayList<Double> daiCol = new ArrayList<>(commonYearEndLimit - commonYearStartLimit);
                            for (int i = commonYearStartLimit; i <= commonYearEndLimit; i++) {
                                LocalDate ld1;
                                LocalDate ld2;
                                try {
                                    ld1 = p.getFirst().toLocalDate(i);
                                    ld2 = p.getSecond().toLocalDate(i);
                                } catch (org.joda.time.IllegalFieldValueException e) {
                                    // np. 02.1937 - ma tylko 28 dni
                                    continue;
                                }
                                double theValue = d.getAvaragedValue(ld1, ld2);
                                if (theValue != FileDataContainer.MISSING_VALUE) {
                                    priCol.add(primaryColumnData[i - commonYearStartLimit]);
                                    daiCol.add(theValue);
                                }
                            }

                            double[] priVals = new double[priCol.size()];
                            double[] vals = new double[priCol.size()];
                            for (int i = 0; i < priCol.size(); i++) {
                                priVals[i] = priCol.get(i);
                                vals[i] = daiCol.get(i);
                            }

                            DailyResult res;
//                            double[] vals = d.getAvaragedValuesForYears(p.getFirst(), p.getSecond(), commonYearStartLimit, commonYearEndLimit);
                            String colName = d.getSourceFile().getName() + ": "
                                    + d.getStation() + " (Range: "
                                    + p.getFirst().toString()
                                    + p.getSecond().toString()
                                    + " in years " + commonYearStartLimit
                                    + "-" + commonYearEndLimit + ")";
                            dataToCorrelate.daily.put(p, new Pair<>(new Column(primaryColumnName, priVals), new Column(colName, vals)));
                            curr++;
                        }
                        double b2e = (System.currentTimeMillis() - b2s) / 1000.0;
                        log.debug("Bottleneck 2 (values prep.): " + b2e + " ms");
                        runParams.getProgress().currentJobFinished();

                        if (chronology.isEmpty() || daily.isEmpty()) {
                            if (chronology.isEmpty()) {
                                log.error(String.format(Misc.getInternationalized("CHRONOLOGIA %S NIE MIEŚCI SIĘ W ZAKRESIE DAT."), primaryColumnName));
                            }
                            if (daily.isEmpty()) {
                                // TODO
                                //log.log(Level.SEVERE, String.format(Misc.getInternationalized("DANE KLIMATYCZNE %S NIE MIESZCZĄ SIĘ W ZAKRESIE DAT."), dailyColumnsName));
                            }
                            break;
                        }

                        log.info(primaryColumnName + " : " + secondaryName);

                        // bottleneck 3:
                        CorrelationProcessing pearsons = new CorrelationProcessing(runParams, dataToCorrelate);
                        long b3s = System.currentTimeMillis();
                        Results result = pearsons.go(commonYearStartLimit, commonYearEndLimit);
                        double b3e = (System.currentTimeMillis() - b3s) / 1000.0;
                        log.debug("Bottleneck 3 (correlating): " + b3e + " ms");
                        runParams.getProgress().currentJobFinished();
                        if (result != null) {
                            result.yearStart = commonYearStartLimit;
                            result.yearEnd = commonYearEndLimit;
                            result.chronoTitle = primaryColumnName;
                            result.dailyTitle = secondaryName;
                            results.add(result);
                        }
                        runParams.getProgress().setFilesDone(fileCounter);
                    }

                    break;
            }

        }
    }

    private void save(File f) {
        if (f != null) {
            log.info(Misc.getInternationalized("ZAPISYWANIE..."));
            ResultsSaver saver = new ResultsSaver(runParams, f, results);
            saver.save();
        }
        results.clear();
        System.gc();
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        isAnyComputationRunning.set(true);
        log.info(Misc.getInternationalized("Wczytywanie danych z plikow"));
        /*
         load all data to respective data containers
         */
        try {
            switch (runParams.getChronologyFileType()) {
                case TABS:
                    getTabs();
                    break;
                case DEKADOWY:
                    getDeka();
                    break;
                case TABS_MULTICOL:
                    getTabsExt();
                    break;
            }

            if (runParams.getRunType() != RunType.DAILY) {
                switch (runParams.getClimateFileType()) {
                    case ICRU:
                        getIcru();
                        break;
                    /*case PRN:
                     getPrn();
                     break;*/
                    case AO:
                        getAo();
                        break;
                }
            } else {
                switch (runParams.getDailyFileType()) {
                    case TYPE1:
                        getType1();
                        break;
                }
            }

            process();
            log.info(Misc.getInternationalized("zakonczono obliczenia"));
            isAnyComputationRunning.set(false);
            long end = System.currentTimeMillis();
            log.trace("runtime: " + (end - start) + "ms");

            Platform.runLater(() -> {
                Media media = new Media(getClass().getClassLoader().getResource("resources/bell_ring.mp3").toString());
                MediaPlayer mp = new MediaPlayer(media);
                mp.play();

                javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
                File file = fc.showSaveDialog(runParams.getRoot());

                if (file != null) {
                    new Thread(() -> {
                        File dst = file;
                        if (!dst.getName().endsWith(".xlsm")) {
                            dst = new File(dst.getParent(), dst.getName() + ".xlsm");
                        }
                        save(dst);
                    }).start();
                }else{
                    log.info("");
                }
            });
        } catch (DataException ex) {
            log.error(ex.getMessage());
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
        } catch (NullPointerException | IOException ex) {
            log.error(Misc.getInternationalized("BŁĄD ODCZYTU Z PLIKU."));
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            log.error(Misc.getInternationalized("WYSTĄPIŁ NIEZNANY BŁĄD."));
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
        }finally{
            isAnyComputationRunning.set(false);
        }
    }

    public void go() {
        computationThread.start();
    }

    class DataException extends IOException {

        public DataException() {
        }

        public DataException(String message) {
            super(message);
        }

        public DataException(String message, Throwable cause) {
            super(message, cause);
        }

        public DataException(Throwable cause) {
            super(cause);
        }
    }

    public RunParams getRunParams() {
        return runParams;
    }

    public void setWp(RunParams wp) {
        this.runParams = wp;
    }

}
