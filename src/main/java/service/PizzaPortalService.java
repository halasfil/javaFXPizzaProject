package service;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import model.Ingredient;
import model.Pizza;
import model.PizzaList;
import utility.InMemoryDB;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PizzaPortalService {
    //metoda mapujaca enum Pizza na liste PizzaList

    public static void mapPizzaToPizzaList() {
        List<PizzaList> pizzas = Arrays.stream(Pizza.values())
                .map(pizza -> new PizzaList(
                        pizza.getName(),                                                                                //pobranie nazw
                        pizza.getIngredients().stream()
                                .map(Ingredient::getName).collect(Collectors.joining(",")),                    //pobranie skladikow
                        (pizza.getIngredients().stream().anyMatch(Ingredient::isSpicy) ? "ostra" : "")
                                + (pizza.getIngredients().stream().noneMatch(Ingredient::isMeat) ? "wege" : ""),        //wege i ostra
                        pizza.getIngredients().stream().mapToDouble(Ingredient::getPrice).sum(),                        //obliczenie ceny
                        0
                ))
                .collect(Collectors.toList());
        InMemoryDB.pizzaLists.addAll(pizzas);
    }

    //metoda wyboru pizzy w tableview
    public List<PizzaList> selectPizza(TableView tblPizza) {
        PizzaList selectedPizza = (PizzaList) tblPizza.getSelectionModel().getSelectedItem();
        if (selectedPizza != null) {
            TextInputDialog dialog = new TextInputDialog(selectedPizza.getQuantity().toString());
            dialog.setTitle("Wybierz ilość");
            dialog.setHeaderText("Podaj ilość zamawianego produktu");
            dialog.setContentText("Aby zamówić określoną ilość produktu należy ją wprowadzić do pola tekstowego");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                System.out.println("Wprowadź ilość: " + result.get());
            }
            //jesli wprowadzimy wartosc to zaktualizujemy ja w liscie pizz
            result.ifPresent(quantity -> InMemoryDB.pizzaLists.stream()
                    .filter(pizzaList -> pizzaList.equals(selectedPizza))
                    .forEach(pizzaList -> pizzaList.setQuantity(Integer.valueOf(result.get()))
                    ));
        }
        return InMemoryDB.pizzaLists;
    }
}



