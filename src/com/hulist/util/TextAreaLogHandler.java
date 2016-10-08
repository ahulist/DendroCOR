/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import com.hulist.gui.MainWindow;
import java.awt.Color;
import java.awt.Window;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TextAreaLogHandler extends Handler {

    private static TextAreaLogHandler INSTANCE;
    private final LogsSaver saver = LogsSaver.getInstance();
    private static Logger log = LoggerFactory.getLogger(TextAreaLogHandler.class);
    private JTextPane dest;
    private JXCollapsiblePane jXCollapsiblePane;
    private StyledDocument doc;

    private TextAreaLogHandler() {
    }

    public static TextAreaLogHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextAreaLogHandler();
        }
        return INSTANCE;
    }

    public void setTextArea(JTextPane dest) {
        this.dest = dest;
        this.doc = dest.getStyledDocument();
    }

    public void setCollapsable(JXCollapsiblePane jXCollapsiblePane) {
        this.jXCollapsiblePane = jXCollapsiblePane;
    }

    @Override
    public void publish(LogRecord record) {
        if (LogsSaver.getInstance().isIsLoggingOn()) {
//            try {
                SimpleAttributeSet as = new SimpleAttributeSet();

                // SEVERE
                /*if (record.getLevel().equals(Level.SEVERE)) {
                    if (jXCollapsiblePane != null) {
                        jXCollapsiblePane.setCollapsed(false);
                        ((Window) SwingUtilities.getRoot(jXCollapsiblePane)).pack();
                    }
                    StyleConstants.setForeground(as, Color.red);
                    StyleConstants.setBold(as, true);
                    doc.insertString(doc.getLength(), record.getMessage() + "\n", as);

                    // WARNING
                } else if (record.getLevel().equals(Level.WARNING) && record.getLevel().intValue() >= level.intValue()) {
                    if (jXCollapsiblePane != null) {
                        jXCollapsiblePane.setCollapsed(false);
                        ((Window) SwingUtilities.getRoot(jXCollapsiblePane)).pack();
                    }
                    StyleConstants.setForeground(as, Color.red);
                    doc.insertString(doc.getLength(), record.getMessage() + "\n", as);

                    // INFO
                } else if (record.getLevel().equals(Level.INFO) && record.getLevel().intValue() >= level.intValue()) {
                    StyleConstants.setForeground(as, Color.BLACK);
                    doc.insertString(doc.getLength(), record.getMessage() + "\n", as);

                    // FINE
                } else if (record.getLevel().equals(Level.FINE) && record.getLevel().intValue() >= level.intValue()) {
                    StyleConstants.setForeground(as, Color.getHSBColor(0.59f, 1, 0.74f));
                    doc.insertString(doc.getLength(), record.getMessage() + "\n", as);

                    // FINER
                } else if (record.getLevel().equals(Level.FINER) && record.getLevel().intValue() >= level.intValue()) {
                    StyleConstants.setForeground(as, Color.getHSBColor(0.69f, 1, 0.39f));
                    doc.insertString(doc.getLength(), record.getMessage() + "\n", as);

                    // FINEST
                } else if (record.getLevel().equals(Level.FINEST)) {
                    StyleConstants.setForeground(as, Color.getHSBColor(0.99f, 1, 0.44f));
                }*/

                saver.log(record.getLevel() + ": " + record.getMessage());

            /*} catch (BadLocationException ex) {
                System.out.println("Fatal logging error");
                ex.printStackTrace();
                //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }*/
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    /*public void setLoggingLevel(Level l) {
        log.log(Level.FINE, String.format(java.util.ResourceBundle.getBundle(MainWindow.BUNDLE).getString("ZMIENIONO POZIOM LOGOWANINA NA %S"), l));
        this.level = l;
    }

    public Level getLoggingLevel() {
        return this.level;
    }*/

}
