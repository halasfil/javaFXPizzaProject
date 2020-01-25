import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.FileService;
import utility.InMemoryDB;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/loginView.fxml"));
        primaryStage.setTitle("Pizza JavaFX");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        //wczytanie/zaktualizowanie listy users z pliku csv
        FileService.selectUsers();                          //pobranie zawartości pliku i dodanie do listy
       // InMemoryDB.users.forEach(System.out::println);      //wypisanie listy users
       // FileService.updateUsers();
        FileService.selectBaskets();
    }


    public static void main(String[] args) {
        launch(args);
    }
}