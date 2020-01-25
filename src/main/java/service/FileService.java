package service;

import model.Basket;
import model.Role;
import model.User;
import utility.InMemoryDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

//logika biznesowa do obsługi plików
public class FileService {

    private static String pathToUsers =
            Paths.get("").toAbsolutePath().toString() + "\\src\\main\\resources\\file\\users.csv";

    private static String pathToBasktets =
            Paths.get("").toAbsolutePath().toString() + "\\src\\main\\resources\\file\\baskets.csv";

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //metoda pobierająca zawartość z pliku i wprowadzajaca ja do listy users
    public static void selectUsers() throws FileNotFoundException {
        File file = new File(pathToUsers);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String user = scanner.nextLine();
            String[] userSplitBySeparator = user.split("; ");
            //wpisywanie pobranej zawartosci z pliku do listy podrecznej users
            User user1 = new User(userSplitBySeparator[0],                                                        //login
                    userSplitBySeparator[1],                                                        //password
                    new HashSet<Role>(                                                                  //konwersja do Set<Role>
                            Arrays.stream(userSplitBySeparator[2]
                                    .split(","))
                                    .map(Role::valueOf)
                                    .collect(Collectors.toSet())),
                    LocalDateTime.parse(userSplitBySeparator[3], dtf),                                   //parsowanie do LDT
                    Boolean.valueOf(userSplitBySeparator[4]),                                       //konwersja do boolean
                    Integer.valueOf(userSplitBySeparator[5]));
            InMemoryDB.users.add(user1);                                     //konwersja do Integer

        }
    }


    //metoda zapisuąca zawartość z listy users do pliku
    public static void updateUsers() throws IOException {
        FileWriter fileWriter = new FileWriter(new File(pathToUsers));

        for (User u : InMemoryDB.users) {
            fileWriter.write(String.format("%s; %s; %s; %s; %s; %d\n",
                    u.getLogin(),
                    u.getPassword(),
                    u.getRoles().stream().map(Enum::name).collect(Collectors.joining(",")),
                    u.getRegistrationDateTime().format(dtf),
                    u.isStatus(),
                    u.getProbes()));
        }
        fileWriter.close();
    }

    //metoda aktualizujaca zawartosc koszyka w oparciu o plik baskets.csv
    public static void updateBasket() throws IOException {
        FileWriter fileWriter = new FileWriter(new File(pathToBasktets));

        for (Basket basket : InMemoryDB.baskets){
            fileWriter.write(
                    String.format("%s; %s; %.2f; %s",
                            basket.getUserLogin(),
                            basket.getOrder(), // ??? formatowanie ???
                            basket.getBasketAmount(),
                            basket.getStatus()
                    ));      // przepisanie zamówień z listy baskets do pliku baskets.csv
        }
        fileWriter.close();


        }

    }



