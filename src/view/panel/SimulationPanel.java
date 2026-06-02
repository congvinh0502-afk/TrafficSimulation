package view.panel;

import layout.IntersectionLayout;
import layout.IntersectionLayoutFactory;
import manager.LaneManager;
import model.SimulationConfig;
import model.intersection.IntersectionType;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;

import controller.TrafficController;
import manager.VehicleSpawnManager;
import model.vehicle.Vehicle;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import system.emergency.EmergencyVehicleSystem;
import view.renderer.EnvironmentRenderer;
import view.renderer.RoadRenderer;
import view.renderer.TrafficLightRenderer;
import view.renderer.VehicleRenderer;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import util.Direction;
import java.util.List;

/**
 * SimulationPanel — panel chính chứa game loop và tất cả render.
 *
 * Thay đổi so với phiên bản cũ (Bước 1 + 3):
 *
 * [Bước 1 — IntersectionLayout]
 *   - applyConfig() gọi LaneManager.setLayout() để inject layout mới.
 *   - drawTrafficLights() lấy vị trí đèn từ layout.getLightPositions()
 *     thay vì hardcode (520,250) và (250,520).
 *   - handleTrafficLightClick() dùng trafficLightRenderer.getBounds()
 *     thay vì hardcode Rectangle.
 *
 * [Bước 3 — LightDisplayMode]
 *   - drawTrafficLights() truyền config.getLightDisplayMode() (enum)
 *     thay vì config.getLightType() (String).
 *
 * Mọi logic khác (game loop, spawn, smart lights, FPS, stats) giữ nguyên.
 */
public class SimulationPanel extends JPanel {

    private Timer timer;

    private TrafficLight verticalLight;
    private TrafficLight horizontalLight;

    private TrafficLightRenderer trafficLightRenderer;
    private List<Vehicle>        vehicles;
    private VehicleRenderer      vehicleRenderer;
    private RoadRenderer         roadRenderer;
    private EnvironmentRenderer  environmentRenderer;
    private TrafficController    trafficController;
    private SimulationConfig     config;
    private boolean              manualMode;
    private VehicleSpawnManager  vehicleSpawnManager;

    private int    spawnCounter = 0;
    private int    fps          = 0;
    private long   lastFpsTime  = System.currentTimeMillis();
    private int    frameCount   = 0;
    private String jamLevel     = "LOW";

    // Flash cho xe cứu thương
    private boolean flash        = false;
    private int     flashCounter = 0;

    private EmergencyVehicleSystem emergencyVehicleSystem;
    private boolean                simulationStarted = false;

    // ─────────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────

    public SimulationPanel(SimulationConfig config) {
        this.config    = config;
        manualMode     = config.getTrafficMode().equals("MANUAL");

        setBackground(Color.GRAY);

        roadRenderer        = new RoadRenderer();
        environmentRenderer = new EnvironmentRenderer();
        vehicles            = new ArrayList<>();
        vehicleRenderer     = new VehicleRenderer();

        verticalLight   = new TrafficLight(LightColor.GREEN, 300);
        horizontalLight = new TrafficLight(LightColor.RED,   300);

        trafficLightRenderer  = new TrafficLightRenderer();
        trafficController     = new TrafficController();
        vehicleSpawnManager   = new VehicleSpawnManager(vehicles);
        emergencyVehicleSystem = new EmergencyVehicleSystem();

        startGameLoop();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (manualMode) {
                    handleTrafficLightClick(e.getX(), e.getY());
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    // APPLY CONFIG — gọi khi user nhấn START
    // ─────────────────────────────────────────────────────────────

    public void applyConfig(SimulationConfig newConfig) {
        simulationStarted = true;
        this.config       = newConfig;
        this.manualMode   = newConfig.getTrafficMode().equals("MANUAL");

        // [Bước 1] Inject layout mới vào LaneManager — tất cả system tự động dùng
        IntersectionLayout layout = IntersectionLayoutFactory.create(
                newConfig.getIntersectionType()
        );
        LaneManager.setLayout(layout);

        vehicles.clear();
        spawnVehicles();

        verticalLight.setColor(LightColor.GREEN);
        horizontalLight.setColor(LightColor.RED);
        verticalLight.setTimer(300);
        horizontalLight.setTimer(300);

        repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // GAME LOOP
    // ─────────────────────────────────────────────────────────────

    private void startGameLoop() {
        timer = new Timer(16, e -> {
            updateSimulation();
            repaint();
        });
        timer.start();
    }

    private void updateSimulation() {
        if (!simulationStarted) return;

        vehicles.removeIf(vehicle ->
                !config.getIntersectionType()
                        .getDirections()
                        .contains(vehicle.getDirection())
        );

        trafficController.updateVehicles(
                vehicles, verticalLight, horizontalLight,
                config.getIntersectionType()
        );

        if (!manualMode) {
            verticalLight.update();
            syncLights();
            updateSmartLights();
        }

        vehicleSpawnManager.removeOutsideVehicles();
        emergencyVehicleSystem.updateEmergencyVehicles(vehicles);
        handleAutoSpawn();
        updateTrafficJam();
        updateFPS();
        updateFlash();
    }

    // ─────────────────────────────────────────────────────────────
    // PAINT
    // ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawEnvironment(g2d);
        drawRoad(g2d);
        drawVehicles(g2d);
        drawTrafficLights(g2d);
        drawStatistics(g2d);
    }

    private void drawEnvironment(Graphics2D g2d) {
        switch (config.getIntersectionType()) {
            case THREE_WAY: environmentRenderer.renderThreeWay(g2d); break;
            case FIVE_WAY:  environmentRenderer.renderFiveWay(g2d);  break;
            default:        environmentRenderer.renderFourWay(g2d);
        }
    }

    private void drawRoad(Graphics2D g2d) {
        switch (config.getIntersectionType()) {
            case THREE_WAY: roadRenderer.renderThreeWay(g2d); break;
            case FIVE_WAY:  roadRenderer.renderFiveWay(g2d);  break;
            default:        roadRenderer.renderFourWay(g2d);
        }
    }

    private void drawVehicles(Graphics2D g2d) {
        for (Vehicle vehicle : vehicles) {
            vehicleRenderer.render(g2d, vehicle, flash);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // VẼ ĐÈN — [Bước 1 + 3] dùng layout positions và enum mode
    // ─────────────────────────────────────────────────────────────

    private void drawTrafficLights(Graphics2D g2d) {
        IntersectionLayout layout = LaneManager.getLayout();
        List<java.awt.Point> positions = layout.getLightPositions();

        // Đèn 0: đèn dọc (vertical) — luôn hiển thị
        if (positions.size() > 0) {
            java.awt.Point p = positions.get(0);
            trafficLightRenderer.render(
                    g2d, verticalLight, p.x, p.y,
                    config.getLightDisplayMode()   // [Bước 3] enum thay vì String
            );
        }

        // Đèn 1: đèn ngang (horizontal) — chỉ hiển thị khi không phải THREE_WAY
        if (positions.size() > 1
                && config.getIntersectionType() != IntersectionType.THREE_WAY) {
            java.awt.Point p = positions.get(1);
            trafficLightRenderer.render(
                    g2d, horizontalLight, p.x, p.y,
                    config.getLightDisplayMode()
            );
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CLICK DETECTION — [Bước 1 + 3] dùng getBounds() từ renderer
    // ─────────────────────────────────────────────────────────────

    private void handleTrafficLightClick(int mouseX, int mouseY) {
        IntersectionLayout layout    = LaneManager.getLayout();
        List<java.awt.Point> positions = layout.getLightPositions();

        // Kiểm tra click vào đèn nào (dùng getBounds từ renderer)
        for (java.awt.Point p : positions) {
            Rectangle bounds = trafficLightRenderer.getBounds(p.x, p.y);
            if (bounds.contains(mouseX, mouseY)) {
                switchManualLights();
                return;
            }
        }
    }

    private void switchManualLights() {
        switch (verticalLight.getColor()) {
            case GREEN:
                verticalLight.setColor(LightColor.RED);
                horizontalLight.setColor(LightColor.GREEN);
                break;
            case RED:
                verticalLight.setColor(LightColor.GREEN);
                horizontalLight.setColor(LightColor.RED);
                break;
        }
        verticalLight.setTimer(300);
        horizontalLight.setTimer(300);
    }

    // ─────────────────────────────────────────────────────────────
    // SYNC LIGHTS — giữ nguyên
    // ─────────────────────────────────────────────────────────────

    private void syncLights() {
        switch (verticalLight.getColor()) {
            case GREEN:
                horizontalLight.setColor(LightColor.RED);
                break;
            case YELLOW:
                horizontalLight.setColor(LightColor.RED);
                break;
            case RED:
                if (horizontalLight.getColor() != LightColor.GREEN) {
                    horizontalLight.setColor(LightColor.GREEN);
                    horizontalLight.setTimer(300);
                }
                break;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN — giữ nguyên
    // ─────────────────────────────────────────────────────────────

    private void spawnVehicles() {
        vehicles.clear();
        List<Direction> directions = config.getIntersectionType().getDirections();
        int perDir;
        switch (config.getTrafficDensity()) {
            case LOW:  perDir = 2; break;
            case HIGH: perDir = 6; break;
            default:   perDir = 4; break;
        }
        for (Direction dir : directions) {
            spawnQueue(dir, perDir);
        }
    }

    private void spawnQueue(Direction dir, int amount) {
        vehicleSpawnManager.spawnTrafficQueue(
                dir, amount,
                config.getIntersectionType().getDirections()
        );
    }

    private void handleAutoSpawn() {
        spawnCounter++;
        if (spawnCounter < getSpawnInterval()) return;
        spawnCounter = 0;

        int maxVehicles;
        switch (config.getTrafficDensity()) {
            case LOW:   maxVehicles = 25;  break;
            case HIGH:  maxVehicles = 100; break;
            default:    maxVehicles = 60;  break;
        }

        if (vehicles.size() >= maxVehicles) return;

        int queueSize;
        switch (config.getTrafficDensity()) {
            case LOW:   queueSize = (int)(Math.random() * 2) + 1; break;
            case HIGH:  queueSize = (int)(Math.random() * 4) + 2; break;
            default:    queueSize = (int)(Math.random() * 3) + 1; break;
        }

        List<Direction> directions = config.getIntersectionType().getDirections();
        Direction direction = directions.get((int)(Math.random() * directions.size()));
        vehicleSpawnManager.spawnTrafficQueue(
                direction, queueSize, directions
        );
    }

    private int getSpawnInterval() {
        switch (config.getTrafficDensity()) {
            case LOW:  return 180;
            case HIGH: return 60;
            default:   return 120;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SMART LIGHTS — giữ nguyên
    // ─────────────────────────────────────────────────────────────

    private void updateSmartLights() {
        int verticalCount   = trafficController.countVehiclesByDirection(
                vehicles, Direction.NORTH, Direction.SOUTH);
        int horizontalCount = trafficController.countVehiclesByDirection(
                vehicles, Direction.EAST, Direction.WEST);

        if (verticalCount > horizontalCount + 3) {
            if (verticalLight.getColor() == LightColor.GREEN)
                verticalLight.setTimer(Math.max(verticalLight.getTimer(), 400));
        }
        if (horizontalCount > verticalCount + 3) {
            if (horizontalLight.getColor() == LightColor.GREEN)
                horizontalLight.setTimer(Math.max(horizontalLight.getTimer(), 400));
        }

        for (Vehicle vehicle : vehicles) {
            boolean emergency =
                    vehicle instanceof Ambulance
                    || vehicle instanceof FireTruck;
            if (!emergency) continue;

            switch (vehicle.getDirection()) {
                case NORTH: case SOUTH:
                    if (verticalLight.getColor() == LightColor.RED) {
                        forceVerticalGreen();
                        verticalLight.setTimer(400);
                    }
                    break;
                case EAST: case WEST:
                    if (horizontalLight.getColor() == LightColor.RED) {
                        forceHorizontalGreen();
                        horizontalLight.setTimer(400);
                    }
                    break;
            }
        }
    }

    private void forceVerticalGreen() {
        verticalLight.setColor(LightColor.GREEN);
        horizontalLight.setColor(LightColor.RED);
        verticalLight.setTimer(400);
    }

    private void forceHorizontalGreen() {
        horizontalLight.setColor(LightColor.GREEN);
        verticalLight.setColor(LightColor.RED);
        horizontalLight.setTimer(400);
    }

    // ─────────────────────────────────────────────────────────────
    // STATS / FPS / FLASH — giữ nguyên
    // ─────────────────────────────────────────────────────────────

    private void updateFlash() {
        flashCounter++;
        if (flashCounter >= 20) {
            flash = !flash;
            flashCounter = 0;
        }
    }

    private void updateFPS() {
        frameCount++;
        long current = System.currentTimeMillis();
        if (current - lastFpsTime >= 1000) {
            fps         = frameCount;
            frameCount  = 0;
            lastFpsTime = current;
        }
    }

    private void updateTrafficJam() {
        int stopped = trafficController.countStoppedVehicles(vehicles);
        int total   = vehicles.size();
        if (total == 0) { jamLevel = "LOW"; return; }
        double ratio = (double) stopped / total;
        if (ratio > 0.6)      jamLevel = "HIGH";
        else if (ratio > 0.3) jamLevel = "MEDIUM";
        else                  jamLevel = "LOW";
    }

    private void drawStatistics(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(20, 20, 260, 180, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        int y = 50;
        g2d.drawString("Vehicle Count: " + vehicles.size(),          40, y); y += 30;
        g2d.drawString("FPS: "           + fps,                      40, y); y += 30;
        g2d.drawString("Density: "       + config.getTrafficDensity(),40, y); y += 30;
        g2d.drawString("Vertical: "      + verticalLight.getColor(), 40, y); y += 30;
        g2d.drawString("Horizontal: "    + horizontalLight.getColor(),40, y); y += 30;
        g2d.drawString("Traffic Jam: "   + jamLevel,                 40, y);
    }
}
