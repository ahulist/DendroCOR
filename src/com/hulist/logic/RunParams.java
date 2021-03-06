/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.gui2.MainFXMLController;
import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import com.hulist.logic.daily.DailyColumnTypes;
import com.hulist.logic.daily.DailyFileTypes;
import com.hulist.util.MonthsPair;
import com.hulist.util.Progress;
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
    private IColumnTypes tabsChronologyColumn;
    private ClimateFileTypes climateFileType;
    private ArrayList<MonthsPair> monthsColumns;
    private File[] dailyFile;
    private DailyFileTypes dailyFileType;
    private DailyColumnTypes dailyColumnType;
    private List<String> excludedValues;

    private RunSettings runSettings;
    private MainFXMLController mainController;
    private Progress progress;

    public RunParams() {
    }

    /**
     * Constructor for monthly data
     */
    public RunParams(RunType runType, boolean allYears, int startYear, int endYear, 
            File[] chronologyFile, File[] climateFile, ChronologyFileTypes chronologyFileType, 
            IColumnTypes tabsChronologyType, ClimateFileTypes climateFileType, ArrayList<MonthsPair> monthsColumns) {
        this.runType = runType;
        this.allYears = allYears;
        this.startYear = startYear;
        this.endYear = endYear;
        this.chronologyFile = chronologyFile;
        this.climateFile = climateFile;
        this.chronologyFileType = chronologyFileType;
        this.tabsChronologyColumn = tabsChronologyType;
        this.climateFileType = climateFileType;
        this.monthsColumns = monthsColumns;
    }

    /**
     * Constructor for daily data
     */
    public RunParams(RunType runType, boolean allYears, int startYear, int endYear, File[] chronologyFile,
            File[] dailyFile, ChronologyFileTypes chronologyFileType, IColumnTypes tabsChronologyType, 
            DailyFileTypes dailyFileType, DailyColumnTypes dailyColumnType, List<String> excludedValues) {
        this.runType = runType;
        this.allYears = allYears;
        this.startYear = startYear;
        this.endYear = endYear;
        this.chronologyFile = chronologyFile;
        this.dailyFile = dailyFile;
        this.chronologyFileType = chronologyFileType;
        this.tabsChronologyColumn = tabsChronologyType;
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

    public IColumnTypes getChronologyColumn() {
        return tabsChronologyColumn;
    }

    public void setTabsChronologyColumn(TabsColumnTypes chronologyColumn) {
        this.tabsChronologyColumn = chronologyColumn;
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

    public RunSettings getSettings() {
        return runSettings;
    }

    public void setSettings(RunSettings prefs) {
        this.runSettings = prefs;
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

    public void setMainController(MainFXMLController mc) {
        this.mainController = mc;
    }

    public MainFXMLController getMainController() {
        return mainController;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
