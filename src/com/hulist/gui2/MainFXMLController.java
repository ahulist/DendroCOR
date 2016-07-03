/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import com.hulist.logic.chronology.ChronologyFileTypes;
import com.hulist.logic.chronology.tabs.TabsColumnTypes;
import com.hulist.logic.climate.ClimateFileTypes;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.DefaultComboBoxModel;

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

    public static final String MAIN_FXML_NAME = "MainFXML.fxml";

    private GUIMain guiMain;
    private PreferencesFXMLController prefsController;
    private Stage prefsStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titledpane.heightProperty().addListener((obs, oldHeight, newHeight) -> this.guiMain.getMainStage().sizeToScene());
        comboBoxClimateFileType.setItems(FXCollections.observableArrayList(ClimateFileTypes.values()));
        comboBoxChronoFileType.setItems(FXCollections.observableArrayList(ChronologyFileTypes.values()));
        comboBoxColSelect.setItems(FXCollections.observableArrayList(TabsColumnTypes.values()));
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
