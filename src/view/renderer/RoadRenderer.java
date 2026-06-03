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

        // ==========================================
        // VẠCH PHÂN LÀN NGÃ 4 (Vạch liền sát nút giao)
        // ==========================================
        g.setColor(Color.WHITE);

        // --- HƯỚNG TÂY (Đi từ trái sang phải) ---
        g.fillRect(190, 395, 80, 10);
        for (int i = 0; i < 190; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HƯỚNG ĐÔNG (Đi từ phải sang trái) ---
        g.fillRect(530, 395, 80, 10);
        for (int i = 620; i < 1200; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HƯỚNG BẮC (Đi từ trên xuống Nam) ---
        g.fillRect(395, 190, 10, 80);
        for (int i = 0; i < 190; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // --- HƯỚNG NAM (Đi từ dưới lên Bắc) ---
        g.fillRect(395, 530, 10, 80);
        for (int i = 620; i < 800; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // Vạch trên (Xe từ Bắc đi xuống Nam - Làn phải nằm bên trái màn hình)
        g.fillRect(300, 270, 100, 6);
        // Vạch dưới (Xe từ Nam đi lên Bắc - Làn phải nằm bên phải màn hình)
        g.fillRect(400, 524, 100, 6);
        // Vạch trái (Xe từ Tây sang Đông - Làn phải nằm bên dưới màn hình)
        g.fillRect(270, 400, 6, 100);
        // Vạch phải (Xe từ Đông sang Tây - Làn phải nằm bên trên màn hình)
        g.fillRect(524, 300, 6, 100);

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

        // ==========================================
        // 3. VẠCH PHÂN LÀN (Vạch liền sát nút giao, đứt nét ở xa)
        // ==========================================
        g.setColor(Color.WHITE);

        // --- HƯỚNG TÂY (Đi từ trái sang phải) ---
        // Vạch liền dài 80px đâm thẳng vào vạch dừng (x = 270)
        g.fillRect(190, 395, 80, 10);
        // Vạch đứt nét ở xa (từ 0 đến 190)
        for (int i = 0; i < 190; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HƯỚNG ĐÔNG (Đi từ phải sang trái) ---
        // Vạch liền dài 80px bắt đầu từ vạch dừng (x = 530) kéo ra xa
        g.fillRect(530, 395, 80, 10);
        // Vạch đứt nét ở xa (từ 620 đến 1200)
        for (int i = 620; i < 1200; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HƯỚNG NAM (Đi từ dưới lên Bắc) ---
        // Vạch liền dài 80px bắt đầu từ vạch dừng (y = 530) kéo xuống
        g.fillRect(395, 530, 10, 80);
        // Vạch đứt nét ở xa (từ 620 đến 800)
        for (int i = 620; i < 800; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // ==========================================
        // 1. VẠCH NGƯỜI ĐI BỘ (Sát mép ngã tư)
        // ==========================================
        // Hướng Nam (Dưới)
        drawHorizontalZebra(g, 300, 490);
        

        // ==========================================
        // 2. VẠCH DỪNG XE (Lùi lại phía sau, nửa làn phải)
        // ==========================================
        g.setColor(Color.WHITE);
        // Vạch dưới (Hướng Nam) - Nằm sau vạch đi bộ
        g.fillRect(400, 524, 100, 6); 
        // Vạch trái (Hướng Tây) - Nằm sau vạch đi bộ
        g.fillRect(270, 400, 6, 100); 
        // Vạch phải (Hướng Đông) - Nằm sau vạch đi bộ
        g.fillRect(524, 300, 6, 100);
        
        

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
        final int armLen = 1200; // Thay 700 thành 1200 để đường đâm xuyên qua map
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

        // Tọa độ vạch dừng (Giữa đường là 45, mép cỏ là 68)
        int stopY = islandR + 45; 
        int cornerY = islandR + 68; 

        // 1. VẠCH DỪNG XE XIÊN (Nối từ giữa tim đường lên chóp bãi cỏ)
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(0, stopY, roadW, cornerY);

        // 2. VẠCH PHÂN LÀN (Liền sát ngã tư, đứt ở xa)
        g.setColor(Color.WHITE);
        
        // Vạch liền cấm lấn làn dài 80px đâm thẳng vào vạch dừng
        g.fillRect(-5, stopY, 10, 80); 
        
        // Vạch đứt nét tự động chạy dài theo chiều dài đường (armLen = 1200)
        for (int y = stopY + 90; y < armLen - 20; y += 40) {
            g.fillRoundRect(-5, y, 10, 20, 4, 4);
        }

        g.dispose();
    } // Kết thúc hàm drawFiveWayArmMarkings

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