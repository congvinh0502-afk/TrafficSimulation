package view.renderer;

import java.awt.*;
import model.trafficlight.LightColor;
import model.trafficlight.LightDisplayMode;
import model.trafficlight.TrafficLight;

/**
 * TrafficLightRenderer — vẽ đèn giao thông.
 */
public class TrafficLightRenderer {

    // Kích thước đèn (không thay đổi)
    public static final int LIGHT_WIDTH  = 42;
    public static final int LIGHT_HEIGHT = 110;
    public static final int POLE_HEIGHT  = 35;
    public static final int TOTAL_HEIGHT = LIGHT_HEIGHT + POLE_HEIGHT; // 145

    // ─────────────────────────────────────────────────────────────
    // RENDER (overload nhận enum)
    // ─────────────────────────────────────────────────────────────
    public void render(Graphics2D g2d, TrafficLight light, int x, int y, LightDisplayMode mode) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ── BODY ──
        g.setColor(new Color(35, 35, 35));
        g.fillRoundRect(x, y, LIGHT_WIDTH, LIGHT_HEIGHT, 12, 12);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, LIGHT_WIDTH, LIGHT_HEIGHT, 12, 12);

        // ── 3 BÓNG ĐÈN ──
        drawLight(g, x + 6, y + 6, Color.RED, light.getColor() == LightColor.RED);
        drawLight(g, x + 6, y + 40, Color.YELLOW, light.getColor() == LightColor.YELLOW);
        drawLight(g, x + 6, y + 74, Color.GREEN, light.getColor() == LightColor.GREEN);

        // ── CỘT ──
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 18, y + LIGHT_HEIGHT, 6, POLE_HEIGHT);

        // ── TIMER ──
        boolean showTimer = false;
        switch (mode) {
            case ALWAYS_COUNTDOWN:
                showTimer = true;
                break;
            case LAST_10S_COUNTDOWN:
                // SỬA Ở ĐÂY: Dùng trực tiếp số giây từ hàm getTimerSeconds()
                showTimer = light.getTimerSeconds() <= 10; 
                break;
            case NO_COUNTDOWN:
            default:
                showTimer = false;
                break;
        }

        if (showTimer) {
            int seconds = light.getTimerSeconds();
            // nền timer
            g.setColor(new Color(20, 20, 20));
            g.fillRoundRect(x - 2, y - 32, 46, 24, 8, 8);

            // text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String text = String.valueOf(seconds);
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (LIGHT_WIDTH - fm.stringWidth(text)) / 2;
            g.drawString(text, textX, y - 14);
        }

        g.dispose();
    }

    // ─────────────────────────────────────────────────────────────
    // RENDER (overload nhận String)
    // ─────────────────────────────────────────────────────────────
    public void render(Graphics2D g2d, TrafficLight light, int x, int y, String lightType) {
        render(g2d, light, x, y, LightDisplayMode.fromString(lightType));
    }

    // ─────────────────────────────────────────────────────────────
    // BOUNDS — dùng cho click detection trong SimulationPanel
    // ─────────────────────────────────────────────────────────────
    public Rectangle getBounds(int x, int y) {
        return new Rectangle(x, y, LIGHT_WIDTH, TOTAL_HEIGHT);
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE: vẽ 1 bóng đèn
    // ─────────────────────────────────────────────────────────────
    private void drawLight(Graphics2D g, int x, int y, Color color, boolean active) {
        // glow
        if (active) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
            g.fillOval(x - 6, y - 6, 42, 42);
        }

        // thân bóng
        g.setColor(active ? color : color.darker().darker());
        g.fillOval(x, y, 30, 30);

        // highlight
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(x + 6, y + 5, 10, 10);
    }
}