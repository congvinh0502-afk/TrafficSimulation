package model.vehicle;

import strategy.driver.EmergencyDriver;
import util.Direction;

/**
 * Xe cứu hỏa — phương tiện ưu tiên, kích thước lớn nhất.
 *
 * <p>
 * Tương tự {@link Ambulance} về hành vi nhưng to hơn,
 * thích hợp mô phỏng tình huống chiếm nhiều diện tích đường.
 * </p>
 */
public class FireTruck extends Vehicle {

    private static final double WIDTH = 80;
    private static final double HEIGHT = 42;

    public FireTruck(double x, double y, Direction direction) {
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