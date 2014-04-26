/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui;

import com.hulist.logic.ProcessData;
import com.hulist.logic.WindowParams;
import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import com.hulist.util.FileChooser;
import com.hulist.util.LocaleChangeListener;
import com.hulist.util.LocaleManager;
import com.hulist.util.TextAreaLogHandler;
import com.hulist.util.TextAreaToMonths;
import com.hulist.util.UserPreferences;
import com.hulist.validators.YearValidator;
import com.hulist.validators.YearsRangeValidator;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author alien
 */
public class MainWindow extends javax.swing.JFrame implements LocaleChangeListener, ChangeListener {

    public static final String APP_NAME = "DendroCOR";
    public static final String APP_VERSION = "2.2";

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        secondaryInit();
        setUINames();
        setFromPreferences();

        log.log(Level.FINER, java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("OKNO ZAINICJALIZOWANO."));
    }

    private void secondaryInit() {
        // locale
        LocaleManager.register(this);
        LocaleManager.changeDefaultLocale(Locale.getDefault());

        // app name
        this.setTitle(APP_NAME);

        // combo boxes models
        comboBoxClimateFileType.setModel(new DefaultComboBoxModel<>(ClimateFileTypes.getDisplayNames()));
        comboBoxChronoFileType.setModel(new DefaultComboBoxModel<>(ChronologyFileTypes.getDisplayNames()));
        comboBoxColSelect.setModel(new DefaultComboBoxModel<>(TabsColumnTypes.values()));

        // output text pane
        textPane.setEnabled(true);
        textPane.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // logger
        TextAreaLogHandler.getInstance().setTextArea(textPane);
        globalLog = Logger.getLogger("com.hulist");
        globalLog.addHandler(TextAreaLogHandler.getInstance());
        globalLog.setLevel(Level.ALL);
        log = Logger.getLogger(this.getClass().getCanonicalName());

        // helper tooltip
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        labelTextAreaMonthsHelper.setToolTipText(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WPROWADŹ ZAKRESY MIESIĘCY W FORMACIE"));

        // months change listener
        textAreaMonths.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setPrefs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if( textAreaMonths.getText().trim().isEmpty() ){
                    UserPreferences.getInstance().getPrefs().remove(textAreaMonths.getName());
                } else {
                    setPrefs();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setPrefs();
            }

            private void setPrefs() {
                UserPreferences.getInstance().getPrefs().put(textAreaMonths.getName(), textAreaMonths.getText());
            }
        });

        // logging level slider
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("MIN")));
        labelTable.put(2, new JLabel(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("MAX")));
        sliderLogLvl.setLabelTable(labelTable);
        sliderLogLvl.setPaintLabels(true);
        sliderLogLvl.setPaintTicks(true);
        sliderLogLvl.addChangeListener(this);

        // menu about
        menuAbout.addMenuListener(this.getMenuAboutListener());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelYearStart = new javax.swing.JLabel();
        textFieldYearStart = new javax.swing.JTextField();
        labelYearEnd = new javax.swing.JLabel();
        textFieldYearEnd = new javax.swing.JTextField();
        checkBoxAllYears = new javax.swing.JCheckBox();
        labelChronologyFile = new javax.swing.JLabel();
        buttonSelectChronoFile = new javax.swing.JButton();
        labelLoadedChrono = new javax.swing.JLabel();
        labelLoadedChronoFile = new javax.swing.JLabel();
        labelFileTypeChrono = new javax.swing.JLabel();
        comboBoxChronoFileType = new javax.swing.JComboBox();
        buttonStart = new javax.swing.JButton();
        comboBoxColSelect = new javax.swing.JComboBox();
        labelColumn = new javax.swing.JLabel();
        labelLoadedClimateFile = new javax.swing.JLabel();
        labelLoadedClima = new javax.swing.JLabel();
        buttonSelectClimateFile = new javax.swing.JButton();
        labelClimateFile = new javax.swing.JLabel();
        labelFileTypeClima = new javax.swing.JLabel();
        comboBoxClimateFileType = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaMonths = new javax.swing.JTextArea();
        buttonClearTextArea = new javax.swing.JButton();
        labelTextAreaMonthsHelper = new javax.swing.JLabel();
        buttonResetTextAreaMonths = new javax.swing.JButton();
        labelMonthsRange = new javax.swing.JLabel();
        labelResults = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        labelLoggingDetails = new javax.swing.JLabel();
        sliderLogLvl = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuLanguage = new javax.swing.JMenu();
        menuItemPL = new javax.swing.JMenuItem();
        menuItemEN = new javax.swing.JMenuItem();
        menuAbout = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(100, 100));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle"); // NOI18N
        labelYearStart.setText(bundle.getString("MainWindow.labelYearStart.text")); // NOI18N

        textFieldYearStart.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldYearStartKeyReleased(evt);
            }
        });

        labelYearEnd.setText(bundle.getString("MainWindow.labelYearEnd.text")); // NOI18N

        textFieldYearEnd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldYearEndKeyReleased(evt);
            }
        });

        checkBoxAllYears.setText(bundle.getString("MainWindow.checkBoxAllYears.text")); // NOI18N
        checkBoxAllYears.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBoxAllYearsItemStateChanged(evt);
            }
        });

        labelChronologyFile.setText(bundle.getString("MainWindow.labelChronologyFile.text")); // NOI18N

        buttonSelectChronoFile.setText(bundle.getString("MainWindow.buttonSelectChronoFile.text")); // NOI18N
        buttonSelectChronoFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectChronoFileActionPerformed(evt);
            }
        });

        labelLoadedChrono.setText(bundle.getString("MainWindow.labelLoadedChrono.text")); // NOI18N

        labelLoadedChronoFile.setForeground(new java.awt.Color(255, 0, 0));
        labelLoadedChronoFile.setText(bundle.getString("MainWindow.labelLoadedChronoFile.text")); // NOI18N

        labelFileTypeChrono.setText(bundle.getString("MainWindow.labelFileTypeChrono.text")); // NOI18N

        comboBoxChronoFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxChronoFileTypeActionPerformed(evt);
            }
        });

        buttonStart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        buttonStart.setText(bundle.getString("MainWindow.buttonStart.text")); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        comboBoxColSelect.setEnabled(false);
        comboBoxColSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxColSelectActionPerformed(evt);
            }
        });

        labelColumn.setText(bundle.getString("MainWindow.labelColumn.text")); // NOI18N

        labelLoadedClimateFile.setForeground(new java.awt.Color(255, 0, 0));
        labelLoadedClimateFile.setText(bundle.getString("MainWindow.labelLoadedClimateFile.text")); // NOI18N

        labelLoadedClima.setText(bundle.getString("MainWindow.labelLoadedClima.text")); // NOI18N

        buttonSelectClimateFile.setText(bundle.getString("MainWindow.buttonSelectClimateFile.text")); // NOI18N
        buttonSelectClimateFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectClimateFileActionPerformed(evt);
            }
        });

        labelClimateFile.setText(bundle.getString("MainWindow.labelClimateFile.text")); // NOI18N

        labelFileTypeClima.setText(bundle.getString("MainWindow.labelFileTypeClima.text")); // NOI18N

        comboBoxClimateFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxClimateFileTypeActionPerformed(evt);
            }
        });

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setViewportView(textPane);

        textAreaMonths.setColumns(20);
        textAreaMonths.setRows(5);
        jScrollPane1.setViewportView(textAreaMonths);

        buttonClearTextArea.setText(bundle.getString("MainWindow.buttonClearTextArea.text")); // NOI18N
        buttonClearTextArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearTextAreaActionPerformed(evt);
            }
        });

        labelTextAreaMonthsHelper.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelTextAreaMonthsHelper.setText("?"); // NOI18N

        buttonResetTextAreaMonths.setText(bundle.getString("MainWindow.buttonResetTextAreaMonths.text")); // NOI18N
        buttonResetTextAreaMonths.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetTextAreaMonthsActionPerformed(evt);
            }
        });

        labelMonthsRange.setText(bundle.getString("MainWindow.labelMonthsRange.text")); // NOI18N

        labelResults.setText(bundle.getString("MainWindow.labelResults.text")); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        labelLoggingDetails.setText(bundle.getString("MainWindow.labelLoggingDetails.text")); // NOI18N

        sliderLogLvl.setMajorTickSpacing(1);
        sliderLogLvl.setMaximum(2);
        sliderLogLvl.setValue(1);

        menuLanguage.setText(bundle.getString("MainWindow.menuLanguage.text")); // NOI18N

        menuItemPL.setText("polski"); // NOI18N
        menuItemPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemPLActionPerformed(evt);
            }
        });
        menuLanguage.add(menuItemPL);

        menuItemEN.setText("english"); // NOI18N
        menuItemEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemENActionPerformed(evt);
            }
        });
        menuLanguage.add(menuItemEN);

        jMenuBar1.add(menuLanguage);

        menuAbout.setText(bundle.getString("MainWindow.menuAbout.text")); // NOI18N
        jMenuBar1.add(menuAbout);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelYearStart)
                            .addComponent(labelYearEnd)
                            .addComponent(textFieldYearEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkBoxAllYears)
                            .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelColumn)
                            .addComponent(comboBoxColSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelChronologyFile)
                                    .addComponent(labelLoadedChronoFile)
                                    .addComponent(labelFileTypeChrono)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(labelLoadedChrono, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(comboBoxChronoFileType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonSelectChronoFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(145, 145, 145)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelClimateFile)
                                    .addComponent(buttonSelectClimateFile, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboBoxClimateFileType, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelFileTypeClima)
                                    .addComponent(labelLoadedClimateFile)
                                    .addComponent(labelLoadedClima)))))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelResults)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonClearTextArea)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelLoggingDetails)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderLogLvl, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelMonthsRange)
                            .addComponent(buttonResetTextAreaMonths)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTextAreaMonthsHelper)))))
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelYearStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelYearEnd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldYearEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkBoxAllYears)
                        .addGap(30, 30, 30)
                        .addComponent(buttonStart))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelChronologyFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelectChronoFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelLoadedChrono))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelClimateFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelectClimateFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelLoadedClima))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(labelLoadedChronoFile)
                                        .addGap(18, 18, 18)
                                        .addComponent(labelFileTypeChrono)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboBoxChronoFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(labelLoadedClimateFile)
                                        .addGap(18, 18, 18)
                                        .addComponent(labelFileTypeClima)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboBoxClimateFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(14, 14, 14)
                        .addComponent(labelColumn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxColSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelResults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelMonthsRange)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelTextAreaMonthsHelper)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonClearTextArea)
                        .addComponent(buttonResetTextAreaMonths)
                        .addComponent(labelLoggingDetails))
                    .addComponent(sliderLogLvl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldYearStartKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldYearStartKeyReleased
        if( YearValidator.validate(textFieldYearStart.getText()) || textFieldYearStart.getText().equals("") ){
            UserPreferences.getInstance().getPrefs().put(textFieldYearStart.getName(), textFieldYearStart.getText());
        }
    }//GEN-LAST:event_textFieldYearStartKeyReleased

    private void textFieldYearEndKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldYearEndKeyReleased
        if( YearValidator.validate(textFieldYearEnd.getText()) || textFieldYearEnd.getText().equals("") ){
            UserPreferences.getInstance().getPrefs().put(textFieldYearEnd.getName(), textFieldYearEnd.getText());
        }
    }//GEN-LAST:event_textFieldYearEndKeyReleased

    private void checkBoxAllYearsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkBoxAllYearsItemStateChanged
        boolean isSelected = evt.getStateChange() == ItemEvent.SELECTED;
        if( evt.getItemSelectable() == checkBoxAllYears ){
            checkBoxAllYearsStateChanged(isSelected);
        }
    }//GEN-LAST:event_checkBoxAllYearsItemStateChanged

    private void buttonSelectChronoFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectChronoFileActionPerformed
        FileChooser fc = new FileChooser(FileChooser.Purpose.OPEN);
        fc.setPrefsDir(buttonSelectChronoFile.getName());
        fc.setOpenMultipleFiles(true);
        File[] file = fc.call();

        if( file != null && file.length != 0 ){
            labelLoadedChronoFile.setForeground(Color.getHSBColor(0.33f, 1, 0.65f));
            if( file.length == 1 ){
                labelLoadedChronoFile.setText(file[0].getName());
            } else {
                labelLoadedChronoFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("ZAŁADOWANO PLIKÓW: ") + file.length);
            }
            selectedChronoFile = file;
            for( int i = 0; i < file.length; i++ ) {
                log.log(Level.FINE, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("ZAŁADOWANO PLIK(I) CHRONOLOGII: %S"), file[i].getAbsolutePath()));
            }
        }
    }//GEN-LAST:event_buttonSelectChronoFileActionPerformed

    private void comboBoxColSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxColSelectActionPerformed
        log.log(Level.FINE, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WYBRANO KOLUMNĘ %S"), comboBoxColSelect.getSelectedItem().toString()));
    }//GEN-LAST:event_comboBoxColSelectActionPerformed

    private void comboBoxChronoFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxChronoFileTypeActionPerformed
        JComboBox comboBox = (JComboBox) evt.getSource();
        String choice = comboBox.getSelectedItem().toString();
        log.log(Level.FINE, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WYBRANO RODZAJ PLIKU: %S"), choice));
        if( choice.equals(ChronologyFileTypes.TABS.getDisplayName()) ){
            comboBoxColSelect.setEnabled(true);
        } else {
            comboBoxColSelect.setEnabled(false);
        }
    }//GEN-LAST:event_comboBoxChronoFileTypeActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        if( isDataValid() ){
            boolean allYears = checkBoxAllYears.isSelected();
            int startYear = -1, endYear = -1;
            if( !allYears ){
                try {
                    startYear = Integer.parseInt(textFieldYearStart.getText());
                    endYear = Integer.parseInt(textFieldYearEnd.getText());
                } catch( NumberFormatException e ) {
                }
            }

            String chronologyFileTypeName = (String) comboBoxChronoFileType.getSelectedItem();
            ChronologyFileTypes chronologyFileType = null;
            for( ChronologyFileTypes type : ChronologyFileTypes.values() ) {
                if( type.getDisplayName().equals(chronologyFileTypeName) ){
                    chronologyFileType = type;
                    break;
                }
            }

            TabsColumnTypes tabsColumnType = (TabsColumnTypes) comboBoxColSelect.getSelectedItem();

            String climateFileTypeName = (String) comboBoxClimateFileType.getSelectedItem();
            ClimateFileTypes climateFileType = null;
            for( ClimateFileTypes type : ClimateFileTypes.values() ) {
                if( type.getDisplayName().equals(climateFileTypeName) ){
                    climateFileType = type;
                    break;
                }
            }

            WindowParams wp = new WindowParams(allYears,
                    startYear,
                    endYear,
                    selectedChronoFile,
                    selectedClimateFile,
                    chronologyFileType,
                    tabsColumnType,
                    climateFileType,
                    new TextAreaToMonths(textAreaMonths).getList());
            StringBuilder sb = new StringBuilder();
            sb.append("WindowParams: [")
                    .append(allYears).append(", ")
                    .append(startYear).append(", ")
                    .append(endYear).append(", ")
                    .append(Arrays.toString(selectedChronoFile)).append(", ")
                    .append(Arrays.toString(selectedClimateFile)).append(", ")
                    .append(chronologyFileType).append(", ")
                    .append(tabsColumnType).append(", ")
                    .append(climateFileType).append("]");
            log.log(Level.FINEST, sb.toString());

            new ProcessData(wp).go();
        }
    }//GEN-LAST:event_buttonStartActionPerformed

    private void comboBoxClimateFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxClimateFileTypeActionPerformed
        log.log(Level.FINE, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WYBRANO KOLUMNĘ %S"), comboBoxClimateFileType.getSelectedItem().toString()));
    }//GEN-LAST:event_comboBoxClimateFileTypeActionPerformed

    private void buttonSelectClimateFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectClimateFileActionPerformed
        FileChooser fc = new FileChooser(FileChooser.Purpose.OPEN);
        fc.setPrefsDir(buttonSelectClimateFile.getName());
        fc.setOpenMultipleFiles(true);
        File[] file = fc.call();

        if( file != null && file.length != 0 ){
            labelLoadedClimateFile.setForeground(Color.getHSBColor(0.33f, 1, 0.65f));
            if( file.length == 1 ){
                labelLoadedClimateFile.setText(file[0].getName());
            } else {
                labelLoadedClimateFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("ZAŁADOWANO PLIKÓW: ") + file.length);
            }
            selectedClimateFile = file;
            for( int i = 0; i < file.length; i++ ) {
                log.log(Level.FINE, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("ZAŁADOWANO PLIK(I) KLIMATYCZNY: %S"), file[i].getAbsolutePath()));
            }
        }
    }//GEN-LAST:event_buttonSelectClimateFileActionPerformed

    private void buttonClearTextAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearTextAreaActionPerformed
        this.textPane.setText("");
    }//GEN-LAST:event_buttonClearTextAreaActionPerformed

    private void buttonResetTextAreaMonthsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetTextAreaMonthsActionPerformed
        textAreaMonths.setText(getDefaultMonths());
    }//GEN-LAST:event_buttonResetTextAreaMonthsActionPerformed

    private void menuItemENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemENActionPerformed
        LocaleManager.changeDefaultLocale(new Locale("en"));
    }//GEN-LAST:event_menuItemENActionPerformed

    private void menuItemPLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemPLActionPerformed
        LocaleManager.changeDefaultLocale(new Locale("pl"));
    }//GEN-LAST:event_menuItemPLActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            /*for( javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels() ) {
             if( "Nimbus".equals(info.getName()) ){
             javax.swing.UIManager.setLookAndFeel(info.getClassName());
             break;
             }
             }*/
        } catch( ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex ) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClearTextArea;
    private javax.swing.JButton buttonResetTextAreaMonths;
    private javax.swing.JButton buttonSelectChronoFile;
    private javax.swing.JButton buttonSelectClimateFile;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkBoxAllYears;
    private javax.swing.JComboBox comboBoxChronoFileType;
    private javax.swing.JComboBox comboBoxClimateFileType;
    private javax.swing.JComboBox comboBoxColSelect;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel labelChronologyFile;
    private javax.swing.JLabel labelClimateFile;
    private javax.swing.JLabel labelColumn;
    private javax.swing.JLabel labelFileTypeChrono;
    private javax.swing.JLabel labelFileTypeClima;
    private javax.swing.JLabel labelLoadedChrono;
    private javax.swing.JLabel labelLoadedChronoFile;
    private javax.swing.JLabel labelLoadedClima;
    private javax.swing.JLabel labelLoadedClimateFile;
    private javax.swing.JLabel labelLoggingDetails;
    private javax.swing.JLabel labelMonthsRange;
    private javax.swing.JLabel labelResults;
    private javax.swing.JLabel labelTextAreaMonthsHelper;
    private javax.swing.JLabel labelYearEnd;
    private javax.swing.JLabel labelYearStart;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JMenuItem menuItemEN;
    private javax.swing.JMenuItem menuItemPL;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JSlider sliderLogLvl;
    private javax.swing.JTextArea textAreaMonths;
    private javax.swing.JTextField textFieldYearEnd;
    private javax.swing.JTextField textFieldYearStart;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
    private File[] selectedChronoFile = null;
    private File[] selectedClimateFile = null;
    private Logger log, globalLog;

    private void setUINames() {
        textFieldYearStart.setName("textFieldYearStart");
        textFieldYearEnd.setName("textFieldYearEnd");
        checkBoxAllYears.setName("checkBoxAllYears");
        buttonSelectChronoFile.setName("buttonSelectChronoFile");
        buttonSelectClimateFile.setName("buttonSelectClimateFile");
        textAreaMonths.setName("textAreaMonths");
    }

    private void setFromPreferences() {
        Preferences p = UserPreferences.getInstance().getPrefs();

        textFieldYearStart.setText(p.get(textFieldYearStart.getName(), ""));
        textFieldYearEnd.setText(p.get(textFieldYearEnd.getName(), ""));
        checkBoxAllYears.setSelected(p.getBoolean(checkBoxAllYears.getName(), false));
        textAreaMonths.setText(p.get(textAreaMonths.getName(), getDefaultMonths()));
    }

    /**
     *
     * @param selected true - checkbox selected, false - checkbox unselected
     */
    private void checkBoxAllYearsStateChanged(boolean selected) {
        textFieldYearStart.setEnabled(!selected);
        textFieldYearEnd.setEnabled(!selected);

        UserPreferences.getInstance().getPrefs().putBoolean(checkBoxAllYears.getName(), selected);
    }

    private boolean isDataValid() {
        TextAreaToMonths ta = new TextAreaToMonths(textAreaMonths);
        ta.setIsLoggingOn(false);

        boolean valid = true;

        if( !checkBoxAllYears.isSelected() && !YearsRangeValidator.validate(textFieldYearStart.getText(), textFieldYearEnd.getText()) ){
            log.log(Level.WARNING, java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WPROWADŹ POPRAWNY ZAKRES LAT."));
            valid = false;
        }

        if( selectedChronoFile == null || selectedChronoFile.length == 0 || (selectedChronoFile.length == 1 && (selectedChronoFile[0] == null || !selectedChronoFile[0].isFile())) ){
            valid = false;
            log.log(Level.WARNING, java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WYBIERZ PLIK CHRONOLOGII."));
        } else {
            for( File oneSelectedChronoFile : selectedChronoFile ) {
                if( oneSelectedChronoFile == null ){
                    valid = false;
                    log.log(Level.WARNING, java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("NIEPOPRAWNY PLIK CHRONOLOGII."));
                } else if( !oneSelectedChronoFile.isFile() ){
                    valid = false;
                    log.log(Level.WARNING, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("PLIK CHRONOLOGII %S NIE JEST PLIKIEM"), oneSelectedChronoFile.getName()));
                }
            }
        }

        if( selectedClimateFile == null || selectedClimateFile.length == 0 || (selectedClimateFile.length == 1 && (selectedClimateFile[0] == null || !selectedClimateFile[0].isFile())) ){
            valid = false;
            log.log(Level.WARNING, java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WYBIERZ PLIK KLIMATYCZNY."));
        } else {
            for( File oneSelectedClimateFile : selectedClimateFile ) {
                if( oneSelectedClimateFile == null ){
                    valid = false;
                    log.log(Level.WARNING, java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("NIEPOPRAWNY PLIK KLIMATYCZNY."));
                } else if( !oneSelectedClimateFile.isFile() ){
                    valid = false;
                    log.log(Level.WARNING, String.format(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("PLIK KLIMATYCZNY %S NIE JEST PLIKIEM"), oneSelectedClimateFile.getName()));
                }
            }
        }

        if( ta.getList().isEmpty() ){
            valid = false;
        }

        return valid;
    }

    private String getDefaultMonths() {
        return "1 1\n"
                + "2 2\n"
                + "3 3\n"
                + "4 4\n"
                + "5 5\n"
                + "6 6\n"
                + "7 7\n"
                + "8 8\n"
                + "9 9\n"
                + "10 10\n"
                + "11 11\n"
                + "12 12\n\n"
                + "1 1 1\n"
                + "2 2 1\n"
                + "3 3 1\n"
                + "4 4 1\n"
                + "5 5 1\n"
                + "6 6 1\n"
                + "7 7 1\n"
                + "8 8 1\n"
                + "9 9 1\n"
                + "10 10 1\n"
                + "11 11 1\n"
                + "12 12 1\n\n"
                + "1 12";
    }

    @Override
    public void onLocaleChange() {
        labelChronologyFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelChronologyFile.text"));
        labelClimateFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelClimateFile.text"));
        labelColumn.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelColumn.text"));
        labelFileTypeChrono.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelFileTypeChrono.text"));
        labelFileTypeClima.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelFileTypeClima.text"));
        labelLoadedChrono.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelLoadedChrono.text"));
        labelLoadedChronoFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelLoadedChronoFile.text"));
        labelLoadedClima.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelLoadedClima.text"));
        labelLoadedClimateFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelLoadedChronoFile.text"));
        labelResults.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelResults.text"));
        labelYearEnd.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelYearEnd.text"));
        labelYearStart.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelYearStart.text"));
        labelMonthsRange.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelMonthsRange.text"));
        labelLoggingDetails.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.labelLoggingDetails.text"));

        menuLanguage.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.menuLanguage.text"));
        menuAbout.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.menuAbout.text"));

        checkBoxAllYears.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.checkBoxAllYears.text"));
        buttonStart.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.buttonStart.text"));
        buttonClearTextArea.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.buttonClearTextArea.text"));
        buttonResetTextAreaMonths.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.buttonResetTextAreaMonths.text"));
        buttonSelectChronoFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.buttonSelectChronoFile.text"));
        buttonSelectClimateFile.setText(java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle").getString("MainWindow.buttonSelectClimateFile.text"));

        labelTextAreaMonthsHelper.setToolTipText(java.util.ResourceBundle.getBundle("com/hulist/bundle/MainWindow").getString("WPROWADŹ ZAKRESY MIESIĘCY W FORMACIE"));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider s = (JSlider) e.getSource();
        if( !s.getValueIsAdjusting() ){
            switch( s.getValue() ) {
                case 0:
                    TextAreaLogHandler.getInstance().setLoggingLevel(Level.WARNING);
                    break;
                case 1:
                    TextAreaLogHandler.getInstance().setLoggingLevel(Level.INFO);
                    break;
                case 2:
                    //TextAreaLogHandler.getInstance().setLoggingLevel(Level.FINE);
                    //break;
                    //case 3:
                    TextAreaLogHandler.getInstance().setLoggingLevel(Level.FINER);
                    break;
            }
        }
    }

    private MenuListener getMenuAboutListener() {
        return new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String title1 = "<html><body>"
                            + "<h3>DendroCOR v. "+APP_VERSION+"</h3><br>"
                            + "\u00a9 Aleksander Hulist (2014)<br>"
                            + "aleksander.hulist@gmail.com<br><br>"
                            + "</body></html>";
                                /*"<html><body style='width: 200px; padding: 5px;'>"
                                + "<h1>Do U C Me?</h1>"
                                + "Here is a long string that will wrap.  "
                                + "The effect we want is a multi-line label.";*/
                    JLabel textLabel = new JLabel(title1);
                    JOptionPane.showMessageDialog(null, textLabel);
                });
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };
    }
}
