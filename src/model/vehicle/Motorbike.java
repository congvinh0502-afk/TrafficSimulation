package model.vehicle;

import util.Direction;

public class Motorbike extends Vehicle {

    public Motorbike(
            double x,
            double y,
            Direction direction
    ) {

        super(x, y, direction);

        this.speed = 3.5;

        this.width = 32;
        this.height = 16;
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