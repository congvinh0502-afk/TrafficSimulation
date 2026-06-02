package view.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controller.MenuController;

import java.io.IOException;

/**
 * Màn hình menu — load từ menu.fxml (Scene Builder compatible).
 */
public class MenuScene {

    private final Scene scene;

    public MenuScene(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            MenuController ctrl = loader.getController();
            ctrl.setPrimaryStage(primaryStage);

            scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css") != null
                            ? getClass().getResource("/css/style.css").toExternalForm()
                            : "");
        } catch (IOException e) {
            throw new RuntimeException("Không load được menu.fxml: " + e.getMessage(), e);
        }
    }

    public Scene getScene() { return scene; }
}
