package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Aquí cargaremos el archivo visual FXML que haremos en el siguiente paso
        Parent root = FXMLLoader.load(getClass().getResource("VentanaPrincipal.fxml"));
        primaryStage.setTitle("Sistema Escolar UPVM");
        primaryStage.setScene(new Scene(root, 1024, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}