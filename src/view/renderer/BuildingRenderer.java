package view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Renderer công trình — vẽ các loại toà nhà trang trí bản đồ.
 *
 * <p>
 * Bốn loại công trình:
 * <ul>
 * <li>{@link #drawSkyscraper} — toà nhà cao tầng</li>
 * <li>{@link #drawLuxuryHouse} — biệt thự</li>
 * <li>{@link #drawModernFactory} — nhà máy hiện đại</li>
 * <li>{@link #drawLuxuryRestaurant} — nhà hàng sang trọng</li>
 * </ul>
 * </p>
 */
public class BuildingRenderer {

    // ==========================================================
    // Toà nhà cao tầng
    // ==========================================================

    /**
     * Vẽ toà nhà cao tầng với cửa sổ và ăng-ten.
     *
     * @param g2d đối tượng đồ họa
     * @param x   tọa độ X góc trên-trái
     * @param y   tọa độ Y góc trên-trái
     */
    public void drawSkyscraper(Graphics2D g2d, int x, int y) {
        int w = 110, h = 150;

        // Bóng đổ
        g2d.setColor(new Color(40, 50, 40, 50));
        g2d.fillRect(x + 10, y + 10, w, h);

        // Thân tòa nhà
        g2d.setColor(new Color(45, 60, 80));
        g2d.fillRect(x, y, w, h);
        g2d.setColor(new Color(30, 40, 55));
        g2d.drawRect(x, y, w, h);

        // Mặt kính
        g2d.setColor(new Color(75, 140, 210));
        g2d.fillRect(x + 15, y, w - 30, h);

        // Cửa sổ
        int winW = 8, winH = 12;
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 5; c++) {
                int winX = x + 20 + c * (winW + 8);
                int winY = y + 15 + r * (winH + 7);
                boolean lit = (r + c) % 3 == 0 || (r == 2 && c == 4);
                g2d.setColor(lit ? new Color(255, 235, 130) : new Color(110, 180, 240));
                g2d.fillRect(winX, winY, winW, winH);
            }
        }

        // Ăng-ten
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(x + w / 2 - 2, y - 20, 4, 20);
        g2d.setColor(Color.RED);
        g2d.fillOval(x + w / 2 - 4, y - 24, 8, 8);
    }

    // ==========================================================
    // Biệt thự
    // ==========================================================

    /**
     * Vẽ biệt thự với kiến trúc nhiều lớp và mái vòm.
     *
     * @param g2d đối tượng đồ họa
     * @param x   tọa độ X góc trên-trái
     * @param y   tọa độ Y góc trên-trái
     */
    public void drawLuxuryHouse(Graphics2D g2d, int x, int y) {
        int baseW = 85, baseH = 75;

        // Bóng đổ
        g2d.setColor(new Color(40, 65, 40, 60));
        g2d.fillRect(x + 6, y + 6, baseW, baseH);

        // Thân nhà
        g2d.setColor(new Color(245, 240, 230));
        g2d.fillRect(x, y, baseW, baseH);
        g2d.setColor(new Color(180, 170, 150));
        g2d.drawRect(x, y, baseW, baseH);

        // Lớp tường gạch
        int l1x = x + 12, l1y = y + 10, l1w = baseW - 24, l1h = baseH - 20;
        g2d.setColor(new Color(210, 105, 30));
        g2d.fillRect(l1x, l1y, l1w, l1h);

        // Mái
        int rx = l1x + 6, ry = l1y + 6, rw = l1w - 12, rh = l1h - 12;
        g2d.setColor(new Color(45, 85, 150));
        g2d.fillRect(rx, ry, rw, rh);

        // Đường viền mái chéo
        g2d.setColor(new Color(25, 50, 100));
        int cx = rx + rw / 2, cy = ry + rh / 2;
        g2d.drawLine(rx, ry, cx, cy);
        g2d.drawLine(rx + rw, ry, cx, cy);
        g2d.drawLine(rx, ry + rh, cx, cy);
        g2d.drawLine(rx + rw, ry + rh, cx, cy);

        // Cửa sổ
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRect(cx - 8, cy - 6, 16, 12);
        g2d.setColor(new Color(25, 50, 100));
        g2d.drawRect(cx - 8, cy - 6, 16, 12);
        g2d.setColor(new Color(135, 206, 250, 180));
        g2d.fillRect(cx - 4, cy - 3, 8, 6);
    }

    // ==========================================================
    // Nhà máy hiện đại
    // ==========================================================

    /**
     * Vẽ nhà máy với mái răng cưa và ống khói.
     *
     * @param g2d đối tượng đồ họa
     * @param x   tọa độ X góc trên-trái
     * @param y   tọa độ Y góc trên-trái
     */
    public void drawModernFactory(Graphics2D g2d, int x, int y) {
        int w = 120, h = 75;

        // Bóng đổ
        g2d.setColor(new Color(40, 50, 40, 50));
        g2d.fillRect(x + 6, y + 6, w, h);

        // Thân nhà máy
        g2d.setColor(new Color(125, 135, 145));
        g2d.fillRect(x, y + 15, w - 25, h - 15);
        g2d.setColor(new Color(90, 100, 110));
        g2d.drawRect(x, y + 15, w - 25, h - 15);

        // Mái răng cưa
        g2d.setColor(new Color(100, 110, 120));
        int[] roofX = { x, x + 25, x + 25, x + 50, x + 50, x + 75, x + 75, x };
        int[] roofY = { y + 15, y, y + 15, y, y + 15, y, y + 15, y + 15 };
        g2d.fillPolygon(roofX, roofY, 8);

        // Cửa nhà máy
        g2d.setColor(new Color(190, 195, 200));
        g2d.fillRect(x + 15, y + 40, 35, 35);
        g2d.setColor(new Color(140, 145, 150));
        for (int lineY = y + 45; lineY < y + 75; lineY += 6) {
            g2d.drawLine(x + 15, lineY, x + 50, lineY);
        }

        // Ống khói
        int chimneyX = x + w - 20;
        g2d.setColor(new Color(150, 60, 45));
        g2d.fillRect(chimneyX, y - 10, 14, h + 10);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(chimneyX, y, 14, 6);
        g2d.fillRect(chimneyX, y + 20, 14, 6);
    }

    // ==========================================================
    // Nhà hàng sang trọng
    // ==========================================================

    /**
     * Vẽ nhà hàng với mái hiên và trang trí đặc trưng.
     *
     * @param g2d đối tượng đồ họa
     * @param x   tọa độ X góc trên-trái
     * @param y   tọa độ Y góc trên-trái
     */
    public void drawLuxuryRestaurant(Graphics2D g2d, int x, int y) {
        int w = 100, h = 80;

        // Bóng đổ
        g2d.setColor(new Color(40, 50, 40, 60));
        g2d.fillRect(x + 6, y + 6, w, h);

        // Thân nhà hàng
        g2d.setColor(new Color(250, 243, 224));
        g2d.fillRect(x, y, w, h);
        g2d.setColor(new Color(190, 170, 140));
        g2d.drawRect(x, y, w, h);

        // Mái hiên
        g2d.setColor(new Color(160, 35, 35));
        g2d.fillRect(x + 10, y + 5, w - 20, 16);
        g2d.setColor(new Color(240, 190, 60));
        g2d.fillRect(x + 25, y + 11, w - 50, 4);

        // Cửa sổ vòm
        g2d.setColor(new Color(100, 180, 220, 180));
        g2d.fillRoundRect(x + 15, y + 35, 22, 35, 10, 10);
        g2d.fillRoundRect(x + w - 37, y + 35, 22, 35, 10, 10);

        // Cửa ra vào
        g2d.setColor(new Color(120, 70, 30));
        g2d.fillRect(x + 45, y + 42, 12, 28);

        // Dải viền trang trí
        for (int i = 5; i < w - 5; i += 10) {
            g2d.setColor((i / 10) % 2 == 0 ? new Color(190, 40, 40) : Color.WHITE);
            g2d.fillRect(x + i, y + 21, 10, 10);
        }
    }
}