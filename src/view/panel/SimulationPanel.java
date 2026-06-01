package view.panel;


import model.vehicle.Vehicle;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;

import java.util.ArrayList;
import java.util.List;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import controller.TrafficController;
import model.SimulationConfig;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import manager.VehicleSpawnManager;
import system.emergency.EmergencyVehicleSystem;
import view.renderer.EnvironmentRenderer;
import view.renderer.RoadRenderer;
import view.renderer.TrafficLightRenderer;
import view.renderer.VehicleRenderer;
public class SimulationPanel extends JPanel {

    private Timer timer;
    private TrafficLight verticalLight;
    private TrafficLight horizontalLight;
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

    // [FIX N-05] flash & flashCounter chuyển về đây — updateFlash() chỉ
    // chạy 1 lần/frame, không N lần theo số xe.
    private boolean flash = false;
    private int flashCounter = 0;
    private EmergencyVehicleSystem emergencyVehicleSystem;
    private boolean simulationStarted = false;
    public SimulationPanel(SimulationConfig config) {

        this.config = config;
        manualMode = config.getTrafficMode().equals("MANUAL");

        setBackground(Color.GRAY);

        roadRenderer = new RoadRenderer();
        environmentRenderer = new EnvironmentRenderer();
        vehicles = new ArrayList<>();
        vehicleRenderer = new VehicleRenderer();

        verticalLight = new TrafficLight(LightColor.GREEN, 300);
        horizontalLight = new TrafficLight(LightColor.RED, 300);

        trafficLightRenderer = new TrafficLightRenderer();
        trafficController = new TrafficController();
        vehicleSpawnManager = new VehicleSpawnManager(vehicles);
        emergencyVehicleSystem = new EmergencyVehicleSystem();
        
        //spawnVehicles();
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

    private void spawnVehicles() {

    vehicles.clear();

    switch (config.getTrafficDensity()) {

        case LOW:

            vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.NORTH,
        2,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.SOUTH,
        2,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.EAST,
        1,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.WEST,
        1,
        config.getIntersectionType().getDirections()
);

            break;

        case MEDIUM:

            vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.NORTH,
        4,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.SOUTH,
        4,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.EAST,
        3,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.WEST,
        3,
        config.getIntersectionType().getDirections()
);

            break;

        case HIGH:

            vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.NORTH,
        8,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.SOUTH,
        8,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.EAST,
        6,
        config.getIntersectionType().getDirections()
);

vehicleSpawnManager.spawnTrafficQueue(
        util.Direction.WEST,
        6,
        config.getIntersectionType().getDirections()
);

            break;
    }
}

    private void startGameLoop() {
        timer = new Timer(16, e -> {
            updateSimulation();
            repaint();
        });
        timer.start();
    }

    private void updateSimulation() {

        // [FIX M-01] removeIf dipindah ke SEBELUM updateVehicles().
        // Sebelumnya: kendaraan SOUTH spawn → updateVehicles() move 1 frame
        //             → tabrakan/kedip → baru dihapus removeIf.
        // Fix: filter dulu kendaraan yang arahnya tidak ada di tipe persimpangan,
        //      sebelum mereka sempat bergerak sekalipun.
        if (!simulationStarted) {
        return;
    }
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
            // [FIX M-07] updateSmartLights() chỉ di AUTO mode
            updateSmartLights();
        }

        vehicleSpawnManager.removeOutsideVehicles();
        emergencyVehicleSystem.updateEmergencyVehicles(vehicles);
        handleAutoSpawn();
        updateTrafficJam();
        updateFPS();

        // [FIX N-05] Một lần per frame
        updateFlash();
    }

    // [FIX N-05] Di chuyển từ VehicleRenderer về đây
    private void updateFlash() {
        flashCounter++;
        if (flashCounter >= 20) {
            flash = !flash;
            flashCounter = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    drawEnvironment(g2d); // nền + cây + nhà
    drawRoad(g2d);        // đường
    drawVehicles(g2d);    // xe
    drawTrafficLights(g2d);
    drawStatistics(g2d);
}
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

    private void drawRoad(Graphics2D g2d) {
        switch (config.getIntersectionType()) {
            case THREE_WAY: roadRenderer.renderThreeWay(g2d); break;
            case FIVE_WAY:  roadRenderer.renderFiveWay(g2d);  break;
            default:        roadRenderer.renderFourWay(g2d);
        }
    }

    private void drawVehicles(Graphics2D g2d) {
        for (Vehicle vehicle : vehicles) {
            // [FIX N-05] Truyền flash vào, không để VehicleRenderer tự gọi updateFlash()
            vehicleRenderer.render(g2d, vehicle, flash);
        }
    }

    private void syncLights() {
    switch (verticalLight.getColor()) {
        case GREEN:
            horizontalLight.setColor(LightColor.RED);
            break;
        case YELLOW:
            horizontalLight.setColor(LightColor.RED); // giữ RED trong lúc YELLOW
            break;
        case RED:
            // chỉ bật GREEN cho horizontal khi vertical thực sự RED
            // và horizontal chưa phải GREEN (tránh set liên tục)
            if (horizontalLight.getColor() != LightColor.GREEN) {
                horizontalLight.setColor(LightColor.GREEN);
                horizontalLight.setTimer(300);
            }
            break;
    }
}

    private void drawTrafficLights(Graphics2D g2d) {

        trafficLightRenderer.render(g2d, verticalLight, 520, 250, config.getLightType());

        if (config.getIntersectionType()
                != model.intersection.IntersectionType.THREE_WAY) {

            trafficLightRenderer.render(g2d, horizontalLight, 250, 520, config.getLightType());
        }
    }

    private void handleTrafficLightClick(int mouseX, int mouseY) {

        Rectangle verticalBounds   = new Rectangle(520, 250, 40, 100);
        // [FIX M-06] Thêm vùng click đèn ngang
        Rectangle horizontalBounds = new Rectangle(250, 520, 40, 100);

        if (verticalBounds.contains(mouseX, mouseY)
                || horizontalBounds.contains(mouseX, mouseY)) {
            switchManualLights();
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

    private void handleAutoSpawn() {
        spawnCounter++;
        if (spawnCounter >= getSpawnInterval()) {
            spawnCounter = 0;
            // [FIX M-03] Dùng config thay vì hardcode 20
            int maxVehicles;

switch (config.getTrafficDensity()) {

    case LOW:
        maxVehicles = 25;
        break;

    case HIGH:
        maxVehicles = 100;
        break;

    default:
        maxVehicles = 60;
        break;
}
            if (vehicles.size() < maxVehicles) {

    int queueSize;

    switch (config.getTrafficDensity()) {

        case LOW:
            queueSize = (int)(Math.random() * 2) + 1; // 1-2 xe
            break;

        case HIGH:
            queueSize = (int)(Math.random() * 4) + 2; // 2-5 xe
            break;

        default:
            queueSize = (int)(Math.random() * 3) + 1; // 1-3 xe
            break;
    }

    java.util.List<util.Direction> directions =
            config.getIntersectionType().getDirections();

    util.Direction direction =
            directions.get(
                    (int)(Math.random() * directions.size())
            );

    vehicleSpawnManager.spawnTrafficQueue(
        direction,
        queueSize,
        config.getIntersectionType().getDirections()
);
}
        }
    }

    private int getSpawnInterval() {
        switch (config.getTrafficDensity()) {
            case LOW:    return 180;
            case HIGH:   return 60;
            default:     return 120;
        }
    }

    private void updateSmartLights() {

        int verticalCount = trafficController.countVehiclesByDirection(
                vehicles, util.Direction.NORTH, util.Direction.SOUTH);

        int horizontalCount = trafficController.countVehiclesByDirection(
                vehicles, util.Direction.EAST, util.Direction.WEST);

        if (verticalCount > horizontalCount + 3) {
            if (verticalLight.getColor() == LightColor.GREEN) {
                verticalLight.setTimer(Math.max(verticalLight.getTimer(), 400));
            }
        }

        if (horizontalCount > verticalCount + 3) {
            if (horizontalLight.getColor() == LightColor.GREEN) {
                horizontalLight.setTimer(Math.max(horizontalLight.getTimer(), 400));
            }
        }

        for (Vehicle vehicle : vehicles) {

            boolean emergency =
                    vehicle instanceof model.vehicle.Ambulance
                    || vehicle instanceof model.vehicle.FireTruck;

            if (!emergency) continue;

            switch (vehicle.getDirection()) {
                case NORTH:
                case SOUTH:
                    if (verticalLight.getColor() == LightColor.RED) {
                        forceVerticalGreen();
                        verticalLight.setTimer(400);
                    }
                    break;

                case EAST:
                case WEST:
                    if (horizontalLight.getColor() == LightColor.RED) {
                        forceHorizontalGreen();
                        horizontalLight.setTimer(400);
                    }
                    break;
            }
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

    private void drawStatistics(Graphics2D g2d) {

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(20, 20, 260, 180, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        int y = 50;
        g2d.drawString("Vehicle Count: " + vehicles.size(), 40, y); y += 30;
        g2d.drawString("FPS: " + fps, 40, y); y += 30;
        g2d.drawString("Density: " + config.getTrafficDensity(), 40, y); y += 30;
        g2d.drawString("Vertical Light: " + verticalLight.getColor(), 40, y); y += 30;
        g2d.drawString("Horizontal Light: " + horizontalLight.getColor(), 40, y); y += 30;
        g2d.drawString("Traffic Jam: " + jamLevel, 40, y);
    }

    private void updateTrafficJam() {
    int stopped = trafficController.countStoppedVehicles(vehicles);
    int total = vehicles.size();

    if (total == 0) {
        jamLevel = "LOW";
        return;
    }

    double ratio = (double) stopped / total;

    if (ratio > 0.6) jamLevel = "HIGH";
    else if (ratio > 0.3) jamLevel = "MEDIUM";
    else jamLevel = "LOW";
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
    public void applyConfig(SimulationConfig newConfig) {
    
    simulationStarted = true;
    this.config = newConfig;

    this.manualMode =
            newConfig.getTrafficMode().equals("MANUAL");

    vehicles.clear();

    spawnVehicles();

    verticalLight.setColor(LightColor.GREEN);
    horizontalLight.setColor(LightColor.RED);

    verticalLight.setTimer(300);
    horizontalLight.setTimer(300);

    repaint();
}
}