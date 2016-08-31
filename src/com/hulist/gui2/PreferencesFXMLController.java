/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * FXML Controller class
 *
 * @author Aleksander
 */
public class PreferencesFXMLController implements Initializable {

    @FXML
    private Label labelCorrWindowVal;

    @FXML
    private Slider sliderCorrWindow;

    public static final String PREFS_FXML_NAME = "PreferencesFXML.fxml";

    private GUIMain guiMain;
    private MainFXMLController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.labelCorrWindowVal.setText(String.format("%d", (int) this.sliderCorrWindow.getValue()));
        this.sliderCorrWindow.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.intValue()%2==1) {
                labelCorrWindowVal.setText(String.format("%d", newValue.intValue()));
            }else{
                labelCorrWindowVal.setText(String.format("%d", newValue.intValue()+1));
            }
        });
    }

    void switchLocale(Locale newLocale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PREFS_FXML_NAME), ResourceBundle.getBundle(guiMain.getBundle(), newLocale));
            Parent newRoot = fxmlLoader.load();
            mainController.setPrefsController(fxmlLoader.getController());
            mainController.setPrefsScene(newRoot);
        } catch (IOException ex) {
        }
    }

    public void setGuiMain(GUIMain guiMain) {
        this.guiMain = guiMain;
    }

    void setMainController(MainFXMLController controller) {
        this.mainController = controller;
    }
}
