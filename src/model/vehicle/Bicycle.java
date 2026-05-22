package model.vehicle;

import util.Direction;

public class Bicycle extends Vehicle {

    public Bicycle(
            double x,
            double y,
            Direction direction
    ) {

        super(x, y, direction);

        this.speed = 2;

        this.width = 20;
        this.height = 40;
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