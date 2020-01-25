package service;

import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import model.*;
import utility.InMemoryDB;


import java.io.IOException;
import java.util.*;
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
    // metoda do oblicznia ceny zamowionych pizz
    public Double calculatePizzaOrder(){
        return InMemoryDB.pizzaLists.stream()
                .mapToDouble(pizza -> pizza.getQuantity() * pizza.getPrice())
                .sum();
    }
    // czyszczenie zamówienia
    public List<PizzaList> clearPizzaOrder(){
        InMemoryDB.pizzaLists.stream().forEach(pizza -> pizza.setQuantity(0));
        return InMemoryDB.pizzaLists;
    }
    // generator pizzy dnia
    public PizzaList generatePizzaOfDay(){
        return InMemoryDB.pizzaLists.get(new Random().nextInt(InMemoryDB.pizzaLists.size()));
    }
    // przypisanie rabatu 30% na pizze dnia
    public List<PizzaList> setDiscount(PizzaList pizza, double discount){
        InMemoryDB.pizzaLists.stream()
                .filter(pizzaList -> pizzaList.equals(pizza))
                .forEach(pizzaList -> pizzaList.setPrice(pizzaList.getPrice() * (1 - (discount/100))));
        return InMemoryDB.pizzaLists;
    }
    // metoda przekazująca dane do koszyka
    // loginUsera; listaPizz -> quantity > 0; cena Dozapłaty
    public void addOrderToBasket(String userLogin) throws IOException {
        List<PizzaList> pizzaLists = InMemoryDB.pizzaLists.stream()
                .filter(pizza -> pizza.getQuantity() > 0)
                .collect(Collectors.toList());
        Double toPay = calculatePizzaOrder();
//        System.out.println(String.format("%s; %s; %.2f; %s",
//                userLogin,
//                pizzaLists.stream()
//                        .map(pizzaList -> pizzaList.getName() + " : " + pizzaList.getQuantity())
//                        .collect(Collectors.joining(", ")),
//                toPay,
//                "nowe zamówienie"
//                )
//        );

        Map<String, Integer> order = new HashMap<>();
        for (PizzaList pizzaList : pizzaLists){
            if(pizzaList.getQuantity() > 0){
                order.put(pizzaList.getName(), pizzaList.getQuantity());
            }
        }
        InMemoryDB.baskets.add(new Basket(
                userLogin,
                order,
                toPay,
                Status.IN_PROGRESS
        ));
        //aktualizacja pliku w oparciu o InMemoryDB baskets
        FileService.updateBasket();
    }
}



