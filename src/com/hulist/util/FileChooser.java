/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class FileChooser {

    private final Purpose purpose;
    private final Logger log = LoggerFactory.getLogger(FileChooser.class);
    private String prefsDir = null;
    private boolean openMultipleFilesAndFolders = false;
    private boolean addXlsmExt = false;
    private String yesNoDialogMessageOnSave = null;

    public enum Purpose {

        SAVE, OPEN;
    }

    public FileChooser(Purpose purpose) {
        this.purpose = purpose;
    }

    /**
     *
     * @return null if user cancelled action or closed dialog
     */
    public File[] call() {
        ArrayList<File> files = new ArrayList<>();
        if (prefsDir == null) {
            prefsDir = this.getClass().getCanonicalName() + ":" + purpose;
        }

        String lastDirName = UserPreferences.getInstance().getPrefs().get(prefsDir, ".");
        File lastDir = new File(lastDirName);
        final JFileChooser fc = new JFileChooser(lastDir);

        int returnVal = JFileChooser.CANCEL_OPTION;
        try {
            switch (purpose) {
                case SAVE:
                    if (yesNoDialogMessageOnSave != null && !yesNoDialogMessageOnSave.equals("")) {
                        int choice = JOptionPane.showOptionDialog(null, yesNoDialogMessageOnSave, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                        if (choice == JOptionPane.OK_OPTION) {
                            returnVal = fc.showSaveDialog(null);
                        }
                    } else {
                        returnVal = fc.showSaveDialog(null);
                    }
                    break;
                case OPEN:
                    //fc.setMultiSelectionEnabled(openMultipleFilesAndFolders);
                    if (openMultipleFilesAndFolders) {
                        fc.setMultiSelectionEnabled(openMultipleFilesAndFolders);
                        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    }
                    returnVal = fc.showOpenDialog(null);
                    break;
            }
        } catch (HeadlessException e) {
            log.error(Misc.getInternationalized("WYSTĄPIŁ BŁĄD PODCZAS OTWIERANIA OKNA DIALOGOWEGO"));
            log.trace(Misc.stackTraceToString(e));
        }

        File[] filesArray = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFiles().length > 0) {
                File[] filesArr = fc.getSelectedFiles();
                for (File file : filesArr) {
                    if (file.isDirectory()) {
                        files.addAll(getFilesFromDir(file));
                    } else {
                        files.add(file);
                    }
                }
            } else if (fc.getSelectedFile() != null) {
                if (fc.getSelectedFile().isDirectory()) {
                    files.addAll(getFilesFromDir(fc.getSelectedFile()));
                } else {
                    files.add(fc.getSelectedFile());
                }
            }

            filesArray = (File[]) files.toArray(new File[files.size()]);
            if (!openMultipleFilesAndFolders && addXlsmExt && filesArray.length > 0) {
                try {
                    String n = filesArray[0].getCanonicalPath();
                    /*if( n.endsWith(".xls") ){
                     log.warn(Misc.getInternationalized("WYBRANO PLIK Z ROZSZERZENIEM .XLS - ZMIANA NA .XLSX"));
                     }*/
                    if (!n.endsWith(".xlsm")) {
                        String n2 = filesArray[0].getCanonicalPath() + ".xlsm";
                        filesArray[0] = new File(n2);
                    }
                } catch (IOException ex) {
                    log.trace(Misc.stackTraceToString(ex));
                }
            }

            /*if( openMultipleFilesAndFolders ){
             if( fc.getSelectedFile() != null && fc.getSelectedFile().isDirectory() ){
             files = fc.getSelectedFile().listFiles();
             } else {
             files = fc.getSelectedFiles();
             }
             } else {
             files = new File[]{fc.getSelectedFile()};
             if( addXlsxExt ){
             try {
             String n = files[0].getCanonicalPath();
             if( n.endsWith(".xls") ){
             log.warn("Wybrano plik z rozszerzeniem .xls - zmiana na .xlsx");
             }
             if( !n.endsWith(".xlsx") ){
             String n2 = files[0].getCanonicalPath() + ".xlsx";
             files = new File[]{new File(n2)};
             }
             } catch( IOException ex ) {
             log.trace(ex.getMessage());
             }
             }
             }*/
            if (!files.isEmpty()) {
                UserPreferences.getInstance().getPrefs().put(prefsDir, files.get(0).getParent());
            }
        }

        return filesArray;
    }

    private ArrayList<File> getFilesFromDir(File dir) {
        ArrayList<File> files = new ArrayList<>();

        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    files.addAll(getFilesFromDir(file));
                } else {
                    files.add(file);
                }
            }
        }

        return files;
    }

    /**
     * there will be no dialog if message is "" or null
     *
     * @param message
     */
    public void setOnSaveDialogMessage(String message) {
        this.yesNoDialogMessageOnSave = message;
    }

    public String getYesNoDialogMessageOnSave() {
        return yesNoDialogMessageOnSave;
    }

    public boolean isOpenMultipleFiles() {
        return openMultipleFilesAndFolders;
    }

    public void setOpenMultipleFiles(boolean openMultipleFiles) {
        this.openMultipleFilesAndFolders = openMultipleFiles;
    }

    public String getPrefsDir() {
        return prefsDir;
    }

    public void setPrefsDir(String prefsDir) {
        this.prefsDir = prefsDir;
    }

    public boolean isAddXlsxExt() {
        return addXlsmExt;
    }

    public void setAddXlsmExt(boolean addXlsmExt) {
        this.addXlsmExt = addXlsmExt;
    }
}
