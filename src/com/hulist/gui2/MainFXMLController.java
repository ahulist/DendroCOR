/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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

    public static final String MAIN_FXML_NAME = "MainFXML.fxml";

    private GUIMain guiMain;
    private PreferencesFXMLController prefsController;
    private Stage prefsStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titledpane.heightProperty().addListener((obs, oldHeight, newHeight) -> this.guiMain.getMainStage().sizeToScene());

        // ComboBoxes
        comboBoxClimateFileType.setItems(FXCollections.observableArrayList(ClimateFileTypes.values()));
        comboBoxChronoFileType.setItems(FXCollections.observableArrayList(ChronologyFileTypes.values()));
        comboBoxColSelect.setItems(FXCollections.observableArrayList(TabsColumnTypes.values()));

        // Lists
        Callback<ListView<File>,ListCell<File>> factory = new Callback<ListView<File>, ListCell<File>>() {

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
                        if (!source.getItems().contains(file)) {
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
        
    }

    @FXML
    private void onActionMenuItemPL(ActionEvent evt) {
        this.guiMain.switchLocale(new Locale("pl"));
    }

    @FXML
    private void onActionMenuItemEN(ActionEvent evt) {
        this.guiMain.switchLocale(Locale.ENGLISH);
    }

    public void switchLocale(Locale newLocale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_FXML_NAME), ResourceBundle.getBundle(guiMain.getBundle(), newLocale));
            Parent newRoot = fxmlLoader.load();
            this.guiMain.setMainController(fxmlLoader.getController());
            this.guiMain.setMainScene(newRoot);
        } catch (IOException ex) {
        }
    }

    @FXML
    private void onActionMenuItemPreferences(ActionEvent evt) {
        if (this.prefsStage == null) {
            try {
                prefsStage = new Stage();
                ResourceBundle newResources = ResourceBundle.getBundle(guiMain.getBundle(), guiMain.getCurrLocale());
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PreferencesFXMLController.PREFS_FXML_NAME), newResources);
                Parent root = loader.load();
                setPrefsScene(root);
                prefsStage.setTitle(ResourceBundle.getBundle(guiMain.getBundle(), guiMain.getCurrLocale()).getString("MainWindow.menuSettings.text"));
                prefsStage.initModality(Modality.WINDOW_MODAL);
                prefsStage.initOwner(guiMain.getMainStage().getScene().getWindow());

                setPrefsController(loader.getController());
            } catch (IOException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.prefsStage.show();
    }

    public void setGuiMain(GUIMain guiMain) {
        this.guiMain = guiMain;
    }

    public void setPrefsController(PreferencesFXMLController controller) {
        prefsController = controller;
        prefsController.setMainController(this);
        prefsController.setGuiMain(guiMain);
        guiMain.setPrefsController(prefsController);
    }

    public void setPrefsScene(Parent root) {
        if (prefsStage != null) {
            this.prefsStage.setScene(new Scene(root));
        }
    }
}
