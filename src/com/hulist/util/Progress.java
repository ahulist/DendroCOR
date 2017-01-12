/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;

/**
 *
 * @author Aleksander
 */
public class Progress {

    // Files
    private int howManyFiles, filesDone /*1-based*/;
    private CssColor currentFileColor = CssColor.RED;
    private boolean isAnyFileRunning = false;

    // Jobs
    private int howManyJobs, currentJob /*1-based*/;
    private double currentJobProgress /*[0-1]*/;
    private int oldCurrentJobProgressInt = -1, newCurrentJobProgressInt;
    private CssColor currentJobColor = CssColor.GREEN;
    private boolean isAnyJobRunning = false;
    private boolean isCurrentJobRunning = false;

    private final Label label;
    private String jobTxt = "", fileTxt = "";
    private final ProgressBar filePb;
    private final ProgressBar jobPb;
    private final Pane container;

    public Progress(Pane container, ProgressBar jobPb, ProgressBar filePb, Label l) {
        this.container = container;
        this.jobPb = jobPb;
        this.filePb = filePb;
        this.label = l;
        this.resetJobsProgress();
        this.resetFilesProgress();
    }

    /**
     * ile będzie filesów
     *
     * @param howManyJobs
     */
    public void setHowManyFiles(int howManyFiles) {
        this.howManyFiles = howManyFiles;
        fileTxt = Misc.getInternationalized("current file") + (filesDone + 1) + "/" + howManyFiles;
        updateLabelText();
    }

    /**
     * który plik zakończył się wykonywać (1-based)
     */
    public void setFilesDone(int done) {
        if (!isAnyFileRunning) {
            isAnyFileRunning = true;
        }

        filesDone = done;
        setFilesProgressBar((filesDone * 1.0) / howManyFiles);
        fileTxt = Misc.getInternationalized("current file") + (filesDone + 1) + "/" + howManyFiles;
        updateLabelText();

        if (filesDone >= howManyFiles) {
            isAnyFileRunning = false;
        }
    }

    /**
     * zresetowanie wszystkich ustawień paska files
     */
    public final void resetFilesProgress() {
        isAnyFileRunning = false;
        howManyFiles = 1;
        filesDone = 0;
        currentFileColor = CssColor.RED;
        setFilesProgressBarStyle("-fx-accent: " + currentFileColor.name().toLowerCase() + ";");
        setFilesProgressBar(filesDone);

        fileTxt = Misc.getInternationalized("current file") + (filesDone + 1) + "/" + howManyFiles;
        updateLabelText();
    }
    
    /**
     * ustawienie koloru posku postępu files
     *
     * @param s
     */
    private void setFilesProgressBarStyle(String s) {
        Platform.runLater(() -> {
            filePb.setStyle(s);
        });
    }

    /**
     * ustawienie wartości na pasku postępu joba
     *
     * @param p
     */
    private void setFilesProgressBar(double p) {
        Platform.runLater(() -> {
            filePb.setProgress(p);
        });
    }

    public int getHowManyJobs() {
        return howManyJobs;
    }

    /**
     * ile będzie jobów w każdej serii
     *
     * @param howManyJobs
     */
    public void setHowManyJobs(int howManyJobs) {
        this.howManyJobs = howManyJobs;
    }

    public double getCurrentJobProgress() {
        return currentJobProgress;
    }

    /**
     * postęp obliczeń aktualnego joba
     *
     * @param currentProgress [0-1]
     */
    public void setCurrentJobProgress(double currentProgress) {
        this.currentJobProgress = currentProgress;
        newCurrentJobProgressInt = (int) (currentProgress * 100);
        if (!isAnyJobRunning) {
            isAnyJobRunning = true;
            jobSerieBeginning();
        }
        if (!isCurrentJobRunning) {
            isCurrentJobRunning = true;
            currentJobBeginning();
        }
        if (oldCurrentJobProgressInt != newCurrentJobProgressInt) {
            oldCurrentJobProgressInt = newCurrentJobProgressInt;
            setJobProgressBar(currentProgress);
        }
    }

    /**
     * początek obliczeń nowego joba
     */
    private void currentJobBeginning() {
        if (howManyJobs > 1) {
            jobTxt = Misc.getInternationalized("current job") + currentJob + "/" + howManyJobs;
            updateLabelText();
        } else {
            jobTxt = "";
        }
    }

    /**
     * koniec obliczeń aktualnego joba
     */
    public void currentJobFinished() {
        isCurrentJobRunning = false;
        setJobProgressBar(0);
        currentJobColor = currentJobColor.next();
        setJobProgressBarStyle("-fx-accent: " + currentJobColor.name().toLowerCase() + ";");
        currentJob++;

        if (currentJob > howManyJobs) {   // koniec serii jobów
            jobSerieFinished();
        }
    }

    /**
     * zresetowanie wszystkich ustawień paska jobów
     */
    public final void resetJobsProgress() {
        isAnyJobRunning = false;
        howManyJobs = 1;
        currentJob = 1;
        currentJobProgress = 0;
        currentJobColor = CssColor.GREEN;
        setJobProgressBarStyle("-fx-accent: " + currentJobColor.name().toLowerCase() + ";");

        jobTxt = "";
        updateLabelText();
        
        setVisible(isAnyJobRunning);
    }

    /**
     * koniec serii jobów
     */
    private void jobSerieFinished() {
        isAnyJobRunning = false;
        setVisible(false);
        jobTxt = "";
        updateLabelText();
    }

    /**
     * początek serii jobów
     */
    private void jobSerieBeginning() {
        setVisible(true);
    }

    private enum CssColor {

        RED,
        GREEN,
        BLUE,
        PURPLE,
        YELLOW,
        GRAY;

        private static final CssColor[] vals = values();

        public CssColor next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    /**
     * widoczność całego kontenera z progress barami i labelem
     *
     * @param visible
     */
    private void setVisible(boolean visible) {
        container.setVisible(visible);
    }

    /**
     * odświeżenie tekstu w labelu
     */
    private void updateLabelText() {
        Platform.runLater(() -> {
            String fileTmp = fileTxt;
            if (!fileTmp.equals("")) {
                fileTmp += "\n";
            }
            label.setText(fileTmp + jobTxt);
        });
    }

    /**
     * ustawienie wartości na pasku postępu joba
     *
     * @param p
     */
    private void setJobProgressBar(double p) {
        Platform.runLater(() -> {
            jobPb.setProgress(p);
        });
    }

    /**
     * ustawienie koloru posku postępu joba
     *
     * @param s
     */
    private void setJobProgressBarStyle(String s) {
        Platform.runLater(() -> {
            jobPb.setStyle(s);
        });
    }
}
