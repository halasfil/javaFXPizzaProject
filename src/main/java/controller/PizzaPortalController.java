package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Basket;
import model.PizzaList;
import model.Role;
import model.Status;
import service.FileService;
import service.LoginService;
import service.PizzaPortalService;
import service.WindowService;
import utility.InMemoryDB;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static utility.InMemoryDB.pizzaLists;

public class PizzaPortalController {
//tab1-----------------------
    //obiekty globalne
    private WindowService windowService;
    private PizzaPortalService pizzaPortalService;
    //przechowuje liste pizz
    private ObservableList pizzas = FXCollections.observableArrayList();

    @FXML
    private Label lblLogin;

    @FXML
    private Tab tabMenu;
    //---------------Lista pizz
    @FXML
    private TableView<PizzaList> tblPizza;

    @FXML
    private TableColumn<PizzaList, String> tcName;

    @FXML
    private TableColumn<PizzaList, String> tcIngredients;

    @FXML
    private TableColumn<PizzaList, String> tcDescription;

    @FXML
    private TableColumn<PizzaList, Double> tcPrice;

    @FXML
    private TableColumn<PizzaList, Integer> tcQuantity;
    //---------------------
    @FXML
    private Label lblSum;

    @FXML
    private Label lblPizzaDay;

    //------------------------------------------------tab2
    @FXML
    private TableView<Basket> tblBasket;

    @FXML
    private TableColumn<Basket, Map> tcBasket;

    @FXML
    private TableColumn<Basket, Status> tcStatus;

    @FXML
    private ListView<String> lvBasket;

    @FXML
    private Label lblBasketAmount;

    //-------------------------TAB3------------
    @FXML
    private Tab tabBasketStatus;
    @FXML
    private TableView<Basket> tblOrders;
    @FXML
    private TableColumn<Basket, String> tcLogin;
    @FXML
    private TableColumn<Basket, Map> tcOrder;
    @FXML
    private TableColumn<Basket, Status> tcOrderStatus;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private Spinner<Integer> sTime;
    @FXML
    private CheckBox cInProgress;
    @FXML
    private CheckBox cNew;

    @FXML
    private Button btnConfirmButton;
    private PizzaList pizzaOfDay;

    @FXML
    private Tab tabBasket;

    //ok
    private void clearOrder() {
        List<PizzaList> pizzaLists = pizzaPortalService.clearPizzaOrder();
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        // wyczyszczenie tabelki
        tblPizza.setItems(pizzas);    // aktualizacja tabelki
        lblSum.setText("do zapłaty: 0.00 zł");
    }

    //ok
    @FXML
    void addToBasketAction(ActionEvent event) throws IOException {
        if (pizzaPortalService.calculatePizzaOrder() > 0) {
            pizzaPortalService.addOrderToBasket(LoginService.loggedUser.getLogin());
            clearOrder();
            WindowService.getAlertWindow(Alert.AlertType.INFORMATION,
                    "Dodawanie do koszyka",
                    "Złożono zamówienie",
                    "Dziękujemy za złożenie zamówienia");
            addDataToBasketsTable();
            addDataToOrderTable();
        } else {
            WindowService.getAlertWindow(Alert.AlertType.WARNING,
                    "Dodawanie do koszyka",
                    "Nie wybrano produktu",
                    "Musisz wybrać jakiś produkt aby zrealizować zamówienia");

        }


    }
//ok
    @FXML
    void clearAction(ActionEvent event) {
        clearOrder();
    }
//ok
    @FXML
    void exitAction(ActionEvent event) {
        //zamkniecie aplikacji
        Platform.exit();
    }

    //ok
    @FXML
    void logoutAction(ActionEvent event) throws IOException {
        //otwarcie nowego okna
        windowService.createNewWindow("loginView", "Pizza JavaFX");
        //zamkniecie aktualnego okna
        windowService.closeWindow(lblLogin);
        clearOrder();
    }

    //ok
    @FXML
    void selectPizzaAction(MouseEvent event) {
        List<PizzaList> pizzaLists = pizzaPortalService.selectPizza(tblPizza);
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        //wyczyszczenie tabelk
        tblPizza.setItems(pizzas);      //aktualizacja tabelki
        lblSum.setText(String.format("do zapłaty: %.2f zł", pizzaPortalService.calculatePizzaOrder()));
    }


//tab2------------------------------
    //ok
    @FXML
    void showDetailsAction(MouseEvent event) {
        //zaznaczamy rekord w tabelce i pobieramy z niego obiekt klasy Basket
        Basket basket = tblBasket.getSelectionModel().getSelectedItem();
        //wypisanie szczegolowych informacji dot zaznaczonego koszyka
        ObservableList<String> detailBasket = FXCollections.observableArrayList();
        detailBasket.add("STATUS: " + basket.getStatus().getStatusName());
        for (String name : basket.getOrder().keySet()) {
            detailBasket.add("Pizza " + name + " : " + basket.getOrder().get(name) + " szt.");
        }
        lvBasket.setItems(detailBasket);
        //aktualizacja kwoty do zaplaty
        lblBasketAmount.setText(String.format("Suma: %.2f PLN", basket.getBasketAmount()));
    }

    //metoda wprowadajaca dane z pliku baskets.csv do koszyka ale dotyczaca jedynie zalogowanego uzytkownika
    //ok
    private List<Basket> getUserBasket(String login) {
        List<Basket> userBaskets = InMemoryDB.baskets.stream()
                .filter(basket -> basket.getUserLogin().equals(login))
                .sorted(Comparator.comparing(Basket::getStatus))
                .collect(Collectors.toList());
        return userBaskets;
    }

    //metoda do konfiguracji kolumn w tblBasket i wprowadzenia danych
    //ok
    private void addDataToBasketsTable() {
        // konfiguracja wartości przekazywanych do kolumn z modelu
        tcLogin.setCellValueFactory(new PropertyValueFactory<>("userLogin"));
        tcBasket.setCellValueFactory(new PropertyValueFactory<>("order"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        //formatowanie zawartosci tabeli
        tcBasket.setCellFactory(order -> new TableCell<Basket, Map>() {
            @Override
            protected void updateItem(Map basket, boolean empty) {
                super.updateItem(basket, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(basket.toString()
                            .replace("{", "")
                            .replace("}", "")
                            .replace("=", " x ")
                    );
                }
            }
        });

        tcStatus.setCellFactory(order -> new TableCell<Basket, Status>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(status.getStatusName());
                }
            }
        });
        // wprowadzenie danych do tabeli z listy baskets
        tblOrders.setItems(FXCollections.observableArrayList(InMemoryDB.baskets));


    }



//tab3-------------------------
    //metoda dodajaca dane do tabelki
    //OK
    private void addDataToOrderTable() {
        //konfiguracja kolumn
        tcLogin.setCellValueFactory(new PropertyValueFactory<>("userLogin"));
        tcOrder.setCellValueFactory(new PropertyValueFactory<>("order"));
        tcOrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        //edycja wyswietlanej wartosci
        tcOrder.setCellFactory(order -> new TableCell<Basket, Map>() {
            @Override
            protected void updateItem(Map basket, boolean empty) {
                super.updateItem(basket, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(basket.toString()
                            .replace("{", "")
                            .replace("}", "")
                            .replace("=", " x ")
                    );
                }

            }
        });

        tcOrderStatus.setCellFactory(order -> new TableCell<Basket, Status>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(status.getStatusName());
                }
            }
        });
        //dodanie koszyka bez statusu "dostarczone"
        tblOrders.setItems(FXCollections.observableArrayList(InMemoryDB.baskets.stream()
                .filter(basket1 -> !basket1.getStatus().equals(Status.DONE))
                .collect(Collectors.toList())));
    }

    private Basket basket;

    //ok
    @FXML
    void confirmStatusAction(ActionEvent event) throws IOException {
        //pobranie statusu z listy rozwijanej
        String statusName = cbStatus.getValue();
        //zmiana statusu wybranego obiektu na wybrany z listy rozwijanej
        InMemoryDB.baskets.stream().forEach(basket1 ->
        {
            if (basket1.equals(basket)) {
                basket1.setStatus(Arrays.stream(Status.values())
                        .filter(status -> status.getStatusName().equals(statusName))
                        .findAny().get());
            }
        });
        //okno alertowe potwierdzajace zmiane statusu
        WindowService.getAlertWindow(Alert.AlertType.INFORMATION,
                "Zmiana statusu zamówienia",
                "Zmieniono status zamówienia",
                "Aktualny status zamówienia: " + statusName);
        //aktualizacja tabelki
        addDataToOrderTable();
        addDataToBasketsTable();
        //aktualizacja pliku
        FileService.updateBasket();

    }

    //ok
    private void selectCheckBox() {
        List<Basket> filteredBaskets = InMemoryDB.baskets.stream()
                .filter(b -> !b.getStatus().equals(Status.DONE))
                .collect(Collectors.toList());
        if (cInProgress.isSelected() && cNew.isSelected()) {
            List<Basket> newOrders = filteredBaskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.NEW)
                            || basket.getStatus().equals(Status.IN_PROGRESS))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        } else if (cInProgress.isSelected()) {
            List<Basket> newOrders = filteredBaskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.IN_PROGRESS))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        } else if (cNew.isSelected()) {
            List<Basket> newOrders = filteredBaskets.stream()
                    .filter(basket -> basket.getStatus().equals(Status.NEW))
                    .collect(Collectors.toList());
            tblOrders.setItems(FXCollections.observableArrayList(newOrders));
        } else {
            tblOrders.setItems(FXCollections.observableArrayList(filteredBaskets));
        }

    }

    @FXML
    void selectInProcessAction(ActionEvent event) {
        selectCheckBox();
    }

    @FXML
    void selectNewAction(ActionEvent event) {
        selectCheckBox();
    }

    //ok
    @FXML
    void selectOrderAction(MouseEvent event) {
        basket = tblOrders.getSelectionModel().getSelectedItem();
        if (basket != null) {
            cbStatus.setDisable(false);
            btnConfirmButton.setDisable(false);
            //pobranie statusu i ustawienie go na combobox
            Status status = basket.getStatus();
            cbStatus.setValue(status.getStatusName());
        } else {
            cbStatus.setDisable(true);
            btnConfirmButton.setDisable(true);
        }

    }
    //OK
    public void initialize() throws IOException, InterruptedException {

    //zarzadzanie widocznoscia w tabach w zaleznosci od uprawnien uzytkownika

    Set<Role> roles = LoginService.loggedUser.getRoles();
    if (!roles.contains(Role.ROLE_USER)){
        tabMenu.setDisable(true);
        tabBasket.setDisable(true);
    }
    if (!roles.contains(Role.ROLE_ADMIN)){
        tabBasketStatus.setDisable(true);
    }

    //wypisanie loginu zalogowanego uzytkownika

    lblLogin.setText("Zalogowano: " + LoginService.loggedUser.getLogin());
    pizzaPortalService = new PizzaPortalService();

    PizzaPortalService.mapPizzaToPizzaList();
    pizzas.clear();
    pizzas.addAll(InMemoryDB.pizzaLists);
    windowService = new WindowService();
    pizzaOfDay = pizzaPortalService.generatePizzaOfDay();
    List<PizzaList> pizzaLists = pizzaPortalService.setDiscount(pizzaOfDay, 30);
    pizzas.clear();
    pizzas.addAll(pizzaLists);
    lblPizzaDay.setText("Pizza dnia to: " + pizzaOfDay.getName().toUpperCase());

    //konfiguracja wprowadzonych do kolumn pizz w tab
        tcName.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("name"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("description"));
        tcIngredients.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("ingredients"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<PizzaList, Double>("price"));
        tcQuantity.setCellValueFactory(new PropertyValueFactory<PizzaList, Integer>("quantity"));
        //formatowanie do typu NumberFormat
        Locale locale = new Locale("pl", "PL");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        tcPrice.setCellFactory(tc -> new TableCell<PizzaList, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
        // wprowadzenie wartości do tbl
        tblPizza.setItems(pizzas);
        //TAB2  - basket-------------
        addDataToBasketsTable();
        //TAB3-------------------------
        addDataToOrderTable();
        //wprowadzenie damych do combobox
        cbStatus.setItems(FXCollections.observableArrayList(
                Arrays.stream(Status.values()).map(Status::getStatusName).collect(Collectors.toList())));

    }
}
