package com.hulist.logic;

import com.hulist.gui2.GUIMain;
import com.hulist.util.ExcelUtil;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.MonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class ResultsSaver {

    /**
     * first written-to column number in excel file in row 0 (0-based)
     */
    private final static int FIRST_COL_NUM = 3;

    enum Sheets {

        CORR(ResourceBundle.getBundle(GUIMain.BUNDLE, new Locale("en")).getString("Korelacja")),
        RUNNING_CORR(ResourceBundle.getBundle(GUIMain.BUNDLE, new Locale("en")).getString("Korelacja kroczaca")),
        DAILY(ResourceBundle.getBundle(GUIMain.BUNDLE, new Locale("en")).getString("Dane dzienne"));

        String name;

        private Sheets(String name) {
            this.name = name;
        }
    }

    private final File file;
    private final RunParams runParams;
    private final ArrayList<Results> results;
    private final Logger log = LoggerFactory.getLogger(ResultsSaver.class);
    private CellStyle style;

    private final int howManyDaily = 100;     // * 2 for two ends

    private final String templateEmptyPath = "templates/template_empty.xlsm";
    private final String templateMonthlyPath = "templates/template_monthly.xlsm";

    public ResultsSaver(RunParams wp, File file, ArrayList<Results> results) {
        this.runParams = wp;
        this.file = file;
        this.results = results;
    }

    /**
     * @return {@code true} on success
     */
    public boolean save() {
        boolean success = this.saver();

        if (success) {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.initOwner(runParams.getRoot());
                alert.setHeaderText(Misc.getInternationalized("czy otworzyc plik"));

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        log.warn(Misc.getInternationalized("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName());
                    }
                }
            });
        }

        return success;
    }

    /**
     * @return {@code true} on success
     */
    private boolean saver() {
        boolean appendingToExistingFile = false;
        boolean intermediateSuccess;
        XSSFWorkbook wb = null;
        OPCPackage pkg = null;
        InputStream fis = null;

        if (file.exists()) {
            appendingToExistingFile = true;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                log.error(String.format(Misc.getInternationalized("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName()));
                log.trace(Misc.stackTraceToString(ex));
                throw new RuntimeException();
            }
        } else {
//            try {
            switch (runParams.getRunType()) {
                case MONTHLY:
//                        fis = new FileInputStream(getClass().getClassLoader().getResource(templateMonthlyPath).getPath());
                    fis = getClass().getClassLoader().getResourceAsStream(templateMonthlyPath);
                    break;
                case DAILY:
//                        fis = new FileInputStream(getClass().getClassLoader().getResource(templateEmptyPath).getPath());
                    fis = getClass().getClassLoader().getResourceAsStream(templateEmptyPath);
                    break;
            }
//            } catch (FileNotFoundException ex) {
//                log.error(String.format(Misc.getInternationalized("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName()));
//                log.trace(Misc.stackTraceToString(ex));
//                throw new RuntimeException();
//            }
        }

        try {
            pkg = OPCPackage.open(fis);
            wb = new XSSFWorkbook(pkg);
        } catch (InvalidFormatException | IOException ex) {
            log.error(String.format(Misc.getInternationalized("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName()));
            log.debug(Misc.stackTraceToString(ex));
            throw new RuntimeException();
        }

        createSignificantCellStyle(wb);

        switch (runParams.getRunType()) {
            case MONTHLY:
                intermediateSuccess = populateCorrelation(wb, appendingToExistingFile);
                if (!intermediateSuccess) {
                    return false;
                }

                if (runParams.getSettings().isRunningCorrelation()) {
                    intermediateSuccess = populateRunningCorrelation(wb);
                    if (!intermediateSuccess) {
                        return false;
                    }
                }
                break;
            case DAILY:
                intermediateSuccess = populateDaily(wb, appendingToExistingFile);
                if (!intermediateSuccess) {
                    return false;
                }
                break;
        }

        try {
            fis.close();
        } catch (NullPointerException | IOException ex) {
            log.debug(Misc.stackTraceToString(ex));
        }

        try {
            ExcelUtil.saveToFile(file, wb);
            log.info(String.format(Misc.getInternationalized("DANE ZAPISANO DO PLIKU %S")
                    + "\n", file.getName()));
        } catch (IOException ex) {
            log.error(Misc.getInternationalized("BŁĄD ZAPISU PLIKU."));
            log.trace(Misc.stackTraceToString(ex));
        }

        if (pkg != null) {
            try {
                pkg.close();
            } catch (IOException ex) {
                log.debug(Misc.stackTraceToString(ex));
            }
        }
        return true;
    }

    private boolean populateCorrelation(XSSFWorkbook wb, boolean appendingToExistingFile) {
        XSSFSheet sh;
        sh = wb.getSheet(Sheets.CORR.name);
        if (sh == null) {
            sh = wb.createSheet(Sheets.CORR.name);
        }

        int firstFreeRow = sh.getLastRowNum() + 1;

        if (appendingToExistingFile && !isFirstRowCoherent(sh) && firstFreeRow != 1) {
            log.error(Misc.getInternationalized("NIESPÓJNE KOLUMNY W ISTNIEJĄCYM PLIKU."));
            return false;
        }

        if (firstFreeRow == 1) {
            createFirstRow(sh);
        }

        if (appendingToExistingFile && firstFreeRow != 1) {
            firstFreeRow++;
        }

        for (Results res : results) {
            Cell rowName = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 0, Cell.CELL_TYPE_STRING);
            rowName.setCellValue(res.chronoTitle);
            Cell rowName2 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 1, Cell.CELL_TYPE_STRING);
            rowName2.setCellValue(res.climateTitle);
            Cell rowName3 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 2, Cell.CELL_TYPE_STRING);
            rowName3.setCellValue(res.yearStart + "-" + res.yearEnd);

            int colCounter = FIRST_COL_NUM;
            for (MonthsPair col : runParams.getMonthsColumns()) {
                Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter, Cell.CELL_TYPE_NUMERIC);
                c.setCellValue(res.climateMap.get(col).getCorrelation());

                // significance
                if (res.isIsTTest() && FastMath.abs(res.climateMap.get(col).gettTestValue()) > FastMath.abs(res.climateMap.get(col).gettTestCritVal())) {
                    c.setCellStyle(style);
                }

                colCounter++;
            }

            firstFreeRow++;
        }

        autosizeColumns(runParams, sh);

        return true;
    }

    private boolean populateRunningCorrelation(XSSFWorkbook wb) {
        XSSFSheet sh;
        sh = wb.getSheet(Sheets.RUNNING_CORR.name);
        if (sh == null) {
            sh = wb.createSheet(Sheets.RUNNING_CORR.name);
        }

        // getting year min-max
        int yearMin = Integer.MAX_VALUE, yearMax = Integer.MIN_VALUE;
        int windowSize = 0;
        for (Results res : results) {
            if (res.yearStart < yearMin) {
                yearMin = res.yearStart;
            }
            if (res.yearEnd > yearMax) {
                yearMax = res.yearEnd;
            }
            windowSize = res.getWindowSize();
        }

        ExcelUtil.getCell(ExcelUtil.getRow(sh, 0), 3, Cell.CELL_TYPE_STRING).setCellValue("Range / Window mid-year");
        for (int i = 0; i < yearMax - yearMin - windowSize + 2; i++) {
            Cell year = ExcelUtil.getCell(ExcelUtil.getRow(sh, 0), i + 4, Cell.CELL_TYPE_NUMERIC);
            year.setCellValue(i + yearMin + windowSize / 2);
        }

        int firstFreeRow = 1;
        for (Results res : results) {
            if (res.isIsRunningCorr()) {
                Cell rowName = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 0, Cell.CELL_TYPE_STRING);
                rowName.setCellValue(res.chronoTitle);
                Cell rowName2 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 1, Cell.CELL_TYPE_STRING);
                rowName2.setCellValue(res.climateTitle);
                Cell rowName3 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 2, Cell.CELL_TYPE_STRING);
                rowName3.setCellValue(res.yearStart + "-" + res.yearEnd + " (" + Misc.getInternationalized("okno korelacji skrot") + ": " + res.getWindowSize() + ")");

                for (MonthsPair col : runParams.getMonthsColumns()) {
                    int colCounter = FIRST_COL_NUM;
                    Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter, Cell.CELL_TYPE_STRING);
                    c.setCellValue(col.toString());

                    for (double value : res.runningCorrMap.get(col).values()) {
                        colCounter++;
                        c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter + res.yearStart - yearMin, Cell.CELL_TYPE_NUMERIC);
                        c.setCellValue(value);
                    }
                    firstFreeRow++;
                }
            }
        }

        autosizeColumns(runParams, sh);

        return true;
    }

    private boolean populateDaily(XSSFWorkbook wb, boolean appendingToExistingFile) {
        for (Results res : results) {
            if (!res.dailyMap.isEmpty()) {
                Map<Pair<MonthDay, MonthDay>, MetaCorrelation> m = Misc.sortByValue(res.dailyMap);

                XSSFSheet sh;
                sh = wb.getSheet(Sheets.DAILY.name);
                if (sh == null) {
                    sh = wb.createSheet(Sheets.DAILY.name);
                }

                int firstFreeRow = sh.getLastRowNum() + 1;
                if (firstFreeRow != 1) {
                    firstFreeRow += 2;
                }

                boolean isMore = m.size() > howManyDaily * 2;
                boolean yearsSet = false;
                Iterable<Pair<MonthDay, MonthDay>> iter = m.keySet();

                if (isMore) {
                    ArrayList<Pair<MonthDay, MonthDay>> keys = new ArrayList<>();
                    int counter = 0;
                    for (Pair<MonthDay, MonthDay> p : m.keySet()) {
                        if (counter < howManyDaily) {
                            keys.add(p);
                            counter++;
                        } else {
                            break;
                        }
                    }
                    ArrayList<Pair<MonthDay, MonthDay>> keysReversed = new ArrayList<>(m.keySet());
                    Collections.reverse(keysReversed);
                    ArrayList<Pair<MonthDay, MonthDay>> endKeys = new ArrayList<>();
                    counter = 0;
                    for (Pair<MonthDay, MonthDay> p : keysReversed) {
                        if (counter < howManyDaily) {
                            endKeys.add(p);
                            counter++;
                        } else {
                            Collections.reverse(endKeys);
                            break;
                        }
                    }
                    keys.addAll(endKeys);
                    iter = keys;
                }

                for (Pair<MonthDay, MonthDay> p : iter) {
                    if (!yearsSet) {
                        XSSFCell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow - 1), 0, Cell.CELL_TYPE_STRING);
                        String dailyColName = res.params.getDailyColumnType()!=null?"("+res.params.getDailyColumnType().getDisplayName()+")":"";
                        c.setCellValue(res.chronoTitle + " / " + res.dailyTitle + dailyColName);
                        yearsSet = true;
                    }

                    ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 0, Cell.CELL_TYPE_STRING)
                            .setCellValue(p.getFirst().getDayOfMonth() + " " + p.getFirst().toString("MMM"));
                    ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 1, Cell.CELL_TYPE_STRING)
                            .setCellValue(p.getSecond().getDayOfMonth() + " " + p.getSecond().toString("MMM"));
                    ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 2, Cell.CELL_TYPE_NUMERIC).setCellValue(m.get(p).getCorrelation());
                    // significance
                    if (res.isIsTTest() && FastMath.abs(m.get(p).gettTestValue()) > FastMath.abs(m.get(p).gettTestCritVal())) {
                        ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 2, Cell.CELL_TYPE_NUMERIC).setCellStyle(style);
                    }

                    firstFreeRow++;
                }

                sh.autoSizeColumn(0);
                sh.autoSizeColumn(1);
            }
        }

        if (wb.getSheet("A") != null) {
            wb.removeSheetAt(wb.getSheetIndex(wb.getSheet("A")));
        }

        return true;
    }

    private void createFirstRow(XSSFSheet sh) {
        int counter = FIRST_COL_NUM;
        for (MonthsPair col : runParams.getMonthsColumns()) {
            Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, 0), counter, Cell.CELL_TYPE_STRING);

            String columnName = transformToNewMonthsName(col.toString());
            c.setCellValue(columnName);
            counter++;
        }
    }

    private boolean isFirstRowCoherent(XSSFSheet sh) {
        XSSFRow existingRow = ExcelUtil.getRow(sh, 0);
        int monthsCount = runParams.getMonthsColumns().size();
        int monthsCountInFile = existingRow.getLastCellNum() - FIRST_COL_NUM;
        if (monthsCount != monthsCountInFile) {
            return false;
        }
        for (int i = FIRST_COL_NUM; i < existingRow.getLastCellNum(); i++) {
            String existing = existingRow.getCell(i).getStringCellValue();
            String newVal = runParams.getMonthsColumns().get(i - FIRST_COL_NUM).toString();
            if (!(existing.equals(newVal) || existing.equals(transformToNewMonthsName(newVal)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * newName pJUN oldName JUN-JUN (1) //@param newName
     *
     * @return
     */
    private String transformToNewMonthsName(String oldName) {
        String start, end;
        int yearsShift = 0;
        start = oldName.substring(0, 3);
        end = oldName.substring(4, 7);
        if (oldName.endsWith(")")) {
            int index = oldName.indexOf("(");
            String number = oldName.substring(index + 1, oldName.length() - 1);
            yearsShift = Integer.parseInt(number);
        }
        if (start.equals(end) && yearsShift == 0) {
            return start;
        } else if (start.equals(end) && yearsShift == 1) {
            return "p" + start;
        } else {
            return oldName;
        }
    }

    private void autosizeColumns(RunParams wp, XSSFSheet sh) {
        for (int i = 0; i < wp.getMonthsColumns().size() + FIRST_COL_NUM; i++) {
            sh.autoSizeColumn(i);
        }
    }

    private void createSignificantCellStyle(XSSFWorkbook wb) {
        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    }
}
