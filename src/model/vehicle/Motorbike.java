package model.vehicle;

import strategy.driver.AggressiveDriver;
import util.Direction;

public class Motorbike extends Vehicle {

    public Motorbike(
            double x,
            double y,
            Direction direction
    ) {

        super(x, y, direction);

        // [FIX N-02] TrÆ°á»›c Ä‘Ã¢y: behavior = null, speed hardcode = 3.5.
        // Motorbike dÃ¹ng AggressiveDriver (cháº¡y nhanh, hung hÄƒng hÆ¡n Car).
        // AggressiveDriver.getSpeed() = 7 â€” nháº¥t quÃ¡n vá»›i Car/Ambulance/FireTruck.
        behavior = new AggressiveDriver();
        speed = 4.0;//behavior.getSpeed();

        this.width = 34;
        this.height = 16;
    }

    /*@Override
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
    }*/
    @Override
    public void move() {
        if (stopped) return;

        // LOGIC CHUYỂN ĐỘNG THEO ĐƯỜNG CONG RẼ
        if (followingPath && path != null && currentPathIndex < path.size()) {
            double[] target = path.get(currentPathIndex);
            double dx = target[0] - x;
            double dy = target[1] - y;
            double dist = Math.hypot(dx, dy);
            // --- THÊM DÒNG NÀY ĐỂ ĐẦU XE XOAY MƯỢT THEO TIẾP TUYẾN ---
            if (dist > 0.5) {
                this.angle = Math.toDegrees(Math.atan2(dy, dx));
            }
            if (dist < speed) {
                x = target[0];
                y = target[1];
                currentPathIndex++;
                if (currentPathIndex >= path.size()) {
                    followingPath = false;
                    //turning = false; // Báo hiệu đã rẽ xong
                }
            } else {
                x += (dx / dist) * speed;
                y += (dy / dist) * speed;
            }
            return; // Nếu đang rẽ quỹ đạo thì bỏ qua đi thẳng
        }

        // LOGIC ĐI THẲNG BÌNH THƯỜNG
        switch (direction) {
            case NORTH: y -= speed; break;
            case SOUTH: y += speed; break;
            case EAST:  x += speed; break;
            case WEST:  x -= speed; break;
            case NORTHEAST: 
                x += speed * 0.31; 
                y -= speed * 0.95; 
                break;
        }
    }
}