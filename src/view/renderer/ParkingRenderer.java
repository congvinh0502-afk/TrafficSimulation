package view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 * Renderer bãi đỗ xe — vẽ bãi đỗ với ô kẻ và xe đậu.
 *
 * <p>
 * Màu xe đậu được chọn ngẫu nhiên nhưng ổn định theo tọa độ
 * (dùng {@code seed = x * y}) để không thay đổi mỗi frame.
 * </p>
 */
public class ParkingRenderer {

    private static final Color[] CAR_COLORS = {
            new Color(40, 115, 210),
            new Color(230, 185, 15),
            new Color(45, 155, 85),
            new Color(225, 225, 230),
            new Color(35, 35, 40),
            new Color(235, 95, 30)
    };

    /**
     * Vẽ bãi đỗ xe với ô kẻ và xe đậu ngẫu nhiên.
     *
     * @param g2d đối tượng đồ họa
     * @param x   X góc trên-trái
     * @param y   Y góc trên-trái
     * @param w   chiều rộng bãi đỗ
     * @param h   chiều cao bãi đỗ
     */
    public void drawParkingLotWithCars(Graphics2D g2d, int x, int y, int w, int h) {
        // Nền bãi đỗ
        g2d.setColor(new Color(75, 75, 80));
        g2d.fillRect(x, y, w, h);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(x, y, w, h);

        int slotW = 45, slotH = 65, index = 0;
        Random rng = new Random((long) x * y); // seed ổn định theo vị trí

        for (int cx = x + 15; cx < x + w - slotW; cx += slotW + 10) {
            // Vẽ ô đỗ hàng trên và hàng dưới
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.drawRect(cx, y + 5, slotW, slotH);
            g2d.drawRect(cx, y + h - slotH - 5, slotW, slotH);

            Color topColor = CAR_COLORS[rng.nextInt(CAR_COLORS.length)];
            Color bottomColor = CAR_COLORS[rng.nextInt(CAR_COLORS.length)];

            // Xe đậu theo điều kiện để không lấp đầy 100%
            if (index % 2 == 0) {
                drawParkedCar(g2d,
                        cx + (slotW - 24) / 2,
                        y + 5 + (slotH - 38) / 2,
                        topColor, true);
            }
            if (index % 3 == 0 || index == 1) {
                drawParkedCar(g2d,
                        cx + (slotW - 24) / 2,
                        (y + h - slotH - 5) + (slotH - 38) / 2,
                        bottomColor, false);
            }
            index++;
        }
    }

    /** Vẽ một xe đang đỗ trong ô. */
    private void drawParkedCar(Graphics2D g, int x, int y, Color carColor, boolean headingSouth) {
        int cw = 24, ch = 38;

        // Bánh xe
        g.setColor(Color.BLACK);
        g.fillRoundRect(x - 2, y + 5, 4, 8, 2, 2);
        g.fillRoundRect(x + cw - 2, y + 5, 4, 8, 2, 2);
        g.fillRoundRect(x - 2, y + ch - 13, 4, 8, 2, 2);
        g.fillRoundRect(x + cw - 2, y + ch - 13, 4, 8, 2, 2);

        // Thân xe
        g.setColor(carColor);
        g.fillRoundRect(x, y, cw, ch, 6, 6);
        g.setColor(carColor.darker());
        g.drawRoundRect(x, y, cw, ch, 6, 6);

        // Kính + đèn
        g.setColor(new Color(50, 50, 60));
        if (headingSouth) {
            g.fillRect(x + 3, y + ch - 15, cw - 6, 8);
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(x + 3, y + ch - 2, 5, 2);
            g.fillRect(x + cw - 8, y + ch - 2, 5, 2);
        } else {
            g.fillRect(x + 3, y + 8, cw - 6, 8);
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(x + 3, y, 5, 2);
            g.fillRect(x + cw - 8, y, 5, 2);
        }
    }
}