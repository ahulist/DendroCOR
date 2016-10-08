/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.gui.MainWindow;
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
import com.hulist.util.FileChooser;
import com.hulist.util.LogsSaver;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class ProcessData implements Runnable {

    RunParams wp = null;
    Thread.UncaughtExceptionHandler handler = null;

    private final Logger log = LoggerFactory.getLogger(ProcessData.class);
    private Thread computationThread;

    private final ArrayList<FileDataContainer> chronologyDataContainer = new ArrayList<>();
    private final ArrayList<FileDataContainer> climateDataContainer = new ArrayList<>();
    private final DataToCorrelate dataToCorrelate = new DataToCorrelate();
    private final ArrayList<Results> results = new ArrayList<>();

    private final FileChooser fc = new FileChooser(FileChooser.Purpose.SAVE);

    public ProcessData(RunParams wp) {
        this.wp = wp;

        this.computationThread = new Thread(this);

        fc.setAddXlsmExt(true);
        fc.setOnSaveDialogMessage(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("CZY ZAPISAĆ OTRZYMANE DANE DO PLIKU?"));
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
        for (File file : wp.getChronologyFiles()) {
            TabsMulticolImporter tabsImporter = new TabsMulticolImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            chronologyDataContainer.addAll(tabsImporter.getData(file));
        }
    }

    private void getTabs() throws IOException, NullPointerException {
        for (File file : wp.getChronologyFiles()) {
            TabsImporter tabsImporter = new TabsImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            TabsDataContainer tabsCont = tabsImporter.getData(file).get(0);

            chronologyDataContainer.add(tabsCont);
        }
    }

    private void getDeka() throws IOException {
        for (File file : wp.getChronologyFiles()) {
            DekaImporter dekaImporter = new DekaImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            DekaSeriesDataContainer dekaCont = new DekaSeriesDataContainer(dekaImporter.getData(file));

            dekaCont.getSeries().stream().forEach((serie) -> {
                chronologyDataContainer.add(serie);
            });
        }
    }

    private void getIcru() throws IOException {
        for (File file : wp.getClimateFiles()) {
            IcruImporter icruImporter = new IcruImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            IcruDataContainer icruCont = icruImporter.getData(file).get(0);

            climateDataContainer.add(icruCont);
        }
    }

    private void getAo() throws IOException {
        for (File file : wp.getClimateFiles()) {
            AoImporter aoImporter = new AoImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            AoDataContainer aoCont = aoImporter.getData(file).get(0);

            climateDataContainer.add(aoCont);
        }
    }

    private void getPrn() throws IOException {
        for (File file : wp.getClimateFiles()) {
            PrnImporter prnImporter = new PrnImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            PrnDataContainer prnCont = prnImporter.getData(file).get(0);

            climateDataContainer.add(prnCont);
        }
    }

    private void process() throws IOException {
        for (FileDataContainer chronology : chronologyDataContainer) {

            for (FileDataContainer climate : climateDataContainer) {
                String primaryColumnName = chronology.getSourceFile().getName();
                int commonYearStartLimit = Math.max(chronology.getYearMin(), climate.getYearMin());
                int commonYearEndLimit = Math.min(chronology.getYearMax(), climate.getYearMax());

                if (commonYearStartLimit > commonYearEndLimit) {
                    throw new DataException(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("niepokrywające się lata"));
                }

                double[] primaryColumnData = null;

                switch (wp.getChronologyFileType()) {
                    case TABS:
                        primaryColumnData = ((TabsDataContainer) chronology).getArray(wp.getChronologyColumn(), commonYearStartLimit, commonYearEndLimit);
                        primaryColumnName += " (" + wp.getChronologyColumn() + ")";
                        break;
                    case DEKADOWY:
                        primaryColumnData = ((DekaSerie) chronology).getArrayData(commonYearStartLimit, commonYearEndLimit);
                        primaryColumnName += " (" + ((DekaSerie) chronology).getChronoCode() + ")";
                        break;
                    case TABS_MULTICOL:
                        primaryColumnData = ((TabsMulticolDataContainer) chronology).getArray(commonYearStartLimit, commonYearEndLimit);
                        primaryColumnName += " (" + ((TabsMulticolDataContainer) chronology).getColumnNumber() + ". column)";
                }
                dataToCorrelate.primary = new Column(primaryColumnName, primaryColumnData);

                String climateColumnsName = climate.getSourceFile().getName();
                for (MonthsPair months : wp.getMonthsColumns()) {
                    switch (wp.getClimateFileType()) {
                        case ICRU:
                            double[] icruData = ((IcruDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                            Column icruC = new Column(climateColumnsName, icruData);
                            dataToCorrelate.columns.put(months, icruC);
                            break;
                        /*case PRN:
                         double[] prnData = ((PrnDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                         Column prnC = new Column(climateColumnsName, prnData);
                         dataToCorrelate.columns.put(months, prnC);
                         break;*/
                        case AO:
                            double[] aoData = ((AoDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                            Column aoC = new Column(climateColumnsName, aoData);
                            dataToCorrelate.columns.put(months, aoC);
                            break;
                    }
                }

                if (chronology.isEmpty() || climate.isEmpty()) {
                    if (chronology.isEmpty()) {
                        log.error(String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("CHRONOLOGIA %S NIE MIEŚCI SIĘ W ZAKRESIE DAT."), primaryColumnName));
                    }
                    if (climate.isEmpty()) {
                        log.error(String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("DANE KLIMATYCZNE %S NIE MIESZCZĄ SIĘ W ZAKRESIE DAT."), climateColumnsName));
                    }
                    break;
                }

                CorrelationProcessing pearsons = new CorrelationProcessing(wp, dataToCorrelate);
                Results result = pearsons.go(commonYearStartLimit, commonYearEndLimit);
                if (result != null) {
                    result.yearStart = commonYearStartLimit;
                    result.yearEnd = commonYearEndLimit;
                    result.chronoTitle = primaryColumnName;
                    result.climateTitle = climateColumnsName;
                    results.add(result);
                }
            }
        }
    }

    private void save() {
        File[] saveDest = fc.call();
        if (saveDest != null && saveDest.length > 0 && saveDest[0] != null) {
            log.info(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ZAPISYWANIE..."));
            ResultsSaver saver = new ResultsSaver(wp, saveDest[0], results);
            saver.save();
        }
    }

    @Override
    public void run() {
        LogsSaver.getInstance().setIsLoggingOn(true/*wp.getPreferencesFrame().getCheckBoxLogging().isSelected()*/);
        long start = System.currentTimeMillis();
        log.debug(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("URUCHOMIONO PRZETWARZANIE DANYCH."));
        /*
         load all data to respective data containers
         */
        try {
            switch (wp.getChronologyFileType()) {
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

            switch (wp.getClimateFileType()) {
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
            log.error(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĄD ODCZYTU Z PLIKU."));
            log.trace(Misc.stackTraceToString(ex));
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            log.error(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("WYSTĄPIŁ NIEZNANY BŁĄD."));
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
}
