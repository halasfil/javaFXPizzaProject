package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.PizzaList;
import service.PizzaPortalService;
import service.WindowService;
import utility.InMemoryDB;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

    private void clearOrder(){
        List<PizzaList> pizzaLists = pizzaPortalService.clearPizzaOrder();
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        // wyczyszczenie tabelki
        tblPizza.setItems(pizzas);    // aktualizacja tabelki
        lblSum.setText("do zapłaty: 0.00 zł");
    }

    @FXML
    void addToBasketAction(ActionEvent event) throws IOException {
        pizzaPortalService.addOrderToBasket("XXX");
        clearOrder();
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

    private PizzaList pizzaOfDay;
    public void initialize(){
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
        tcIngredients.setCellValueFactory(new PropertyValueFactory<PizzaList,String>("ingredients"));
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

    }
}
