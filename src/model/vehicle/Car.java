package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

public class Car extends Vehicle {
    private static final double WIDTH = 50, HEIGHT = 28;

    public Car(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH; height = HEIGHT;
        behavior = new NormalDriver();
        speed = behavior.getSpeed();
        maxSpeed = behavior.getSpeed();
    }
}
