package view.renderer;

import java.awt.*;

public class RoadRenderer {

    // =========================================================
    // FOUR WAY (NGÃƒ TÆ¯)
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
        // Váº CH PHÃ‚N LÃ€N NGÃƒ 4 (Váº¡ch liá»n sÃ¡t nÃºt giao)
        // ==========================================
        g.setColor(Color.WHITE);

        // --- HÆ¯á»šNG TÃ‚Y (Äi tá»« trÃ¡i sang pháº£i) ---
        g.fillRect(190, 395, 80, 10);
        for (int i = 0; i < 190; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HÆ¯á»šNG ÄÃ”NG (Äi tá»« pháº£i sang trÃ¡i) ---
        g.fillRect(530, 395, 80, 10);
        for (int i = 620; i < 1200; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HÆ¯á»šNG Báº®C (Äi tá»« trÃªn xuá»‘ng Nam) ---
        g.fillRect(395, 190, 10, 80);
        for (int i = 0; i < 190; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // --- HÆ¯á»šNG NAM (Äi tá»« dÆ°á»›i lÃªn Báº¯c) ---
        g.fillRect(395, 530, 10, 80);
        for (int i = 620; i < 800; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // Váº¡ch trÃªn (Xe tá»« Báº¯c Ä‘i xuá»‘ng Nam - LÃ n pháº£i náº±m bÃªn trÃ¡i mÃ n hÃ¬nh)
        g.fillRect(300, 270, 100, 6);
        // Váº¡ch dÆ°á»›i (Xe tá»« Nam Ä‘i lÃªn Báº¯c - LÃ n pháº£i náº±m bÃªn pháº£i mÃ n hÃ¬nh)
        g.fillRect(400, 524, 100, 6);
        // Váº¡ch trÃ¡i (Xe tá»« TÃ¢y sang ÄÃ´ng - LÃ n pháº£i náº±m bÃªn dÆ°á»›i mÃ n hÃ¬nh)
        g.fillRect(270, 400, 6, 100);
        // Váº¡ch pháº£i (Xe tá»« ÄÃ´ng sang TÃ¢y - LÃ n pháº£i náº±m bÃªn trÃªn mÃ n hÃ¬nh)
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
    // THREE WAY (NGÃƒ BA CHá»® T HÆ¯á»šNG XUá»NG)
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(new Color(55, 55, 55));
        // Trá»¥c ngang (EAST, WEST)
        g.fillRect(0, 300, 1200, 200);
        // Trá»¥c dá»c tá»« dÆ°á»›i lÃªn (NORTH)
        g.fillRect(300, 500, 200, 300);

        // MÃ©p Ä‘Æ°á»ng
        g.setColor(new Color(40, 40, 40));
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 295, 5);
        g.fillRect(505, 500, 695, 5);
        g.fillRect(295, 500, 5, 300);
        g.fillRect(500, 500, 5, 300);

        // ==========================================
        // 3. Váº CH PHÃ‚N LÃ€N (Váº¡ch liá»n sÃ¡t nÃºt giao, Ä‘á»©t nÃ©t á»Ÿ xa)
        // ==========================================
        g.setColor(Color.WHITE);

        // --- HÆ¯á»šNG TÃ‚Y (Äi tá»« trÃ¡i sang pháº£i) ---
        // Váº¡ch liá»n dÃ i 80px Ä‘Ã¢m tháº³ng vÃ o váº¡ch dá»«ng (x = 270)
        g.fillRect(190, 395, 80, 10);
        // Váº¡ch Ä‘á»©t nÃ©t á»Ÿ xa (tá»« 0 Ä‘áº¿n 190)
        for (int i = 0; i < 190; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HÆ¯á»šNG ÄÃ”NG (Äi tá»« pháº£i sang trÃ¡i) ---
        // Váº¡ch liá»n dÃ i 80px báº¯t Ä‘áº§u tá»« váº¡ch dá»«ng (x = 530) kÃ©o ra xa
        g.fillRect(530, 395, 80, 10);
        // Váº¡ch Ä‘á»©t nÃ©t á»Ÿ xa (tá»« 620 Ä‘áº¿n 1200)
        for (int i = 620; i < 1200; i += 40) {
            g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // --- HÆ¯á»šNG NAM (Äi tá»« dÆ°á»›i lÃªn Báº¯c) ---
        // Váº¡ch liá»n dÃ i 80px báº¯t Ä‘áº§u tá»« váº¡ch dá»«ng (y = 530) kÃ©o xuá»‘ng
        g.fillRect(395, 530, 10, 80);
        // Váº¡ch Ä‘á»©t nÃ©t á»Ÿ xa (tá»« 620 Ä‘áº¿n 800)
        for (int i = 620; i < 800; i += 40) {
            g.fillRoundRect(395, i, 10, 20, 4, 4);
        }

        // ==========================================
        // 1. Váº CH NGÆ¯á»œI ÄI Bá»˜ (SÃ¡t mÃ©p ngÃ£ tÆ°)
        // ==========================================
        // HÆ°á»›ng Nam (DÆ°á»›i)
        drawHorizontalZebra(g, 300, 490);
        

        // ==========================================
        // 2. Váº CH Dá»ªNG XE (LÃ¹i láº¡i phÃ­a sau, ná»­a lÃ n pháº£i)
        // ==========================================
        g.setColor(Color.WHITE);
        // Váº¡ch dÆ°á»›i (HÆ°á»›ng Nam) - Náº±m sau váº¡ch Ä‘i bá»™
        g.fillRect(400, 524, 100, 6); 
        // Váº¡ch trÃ¡i (HÆ°á»›ng TÃ¢y) - Náº±m sau váº¡ch Ä‘i bá»™
        g.fillRect(270, 400, 6, 100); 
        // Váº¡ch pháº£i (HÆ°á»›ng ÄÃ´ng) - Náº±m sau váº¡ch Ä‘i bá»™
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
        
        final int cx = 500;
        final int cy = 400;
        final int roadW = 130;
        final int armLen = 1200; // Thay 700 thÃ nh 1200 Ä‘á»ƒ Ä‘Æ°á»ng Ä‘Ã¢m xuyÃªn qua map
        final int islandR = 80; // THAY DOI

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

        // Äáº·t tá»a Ä‘á»™ váº¡ch dá»«ng ngang hoÃ n toÃ n vá»›i chÃ³p cá» (68)
        int stopY = islandR + 99; // FIX: 68→99, vach cham dung goc co (dist=179)

        // 1. Váº CH Dá»ªNG XE NGANG
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        // Váº½ váº¡ch ngang ná»‘i tá»« tim Ä‘Æ°á»ng (0) tháº³ng sang mÃ©p pháº£i (roadW)
        g.drawLine(0, stopY, roadW, stopY);

        // 2. Váº CH PHÃ‚N LÃ€N (Liá»n sÃ¡t ngÃ£ tÆ°, Ä‘á»©t á»Ÿ xa)
        g.setColor(Color.WHITE);
        
        // Váº¡ch liá»n cáº¥m láº¥n lÃ n Ä‘Ã¢m tháº³ng vÃ o váº¡ch dá»«ng
        g.fillRect(-5, stopY, 10, 80); 
        
        // Váº¡ch Ä‘á»©t nÃ©t tá»± Ä‘á»™ng cháº¡y dÃ i theo chiá»u dÃ i Ä‘Æ°á»ng
        for (int y = stopY + 90; y < armLen - 20; y += 40) {
            g.fillRoundRect(-5, y, 10, 20, 4, 4);
        }

        g.dispose();
    } // Káº¿t thÃºc hÃ m drawFiveWayArmMarkings

    private void drawFiveWayIsland(Graphics2D g2d, int cx, int cy, int islandR) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int outerR = islandR + 35; // THAY DOI: +28 → +35, vong xoay rong hon
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


