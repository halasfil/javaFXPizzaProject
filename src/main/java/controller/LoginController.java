package controller;
//klasa obslugujaca zadania uzytkownika
//mapowanie zadan uzytkownika na logike programu

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;
import service.LoginService;

import java.util.Optional;
import java.util.regex.Pattern;

public class LoginController {
//obiekty globalne

    LoginService loginService;

    @FXML
    private TextField tfLogin;

    @FXML
    private TextField pfPassword;

    @FXML
    private Label lblInfo;

    @FXML
    void loginAction(ActionEvent event) {
        Optional<User> userOPT = loginService.loginUser(tfLogin.getText(), pfPassword.getText());
        if (userOPT.isPresent()) {
            if (userOPT.get().getProbes() > 0) {
                lblInfo.setText("Zalogowano");
                loginService.clearLoginProbes(userOPT.get());
            } else {
                lblInfo.setText("Konto jest zablokowane");
            }
        } else {
            loginService.decrementingProbes(tfLogin.getText());
            lblInfo.setText(loginService.getLoginProbes(tfLogin.getText()));
        }
    }

    @FXML
    void registerAction(ActionEvent event) {

    }

    //metoda ktora zostanie zastosowana jako pierwsza po wyswietleniu widoku loginView
    public void initialize() {
        //inicjalizacja logiki biznesowej
        loginService = new LoginService();
    }
}
