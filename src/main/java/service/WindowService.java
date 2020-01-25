package service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

//klasa do obslugi okien aplikacji
public class WindowService {
    //metoda do otwierania nowego okna aplikacji
    public void createNewWindow(String viewName, String title) throws IOException {
        Stage primaryStage = new Stage();       //utworzenie obiektu okna
        Parent root = FXMLLoader.load(getClass().getResource("/view/" + viewName + ".fxml"));
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);       //blokowanie zmiany rozmiaru okna
        primaryStage.show();
    }
    //metoda do zamykania aktualnie otwartego okna
    public void closeWindow(Node node){
        //Node klasa po której dziedziczą wszystkie kontrolki
        Stage stageToClose = (Stage) node.getScene().getWindow();
        stageToClose.close();
    }

    //uniwersalna metoda do tworzenia okna
    public static void getAlertWindow (Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

}
