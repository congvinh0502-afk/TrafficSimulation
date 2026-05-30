package view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Renderer nền — vẽ cỏ và đèn đường.
 *
 * <p>
 * Được gọi đầu tiên trong mỗi lần render để tạo lớp nền
 * trước khi các renderer khác vẽ đè lên.
 * </p>
 */
public class BaseRenderer {

    // Màu cỏ
    private static final Color GRASS_BASE = new Color(65, 142, 65);
    private static final Color GRASS_DETAIL = new Color(58, 132, 58);

    // Màu đèn đường
    private static final Color LIGHT_GLOW = new Color(255, 255, 150, 45);
    private static final Color LIGHT_POLE = Color.DARK_GRAY;
    private static final Color LIGHT_BULB = Color.WHITE;

    /** Vị trí bốn đèn đường tại các góc giao lộ. */
    private static final int[][] STREET_LIGHT_POSITIONS = {
            { 250, 250 }, { 550, 250 }, { 250, 550 }, { 550, 550 }
    };

    /**
     * Vẽ nền cỏ phủ toàn màn hình với chi tiết kết cấu nhỏ.
     *
     * @param g2d đối tượng đồ họa
     */
    public void drawGrass(Graphics2D g2d) {
        g2d.setColor(GRASS_BASE);
        g2d.fillRect(0, 0, 1200, 800);

        // Chi tiết kết cấu cỏ
        g2d.setColor(GRASS_DETAIL);
        for (int i = 0; i < 1200; i += 50) {
            for (int j = 0; j < 800; j += 50) {
                if ((i + j) % 3 == 0) {
                    g2d.fillOval(i + 15, j + 10, 6, 4);
                }
            }
        }
    }

    /**
     * Vẽ bốn đèn đường tại các góc giao lộ.
     *
     * @param g2d đối tượng đồ họa
     */
    public void drawStreetLights(Graphics2D g2d) {
        for (int[] pos : STREET_LIGHT_POSITIONS) {
            int px = pos[0];
            int py = pos[1];

            // Hào quang phát sáng
            g2d.setColor(LIGHT_GLOW);
            g2d.fillOval(px - 20, py - 20, 50, 50);

            // Đế đèn
            g2d.setColor(LIGHT_POLE);
            g2d.fillOval(px - 4, py - 4, 8, 8);

            // Bóng đèn
            g2d.setColor(LIGHT_BULB);
            g2d.fillOval(px - 2, py - 2, 4, 4);
        }
    }
}