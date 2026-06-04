package strategy.turn;

import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;

/**
 * Interface chuẩn cho mọi chiến lược rẽ tại giao lộ.
 */
public interface TurnStrategy {
    void handleTurning(Vehicle vehicle, IntersectionLayout layout);

    void smoothTurning(Vehicle vehicle, IntersectionLayout layout);
}