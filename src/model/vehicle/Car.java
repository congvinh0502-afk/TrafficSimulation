package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

public class Car extends Vehicle {

    public Car(double x, double y, Direction direction) {

        super(x, y, direction);

        width = 50;
        height = 28;

        // [FIX D-05] Xóa "speed = 2" thừa (bị ghi đè ngay bên dưới).
        // Xóa "this.direction = direction" thừa (super() đã gán rồi).
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