/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class FileChooser {

    private final Purpose purpose;
    private Logger log;
    private String prefsDir = null;
    private boolean openMultipleFiles = false;
    private boolean addXlsxExt = false;
    private String yesNoDialogMessageOnSave = null;

    public enum Purpose {

        SAVE, OPEN;
    }

    public FileChooser(Purpose purpose) {
        this.purpose = purpose;
        this.log = Logger.getLogger(this.getClass().getName());
        log.setLevel(Level.ALL);
    }

    /**
     *
     * @return null if user cancelled action or closed dialog
     */
    public File[] call() {
        File[] file = null;
        if( prefsDir == null ){
            prefsDir = this.getClass().getName() + ":" + purpose;
        }

        String lastDirName = UserPreferences.getInstance().getPrefs().get(prefsDir, ".");
        File lastDir = new File(lastDirName);
        final JFileChooser fc = new JFileChooser(lastDir);

        int returnVal = JFileChooser.CANCEL_OPTION;
        try {
            switch( purpose ) {
                case SAVE:
                    if( yesNoDialogMessageOnSave != null && !yesNoDialogMessageOnSave.equals("") ){
                        int choice = JOptionPane.showOptionDialog(null, yesNoDialogMessageOnSave, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                        if( choice == JOptionPane.OK_OPTION ){
                            returnVal = fc.showSaveDialog(null);
                        }
                    } else {
                        returnVal = fc.showSaveDialog(null);
                    }
                    break;
                case OPEN:
                    fc.setMultiSelectionEnabled(openMultipleFiles);
                    returnVal = fc.showOpenDialog(null);
                    break;
            }
        } catch( HeadlessException e ) {
            log.log(Level.SEVERE, "Wystąpił błąd podczas otwierania okna dialogowego");
            log.log(Level.FINEST, e.getMessage());
        }

        if( returnVal == JFileChooser.APPROVE_OPTION ){
            if( openMultipleFiles ){
                file = fc.getSelectedFiles();
            } else {
                file = new File[]{fc.getSelectedFile()};
                if( addXlsxExt ){
                    try {
                        String n = file[0].getCanonicalPath();
                        if( n.endsWith(".xls") ){
                            log.log(Level.WARNING, "Wybrano plik z rozszerzeniem .xls - zmiana na .xlsx");
                        }
                        if( !n.endsWith(".xlsx") ){
                            String n2 = file[0].getCanonicalPath() + ".xlsx";
                            file = new File[]{new File(n2)};
                        }
                    } catch( IOException ex ) {
                        log.log(Level.FINEST, ex.getMessage());
                    }
                }
            }

            if( file != null && file.length != 0 ){
                UserPreferences.getInstance().getPrefs().put(prefsDir, file[0].getParent());
            }
        }

        return file;
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
        return openMultipleFiles;
    }

    public void setOpenMultipleFiles(boolean openMultipleFiles) {
        this.openMultipleFiles = openMultipleFiles;
    }

    public String getPrefsDir() {
        return prefsDir;
    }

    public void setPrefsDir(String prefsDir) {
        this.prefsDir = prefsDir;
    }

    public boolean isAddXlsxExt() {
        return addXlsxExt;
    }

    public void setAddXlsxExt(boolean addXlsxExt) {
        this.addXlsxExt = addXlsxExt;
    }
}
