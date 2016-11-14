/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import com.hulist.logic.ProcessData;
import com.hulist.logic.RunParams;
import com.hulist.logic.RunType;
import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import com.hulist.logic.daily.DailyColumnTypes;
import com.hulist.logic.daily.DailyFileTypes;
import com.hulist.logic.daily.YearlyCombinations;
import com.hulist.util.Misc;
import com.hulist.util.Progress;
import com.hulist.util.StaticSettings;
import com.hulist.util.TextAreaToMonths;
import com.hulist.util.UserPreferences;
import com.hulist.util.log.BlockingQueueOutput;
import com.hulist.validators.YearsValidator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Aleksander
 */
public class MainFXMLController implements Initializable {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private VBox vbox;
    @FXML
    private TitledPane titledpane;
    @FXML
    private ChoiceBox<TabsColumnTypes> comboBoxColSelect;
    @FXML
    private ChoiceBox<TabsColumnTypes> comboBoxColSelectDaily;
    @FXML
    private ChoiceBox<ChronologyFileTypes> comboBoxChronoFileType;
    @FXML
    private ChoiceBox<ChronologyFileTypes> comboBoxChronoFileTypeDailyTab;
    @FXML
    private ChoiceBox<ClimateFileTypes> comboBoxClimateFileType;
    @FXML
    private ChoiceBox<DailyFileTypes> comboBoxDailyFileType;
    @FXML
    private ChoiceBox<DailyColumnTypes> comboBoxDailyColumn;
    @FXML
    private ListView<File> listViewDendroFiles;
    @FXML
    private ListView<File> listViewDendroFilesDailyTab;
    @FXML
    private ListView<File> listViewClimateFiles;
    @FXML
    private ListView<File> listViewDailyFiles;
    @FXML
    private TextArea textAreaMonthsRanges;
    @FXML
    private TextField textFieldExcludedValues;
    @FXML
    private FlowPane paneColumnDendro;
    @FXML
    private FlowPane paneColumnDendroDailyTab;
    @FXML
    private FlowPane paneColumnDaily;
    @FXML
    private Button buttonResetMonths;
    @FXML
    private CheckBox checkBoxAllYears;
    @FXML
    private TextField textFieldYearStart;
    @FXML
    private TextField textFieldYearEnd;
    @FXML
    private TextArea textAreaOutput;
    @FXML
    private Button buttonClearOutput;
    @FXML
    private Button buttonStart;
    @FXML
    private Menu menuLanguage;
    @FXML
    private TabPane tabPane;
    @FXML
    private ProgressBar progressBarJobs;
    @FXML
    private ProgressBar progressBarFiles;
    @FXML
    private Label labelProgress;
    @FXML
    private FlowPane flowPaneProgressContainer;

    public static final String MAIN_FXML_NAME = "MainFXML.fxml";
    Logger log = LoggerFactory.getLogger(MainFXMLController.class);

    private GUIMain guiMain;
    private PreferencesFXMLController prefsController;
    private Stage prefsStage;
    private Preferences prefs = UserPreferences.getInstance().getPrefs();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Logger & TextArea
        textAreaOutput.setTextFormatter(getTextAreaOutputFormatter());
        BlockingQueueOutput.setTextArea(textAreaOutput);

        // YearlyCombinations
        new Thread(() -> {
            YearlyCombinations.initialize();
        }).start();

        // TitledPane expansion
        titledpane.heightProperty().addListener((obs, oldHeight, newHeight) -> this.guiMain.getMainStage().sizeToScene());
        titledpane.expandedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            this.guiMain.getMainStage().sizeToScene();
        });

        // ComboBoxes
        comboBoxClimateFileType.setItems(FXCollections.observableArrayList(ClimateFileTypes.values()));
        comboBoxChronoFileType.setItems(FXCollections.observableArrayList(ChronologyFileTypes.values()));
        comboBoxChronoFileTypeDailyTab.setItems(FXCollections.observableArrayList(ChronologyFileTypes.values()));
        comboBoxColSelect.setItems(FXCollections.observableArrayList(TabsColumnTypes.values()));
        comboBoxColSelectDaily.setItems(FXCollections.observableArrayList(TabsColumnTypes.values()));
        comboBoxChronoFileType.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (ChronologyFileTypes.TABS.ordinal() == newValue.intValue()) {
                paneColumnDendro.setVisible(true);
            } else {
                paneColumnDendro.setVisible(false);
            }
        });
        comboBoxChronoFileTypeDailyTab.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (ChronologyFileTypes.TABS.ordinal() == newValue.intValue()) {
                paneColumnDendroDailyTab.setVisible(true);
            } else {
                paneColumnDendroDailyTab.setVisible(false);
            }
        });
        comboBoxDailyFileType.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (DailyFileTypes.N_YMD_VS.ordinal() == newValue.intValue()) {
                paneColumnDaily.setVisible(true);
            } else {
                paneColumnDaily.setVisible(false);
            }
        });
        comboBoxDailyFileType.setItems(FXCollections.observableArrayList(DailyFileTypes.values()));
        comboBoxDailyColumn.setItems(FXCollections.observableArrayList(DailyColumnTypes.values()));

        // Lists
        ArrayList<ListView<File>> lists = new ArrayList<>();
        lists.add(listViewDendroFiles);
        lists.add(listViewClimateFiles);
        lists.add(listViewDendroFilesDailyTab);
        lists.add(listViewDailyFiles);
        initializeListViews(lists);

        // Buttons
        buttonResetMonths.setOnMouseClicked((MouseEvent event) -> {
            textAreaMonthsRanges.setText(StaticSettings.getDefaultMonths());
            prefs.put(textAreaMonthsRanges.getId(), textAreaMonthsRanges.getText());
        });
        buttonClearOutput.setOnMouseClicked((MouseEvent event) -> {
            textAreaOutput.clear();
        });
        // Start button (when computation is running)
        ProcessData.isAnyComputationRunning.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                buttonStart.setDisable(true);
                menuLanguage.setDisable(true);
            } else {
                buttonStart.setDisable(false);
                menuLanguage.setDisable(false);
            }
        });

        // Preferences stage
        try {
            prefsStage = new Stage();
            ResourceBundle newResources = ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PreferencesFXMLController.PREFS_FXML_NAME), newResources);
            Parent root = loader.load();
            setPrefsScene(root);
            prefsStage.setResizable(false);
            prefsStage.setTitle(Misc.getInternationalized("MainWindow.menuSettings.text"));
            prefsStage.initModality(Modality.WINDOW_MODAL);
            prefsController = loader.getController();
        } catch (IOException ex) {
            log.error(ex.toString());
        }

        // TextField
        textFieldExcludedValues.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {    // focus lost
                textFieldExcludedValues.setText(textFieldExcludedValues.getText().replace(",", "."));
                textFieldExcludedValues.setText(textFieldExcludedValues.getText().replace(";;", ";"));
                String[] ev = textFieldExcludedValues.getText().split(";");
                boolean ok = true;
                for (String val : ev) {
                    if (!val.trim().isEmpty()) {
                        try {
                            double number = Double.parseDouble(val.trim());
                        } catch (NumberFormatException e) {
                            ok = false;
                            log.info(String.format(Misc.getInternationalized("zla wartosc (%s) excludedValue"), val.trim()));
                        }
                    }
                }
                if (ok) {
                    prefs.put(textFieldExcludedValues.getId(), textFieldExcludedValues.getText());
                }
            }
        });

        // Tabs
        tabPane.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            prefs.putInt(tabPane.getId() + ":selectedTabIndex", newValue.intValue());
        });

        // Set values from UserPreferences
        setValuesFromPrefs();
    }

    @FXML
    private void onActionMenuItemAbout(ActionEvent evt) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(Misc.getInternationalized("MainWindow.menuAbout.text"));
        alert.setHeaderText(GUIMain.APP_NAME + " " + GUIMain.APP_VERSION);
        StringBuilder sb = new StringBuilder();
        sb.append(Misc.getInternationalized("tree lab"))
                .append("\n\n")
                .append(String.format(Misc.getInternationalized("copyright"), GUIMain.YEAR));
        alert.setContentText(sb.toString());
        alert.initOwner(guiMain.getMainStage());

        alert.showAndWait();
    }

    @FXML
    private void onActionMenuItemPL(ActionEvent evt) {
        this.guiMain.switchLocale(new Locale("pl"));
    }

    @FXML
    private void onActionMenuItemEN(ActionEvent evt) {
        this.guiMain.switchLocale(Locale.ENGLISH);
    }

    @FXML
    void onKeyReleasedTextFieldYearStart(KeyEvent event) {
        if (new YearsValidator().validateSingleYear(textFieldYearStart.getText(), true) || textFieldYearStart.getText().equals("")) {
            prefs.put(textFieldYearStart.getId(), textFieldYearStart.getText());
        }
    }

    @FXML
    void onKeyReleasedTextFieldYearEnd(KeyEvent event) {
        if (new YearsValidator().validateSingleYear(textFieldYearEnd.getText(), true) || textFieldYearEnd.getText().equals("")) {
            prefs.put(textFieldYearEnd.getId(), textFieldYearEnd.getText());
        }
    }

    @FXML
    void onKeyReleasedTextAreaMonths(KeyEvent event) {
        prefs.put(textAreaMonthsRanges.getId(), textAreaMonthsRanges.getText());
    }

    @FXML
    void onKeyReleasedCheckBoxAllYears(ActionEvent event) {
        textFieldYearStart.setDisable(checkBoxAllYears.isSelected());
        textFieldYearEnd.setDisable(checkBoxAllYears.isSelected());
        prefs.putBoolean(checkBoxAllYears.getId(), checkBoxAllYears.isSelected());
    }

    private File[] selectedChronoFile = null;
    private File[] selectedClimateFile = null;
    private File[] selectedDailyFile = null;

    @FXML
    private void onStart() {
        ObservableList<File> chronoObservableList = null;
        ChronologyFileTypes chronologyFileType = null;
        TabsColumnTypes tabsColumnType = null;
        switch (tabPane.getSelectionModel().selectedIndexProperty().intValue()) {
            case 0:     // tab 0 selected : monthly
                chronoObservableList = listViewDendroFiles.getItems();
                chronologyFileType = comboBoxChronoFileType.getValue();
                tabsColumnType = comboBoxColSelect.getValue();
                selectedClimateFile = new File[listViewClimateFiles.getItems().size()];
                for (int i = 0; i < listViewClimateFiles.getItems().size(); i++) {
                    selectedClimateFile[i] = listViewClimateFiles.getItems().get(i);
                }
                break;
            case 1:     // tab 1 selected : daily
                chronoObservableList = listViewDendroFilesDailyTab.getItems();
                chronologyFileType = comboBoxChronoFileTypeDailyTab.getValue();
                tabsColumnType = comboBoxColSelectDaily.getValue();
                selectedDailyFile = new File[listViewDailyFiles.getItems().size()];
                for (int i = 0; i < listViewDailyFiles.getItems().size(); i++) {
                    selectedDailyFile[i] = listViewDailyFiles.getItems().get(i);
                }
        }

        selectedChronoFile = new File[chronoObservableList.size()];
        for (int i = 0; i < chronoObservableList.size(); i++) {
            selectedChronoFile[i] = chronoObservableList.get(i);
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

        if (isDataValid()) {
            RunParams runParams = null;
            Progress progress = new Progress(flowPaneProgressContainer, progressBarJobs, progressBarFiles, labelProgress);
            StringBuilder sb = new StringBuilder();

            switch (tabPane.getSelectionModel().selectedIndexProperty().intValue()) {
                case 0:
                    ClimateFileTypes climateFileType = comboBoxClimateFileType.getValue();
                    runParams = new RunParams(RunType.MONTHLY,
                            allYears,
                            startYear,
                            endYear,
                            selectedChronoFile,
                            selectedClimateFile,
                            chronologyFileType,
                            tabsColumnType,
                            climateFileType,
                            new TextAreaToMonths(textAreaMonthsRanges).getList());

                    sb.append("WindowParams: [")
                            .append(allYears).append(", ")
                            .append(startYear).append(", ")
                            .append(endYear).append(", ")
                            .append(Arrays.toString(selectedChronoFile)).append(", ")
                            .append(Arrays.toString(selectedClimateFile)).append(", ")
                            .append(chronologyFileType).append(", ")
                            .append(tabsColumnType).append(", ")
                            .append(climateFileType).append("]");
                    break;
                case 1:
                    DailyFileTypes dft = comboBoxDailyFileType.getValue();
                    DailyColumnTypes dct = comboBoxDailyColumn.getValue();
                    List<String> excludedValues = Arrays.asList(textFieldExcludedValues.getText().split(";", -1));
                    excludedValues.removeAll(Arrays.asList(""));
                    runParams = new RunParams(RunType.DAILY,
                            allYears,
                            startYear,
                            endYear,
                            selectedChronoFile,
                            selectedDailyFile,
                            chronologyFileType,
                            tabsColumnType,
                            dft,
                            dct,
                            excludedValues);

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
                            .append(textFieldExcludedValues.getText()).append("]");
                    break;
            }

            runParams.setSettings(prefsController.getRunSettings());
            runParams.setProgress(progress);
            log.debug(sb.toString());

            ProcessData pd = new ProcessData(runParams);
            pd.getRunParams().setRoot(guiMain.getMainStage());
            pd.getRunParams().setMainController(this);
            pd.go();
        }
    }

    private boolean isDataValid() {
        log.info(Misc.getInternationalized("Starting data validation"));
        boolean valid = true;
        int selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();
        ResourceBundle bundle = ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale());

        // Lista plików chronologii dendro
        if (selectedChronoFile.length == 0) {
            valid = false;
            log.warn(bundle.getString("WYBIERZ PLIK CHRONOLOGII."));
        } else {
            for (File oneSelectedChronoFile : selectedChronoFile) {
                if (oneSelectedChronoFile == null) {
                    valid = false;
                    log.warn(bundle.getString("NIEPOPRAWNY PLIK CHRONOLOGII."));
                } else if (!oneSelectedChronoFile.isFile()) {
                    valid = false;
                    log.warn(String.format(bundle.getString("PLIK CHRONOLOGII %S NIE JEST PLIKIEM"), oneSelectedChronoFile.getName()));
                }
            }
        }

        // Lista plików klimatycznych
        if (selectedTabIndex == 0) {
            if (selectedClimateFile.length == 0) {
                valid = false;
                log.warn(bundle.getString("WYBIERZ PLIK KLIMATYCZNY."));
            } else {
                for (File oneSelectedClimateFile : selectedClimateFile) {
                    if (oneSelectedClimateFile == null) {
                        valid = false;
                        log.warn(bundle.getString("NIEPOPRAWNY PLIK KLIMATYCZNY."));
                    } else if (!oneSelectedClimateFile.isFile()) {
                        valid = false;
                        log.warn(String.format(bundle.getString("PLIK KLIMATYCZNY %S NIE JEST PLIKIEM"), oneSelectedClimateFile.getName()));
                    }
                }
            }
        }

        // Lista plików dziennych
        if (selectedTabIndex == 1) {
            if (selectedDailyFile.length == 0) {
                valid = false;
                log.warn(bundle.getString("WYBIERZ PLIK DZIENNY."));
            } else {
                for (File oneSelectedDailyFile : selectedDailyFile) {
                    if (oneSelectedDailyFile == null) {
                        valid = false;
                        log.warn(bundle.getString("NIEPOPRAWNY PLIK DZIENNY."));
                    } else if (!oneSelectedDailyFile.isFile()) {
                        valid = false;
                        log.warn(String.format(bundle.getString("PLIK DZIENNY %S NIE JEST PLIKIEM"), oneSelectedDailyFile.getName()));
                    }
                }
            }
        }

        // ComboBoxy plików dendro
        ChoiceBox<ChronologyFileTypes> comboBoxChronoFileTypeGeneral = null;
        ChoiceBox<TabsColumnTypes> comboBoxColSelectGeneral = null;
        switch (selectedTabIndex) {
            case 0:
                comboBoxChronoFileTypeGeneral = comboBoxChronoFileType;
                comboBoxColSelectGeneral = comboBoxColSelect;
                break;
            case 1:
                comboBoxChronoFileTypeGeneral = comboBoxChronoFileTypeDailyTab;
                comboBoxColSelectGeneral = comboBoxColSelectDaily;
                break;
        }

        if (comboBoxChronoFileTypeGeneral.getSelectionModel().isEmpty()) {
            log.warn(bundle.getString("Wybierz typ pliku chronologii"));
            valid = false;
        }
        if (!comboBoxChronoFileTypeGeneral.getSelectionModel().isEmpty()
                && comboBoxChronoFileTypeGeneral.getSelectionModel().getSelectedItem().equals(ChronologyFileTypes.TABS)
                && comboBoxColSelectGeneral.getSelectionModel().isEmpty()) {
            log.warn(String.format(bundle.getString("Wybierz kolumnę"), ChronologyFileTypes.TABS.toString()));
            valid = false;
        }

        // ComboBox plików klimatycznych
        if (selectedTabIndex == 0 && comboBoxClimateFileType.getSelectionModel().isEmpty()) {
            log.warn(bundle.getString("Wybierz typ pliku klimatycznego"));
            valid = false;
        }

        // ComboBox plików dziennych
        if (selectedTabIndex == 1 && comboBoxDailyFileType.getSelectionModel().isEmpty()) {
            log.warn(bundle.getString("Wybierz typ pliku dziennego"));
            valid = false;
        }
        if (selectedTabIndex == 1 && comboBoxDailyFileType.getSelectionModel().getSelectedItem().equals(DailyFileTypes.N_YMD_VS) && comboBoxDailyColumn.getSelectionModel().isEmpty()) {
            log.warn(bundle.getString("Wybierz kolumne dla pliku dziennego"));
            valid = false;
        }

        // Zakresy miesięcy
        TextAreaToMonths ta = new TextAreaToMonths(textAreaMonthsRanges);
        if (selectedTabIndex == 0 && ta.getList().isEmpty()) {
            valid = false;
        }

        // Zakres lat
        if (!checkBoxAllYears.isSelected() && !new YearsValidator().validateRange(textFieldYearStart.getText(), textFieldYearEnd.getText(), false)) {
            ClassLoader hackLoader = new HackClassLoader(getClass().getClassLoader());
            // log.warn już jest w validatorze
            // log.warn(bundle.getString("WPROWADŹ POPRAWNY ZAKRES LAT."));
            valid = false;
        }

        // Excluded values
        if (selectedTabIndex == 1) {
            String[] ev = textFieldExcludedValues.getText().split(";");
            for (String val : ev) {
                if (!val.trim().isEmpty()) {
                    try {
                        double number = Double.parseDouble(val.trim());
                    } catch (NumberFormatException e) {
                        valid = false;
                        log.info(String.format(Misc.getInternationalized("zla wartosc (%s) excludedValue"), val.trim()));
                    }
                }
            }
        }

        return valid;
    }

    public void switchLocale(Locale newLocale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_FXML_NAME), ResourceBundle.getBundle(GUIMain.BUNDLE, newLocale));
            Parent newRoot = fxmlLoader.load();
            this.guiMain.setMainController(fxmlLoader.getController());
            this.guiMain.setMainScene(newRoot);
        } catch (IOException ex) {
            log.error(Misc.stackTraceToString(ex));
        }
    }

    @FXML
    private void onActionMenuItemPreferences(ActionEvent evt) {
        this.prefsStage.show();
    }

    public void setGuiMain(GUIMain guiMain) {
        this.guiMain = guiMain;
    }

    public void setPrefsController(PreferencesFXMLController prefsController) {
        this.prefsController = prefsController;
        prefsController.setMainController(this);
        prefsController.setGuiMain(guiMain);
        guiMain.setPrefsController(prefsController);
    }

    public void initController() {
        prefsStage.initOwner(guiMain.getMainStage().getScene().getWindow());
    }

    public void setPrefsScene(Parent root) {
        if (prefsStage != null) {
            this.prefsStage.setScene(new Scene(root));
        }
    }

    private void setValuesFromPrefs() {
        textFieldYearStart.setText(prefs.get(textFieldYearStart.getId(), ""));
        textFieldYearEnd.setText(prefs.get(textFieldYearEnd.getId(), ""));
        textFieldExcludedValues.setText(prefs.get(textFieldExcludedValues.getId(), ""));
        checkBoxAllYears.setSelected(prefs.getBoolean(checkBoxAllYears.getId(), false));
        textAreaMonthsRanges.setText(prefs.get(textAreaMonthsRanges.getId(), StaticSettings.getDefaultMonths()));
        textFieldYearStart.setDisable(checkBoxAllYears.isSelected());
        textFieldYearEnd.setDisable(checkBoxAllYears.isSelected());

        tabPane.getSelectionModel().select(prefs.getInt(tabPane.getId() + ":selectedTabIndex", 0));
    }

    private TextFormatter<String> getTextAreaOutputFormatter() {
        return null;
    }

    private void initializeListViews(ArrayList<ListView<File>> lists) {
        Callback<ListView<File>, ListCell<File>> factory = new Callback<ListView<File>, ListCell<File>>() {
            @Override
            public ListCell<File> call(ListView<File> param) {
                ListCell<File> cell = new ListCell<File>() {
                    Tooltip tooltip = new Tooltip();

                    @Override
                    public void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(item.getName());
                            tooltip.setText(item.getAbsolutePath());
                            setTooltip(tooltip);
                        }
                    }
                };
                return cell;
            }
        };
        EventHandler<DragEvent> doeh = (DragEvent event) -> {
            if (event.getSource() instanceof ListView) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        };
        EventHandler<DragEvent> ddeh = (DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                if (event.getSource() instanceof ListView) {
                    ListView<File> source = (ListView<File>) event.getSource();
                    for (File file : event.getDragboard().getFiles()) {
                        if (!source.getItems().contains(file) && file.isFile()) {
                            source.getItems().add(file);
                        }
                    }
                }
            }
        };
        EventHandler<KeyEvent> kpeh = (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.DELETE) && event.getSource() instanceof ListView) {
                ListView<File> source = (ListView<File>) event.getSource();
                source.getItems().removeAll(source.getSelectionModel().getSelectedItems());
            }
        };

        for (ListView<File> list : lists) {
            list.setCellFactory(factory);
            list.setItems(FXCollections.observableArrayList());
            list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            list.setOnDragOver(doeh);
            list.setOnDragDropped(ddeh);
            list.setOnKeyPressed(kpeh);
        }
    }

    public class HackClassLoader extends ClassLoader {

        public HackClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            InputStream utf8in = getParent().getResourceAsStream(name);
            if (utf8in != null) {
                try {
                    byte[] utf8Bytes = new byte[utf8in.available()];
                    utf8in.read(utf8Bytes, 0, utf8Bytes.length);
                    byte[] iso8859Bytes = new String(utf8Bytes, "UTF-8").getBytes("ISO-8859-1");
                    return new ByteArrayInputStream(iso8859Bytes);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        utf8in.close();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }
    }

}
