package model.vehicle;

import strategy.driver.EmergencyDriver;
import util.Direction;

public class FireTruck extends Vehicle {
    private static final double WIDTH = 70, HEIGHT = 38;

    public FireTruck(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH; height = HEIGHT;
        behavior = new EmergencyDriver();
        speed = behavior.getSpeed();
        maxSpeed = behavior.getSpeed();
    }
}
