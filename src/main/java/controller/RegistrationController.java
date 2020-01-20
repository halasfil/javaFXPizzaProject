package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import service.RegistrationService;
import service.WindowService;

import java.io.IOException;

public class RegistrationController {

    private WindowService windowService;
    private RegistrationService registrationService;
    private int result;

    @FXML
    private TextField tfLogin;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private PasswordField pfPasswordRepeat;

    @FXML
    private Label lblEquation;

    @FXML
    private TextField lblResault;

    @FXML
    private Label lblInfo;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnBack;

    @FXML
    void backAction(ActionEvent event) throws IOException {
        windowService.createNewWindow("loginView", "Pizza JavaFX");
        windowService.closeWindow(btnBack);
    }

    @FXML
    void keyLoginAction(KeyEvent event) {

    }

    @FXML
    void registerAction(ActionEvent event) {
        registrationService.registration(
                tfLogin,
                pfPassword,
                pfPasswordRepeat,
                lblInfo,
                lblEquation,
                lblResault,
                result);
    }

    public void initialize() {
        //inicjalizacja logiki biznesowej
        windowService = new WindowService();
        registrationService = new RegistrationService();
        //generowanie randomowego równania
        //wypisanie rownania na lblEquation -> zwrocenie wartosci do pola result
        result = registrationService.generateRandomEquation(lblEquation);
    }
}
