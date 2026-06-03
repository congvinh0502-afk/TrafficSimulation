package view.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Renderer thiên nhiên — vẽ cây và công viên.
 *
 * <p>
 * Ba kiểu cây xoay vòng theo index:
 * <ul>
 * <li>0: {@link #drawBeautifulTree} — cây tán tròn</li>
 * <li>1: {@link #drawPineTree} — cây thông hình tháp</li>
 * <li>2: {@link #drawBushTree} — cây bụi thấp có hoa</li>
 * </ul>
 * </p>
 */
public class NatureRenderer {

    // Ngưỡng lọc — không vẽ cây chồng lên đường
    private static final int ROAD_MIN = 260;
    private static final int ROAD_MAX = 540;

    // ==========================================================
    // Ba kiểu cây đơn
    // ==========================================================

    /**
     * Cây tán tròn màu xanh đậm, thích hợp vỉa hè và công viên.
     */
    public void drawBeautifulTree(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(90, 50, 15));
        g2d.fillRect(x + 10, y + 22, 6, 12);
        g2d.setColor(new Color(15, 95, 15));
        g2d.fillOval(x, y, 26, 26);
        g2d.setColor(new Color(35, 145, 35));
        g2d.fillOval(x + 2, y + 1, 22, 22);
        g2d.setColor(new Color(75, 195, 75));
        g2d.fillOval(x + 5, y - 1, 14, 14);
    }

    /**
     * Cây thông hình tháp 3 tầng, nhọn đỉnh.
     */
    public void drawPineTree(Graphics2D g2d, int x, int y) {
        // Thân
        g2d.setColor(new Color(100, 60, 20));
        g2d.fillRect(x + 9, y + 24, 5, 10);

        // Tầng lá dưới
        g2d.setColor(new Color(20, 100, 30));
        g2d.fillPolygon(new int[] { x, x + 26, x + 13 }, new int[] { y + 26, y + 26, y + 10 }, 3);

        // Tầng lá giữa
        g2d.setColor(new Color(30, 130, 45));
        g2d.fillPolygon(new int[] { x + 4, x + 22, x + 13 }, new int[] { y + 18, y + 18, y + 4 }, 3);

        // Tầng lá trên
        g2d.setColor(new Color(50, 160, 60));
        g2d.fillPolygon(new int[] { x + 7, x + 19, x + 13 }, new int[] { y + 12, y + 12, y }, 3);
    }

    /**
     * Cây bụi thấp nhiều tán nhỏ, có điểm hoa hồng nhạt.
     */
    public void drawBushTree(Graphics2D g2d, int x, int y) {
        // Thân
        g2d.setColor(new Color(110, 65, 20));
        g2d.fillRect(x + 10, y + 20, 4, 8);

        // Tán lá
        g2d.setColor(new Color(30, 110, 50));
        g2d.fillOval(x, y + 5, 20, 18);
        g2d.fillOval(x + 8, y + 2, 20, 18);
        g2d.fillOval(x + 4, y, 18, 18);

        // Highlight
        g2d.setColor(new Color(60, 165, 80));
        g2d.fillOval(x + 4, y + 2, 12, 10);
        g2d.fillOval(x + 10, y, 10, 10);

        // Hoa nhỏ
        g2d.setColor(new Color(240, 140, 160, 180));
        g2d.fillOval(x + 6, y + 4, 5, 4);
        g2d.fillOval(x + 14, y + 2, 4, 4);
    }

    // ==========================================================
    // Hàng cây
    // ==========================================================

    /**
     * Vẽ hàng cây dọc hoặc ngang, tự động bỏ qua vị trí chồng lên đường.
     *
     * @param g2d          đối tượng đồ họa
     * @param startX       X bắt đầu
     * @param startY       Y bắt đầu
     * @param length       chiều dài hàng cây (pixel)
     * @param isHorizontal {@code true} = hàng ngang, {@code false} = hàng dọc
     */
    public void drawTreeRow(Graphics2D g2d, int startX, int startY, int length, boolean isHorizontal) {
        int spacing = 55;
        int index = 0;

        if (isHorizontal) {
            for (int x = startX; x < startX + length; x += spacing) {
                if (x < ROAD_MIN || x > ROAD_MAX) {
                    drawTreeByIndex(g2d, x, startY + 6, index++);
                }
            }
        } else {
            for (int y = startY; y < startY + length; y += spacing) {
                if (y < ROAD_MIN || y > ROAD_MAX) {
                    drawTreeByIndex(g2d, startX + 6, y, index++);
                }
            }
        }
    }

    /**
     * Chọn kiểu cây theo index xoay vòng 0 → 1 → 2 → 0 → ...
     */
    public void drawTreeByIndex(Graphics2D g2d, int x, int y, int index) {
        switch (index % 3) {
            case 0:
                drawBeautifulTree(g2d, x, y);
                break;
            case 1:
                drawPineTree(g2d, x, y);
                break;
            case 2:
                drawBushTree(g2d, x, y);
                break;
        }
    }

    // ==========================================================
    // Công viên có hồ nước
    // ==========================================================

    /**
     * Vẽ công viên với hồ nước ở giữa, ghế đá và 4 cây góc.
     *
     * @param g2d đối tượng đồ họa
     * @param x   X góc trên-trái
     * @param y   Y góc trên-trái
     * @param w   chiều rộng công viên
     * @param h   chiều cao công viên
     */
    public void drawParkWithPond(Graphics2D g2d, int x, int y, int w, int h) {
        // Nền cỏ công viên
        g2d.setColor(new Color(50, 160, 70));
        g2d.fillRect(x, y, w, h);

        // Hồ nước
        g2d.setColor(new Color(55, 120, 190));
        g2d.fillOval(x + w / 4, y + h / 4, w / 2, h / 2);
        g2d.setColor(new Color(120, 110, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(x + w / 4, y + h / 4, w / 2, h / 2);
        g2d.setStroke(new BasicStroke(1));

        // Ghế đá ven hồ
        g2d.setColor(new Color(140, 75, 25));
        g2d.fillRect(x + w / 4 - 15, y + h / 2, 10, 20);
        g2d.fillRect(x + 3 * w / 4 + 5, y + h / 2, 10, 20);

        // 4 cây ở 4 góc
        drawBeautifulTree(g2d, x + 10, y + 10);
        drawPineTree(g2d, x + w - 35, y + 10);
        drawBushTree(g2d, x + 10, y + h - 35);
        drawBeautifulTree(g2d, x + w - 35, y + h - 35);
    }
}