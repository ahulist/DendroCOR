/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.gui2.PreferencesFXMLController;
import com.hulist.gui2.PreferencesFXMLController.PlotColorType;
import com.hulist.logic.chronology.deka.DekaImporter;
import com.hulist.logic.chronology.deka.DekaSerie;
import com.hulist.logic.chronology.deka.DekaSeriesDataContainer;
import com.hulist.logic.chronology.rcs.RcsDataContainer;
import com.hulist.logic.chronology.rcs.RcsImporter;
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
import com.hulist.logic.daily.DailyFileDataContainer;
import com.hulist.logic.daily.DailyResult;
import com.hulist.logic.daily.YearlyCombinations;
import com.hulist.logic.daily.N_YMD_VS.N_YMD_VSDataContainer;
import com.hulist.logic.daily.N_YMD_VS.N_YMD_VSImporter;
import com.hulist.logic.daily.N_YMD_VS.N_YMD_VSSeriesDataContainer;
import com.hulist.logic.daily.Y_M_D_V.Y_M_D_VImporter;
import com.hulist.logic.daily.Y_M_D_V.Y_M_D_VSeriesDataContainer;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import org.joda.time.DateTime;
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

        isAnyComputationRunning.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                runParams.getProgress().resetJobsProgress();
            }
        });
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

    private void getRcs() throws IOException, NullPointerException {
        for (File file : runParams.getChronologyFiles()) {
            RcsImporter rcsImporter = new RcsImporter(runParams);
            RcsDataContainer rcsCont = rcsImporter.getData(file).get(0);

            chronologyDataContainer.add(rcsCont);
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

    private void getN_YMD_VS() throws IOException {
        for (File file : runParams.getDailyFile()) {
            N_YMD_VSImporter type1Importer = new N_YMD_VSImporter(runParams);
            N_YMD_VSSeriesDataContainer seriesCont = new N_YMD_VSSeriesDataContainer(type1Importer.getData(file));

            dailyDataContainer.addAll(seriesCont.getData());
        }
    }

    private void getY_M_D_V() throws IOException {
        for (File file : runParams.getDailyFile()) {
            Y_M_D_VImporter type2Importer = new Y_M_D_VImporter(runParams);
            Y_M_D_VSeriesDataContainer seriesCont = new Y_M_D_VSeriesDataContainer(type2Importer.getData(file));

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
        switch (runParams.getRunType()) {
            case MONTHLY:
                runParams.getProgress().setHowManyFiles(chronologyDataContainer.size() * climateDataContainer.size());
                break;
            case DAILY:
                runParams.getProgress().setHowManyFiles(chronologyDataContainer.size() * dailyDataContainer.size());
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
                            String msg = Misc.getInternationalized("niepokrywajace sie lata");
                            int errCol = -1;
                            if (chronology instanceof TabsMulticolDataContainer) {
                                errCol = ((TabsMulticolDataContainer) chronology).getColumnNumber();
                            }
                            if (errCol != -1) {
                                msg = msg + " ("+chronology.getSourceFile().getName()+ ", " + Misc.getInternationalized("MainWindow.labelColumn.text") + ": " + errCol + ")";
                            }
                            
                            log.warn(msg);
                            continue;
//                            throw new DataException(msg);
                        }

                        double[] primaryColumnData = null;

                        switch (runParams.getChronologyFileType()) {
                            case RCS:
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
                            throw new DataException(Misc.getInternationalized("niepokrywajace sie lata"));
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

                        DailyFileDataContainer d = ((DailyFileDataContainer) daily);
                        // bottleneck 1:
                        long b1s = System.currentTimeMillis();
                        d.setProgress(runParams.getProgress());
                        d.populateYearlyCombinations(runParams);
                        String secondaryName = d.getSourceFile().getName() + " "
                                + "(" + commonYearStartLimit + "-" + commonYearEndLimit + ") "
                                + (d instanceof N_YMD_VSDataContainer ? ((N_YMD_VSDataContainer) d).getStation() : "");
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
                            runParams.getProgress().setCurrentJobProgress(done / 100);

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
                            String colName = d.getSourceFile().getName() + " "
                                    + "(" + commonYearStartLimit + "-" + commonYearEndLimit + ") "
                                    + (d instanceof N_YMD_VSDataContainer ? ((N_YMD_VSDataContainer) d).getStation() : "")
                                    + "(" + p.getFirst().toString() + "-"
                                    + p.getSecond().toString() + ")";
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

                        result.dailyPlot = getDailyPlot(runParams, result);

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
                case RCS:
                    getRcs();
                    break;
            }

            if (runParams.getRunType().equals(RunType.MONTHLY)) {   // MONTHLY
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
            } else {        // DAILY
                switch (runParams.getDailyFileType()) {
                    case N_YMD_VS:
                        getN_YMD_VS();
                        break;
                    case Y_M_D_V:
                        getY_M_D_V();
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
                } else {
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
        } finally {
            isAnyComputationRunning.set(false);
        }
    }

    public void go() {
        computationThread.start();
    }

    private WritableImage getDailyPlot(RunParams runParams, Results result) {
        int extraSpace = 80;
        WritableImage wi = new WritableImage(741 + extraSpace, 741 + extraSpace);

        Platform.runLater(() -> {
            Canvas canvas = new Canvas(741 + extraSpace, 741 + extraSpace);

            MonthDay start = new MonthDay(1, 1);
            MonthDay end = new MonthDay(12, 31);
            MonthDay currentStart = start;
            MonthDay currentEnd = start;

            int counter = 1;

            boolean isPlotColored = runParams.getSettings().isPlotColored();
            PlotColorType plotColorType = runParams.getSettings().getPlotColorType();

            while (currentStart.isBefore(end)) {
                String monthStart = String.valueOf(currentStart.monthOfYear().get());
                String dayStart = String.valueOf(currentStart.dayOfMonth().get());
                DateTime dtS = new DateTime(2016, currentStart.getMonthOfYear(), currentStart.getDayOfMonth(), 0, 1);
                int x = extraSpace + dtS.getDayOfYear() * 2 + dtS.getMonthOfYear() - 1;
                while (currentEnd.isBefore(end)) {
                    String monthEnd = String.valueOf(currentEnd.monthOfYear().get());
                    String dayEnd = String.valueOf(currentEnd.dayOfMonth().get());
                    DateTime dtE = new DateTime(2016, currentEnd.getMonthOfYear(), currentEnd.getDayOfMonth(), 0, 1);
                    int y = extraSpace + (365 * 2 + 12 - 1) - (dtE.getDayOfYear() * 2 + dtE.getMonthOfYear() - 1);

                    String key = dayStart + monthStart + " " + dayEnd + monthEnd;

                    MetaCorrelation mc = result.dailyMap.get(new Pair<>(currentStart, currentEnd));
                    if (mc != null) {
                        if (Math.abs(mc.gettTestValue()) > mc.gettTestCritVal()
                                && (plotColorType.equals(PlotColorType.ALL)
                                || mc.getCorrelation() > 0 && plotColorType.equals(PlotColorType.POSITIVE_ONLY)
                                || mc.getCorrelation() < 0 && plotColorType.equals(PlotColorType.NEGATIVE_ONLY))) {

                            canvas.getGraphicsContext2D().setGlobalAlpha(Math.abs(mc.getCorrelation()));

                            if (isPlotColored) {
                                if (mc.getCorrelation() > 0) {
                                    canvas.getGraphicsContext2D().setFill(Color.RED);
                                } else {
                                    canvas.getGraphicsContext2D().setFill(Color.BLUE);
                                }
                            } else {
                                canvas.getGraphicsContext2D().setFill(Color.BLACK);
                                canvas.getGraphicsContext2D().setGlobalAlpha(Math.abs(mc.getCorrelation()));
                            }
                        } else {
                            canvas.getGraphicsContext2D().setFill(Color.BLACK);
                            canvas.getGraphicsContext2D().setGlobalAlpha(0.1);
                        }

                        canvas.getGraphicsContext2D().fillOval(x, y, 2, 2);
                    }

                    counter++;
                    if (Debug.IS_DUBUGGGING) {
                        System.out.println("Plot: " + counter);
                    }
                    currentEnd = currentEnd.plusDays(1);
                }

                currentStart = currentStart.plusDays(1);
                currentEnd = currentStart;
            }

            canvas.snapshot(null, wi);

            ImageView iv = new ImageView(wi);
            ImageView overlay;
            if (isPlotColored) {
                overlay = new ImageView(getClass().getClassLoader().getResource("resources/axes_legend_template.png").toString());
            } else {
                overlay = new ImageView(getClass().getClassLoader().getResource("resources/axes_legend_template_bw.png").toString());
            }
            overlay.setBlendMode(BlendMode.MULTIPLY);
            Group blend = new Group(iv, overlay);
            blend.snapshot(null, wi);
        });

        return wi;
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
