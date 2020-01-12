package service;

import model.User;
import utility.InMemoryDB;

import java.util.Optional;

//klasa implementujaca logike biznesowa aplikacji
public class LoginService {

    public Optional<User> loginUser(String login, String password) {
        return InMemoryDB.users.stream()                            //stream
                .filter(user -> user.getLogin().equals(login)       //logowanie login
                        && user.getPassword().equals(password))     //logowanie password
                .findAny();                                         //optional
    }

    //metoda do blokowania statusu użytkownika
    public void decrementingProbes(String login) {
        //wyszukanie użytkownika po loginie

        Optional<User> userOpt = InMemoryDB.users.stream()
                .filter(user -> user.getLogin().equals(login)).findAny();
        if (userOpt.isPresent()) {
            userOpt.get().setProbes(userOpt.get().getProbes() - 1); // zmniejsza o 1 ilość prób do logowania
        }
        if (userOpt.isPresent()) {
            if (userOpt.get().getProbes() == 0) {
                System.out.println("Zablokowano konto");
                userOpt.get().setStatus(false);
            }
        }
    }

    //metoda restartująca ilosć prób
    public void clearLoginProbes(User user) {
        user.setProbes(3);
    }
    //metoda zwracajaca pozostala ilosc prob logowania lub nic w przypadku loginu ktory nie istnieje w naszej bazie

    public String getLoginProbes(String login) {
        Optional<User> userOpt = InMemoryDB.users.stream().filter(user -> user.getLogin().equals(login))
                .findAny();
        if (userOpt.isPresent()) {
            if (userOpt.get().getProbes() > 1) {
                return "Błąd logowania\nPozostało: " + userOpt.get().getProbes() + " próby logowania.";
            } else if (userOpt.get().getProbes() == 1) {
                return "Błąd logowania\nPozostała ostatnia próba logowania.";
            } else {
                return "Twoje konto jest zablokowane.";
            }
        }
        return "Błędny login";
    }

}