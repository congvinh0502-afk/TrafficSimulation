package view.renderer;

import java.awt.*;

public class NatureRenderer {

    // =========================================================
    // CÂY TRÒN TÁN LÁ RỘNG (cây cũ, giữ nguyên)
    // =========================================================
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

    // =========================================================
    // CÂY LÁ KIM / CÂY THÔNG (hình tam giác)
    // =========================================================
    public void drawPineTree(Graphics2D g2d, int x, int y) {
        // Thân cây
        g2d.setColor(new Color(100, 60, 20));
        g2d.fillRect(x + 9, y + 24, 5, 10);

        // Tầng lá dưới (rộng nhất)
        g2d.setColor(new Color(20, 100, 30));
        int[] xBot = { x, x + 26, x + 13 };
        int[] yBot = { y + 26, y + 26, y + 10 };
        g2d.fillPolygon(xBot, yBot, 3);

        // Tầng lá giữa
        g2d.setColor(new Color(30, 130, 45));
        int[] xMid = { x + 4, x + 22, x + 13 };
        int[] yMid = { y + 18, y + 18, y + 4 };
        g2d.fillPolygon(xMid, yMid, 3);

        // Tầng lá trên (nhọn đỉnh)
        g2d.setColor(new Color(50, 160, 60));
        int[] xTop = { x + 7, x + 19, x + 13 };
        int[] yTop = { y + 12, y + 12, y };
        g2d.fillPolygon(xTop, yTop, 3);
    }

    // =========================================================
    // CÂY LÙM BỤI THẤP / BỤI HOA (nhiều tán nhỏ)
    // =========================================================
    public void drawBushTree(Graphics2D g2d, int x, int y) {
        // Thân nhỏ
        g2d.setColor(new Color(110, 65, 20));
        g2d.fillRect(x + 10, y + 20, 4, 8);

        // Tán lá chính (3 hình tròn chồng nhau)
        g2d.setColor(new Color(30, 110, 50));
        g2d.fillOval(x, y + 5, 20, 18);
        g2d.fillOval(x + 8, y + 2, 20, 18);
        g2d.fillOval(x + 4, y, 18, 18);

        // Highlight sáng hơn
        g2d.setColor(new Color(60, 165, 80));
        g2d.fillOval(x + 4, y + 2, 12, 10);
        g2d.fillOval(x + 10, y, 10, 10);

        // Điểm hoa nhỏ (tùy chọn – màu hồng nhạt)
        g2d.setColor(new Color(240, 140, 160, 180));
        g2d.fillOval(x + 6, y + 4, 5, 4);
        g2d.fillOval(x + 14, y + 2, 4, 4);
    }

    // =========================================================
    // HÀNG CÂY – hỗ trợ cả 3 kiểu, xoay vòng theo vị trí
    // =========================================================
    public void drawTreeRow(Graphics2D g2d, int startX, int startY, int length, boolean isHorizontal) {
        int spacing = 55;
        int index = 0;
        if (isHorizontal) {
            for (int x = startX; x < startX + length; x += spacing) {
                if (x < 260 || x > 540) {
                    drawTreeByIndex(g2d, x, startY + 6, index);
                    index++;
                }
            }
        } else {
            for (int y = startY; y < startY + length; y += spacing) {
                if (y < 260 || y > 540) {
                    drawTreeByIndex(g2d, startX + 6, y, index);
                    index++;
                }
            }
        }
    }

    // Chọn kiểu cây xoay vòng 0-1-2
    public void drawTreeByIndex(Graphics2D g2d, int x, int y, int index) {
        switch (index % 3) {
            case 0: drawBeautifulTree(g2d, x, y); break;
            case 1: drawPineTree(g2d, x, y);      break;
            case 2: drawBushTree(g2d, x, y);      break;
        }
    }

    // =========================================================
    // CÔNG VIÊN CÓ HỒ NƯỚC
    // =========================================================
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

        // Ghế ngồi ven hồ
        g2d.setColor(new Color(140, 75, 25));
        g2d.fillRect(x + w / 4 - 15, y + h / 2, 10, 20);
        g2d.fillRect(x + 3 * w / 4 + 5, y + h / 2, 10, 20);

        // 4 cây ở 4 góc – xoay vòng kiểu
        drawBeautifulTree(g2d, x + 10, y + 10);
        drawPineTree(g2d, x + w - 35, y + 10);
        drawBushTree(g2d, x + 10, y + h - 35);
        drawBeautifulTree(g2d, x + w - 35, y + h - 35);
    }
}