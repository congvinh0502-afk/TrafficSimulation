package model.vehicle;

import util.Direction;
import strategy.driver.NormalDriver;

public class Car extends Vehicle {

    public Car(double x, double y, Direction direction) {

    super(x, y, direction);

    width = 60;
    height = 30;

    speed = 2;

    this.direction = direction;
    
    behavior = new NormalDriver();

    speed = behavior.getSpeed();
}

@Override
public void move() {

    if (stopped) {
        return;
    }

    switch (direction) {

        case SOUTH:
            y += speed;
            break;

        case NORTH:
            y -= speed;
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