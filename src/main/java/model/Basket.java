package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data

public class Basket {

    private String userLogin;
    private Map<String, Integer> order; //mapa do przechowywania danych zamowienia

    private double basketAmount;
    private Status status;
}
