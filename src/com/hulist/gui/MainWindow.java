package com.hulist.gui;

/*
 TODO
 - przesunać "korelacje" z PreferencesJFrame do menu
 - kolorem zaznaczone komórki istotne dla zadanego alfa
 - 3 set/restore months ranges
 - logi: same ostrzeżenia/full logi
 - korelacja: linie poziome na wykresach -> istotność statystyczna
 (jeśli dane są dodatnie i ujemne, to linie po obu stronach)
 - wartość istotności stat. w logach szczegółowych
 - wielowątkowość
 - zapamiętać ustawienia użytkownika w WindowParams przed uruchomieniem
 (tak żeby można było mieszać w ustawieniach po uruchomieniu obliczeń)
 */
import com.hulist.logic.ProcessData;
import com.hulist.logic.RunParams;
import com.hulist.logic.RunType;
import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import com.hulist.logic.daily.DailyColumnTypes;
import com.hulist.logic.daily.DailyFileTypes;
import com.hulist.util.FileWrap;
import com.hulist.util.LocaleChangeListener;
import com.hulist.util.LocaleManager;
import com.hulist.util.TextAreaLogHandler;
import com.hulist.util.TextAreaToMonths;
import com.hulist.util.UserPreferences;
import com.hulist.validators.YearValidator;
import com.hulist.validators.YearsRangeValidator;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;
import net.iharder.dnd.FileDrop;
import org.jdesktop.swingx.JXCollapsiblePane;

/**
 *
 * @author alien
 */
public class MainWindow extends JFrame/*Background*/ implements LocaleChangeListener {

    public static final String APP_NAME = "DendroCORR";
    public static final String APP_VERSION = "2.9.3";
    public static final String BUNDLE = "com/hulist/bundle/Bundle";
    private static final int YEAR = 2016;//Calendar.getInstance().get(Calendar.YEAR);

    // dropdown panel
    private final DropdownContentsPanel dropdownPanel = new DropdownContentsPanel(this);
    private final JXCollapsiblePane jXCollapsiblePane = new org.jdesktop.swingx.JXCollapsiblePane();
    private final static PreferencesJFrame PREFERENCES_JFRAME = new PreferencesJFrame();

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        super();
        initComponents();
        secondaryInit();
        setUINames();
        setFromPreferences();

        //log.log(Level.FINER, ResourceBundle.getBundle(BUNDLE).getString("OKNO ZAINICJALIZOWANO."));
    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    private void secondaryInit() {
        // title bar icon
        SwingUtilities.invokeLater(() -> {
            try {
                final List<Image> icons1 = new ArrayList<>();
                icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/32.png")));
                icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/48.png")));
                icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/64.png")));
                icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/96.png")));
                icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/128.png")));
                icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/450.png")));
                setIconImages(icons1);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // app name
        this.setTitle(APP_NAME);

        // combo boxes models
        comboBoxClimateFileType.setModel(new DefaultComboBoxModel<>(ClimateFileTypes.getDisplayNames()));
        comboBoxChronoFileType.setModel(new DefaultComboBoxModel<>(ChronologyFileTypes.getDisplayNames()));
        comboBoxColSelect.setModel(new DefaultComboBoxModel<>(TabsColumnTypes.values()));
        comboBoxDailyFileType.setModel(new DefaultComboBoxModel<>(DailyFileTypes.values()));
        comboBoxColDailySelect.setModel(new DefaultComboBoxModel<>(DailyColumnTypes.getDisplayNames()));

        // output text pane
        dropdownPanel.getTextPane().setEnabled(true);
        dropdownPanel.getTextPane().setEditable(false);
        DefaultCaret caret = (DefaultCaret) dropdownPanel.getTextPane().getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // logger
        TextAreaLogHandler.getInstance().setTextArea(dropdownPanel.getTextPane());
        TextAreaLogHandler.getInstance().setCollapsable(jXCollapsiblePane);
        globalLog = Logger.getLogger("com.hulist");
        globalLog.addHandler(TextAreaLogHandler.getInstance());
        globalLog.setLevel(Level.ALL);
        log = Logger.getLogger(this.getClass().getCanonicalName());

        // helper tooltip
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        dropdownPanel.getLabelTextAreaMonthsHelper().setToolTipText(ResourceBundle.getBundle(BUNDLE).getString("WPROWADŹ ZAKRESY MIESIĘCY W FORMACIE"));

        // months change listener
        dropdownPanel.getTextAreaMonths().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setPrefs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (dropdownPanel.getTextAreaMonths().getText().trim().isEmpty()) {
                    UserPreferences.getInstance().getPrefs().remove(dropdownPanel.getTextAreaMonths().getName());
                } else {
                    setPrefs();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setPrefs();
            }

            private void setPrefs() {
                UserPreferences.getInstance().getPrefs().put(dropdownPanel.getTextAreaMonths().getName(), dropdownPanel.getTextAreaMonths().getText());
            }
        });

        // daily excluded list
        textFieldDailyExcluded.setText(UserPreferences.getInstance().getPrefs().get("textFieldDailyExcluded", ""));

        // collapsible pane
        jXCollapsiblePane.setCollapsed(true);
        jXCollapsiblePane.setAnimated(false);
        collapsibleParent.add(jXCollapsiblePane, BorderLayout.NORTH);
        jXCollapsiblePane.add(dropdownPanel);

        // d'n'd list
        setDnDLists();

        // locale
        LocaleManager.register(this);
        LocaleManager.changeDefaultLocale(Locale.ENGLISH);

        disableSettings();

        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        collapsibleParent = new javax.swing.JPanel();
        panelDendro = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listDendroFiles = new javax.swing.JList();
        labelFileTypeChrono = new javax.swing.JLabel();
        comboBoxChronoFileType = new javax.swing.JComboBox();
        labelColumn = new javax.swing.JLabel();
        comboBoxColSelect = new javax.swing.JComboBox();
        labelChronologyFile = new javax.swing.JLabel();
        labelListDendroHelper = new javax.swing.JLabel();
        panelYears = new javax.swing.JPanel();
        labelYearStart = new javax.swing.JLabel();
        textFieldYearStart = new javax.swing.JTextField();
        labelYearEnd = new javax.swing.JLabel();
        textFieldYearEnd = new javax.swing.JTextField();
        checkBoxAllYears = new javax.swing.JCheckBox();
        panelClimate = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listClimateFiles = new javax.swing.JList();
        labelClimateFile = new javax.swing.JLabel();
        labelFileTypeClima = new javax.swing.JLabel();
        comboBoxClimateFileType = new javax.swing.JComboBox();
        labelListClimateHelper = new javax.swing.JLabel();
        buttonStart = new javax.swing.JButton();
        buttonMore = new javax.swing.JButton();
        panelDaily = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listDailyFiles = new javax.swing.JList();
        labelDailyFile = new javax.swing.JLabel();
        labelFileTypeDaily = new javax.swing.JLabel();
        comboBoxDailyFileType = new javax.swing.JComboBox();
        labelColumnDaily = new javax.swing.JLabel();
        comboBoxColDailySelect = new javax.swing.JComboBox();
        labelDailyExcluded = new javax.swing.JLabel();
        textFieldDailyExcluded = new javax.swing.JTextField();
        radioMonthly = new javax.swing.JRadioButton();
        radioDaily = new javax.swing.JRadioButton();
        menuBar = new javax.swing.JMenuBar();
        menuLogs = new javax.swing.JMenu();
        menuLogsDetails = new javax.swing.JMenu();
        menuItemLowDetails = new javax.swing.JCheckBoxMenuItem();
        menuItemMidDetails = new javax.swing.JCheckBoxMenuItem();
        menuItemHighDetails = new javax.swing.JCheckBoxMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        menuSettings = new javax.swing.JMenu();
        menuLanguage = new javax.swing.JMenu();
        menuItemLangPL = new javax.swing.JMenuItem();
        menuItemLangEN = new javax.swing.JMenuItem();
        menuItemPreferences = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(100, 100));
        setResizable(false);

        collapsibleParent.setLayout(new java.awt.BorderLayout());

        panelDendro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        listDendroFiles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesKeyPressedHandler(evt);
            }
        });
        jScrollPane1.setViewportView(listDendroFiles);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/hulist/bundle/Bundle"); // NOI18N
        labelFileTypeChrono.setText(bundle.getString("MainWindow.labelFileTypeChrono.text")); // NOI18N

        comboBoxChronoFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxChronoFileTypeActionPerformed(evt);
            }
        });

        labelColumn.setText(bundle.getString("MainWindow.labelColumn.text")); // NOI18N

        comboBoxColSelect.setEnabled(false);
        comboBoxColSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxColSelectActionPerformed(evt);
            }
        });

        labelChronologyFile.setText(bundle.getString("MainWindow.labelChronologyFile.text")); // NOI18N

        labelListDendroHelper.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelListDendroHelper.setText("?"); // NOI18N

        javax.swing.GroupLayout panelDendroLayout = new javax.swing.GroupLayout(panelDendro);
        panelDendro.setLayout(panelDendroLayout);
        panelDendroLayout.setHorizontalGroup(
            panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDendroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addGroup(panelDendroLayout.createSequentialGroup()
                        .addGroup(panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelFileTypeChrono)
                            .addComponent(labelColumn))
                        .addGap(18, 18, 18)
                        .addGroup(panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboBoxColSelect, 0, 100, Short.MAX_VALUE)
                            .addComponent(comboBoxChronoFileType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelDendroLayout.createSequentialGroup()
                        .addComponent(labelChronologyFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelListDendroHelper)))
                .addContainerGap())
        );
        panelDendroLayout.setVerticalGroup(
            panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDendroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelChronologyFile)
                    .addComponent(labelListDendroHelper))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFileTypeChrono)
                    .addComponent(comboBoxChronoFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDendroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelColumn)
                    .addComponent(comboBoxColSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelYears.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        javax.swing.GroupLayout panelYearsLayout = new javax.swing.GroupLayout(panelYears);
        panelYears.setLayout(panelYearsLayout);
        panelYearsLayout.setHorizontalGroup(
            panelYearsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelYearsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelYearsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelYearStart)
                    .addComponent(labelYearEnd)
                    .addComponent(checkBoxAllYears)
                    .addGroup(panelYearsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(textFieldYearEnd, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(textFieldYearStart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                .addGap(58, 58, 58))
        );
        panelYearsLayout.setVerticalGroup(
            panelYearsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelYearsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelYearStart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelYearEnd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldYearEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(checkBoxAllYears)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelClimate.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        listClimateFiles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesKeyPressedHandler(evt);
            }
        });
        jScrollPane2.setViewportView(listClimateFiles);

        labelClimateFile.setText(bundle.getString("MainWindow.labelClimateFile.text")); // NOI18N

        labelFileTypeClima.setText(bundle.getString("MainWindow.labelFileTypeClima.text")); // NOI18N

        comboBoxClimateFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxClimateFileTypeActionPerformed(evt);
            }
        });

        labelListClimateHelper.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelListClimateHelper.setText("?"); // NOI18N

        javax.swing.GroupLayout panelClimateLayout = new javax.swing.GroupLayout(panelClimate);
        panelClimate.setLayout(panelClimateLayout);
        panelClimateLayout.setHorizontalGroup(
            panelClimateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClimateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClimateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addGroup(panelClimateLayout.createSequentialGroup()
                        .addComponent(labelFileTypeClima)
                        .addGap(18, 18, 18)
                        .addComponent(comboBoxClimateFileType, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelClimateLayout.createSequentialGroup()
                        .addComponent(labelClimateFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelListClimateHelper)))
                .addContainerGap())
        );
        panelClimateLayout.setVerticalGroup(
            panelClimateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClimateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClimateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelClimateFile)
                    .addComponent(labelListClimateHelper))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(panelClimateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFileTypeClima)
                    .addComponent(comboBoxClimateFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonStart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        buttonStart.setText(bundle.getString("MainWindow.buttonStart.text")); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        buttonMore.setText(bundle.getString("MainWindow.buttonMore.text")); // NOI18N
        buttonMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoreActionPerformed(evt);
            }
        });

        panelDaily.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelDaily.setPreferredSize(new java.awt.Dimension(290, 244));

        listDailyFiles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesKeyPressedHandler(evt);
            }
        });
        jScrollPane3.setViewportView(listDailyFiles);

        labelDailyFile.setText(bundle.getString("MainWindow.labelDailyFile.text")); // NOI18N

        labelFileTypeDaily.setText(bundle.getString("MainWindow.labelFileTypeDaily.text")); // NOI18N

        comboBoxDailyFileType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDailyFileTypeActionPerformed(evt);
            }
        });

        labelColumnDaily.setText(bundle.getString("MainWindow.labelColumnDaily.text")); // NOI18N

        comboBoxColDailySelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxColDailySelectActionPerformed(evt);
            }
        });

        labelDailyExcluded.setText(bundle.getString("MainWindow.labelDailyExcluded.text")); // NOI18N
        labelDailyExcluded.setToolTipText(bundle.getString("MainWindow.labelDailyExcluded.toolTipText")); // NOI18N

        textFieldDailyExcluded.setText(bundle.getString("MainWindow.textFieldDailyExcluded.text")); // NOI18N
        textFieldDailyExcluded.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldDailyExcludedFocusLost(evt);
            }
        });

        javax.swing.GroupLayout panelDailyLayout = new javax.swing.GroupLayout(panelDaily);
        panelDaily.setLayout(panelDailyLayout);
        panelDailyLayout.setHorizontalGroup(
            panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDailyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addGroup(panelDailyLayout.createSequentialGroup()
                        .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDailyFile)
                            .addGroup(panelDailyLayout.createSequentialGroup()
                                .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelFileTypeDaily)
                                    .addComponent(labelColumnDaily)
                                    .addComponent(labelDailyExcluded))
                                .addGap(28, 28, 28)
                                .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(textFieldDailyExcluded)
                                    .addComponent(comboBoxColDailySelect, 0, 120, Short.MAX_VALUE)
                                    .addComponent(comboBoxDailyFileType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelDailyLayout.setVerticalGroup(
            panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDailyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDailyFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFileTypeDaily)
                    .addComponent(comboBoxDailyFileType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelColumnDaily)
                    .addComponent(comboBoxColDailySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDailyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDailyExcluded)
                    .addComponent(textFieldDailyExcluded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        buttonGroup1.add(radioMonthly);
        radioMonthly.setSelected(true);
        radioMonthly.setText(bundle.getString("MainWindow.radioMonthly.text")); // NOI18N

        buttonGroup1.add(radioDaily);
        radioDaily.setText(bundle.getString("MainWindow.radioDaily.text")); // NOI18N

        menuLogs.setText(bundle.getString("MainWindow.menuLogs.text")); // NOI18N

        menuLogsDetails.setText(bundle.getString("MainWindow.menuLogsDetails.text")); // NOI18N

        menuItemLowDetails.setText(bundle.getString("MainWindow.menuItemLowDetails.text")); // NOI18N
        menuItemLowDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLowDetailsActionPerformed(evt);
            }
        });
        menuLogsDetails.add(menuItemLowDetails);

        menuItemMidDetails.setText(bundle.getString("MainWindow.menuItemMidDetails.text")); // NOI18N
        menuItemMidDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemMidDetailsActionPerformed(evt);
            }
        });
        menuLogsDetails.add(menuItemMidDetails);

        menuItemHighDetails.setSelected(true);
        menuItemHighDetails.setText(bundle.getString("MainWindow.menuItemHighDetails.text")); // NOI18N
        menuItemHighDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHighDetailsActionPerformed(evt);
            }
        });
        menuLogsDetails.add(menuItemHighDetails);

        jMenuItem2.setText(bundle.getString("MainWindow.jMenuItem2.text")); // NOI18N
        menuLogsDetails.add(jMenuItem2);

        menuLogs.add(menuLogsDetails);

        menuBar.add(menuLogs);

        menuSettings.setText(bundle.getString("MainWindow.menuSettings.text")); // NOI18N

        menuLanguage.setText(bundle.getString("MainWindow.menuLanguage.text")); // NOI18N

        menuItemLangPL.setText(bundle.getString("MainWindow.menuItemLangPL.text")); // NOI18N
        menuItemLangPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemPLActionPerformed(evt);
            }
        });
        menuLanguage.add(menuItemLangPL);

        menuItemLangEN.setText(bundle.getString("MainWindow.menuItemLangEN.text")); // NOI18N
        menuItemLangEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemENActionPerformed(evt);
            }
        });
        menuLanguage.add(menuItemLangEN);

        menuSettings.add(menuLanguage);

        menuItemPreferences.setText(bundle.getString("MainWindow.menuItemPreferences.text")); // NOI18N
        menuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemPreferencesActionPerformed(evt);
            }
        });
        menuSettings.add(menuItemPreferences);

        menuBar.add(menuSettings);

        menuHelp.setText(bundle.getString("MainWindow.menuHelp.text")); // NOI18N
        menuHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuHelpMouseClicked(evt);
            }
        });

        menuAbout.setText(bundle.getString("MainWindow.menuAbout.text")); // NOI18N
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(collapsibleParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(panelYears, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonMore, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(radioMonthly)
                                    .addComponent(radioDaily))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDendro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelClimate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelDaily, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelYears, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioMonthly)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioDaily)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonMore)
                            .addComponent(buttonStart))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelDendro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelClimate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDaily, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(collapsibleParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        collapsibleParent.setPreferredSize(collapsibleParent.getPreferredSize());
        pack();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldYearStartKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldYearStartKeyReleased
        if (YearValidator.validate(textFieldYearStart.getText()) || textFieldYearStart.getText().equals("")) {
            UserPreferences.getInstance().getPrefs().put(textFieldYearStart.getName(), textFieldYearStart.getText());
        }
    }//GEN-LAST:event_textFieldYearStartKeyReleased

    private void textFieldYearEndKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldYearEndKeyReleased
        if (YearValidator.validate(textFieldYearEnd.getText()) || textFieldYearEnd.getText().equals("")) {
            UserPreferences.getInstance().getPrefs().put(textFieldYearEnd.getName(), textFieldYearEnd.getText());
        }
    }//GEN-LAST:event_textFieldYearEndKeyReleased

    private void checkBoxAllYearsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkBoxAllYearsItemStateChanged
        boolean isSelected = evt.getStateChange() == ItemEvent.SELECTED;
        if (evt.getItemSelectable() == checkBoxAllYears) {
            checkBoxAllYearsStateChanged(isSelected);
        }
    }//GEN-LAST:event_checkBoxAllYearsItemStateChanged

    private void comboBoxColSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxColSelectActionPerformed
        log.log(Level.FINE, String.format(ResourceBundle.getBundle(BUNDLE).getString("WYBRANO KOLUMNĘ %S"), comboBoxColSelect.getSelectedItem().toString()));
    }//GEN-LAST:event_comboBoxColSelectActionPerformed

    private void comboBoxChronoFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxChronoFileTypeActionPerformed
        JComboBox comboBox = (JComboBox) evt.getSource();
        String choice = comboBox.getSelectedItem().toString();
        log.log(Level.FINE, String.format(ResourceBundle.getBundle(BUNDLE).getString("WYBRANO RODZAJ PLIKU: %S"), choice));
        if (choice.equals(ChronologyFileTypes.TABS.getDisplayName())) {
            comboBoxColSelect.setEnabled(true);
        } else {
            comboBoxColSelect.setEnabled(false);
        }
    }//GEN-LAST:event_comboBoxChronoFileTypeActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        // for logging testing
        /*log.log(Level.SEVERE, "severe");
         log.log(Level.WARNING, "warning");
         log.log(Level.INFO, "info");
         log.log(Level.FINE, "fine");
         log.log(Level.FINER, "finer");*/

        if (radioMonthly.isSelected()) {
            selectedChronoFile = new File[listDendroFiles.getModel().getSize()];
            for (int i = 0; i < listDendroFiles.getModel().getSize(); i++) {
                selectedChronoFile[i] = (File) listDendroFiles.getModel().getElementAt(i);
            }
            selectedClimateFile = new File[listClimateFiles.getModel().getSize()];
            for (int i = 0; i < listClimateFiles.getModel().getSize(); i++) {
                selectedClimateFile[i] = (File) listClimateFiles.getModel().getElementAt(i);
            }

            if (isDataValid()) {
                boolean allYears = checkBoxAllYears.isSelected();
                int startYear = -1, endYear = -1;
                if (!allYears) {
                    try {
                        startYear = Integer.parseInt(textFieldYearStart.getText());
                        endYear = Integer.parseInt(textFieldYearEnd.getText());
                    } catch (NumberFormatException e) {
                    }
                }

                String chronologyFileTypeName = (String) comboBoxChronoFileType.getSelectedItem();
                ChronologyFileTypes chronologyFileType = null;
                for (ChronologyFileTypes type : ChronologyFileTypes.values()) {
                    if (type.getDisplayName().equals(chronologyFileTypeName)) {
                        chronologyFileType = type;
                        break;
                    }
                }

                TabsColumnTypes tabsColumnType = (TabsColumnTypes) comboBoxColSelect.getSelectedItem();

                String climateFileTypeName = (String) comboBoxClimateFileType.getSelectedItem();
                ClimateFileTypes climateFileType = null;
                for (ClimateFileTypes type : ClimateFileTypes.values()) {
                    if (type.getDisplayName().equals(climateFileTypeName)) {
                        climateFileType = type;
                        break;
                    }
                }

                RunParams wp = new RunParams(RunType.MONTHLY,
                        allYears,
                        startYear,
                        endYear,
                        selectedChronoFile,
                        selectedClimateFile,
                        chronologyFileType,
                        tabsColumnType,
                        climateFileType,
                        new TextAreaToMonths(dropdownPanel.getTextAreaMonths()).getList());
                wp.setPreferencesFrame(PREFERENCES_JFRAME);
                wp.setMainWindow(this);
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
        } else if (radioDaily.isSelected()) {
            selectedChronoFile = new File[listDendroFiles.getModel().getSize()];
            for (int i = 0; i < listDendroFiles.getModel().getSize(); i++) {
                selectedChronoFile[i] = (File) listDendroFiles.getModel().getElementAt(i);
            }
            selectedDailyFile = new File[listDailyFiles.getModel().getSize()];
            for (int i = 0; i < listDailyFiles.getModel().getSize(); i++) {
                selectedDailyFile[i] = (File) listDailyFiles.getModel().getElementAt(i);
            }
            
            boolean allYears = checkBoxAllYears.isSelected();
            int startYear = -1, endYear = -1;
            if (!allYears) {
                try {
                    startYear = Integer.parseInt(textFieldYearStart.getText());
                    endYear = Integer.parseInt(textFieldYearEnd.getText());
                } catch (NumberFormatException e) {
                }
            }

            String chronologyFileTypeName = (String) comboBoxChronoFileType.getSelectedItem();
            ChronologyFileTypes chronologyFileType = null;
            for (ChronologyFileTypes type : ChronologyFileTypes.values()) {
                if (type.getDisplayName().equals(chronologyFileTypeName)) {
                    chronologyFileType = type;
                    break;
                }
            }

            TabsColumnTypes tabsColumnType = (TabsColumnTypes) comboBoxColSelect.getSelectedItem();
            DailyFileTypes dft = (DailyFileTypes) comboBoxDailyFileType.getSelectedItem();
            
            String dailyColumnTypeName = (String) comboBoxColDailySelect.getSelectedItem();
            DailyColumnTypes dct = null;
            for (DailyColumnTypes type : DailyColumnTypes.values()) {
                if (type.getDisplayName().equals(dailyColumnTypeName)) {
                    dct = type;
                    break;
                }
            }
            
            RunParams wp = new RunParams(RunType.DAILY, allYears, startYear, endYear, selectedChronoFile, 
                    selectedDailyFile, chronologyFileType, tabsColumnType,
                    dft, dct, textFieldDailyExcluded.getText().split("\\s*,\\s*"));
            
            wp.setPreferencesFrame(PREFERENCES_JFRAME);
            wp.setMainWindow(this);
            StringBuilder sb = new StringBuilder();
            sb.append("WindowParams: [")
                    .append(allYears).append(", ")
                    .append(startYear).append(", ")
                    .append(endYear).append(", ")
                    .append(Arrays.toString(selectedChronoFile)).append(", ")
                    .append(Arrays.toString(selectedDailyFile)).append(", ")
                    .append(chronologyFileType).append(", ")
                    .append(tabsColumnType).append(", ")
                    .append(dft).append(", ")
                    .append(dct).append(", ")
                    .append(textFieldDailyExcluded.getText()).append("]");
            log.log(Level.FINEST, sb.toString());

            new ProcessData(wp).go();
        }
    }//GEN-LAST:event_buttonStartActionPerformed

    private void comboBoxClimateFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxClimateFileTypeActionPerformed
        log.log(Level.FINE, String.format(ResourceBundle.getBundle(BUNDLE).getString("WYBRANO KOLUMNĘ %S"), comboBoxClimateFileType.getSelectedItem().toString()));
    }//GEN-LAST:event_comboBoxClimateFileTypeActionPerformed

    private void menuItemENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemENActionPerformed
        LocaleManager.changeDefaultLocale(new Locale("en"));
    }//GEN-LAST:event_menuItemENActionPerformed

    private void menuItemPLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemPLActionPerformed
        LocaleManager.changeDefaultLocale(new Locale("pl"));
    }//GEN-LAST:event_menuItemPLActionPerformed

    private void menuItemLowDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLowDetailsActionPerformed
        TextAreaLogHandler.getInstance().setLoggingLevel(Level.WARNING);
        menuItemLowDetails.setSelected(true);
        menuItemMidDetails.setSelected(false);
        menuItemHighDetails.setSelected(false);
    }//GEN-LAST:event_menuItemLowDetailsActionPerformed

    private void menuItemMidDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemMidDetailsActionPerformed
        TextAreaLogHandler.getInstance().setLoggingLevel(Level.INFO);
        menuItemLowDetails.setSelected(false);
        menuItemMidDetails.setSelected(true);
        menuItemHighDetails.setSelected(false);
    }//GEN-LAST:event_menuItemMidDetailsActionPerformed

    private void menuItemHighDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHighDetailsActionPerformed
        TextAreaLogHandler.getInstance().setLoggingLevel(Level.FINER);
        menuItemLowDetails.setSelected(false);
        menuItemMidDetails.setSelected(false);
        menuItemHighDetails.setSelected(true);
    }//GEN-LAST:event_menuItemHighDetailsActionPerformed

    private void buttonMoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoreActionPerformed
        jXCollapsiblePane.setCollapsed(!jXCollapsiblePane.isCollapsed());
        pack();

        if (jXCollapsiblePane.isCollapsed()) {
            buttonMore.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonMore.text"));
        } else {
            buttonMore.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonMore.text_collapse"));
        }
    }//GEN-LAST:event_buttonMoreActionPerformed

    private void listFilesKeyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilesKeyPressedHandler
        JList list = (JList) evt.getSource();
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int[] indices = list.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                ((DefaultListModel) list.getModel()).remove(indices[i]);
            }
            if (indices.length > 0) {
                int firstSelected = indices[0] + 1;   // 1-based for convenience
                int listSize = list.getModel().getSize();
                if (listSize >= firstSelected) {
                    list.setSelectedIndex(firstSelected - 1);
                } else {
                    list.setSelectedIndex(listSize - 1);
                }
            }
        }
    }//GEN-LAST:event_listFilesKeyPressedHandler

    private void menuHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuHelpMouseClicked

    }//GEN-LAST:event_menuHelpMouseClicked

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        SwingUtilities.invokeLater(() -> {
            String title1 = "<html><body>"
                    + "<h3>" + APP_NAME + " v. " + APP_VERSION + "</h3><br>"
                    + ResourceBundle.getBundle(BUNDLE).getString("dla Katedry Paleogeografii")
                    + "<br><br>"
                    + "\u00a9 Aleksander Hulist (" + MainWindow.YEAR + ")<br>"
                    + "aleksander.hulist@gmail.com<br><br>"
                    + "</body></html>";
            /*"<html><body style='width: 200px; padding: 5px;'>"
             + "<h1>Do U C Me?</h1>"
             + "Here is a long string that will wrap.  "
             + "The effect we want is a multi-line label.";*/
            JLabel textLabel = new JLabel(title1);

            JOptionPane optionPane = new JOptionPane(textLabel, INFORMATION_MESSAGE);
            final JDialog dialog = optionPane.createDialog(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuAbout.text"));
            dialog.setModalityType(Dialog.ModalityType.MODELESS);
            dialog.setAlwaysOnTop(false);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                try {
                    final List<Image> icons1 = new ArrayList<>();
                    icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/32.png")));
                    icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/48.png")));
                    icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/64.png")));
                    icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/96.png")));
                    icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/128.png")));
                    icons1.add(ImageIO.read(getClass().getClassLoader().getResource("resources/450.png")));
                    dialog.setIconImages(icons1);
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            textLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && !e.isConsumed() && Locale.getDefault().equals(new Locale("pl"))) {
                        e.consume();

                        final JFrame whoopsieFrame = new JFrame("Whoopsie!");
                        EventQueue.invokeLater(() -> {
//                            ten kod psuje!!! wtf
//                            try {
//                                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//                                ex.printStackTrace();
//                            }

                            dialog.setVisible(false);
                            dialog.dispose();

                            whoopsieFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            whoopsieFrame.add(new EasterEggPane());
                            whoopsieFrame.pack();
                            whoopsieFrame.setLocationRelativeTo(null);
                            whoopsieFrame.setVisible(true);
                            whoopsieFrame.requestFocus();

                            Timer timer = new Timer(3000, (ActionEvent e1) -> {
                                whoopsieFrame.setVisible(false);
                                whoopsieFrame.dispose();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        });
                    }
                }
            });

            // background does not work
            BufferedImage bi = null;
            try {
                bi = ImageIO.read(getClass().getClassLoader().getResource("resources/background_700.jpg"));
            } catch (IOException ex) {
            }
        });
    }//GEN-LAST:event_menuAboutActionPerformed

    private void menuItemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemPreferencesActionPerformed
        JFrame frame = PREFERENCES_JFRAME;
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //frame.getContentPane().add(new MyPanel2());
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_menuItemPreferencesActionPerformed

    private void comboBoxDailyFileTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDailyFileTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxDailyFileTypeActionPerformed

    private void comboBoxColDailySelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxColDailySelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxColDailySelectActionPerformed

    private void textFieldDailyExcludedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldDailyExcludedFocusLost
        UserPreferences.getInstance().getPrefs().put("textFieldDailyExcluded", textFieldDailyExcluded.getText());
    }//GEN-LAST:event_textFieldDailyExcludedFocusLost

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton buttonMore;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkBoxAllYears;
    private javax.swing.JPanel collapsibleParent;
    private javax.swing.JComboBox comboBoxChronoFileType;
    private javax.swing.JComboBox comboBoxClimateFileType;
    private javax.swing.JComboBox comboBoxColDailySelect;
    private javax.swing.JComboBox comboBoxColSelect;
    private javax.swing.JComboBox comboBoxDailyFileType;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelChronologyFile;
    private javax.swing.JLabel labelClimateFile;
    private javax.swing.JLabel labelColumn;
    private javax.swing.JLabel labelColumnDaily;
    private javax.swing.JLabel labelDailyExcluded;
    private javax.swing.JLabel labelDailyFile;
    private javax.swing.JLabel labelFileTypeChrono;
    private javax.swing.JLabel labelFileTypeClima;
    private javax.swing.JLabel labelFileTypeDaily;
    private javax.swing.JLabel labelListClimateHelper;
    private javax.swing.JLabel labelListDendroHelper;
    private javax.swing.JLabel labelYearEnd;
    private javax.swing.JLabel labelYearStart;
    private javax.swing.JList listClimateFiles;
    private javax.swing.JList listDailyFiles;
    private javax.swing.JList listDendroFiles;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JCheckBoxMenuItem menuItemHighDetails;
    private javax.swing.JMenuItem menuItemLangEN;
    private javax.swing.JMenuItem menuItemLangPL;
    private javax.swing.JCheckBoxMenuItem menuItemLowDetails;
    private javax.swing.JCheckBoxMenuItem menuItemMidDetails;
    public javax.swing.JMenuItem menuItemPreferences;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JMenu menuLogs;
    private javax.swing.JMenu menuLogsDetails;
    private javax.swing.JMenu menuSettings;
    private javax.swing.JPanel panelClimate;
    private javax.swing.JPanel panelDaily;
    private javax.swing.JPanel panelDendro;
    private javax.swing.JPanel panelYears;
    private javax.swing.JRadioButton radioDaily;
    private javax.swing.JRadioButton radioMonthly;
    private javax.swing.JTextField textFieldDailyExcluded;
    private javax.swing.JTextField textFieldYearEnd;
    private javax.swing.JTextField textFieldYearStart;
    // End of variables declaration//GEN-END:variables
    private File[] selectedChronoFile = null;
    private File[] selectedClimateFile = null;
    private File[] selectedDailyFile = null;
    private Logger log, globalLog;

    private void setUINames() {
        textFieldYearStart.setName("textFieldYearStart");
        textFieldYearEnd.setName("textFieldYearEnd");
        checkBoxAllYears.setName("checkBoxAllYears");
        dropdownPanel.getTextAreaMonths().setName("textAreaMonths");
    }

    private void setFromPreferences() {
        Preferences p = UserPreferences.getInstance().getPrefs();

        textFieldYearStart.setText(p.get(textFieldYearStart.getName(), ""));
        textFieldYearEnd.setText(p.get(textFieldYearEnd.getName(), ""));
        checkBoxAllYears.setSelected(p.getBoolean(checkBoxAllYears.getName(), false));
        dropdownPanel.getTextAreaMonths().setText(p.get(dropdownPanel.getTextAreaMonths().getName(), getDefaultMonths()));
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
        TextAreaToMonths ta = new TextAreaToMonths(dropdownPanel.getTextAreaMonths());
        ta.setIsLoggingOn(false);

        boolean valid = true;

        if (!checkBoxAllYears.isSelected() && !YearsRangeValidator.validate(textFieldYearStart.getText(), textFieldYearEnd.getText())) {
            log.log(Level.WARNING, ResourceBundle.getBundle(BUNDLE).getString("WPROWADŹ POPRAWNY ZAKRES LAT."));
            valid = false;
        }

//        if (selectedChronoFile == null || selectedChronoFile.length == 0 || (selectedChronoFile.length == 1 && (selectedChronoFile[0] == null || !selectedChronoFile[0].isFile()))) {
        if (selectedChronoFile.length == 0) {
            valid = false;
            log.log(Level.WARNING, ResourceBundle.getBundle(BUNDLE).getString("WYBIERZ PLIK CHRONOLOGII."));
        } else {
            for (File oneSelectedChronoFile : selectedChronoFile) {
                if (oneSelectedChronoFile == null) {
                    valid = false;
                    log.log(Level.WARNING, ResourceBundle.getBundle(BUNDLE).getString("NIEPOPRAWNY PLIK CHRONOLOGII."));
                } else if (!oneSelectedChronoFile.isFile()) {
                    valid = false;
                    log.log(Level.WARNING, String.format(ResourceBundle.getBundle(BUNDLE).getString("PLIK CHRONOLOGII %S NIE JEST PLIKIEM"), oneSelectedChronoFile.getName()));
                }
            }
        }

//        if (selectedClimateFile == null || selectedClimateFile.length == 0 || (selectedClimateFile.length == 1 && (selectedClimateFile[0] == null || !selectedClimateFile[0].isFile()))) {
        if (selectedClimateFile.length == 0) {
            valid = false;
            log.log(Level.WARNING, ResourceBundle.getBundle(BUNDLE).getString("WYBIERZ PLIK KLIMATYCZNY."));
        } else {
            for (File oneSelectedClimateFile : selectedClimateFile) {
                if (oneSelectedClimateFile == null) {
                    valid = false;
                    log.log(Level.WARNING, ResourceBundle.getBundle(BUNDLE).getString("NIEPOPRAWNY PLIK KLIMATYCZNY."));
                } else if (!oneSelectedClimateFile.isFile()) {
                    valid = false;
                    log.log(Level.WARNING, String.format(ResourceBundle.getBundle(BUNDLE).getString("PLIK KLIMATYCZNY %S NIE JEST PLIKIEM"), oneSelectedClimateFile.getName()));
                }
            }
        }

        if (ta.getList().isEmpty()) {
            valid = false;
        }

        return valid;
    }

    public String getDefaultMonths() {
        return "6 6 1\n"
                + "7 7 1\n"
                + "8 8 1\n"
                + "9 9 1\n"
                + "10 10 1\n"
                + "11 11 1\n"
                + "12 12 1\n\n"
                + "1 1\n"
                + "2 2\n"
                + "3 3\n"
                + "4 4\n"
                + "5 5\n"
                + "6 6\n"
                + "7 7\n"
                + "8 8\n"
                + "9 9\n"
                + "10 10\n\n"
                + "1 3\n"
                + "4 5\n"
                + "6 7\n"
                + "6 8\n"
                + "9 10\n"
                + "11 12\n"
                + "4 9\n"
                + "5 10\n"
                + "4 10\n\n"
                + "1 12\n";
    }

    @Override
    public void onLocaleChange(Locale oldLocale) {
        if (jXCollapsiblePane.isCollapsed()) {
            buttonMore.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonMore.text"));
        } else {
            buttonMore.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonMore.text_collapse"));
        }
        labelChronologyFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelChronologyFile.text"));
        labelClimateFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelClimateFile.text"));
        labelColumn.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelColumn.text"));
        labelFileTypeChrono.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelFileTypeChrono.text"));
        labelFileTypeClima.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelFileTypeClima.text"));
        /*labelLoadedChrono.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelLoadedChrono.text"));
         if (labelLoadedChronoFile.getText().equals(ResourceBundle.getBundle(BUNDLE, oldLocale).getString("MainWindow.labelLoadedChronoFile.text"))) {
         labelLoadedChronoFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelLoadedChronoFile.text"));
         }
         labelLoadedClima.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelLoadedClima.text"));
         if (labelLoadedClimateFile.getText().equals(ResourceBundle.getBundle(BUNDLE, oldLocale).getString("MainWindow.labelLoadedClimateFile.text"))) {
         labelLoadedClimateFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelLoadedClimateFile.text"));
         }*/
        dropdownPanel.getLabelResults().setText(ResourceBundle.getBundle(BUNDLE).getString("DropdownContentsPanel.labelResults.text"));
        labelYearEnd.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelYearEnd.text"));
        labelYearStart.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelYearStart.text"));
        dropdownPanel.getLabelMonthsRange().setText(ResourceBundle.getBundle(BUNDLE).getString("DropdownContentsPanel.labelMonthsRange.text"));

        menuLanguage.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuLanguage.text"));
        menuHelp.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuHelp.text"));
        menuAbout.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuAbout.text"));
        menuSettings.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuSettings.text"));
        menuItemPreferences.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuItemPreferences.text"));
        PREFERENCES_JFRAME.setTitle(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuSettings.text"));

        /* menu logów
         menuLogs.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuSettings.text"));
         menuLogsDetails.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuSettingsDetails.text"));
         menuItemLowDetails.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuItemLowDetails.text"));
         menuItemMidDetails.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuItemMidDetails.text"));
         menuItemHighDetails.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.menuItemHighDetails.text"));*/
        checkBoxAllYears.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.checkBoxAllYears.text"));
        buttonStart.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonStart.text"));
        dropdownPanel.getButtonClearTextArea().setText(ResourceBundle.getBundle(BUNDLE).getString("DropdownContentsPanel.buttonClearTextArea.text"));
        dropdownPanel.getButtonResetTextAreaMonths().setText(ResourceBundle.getBundle(BUNDLE).getString("DropdownContentsPanel.buttonResetTextAreaMonths.text"));
        //buttonSelectChronoFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonSelectChronoFile.text"));
        //buttonSelectClimateFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.buttonSelectClimateFile.text"));

        labelDailyFile.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelDailyFile.text"));
        labelFileTypeDaily.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelFileTypeDaily.text"));
        labelColumnDaily.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelColumnDaily.text"));

        radioDaily.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelDailyFile.text"));
        radioMonthly.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.radioMonthly.text"));
        labelDailyExcluded.setText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelDailyExcluded.text"));
        labelDailyExcluded.setToolTipText(ResourceBundle.getBundle(BUNDLE).getString("MainWindow.labelDailyExcluded.toolTipText"));

        dropdownPanel.getLabelTextAreaMonthsHelper().setToolTipText(ResourceBundle.getBundle(BUNDLE).getString("WPROWADŹ ZAKRESY MIESIĘCY W FORMACIE"));
        labelListDendroHelper.setToolTipText(ResourceBundle.getBundle(BUNDLE).getString("jListHelper"));
        labelListClimateHelper.setToolTipText(ResourceBundle.getBundle(BUNDLE).getString("jListHelper"));

        comboBoxChronoFileType.setModel(new DefaultComboBoxModel<>(ChronologyFileTypes.getDisplayNames()));
        comboBoxColDailySelect.setModel(new DefaultComboBoxModel<>(DailyColumnTypes.getDisplayNames()));

        PREFERENCES_JFRAME.onLocaleChange(oldLocale);

        pack();
    }

    /*
     dodając taki listener
     menuAbout.addMenuListener(this.getMenuAboutListener());
     można zrobić click-on action na kontenerze typu Menu (w przeciwieństwie do MenuItem)
     */
//    private MenuListener getMenuAboutListener() {
//        return new MenuListener() {
//
//            @Override
//            public void menuSelected(MenuEvent e) {
//                SwingUtilities.invokeLater(() -> {
//                    String title1 = "<html><body>"
//                            + "<h3>" + APP_NAME + " v. " + APP_VERSION + "</h3><br>"
//                            + ResourceBundle.getBundle(BUNDLE).getString("dla Katedry Paleogeografii")
//                            + "<br><br>"
//                            + "\u00a9 Aleksander Hulist ("+MainWindow.YEAR+")<br>"
//                            + "aleksander.hulist@gmail.com<br><br>"
//                            + "</body></html>";
//                    /*"<html><body style='width: 200px; padding: 5px;'>"
//                     + "<h1>Do U C Me?</h1>"
//                     + "Here is a long string that will wrap.  "
//                     + "The effect we want is a multi-line label.";*/
//                    JLabel textLabel = new JLabel(title1);
//                    BufferedImage bi = null;
//                    try {
//                        bi = ImageIO.read(getClass().getClassLoader().getResource("resources/background_700.jpg"));
//                    } catch (IOException ex) {}
//                    if (bi!=null) {
//                        JOptionPaneBackground opb = new JOptionPaneBackground(bi);
//                        opb.showMessageDialog(null, textLabel);
//                    }else{
//                        JOptionPane.showMessageDialog(null, textLabel);
//                    }
//                });
//            }
//
//            @Override
//            public void menuDeselected(MenuEvent e) {
//            }
//
//            @Override
//            public void menuCanceled(MenuEvent e) {
//            }
//        };
//    }
    class JOptionPaneBackground extends JOptionPane {

        private final BufferedImage img;

        public JOptionPaneBackground(BufferedImage image) {
            this.img = image;
        }

        @Override
        public void paint(Graphics g) {
           //Pick one of the two painting methods below.

            //Option 1:
            //Define the bounding region to paint based on image size.
            //Be careful, if the image is smaller than the JOptionPane size you
            //will see a solid white background where the image does not reach.
            //g.drawImage(img, 0, 0, img.getWidth(), img.getHeight());
            //Option 2:
            //If the image can be guaranteed to be larger than the JOptionPane's size
            Dimension curSize = this.getSize();
            g.drawImage(img, 0, 0, curSize.width, curSize.height, null);

            //Make sure to paint all the other properties of Swing components.
            super.paint(g);
        }
    }

    private void setDnDLists() {
        MouseMotionAdapter mouseAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();
                int index = l.locationToIndex(e.getPoint());
                if (index > -1) {
                    Object elem = m.getElementAt(index);
                    if (elem instanceof FileWrap) {
                        l.setToolTipText(((FileWrap) elem).getTooltip());
                    } else {
                        l.setToolTipText(m.getElementAt(index).toString());
                    }
                }
            }
        };

        // dendro list
        listDendroFiles.setModel(new DefaultListModel<>());
        new FileDrop(listDendroFiles, new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                DefaultListModel list = ((DefaultListModel) listDendroFiles.getModel());
                for (File file : files) {
                    boolean duplicate = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (file.equals(list.get(i))) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        list.addElement(new FileWrap(file));
                    }
                }
            }
        });
        listDendroFiles.addMouseMotionListener(mouseAdapter);

        // climate list
        listClimateFiles.setModel(new DefaultListModel<>());
        new FileDrop(listClimateFiles, new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                DefaultListModel list = ((DefaultListModel) listClimateFiles.getModel());
                for (File file : files) {
                    boolean duplicate = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (file.equals(list.get(i))) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        list.addElement(new FileWrap(file));
                    }
                }
            }
        });
        listClimateFiles.addMouseMotionListener(mouseAdapter);

        // daily list
        listDailyFiles.setModel(new DefaultListModel<>());
        new FileDrop(listDailyFiles, new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                DefaultListModel list = ((DefaultListModel) listDailyFiles.getModel());
                for (File file : files) {
                    boolean duplicate = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (file.equals(list.get(i))) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        list.addElement(new FileWrap(file));
                    }
                }
            }
        });
        listDailyFiles.addMouseMotionListener(mouseAdapter);
    }

    /**
     * removes settings from menu and sets logging to high
     */
    private void disableSettings() {
        menuBar.remove(menuLogs);
        menuItemHighDetailsActionPerformed(null);
    }

    public class EasterEggPane extends JPanel {

        private BufferedImage image;

        public EasterEggPane() {
            try {
                image = ImageIO.read(getClass().getResource("/resources/4519_ea78_480"));
            } catch (IOException ex) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(480, 390);
        }
    }
}
