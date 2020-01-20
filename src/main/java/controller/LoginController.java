package controller;
//klasa obslugujaca zadania uzytkownika
//mapowanie zadan uzytkownika na logike programu

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import service.LoginService;
import service.WindowService;

import java.io.IOException;


public class LoginController {
//obiekty globalne

    private LoginService loginService;
    private WindowService windowService;

    @FXML
    private TextField tfLogin;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblInfo;

    @FXML
    void loginAction(ActionEvent event) {

        loginService.login(tfLogin, pfPassword, lblInfo);
    }

    @FXML
    void registerAction(ActionEvent event) throws IOException {
        //otwarcie nowego okna rejestracji
        windowService.createNewWindow("registrationView", "Rejestracja");
        //zamkniecie okna logowania
        windowService.closeWindow(lblInfo);   //podajemy dowolna kontrolke znajdujaca sie w zamykanym oknie w argumencie
    }

    //obsluga klawisza enter do logowania
    @FXML
    void keyLoginAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            loginService.login(tfLogin, pfPassword, lblInfo);
        }

    }

    //metoda "własna" która nie jest wywoływana z widoku FXML


    //metoda ktora zostanie zastosowana jako pierwsza po wyswietleniu widoku loginView
    public void initialize() {
        //inicjalizacja logiki biznesowej
        loginService = new LoginService();
        windowService = new WindowService();
    }
}
