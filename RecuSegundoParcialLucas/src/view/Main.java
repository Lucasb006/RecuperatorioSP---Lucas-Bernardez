package view;

import controladores.ControladorProductos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewfxml/ProductView.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Gestión de Productos de Limpieza");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            Object controllerObj = loader.getController();
            if (controllerObj instanceof ControladorProductos) {
                ((ControladorProductos) controllerObj).saveData();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}