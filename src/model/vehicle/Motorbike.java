package model.vehicle;

import strategy.driver.AggressiveDriver;
import util.Direction;

public class Motorbike extends Vehicle {
    private static final double WIDTH = 34, HEIGHT = 16;

    public Motorbike(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH; height = HEIGHT;
        behavior = new AggressiveDriver();
        speed = behavior.getSpeed();
        maxSpeed = behavior.getSpeed();
    }
}
