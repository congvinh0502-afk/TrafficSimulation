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

                return lane == Lane.LEFT
                        ? 430
                        : 470;

            case SOUTH:

                return lane == Lane.LEFT
                        ? 330
                        : 370;
            // getLaneCenterX
case NORTHEAST:
    return lane == Lane.LEFT ? 430 : 470;


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

                return lane == Lane.LEFT
                        ? 330
                        : 370;

            case WEST:

                return lane == Lane.LEFT
                        ? 430
                        : 470;
            // getLaneCenterY  
case NORTHEAST:
    return lane == Lane.LEFT ? 430 : 470;

            default:

                return 0;
        }
    }
}