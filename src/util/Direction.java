package util;

import math.Vector2D;

public enum Direction {

    // --- CÁC HƯỚNG CHUẨN CỦA NGÃ 4 ---
    NORTH(new Vector2D(0, -1), -90),
    SOUTH(new Vector2D(0, 1), 90),
    EAST(new Vector2D(1, 0), 0),
    WEST(new Vector2D(-1, 0), 180),
    NORTHEAST(new Vector2D(Math.sqrt(2) / 2, -Math.sqrt(2) / 2), -45),

    // --- NGÃ 5: XE TỪ NHÁNH ĐI VÀO TÂM (INBOUND) ---
    // Nhánh 270 độ (Top) đi vào -> Đi hướng SOUTH (90)
    FW_IN_342(Vector2D.fromAngle(162), 162), // Nhánh Top-Right đi vào
    FW_IN_54(Vector2D.fromAngle(234), 234), // Nhánh Bot-Right đi vào
    FW_IN_126(Vector2D.fromAngle(306), 306), // Nhánh Bot-Left đi vào
    FW_IN_198(Vector2D.fromAngle(18), 18), // Nhánh Top-Left đi vào

    // --- NGÃ 5: XE TỪ TÂM ĐI RA NGOÀI (OUTBOUND) ---
    // Nhánh 270 độ (Top) đi ra -> Đi hướng NORTH (-90)
    FW_OUT_342(Vector2D.fromAngle(342), 342),
    FW_OUT_54(Vector2D.fromAngle(54), 54),
    FW_OUT_126(Vector2D.fromAngle(126), 126),
    FW_OUT_198(Vector2D.fromAngle(198), 198);

    private final Vector2D vector;
    private final double angleDeg;

    Direction(Vector2D vector, double angleDeg) {
        this.vector = vector;
        this.angleDeg = angleDeg;
    }

    public Vector2D toVector() {
        return vector;
    }

    public double toAngleDeg() {
        return angleDeg;
    }

    public Direction opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            case FW_IN_342:
                return FW_OUT_342;
            case FW_IN_54:
                return FW_OUT_54;
            case FW_IN_126:
                return FW_OUT_126;
            case FW_IN_198:
                return FW_OUT_198;
            case FW_OUT_342:
                return FW_IN_342;
            case FW_OUT_54:
                return FW_IN_54;
            case FW_OUT_126:
                return FW_IN_126;
            case FW_OUT_198:
                return FW_IN_198;
            default:
                return this;
        }
    }
}