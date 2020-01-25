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
import model.Status;
import service.LoginService;
import service.PizzaPortalService;
import service.WindowService;
import utility.InMemoryDB;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static utility.InMemoryDB.pizzaLists;

public class PizzaPortalController {

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



    private void clearOrder() {
        List<PizzaList> pizzaLists = pizzaPortalService.clearPizzaOrder();
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        // wyczyszczenie tabelki
        tblPizza.setItems(pizzas);    // aktualizacja tabelki
        lblSum.setText("do zapłaty: 0.00 zł");
    }

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
        } else {
            WindowService.getAlertWindow(Alert.AlertType.WARNING,
                    "Dodawanie do koszyka",
                    "Nie wybrano produktu",
                    "Musisz wybrać jakiś produkt aby zrealizować zamówienia");

        }


    }

    @FXML
    void clearAction(ActionEvent event) {
        clearOrder();
    }

    @FXML
    void exitAction(ActionEvent event) {
        //zamkniecie aplikacji
        Platform.exit();
    }

    @FXML
    void logoutAction(ActionEvent event) throws IOException {
        //otwarcie nowego okna
        windowService.createNewWindow("loginView", "Pizza JavaFX");
        //zamkniecie aktualnego okna
        windowService.closeWindow(lblLogin);
        clearOrder();
    }

    @FXML
    void selectPizzaAction(MouseEvent event) {
        pizzaPortalService.selectPizza(tblPizza);
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        //wyczyszczenie tabelk
        tblPizza.setItems(pizzas);      //aktualizacja tabelki
    }

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

    @FXML
    void showDetailsAction(MouseEvent event) {
        //zaznaczamy rekord w tabelce i pobieramy z niego obiekt klasy Basket
        Basket basket = tblBasket.getSelectionModel().getSelectedItem();
        //wypisanie szczegolowych informacji dot zaznaczonego koszyka
        ObservableList<String> detailBasket = FXCollections.observableArrayList();
        detailBasket.add("STATUS: " + basket.getStatus().getStatusName());
        for (String name : basket.getOrder().keySet()){
            detailBasket.add("Pizza " + name + " : " + basket.getOrder().get(name) + " szt.");
        }
        lvBasket.setItems(detailBasket);
        //aktualizacja kwoty do zaplaty
        lblBasketAmount.setText(String.format( "Suma: %.2f PLN", basket.getBasketAmount()));
    }

    //metoda wprowadajaca dane z pliku baskets.csv do koszyka ale dotyczaca jedynie zalogowanego uzytkownika
    private List<Basket> getUserBasket(String login){
        List<Basket> userBaskets = InMemoryDB.baskets.stream()
                .filter(basket -> basket.getUserLogin().equals(login))
                .sorted(Comparator.comparing(Basket::getStatus))
                .collect(Collectors.toList());
        return userBaskets;
    }

    //metoda do konfiguracji kolumn w tblBasket i wprowadzenia danych
    private void addDataToBasketsTable(){
        // konfiguracja wartości przekazywanych do kolumn z modelu
        tcBasket.setCellValueFactory(new PropertyValueFactory<>("order"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        //formatowanie zawartosci tabeli
        tcBasket.setCellFactory(order -> new TableCell<Basket, Map>(){
            @Override
            protected void updateItem(Map basket, boolean empty){
                super.updateItem(basket, empty);
                if (empty){
                    setText(null);
                } else {
                    setText(basket.toString()
                            .replace("{", "")
                            .replace("}","")
                            .replace("=", " x ")
                    );
                }
            }
        });

        tcStatus.setCellFactory(order -> new TableCell<Basket, Status>(){
            @Override
            protected void updateItem(Status status, boolean empty){
                super.updateItem(status, empty);
                if (empty){
                    setText(null);
                } else {
                    setText(status.getStatusName());
                }
            }
        });
        // wprowadzenie danych do tabeli z listy baskets
        tblBasket.setItems(FXCollections.observableArrayList(
                getUserBasket(LoginService.loggedUser.getLogin())
        ));

    }
    //-------------------------------------------------/tab2
    //-------------------------TAB3------------


    //TAB3 ----------------TAB3

    private PizzaList pizzaOfDay;

    public void initialize() {
        lblLogin.setText("Zalogowano: " + LoginService.loggedUser.getLogin());

        pizzaPortalService = new PizzaPortalService();  // nowa instancja klasy PPS
        // mapowanie enuma do PizzaList
        PizzaPortalService.mapPizzaToPizzaList();
        pizzas.addAll(InMemoryDB.pizzaLists);
        windowService = new WindowService();
        // generowanie pizzy dnia aktualizacja ceny i wypisanie na lbl
        pizzaOfDay = pizzaPortalService.generatePizzaOfDay();
        List<PizzaList> pizzaLists = pizzaPortalService.setDiscount(pizzaOfDay, 30);
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        lblPizzaDay.setText("PIZZA DNIA TO " + pizzaOfDay.getName().toUpperCase() + "!");
        // konfiguracja wartości wprowadzanych do kolumn tblPizza
        tcName.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("name"));
        tcIngredients.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("ingredients"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("description"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<PizzaList, Double>("price"));
        tcQuantity.setCellValueFactory(new PropertyValueFactory<PizzaList, Integer>("quantity"));
        // formatowanie do typu NumberFormat
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
        //---------------------------

    }
}
