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
        speed = behavior.getSpeed();

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