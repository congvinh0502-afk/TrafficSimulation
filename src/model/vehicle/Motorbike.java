package model.vehicle;

import strategy.driver.AggressiveDriver;
import util.Direction;

public class Motorbike extends Vehicle {

    public Motorbike(double x, double y, Direction direction) {
        super(x, y, direction);
        behavior = new AggressiveDriver();
        speed = behavior.getSpeed();
        this.width = 34;
        this.height = 16;
    }

    @Override
    public String getSoundKey() {
        return "motorbike";
    }

    @Override
    public void move() {
        if (stopped) return;
        switch (direction) {
            case NORTH: y -= speed; break;
            case SOUTH: y += speed; break;
            case EAST:  x += speed; break;
            case WEST:  x -= speed; break;
        }
    }
}