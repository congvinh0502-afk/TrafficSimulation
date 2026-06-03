package model.vehicle;

import config.Constants;
import strategy.driver.DriverBehavior;
import util.Direction;
import util.Lane;
import util.TurnType;

/**
 * Lớp cơ sở trừu tượng cho tất cả phương tiện.
 *
 * <p>
 * Quản lý:
 * <ul>
 * <li>Vị trí và góc hiển thị ({@code x, y, angle})</li>
 * <li>Trạng thái di chuyển ({@code stopped, turning, changingLane})</li>
 * <li>Chiến lược lái ({@link DriverBehavior})</li>
 * <li>Thông tin làn đường và loại rẽ</li>
 * </ul>
 * </p>
 *
 * <p>
 * Các lớp con phải implement {@link #move()} — định nghĩa
 * cách xe dịch chuyển theo {@code direction} mỗi frame.
 * </p>
 *
 * <p>
 * Lớp này chỉ chứa dữ liệu và logic di chuyển đơn giản.
 * Toàn bộ logic phức tạp (rẽ, đổi làn, va chạm, đèn) nằm
 * trong package {@code system.*}.
 * </p>
 */
public abstract class Vehicle {

    // ----------------------------------------------------------
    // Vị trí và kích thước
    // ----------------------------------------------------------
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // ----------------------------------------------------------
    // Chuyển động
    // ----------------------------------------------------------
    protected double speed;
    protected Direction direction;
    protected boolean stopped;

    // ----------------------------------------------------------
    // Góc hiển thị (để xoay hình ảnh xe)
    // ----------------------------------------------------------
    protected double angle;
    protected double targetAngle;

    // ----------------------------------------------------------
    // Rẽ
    // ----------------------------------------------------------
    protected boolean turning;
    protected boolean turned; // đã rẽ trong giao lộ này rồi
    protected Direction targetDirection; // hướng đích sau khi rẽ xong
    protected TurnType turnType;

    // ----------------------------------------------------------
    // Làn đường
    // ----------------------------------------------------------
    protected Lane lane;
    protected boolean changingLane;
    protected Lane targetLane;
    private int laneChangeCooldown;

    // ----------------------------------------------------------
    // Target vị trí (dùng cho alignment sau rẽ)
    // ----------------------------------------------------------
    protected double targetX;
    protected double targetY;

    // ----------------------------------------------------------
    // Chiến lược lái xe
    // ----------------------------------------------------------
    protected DriverBehavior behavior;

    // ==========================================================
    // Constructor
    // ==========================================================

    /**
     * Khởi tạo xe tại vị trí (x, y) với hướng cho trước.
     * Góc ban đầu được thiết lập phù hợp với hướng.
     *
     * @param x         tọa độ X ban đầu
     * @param y         tọa độ Y ban đầu
     * @param direction hướng di chuyển ban đầu
     */
    protected Vehicle(double x, double y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.lane = Lane.RIGHT;
        this.angle = initialAngle(direction);
    }

    /** Tính góc xoay ban đầu tương ứng với hướng di chuyển. */
    private static double initialAngle(Direction direction) {
        switch (direction) {
            case EAST:
                return Constants.ANGLE_EAST;
            case SOUTH:
                return Constants.ANGLE_SOUTH;
            case WEST:
                return Constants.ANGLE_WEST;
            case NORTH:
                return Constants.ANGLE_NORTH;
            case NORTHEAST:
                return Constants.ANGLE_NORTHEAST;
            default:
                return 0;
        }
    }

    // ==========================================================
    // Phương thức trừu tượng
    // ==========================================================

    /**
     * Dịch chuyển xe một bước theo {@code direction} và {@code speed}.
     * Gọi mỗi frame nếu xe không bị dừng.
     */
    public abstract void move();

    // ==========================================================
    // Getter / Setter — vị trí
    // ==========================================================

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // ==========================================================
    // Getter / Setter — chuyển động
    // ==========================================================

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction d) {
        this.direction = d;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    // ==========================================================
    // Getter / Setter — góc
    // ==========================================================

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getTargetAngle() {
        return targetAngle;
    }

    public void setTargetAngle(double a) {
        this.targetAngle = a;
    }

    // ==========================================================
    // Getter / Setter — rẽ
    // ==========================================================

    public boolean isTurning() {
        return turning;
    }

    public void setTurning(boolean turning) {
        this.turning = turning;
    }

    public boolean hasTurned() {
        return turned;
    }

    public void setTurned(boolean turned) {
        this.turned = turned;
    }

    public Direction getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(Direction d) {
        this.targetDirection = d;
    }

    public TurnType getTurnType() {
        return turnType;
    }

    public void setTurnType(TurnType turnType) {
        this.turnType = turnType;
    }

    // ==========================================================
    // Getter / Setter — làn đường
    // ==========================================================

    public Lane getLane() {
        return lane;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    public boolean isChangingLane() {
        return changingLane;
    }

    public void setChangingLane(boolean b) {
        this.changingLane = b;
    }

    public Lane getTargetLane() {
        return targetLane;
    }

    public void setTargetLane(Lane targetLane) {
        this.targetLane = targetLane;
    }

    public int getLaneChangeCooldown() {
        return laneChangeCooldown;
    }

    public void setLaneChangeCooldown(int cooldown) {
        this.laneChangeCooldown = cooldown;
    }

    // ==========================================================
    // Getter / Setter — target position
    // ==========================================================

    public double getTargetX() {
        return targetX;
    }

    public void setTargetX(double tx) {
        this.targetX = tx;
    }

    public double getTargetY() {
        return targetY;
    }

    public void setTargetY(double ty) {
        this.targetY = ty;
    }

    // ==========================================================
    // Getter / Setter — chiến lược lái
    // ==========================================================

    public DriverBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(DriverBehavior b) {
        this.behavior = b;
    }
}