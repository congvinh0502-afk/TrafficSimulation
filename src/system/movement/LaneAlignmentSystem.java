package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import util.Direction; // <-- Dòng import bắt buộc phải có

/**
 * Hệ thống căn giữa làn đường.
 * Tích hợp công thức hình học để bám làn trên các đường chéo 72 độ.
 */
public class LaneAlignmentSystem {

    public void alignToLane(Vehicle vehicle, IntersectionLayout layout) {
        if (vehicle.isChangingLane() || vehicle.isTurning())
            return;

        boolean postTurn = vehicle.isPostTurnAligning();
        double smoothFactor = postTurn ? Constants.POST_TURN_ALIGN_FACTOR : Constants.LANE_ALIGN_SMOOTH_FACTOR;
        double maxSmooth = postTurn ? 4.0 : Constants.LANE_ALIGN_MAX_SMOOTH;
        double smooth = Math.min(vehicle.getSpeed() * smoothFactor + (postTurn ? 0.5 : 0), maxSmooth);

        Direction dir = vehicle.getDirection();
        boolean isFiveWay = layout.getDirections().size() == 5;

        if (!isFiveWay && (dir == Direction.NORTH || dir == Direction.SOUTH || dir == Direction.EAST
                || dir == Direction.WEST)) {
            if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                int targetX = layout.getLaneCenterX(dir, vehicle.getLane());
                if (targetX == 0)
                    return;
                double diff = targetX - vehicle.getX();
                if (postTurn && Math.abs(diff) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    vehicle.setX(targetX);
                    vehicle.setPostTurnAligning(false);
                } else {
                    vehicle.setX(vehicle.getX() + Math.signum(diff) * Math.min(smooth, Math.abs(diff)));
                }
            } else {
                int targetY = layout.getLaneCenterY(dir, vehicle.getLane());
                if (targetY == 0)
                    return;
                double diff = targetY - vehicle.getY();
                if (postTurn && Math.abs(diff) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    vehicle.setY(targetY);
                    vehicle.setPostTurnAligning(false);
                } else {
                    vehicle.setY(vehicle.getY() + Math.signum(diff) * Math.min(smooth, Math.abs(diff)));
                }
            }
        } else {
            // Lực hút toán học: giữ xe ngã 5 bám đúng đường 72 độ
            double laneOffset = (vehicle.getLane() == util.Lane.RIGHT) ? 25.0 : -25.0;
            double rad = Math.toRadians(dir.toAngleDeg());
            double nx = Math.cos(rad + Math.PI / 2);
            double ny = Math.sin(rad + Math.PI / 2);

            double linePX = layout.getCx() + nx * laneOffset;
            double linePY = layout.getCy() + ny * laneOffset;

            double dx = vehicle.getX() - linePX;
            double dy = vehicle.getY() - linePY;
            double dist = dx * nx + dy * ny;

            if (Math.abs(dist) > 0.1) {
                double correction = Math.signum(dist) * Math.min(smooth, Math.abs(dist));
                if (postTurn && Math.abs(dist) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    vehicle.setX(vehicle.getX() - dist * nx);
                    vehicle.setY(vehicle.getY() - dist * ny);
                    vehicle.setPostTurnAligning(false);
                } else {
                    vehicle.setX(vehicle.getX() - correction * nx);
                    vehicle.setY(vehicle.getY() - correction * ny);
                }
            }
        }
    }
}