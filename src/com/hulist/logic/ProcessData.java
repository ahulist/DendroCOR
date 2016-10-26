/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.gui2.GUIMain;
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
import com.hulist.util.FileChooser;
import com.hulist.util.LogsSaver;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class ProcessData implements Runnable {

    RunParams runParams = null;
    Thread.UncaughtExceptionHandler handler = null;

    private final Logger log = LoggerFactory.getLogger(ProcessData.class);
    private Thread computationThread;

    private final ArrayList<FileDataContainer> chronologyDataContainer = new ArrayList<>();
    private final ArrayList<FileDataContainer> climateDataContainer = new ArrayList<>();
    private final ArrayList<FileDataContainer> dailyDataContainer = new ArrayList<>();
    private final DataToCorrelate dataToCorrelate = new DataToCorrelate();
    private final ArrayList<Results> results = new ArrayList<>();

    private final FileChooser fc = new FileChooser(FileChooser.Purpose.SAVE);

    public ProcessData(RunParams wp) {
        this.runParams = wp;
        fc.setAddXlsmExt(true);

        this.computationThread = new Thread(this);

        fc.setOnSaveDialogMessage(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("CZY ZAPISAĆ OTRZYMANE DANE DO PLIKU?"));
    }

    public ProcessData(RunParams wp, Thread.UncaughtExceptionHandler handler) {
        this(wp);
        this.handler = handler;
    }

    /*@Override
     public void run() {
     Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "Uruchomiono przetwarzanie danych.");
     switch( wp.getSecColFileType() ) {
     case TABS:
     try {
     Results res = processTabs();
                    
     } catch( NullPointerException | IOException ex ) {
     log.error("Błąd odczytu z pliku.");
     log.trace(ex.getMessage());
     throw new RuntimeException(ex);
     } catch( Exception ex ){
     log.error("Wystąpił nieznany błąd.");
     log.trace(ex.getMessage());
     throw new RuntimeException(ex);
     }
     break;
     case DEKADOWY:
     break;
     }
     }

     public void go() {
     Thread t = new Thread(this);
     if( this.handler != null ){
     // TODO dodać obsługę w klasie wywołującej
     t.setUncaughtExceptionHandler(handler);
     }
     t.start();
     }*/
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
        for (FileDataContainer chronology : chronologyDataContainer) {
            String primaryColumnNameStart = chronology.getSourceFile().getName();
            String primaryColumnName = "";

            switch (runParams.getRunType()) {
                case MONTHLY:

                    for (FileDataContainer climate : climateDataContainer) {
                        int commonYearStartLimit = Math.max(chronology.getYearMin(), climate.getYearMin());
                        int commonYearEndLimit = Math.min(chronology.getYearMax(), climate.getYearMax());

                        if (commonYearStartLimit > commonYearEndLimit) {
                            throw new DataException(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("niepokrywające się lata"));
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
                                log.error(String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("CHRONOLOGIA %S NIE MIEŚCI SIĘ W ZAKRESIE DAT."), primaryColumnName));
                            }
                            if (climate.isEmpty()) {
                                log.error(String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("DANE KLIMATYCZNE %S NIE MIESZCZĄ SIĘ W ZAKRESIE DAT."), climateColumnsName));
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
                    }
                    break;
                case DAILY:

                    for (FileDataContainer daily : dailyDataContainer) {
                        int commonYearStartLimit = Math.max(chronology.getYearMin(), daily.getYearMin());
                        int commonYearEndLimit = Math.min(chronology.getYearMax(), daily.getYearMax());

                        if (commonYearStartLimit > commonYearEndLimit) {
                            throw new DataException(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("niepokrywające się lata"));
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
                        d.populateYearlyCombinations();
                        String secondaryName = d.getSourceFile().getName() + ": "
                                + d.getStation() + " in years " + commonYearStartLimit
                                + "-" + commonYearEndLimit;
                        int max = YearlyCombinations.getCombinations().size();
                        float curr = 1;
                        double b1e = (System.currentTimeMillis() - b1s)/1000.0;
                        
                        // bottleneck 2:
                        long b2s = System.currentTimeMillis();
                        for (Pair<MonthDay, MonthDay> p : YearlyCombinations.getCombinations()) {

                            System.out.println("Values prep.: "+curr / max * 100 + " %");

                            ArrayList<Double> priCol = new ArrayList<>(commonYearEndLimit-commonYearStartLimit);
                            ArrayList<Double> daiCol = new ArrayList<>(commonYearEndLimit-commonYearStartLimit);
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
                        double b2e = (System.currentTimeMillis() - b2s)/1000.0;

                        if (chronology.isEmpty() || daily.isEmpty()) {
                            if (chronology.isEmpty()) {
                                log.error(String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("CHRONOLOGIA %S NIE MIEŚCI SIĘ W ZAKRESIE DAT."), primaryColumnName));
                            }
                            if (daily.isEmpty()) {
                                // TODO
                                //log.log(Level.SEVERE, String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("DANE KLIMATYCZNE %S NIE MIESZCZĄ SIĘ W ZAKRESIE DAT."), dailyColumnsName));
                            }
                            break;
                        }

                        log.info(primaryColumnName + " : " + secondaryName);

                        // bottleneck 3:
                        CorrelationProcessing pearsons = new CorrelationProcessing(runParams, dataToCorrelate);
                        long b3s = System.currentTimeMillis();
                        Results result = pearsons.go(commonYearStartLimit, commonYearEndLimit);
                        double b3e = (System.currentTimeMillis() - b3s)/1000.0;
                        if (result != null) {
                            result.yearStart = commonYearStartLimit;
                            result.yearEnd = commonYearEndLimit;
                            result.chronoTitle = primaryColumnName;
                            result.dailyTitle = secondaryName;
                            results.add(result);
                        }
                    }

                    break;
            }

        }
    }

    private void save() {
        File[] saveDest = fc.call();
        if (saveDest != null && saveDest.length > 0 && saveDest[0] != null) {
            log.info(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("ZAPISYWANIE..."));
            ResultsSaver saver = new ResultsSaver(runParams, saveDest[0], results);
            saver.save();
        }
        results.clear();
        System.gc();
    }

    @Override
    public void run() {
        LogsSaver.getInstance().setIsLoggingOn(true/*wp.getPreferencesFrame().getCheckBoxLogging().isSelected()*/);
        long start = System.currentTimeMillis();
        log.debug(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("URUCHOMIONO PRZETWARZANIE DANYCH."));
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
            long end = System.currentTimeMillis();
            LogsSaver.getInstance().setIsLoggingOn(true);
            log.trace("runtime: " + (end - start) + "ms");
            save();
        } catch (DataException ex) {
            log.error(ex.getMessage());
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
        } catch (NullPointerException | IOException ex) {
            log.error(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("BŁĄD ODCZYTU Z PLIKU."));
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            log.error(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("WYSTĄPIŁ NIEZNANY BŁĄD."));
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
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

    public RunParams getWp() {
        return runParams;
    }

    public void setWp(RunParams wp) {
        this.runParams = wp;
    }
    
}
