/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Aleksander
 */
public class GUIMain extends Application {

    private Stage stage;
    private final String bundlePath = "com.hulist.bundles.Bundle";
    private final String mainFxmlName = "MainFXML.fxml";
    private MainFXMLController mainController;

    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        this.stage = stage;

        URL location = getClass().getResource(mainFxmlName);
        ResourceBundle resources = ResourceBundle.getBundle(bundlePath, Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(location, resources);
        Parent root = loader.load();
        Scene scene = new Scene(root);

        setMainController(loader.getController(), stage);

        initNodes(root);

        stage.setScene(scene);
        stage.show();
    }

    private void setMainController(MainFXMLController newController, Stage stage) {
        this.mainController = newController;
        this.mainController.setStage(stage);
        this.mainController.setGuiMain(this);
    }

    void switchLocale(Locale newLocale) {
        try {
            // get new
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(mainFxmlName), ResourceBundle.getBundle(bundlePath, newLocale));
            VBox newRoot = (VBox) fxmlLoader.load();
            ObservableList newContent = newRoot.getChildren();
            // init new
            setMainController(fxmlLoader.getController(), this.stage);
            initNodes(newRoot);
            // replace the old content
            VBox currentRoot = (VBox) this.stage.getScene().getRoot();
            currentRoot.getChildren().clear();
            currentRoot.getChildren().addAll(newContent);
        } catch (IOException ex) {
        }
    }

    private void initNodes(Parent root) {
        TitledPane tp = (TitledPane) root.lookup("#titledpane");
        ReadOnlyDoubleProperty dp = tp.heightProperty();
        dp.addListener((obs, oldHeight, newHeight) -> this.stage.sizeToScene());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
