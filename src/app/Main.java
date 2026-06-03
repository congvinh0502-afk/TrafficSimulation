package app;

import javafx.application.Application;
import javafx.stage.Stage;
import view.scene.MenuScene;

/**
 * Entry point của ứng dụng mô phỏng giao thông (JavaFX).
 *
 * <p>Khởi động JavaFX, tạo Stage chính và hiển thị màn hình menu.</p>
 *
 * <p>Compile & run với JavaFX trên module-path:
 * <pre>
 *   mvn javafx:run
 *   # hoặc
 *   java --module-path /path/to/javafx-sdk/lib \
 *        --add-modules javafx.controls,javafx.fxml \
 *        -cp target/classes app.Main
 * </pre>
 * </p>
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart City Traffic Simulation");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);

        MenuScene menuScene = new MenuScene(primaryStage);
        primaryStage.setScene(menuScene.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
