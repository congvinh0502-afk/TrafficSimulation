package view.panel;


import model.vehicle.Vehicle;


import view.renderer.VehicleRenderer;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
    }

    syncLights();

    vehicleSpawnManager
            .removeOutsideVehicles();

    vehicles.removeIf(vehicle ->
            !config.getIntersectionType()
                    .getDirections()
                    .contains(
                            vehicle.getDirection()
                    )
    );
}

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        drawRoad(g2d);

        drawVehicles(g2d);
        
        drawTrafficLights(g2d);

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
            250
    );

    if (config.getIntersectionType()
            != model.intersection.IntersectionType.THREE_WAY) {

        trafficLightRenderer.render(
                g2d,
                horizontalLight,
                250,
                520
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
    
    
    
}