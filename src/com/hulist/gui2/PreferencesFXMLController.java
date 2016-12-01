/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui2;

import com.hulist.logic.RunSettings;
import com.hulist.util.Misc;
import com.hulist.util.StaticSettings;
import com.hulist.util.UserPreferences;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @FXML
    private CheckBox checkBoxStatSignificance;
    @FXML
    private CheckBox checkBoxBootstrapSampling;
    @FXML
    private TextField textFieldSignifLevelAlpha;
    @FXML
    private CheckBox checkBoxTwoTailed;
    @FXML
    private CheckBox checkBoxRunCorrelation;
    @FXML
    private CheckBox checkBoxAllRows;
    @FXML
    private TextField textFieldBootstrapSamples;
    @FXML
    private TextField textFieldHowManyRows;

    public static final String PREFS_FXML_NAME = "PreferencesFXML.fxml";

    private GUIMain guiMain;
    private MainFXMLController mainController;

    Logger log = LoggerFactory.getLogger(PreferencesFXMLController.class);

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkBoxStatSignificance.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            UserPreferences.getInstance().getPrefs().putBoolean(checkBoxStatSignificance.getId(), newValue);
            textFieldSignifLevelAlpha.setDisable(!checkBoxStatSignificance.isSelected());
            checkBoxTwoTailed.setDisable(!checkBoxStatSignificance.isSelected());
        });
        textFieldSignifLevelAlpha.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                try {
                    double newAlpha = Double.parseDouble(textFieldSignifLevelAlpha.getText());
                    if (newAlpha < 0 || newAlpha > 1) {
                        log.warn(Misc.getInternationalized("Alfa nie jest w zakresie"));
                    } else {
                        UserPreferences.getInstance().getPrefs().put(textFieldSignifLevelAlpha.getId(), textFieldSignifLevelAlpha.getText());
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    log.warn(Misc.getInternationalized("Alfa nie jest liczbą"));
                    log.debug(Misc.stackTraceToString(e));
                }
                textFieldSignifLevelAlpha.setText(UserPreferences.getInstance().getPrefs().get(textFieldSignifLevelAlpha.getId(), "0.05"));
            }
        });
        checkBoxTwoTailed.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            UserPreferences.getInstance().getPrefs().putBoolean(checkBoxTwoTailed.getId(), newValue);
        });
        checkBoxRunCorrelation.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            UserPreferences.getInstance().getPrefs().putBoolean(checkBoxRunCorrelation.getId(), newValue);
            sliderCorrWindow.setDisable(!checkBoxRunCorrelation.isSelected());
        });
        this.labelCorrWindowVal.setText(String.format("%d", (int) this.sliderCorrWindow.getValue()));
        this.sliderCorrWindow.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.intValue() % 2 == 1) {
                labelCorrWindowVal.setText(String.format("%d", newValue.intValue()));
            } else {
                labelCorrWindowVal.setText(String.format("%d", newValue.intValue() + 1));
            }
            UserPreferences.getInstance().getPrefs().putInt(sliderCorrWindow.getId(), newValue.intValue());
        });
        checkBoxBootstrapSampling.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            UserPreferences.getInstance().getPrefs().putBoolean(checkBoxBootstrapSampling.getId(), newValue);
            textFieldBootstrapSamples.setDisable(!checkBoxBootstrapSampling.isSelected());
        });
        textFieldBootstrapSamples.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                try {
                    int newBootstrap = Integer.parseInt(textFieldBootstrapSamples.getText());
                    if (newBootstrap < 0) {
                        log.warn(Misc.getInternationalized("Wartość bootstrap jest ujemna"));
                    } else {
                        UserPreferences.getInstance().getPrefs().put(textFieldBootstrapSamples.getId(), textFieldBootstrapSamples.getText());
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    log.warn(Misc.getInternationalized("Wartość bootstrap nie jest liczbą całkowitą"));
                    log.debug(Misc.stackTraceToString(e));
                }
                textFieldBootstrapSamples.setText(UserPreferences.getInstance().getPrefs().get(textFieldBootstrapSamples.getId(), "10000"));
            }
        });
        checkBoxAllRows.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            UserPreferences.getInstance().getPrefs().putBoolean(checkBoxAllRows.getId(), newValue);
            textFieldHowManyRows.setDisable(newValue);
        });
        textFieldHowManyRows.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                try {
                    int newHowManyRows = Integer.parseInt(textFieldHowManyRows.getText());
                    if (newHowManyRows < 1) {
                        log.warn(Misc.getInternationalized("Liczba wierszy do zapisania jest ujemna!"));
                    } else {
                        UserPreferences.getInstance().getPrefs().put(textFieldHowManyRows.getId(), textFieldHowManyRows.getText());
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    log.warn(Misc.getInternationalized("Liczba wierszy do zapisania nie jest liczba calkowita"));
                    log.debug(Misc.stackTraceToString(e));
                }
                textFieldHowManyRows.setText(UserPreferences.getInstance().getPrefs().get(textFieldHowManyRows.getId(), "100"));
            }
        });

        setValuesFromPrefs();
    }

    void switchLocale(Locale newLocale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PREFS_FXML_NAME), ResourceBundle.getBundle(GUIMain.BUNDLE, newLocale));
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

    public RunSettings getRunSettings() {
        return new RunSettings(checkBoxStatSignificance.isSelected(),
                checkBoxTwoTailed.isSelected(),
                Double.parseDouble(textFieldSignifLevelAlpha.getText()),
                checkBoxRunCorrelation.isSelected(),
                (int) sliderCorrWindow.getValue(),
                checkBoxBootstrapSampling.isSelected(),
                Integer.parseInt(textFieldBootstrapSamples.getText()),
                Integer.parseInt(textFieldHowManyRows.getText()),
                checkBoxAllRows.isSelected()
        );
    }

    private void setValuesFromPrefs() {
        checkBoxStatSignificance.setSelected(UserPreferences.getInstance().getPrefs().getBoolean(checkBoxStatSignificance.getId(), true));
        textFieldSignifLevelAlpha.setText(UserPreferences.getInstance().getPrefs().get(textFieldSignifLevelAlpha.getId(), "0.05"));
        textFieldSignifLevelAlpha.setDisable(!checkBoxStatSignificance.isSelected());
        checkBoxTwoTailed.setSelected(UserPreferences.getInstance().getPrefs().getBoolean(checkBoxTwoTailed.getId(), true));
        checkBoxTwoTailed.setDisable(!checkBoxStatSignificance.isSelected());
        checkBoxRunCorrelation.setSelected(UserPreferences.getInstance().getPrefs().getBoolean(checkBoxRunCorrelation.getId(), false));
        sliderCorrWindow.setValue(UserPreferences.getInstance().getPrefs().getInt(sliderCorrWindow.getId(), 5));
        sliderCorrWindow.setDisable(!checkBoxRunCorrelation.isSelected());
        checkBoxBootstrapSampling.setSelected(UserPreferences.getInstance().getPrefs().getBoolean(checkBoxBootstrapSampling.getId(), false));
        textFieldBootstrapSamples.setText(UserPreferences.getInstance().getPrefs().get(textFieldBootstrapSamples.getId(), "10000"));
        textFieldBootstrapSamples.setDisable(!checkBoxBootstrapSampling.isSelected());
        textFieldHowManyRows.setText(UserPreferences.getInstance().getPrefs().get(textFieldHowManyRows.getId(), "100"));
    }
}
