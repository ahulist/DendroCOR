/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.util.ExcelUtil;
import com.hulist.util.Misc;
import com.hulist.util.MonthsPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
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
    
    private final File file;
    private final WindowParams wp;
    private final ArrayList<Results> results;
    private final Logger log;

    public ResultsSaver(WindowParams wp, File file, ArrayList<Results> results) {
        this.wp = wp;
        this.file = file;
        this.results = results;

        log = Logger.getLogger(this.getClass().getCanonicalName());
        log.setLevel(Level.ALL);
    }

    public void save() {
        boolean appendingToExistingFile = false;
        XSSFWorkbook wb = null;
        if( file.exists() ){
            appendingToExistingFile = true;
            FileInputStream fos;
            try {
                fos = new FileInputStream(file);
                wb = new XSSFWorkbook(fos);
            } catch( IOException ex ) {
                log.log(Level.SEVERE, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("BŁĄD PODCZAS ZAPISU DO PLIKU %S"), file.getName()));
                log.log(Level.FINEST, Misc.stackTraceToString(ex));
                throw new RuntimeException();
            }
        } else {
            wb = new XSSFWorkbook();
        }
        XSSFSheet sh;
        try {
            sh = wb.getSheetAt(0);
        } catch( IllegalArgumentException e ) {
            sh = wb.createSheet();
        }

        if( appendingToExistingFile && !isFirstRowCoherent(sh) ){
            log.log(Level.SEVERE, java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("NIESPÓJNE KOLUMNY W ISTNIEJĄCYM PLIKU."));
            return;
        }

        int firstFreeRow = sh.getLastRowNum() + 1;
        if( firstFreeRow == 1 ){
            createFirstRow(sh);
        }

        for( Results res : results ) {
            Cell rowName = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 0, Cell.CELL_TYPE_STRING);
            rowName.setCellValue(res.chronoTitle);
            Cell rowName2 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 1, Cell.CELL_TYPE_STRING);
            rowName2.setCellValue(res.climateTitle);
            Cell rowName3 = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), 2, Cell.CELL_TYPE_STRING);
            rowName3.setCellValue(res.yearStart+"-"+res.yearEnd);

            int colCounter = FIRST_COL_NUM;
            for( MonthsPair col : wp.getMonthsColumns() ) {
                Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, firstFreeRow), colCounter, Cell.CELL_TYPE_NUMERIC);
                c.setCellValue(res.map.get(col));
                colCounter++;
            }

            firstFreeRow++;
        }

        for( int i = 0; i < wp.getMonthsColumns().size() + FIRST_COL_NUM; i++ ) {
            sh.autoSizeColumn(i);
        }

        try {
            ExcelUtil.saveToFile(file, wb);
            log.log(Level.INFO, String.format("\n"+java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("DANE ZAPISANO DO PLIKU %S")
                    + "\n-------------------------------------", file.getName()));
        } catch( IOException ex ) {
            log.log(Level.SEVERE, java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("BŁĄD ZAPISU PLIKU."));
            log.log(Level.FINEST, Misc.stackTraceToString(ex));
        }
    }

    private void createFirstRow(XSSFSheet sh) {
        int counter = FIRST_COL_NUM;
        for( MonthsPair col : wp.getMonthsColumns() ) {
            Cell c = ExcelUtil.getCell(ExcelUtil.getRow(sh, 0), counter, Cell.CELL_TYPE_STRING);
            c.setCellValue(col.toString());
            counter++;
        }
    }

    private boolean isFirstRowCoherent(XSSFSheet sh) {
        XSSFRow existingRow = ExcelUtil.getRow(sh, 0);
        int monthsCount = wp.getMonthsColumns().size();
        int monthsCountInFile = existingRow.getLastCellNum() - FIRST_COL_NUM;
        if( monthsCount != monthsCountInFile ){
            return false;
        }
        for( int i = FIRST_COL_NUM; i < existingRow.getLastCellNum(); i++ ) {
            if( !existingRow.getCell(i).getStringCellValue().equals(wp.getMonthsColumns().get(i - FIRST_COL_NUM).toString()) ){
                return false;
            }
        }
        return true;
    }

}
