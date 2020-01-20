package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.PizzaList;
import service.PizzaPortalService;
import service.WindowService;
import utility.InMemoryDB;

import java.io.IOException;

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
    private TableColumn<PizzaList, Double> tcPrize;

    @FXML
    private TableColumn<PizzaList, Integer> tcQuantity;
//---------------------
    @FXML
    private Label lblSum;

    @FXML
    private Label lblPizzaDay;

    @FXML
    void addToBasketAction(ActionEvent event) {

    }

    @FXML
    void clearAction(ActionEvent event) {

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
    }

    @FXML
    void selectPizzaAction(MouseEvent event) {
        pizzaPortalService.selectPizza(tblPizza);
        pizzas.clear();
        pizzas.addAll(pizzaLists);
        //wyczyszczenie tabelk
        tblPizza.setItems(pizzas);      //aktualizacja tabelki
    }

    public void initialize(){
        pizzaPortalService = new PizzaPortalService();
        //mapowanie enuma na PizzaList
        PizzaPortalService.mapPizzaToPizzaList();
        pizzas.addAll(pizzaLists);
        windowService = new WindowService();
        //konfigujarcja wartosci wprowadzanych
        tcName.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("name"));
        tcIngredients.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("ingredients"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<PizzaList, String>("description"));
        tcPrize.setCellValueFactory(new PropertyValueFactory<PizzaList, Double>("prize"));
        tcQuantity.setCellValueFactory(new PropertyValueFactory<PizzaList, Integer>("quantity"));
        //wprowadzanie wartości do tbl
        tblPizza.setItems(pizzas);

    }
}
