package view.panel;

import controller.TrafficController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import layout.IntersectionLayout;
import layout.IntersectionLayoutFactory;
import manager.LaneManager;
import manager.VehicleSpawnManager;
import model.SimulationConfig;
import model.intersection.IntersectionType;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;
import system.emergency.EmergencyVehicleSystem;
import util.Direction;
import view.renderer.EnvironmentRenderer;
import view.renderer.RoadRenderer;
import view.renderer.TrafficLightRenderer;
import view.renderer.VehicleRenderer;

/**
 * SimulationPanel — panel chính chứa game loop và tất cả render.
 */
public class SimulationPanel extends JPanel {

    private Timer timer;
    private TrafficLight verticalLight;
    private TrafficLight horizontalLight;
    private LightColor prevVerticalColor = LightColor.GREEN;

    // Thêm field:
    private long lastUpdateTime = System.currentTimeMillis();
    private TrafficLightRenderer trafficLightRenderer;
    private List<Vehicle> vehicles;
    private VehicleRenderer vehicleRenderer;
    private RoadRenderer roadRenderer;
    private EnvironmentRenderer environmentRenderer;
    private TrafficController trafficController;
    private SimulationConfig config;
    private boolean manualMode;
    private VehicleSpawnManager vehicleSpawnManager;
    private int spawnCounter = 0;
    private int fps = 0;
    private long lastFpsTime = System.currentTimeMillis();
    private int frameCount = 0;
    private String jamLevel = "LOW";
    private boolean flash = false;
    private int flashCounter = 0;

    private EmergencyVehicleSystem emergencyVehicleSystem;
    private boolean simulationStarted = false;
    private boolean emergencyOverride = false;
    
    // Thêm field vào class SimulationPanel
    private long lastEmergencyTriggerMs = 0;
    private static final long EMERGENCY_COOLDOWN_MS = 8_000; // 8 giây cooldown
    
    private long lastEmergencySpawnTime = System.currentTimeMillis();
    private long nextEmergencyInterval = getRandomEmergencyInterval();

    // ─────────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────
    public SimulationPanel(SimulationConfig config) {
        this.config = config;
        manualMode = config.getTrafficMode().equals("MANUAL");
        setBackground(Color.GRAY);

        roadRenderer = new RoadRenderer();
        environmentRenderer = new EnvironmentRenderer();
        vehicles = new ArrayList<>();
        vehicleRenderer = new VehicleRenderer();
        verticalLight = new TrafficLight(LightColor.GREEN, 12_000);
        horizontalLight = new TrafficLight(LightColor.RED, 15_000);

        trafficLightRenderer = new TrafficLightRenderer();
        trafficController = new TrafficController();
        vehicleSpawnManager = new VehicleSpawnManager(vehicles);
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
    // APPLY CONFIG
    // ─────────────────────────────────────────────────────────────
    public void applyConfig(SimulationConfig newConfig) {
        simulationStarted = true;
        this.config = newConfig;
        this.manualMode = newConfig.getTrafficMode().equals("MANUAL");

        IntersectionLayout layout = IntersectionLayoutFactory.create(newConfig.getIntersectionType());
        LaneManager.setLayout(layout);

        vehicles.clear();
        spawnVehicles();

        verticalLight.setColor(LightColor.GREEN);
        horizontalLight.setColor(LightColor.RED);
        verticalLight.setTimerMs(12_000);   // 12 giây GREEN
        horizontalLight.setTimerMs(15_000); // 15 giây RED (chờ vertical xong)
        prevVerticalColor = LightColor.GREEN; // Đặt lại trạng thái ban đầu

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
        long now = System.currentTimeMillis();
        long deltaMs = now - lastUpdateTime;
        lastUpdateTime = now;
        
        if (now - lastEmergencySpawnTime >= nextEmergencyInterval) {
            vehicleSpawnManager.spawnEmergencyVehicle(config.getIntersectionType().getDirections());
            lastEmergencySpawnTime = now;
            nextEmergencyInterval = getRandomEmergencyInterval(); // Reset lại thời gian cho lần spawn tiếp theo
        }
        
        vehicles.removeIf(vehicle -> !config.getIntersectionType().getDirections().contains(vehicle.getDirection()));

        trafficController.updateVehicles(vehicles, verticalLight, horizontalLight, config.getIntersectionType());

        if (!manualMode) {
            verticalLight.update(deltaMs); // truyền deltaMs thay vì không có arg
            syncLights(deltaMs);
            updateSmartLights();
        }
        
        vehicleSpawnManager.removeOutsideVehicles();
        emergencyVehicleSystem.updateEmergencyVehicles(vehicles);
        handleAutoSpawn();
        updateTrafficJam();
        updateFPS();
        updateFlash();
        // ─── ĐOẠN CẬP NHẬT ÂM THANH ───
        boolean hasAmbulance = false;
        boolean hasFireTruck = false;
        boolean anyTurning = false;
        
        for (Vehicle v : vehicles) {
            if (v instanceof model.vehicle.Ambulance) hasAmbulance = true;
            if (v instanceof model.vehicle.FireTruck) hasFireTruck = true;
            
            if ((v.isTurning() || v.isChangingLane()) && !(v instanceof model.vehicle.Bicycle)) {
                anyTurning = true;
            }
        }
        
        manager.SoundManager.updateAmbulance(hasAmbulance);
        manager.SoundManager.updateFireTruck(hasFireTruck);
        manager.SoundManager.updateSignal(anyTurning);
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

    private void drawTrafficLights(Graphics2D g2d) {
        IntersectionLayout layout = LaneManager.getLayout();
        List<java.awt.Point> positions = layout.getLightPositions();

        if (positions.size() > 0) {
            java.awt.Point p = positions.get(0);
            trafficLightRenderer.render(g2d, verticalLight, p.x, p.y, config.getLightDisplayMode());
        }

        if (positions.size() > 1 && config.getIntersectionType() != IntersectionType.THREE_WAY) {
            java.awt.Point p = positions.get(1);
            trafficLightRenderer.render(g2d, horizontalLight, p.x, p.y, config.getLightDisplayMode());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CLICK DETECTION
    // ─────────────────────────────────────────────────────────────
    private void handleTrafficLightClick(int mouseX, int mouseY) {
        IntersectionLayout layout = LaneManager.getLayout();
        List<java.awt.Point> positions = layout.getLightPositions();

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
        prevVerticalColor = verticalLight.getColor(); // Khóa trạng thái
    }

    // ─────────────────────────────────────────────────────────────
    // SYNC LIGHTS — Giải quyết triệt để mất Vàng và loạn Số
    // ─────────────────────────────────────────────────────────────
    private void syncLights(long deltaMs) {
        if (emergencyOverride) {
            emergencyOverride = false;
            prevVerticalColor = verticalLight.getColor();
            return;
        }

        LightColor vColor = verticalLight.getColor();

        if (vColor != prevVerticalColor) {
            if (vColor == LightColor.GREEN) {
                horizontalLight.setColor(LightColor.RED);
                horizontalLight.setTimerMs(verticalLight.getTimerMs() + 3_000);
            } else if (vColor == LightColor.YELLOW) {
                horizontalLight.setTimerMs(verticalLight.getTimerMs());
            } else if (vColor == LightColor.RED) {
                horizontalLight.setColor(LightColor.GREEN);
                horizontalLight.setTimerMs(12_000);
            }
        }

        if (horizontalLight.getColor() != LightColor.RED) {
            horizontalLight.update(deltaMs); // dùng deltaMs thực tế
        } else {
            if (vColor == LightColor.GREEN) {
                horizontalLight.setTimerMs(verticalLight.getTimerMs() + 3_000);
            } else if (vColor == LightColor.YELLOW) {
                horizontalLight.setTimerMs(verticalLight.getTimerMs());
            }
        }

        prevVerticalColor = vColor;
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN
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
        vehicleSpawnManager.spawnTrafficQueue(dir, amount, config.getIntersectionType().getDirections());
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
        vehicleSpawnManager.spawnTrafficQueue(direction, queueSize, directions);
    }

    private int getSpawnInterval() {
        switch (config.getTrafficDensity()) {
            case LOW:  return 180;
            case HIGH: return 60;
            default:   return 120;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SMART LIGHTS
    // ─────────────────────────────────────────────────────────────
    private void updateSmartLights() {
        int verticalCount = trafficController.countVehiclesByDirection(vehicles, Direction.NORTH, Direction.SOUTH);
        int horizontalCount = trafficController.countVehiclesByDirection(vehicles, Direction.EAST, Direction.WEST);

        if (verticalCount > horizontalCount + 3) {
            if (verticalLight.getColor() == LightColor.GREEN && verticalLight.getTimerMs() > 5_000) {
                verticalLight.setTimerMs(Math.max(verticalLight.getTimerMs(), 8_000));
            }
        }
        if (horizontalCount > verticalCount + 3) {
            if (horizontalLight.getColor() == LightColor.GREEN && horizontalLight.getTimerMs() > 5_000) {
                horizontalLight.setTimerMs(Math.max(horizontalLight.getTimerMs(), 8_000));
            }
        }

        // ── EMERGENCY: chỉ trigger 1 lần, có cooldown ──
        long now = System.currentTimeMillis();
        if (now - lastEmergencyTriggerMs < EMERGENCY_COOLDOWN_MS) return; // đang cooldown

        boolean hasNSEmergency = false;
        boolean hasEWEmergency = false;

        for (Vehicle vehicle : vehicles) {
            if (!(vehicle instanceof Ambulance) && !(vehicle instanceof FireTruck)) continue;
            switch (vehicle.getDirection()) {
                case NORTH: case SOUTH: hasNSEmergency = true; break;
                case EAST:  case WEST:  hasEWEmergency = true; break;
            }
        }

        // Ưu tiên N/S nếu cả hai hướng đều có xe cứu thương
        if (hasNSEmergency && verticalLight.getColor() == LightColor.RED) {
            forceVerticalGreen();
            lastEmergencyTriggerMs = now;
        } else if (hasEWEmergency && horizontalLight.getColor() == LightColor.RED) {
            forceHorizontalGreen();
            lastEmergencyTriggerMs = now;
        }
    }

    // SỬA LỖI: Khi xe cứu thương ép đèn, phải reset cờ `prevVerticalColor` để hệ thống ko bắt lỗi lệch nhịp
    private void forceVerticalGreen() {
        emergencyOverride = true;
        verticalLight.setColor(LightColor.GREEN);
        verticalLight.setTimerMs(6_700);           // ~6.7 giây
        horizontalLight.setColor(LightColor.RED);
        horizontalLight.setTimerMs(6_700 + 3_000); // GREEN + YELLOW
        prevVerticalColor = LightColor.RED;
    }

    private void forceHorizontalGreen() {
        emergencyOverride = true;
        horizontalLight.setColor(LightColor.GREEN);
        horizontalLight.setTimerMs(6_700);
        verticalLight.setColor(LightColor.RED);
        verticalLight.setTimerMs(6_700 + 3_000);
        prevVerticalColor = LightColor.GREEN;
    }

    // ─────────────────────────────────────────────────────────────
    // STATS / FPS / FLASH
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
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = current;
        }
    }

    private void updateTrafficJam() {
        int stopped = trafficController.countStoppedVehicles(vehicles);
        int total = vehicles.size();
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
        g2d.drawString("Vehicle Count: " + vehicles.size(), 40, y); y += 30;
        g2d.drawString("FPS: " + fps, 40, y); y += 30;
        g2d.drawString("Density: " + config.getTrafficDensity(), 40, y); y += 30;
        g2d.drawString("Vertical: " + verticalLight.getColor(), 40, y); y += 30;
        g2d.drawString("Horizontal: " + horizontalLight.getColor(), 40, y); y += 30;
        g2d.drawString("Traffic Jam: " + jamLevel, 40, y);
    }
    // Hàm trả về ngẫu nhiên khoảng thời gian từ 5000ms (5s) đến 10000ms (10s)
    private long getRandomEmergencyInterval() {
        return 5000 + (long)(Math.random() * 5000);
    }
}