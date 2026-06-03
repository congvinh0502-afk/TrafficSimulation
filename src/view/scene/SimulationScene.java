package view.scene;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import config.Constants;
import controller.TrafficController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import manager.SoundManager;
import manager.VehicleSpawnManager;
import model.SimulationConfig;
import model.intersection.IntersectionType;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.emergency.EmergencyVehicleSystem;
import util.Direction;
import util.TrafficDensity;
import view.renderer.EnvironmentRenderer;
import view.renderer.TrafficLightRenderer;
import view.renderer.VehicleRenderer;

/**
 * Màn hình mô phỏng (JavaFX).
 *
 * <p>Tính năng:
 * <ul>
 *   <li>AnimationTimer 60fps thay cho Swing Timer.</li>
 *   <li>Canvas + Camera (scroll = zoom, drag = pan, R = reset).</li>
 *   <li>HUD thống kê góc trên-trái.</li>
 *   <li>Nút "⚙ Settings" mở dropdown chỉnh sửa khi đang chạy.</li>
 *   <li>Nút "← Menu" quay về menu bất cứ lúc nào.</li>
 * </ul>
 * </p>
 */
public class SimulationScene {

    // --------------------------------------------------------
    // Scene + Camera
    // --------------------------------------------------------
    private final Scene  scene;
    private final Canvas canvas;
    private final Camera camera = new Camera();

    // --------------------------------------------------------
    // Dữ liệu mô phỏng
    // --------------------------------------------------------
    private SimulationConfig config;
    private final List<Vehicle> vehicles = new ArrayList<>();

    private TrafficLight verticalLight;
    private TrafficLight horizontalLight;

    private final TrafficController      trafficController;
    private final VehicleSpawnManager    spawnManager;
    private final EmergencyVehicleSystem emergencySystem;

    // --------------------------------------------------------
    // Renderer
    // --------------------------------------------------------
    private final EnvironmentRenderer  envRenderer   = new EnvironmentRenderer();
    private final TrafficLightRenderer lightRenderer = new TrafficLightRenderer();
    private final VehicleRenderer      vehicleRenderer = new VehicleRenderer();

    // --------------------------------------------------------
    // Trạng thái HUD / thống kê
    // --------------------------------------------------------
    private int    spawnCounter = 0;
    private int    fps          = 0;
    private int    frameCount   = 0;
    private long   lastFpsTime  = System.nanoTime();
    private String jamLevel     = "LOW";
    private boolean flash       = false;
    private int    flashCounter = 0;

    // --------------------------------------------------------
    // Runtime settings (dropdown)
    // --------------------------------------------------------
    private int    runtimeVehicleCount;
    private TrafficDensity runtimeDensity;

    // --------------------------------------------------------
    // UI overlay elements
    // --------------------------------------------------------
    private Label hudLabel;

    private final Stage primaryStage;
    private AnimationTimer gameTimer;
    // Thêm vào khu vực "Trạng thái HUD / thống kê"
    private boolean isPaused = false;

    // ==========================================================
    // Constructor
    // ==========================================================

    public SimulationScene(Stage primaryStage, SimulationConfig config) {
        this.primaryStage         = primaryStage;
        this.config               = config;
        this.runtimeVehicleCount  = config.getVehicleCount();
        this.runtimeDensity       = config.getTrafficDensity();

        // Khởi đèn
        verticalLight   = new TrafficLight(LightColor.GREEN, Constants.LIGHT_GREEN_DURATION);
        horizontalLight = new TrafficLight(LightColor.RED,   Constants.LIGHT_RED_DURATION);

        trafficController = new TrafficController();
        spawnManager      = new VehicleSpawnManager(vehicles);
        emergencySystem   = new EmergencyVehicleSystem();

        // Xây UI
        canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        StackPane root = buildUI();

        scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        scene.setFill(Color.GRAY);

        bindCameraEvents();
        bindKeyboard();

        spawnInitialVehicles();
        startGameLoop();
    }

    // ==========================================================
    // Xây giao diện
    // ==========================================================

    private StackPane buildUI() {
        StackPane root = new StackPane();

        // --- Tầng 1: Canvas mô phỏng ---
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        // --- Tầng 2: Overlay (HUD + controls) ---
        BorderPane overlay = new BorderPane();
        overlay.setPickOnBounds(false);

        // HUD góc trên-trái
        hudLabel = new Label();
        hudLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        hudLabel.setTextFill(Color.WHITE);
        hudLabel.setStyle("-fx-background-color: rgba(0,0,0,0.65); " +
                "-fx-background-radius: 10; -fx-padding: 12;");
        hudLabel.setMouseTransparent(true);
        BorderPane.setMargin(hudLabel, new Insets(16));
        overlay.setLeft(hudLabel);

        // Thanh nút góc trên-phải
        HBox topRight = buildTopRightBar();
        BorderPane.setMargin(topRight, new Insets(16));
        overlay.setRight(topRight);

        overlay.setPickOnBounds(false);
        root.getChildren().add(overlay);

        return root;
    }

    private HBox buildTopRightBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.TOP_RIGHT);

        // --- Nút quay về menu ---
        Button menuBtn = new Button("← Menu");
        styleControlBtn(menuBtn, "#C62828");
        menuBtn.setOnAction(e -> returnToMenu());

        // --- Nút tạm dừng ---
        Button pauseBtn = new Button("⏸ Pause");
        styleControlBtn(pauseBtn, "#37474F");
        pauseBtn.setOnAction(e -> {
            isPaused = !isPaused;
            if (isPaused) {
                SoundManager.getInstance().muteAll();  // tắt tiếng ngay
                pauseBtn.setText("▶ Resume");
            } else {
                pauseBtn.setText("⏸ Pause");
                // tiếng tự phục hồi ở frame tiếp theo qua updateVehicleSound()
            }
        });

        // --- Nút Settings (dropdown) ---
        MenuButton settingsBtn = buildSettingsMenu();

        bar.getChildren().addAll(menuBtn, pauseBtn, settingsBtn);
        return bar;
    }
    private MenuButton buildSettingsMenu() {
        MenuButton btn = new MenuButton("⚙ Settings");
        btn.setStyle("-fx-background-color: #455A64; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;");

        // -- Mật độ xe --
        Menu densityMenu = new Menu("Traffic Density");
        ToggleGroup dg = new ToggleGroup();
        for (TrafficDensity d : TrafficDensity.values()) {
            RadioMenuItem item = new RadioMenuItem(d.name());
            item.setToggleGroup(dg);
            if (d == runtimeDensity) item.setSelected(true);
            item.setOnAction(e -> runtimeDensity = d);
            densityMenu.getItems().add(item);
        }
        btn.getItems().add(densityMenu);

        // -- Số lượng xe --
        Menu countMenu = new Menu("Max Vehicles");
        for (int v : new int[]{10, 20, 30, 50, 80, 100}) {
            MenuItem item = new MenuItem(String.valueOf(v));
            item.setOnAction(e -> runtimeVehicleCount = v);
            countMenu.getItems().add(item);
        }
        btn.getItems().add(countMenu);

        // -- Chế độ đèn (toggle manual/auto) --
        btn.getItems().add(new SeparatorMenuItem());
        MenuItem lightToggle = new MenuItem("Toggle Manual/Auto Light");
        lightToggle.setOnAction(e -> toggleLightMode());
        btn.getItems().add(lightToggle);

        // -- Reset camera --
        MenuItem resetCam = new MenuItem("Reset Camera");
        resetCam.setOnAction(e -> camera.reset());
        btn.getItems().add(resetCam);

        return btn;
    }

    private void styleControlBtn(Button btn, String bg) {
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");
    }

    // ==========================================================
    // Sự kiện camera
    // ==========================================================

    private void bindCameraEvents() {
        // Scroll → zoom
        canvas.setOnScroll(camera::handleScroll);

        // Drag → pan
        canvas.setOnMousePressed(e -> camera.startDrag(e.getX(), e.getY()));
        canvas.setOnMouseDragged(e -> camera.drag(e.getX(), e.getY()));
        canvas.setOnMouseReleased(e -> camera.stopDrag());

        // Click trái thủ công (MANUAL mode)
        canvas.setOnMouseClicked(e -> {
            if (config.getTrafficMode().equals("MANUAL")) {
                handleManualLightClick(camera.toWorldX(e.getX()), camera.toWorldY(e.getY()));
            }
        });
    }

    private void bindKeyboard() {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) camera.reset();
        });
    }

    // ==========================================================
    // Vòng lặp game
    // ==========================================================

    private void startGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    updateSimulation();   // logic + sound
                }
                render();                 // vẫn render để UI không đơ
                updateFPS(now);
            }
        };
        gameTimer.start();
    }

    // ==========================================================
    // Cập nhật logic
    // ==========================================================

    private void updateSimulation() {
        vehicles.removeIf(v -> !config.getIntersectionType().getDirections().contains(v.getDirection()));

        trafficController.updateVehicles(vehicles, verticalLight, horizontalLight,
                config.getIntersectionType());

        if (!config.getTrafficMode().equals("MANUAL")) {
            verticalLight.update();
            syncHorizontalLight();
            applySmartLight();
        }

        spawnManager.removeOutsideVehicles();
        emergencySystem.updateEmergencyVehicles(vehicles);
        handleAutoSpawn();
        updateTrafficJam();
        updateFlash();
    }

    // ==========================================================
    // Render
    // ==========================================================

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();

        // Nền
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, w, h);

        // World (camera transform)
        camera.applyTransform(gc);

        switch (config.getIntersectionType()) {
            case THREE_WAY: envRenderer.renderThreeWay(gc); break;
            case FIVE_WAY:  envRenderer.renderFiveWay(gc);  break;
            default:        envRenderer.renderFourWay(gc);  break;
        }

        for (Vehicle v : vehicles) vehicleRenderer.render(gc, v, flash);

        // Đèn giao thông (không hiện đèn ngang cho ngã ba)
        lightRenderer.render(gc, verticalLight,   520, 250, config.getLightType());
        if (config.getIntersectionType() != IntersectionType.THREE_WAY) {
            lightRenderer.render(gc, horizontalLight, 250, 520, config.getLightType());
        }

        camera.restoreTransform(gc);

        // HUD (screen space — không qua camera)
        updateHUD();
    }

    private void updateHUD() {
        int stopped = trafficController.countStoppedVehicles(vehicles);
        hudLabel.setText(String.format(
                "  Vehicles : %d / %d%n" +
                "  Stopped  : %d%n" +
                "  FPS      : %d%n" +
                "  Jam      : %s%n" +
                "  V-Light  : %s%n" +
                "  H-Light  : %s%n" +
                "  Zoom     : %.0f%%%n" +
                "  [Scroll] Zoom  [Drag] Pan  [R] Reset",
                vehicles.size(), runtimeVehicleCount,
                stopped, fps, jamLevel,
                verticalLight.getColor(), horizontalLight.getColor(),
                camera.getZoom() * 100
        ));
    }

    // ==========================================================
    // Đèn giao thông
    // ==========================================================

    private void syncHorizontalLight() {
        switch (verticalLight.getColor()) {
            case GREEN: case YELLOW:
                horizontalLight.setColor(LightColor.RED);
                break;
            case RED:
                if (horizontalLight.getColor() != LightColor.GREEN) {
                    horizontalLight.setColor(LightColor.GREEN);
                    horizontalLight.setTimer(Constants.LIGHT_GREEN_DURATION);
                }
                break;
        }
    }

    private void applySmartLight() {
        int vc = trafficController.countVehiclesByDirection(vehicles, Direction.NORTH, Direction.SOUTH);
        int hc = trafficController.countVehiclesByDirection(vehicles, Direction.EAST,  Direction.WEST);
        int th = Constants.SMART_LIGHT_THRESHOLD, mx = Constants.SMART_LIGHT_MAX_TIMER;

        if (vc > hc + th && verticalLight.getColor()   == LightColor.GREEN)
            verticalLight.setTimer(Math.max(verticalLight.getTimer(), mx));
        if (hc > vc + th && horizontalLight.getColor() == LightColor.GREEN)
            horizontalLight.setTimer(Math.max(horizontalLight.getTimer(), mx));

        for (Vehicle v : vehicles) {
            boolean em = v instanceof model.vehicle.Ambulance || v instanceof model.vehicle.FireTruck;
            if (!em) continue;
            switch (v.getDirection()) {
                case NORTH: case SOUTH:
                    if (verticalLight.getColor()   == LightColor.RED) forceGreen(verticalLight, horizontalLight); break;
                case EAST:  case WEST:
                    if (horizontalLight.getColor() == LightColor.RED) forceGreen(horizontalLight, verticalLight); break;
                default: break;
            }
        }
    }

    private void forceGreen(TrafficLight target, TrafficLight other) {
        target.setColor(LightColor.GREEN); target.setTimer(Constants.LIGHT_EMERGENCY_DURATION);
        other.setColor(LightColor.RED);
    }

    private void toggleLightMode() {
        // Toggle vertical: xanh↔đỏ
        if (verticalLight.getColor() == LightColor.GREEN) {
            verticalLight.setColor(LightColor.RED);
            horizontalLight.setColor(LightColor.GREEN);
        } else {
            verticalLight.setColor(LightColor.GREEN);
            horizontalLight.setColor(LightColor.RED);
        }
        verticalLight.setTimer(Constants.LIGHT_GREEN_DURATION);
        horizontalLight.setTimer(Constants.LIGHT_GREEN_DURATION);
    }

    private void handleManualLightClick(double wx, double wy) {
        boolean nearV = wx >= 510 && wx <= 560 && wy >= 240 && wy <= 360;
        boolean nearH = wx >= 240 && wx <= 360 && wy >= 510 && wy <= 560;
        if (nearV || nearH) toggleLightMode();
    }

    // ==========================================================
    // Spawn
    // ==========================================================

    private void spawnInitialVehicles() {
        List<Direction> dirs = config.getIntersectionType().getDirections();
        for (int i = 0; i < runtimeVehicleCount; i++) spawnManager.spawnRandomVehicle(dirs);
    }

    private void handleAutoSpawn() {
        spawnCounter++;
        if (spawnCounter >= spawnInterval()) {
            spawnCounter = 0;
            if (vehicles.size() < runtimeVehicleCount)
                spawnManager.spawnRandomVehicle(config.getIntersectionType().getDirections());
        }
    }

    private int spawnInterval() {
        switch (runtimeDensity) {
            case LOW:  return 180;
            case HIGH: return 60;
            default:   return 120;
        }
    }

    // ==========================================================
    // Thống kê
    // ==========================================================

    private void updateTrafficJam() {
        int total = vehicles.size();
        if (total == 0) { jamLevel = "LOW"; return; }
        double ratio = (double) trafficController.countStoppedVehicles(vehicles) / total;
        jamLevel = ratio > Constants.JAM_HIGH_THRESHOLD ? "HIGH"
                 : ratio > Constants.JAM_MEDIUM_THRESHOLD ? "MEDIUM" : "LOW";
    }

    private void updateFPS(long now) {
        frameCount++;
        if (now - lastFpsTime >= 1_000_000_000L) {
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = now;
        }
    }

    private void updateFlash() {
        if (++flashCounter >= Constants.FLASH_INTERVAL_FRAMES) {
            flash = !flash;
            flashCounter = 0;
        }
    }

    // ==========================================================
    // Quay về menu
    // ==========================================================

    private void returnToMenu() {
        if (gameTimer != null) gameTimer.stop();
        MenuScene menu = new MenuScene(primaryStage);
        primaryStage.setScene(menu.getScene());
    }

    // ==========================================================
    // Getter
    // ==========================================================

    public Scene getScene() { return scene; }
}
