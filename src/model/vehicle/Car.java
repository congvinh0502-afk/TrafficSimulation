package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

public class Car extends Vehicle {

    public Car(double x, double y, Direction direction) {
        super(x, y, direction);
        width = 50;
        height = 28;
        behavior = new NormalDriver();
        speed = behavior.getSpeed();
    }

    @Override
    public String getSoundKey() {
        return "car";
    }

    @Override
    public void move() {
        if (stopped) return;
        switch (direction) {
            case SOUTH: y += speed; break;
            case NORTH: y -= speed; break;
            case EAST:  x += speed; break;
            case WEST:  x -= speed; break;
        }
    }
}