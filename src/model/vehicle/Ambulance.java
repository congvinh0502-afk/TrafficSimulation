package model.vehicle;

import strategy.driver.EmergencyDriver;
import util.Direction;

/**
 * Xe cứu thương — phương tiện ưu tiên cao nhất.
 *
 * <p>
 * Dùng {@link EmergencyDriver}: không dừng đèn đỏ,
 * tốc độ cao, có đèn nháy đặc trưng khi render.
 * Các xe thường sẽ nhường đường khi xe này đến gần.
 * </p>
 */
public class Ambulance extends Vehicle {

    private static final double WIDTH = 65;
    private static final double HEIGHT = 36;

    public Ambulance(double x, double y, Direction direction) {
        super(x, y, direction);
        width = WIDTH;
        height = HEIGHT;
        behavior = new EmergencyDriver();
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