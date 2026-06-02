package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

public class Bicycle extends Vehicle {
    private static final double WIDTH = 28, HEIGHT = 14;

    public Bicycle(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH; height = HEIGHT;
        behavior = new NormalDriver();
        speed = 2;
        maxSpeed = 2;
    }
}
