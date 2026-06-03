package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

/**
 * Xe đạp — chậm nhất, tuân thủ luật giao thông.
 *
 * <p>
 * Dùng {@link NormalDriver} với tốc độ thấp.
 * Kích thước nhỏ — dễ len lỏi giữa các xe.
 * </p>
 */
public class Bicycle extends Vehicle {

    private static final double WIDTH = 34;
    private static final double HEIGHT = 16;

    public Bicycle(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH;
        height = HEIGHT;
        behavior = new NormalDriver();
        speed = behavior.getSpeed();
    }

    @Override
    public void move() {
        if (stopped)
            return;
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
            default:
                break;
        }
    }
}