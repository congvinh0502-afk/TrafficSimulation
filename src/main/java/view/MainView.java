package view;

import config.Constants;
import engine.SimulationEngine;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor; // Thêm import này

public class MainView {
    private BorderPane root;
    private Canvas canvas;
    private SimulationEngine engine;
    
    // Biến lưu tọa độ chuột để tính toán Camera
    private double lastMouseX;
    private double lastMouseY;

    public MainView() {
        root = new BorderPane();
        
        canvas = new Canvas(Constants.WINDOW_WIDTH - 250, Constants.WINDOW_HEIGHT);
        root.setCenter(canvas);
        
        VBox controlPanel = new VBox();
        controlPanel.setPrefWidth(250);
        controlPanel.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15px;");
        
        Label title = new Label("BẢNG ĐIỀU KHIỂN");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        controlPanel.getChildren().add(title);
        
        root.setRight(controlPanel);

        engine = new SimulationEngine(canvas);
        
        // Gọi hàm cài đặt Camera
        setupCameraControls();
    }

    // --- HÀM MỚI: XỬ LÝ SỰ KIỆN KÉO CHUỘT ---
    private void setupCameraControls() {
        // Đổi hình trỏ chuột thành hình bàn tay để người dùng biết có thể kéo
        canvas.setCursor(Cursor.HAND);

        // 1. Khi vừa nhấn chuột xuống -> Lưu lại tọa độ bắt đầu
        canvas.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            canvas.setCursor(Cursor.CLOSED_HAND); // Hiệu ứng nắm tay lại
        });

        // 2. Khi kéo rê chuột -> Tính khoảng cách và di chuyển Camera
        canvas.setOnMouseDragged(event -> {
            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;
            
            engine.moveCamera(deltaX, deltaY);
            
            // Cập nhật lại tọa độ để lần kéo tiếp theo mượt mà
            lastMouseX = event.getX();
            lastMouseY = event.getY();
        });

        // 3. Khi nhả chuột ra -> Trả lại hình bàn tay mở
        canvas.setOnMouseReleased(event -> {
            canvas.setCursor(Cursor.HAND);
        });
    }

    public BorderPane getRoot() {
        return root;
    }

    public void startSimulation() {
        engine.start();
    }
}