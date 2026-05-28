package model.vehicle;

import strategy.driver.NormalDriver;
import util.Direction;

public class Bicycle extends Vehicle {

    public Bicycle(
            double x,
            double y,
            Direction direction
    ) {

        super(x, y, direction);

        // [FIX N-01] Trước đây behavior = null → NullPointerException
        // sau khi fix C-01 gọi vehicle.getBehavior().shouldStop().
        // Xe đạp dùng NormalDriver; tốc độ lấy từ behavior để nhất quán.
        behavior = new NormalDriver();
        speed = behavior.getSpeed(); // NormalDriver.getSpeed() = 4

        // Nếu muốn xe đạp chậm hơn Car, override lại:
        // speed = 2;

        this.width = 34;
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