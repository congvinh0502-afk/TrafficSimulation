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

        // [FIX N-01] TrÆ°á»›c Ä‘Ã¢y behavior = null â†’ NullPointerException
        // sau khi fix C-01 gá»i vehicle.getBehavior().shouldStop().
        // Xe Ä‘áº¡p dÃ¹ng NormalDriver; tá»‘c Ä‘á»™ láº¥y tá»« behavior Ä‘á»ƒ nháº¥t quÃ¡n.
        behavior = new NormalDriver();
        speed = 2.5;//behavior.getSpeed(); // NormalDriver.getSpeed() = 4

        // Náº¿u muá»‘n xe Ä‘áº¡p cháº­m hÆ¡n Car, override láº¡i:
        // speed = 2;

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