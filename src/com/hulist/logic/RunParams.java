/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.logic;

import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import com.hulist.logic.daily.DailyColumnTypes;
import com.hulist.logic.daily.DailyFileTypes;
import com.hulist.util.MonthsPair;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class RunParams {

    private Stage root;

    private RunType runType;
    
    private boolean allYears;
    private int startYear;
    private int endYear;
    private File[] chronologyFile, climateFile;
    private ChronologyFileTypes chronologyFileType;
    private TabsColumnTypes chronologyColumn;
    private ClimateFileTypes climateFileType;
    private ArrayList<MonthsPair> monthsColumns;
    private File[] dailyFile;
    private DailyFileTypes dailyFileType;
    private DailyColumnTypes dailyColumnType;
    private List<String> excludedValues;
    
    private RunParamsPrefs prefs;
    
    public RunParams() {
    }

    /**
     Constructor for monthly data
     */
    public RunParams(RunType runType, boolean allYears, int startYear, int endYear, File[] chronologyFile, File[] climateFile, ChronologyFileTypes chronologyFileType, TabsColumnTypes chronologyType, ClimateFileTypes climateFileType, ArrayList<MonthsPair> monthsColumns) {
        this.runType = runType;
        this.allYears = allYears;
        this.startYear = startYear;
        this.endYear = endYear;
        this.chronologyFile = chronologyFile;
        this.climateFile = climateFile;
        this.chronologyFileType = chronologyFileType;
        this.chronologyColumn = chronologyType;
        this.climateFileType = climateFileType;
        this.monthsColumns = monthsColumns;
    }

    /**
     Constructor for daily data
     */
    public RunParams(RunType runType, boolean allYears, int startYear, int endYear, File[] chronologyFile,
            File[] dailyFile, ChronologyFileTypes chronologyFileType, TabsColumnTypes chronologyType,
            DailyFileTypes dailyFileType, DailyColumnTypes dailyColumnType, List<String> excludedValues) {
        this.runType = runType;
        this.allYears = allYears;
        this.startYear = startYear;
        this.endYear = endYear;
        this.chronologyFile = chronologyFile;
        this.dailyFile = dailyFile;
        this.chronologyFileType = chronologyFileType;
        this.chronologyColumn = chronologyType;
        this.dailyFileType = dailyFileType;
        this.dailyColumnType = dailyColumnType;
        this.excludedValues = excludedValues;
    }

    public boolean isAllYears() {
        return allYears;
    }

    public void setAllYears(boolean allYears) {
        this.allYears = allYears;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public File[] getChronologyFiles() {
        return chronologyFile;
    }

    public void setChronologyFiles(File[] chronologyFile) {
        this.chronologyFile = chronologyFile;
    }

    public File[] getClimateFiles() {
        return climateFile;
    }

    public void setClimateFiles(File[] climateFile) {
        this.climateFile = climateFile;
    }

    public ChronologyFileTypes getChronologyFileType() {
        return chronologyFileType;
    }

    public void setChronologyFileType(ChronologyFileTypes chronologyFileType) {
        this.chronologyFileType = chronologyFileType;
    }

    public TabsColumnTypes getChronologyColumn() {
        return chronologyColumn;
    }

    public void setChronologyColumn(TabsColumnTypes chronologyColumn) {
        this.chronologyColumn = chronologyColumn;
    }

    public ClimateFileTypes getClimateFileType() {
        return climateFileType;
    }

    public void setClimateFileType(ClimateFileTypes climateFileType) {
        this.climateFileType = climateFileType;
    }

    public ArrayList<MonthsPair> getMonthsColumns() {
        return monthsColumns;
    }

    public void setMonthsColumns(ArrayList<MonthsPair> monthsColumns) {
        this.monthsColumns = monthsColumns;
    }

    public RunParamsPrefs getPrefs() {
        return prefs;
    }

    public void setPrefs(RunParamsPrefs prefs) {
        this.prefs = prefs;
    }

    public Stage getRoot() {
        return root;
    }

    public void setRoot(Stage root) {
        this.root = root;
    }

    public RunType getRunType() {
        return runType;
    }

    public DailyFileTypes getDailyFileType() {
        return dailyFileType;
    }

    public DailyColumnTypes getDailyColumnType() {
        return dailyColumnType;
    }

    public File[] getDailyFile() {
        return dailyFile;
    }

    public List<String> getExcludedValues() {
        return excludedValues;
    }

    
}
