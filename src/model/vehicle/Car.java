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

    /*@Override
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