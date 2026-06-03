package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

public class Car extends Vehicle {

    public Car(double x, double y, Direction direction) {

        super(x, y, direction);

        width = 50;
        height = 28;

        // [FIX D-05] XÃ³a "speed = 2" thá»«a (bá»‹ ghi Ä‘Ã¨ ngay bÃªn dÆ°á»›i).
        // XÃ³a "this.direction = direction" thá»«a (super() Ä‘Ã£ gÃ¡n rá»“i).
        behavior = new NormalDriver();
        speed = 4.0;//behavior.getSpeed();
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