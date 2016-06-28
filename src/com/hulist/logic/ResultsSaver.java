package com.hulist.logic;

import com.hulist.gui.MainWindow;
import com.hulist.util.ExcelUtil;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.math3.util.FastMath;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

        CORR(ResourceBundle.getBundle(MainWindow.BUNDLE, new Locale("en")).getString("Korelacja")),
        RUNNING_CORR(ResourceBundle.getBundle(MainWindow.BUNDLE, new Locale("en")).getString("Korelacja kroczaca"));

        String name;

        private Sheets(String name) {
            this.name = name;
        }
    }

    private final File file;
    private final RunParams wp;
    private final ArrayList<Results> results;
    private final Logger log;
    private CellStyle style;

    private final URL template = getClass().getClassLoader().getResource("templates/template.xlsm");

    public ResultsSaver(RunParams wp, File file, ArrayList<Results> results) {
        this.wp = wp;
        this.file = file;
        this.results = results;

        log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);
    }

    /**
     * @return {@code true} on success
     */
    public boolean save() {
        boolean success = this.saver();

        if (success) {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null,
                    String.format(ResourceBundle.getBundle(MainWindow.BUNDLE).getString("czy chcesz otworzyc zapisany plik")), "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    log.log(Level.WARNING, ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName());
                }
            }
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

        if (file.exists()) {
            appendingToExistingFile = true;
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                wb = new XSSFWorkbook(OPCPackage.open(fis));
            } catch (InvalidFormatException | IOException ex) {
                log.log(Level.SEVERE, String.format(ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName()));
                log.log(Level.FINEST, Misc.stackTraceToString(ex));
                throw new RuntimeException();
            }
        } else {
            try {
                //ClassLoader.class.getResourceAsStream("templates/template.xlsm");
                //fis = new FileInputStream(template.getPath());
                wb = new XSSFWorkbook(OPCPackage.open(ClassLoader.class.getResourceAsStream("/templates/template.xlsm")));
            } catch (InvalidFormatException | IOException ex) {
                log.log(Level.SEVERE, String.format(ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName()));
                log.log(Level.FINEST, Misc.stackTraceToString(ex));
                throw new RuntimeException();
            }
        }

        createSignificantCellStyle(wb);

        intermediateSuccess = populateCorrelation(wb, appendingToExistingFile);
        if (!intermediateSuccess) {
            return false;
        }

        intermediateSuccess = populateRunningCorrelation(wb);
        if (!intermediateSuccess) {
            return false;
        }

        try {
            ExcelUtil.saveToFile(file, wb);
            log.log(Level.INFO, String.format("\n" + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("DANE ZAPISANO DO PLIKU %S")
                    + "\n-------------------------------------", file.getName()));
        } catch (IOException ex) {
            log.log(Level.SEVERE, ResourceBundle.getBundle(MainWindow.BUNDLE).getString("BŁĄD ZAPISU PLIKU."));
            log.log(Level.FINEST, Misc.stackTraceToString(ex));
        }

        return true;
    }

    private boolean populateCorrelation(XSSFWorkbook wb, boolean appendingToExistingFile) {
        XSSFSheet sh;
        try {
            sh = wb.getSheetAt(0);
        } catch (IllegalArgumentException e) {
            sh = wb.createSheet();
        }
        wb.setSheetName(0, Sheets.CORR.name);

        if (appendingToExistingFile && !isFirstRowCoherent(sh)) {
            log.log(Level.SEVERE, ResourceBundle.getBundle(MainWindow.BUNDLE).getString("NIESPÓJNE KOLUMNY W ISTNIEJĄCYM PLIKU."));
            return false;
        }

        int firstFreeRow = sh.getLastRowNum() + 1;
        if (firstFreeRow == 1) {
            createFirstRow(sh);
        }

        if (appendingToExistingFile) {
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
            for (MonthsPair col : wp.getMonthsColumns()) {
                Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter, Cell.CELL_TYPE_NUMERIC);
                c.setCellValue(res.map.get(col).getCorrelation());

                // significance
                if (res.isIsTTest() && FastMath.abs(res.map.get(col).gettTestValue()) > FastMath.abs(res.map.get(col).gettTestCritVal())) {
                    c.setCellStyle(style);
                }

                colCounter++;
            }

            firstFreeRow++;
        }

        autosizeColumns(wp, sh);

        return true;
    }

    private boolean populateRunningCorrelation(XSSFWorkbook wb) {
        XSSFSheet sh;
        sh = wb.getSheet(Sheets.RUNNING_CORR.name);
        if (sh == null) {
            sh = wb.createSheet(Sheets.RUNNING_CORR.name);
        }
        
        int firstFreeRow = sh.getLastRowNum() + 1;
        for (Results res : results) {
            if (res.isIsRunningCorr()) {
                Cell rowName = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 0, Cell.CELL_TYPE_STRING);
                rowName.setCellValue(res.chronoTitle);
                Cell rowName2 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 1, Cell.CELL_TYPE_STRING);
                rowName2.setCellValue(res.climateTitle);
                Cell rowName3 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 2, Cell.CELL_TYPE_STRING);
                rowName3.setCellValue(res.yearStart + "-" + res.yearEnd + " (" + ResourceBundle.getBundle(MainWindow.BUNDLE).getString("okno korelacji skrot") + ": " + res.getWindowSize() + ")");

                for (MonthsPair col : wp.getMonthsColumns()) {
                    int colCounter = FIRST_COL_NUM;
                    Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter, Cell.CELL_TYPE_STRING);
                    c.setCellValue(col.toString());

                    for (double value : res.runningCorrMap.get(col).values()) {
                        colCounter++;
                        c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter, Cell.CELL_TYPE_NUMERIC);
                        c.setCellValue(value);
                    }
                    firstFreeRow++;
                }
            }
        }

        autosizeColumns(wp, sh);

        return true;
    }

    private void createFirstRow(XSSFSheet sh) {
        int counter = FIRST_COL_NUM;
        for (MonthsPair col : wp.getMonthsColumns()) {
            Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, 0), counter, Cell.CELL_TYPE_STRING);

            String columnName = transformToNewMonthsName(col.toString());
            c.setCellValue(columnName);
            counter++;
        }
    }

    private boolean isFirstRowCoherent(XSSFSheet sh) {
        XSSFRow existingRow = ExcelUtil.getRow(sh, 0);
        int monthsCount = wp.getMonthsColumns().size();
        int monthsCountInFile = existingRow.getLastCellNum() - FIRST_COL_NUM;
        if (monthsCount != monthsCountInFile) {
            return false;
        }
        for (int i = FIRST_COL_NUM; i < existingRow.getLastCellNum(); i++) {
            String existing = existingRow.getCell(i).getStringCellValue();
            String newVal = wp.getMonthsColumns().get(i - FIRST_COL_NUM).toString();
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
