package service;

//logika biznesowa rejestracji

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Role;
import model.User;
import utility.InMemoryDB;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class RegistrationService {
    //metoda do sprawdzania czy jestes robotem
    //metoda generujaca losowe rownanie
    public int generateRandomEquation(Label lblEquation) {
        int x1, x2;
        Random random = new Random();
        x1 = random.nextInt(10);
        x2 = random.nextInt(10);
        lblEquation.setText(x1 + " + " + x2 + " = ");
        return x1 + x2;
    }

    //metoda obliczajaca wynik losowego rownania
    public boolean isNotRobot(int userResult, int generatedResult) {
        return userResult == generatedResult;
    }

    public boolean isLoginCorrect(String login) {
        return login.length() >= 6;
    }

    public boolean isLoginUnique(String login) {
        return InMemoryDB.users.stream().noneMatch(user -> user.getLogin().equals(login));
    }

    public boolean isPasswordCorrect(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[_!.,#-])[^\\s\\t]{4,}$");
    }

    public boolean isPasswordConfirmed(String password, String confirmedPassword) {
        return password.equals(confirmedPassword);
    }


    //dodawanie uzytkownika do DB
    //metoda wywolywana do rejestracji uzytkownika -> zapis w tablicy i pliku
    public void registerNewUser(String login, String password) throws IOException {
        //utwórz obiekt użytkownika
        User user = new User(
                login,
                password,
                new HashSet<>(Arrays.asList(Role.ROLE_USER)),
                LocalDateTime.now(),
                true,
                3);
        //Dodajemy obiekt do listy uzytkownikow
        InMemoryDB.users.add(user);
        //aktualizacja pliku
        FileService.updateUsers();

    }


    public void registration(TextField tfLogin,
                             PasswordField pfPassword,
                             PasswordField pfPasswordRepeat,
                             Label lblInfo,
                             Label lblEquation,
                             TextField lblResault,
                             int result) {
        try {
            if (isNotRobot(Integer.valueOf(lblResault.getText()), result)) {
                if (!isLoginCorrect(tfLogin.getText())) {
                    lblInfo.setText("Błędny login. Musi mieć minimum 6 znaków!");
                } else if (!isLoginUnique(tfLogin.getText())) {
                    lblInfo.setText("Taki login jest już zajęty");
                } else if (!isPasswordCorrect(pfPassword.getText())) {
                    lblInfo.setText("Błędne hasło. Musi zawierać min. jedną wielką, jedną małą literę, cyfrę i znak specjalny");
                } else if (!isPasswordConfirmed(pfPassword.getText(), pfPasswordRepeat.getText())) {
                    lblInfo.setText("Błędnie powtórzone hasło.");
                } else {
                    registerNewUser(tfLogin.getText(), pfPassword.getText());
                    lblInfo.setText("Zarejestrowano użytkownika");
                }
            } else {
                lblInfo.setText("Zły wynik");
                //losowanie nowego rownania
                result = generateRandomEquation(lblEquation);
                lblResault.clear();
            }
        } catch (Exception e) {
            lblInfo.setText("Niepoprawna rejestracja");
        }

    }
}
