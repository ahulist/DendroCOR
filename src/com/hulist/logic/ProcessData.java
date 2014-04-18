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
import com.hulist.logic.climate._prn.PrnDataContainer;
import com.hulist.logic.climate._prn.PrnImporter;
import com.hulist.logic.climate.icru.IcruDataContainer;
import com.hulist.logic.climate.icru.IcruImporter;
import com.hulist.util.FileChooser;
import com.hulist.util.MonthsPair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class ProcessData implements Runnable {

    WindowParams wp = null;
    Thread.UncaughtExceptionHandler handler = null;

    private final Logger log;
    private Thread computationThread;

    private final ArrayList<DetailedFileDataContainer> chronologyDataContainer = new ArrayList<>();
    private final ArrayList<DetailedFileDataContainer> climateDataContainer = new ArrayList<>();
    private final DataToCorrelate dataToCorrelate = new DataToCorrelate();
    private final ArrayList<Results> results = new ArrayList<>();

    private final FileChooser fc = new FileChooser(FileChooser.Purpose.SAVE);

    public ProcessData(WindowParams wp) {
        this.wp = wp;
        this.log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);

        this.computationThread = new Thread(this);

        fc.setAddXlsxExt(true);
        fc.setOnSaveDialogMessage("Czy zapisać otrzymane dane do pliku?");
    }

    public ProcessData(WindowParams wp, Thread.UncaughtExceptionHandler handler) {
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
     log.log(Level.SEVERE, "Błąd odczytu z pliku.");
     log.log(Level.FINEST, ex.getMessage());
     throw new RuntimeException(ex);
     } catch( Exception ex ){
     log.log(Level.SEVERE, "Wystąpił nieznany błąd.");
     log.log(Level.FINEST, ex.getMessage());
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
    private void getTabs() throws IOException, NullPointerException {
        for( File file : wp.getChronologyFiles() ) {
            TabsImporter tabsImporter = new TabsImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            TabsDataContainer tabsCont = tabsImporter.getData(file).get(0);

            chronologyDataContainer.add(tabsCont);
        }
    }

    private void getDeka() throws IOException {
        for( File file : wp.getChronologyFiles() ) {
            DekaImporter dekaImporter = new DekaImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            DekaSeriesDataContainer dekaCont = new DekaSeriesDataContainer(dekaImporter.getData(file));

            dekaCont.getSeries().stream().forEach(( serie ) -> {
                chronologyDataContainer.add(serie);
            });
        }
    }

    private void getIcru() throws IOException {
        for( File file : wp.getClimateFiles() ) {
            IcruImporter icruImporter = new IcruImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            IcruDataContainer icruCont = icruImporter.getData(file).get(0);

            climateDataContainer.add(icruCont);
        }
    }

    private void getPrn() throws IOException {
        for( File file : wp.getClimateFiles() ) {
            PrnImporter prnImporter = new PrnImporter(wp.isAllYears(), wp.getStartYear(), wp.getEndYear());
            PrnDataContainer prnCont = prnImporter.getData(file).get(0);

            climateDataContainer.add(prnCont);
        }
    }

    private void process() {
        for( DetailedFileDataContainer chronology : chronologyDataContainer ) {

            for( DetailedFileDataContainer climate : climateDataContainer ) {
                String primaryColumnName = chronology.getSourceFile().getName();
                int commonYearStartLimit = Math.max(chronology.getYearMin(), climate.getYearMin());
                int commonYearEndLimit = Math.min(chronology.getYearMax(), climate.getYearMax());

                double[] primaryColumnData = null;

                switch( wp.getChronologyFileType() ) {
                    case TABS:
                        primaryColumnData = ((TabsDataContainer) chronology).getArray(wp.getChronologyColumn(), commonYearStartLimit, commonYearEndLimit);
                        primaryColumnName += " (" + wp.getChronologyColumn() + ")";
                        break;
                    case DEKADOWY:
                        primaryColumnData = ((DekaSerie) chronology).getArrayData(commonYearStartLimit, commonYearEndLimit);
                        primaryColumnName += " (" + ((DekaSerie) chronology).getChronoCode() + ")";
                        break;
                }
                dataToCorrelate.primary = new Column(primaryColumnName, primaryColumnData);

                String climateColumnsName = climate.getSourceFile().getName();
                for( MonthsPair months : wp.getMonthsColumns() ) {
                    switch( wp.getClimateFileType() ) {
                        case ICRU:
                            double[] icruData = ((IcruDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                            Column icruC = new Column(climateColumnsName, icruData);
                            dataToCorrelate.columns.put(months, icruC);
                            break;
                        case PRN:
                            double[] prnData = ((PrnDataContainer) climate).getArray(months, commonYearStartLimit, commonYearEndLimit);
                            Column prnC = new Column(climateColumnsName, prnData);
                            dataToCorrelate.columns.put(months, prnC);
                            break;
                    }
                }

                if( chronology.isEmpty() || climate.isEmpty() ){
                    if( chronology.isEmpty() ){
                        log.log(Level.SEVERE, String.format("chronologia %s nie mie\u015bci si\u0119 w zakresie dat.", primaryColumnName));
                    }
                    if( climate.isEmpty() ){
                        log.log(Level.SEVERE, String.format("dane klimatyczne %s nie mieszcz\u0105 si\u0119 w zakresie dat.", climateColumnsName));
                    }
                    break;
                }

                PearsonsCorrelationProcessing pearsons = new PearsonsCorrelationProcessing(wp, dataToCorrelate);
                Results result = pearsons.go(commonYearStartLimit, commonYearEndLimit);
                if( result != null ){
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
        if( saveDest != null && saveDest.length>0 && saveDest[0]!=null ){
            log.log(Level.INFO, "Zapisywanie...");
            ResultsSaver saver = new ResultsSaver(wp, saveDest[0], results);
            saver.save();
        }
    }

    @Override
    public void run() {
        log.log(Level.FINE, "Uruchomiono przetwarzanie danych.");
        try {
            switch( wp.getChronologyFileType() ) {
                case TABS:
                    getTabs();
                    break;
                case DEKADOWY:
                    getDeka();
                    break;
            }

            switch( wp.getClimateFileType() ) {
                case ICRU:
                    getIcru();
                    break;
                case PRN:
                    getPrn();
                    break;
            }

            process();
            save();
        } catch( NullPointerException | IOException ex ) {
            log.log(Level.SEVERE, "Błąd odczytu z pliku.");
            log.log(Level.FINEST, ex.getMessage());
            throw new RuntimeException(ex);
        } catch( Exception ex ) {
            log.log(Level.SEVERE, "Wystąpił nieznany błąd.");
            log.log(Level.FINEST, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void go() {
        computationThread.start();
    }

}
