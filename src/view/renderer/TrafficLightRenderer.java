package view.renderer;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Renderer đèn giao thông.
 *
 * <p>
 * Vẽ hộp đèn (3 bóng: đỏ-vàng-xanh), cột đỡ, và
 * bảng đếm ngược tùy thuộc vào {@code lightType}:
 * <ul>
 * <li>"NO COUNTDOWN" — không hiển thị số</li>
 * <li>"ALWAYS COUNTDOWN" — luôn hiển thị số giây</li>
 * <li>"COUNT &lt;= 10" — chỉ hiển thị khi còn ≤ 10 giây</li>
 * </ul>
 * </p>
 */
public class TrafficLightRenderer {

    // Kích thước hộp đèn
    private static final int BOX_WIDTH = 42;
    private static final int BOX_HEIGHT = 110;
    private static final int BOX_ARC = 12;

    // Kích thước bóng đèn
    private static final int BULB_SIZE = 30;
    private static final int BULB_OFFSET = 6;
    private static final int GLOW_SIZE = 42;
    private static final int GLOW_OFFSET = 6;

    // Khoảng cách giữa tâm các bóng
    private static final int BULB_SPACING = 34;

    /**
     * Vẽ đèn giao thông tại vị trí (x, y).
     *
     * @param g2d       đối tượng đồ họa
     * @param light     trạng thái đèn cần hiển thị
     * @param x         tọa độ X góc trên-trái hộp đèn
     * @param y         tọa độ Y góc trên-trái hộp đèn
     * @param lightType kiểu hiển thị đếm ngược
     */
    public void render(Graphics2D g2d, TrafficLight light, int x, int y, String lightType) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBox(g, x, y);
        drawBulb(g, x + BULB_OFFSET, y + BULB_OFFSET, Color.RED, light.getColor() == LightColor.RED);
        drawBulb(g, x + BULB_OFFSET, y + BULB_OFFSET + 34, Color.YELLOW, light.getColor() == LightColor.YELLOW);
        drawBulb(g, x + BULB_OFFSET, y + BULB_OFFSET + 68, Color.GREEN, light.getColor() == LightColor.GREEN);
        drawPole(g, x, y);

        if (shouldShowTimer(light, lightType)) {
            drawTimer(g, x, y, light.getTimer() / 60);
        }

        g.dispose();
    }

    // ----------------------------------------------------------
    // Các phần của đèn
    // ----------------------------------------------------------

    /** Vẽ hộp đèn (nền tối, viền đen). */
    private void drawBox(Graphics2D g, int x, int y) {
        g.setColor(new Color(35, 35, 35));
        g.fillRoundRect(x, y, BOX_WIDTH, BOX_HEIGHT, BOX_ARC, BOX_ARC);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, BOX_WIDTH, BOX_HEIGHT, BOX_ARC, BOX_ARC);
    }

    /** Vẽ cột đỡ đèn. */
    private void drawPole(Graphics2D g, int x, int y) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 18, y + BOX_HEIGHT, 6, 35);
    }

    /**
     * Vẽ một bóng đèn.
     *
     * @param g      đối tượng đồ họa
     * @param x      X góc trên-trái bóng
     * @param y      Y góc trên-trái bóng
     * @param color  màu bóng khi sáng
     * @param active true = đang sáng
     */
    private void drawBulb(Graphics2D g, int x, int y, Color color, boolean active) {
        // Hào quang khi sáng
        if (active) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
            g.fillOval(x - GLOW_OFFSET, y - GLOW_OFFSET, GLOW_SIZE, GLOW_SIZE);
        }

        // Thân bóng
        g.setColor(active ? color : color.darker().darker());
        g.fillOval(x, y, BULB_SIZE, BULB_SIZE);

        // Highlight phản chiếu
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(x + 6, y + 5, 10, 10);
    }

    /** Vẽ bảng đếm ngược phía trên hộp đèn. */
    private void drawTimer(Graphics2D g, int x, int y, int seconds) {
        // Nền bảng
        g.setColor(new Color(20, 20, 20));
        g.fillRoundRect(x - 2, y - 32, 46, 24, 8, 8);

        // Số giây
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String text = String.valueOf(seconds);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x + (BOX_WIDTH - fm.stringWidth(text)) / 2, y - 14);
    }

    /** Quyết định có hiển thị đếm ngược không dựa trên cài đặt. */
    private boolean shouldShowTimer(TrafficLight light, String lightType) {
        switch (lightType) {
            case "ALWAYS COUNTDOWN":
                return true;
            case "COUNT <= 10":
                return light.getTimer() / 60 <= 10;
            default:
                return false; // "NO COUNTDOWN"
        }
    }
}