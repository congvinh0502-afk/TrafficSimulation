package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

/**
 * Ô tô — phương tiện cá nhân phổ biến nhất.
 *
 * <p>
 * Mặc định dùng {@link NormalDriver} (tuân thủ đèn giao thông).
 * Kiểu lái có thể bị ghi đè bởi {@link manager.VehicleSpawnManager}
 * để tạo ra các tài xế hung hăng.
 * </p>
 */
public class Car extends Vehicle {

    private static final double WIDTH = 50;
    private static final double HEIGHT = 28;

    /**
     * @param x         tọa độ X ban đầu
     * @param y         tọa độ Y ban đầu
     * @param direction hướng di chuyển
     */
    public Car(double x, double y, Direction direction) {
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