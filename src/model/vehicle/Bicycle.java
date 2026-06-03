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