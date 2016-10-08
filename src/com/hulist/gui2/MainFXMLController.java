/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import com.hulist.logic.ProcessData;
import com.hulist.logic.RunParams;
import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import com.hulist.util.StaticSettings;
import com.hulist.util.TextAreaToMonths;
import com.hulist.util.UserPreferences;
import com.hulist.util.log.DelegatingAppender;
import com.hulist.util.log.TextAreaOutputStream;
import com.hulist.validators.YearValidator;
import com.hulist.validators.YearsRangeValidator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
    private ChoiceBox<ChronologyFileTypes> comboBoxChronoFileType;
    @FXML
    private ChoiceBox<ClimateFileTypes> comboBoxClimateFileType;
    @FXML
    private ListView<File> listViewDendroFiles;
    @FXML
    private ListView<File> listViewClimateFiles;
    @FXML
    private TextArea textAreaMonthsRanges;
    @FXML
    private FlowPane paneColumnDendro;
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

    public static final String MAIN_FXML_NAME = "MainFXML.fxml";
    Logger log = LoggerFactory.getLogger(MainFXMLController.class);

    public static final String BUNDLE = "com/hulist/bundles/Bundle";

    private GUIMain guiMain;
    private PreferencesFXMLController prefsController;
    private Stage prefsStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Logger
        DelegatingAppender.setStaticOutputStream(new TextAreaOutputStream(textAreaOutput));

        titledpane.heightProperty().addListener((obs, oldHeight, newHeight) -> this.guiMain.getMainStage().sizeToScene());

        // ComboBoxes
        comboBoxClimateFileType.setItems(FXCollections.observableArrayList(ClimateFileTypes.values()));
        comboBoxChronoFileType.setItems(FXCollections.observableArrayList(ChronologyFileTypes.values()));
        comboBoxColSelect.setItems(FXCollections.observableArrayList(TabsColumnTypes.values()));
        comboBoxChronoFileType.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (ChronologyFileTypes.TABS.ordinal() == newValue.intValue()) {
                paneColumnDendro.setVisible(true);
            } else {
                paneColumnDendro.setVisible(false);
            }
        });

        // Lists
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
        listViewDendroFiles.setCellFactory(factory);
        listViewClimateFiles.setCellFactory(factory);
        listViewDendroFiles.setItems(FXCollections.observableArrayList());
        listViewClimateFiles.setItems(FXCollections.observableArrayList());
        listViewDendroFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewClimateFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        EventHandler<DragEvent> doeh = (DragEvent event) -> {
            if (event.getSource() instanceof ListView) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        };
        listViewDendroFiles.setOnDragOver(doeh);
        listViewClimateFiles.setOnDragOver(doeh);
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
        listViewDendroFiles.setOnDragDropped(ddeh);
        listViewClimateFiles.setOnDragDropped(ddeh);
        EventHandler<KeyEvent> kpeh = (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.DELETE) && event.getSource() instanceof ListView) {
                ListView<File> source = (ListView<File>) event.getSource();
                source.getItems().removeAll(source.getSelectionModel().getSelectedItems());
            }
        };
        listViewDendroFiles.setOnKeyPressed(kpeh);
        listViewClimateFiles.setOnKeyPressed(kpeh);

        // Buttons
        buttonResetMonths.setOnMouseClicked((MouseEvent event) -> {
            textAreaMonthsRanges.setText(StaticSettings.getDefaultMonths());
            UserPreferences.getInstance().getPrefs().put(textAreaMonthsRanges.getId(), textAreaMonthsRanges.getText());
        });
        buttonClearOutput.setOnMouseClicked((MouseEvent event) -> {
            textAreaOutput.clear();
        });

        // Preferences stage
        try {
            prefsStage = new Stage();
            ResourceBundle newResources = ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PreferencesFXMLController.PREFS_FXML_NAME), newResources);
            Parent root = loader.load();
            setPrefsScene(root);
            prefsStage.setTitle(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("MainWindow.menuSettings.text"));
            prefsStage.initModality(Modality.WINDOW_MODAL);
            prefsController = loader.getController();
        } catch (IOException ex) {
            log.error(ex.toString());
        }

        // Set values from UserPreferences
        setValuesFromPrefs();
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
        if (YearValidator.validate(textFieldYearStart.getText()) || textFieldYearStart.getText().equals("")) {
            UserPreferences.getInstance().getPrefs().put(textFieldYearStart.getId(), textFieldYearStart.getText());
        }
    }

    @FXML
    void onKeyReleasedTextFieldYearEnd(KeyEvent event) {
        if (YearValidator.validate(textFieldYearEnd.getText()) || textFieldYearEnd.getText().equals("")) {
            UserPreferences.getInstance().getPrefs().put(textFieldYearEnd.getId(), textFieldYearEnd.getText());
        }
    }

    @FXML
    void onKeyReleasedTextAreaMonths(KeyEvent event) {
        UserPreferences.getInstance().getPrefs().put(textAreaMonthsRanges.getId(), textAreaMonthsRanges.getText());
    }

    @FXML
    void onKeyReleasedCheckBoxAllYears(ActionEvent event) {
        textFieldYearStart.setDisable(checkBoxAllYears.isSelected());
        textFieldYearEnd.setDisable(checkBoxAllYears.isSelected());
        UserPreferences.getInstance().getPrefs().putBoolean(checkBoxAllYears.getId(), checkBoxAllYears.isSelected());
    }

    private File[] selectedChronoFile = null;
    private File[] selectedClimateFile = null;

    @FXML
    private void onStart() {
        selectedChronoFile = new File[listViewDendroFiles.getItems().size()];
        for (int i = 0; i < listViewDendroFiles.getItems().size(); i++) {
            selectedChronoFile[i] = listViewDendroFiles.getItems().get(i);
        }
        selectedClimateFile = new File[listViewClimateFiles.getItems().size()];
        for (int i = 0; i < listViewClimateFiles.getItems().size(); i++) {
            selectedClimateFile[i] = listViewClimateFiles.getItems().get(i);
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

            ChronologyFileTypes chronologyFileType = comboBoxChronoFileType.getValue();
            TabsColumnTypes tabsColumnType = comboBoxColSelect.getValue();
            ClimateFileTypes climateFileType = comboBoxClimateFileType.getValue();

            RunParams wp = new RunParams(allYears,
                    startYear,
                    endYear,
                    selectedChronoFile,
                    selectedClimateFile,
                    chronologyFileType,
                    tabsColumnType,
                    climateFileType,
                    new TextAreaToMonths(textAreaMonthsRanges).getList());
            wp.setPrefs(prefsController.getRunParams());
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
            log.debug(sb.toString());

            new ProcessData(wp).go();
        }
    }

    private boolean isDataValid() {
        TextAreaToMonths ta = new TextAreaToMonths(textAreaMonthsRanges);
        ta.setIsLoggingOn(false);

        boolean valid = true;

        if (!checkBoxAllYears.isSelected() && !YearsRangeValidator.validate(textFieldYearStart.getText(), textFieldYearEnd.getText())) {
            ClassLoader hackLoader = new HackClassLoader(getClass().getClassLoader());
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()/*, hackLoader*/);
            log.warn(bundle.getString("WPROWADŹ POPRAWNY ZAKRES LAT."));
            valid = false;
        }

//        if (selectedChronoFile == null || selectedChronoFile.length == 0 || (selectedChronoFile.length == 1 && (selectedChronoFile[0] == null || !selectedChronoFile[0].isFile()))) {
        if (selectedChronoFile.length == 0) {
            valid = false;
            log.warn(ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()).getString("WYBIERZ PLIK CHRONOLOGII."));
        } else {
            for (File oneSelectedChronoFile : selectedChronoFile) {
                if (oneSelectedChronoFile == null) {
                    valid = false;
                    log.warn(ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()).getString("NIEPOPRAWNY PLIK CHRONOLOGII."));
                } else if (!oneSelectedChronoFile.isFile()) {
                    valid = false;
                    log.warn(String.format(ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()).getString("PLIK CHRONOLOGII %S NIE JEST PLIKIEM"), oneSelectedChronoFile.getName()));
                }
            }
        }

//        if (selectedClimateFile == null || selectedClimateFile.length == 0 || (selectedClimateFile.length == 1 && (selectedClimateFile[0] == null || !selectedClimateFile[0].isFile()))) {
        if (selectedClimateFile.length == 0) {
            valid = false;
            log.warn(ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()).getString("WYBIERZ PLIK KLIMATYCZNY."));
        } else {
            for (File oneSelectedClimateFile : selectedClimateFile) {
                if (oneSelectedClimateFile == null) {
                    valid = false;
                    log.warn(ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()).getString("NIEPOPRAWNY PLIK KLIMATYCZNY."));
                } else if (!oneSelectedClimateFile.isFile()) {
                    valid = false;
                    log.warn(String.format(ResourceBundle.getBundle(BUNDLE, GUIMain.getCurrLocale()).getString("PLIK KLIMATYCZNY %S NIE JEST PLIKIEM"), oneSelectedClimateFile.getName()));
                }
            }
        }

        if (ta.getList().isEmpty()) {
            valid = false;
        }

        return valid;
    }

    public void switchLocale(Locale newLocale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_FXML_NAME), ResourceBundle.getBundle(guiMain.getBundle(), newLocale));
            Parent newRoot = fxmlLoader.load();
            this.guiMain.setMainController(fxmlLoader.getController());
            this.guiMain.setMainScene(newRoot);
        } catch (IOException ex) {
            System.out.println(ex);
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
        textFieldYearStart.setText(UserPreferences.getInstance().getPrefs().get(textFieldYearStart.getId(), ""));
        textFieldYearEnd.setText(UserPreferences.getInstance().getPrefs().get(textFieldYearEnd.getId(), ""));
        checkBoxAllYears.setSelected(UserPreferences.getInstance().getPrefs().getBoolean(checkBoxAllYears.getId(), false));
        textAreaMonthsRanges.setText(UserPreferences.getInstance().getPrefs().get(textAreaMonthsRanges.getId(), StaticSettings.getDefaultMonths()));
        textFieldYearStart.setDisable(checkBoxAllYears.isSelected());
        textFieldYearEnd.setDisable(checkBoxAllYears.isSelected());
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
