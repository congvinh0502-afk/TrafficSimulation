package model.trafficlight;

import config.Constants;

/**
 * Mô hình đèn giao thông tại một hướng.
 *
 * <p>
 * Mỗi frame gọi {@link #update()} để đếm ngược timer.
 * Khi timer về 0, đèn tự chuyển theo chu kỳ:
 * GREEN → YELLOW → RED → GREEN → ...
 * </p>
 *
 * <p>
 * Có thể ghi đè màu và timer thủ công thông qua
 * {@link #setColor(LightColor)} và {@link #setTimer(int)}
 * khi chế độ MANUAL hoặc khi có xe ưu tiên.
 * </p>
 */
public class TrafficLight {

    private LightColor color;
    private int timer;

    /**
     * Tạo đèn với màu và thời gian ban đầu.
     *
     * @param color màu khởi đầu
     * @param timer số frame còn lại của pha này
     */
    public TrafficLight(LightColor color, int timer) {
        this.color = color;
        this.timer = timer;
    }

    /**
     * Cập nhật mỗi frame — giảm timer; chuyển màu khi hết giờ.
     */
    public void update() {
        timer--;
        if (timer <= 0) {
            switchLight();
        }
    }

    /** Chuyển sang pha đèn tiếp theo và đặt lại timer. */
    private void switchLight() {
        switch (color) {
            case GREEN:
                color = LightColor.YELLOW;
                timer = Constants.LIGHT_YELLOW_DURATION;
                break;
            case YELLOW:
                color = LightColor.RED;
                timer = Constants.LIGHT_RED_DURATION;
                break;
            case RED:
                color = LightColor.GREEN;
                timer = Constants.LIGHT_GREEN_DURATION;
                break;
        }
    }

    // ----------------------------------------------------------
    // Getter / Setter
    // ----------------------------------------------------------

    /** @return màu đèn hiện tại */
    public LightColor getColor() {
        return color;
    }

    /** @return số frame còn lại của pha này */
    public int getTimer() {
        return timer;
    }

    /**
     * Đặt màu đèn thủ công (dùng cho chế độ MANUAL hoặc xe ưu tiên).
     *
     * @param color màu mới
     */
    public void setColor(LightColor color) {
        this.color = color;
    }

    /**
     * Đặt lại timer (dùng kèm với {@link #setColor}).
     *
     * @param timer số frame
     */
    public void setTimer(int timer) {
        this.timer = timer;
    }
}