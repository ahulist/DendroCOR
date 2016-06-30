/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private GUIMain guiMain;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onActionMenuItemPL(ActionEvent evt) {
        this.guiMain.switchLocale(new Locale("pl"));
    }

    @FXML
    private void onActionMenuItemEN(ActionEvent evt) {
        this.guiMain.switchLocale(Locale.ENGLISH);
    }

    public GUIMain getGuiMain() {
        return guiMain;
    }

    public void setGuiMain(GUIMain guiMain) {
        this.guiMain = guiMain;
    }

}
