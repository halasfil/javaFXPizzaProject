package utility;

import model.Basket;
import model.PizzaList;
import model.User;
import java.util.ArrayList;
import java.util.List;

public class InMemoryDB {

    public static List<User> users = new ArrayList<>();
    public static List<PizzaList> pizzaLists = new ArrayList<>();
    public static List<Basket> baskets = new ArrayList<>();

}
