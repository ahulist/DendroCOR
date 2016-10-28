/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class ExcelUtil {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);
    
    public static XSSFCell getCell(XSSFRow row, int cell, int cellType) {
        if( row.getCell(cell) == null ){
            row.createCell(cell, cellType);
        }
        return row.getCell(cell);
    }

    public static void saveToFile(File output, XSSFWorkbook wb) throws FileNotFoundException, IOException {
        try( FileOutputStream out = new FileOutputStream(output) ) {
            wb.write(out);
            out.flush();
            out.close();
        }
    }
    
    public static XSSFRow getRow(XSSFSheet sheet, int row) {
        if( sheet.getRow(row) == null ){
            sheet.createRow(row);
        }
        return sheet.getRow(row);
    }
    
}
