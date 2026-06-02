package model.trafficlight;
public class TrafficLight {

    private LightColor color;
    private long timerMs; // đổi từ int ticks → long milliseconds

    public TrafficLight(LightColor color, long timerMs) {
        this.color   = color;
        this.timerMs = timerMs;
    }

    // Thay update() cũ bằng:
    public void update(long deltaMs) {
        timerMs -= deltaMs;
        if (timerMs <= 0) {
            switchLight();
        }
    }

    private void switchLight() {
        switch (color) {
            case GREEN:
                color   = LightColor.YELLOW;
                timerMs = 3_000;   // 3 giây
                break;
            case YELLOW:
                color   = LightColor.RED;
                timerMs = 15_000;  // 15 giây
                break;
            case RED:
                color   = LightColor.GREEN;
                timerMs = 12_000;  // 12 giây
                break;
        }
    }

    // getter trả về giây để hiển thị countdown
    public int getTimerSeconds() {
        return (int) Math.ceil(timerMs / 1000.0);
    }

    public long getTimerMs() { return timerMs; }
    public void setTimerMs(long ms) { this.timerMs = Math.max(0, ms); }

    // Giữ lại getTimer/setTimer cũ nếu code khác vẫn dùng
    public int getTimer() { return (int) timerMs; }
    public void setTimer(int t) { this.timerMs = t; }

    public LightColor getColor() { return color; }
    public void setColor(LightColor color) { this.color = color; }
}