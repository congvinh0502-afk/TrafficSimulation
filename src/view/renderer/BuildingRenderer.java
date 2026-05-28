package view.renderer;

import java.awt.*;

public class BuildingRenderer {

    public void drawSkyscraper(Graphics2D g, int x, int y) {
        int w = 110; int h = 150;
        g.setColor(new Color(40, 50, 40, 50));
        g.fillRect(x + 10, y + 10, w, h);
        g.setColor(new Color(45, 60, 80));
        g.fillRect(x, y, w, h);
        g.setColor(new Color(30, 40, 55));
        g.drawRect(x, y, w, h);
        g.setColor(new Color(75, 140, 210));
        g.fillRect(x + 15, y, w - 30, h);

        int winW = 8; int winH = 12;
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 5; c++) {
                int winX = x + 20 + c * (winW + 8);
                int winY = y + 15 + r * (winH + 7);
                if ((r + c) % 3 == 0 || (r == 2 && c == 4)) {
                    g.setColor(new Color(255, 235, 130));
                } else {
                    g.setColor(new Color(110, 180, 240));
                }
                g.fillRect(winX, winY, winW, winH);
            }
        }
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + w / 2 - 2, y - 20, 4, 20);
        g.setColor(Color.RED);
        g.fillOval(x + w / 2 - 4, y - 24, 8, 8);
    }

    public void drawLuxuryRestaurant(Graphics2D g, int x, int y) {
        int w = 100; int h = 80;
        g.setColor(new Color(40, 50, 40, 60));
        g.fillRect(x + 6, y + 6, w, h);
        g.setColor(new Color(250, 243, 224));
        g.fillRect(x, y, w, h);
        g.setColor(new Color(190, 170, 140));
        g.drawRect(x, y, w, h);
        g.setColor(new Color(160, 35, 35));
        g.fillRect(x + 10, y + 5, w - 20, 16);
        g.setColor(new Color(240, 190, 60));
        g.fillRect(x + 25, y + 11, w - 50, 4);
        g.setColor(new Color(100, 180, 220, 180));
        g.fillRoundRect(x + 15, y + 35, 22, 35, 10, 10);
        g.fillRoundRect(x + w - 37, y + 35, 22, 35, 10, 10);
        g.setColor(new Color(120, 70, 30));
        g.fillRect(x + 45, y + 42, 12, 28);

        for (int i = 5; i < w - 5; i += 10) {
            if ((i / 10) % 2 == 0) g.setColor(new Color(190, 40, 40));
            else g.setColor(Color.WHITE);
            g.fillRect(x + i, y + 21, 10, 10);
        }
    }

    public void drawModernFactory(Graphics2D g, int x, int y) {
        int w = 120; int h = 75;
        g.setColor(new Color(40, 50, 40, 50));
        g.fillRect(x + 6, y + 6, w, h);
        g.setColor(new Color(125, 135, 145));
        g.fillRect(x, y + 15, w - 25, h - 15);
        g.setColor(new Color(90, 100, 110));
        g.drawRect(x, y + 15, w - 25, h - 15);

        g.setColor(new Color(100, 110, 120));
        int[] roofX = { x, x + 25, x + 25, x + 50, x + 50, x + 75, x + 75, x };
        int[] roofY = { y + 15, y, y + 15, y, y + 15, y, y + 15, y + 15 };
        g.fillPolygon(roofX, roofY, 8);

        g.setColor(new Color(190, 195, 200));
        g.fillRect(x + 15, y + 40, 35, 35);
        g.setColor(new Color(140, 145, 150));
        for (int lineY = y + 45; lineY < y + 75; lineY += 6) {
            g.drawLine(x + 15, lineY, x + 50, lineY);
        }
        int chimneyX = x + w - 20;
        g.setColor(new Color(150, 60, 45));
        g.fillRect(chimneyX, y - 10, 14, h + 10);
        g.setColor(Color.WHITE);
        g.fillRect(chimneyX, y, 14, 6);
        g.fillRect(chimneyX, y + 20, 14, 6);
    }

    public void drawLuxuryHouse(Graphics2D g2d, int x, int y) {
        int baseW = 85; int baseH = 75;
        g2d.setColor(new Color(40, 65, 40, 60));
        g2d.fillRect(x + 6, y + 6, baseW, baseH);
        g2d.setColor(new Color(245, 240, 230)); 
        g2d.fillRect(x, y, baseW, baseH);
        g2d.setColor(new Color(180, 170, 150));
        g2d.drawRect(x, y, baseW, baseH);
        int layer1X = x + 12; int layer1Y = y + 10;
        int layer1W = baseW - 24; int layer1H = baseH - 20;
        g2d.setColor(new Color(210, 105, 30)); 
        g2d.fillRect(layer1X, layer1Y, layer1W, layer1H);
        int roofX = layer1X + 6; int roofY = layer1Y + 6;
        int roofW = layer1W - 12; int roofH = layer1H - 12;
        g2d.setColor(new Color(45, 85, 150)); 
        g2d.fillRect(roofX, roofY, roofW, roofH);
        g2d.setColor(new Color(25, 50, 100));
        int cx = roofX + roofW / 2; int cy = roofY + roofH / 2;
        g2d.drawLine(roofX, roofY, cx, cy);
        g2d.drawLine(roofX + roofW, roofY, cx, cy);
        g2d.drawLine(roofX, roofY + roofH, cx, cy);
        g2d.drawLine(roofX + roofW, roofY + roofH, cx, cy);
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRect(cx - 8, cy - 6, 16, 12);
        g2d.setColor(new Color(25, 50, 100));
        g2d.drawRect(cx - 8, cy - 6, 16, 12);
        g2d.setColor(new Color(135, 206, 250, 180));
        g2d.fillRect(cx - 4, cy - 3, 8, 6);
    }
}