package view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.SimulationConfig;
import model.intersection.IntersectionType;
import util.TrafficDensity;
import view.scene.SimulationScene;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình menu (menu.fxml).
 *
 * <p>Mọi field {@code @FXML} tương ứng với {@code fx:id} trong file FXML
 * và có thể chỉnh sửa layout trực tiếp trong Scene Builder.</p>
 */
public class MenuController implements Initializable {

    // ---- Khai báo phải khớp fx:id trong menu.fxml ----
    @FXML private ComboBox<String> intersectionBox;
    @FXML private ComboBox<String> modeBox;
    @FXML private ComboBox<String> lightBox;
    @FXML private Slider           vehicleSlider;
    @FXML private Label            vehicleCountLabel;
    @FXML private ComboBox<String> densityBox;
    @FXML private Button           startButton;

    /** Stage chính — được truyền vào từ Main trước khi hiển thị. */
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Điền giá trị cho ComboBox
        intersectionBox.getItems().addAll("Three Way", "Four Way", "Five Way");
        intersectionBox.setValue("Four Way");

        modeBox.getItems().addAll("AUTO", "MANUAL");
        modeBox.setValue("AUTO");

        lightBox.getItems().addAll("NO COUNTDOWN", "ALWAYS COUNTDOWN", "COUNT <= 10");
        lightBox.setValue("NO COUNTDOWN");

        densityBox.getItems().addAll("LOW", "MEDIUM", "HIGH");
        densityBox.setValue("MEDIUM");

        vehicleSlider.setMin(1);
        vehicleSlider.setMax(100);
        vehicleSlider.setValue(20);
        vehicleSlider.setMajorTickUnit(10);
        vehicleSlider.setShowTickLabels(true);
        vehicleSlider.setShowTickMarks(true);

        // Cập nhật label khi kéo slider
        vehicleCountLabel.setText("20");
        vehicleSlider.valueProperty().addListener((obs, old, val) ->
                vehicleCountLabel.setText(String.valueOf(val.intValue())));

        startButton.setOnAction(e -> startSimulation());
    }

    @FXML
    private void startSimulation() {
        SimulationConfig config = buildConfig();
        SimulationScene sim = new SimulationScene(primaryStage, config);
        primaryStage.setScene(sim.getScene());
    }

    private SimulationConfig buildConfig() {
        return new SimulationConfig(
                parseIntersection(),
                modeBox.getValue(),
                lightBox.getValue(),
                (int) vehicleSlider.getValue(),
                parseDensity()
        );
    }

    private IntersectionType parseIntersection() {
        switch (intersectionBox.getValue()) {
            case "Three Way": return IntersectionType.THREE_WAY;
            case "Five Way":  return IntersectionType.FIVE_WAY;
            default:          return IntersectionType.FOUR_WAY;
        }
    }

    private TrafficDensity parseDensity() {
        switch (densityBox.getValue()) {
            case "LOW":  return TrafficDensity.LOW;
            case "HIGH": return TrafficDensity.HIGH;
            default:     return TrafficDensity.MEDIUM;
        }
    }
}
