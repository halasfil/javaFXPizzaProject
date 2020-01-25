package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor     //konstruktor ze wszystkimi polami w argumentach
@Data                   //getters+setters

public class PizzaList {
    private String name;
    private String ingredients;
    private String description;
    private Double price;
    private Integer quantity;


}
