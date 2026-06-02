package view.renderer;

import java.awt.*;

public class RoadRenderer {

    // =========================================================
    // FOUR WAY (NGÃ TƯ)
    // =========================================================
    public void renderFourWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);
        g.fillRect(0, 300, 1200, 200);

        g.setColor(new Color(40, 40, 40));
        g.fillRect(295, 0, 5, 800);
        g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 1200, 5);

        g.setColor(Color.WHITE);
        for (int i = 0; i < 800; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(395, i, 10, 20, 4, 4);
        }
        for (int i = 0; i < 1200; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        g.fillRect(300, 270, 200, 6);
        g.fillRect(300, 524, 200, 6);
        g.fillRect(270, 300, 6, 200);
        g.fillRect(524, 300, 6, 200);

        drawHorizontalZebra(g, 300, 280);
        drawHorizontalZebra(g, 300, 490);
        drawVerticalZebra(g, 280, 300);
        drawVerticalZebra(g, 490, 300);

        g.setColor(new Color(90, 90, 90));
        g.fillOval(360, 360, 80, 80);
        g.dispose();
    }

    // =========================================================
    // THREE WAY (NGÃ BA CHỮ T HƯỚNG XUỐNG)
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(new Color(55, 55, 55));
        // Trục ngang (EAST, WEST)
        g.fillRect(0, 300, 1200, 200);
        // Trục dọc từ dưới lên (NORTH)
        g.fillRect(300, 500, 200, 300);

        // Mép đường
        g.setColor(new Color(40, 40, 40));
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 295, 5);
        g.fillRect(505, 500, 695, 5);
        g.fillRect(295, 500, 5, 300);
        g.fillRect(500, 500, 5, 300);

        // Vạch đứt
        g.setColor(Color.WHITE);
        for (int i = 0; i < 1200; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }
        for (int i = 500; i < 800; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // Vạch dừng & Zebra
        g.fillRect(300, 524, 200, 6);
        g.fillRect(270, 300, 6, 200);
        g.fillRect(524, 300, 6, 200);
        
        drawHorizontalZebra(g, 300, 490);
        drawVerticalZebra(g, 280, 300);
        drawVerticalZebra(g, 490, 300);

        g.dispose();
    }

    private void drawHorizontalZebra(Graphics2D g, int startX, int y) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < 200; i += 20) {
            g.fillRect(startX + i, y, 10, 20);
        }
    }

    private void drawVerticalZebra(Graphics2D g, int x, int startY) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < 200; i += 20) {
            g.fillRect(x, startY + i, 20, 10);
        }
    }

    // =========================================================
    // FIVE WAY 
    // =========================================================
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        final int cx = 400;
        final int cy = 400;
        final int roadW = 100;
        final int armLen = 700;
        final int islandR = 72;

        double[] angles = {
            Math.toRadians(270),   // NORTH
            Math.toRadians(342),   // NORTHEAST
            Math.toRadians( 54),   // EAST
            Math.toRadians(126),   // SOUTH
            Math.toRadians(198),   // WEST
        };

        for (double angle : angles) {
            drawFiveWayArm(g, cx, cy, angle, armLen, roadW);
        }
        drawFiveWayIsland(g, cx, cy, islandR);
        for (double angle : angles) {
            drawFiveWayArmMarkings(g, cx, cy, angle, armLen, roadW, islandR);
        }
        g.dispose();
    }

    private void drawFiveWayArm(Graphics2D g2d, int cx, int cy, double angle, int armLen, int roadW) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(cx, cy);
        g.rotate(angle + Math.PI / 2);
        g.setColor(new Color(40, 40, 40));
        g.fillRect(-roadW - 5, 0, (roadW + 5) * 2, armLen);
        g.setColor(new Color(55, 55, 55));
        g.fillRect(-roadW, 0, roadW * 2, armLen);
        g.dispose();
    }

    private void drawFiveWayArmMarkings(Graphics2D g2d, int cx, int cy, double angle, int armLen, int roadW, int islandR) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(cx, cy);
        g.rotate(angle + Math.PI / 2);

        int startY = islandR + 8;
        g.setColor(Color.WHITE);
        for (int y = startY + 40; y < armLen - 20; y += 40) {
            g.fillRoundRect(-5, y, 10, 20, 4, 4);
        }

        g.setColor(new Color(200, 200, 200, 160));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(-roadW + 4, startY + 20, -roadW + 4, armLen - 10);
        g.drawLine( roadW - 4, startY + 20,  roadW - 4, armLen - 10);

        int stopY = islandR + 46;
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(-roadW, stopY, roadW, stopY);

        int zebraY = islandR + 28;
        g.setStroke(new BasicStroke(1f));
        g.setColor(Color.WHITE);
        for (int x = -roadW + 6; x < roadW - 6; x += 18) {
            g.fillRect(x, zebraY, 9, 16);
        }
        g.dispose();
    }

    private void drawFiveWayIsland(Graphics2D g2d, int cx, int cy, int islandR) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int outerR = islandR + 28; 
        g.setColor(new Color(55, 55, 55));
        g.fillOval(cx - outerR, cy - outerR, outerR * 2, outerR * 2);

        float[] dash = {13.0f, 11.0f};
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        int laneR = outerR - 12;
        g.drawOval(cx - laneR, cy - laneR, laneR * 2, laneR * 2);
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(45, 100, 45));
        g.fillOval(cx - islandR, cy - islandR, islandR * 2, islandR * 2);
        g.setColor(new Color(70, 140, 60));
        g.fillOval(cx - islandR + 6, cy - islandR + 6, (islandR - 6) * 2, (islandR - 6) * 2);

        g.setColor(new Color(110, 180, 90, 140));
        g.fillOval(cx - islandR / 2 - 6, cy - islandR / 2 - 10, islandR, islandR / 2);

        g.setColor(new Color(230, 230, 230, 220));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(cx - islandR, cy - islandR, islandR * 2, islandR * 2);
        g.dispose();
    }
}