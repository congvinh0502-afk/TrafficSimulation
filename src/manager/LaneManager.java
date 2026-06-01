package manager;

import util.Direction;
import util.Lane;

public class LaneManager {

    public static int getLaneCenterX(
        Direction direction,
        Lane lane
) {

    switch (direction) {

        case NORTH:
            return lane == Lane.RIGHT ? 430 : 470;

        case SOUTH:
            return lane == Lane.RIGHT ? 370 : 330;

        case NORTHEAST:
            return lane == Lane.LEFT ? 360 : 420;

        default:
            return 0;
    }
}

    public static int getLaneCenterY(
        Direction direction,
        Lane lane
) {

    switch (direction) {

        case EAST:
            // đi từ trái sang phải
            // làn phải = phía dưới
            return lane == Lane.RIGHT
                    ? 430
                    : 470;

        case WEST:
            // đi từ phải sang trái
            // làn phải = phía trên
            return lane == Lane.RIGHT
                    ? 370
                    : 330;

        case NORTHEAST:
            return lane == Lane.LEFT ? 640 : 580;

        default:
            return 0;
    }
}
}