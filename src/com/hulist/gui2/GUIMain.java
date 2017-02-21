/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import com.hulist.util.UserPreferences;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Aleksanders
 */
public class GUIMain extends Application {

    public static final String APP_NAME = "DendroCORR";
    public static final String APP_VERSION = "3.3";
    public static final int YEAR = 2017;//Calendar.getInstance().get(Calendar.YEAR);
    public static final String BUNDLE = "com.hulist.bundles.Bundle";

    private Stage mainStage;
    private static Locale currLocale = null;

    private MainFXMLController mainController;
    private PreferencesFXMLController prefsController;

    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(new Locale(UserPreferences.getInstance().getPrefs().get("locale", Locale.ENGLISH.toString())));
        currLocale = Locale.getDefault();
        this.mainStage = stage;

        URL location = getClass().getResource(MainFXMLController.MAIN_FXML_NAME);
        ResourceBundle resources = ResourceBundle.getBundle(BUNDLE, Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(location, resources);
        Parent root = loader.load();

        setMainScene(root);
  
        setMainController(loader.getController());
        mainController.initController();

        initMainStage();
        stage.sizeToScene();
        stage.show();
    }

    public void setMainController(MainFXMLController newController) {
        mainController = newController;
        mainController.setGuiMain(this);
    }

    public void setMainScene(Parent root) {
        this.mainStage.setScene(new Scene(root));
    }

    void switchLocale(Locale newLocale) {
        currLocale = newLocale;
        
        UserPreferences.getInstance().getPrefs().put("locale", getCurrLocale().toString());
        mainController.switchLocale(currLocale);
        if (prefsController != null) {
            prefsController.switchLocale(currLocale);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static Locale getCurrLocale() {
        return currLocale;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setPrefsController(PreferencesFXMLController prefsController) {
        this.prefsController = prefsController;
    }

    private void initMainStage() {
        mainStage.setTitle(APP_NAME);
        final List<Image> icons1 = new ArrayList<>();
        icons1.add(new Image(getClass().getClassLoader().getResourceAsStream("resources/32.png")));
        icons1.add(new Image(getClass().getClassLoader().getResourceAsStream("resources/48.png")));
        icons1.add(new Image(getClass().getClassLoader().getResourceAsStream("resources/64.png")));
        icons1.add(new Image(getClass().getClassLoader().getResourceAsStream("resources/96.png")));
        icons1.add(new Image(getClass().getClassLoader().getResourceAsStream("resources/128.png")));
        icons1.add(new Image(getClass().getClassLoader().getResourceAsStream("resources/450.png")));
        mainStage.getIcons().addAll(icons1);
        mainStage.setResizable(false);
    }

}
