package view.panel;

import config.Constants;
import controller.TrafficController;
import manager.VehicleSpawnManager;
import model.SimulationConfig;
import model.intersection.IntersectionType;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.emergency.EmergencyVehicleSystem;
import util.Direction;
import view.renderer.EnvironmentRenderer;
import view.renderer.TrafficLightRenderer;
import view.renderer.VehicleRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel mô phỏng chính — vòng lặp game và hiển thị.
 *
 * <p>
 * Trách nhiệm của lớp này:
 * <ul>
 * <li>Khởi tạo và quản lý trạng thái mô phỏng (đèn, xe).</li>
 * <li>Chạy vòng lặp game 60 FPS qua {@link javax.swing.Timer}.</li>
 * <li>Gọi các renderer theo đúng thứ tự lớp vẽ.</li>
 * <li>Hiển thị HUD thống kê (số xe, FPS, đèn, tắc đường).</li>
 * <li>Xử lý sự kiện click chuột cho chế độ MANUAL.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Tách biệt logic và view:</b> Lớp này không chứa
 * bất kỳ logic di chuyển hoặc va chạm nào — tất cả được
 * ủy quyền cho {@link TrafficController} và các system tương ứng.
 * </p>
 */
public class SimulationPanel extends JPanel {

    // ----------------------------------------------------------
    // Hằng số giao diện
    // ----------------------------------------------------------
    private static final int TARGET_FPS = 60;
    private static final int TIMER_DELAY_MS = 1000 / TARGET_FPS; // ~16ms
    private static final Color HUD_BG = new Color(0, 0, 0, 180);
    private static final Font HUD_FONT = new Font("Arial", Font.BOLD, 16);

    // Vùng click để chuyển đèn (chế độ MANUAL)
    private static final Rectangle VERTICAL_LIGHT_BOUNDS = new Rectangle(520, 250, 40, 100);
    private static final Rectangle HORIZONTAL_LIGHT_BOUNDS = new Rectangle(250, 520, 40, 100);

    // ----------------------------------------------------------
    // Trạng thái mô phỏng
    // ----------------------------------------------------------
    private final SimulationConfig config;
    private final boolean manualMode;

    private final List<Vehicle> vehicles;
    private final TrafficLight verticalLight; // đèn hướng NORTH/SOUTH
    private final TrafficLight horizontalLight; // đèn hướng EAST/WEST

    // ----------------------------------------------------------
    // Hệ thống logic
    // ----------------------------------------------------------
    private final TrafficController trafficController;
    private final VehicleSpawnManager vehicleSpawnManager;
    private final EmergencyVehicleSystem emergencySystem;

    // ----------------------------------------------------------
    // Renderer (tầng view)
    // ----------------------------------------------------------
    private final EnvironmentRenderer environmentRenderer;
    private final TrafficLightRenderer trafficLightRenderer;
    private final VehicleRenderer vehicleRenderer;

    // ----------------------------------------------------------
    // Bộ đếm và thống kê
    // ----------------------------------------------------------
    private Timer gameTimer;
    private int spawnCounter = 0;
    private int fps = 0;
    private long lastFpsTime = System.currentTimeMillis();
    private int frameCount = 0;
    private String jamLevel = "LOW";

    // Đèn nhấp nháy xe ưu tiên — toggle tập trung 1 lần/frame
    private boolean flash = false;
    private int flashCounter = 0;

    // ==========================================================
    // Khởi tạo
    // ==========================================================

    /**
     * Tạo panel mô phỏng với cấu hình đã chọn.
     *
     * @param config cấu hình phiên mô phỏng từ menu
     */
    public SimulationPanel(SimulationConfig config) {
        this.config = config;
        this.manualMode = config.getTrafficMode().equals("MANUAL");

        setBackground(Color.GRAY);

        // Khởi tạo dữ liệu
        vehicles = new ArrayList<>();
        verticalLight = new TrafficLight(LightColor.GREEN, Constants.LIGHT_GREEN_DURATION);
        horizontalLight = new TrafficLight(LightColor.RED, Constants.LIGHT_RED_DURATION);

        // Khởi tạo hệ thống logic
        trafficController = new TrafficController();
        vehicleSpawnManager = new VehicleSpawnManager(vehicles);
        emergencySystem = new EmergencyVehicleSystem();

        // Khởi tạo renderer
        environmentRenderer = new EnvironmentRenderer();
        trafficLightRenderer = new TrafficLightRenderer();
        vehicleRenderer = new VehicleRenderer();

        spawnInitialVehicles();
        registerMouseListener();
        startGameLoop();
    }

    // ==========================================================
    // Khởi tạo phụ
    // ==========================================================

    /** Spawn số xe ban đầu theo cấu hình. */
    private void spawnInitialVehicles() {
        List<Direction> dirs = config.getIntersectionType().getDirections();
        for (int i = 0; i < config.getVehicleCount(); i++) {
            vehicleSpawnManager.spawnRandomVehicle(dirs);
        }
    }

    /** Đăng ký xử lý click chuột cho chế độ MANUAL. */
    private void registerMouseListener() {
        if (!manualMode)
            return;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTrafficLightClick(e.getX(), e.getY());
            }
        });
    }

    /** Bắt đầu vòng lặp game 60 FPS. */
    private void startGameLoop() {
        gameTimer = new Timer(TIMER_DELAY_MS, e -> {
            updateSimulation();
            repaint();
        });
        gameTimer.start();
    }

    // ==========================================================
    // Vòng lặp cập nhật (logic)
    // ==========================================================

    /**
     * Cập nhật toàn bộ trạng thái mô phỏng mỗi frame.
     *
     * <p>
     * Thứ tự quan trọng:
     * <ol>
     * <li>Lọc xe không hợp lệ với loại ngã rẽ hiện tại.</li>
     * <li>Cập nhật vị trí/trạng thái tất cả xe.</li>
     * <li>Cập nhật đèn và logic thông minh (chỉ AUTO).</li>
     * <li>Xóa xe ra ngoài bản đồ.</li>
     * <li>Cập nhật nhường đường xe ưu tiên.</li>
     * <li>Auto-spawn xe mới.</li>
     * <li>Cập nhật thống kê HUD.</li>
     * </ol>
     * </p>
     */
    private void updateSimulation() {
        // 1. Lọc xe sai hướng (ví dụ: xe SOUTH khi chuyển sang ngã ba)
        vehicles.removeIf(v -> !config.getIntersectionType().getDirections().contains(v.getDirection()));

        // 2. Cập nhật xe
        trafficController.updateVehicles(vehicles, verticalLight, horizontalLight,
                config.getIntersectionType());

        // 3. Cập nhật đèn (chỉ AUTO mode)
        if (!manualMode) {
            verticalLight.update();
            syncHorizontalLight();
            applySmartLightLogic();
        }

        // 4. Xóa xe ra ngoài
        vehicleSpawnManager.removeOutsideVehicles();

        // 5. Xe ưu tiên → ép xe thường nhường đường
        emergencySystem.updateEmergencyVehicles(vehicles);

        // 6. Auto-spawn xe mới
        handleAutoSpawn();

        // 7. Cập nhật thống kê
        updateTrafficJamLevel();
        updateFPS();
        updateFlash();
    }

    // ==========================================================
    // Render (view)
    // ==========================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawEnvironment(g2d);
        drawVehicles(g2d);
        drawTrafficLights(g2d);
        drawHUD(g2d);
    }

    /** Vẽ cảnh quan theo loại ngã rẽ. */
    private void drawEnvironment(Graphics2D g2d) {
        switch (config.getIntersectionType()) {
            case THREE_WAY:
                environmentRenderer.renderThreeWay(g2d);
                break;
            case FIVE_WAY:
                environmentRenderer.renderFiveWay(g2d);
                break;
            default:
                environmentRenderer.renderFourWay(g2d);
                break;
        }
    }

    /** Vẽ tất cả xe hiện có trên bản đồ. */
    private void drawVehicles(Graphics2D g2d) {
        for (Vehicle v : vehicles) {
            vehicleRenderer.render(g2d, v, flash);
        }
    }

    /** Vẽ đèn giao thông (ẩn đèn ngang với ngã ba). */
    private void drawTrafficLights(Graphics2D g2d) {
        trafficLightRenderer.render(g2d, verticalLight, 520, 250, config.getLightType());

        if (config.getIntersectionType() != IntersectionType.THREE_WAY) {
            trafficLightRenderer.render(g2d, horizontalLight, 250, 520, config.getLightType());
        }
    }

    /** Vẽ bảng thống kê HUD góc trên trái. */
    private void drawHUD(Graphics2D g2d) {
        g2d.setColor(HUD_BG);
        g2d.fillRoundRect(20, 20, 260, 185, 20, 20);

        g2d.setColor(Color.WHITE);
        g2d.setFont(HUD_FONT);

        int y = 50;
        g2d.drawString("Vehicles: " + vehicles.size(), 40, y);
        y += 30;
        g2d.drawString("FPS: " + fps, 40, y);
        y += 30;
        g2d.drawString("Density: " + config.getTrafficDensity(), 40, y);
        y += 30;
        g2d.drawString("Vertical:   " + verticalLight.getColor(), 40, y);
        y += 30;
        g2d.drawString("Horizontal: " + horizontalLight.getColor(), 40, y);
        y += 30;
        g2d.drawString("Traffic Jam: " + jamLevel, 40, y);
    }

    // ==========================================================
    // Đèn giao thông
    // ==========================================================

    /**
     * Đồng bộ đèn ngang theo trạng thái đèn dọc.
     *
     * <p>
     * Quy tắc: dọc GREEN/YELLOW → ngang RED;
     * dọc RED → bật GREEN cho ngang (chỉ khi chưa xanh).
     * </p>
     */
    private void syncHorizontalLight() {
        switch (verticalLight.getColor()) {
            case GREEN:
            case YELLOW:
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

    /**
     * Điều chỉnh đèn thông minh theo lưu lượng và xe ưu tiên.
     *
     * <p>
     * Hai cơ chế:
     * <ul>
     * <li>Kéo dài đèn xanh phía đông vếu nhiều xe hơn
     * {@link Constants#SMART_LIGHT_VEHICLE_THRESHOLD}.</li>
     * <li>Bật đèn xanh ngay cho hướng xe ưu tiên nếu đang đỏ.</li>
     * </ul>
     * </p>
     */
    private void applySmartLightLogic() {
        int vertCount = trafficController.countVehiclesByDirection(
                vehicles, Direction.NORTH, Direction.SOUTH);
        int horizCount = trafficController.countVehiclesByDirection(
                vehicles, Direction.EAST, Direction.WEST);

        int threshold = Constants.SMART_LIGHT_VEHICLE_THRESHOLD;
        int maxTimer = Constants.SMART_LIGHT_MAX_TIMER;

        if (vertCount > horizCount + threshold && verticalLight.getColor() == LightColor.GREEN) {
            verticalLight.setTimer(Math.max(verticalLight.getTimer(), maxTimer));
        }
        if (horizCount > vertCount + threshold && horizontalLight.getColor() == LightColor.GREEN) {
            horizontalLight.setTimer(Math.max(horizontalLight.getTimer(), maxTimer));
        }

        // Ưu tiên xe khẩn cấp
        for (Vehicle v : vehicles) {
            boolean isEmergency = v instanceof model.vehicle.Ambulance
                    || v instanceof model.vehicle.FireTruck;
            if (!isEmergency)
                continue;

            switch (v.getDirection()) {
                case NORTH:
                case SOUTH:
                    if (verticalLight.getColor() == LightColor.RED) {
                        forceGreen(verticalLight, horizontalLight);
                    }
                    break;
                case EAST:
                case WEST:
                    if (horizontalLight.getColor() == LightColor.RED) {
                        forceGreen(horizontalLight, verticalLight);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Bật xanh cho {@code target} và đỏ cho {@code other}.
     */
    private void forceGreen(TrafficLight target, TrafficLight other) {
        target.setColor(LightColor.GREEN);
        target.setTimer(Constants.LIGHT_EMERGENCY_DURATION);
        other.setColor(LightColor.RED);
    }

    /**
     * Xử lý click chuột trong chế độ MANUAL — toggle đèn.
     */
    private void handleTrafficLightClick(int mx, int my) {
        if (!VERTICAL_LIGHT_BOUNDS.contains(mx, my)
                && !HORIZONTAL_LIGHT_BOUNDS.contains(mx, my)) {
            return;
        }
        // Toggle: xanh ↔ đỏ
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

    // ==========================================================
    // Auto-spawn xe
    // ==========================================================

    /** Spawn xe mới theo chu kỳ nếu chưa đạt số lượng tối đa. */
    private void handleAutoSpawn() {
        spawnCounter++;
        if (spawnCounter >= getSpawnInterval()) {
            spawnCounter = 0;
            if (vehicles.size() < config.getVehicleCount()) {
                vehicleSpawnManager.spawnRandomVehicle(
                        config.getIntersectionType().getDirections());
            }
        }
    }

    /** Chu kỳ spawn (frame) theo mật độ giao thông. */
    private int getSpawnInterval() {
        switch (config.getTrafficDensity()) {
            case LOW:
                return 180;
            case HIGH:
                return 60;
            default:
                return 120;
        }
    }

    // ==========================================================
    // Thống kê HUD
    // ==========================================================

    /** Cập nhật mức độ tắc đường dựa trên tỉ lệ xe dừng. */
    private void updateTrafficJamLevel() {
        int total = vehicles.size();
        if (total == 0) {
            jamLevel = "LOW";
            return;
        }

        double ratio = (double) trafficController.countStoppedVehicles(vehicles) / total;

        if (ratio > Constants.JAM_HIGH_THRESHOLD)
            jamLevel = "HIGH";
        else if (ratio > Constants.JAM_MEDIUM_THRESHOLD)
            jamLevel = "MEDIUM";
        else
            jamLevel = "LOW";
    }

    /** Cập nhật FPS mỗi giây. */
    private void updateFPS() {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFpsTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = now;
        }
    }

    /**
     * Toggle trạng thái đèn nhấp nháy xe ưu tiên mỗi
     * {@link Constants#FLASH_INTERVAL_FRAMES} frame.
     * Tập trung tại đây — không để từng xe tự toggle.
     */
    private void updateFlash() {
        flashCounter++;
        if (flashCounter >= Constants.FLASH_INTERVAL_FRAMES) {
            flash = !flash;
            flashCounter = 0;
        }
    }
}