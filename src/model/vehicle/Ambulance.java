package model.vehicle;

import strategy.driver.EmergencyDriver;
import util.Direction;

public class Ambulance extends Vehicle {

    public Ambulance(
            double x,
            double y,
            Direction direction
    ) {

        super(x, y, direction);

        width = 65;
        height = 36;

        behavior = new EmergencyDriver();

        speed = 6.0;//behavior.getSpeed();
    }

    @Override
    public void move() {

        if (stopped) {
            return;
        }

        switch (direction) {

            case NORTH:
                y -= speed;
                break;

            case SOUTH:
                y += speed;
                break;

            case EAST:
                x += speed;
                break;

            case WEST:
                x -= speed;
                break;
        }
    }
}