package view.panel;


import model.vehicle.Vehicle;


import view.renderer.VehicleRenderer;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;

import java.util.ArrayList;
import java.util.List;


import view.renderer.RoadRenderer;


import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import view.renderer.TrafficLightRenderer;

import controller.TrafficController;

import model.SimulationConfig;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Rectangle;


import manager.VehicleSpawnManager;
public class SimulationPanel extends JPanel {

    private Timer timer;
    
    private TrafficLight verticalLight;

    private TrafficLight horizontalLight;

    private TrafficLightRenderer trafficLightRenderer;

    private List<Vehicle> vehicles;

    private VehicleRenderer vehicleRenderer;
    
    private RoadRenderer roadRenderer;
    
    private TrafficController trafficController;
    
    private SimulationConfig config;
    
    private boolean manualMode;
    
    
    private VehicleSpawnManager vehicleSpawnManager;
    private int spawnCounter = 0;
    private int fps = 0;

    private long lastFpsTime = System.currentTimeMillis();

    private int frameCount = 0;
    private String jamLevel = "LOW";
   public SimulationPanel(SimulationConfig config) {

    this.config = config;

    manualMode =
            config.getTrafficMode().equals("MANUAL");

    setBackground(Color.GRAY);

    roadRenderer = new RoadRenderer();

    vehicles = new ArrayList<>();

    vehicleRenderer = new VehicleRenderer();

    verticalLight = new TrafficLight(
            LightColor.GREEN,
            300
    );

    horizontalLight = new TrafficLight(
            LightColor.RED,
            300
    );

    trafficLightRenderer = new TrafficLightRenderer();

    trafficController = new TrafficController();
    vehicleSpawnManager =
        new VehicleSpawnManager(
                vehicles
        );

    spawnVehicles();

    startGameLoop();

    addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (manualMode) {

                handleTrafficLightClick(
                        e.getX(),
                        e.getY()
                );
            }
        }
    });
    
}

    private void spawnVehicles() {

    vehicles.clear();

    int count =
            config.getVehicleCount();

    for (int i = 0; i < count; i++) {

        vehicleSpawnManager.spawnRandomVehicle(
        config.getIntersectionType()
                .getDirections()
);
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

    trafficController.updateVehicles(
            vehicles,
            verticalLight,
            horizontalLight,
            config.getIntersectionType()
    );

    if (!manualMode) {

        verticalLight.update();
        syncLights();
    }

    
    updateSmartLights();

    vehicleSpawnManager
            .removeOutsideVehicles();

    vehicles.removeIf(vehicle ->
            !config.getIntersectionType()
                    .getDirections()
                    .contains(
                            vehicle.getDirection()
                    )
    );

    handleAutoSpawn();
    updateTrafficJam();
    updateFPS();
}
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        drawRoad(g2d);

        drawVehicles(g2d);
        
        drawTrafficLights(g2d);
        drawStatistics(g2d);

    }

    private void drawRoad(Graphics2D g2d) {

    switch (config.getIntersectionType()) {

        case THREE_WAY:

            roadRenderer.renderThreeWay(g2d);
            break;

        case FIVE_WAY:

            roadRenderer.renderFiveWay(g2d);
            break;

        default:

            roadRenderer.renderFourWay(g2d);
    }
}

    private void drawVehicles(Graphics2D g2d) {

        for (Vehicle vehicle : vehicles) {

            vehicleRenderer.render(g2d, vehicle);

        }

    }
    private void syncLights() {

    switch (verticalLight.getColor()) {

        case GREEN:

            horizontalLight.setColor(
                    LightColor.RED
            );

            break;

        case YELLOW:

            horizontalLight.setColor(
                    LightColor.RED
            );

            break;

        case RED:

            horizontalLight.setColor(
                    LightColor.GREEN
            );

            break;
    }

    horizontalLight.setTimer(
            verticalLight.getTimer()
    );
}
    private void drawTrafficLights(Graphics2D g2d) {

    trafficLightRenderer.render(
        g2d,
        verticalLight,
        520,
        250,
        config.getLightType()
);

    if (config.getIntersectionType()
            != model.intersection.IntersectionType.THREE_WAY) {

        trafficLightRenderer.render(
        g2d,
        horizontalLight,
        250,
        520,
        config.getLightType()
);
    }
}
    private void handleTrafficLightClick(int mouseX, int mouseY) {

    Rectangle verticalBounds =
            new Rectangle(520, 250, 40, 100);

    if (verticalBounds.contains(mouseX, mouseY)) {

        switchManualLights();
    }
}
    private void switchManualLights() {

    switch (verticalLight.getColor()) {

        case GREEN:

            verticalLight.setColor(
                    LightColor.RED
            );

            horizontalLight.setColor(
                    LightColor.GREEN
            );

            break;

        case RED:

            verticalLight.setColor(
                    LightColor.GREEN
            );

            horizontalLight.setColor(
                    LightColor.RED
            );

            break;
    }

    verticalLight.setTimer(300);

    horizontalLight.setTimer(300);
}
    private void handleAutoSpawn() {

    spawnCounter++;

    if (spawnCounter >= getSpawnInterval()) {

        spawnCounter = 0;

        if (vehicles.size() < 20) {

            vehicleSpawnManager
                    .spawnRandomVehicle(
                            config.getIntersectionType()
                                    .getDirections()
                    );
        }
    }
}
    private int getSpawnInterval() {

    switch (
            config.getTrafficDensity()
    ) {

        case LOW:

            return 180;

        case MEDIUM:

            return 120;

        case HIGH:

            return 60;

        default:

            return 120;
    }
}
    private void updateSmartLights() {

    int verticalCount =
            trafficController
                    .countVehiclesByDirection(
                            vehicles,
                            util.Direction.NORTH,
                            util.Direction.SOUTH
                    );

    int horizontalCount =
            trafficController
                    .countVehiclesByDirection(
                            vehicles,
                            util.Direction.EAST,
                            util.Direction.WEST
                    );

    // vertical đông hơn

    if (verticalCount > horizontalCount + 3) {

        if (verticalLight.getColor()
                == LightColor.GREEN) {

            verticalLight.setTimer(
                    Math.max(
                            verticalLight.getTimer(),
                            400
                    )
            );
        }
    }

    // horizontal đông hơn

    if (horizontalCount > verticalCount + 3) {

        if (horizontalLight.getColor()
                == LightColor.GREEN) {

            horizontalLight.setTimer(
                    Math.max(
                            horizontalLight.getTimer(),
                            400
                    )
            );
        }
    }
    for (Vehicle vehicle : vehicles) {

    boolean emergency =

            vehicle instanceof model.vehicle.Ambulance
            ||
            vehicle instanceof model.vehicle.FireTruck;

    if (!emergency) {
        continue;
    }

    switch (vehicle.getDirection()) {

        case NORTH:
        case SOUTH:

            if (verticalLight.getColor()
                    == LightColor.RED) {

                forceVerticalGreen();

                verticalLight.setTimer(400);
            }

            break;

        case EAST:
        case WEST:

            if (horizontalLight.getColor()
                    == LightColor.RED) {

                forceHorizontalGreen();

                horizontalLight.setTimer(400);
            }

            break;
    }
}
}
   private void updateFPS() {

    frameCount++;

    long current =
            System.currentTimeMillis();

    if (current - lastFpsTime >= 1000) {

        fps = frameCount;

        frameCount = 0;

        lastFpsTime = current;
    }
} 
    private void drawStatistics(
        Graphics2D g2d
) {

    g2d.setColor(
            new Color(0, 0, 0, 180)
    );

    g2d.fillRoundRect(
            20,
            20,
            260,
            180,
            20,
            20
    );

    g2d.setColor(Color.WHITE);

    g2d.setFont(
            new Font(
                    "Arial",
                    Font.BOLD,
                    16
            )
    );

    int y = 50;

    g2d.drawString(
            "Vehicle Count: "
                    + vehicles.size(),
            40,
            y
    );

    y += 30;

    g2d.drawString(
            "FPS: " + fps,
            40,
            y
    );

    y += 30;

    g2d.drawString(
            "Density: "
                    + config.getTrafficDensity(),
            40,
            y
    );

    y += 30;

    g2d.drawString(
            "Vertical Light: "
                    + verticalLight.getColor(),
            40,
            y
    );

    y += 30;

    g2d.drawString(
            "Horizontal Light: "
                    + horizontalLight.getColor(),
            40,
            y
    );
    y += 30;

    g2d.drawString(
        "Traffic Jam: " + jamLevel,
        40,
        y
    );
}
    private void updateTrafficJam() {

    int stopped =
            trafficController
                    .countStoppedVehicles(
                            vehicles
                    );

    if (stopped > 15) {

        jamLevel = "HIGH";

    } else if (stopped > 7) {

        jamLevel = "MEDIUM";

    } else {

        jamLevel = "LOW";
    }
}
    private void forceVerticalGreen() {

    verticalLight.setColor(
            LightColor.GREEN
    );

    horizontalLight.setColor(
            LightColor.RED
    );

    verticalLight.setTimer(400);
}

private void forceHorizontalGreen() {

    horizontalLight.setColor(
            LightColor.GREEN
    );

    verticalLight.setColor(
            LightColor.RED
    );

    horizontalLight.setTimer(400);
}
    
}