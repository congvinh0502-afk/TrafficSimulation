package model.vehicle;

import strategy.driver.EmergencyDriver;
import util.Direction;

public class FireTruck extends Vehicle {

    public FireTruck(
            double x,
            double y,
            Direction direction
    ) {

        super(x, y, direction);

        width = 80;
        height = 42;

        behavior = new EmergencyDriver();

        speed = behavior.getSpeed();
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