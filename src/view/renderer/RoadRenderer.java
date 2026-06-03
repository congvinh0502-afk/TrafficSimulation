package view.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Renderer đường giao thông — vẽ mặt đường, vạch kẻ và bùng binh.
 *
 * <p>
 * Ba phương thức công khai tương ứng ba loại ngã rẽ:
 * <ul>
 * <li>{@link #renderThreeWay} — ngã ba</li>
 * <li>{@link #renderFourWay} — ngã tư</li>
 * <li>{@link #renderFiveWay} — ngã năm với bùng binh và đường chéo</li>
 * </ul>
 * </p>
 */
public class RoadRenderer {

    private static final Color ROAD_ASPHALT = new Color(55, 55, 55);
    private static final Color ROAD_EDGE = new Color(40, 40, 40);
    private static final Color ROAD_MARKING = Color.WHITE;
    private static final Color ISLAND_GRAY = new Color(90, 90, 90);
    private static final Color ISLAND_LIGHT = new Color(160, 160, 160);
    private static final Color ISLAND_GREEN = new Color(35, 130, 55);
    private static final Color ROUNDABOUT_EDGE = new Color(45, 45, 45);

    // ==========================================================
    // Ngã ba
    // ==========================================================

    /**
     * Vẽ ngã ba: trục dọc + nhánh trái (NORTH, EAST, WEST).
     *
     * @param g2d đối tượng đồ họa
     */
    public void renderThreeWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setColor(ROAD_ASPHALT);
        g.fillRect(300, 0, 200, 800); // trục dọc
        g.fillRect(0, 300, 500, 200); // nhánh trái
        g.dispose();
    }

    // ==========================================================
    // Ngã tư
    // ==========================================================

    /**
     * Vẽ ngã tư đầy đủ: hai trục vuông góc, vạch kẻ, vạch dừng,
     * vạch đi bộ và đảo giao thông nhỏ ở giữa.
     *
     * @param g2d đối tượng đồ họa
     */
    public void renderFourWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Mặt đường nhựa
        g.setColor(ROAD_ASPHALT);
        g.fillRect(300, 0, 200, 800); // trục dọc
        g.fillRect(0, 300, 1200, 200); // trục ngang

        // Mép vỉa hè
        g.setColor(ROAD_EDGE);
        g.fillRect(295, 0, 5, 800);
        g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 1200, 5);

        // Vạch đứt chia làn
        g.setColor(ROAD_MARKING);
        for (int i = 0; i < 800; i += 40) {
            if (i < 280 || i > 500)
                g.fillRoundRect(395, i, 10, 20, 4, 4);
        }
        for (int i = 0; i < 1200; i += 40) {
            if (i < 280 || i > 500)
                g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // Vạch dừng đèn đỏ
        g.fillRect(300, 270, 200, 6);
        g.fillRect(300, 524, 200, 6);
        g.fillRect(270, 300, 6, 200);
        g.fillRect(524, 300, 6, 200);

        // Vạch đi bộ
        drawHorizontalZebra(g, 300, 280);
        drawHorizontalZebra(g, 300, 490);
        drawVerticalZebra(g, 280, 300);
        drawVerticalZebra(g, 490, 300);

        // Đảo giao thông nhỏ
        g.setColor(ISLAND_GRAY);
        g.fillOval(360, 360, 80, 80);

        g.dispose();
    }

    // ==========================================================
    // Ngã năm (bùng binh)
    // ==========================================================

    /**
     * Vẽ ngã năm với bùng binh lớn và đường chéo Đông-Bắc.
     *
     * @param g2d đối tượng đồ họa
     */
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = 400;
        int centerY = 400;
        int roadWidth = 200;
        int roundaboutDia = 340;
        int islandDia = 160;

        // Bốn trục vuông góc
        g.setColor(ROAD_ASPHALT);
        g.fillRect(300, 0, 200, 800);
        g.fillRect(0, 300, 1200, 200);

        // Mép vỉa hè
        g.setColor(ROAD_EDGE);
        g.fillRect(295, 0, 5, 800);
        g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 1200, 5);

        // Đường chéo Đông-Bắc
        drawDiagonalRoad(g, centerX, centerY, roadWidth);

        // Bùng binh phủ lên trên (che điểm giao lỗi)
        drawRoundabout(g, centerX, centerY, roundaboutDia, islandDia);

        // Vạch đi bộ ngoài bùng binh
        drawHorizontalZebra(g, 300, 210);
        drawHorizontalZebra(g, 300, 570);
        drawVerticalZebra(g, 210, 300);
        drawVerticalZebra(g, 570, 300);

        g.dispose();
    }

    // ==========================================================
    // Vẽ các thành phần con
    // ==========================================================

    /** Vạch đi bộ ngang (trên trục dọc). */
    private void drawHorizontalZebra(Graphics2D g, int startX, int y) {
        g.setColor(ROAD_MARKING);
        for (int i = 0; i < 200; i += 20) {
            g.fillRect(startX + i, y, 10, 20);
        }
    }

    /** Vạch đi bộ dọc (trên trục ngang). */
    private void drawVerticalZebra(Graphics2D g, int x, int startY) {
        g.setColor(ROAD_MARKING);
        for (int i = 0; i < 200; i += 20) {
            g.fillRect(x, startY + i, 20, 10);
        }
    }

    /**
     * Vẽ nhánh đường chéo Đông-Bắc (xoay -45 độ từ tâm).
     */
    public void drawDiagonalRoad(Graphics2D g2d, int centerX, int centerY, int roadWidth) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(centerX, centerY);
        g.rotate(Math.toRadians(-45));

        // Mép đường
        g.setColor(ROAD_EDGE);
        g.fillRect(-(roadWidth / 2 + 5), -900, roadWidth + 10, 900);

        // Mặt đường
        g.setColor(ROAD_ASPHALT);
        g.fillRect(-roadWidth / 2, -900, roadWidth, 900);

        // Vạch đứt phân làn
        g.setColor(ROAD_MARKING);
        for (int i = -900; i < -180; i += 40) {
            g.fillRoundRect(-5, i, 10, 20, 4, 4);
        }

        // Vạch đi bộ chéo
        for (int i = -roadWidth / 2; i < roadWidth / 2; i += 20) {
            g.fillRect(i, -230, 10, 20);
        }

        g.dispose();
    }

    /**
     * Vẽ bùng binh tròn với đảo xanh ở giữa và vạch phân làn.
     *
     * @param g2d       đối tượng đồ họa
     * @param centerX   tâm X
     * @param centerY   tâm Y
     * @param outerDia  đường kính ngoài (phần nhựa)
     * @param islandDia đường kính đảo trung tâm
     */
    public void drawRoundabout(Graphics2D g2d, int centerX, int centerY,
            int outerDia, int islandDia) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Mặt đường bùng binh
        g.setColor(ROAD_ASPHALT);
        g.fillOval(centerX - outerDia / 2, centerY - outerDia / 2, outerDia, outerDia);

        // Viền bùng binh
        g.setColor(ROUNDABOUT_EDGE);
        g.setStroke(new BasicStroke(3));
        g.drawOval(centerX - outerDia / 2, centerY - outerDia / 2, outerDia, outerDia);

        // Vạch vòng tròn phân làn
        g.setColor(ROAD_MARKING);
        float[] dash = { 14.0f, 12.0f };
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        int laneR = outerDia - 60;
        g.drawOval(centerX - laneR / 2, centerY - laneR / 2, laneR, laneR);

        // Đảo đá lề
        g.setStroke(new BasicStroke(1));
        g.setColor(ISLAND_LIGHT);
        g.fillOval(centerX - islandDia / 2, centerY - islandDia / 2, islandDia, islandDia);

        // Lõi cỏ
        g.setColor(ISLAND_GREEN);
        int green = islandDia - 14;
        g.fillOval(centerX - green / 2, centerY - green / 2, green, green);

        g.dispose();
    }
}