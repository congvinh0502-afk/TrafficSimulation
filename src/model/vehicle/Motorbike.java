package model.vehicle;

import strategy.driver.AggressiveDriver;
import util.Direction;

/**
 * Xe máy — nhanh và hung hăng hơn ô tô.
 *
 * <p>
 * Mặc định dùng {@link AggressiveDriver}:
 * tốc độ cao, có xác suất vượt đèn đỏ.
 * </p>
 */
public class Motorbike extends Vehicle {

    private static final double WIDTH = 34;
    private static final double HEIGHT = 16;

    public Motorbike(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH;
        height = HEIGHT;
        behavior = new AggressiveDriver();
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