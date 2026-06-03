package model.vehicle;

import strategy.driver.EmergencyDriver;
import util.Direction;

public class Ambulance extends Vehicle {
    private static final double WIDTH = 65, HEIGHT = 36;

    public Ambulance(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH; height = HEIGHT;
        behavior = new EmergencyDriver();
        speed = behavior.getSpeed();
        maxSpeed = behavior.getSpeed();
    }
}
