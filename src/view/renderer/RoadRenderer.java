package view.renderer;

import java.awt.*;

public class RoadRenderer {

    // =========================================================
    // FOUR WAY (NGÃƒ TÆ¯)
    // =========================================================
    public void renderFourWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // LÃ²ng Ä‘Æ°á»ng nhá»±a vuÃ´ng gÃ³c (Rá»™ng 200px)
        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);     // Trá»¥c dá»c
        g.fillRect(0, 300, 1200, 200);    // Trá»¥c ngang

        // MÃ©p vá»‰a hÃ¨ xÃ¡m Ä‘áº­m cho ngÃ£ tÆ° thÆ°á»ng
        g.setColor(new Color(40, 40, 40));
        g.fillRect(295, 0, 5, 800);
        g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 1200, 5);

        // Váº¡ch Ä‘á»©t chia lÃ n
        g.setColor(Color.WHITE);
        for (int i = 0; i < 800; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(395, i, 10, 20, 4, 4);
        }
        for (int i = 0; i < 1200; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // Váº¡ch dá»«ng Ä‘Ã¨n Ä‘á»
        g.fillRect(300, 270, 200, 6);
        g.fillRect(300, 524, 200, 6);
        g.fillRect(270, 300, 6, 200);
        g.fillRect(524, 300, 6, 200);

        // Váº¡ch Ä‘i bá»™
        drawHorizontalZebra(g, 300, 280);
        drawHorizontalZebra(g, 300, 490);
        drawVerticalZebra(g, 280, 300);
        drawVerticalZebra(g, 490, 300);

        // Äáº£o giao thÃ´ng nhá» ngÃ£ tÆ° thÆ°á»ng
        g.setColor(new Color(90, 90, 90));
        g.fillOval(360, 360, 80, 80);

        g.dispose();
    }

    // =========================================================
    // THREE WAY (NGÃƒ BA)
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);
        g.fillRect(0, 300, 500, 200);
        g.dispose();
    }

    // =========================================================
    // FIVE WAY (NGÃƒ NÄ‚M VÃ’NG XUYáº¾N Lá»šN - CHUáº¨N Tá»ŒA Äá»˜)
    // =========================================================
    /*renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = 400; // TÃ¢m hÃ¬nh há»c chÃ­nh xÃ¡c cá»§a há»‡ thá»‘ng 1200x800
        int centerY = 400;
        int roadWidth = 200;       // Äá»™ rá»™ng Ä‘Æ°á»ng Ä‘á»“ng bá»™ hoÃ n toÃ n
        int roundaboutDia = 340;   // ÄÆ°á»ng kÃ­nh tháº£m nhá»±a bÃ¹ng binh lá»›n káº¿t ná»‘i 5 ngáº£
        int islandDia = 160;       // ÄÆ°á»ng kÃ­nh Ä‘áº£o cá» trung tÃ¢m

        // 1. Tráº£i nhá»±a ná»n Ä‘Æ°á»ng 4 trá»¥c vuÃ´ng gÃ³c
        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);
        g.fillRect(0, 300, 1200, 200);

        // 2. Váº½ mÃ©p Ä‘Æ°á»ng (Vá»‰a hÃ¨ gÃ³c) cho cÃ¡c trá»¥c tháº³ng bÃªn ngoÃ i vÃ¹ng bÃ¹ng binh
        g.setColor(new Color(40, 40, 40));
        g.fillRect(295, 0, 5, 800);  g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5); g.fillRect(0, 500, 1200, 5);

        // 3. Váº½ nhÃ¡nh Ä‘Æ°á»ng chÃ©o thá»© 5 (ÄÃ´ng Báº¯c) lao tháº³ng vÃ o tÃ¢m 400, 400
        drawDiagonalRoad(g, centerX, centerY, roadWidth);

        // 4. Váº½ Ä‘Ã¨ khá»‘i bÃ¹ng binh lá»›n lÃªn chÃ­nh giá»¯a Ä‘á»ƒ nuá»‘t trá»n cÃ¡c Ä‘iá»ƒm giao lá»—i cáº¥u trÃºc
        drawRoundabout(g, centerX, centerY, roundaboutDia, islandDia);

        // 5. Váº½ váº¡ch káº» Ä‘i bá»™ lÃ¹i ra ngoÃ i rÃ¬a bÃ¹ng binh má»™t chÃºt Ä‘á»ƒ táº¡o khÃ´ng gian thoÃ¡ng
        drawHorizontalZebra(g, 300, 210); // NhÃ¡nh Báº¯c (LÃ¹i lÃªn trÃªn)
        drawHorizontalZebra(g, 300, 570); // NhÃ¡nh Nam (LÃ¹i xuá»‘ng dÆ°á»›i)
        drawVerticalZebra(g, 210, 300);   // NhÃ¡nh TÃ¢y (LÃ¹i sang trÃ¡i)
        drawVerticalZebra(g, 570, 300);   // NhÃ¡nh ÄÃ´ng (LÃ¹i sang pháº£i)

        g.dispose();
    }*/
    

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

    // Váº½ nhÃ¡nh Ä‘Æ°á»ng chÃ©o ÄÃ´ng Báº¯c (Xoay ngÆ°á»£c chiá»u kim Ä‘á»“ng há»“ 45 Ä‘á»™)
    /*public void drawDiagonalRoad(Graphics2D g2d, int centerX, int centerY, int roadWidth) {
        Graphics2D gChieo = (Graphics2D) g2d.create();
        gChieo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        gChieo.translate(centerX, centerY);
        gChieo.rotate(Math.toRadians(-45)); // Quay hÆ°á»›ng lÃªn gÃ³c trÃªn bÃªn pháº£i mÃ n hÃ¬nh
        
        // MÃ©p Ä‘Æ°á»ng chÃ©o
        gChieo.setColor(new Color(40, 40, 40));
        gChieo.fillRect(-(roadWidth / 2 + 5), -900, roadWidth + 10, 900);
        
        // LÃ²ng Ä‘Æ°á»ng chÃ©o nhá»±a xÃ¡m
        gChieo.setColor(new Color(55, 55, 55));
        gChieo.fillRect(-roadWidth / 2, -900, roadWidth, 900); 

        // Váº¡ch Ä‘á»©t phÃ¢n lÃ n cho nhÃ¡nh Ä‘Æ°á»ng chÃ©o
        gChieo.setColor(Color.WHITE);
        for (int i = -900; i < -180; i += 40) {
            gChieo.fillRoundRect(-5, i, 10, 20, 4, 4);
        }
        
        // Váº¡ch Ä‘i bá»™ chÃ©o dÃ nh riÃªng cho nhÃ¡nh thá»© 5
        gChieo.setColor(Color.WHITE);
        for (int i = -roadWidth/2; i < roadWidth/2; i += 20) {
            gChieo.fillRect(i, -230, 10, 20);
        }
        
        gChieo.dispose();
    }*/

    // Váº½ khá»‘i bÃ¹ng binh trÃ²n
    /*public void drawRoundabout(Graphics2D g2d, int centerX, int centerY, int outerDia, int islandDia) {
        Graphics2D gRound = (Graphics2D) g2d.create();
        gRound.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Tháº£m nhá»±a bÃ¹ng binh
        gRound.setColor(new Color(55, 55, 55));
        gRound.fillOval(centerX - outerDia / 2, centerY - outerDia / 2, outerDia, outerDia);

        // MÃ©p viá»n Ä‘Ã¡ bao quanh bÃ¹ng binh nhá»±a Ä‘á»ƒ phÃ¢n biá»‡t vá»›i cÃ¡c ngáº£ Ä‘Æ°á»ng nháº­p lÃ n
        gRound.setColor(new Color(45, 45, 45));
        gRound.setStroke(new BasicStroke(3));
        gRound.drawOval(centerX - outerDia / 2, centerY - outerDia / 2, outerDia, outerDia);

        // 2. Váº¡ch Ä‘á»©t xoay vÃ²ng trÃ²n hÆ°á»›ng dáº«n xe cháº¡y trong vÃ²ng xuyáº¿n
        gRound.setColor(Color.WHITE);
        float[] dash = {14.0f, 12.0f};
        gRound.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        int laneRadius = outerDia - 60;
        gRound.drawOval(centerX - laneRadius / 2, centerY - laneRadius / 2, laneRadius, laneRadius);

        // 3. Äáº£o Ä‘Ã¡ xá»‰ vá»‰a hÃ¨ bÃ¹ng binh
        gRound.setStroke(new BasicStroke(1));
        gRound.setColor(new Color(160, 160, 160));
        gRound.fillOval(centerX - islandDia / 2, centerY - islandDia / 2, islandDia, islandDia);

        // 4. LÃµi cá» xanh trang trÃ­ tiá»ƒu cáº£nh bÃ¹ng binh trung tÃ¢m
        gRound.setColor(new Color(35, 130, 55)); 
        gRound.fillOval(centerX - (islandDia - 14) / 2, centerY - (islandDia - 14) / 2, islandDia - 14, islandDia - 14);
        
        gRound.dispose();
    }*/
    // ====================================================================
// PATCH: DÃ¡n 4 method nÃ y vÃ o class RoadRenderer.java
//
// BÆ¯á»šC 1: XÃ³a method renderFiveWay() cÅ© trong RoadRenderer.java
// BÆ¯á»šC 2: XÃ³a drawDiagonalRoad() vÃ  drawRoundabout() náº¿u chá»‰ dÃ¹ng bá»Ÿi renderFiveWay
// BÆ¯á»šC 3: DÃ¡n 4 method bÃªn dÆ°á»›i vÃ o trong class RoadRenderer (cÃ¹ng cáº¥p vá»›i renderFourWay)
// BÆ¯á»šC 4: Äáº£m báº£o import java.awt.* Ä‘Ã£ cÃ³ trong file (Ä‘Ã£ cÃ³ sáºµn)
//
// THIáº¾T Káº¾:
//   - TÃ¢m ngÃ£ 5: (400, 400) â€” giá»¯ nguyÃªn tÃ¢m cÅ©
//   - 5 nhÃ¡nh cÃ¡ch Ä‘á»u 72Â°, thá»© tá»±:
//       NORTH     = 270Â° (tháº³ng lÃªn)
//       NORTHEAST = 342Â° (lÃªn-pháº£i)
//       EAST      =  54Â° (xuá»‘ng-pháº£i)
//       SOUTH     = 126Â° (xuá»‘ng-trÃ¡i lá»‡ch)
//       WEST      = 198Â° (xuá»‘ng-trÃ¡i)
//   - Äáº£o trung tÃ¢m trÃ²n bÃ¡n kÃ­nh 72px, cÃ³ cá» + viá»n tráº¯ng
//   - Váº¡ch zebra + stop line + váº¡ch phÃ¢n lÃ n trÃªn má»—i nhÃ¡nh
// ====================================================================

    // =========================================================
    // FIVE WAY â€” NgÃ£ nÄƒm hÃ¬nh ngÃ´i sao (5 nhÃ¡nh Ä‘á»u 72Â°)
    // =========================================================
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        final int cx      = 400;   // tÃ¢m X (giá»¯ nguyÃªn tÃ¢m cÅ© cá»§a há»‡ thá»‘ng)
        final int cy      = 400;   // tÃ¢m Y
        final int roadW   = 100;   // ná»­a chiá»u rá»™ng 1 nhÃ¡nh (tá»•ng 200px má»—i nhÃ¡nh)
        final int armLen  = 700;   // chiá»u dÃ i nhÃ¡nh tá»« tÃ¢m ra ngoÃ i mÃ n hÃ¬nh
        final int islandR = 72;    // bÃ¡n kÃ­nh Ä‘áº£o cá» trung tÃ¢m

        // 5 gÃ³c nhÃ¡nh (radian). Báº¯t Ä‘áº§u tá»« 270Â° (NORTH = tháº³ng lÃªn), +72Â° má»—i nhÃ¡nh.
        // Thá»© tá»± Ã¡nh xáº¡: NORTH, NORTHEAST, EAST, SOUTH, WEST
        double[] angles = {
            Math.toRadians(270),   // NORTH
            Math.toRadians(342),   // NORTHEAST
            Math.toRadians( 54),   // EAST
            Math.toRadians(126),   // SOUTH
            Math.toRadians(198),   // WEST
        };

        // BÆ°á»›c 1: Váº½ 5 nhÃ¡nh Ä‘Æ°á»ng (lá»›p dÆ°á»›i cÃ¹ng)
        for (double angle : angles) {
            drawFiveWayArm(g, cx, cy, angle, armLen, roadW);
        }

        // BÆ°á»›c 2: Phá»§ Ä‘áº£o trung tÃ¢m lÃªn trÃªn Ä‘á»ƒ che cÃ¡c gÃ³c nhá»n giao nhau
        drawFiveWayIsland(g, cx, cy, islandR);

        // BÆ°á»›c 3: Váº¡ch káº» Ä‘Æ°á»ng trÃªn tá»«ng nhÃ¡nh (váº½ SAU Ä‘áº£o Ä‘á»ƒ zebra khÃ´ng bá»‹ che)
        for (double angle : angles) {
            drawFiveWayArmMarkings(g, cx, cy, angle, armLen, roadW, islandR);
        }

        g.dispose();
    }

    // =========================================================
    // Váº½ 1 nhÃ¡nh Ä‘Æ°á»ng (hÃ¬nh chá»¯ nháº­t xoay theo gÃ³c angle)
    // =========================================================
    private void drawFiveWayArm(Graphics2D g2d, int cx, int cy,
                                 double angle, int armLen, int roadW) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Dá»‹ch vá» tÃ¢m, xoay Ä‘á»ƒ trá»¥c Y hÆ°á»›ng theo nhÃ¡nh
        g.translate(cx, cy);
        g.rotate(angle + Math.PI / 2);

        // MÃ©p ngoÃ i tá»‘i (viá»n Ä‘Æ°á»ng)
        g.setColor(new Color(40, 40, 40));
        g.fillRect(-roadW - 5, 0, (roadW + 5) * 2, armLen);

        // LÃ²ng Ä‘Æ°á»ng nhá»±a
        g.setColor(new Color(55, 55, 55));
        g.fillRect(-roadW, 0, roadW * 2, armLen);

        g.dispose();
    }

    // =========================================================
    // Váº½ váº¡ch phÃ¢n lÃ n, váº¡ch zebra, stop line trÃªn 1 nhÃ¡nh
    // =========================================================
    private void drawFiveWayArmMarkings(Graphics2D g2d, int cx, int cy,
                                         double angle, int armLen,
                                         int roadW, int islandR) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(cx, cy);
        g.rotate(angle + Math.PI / 2);

        int startY = islandR + 8;

        // Váº¡ch phÃ¢n lÃ n giá»¯a (Ä‘á»©t Ä‘oáº¡n tráº¯ng)
        g.setColor(Color.WHITE);
        for (int y = startY + 40; y < armLen - 20; y += 40) {
            g.fillRoundRect(-5, y, 10, 20, 4, 4);
        }

        // Váº¡ch viá»n trÃ¡i & pháº£i nhÃ¡nh (má», liÃªn tá»¥c)
        g.setColor(new Color(200, 200, 200, 160));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(-roadW + 4, startY + 20, -roadW + 4, armLen - 10);
        g.drawLine( roadW - 4, startY + 20,  roadW - 4, armLen - 10);

        // Váº¡ch dá»«ng (stop line) â€” nÃ©t Ä‘áº­m ngang
        int stopY = islandR + 46;
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(-roadW, stopY, roadW, stopY);

        // Váº¡ch zebra (váº¡ch Ä‘i bá»™) â€” ngay trÆ°á»›c stop line
        int zebraY = islandR + 28;
        g.setStroke(new BasicStroke(1f));
        g.setColor(Color.WHITE);
        for (int x = -roadW + 6; x < roadW - 6; x += 18) {
            g.fillRect(x, zebraY, 9, 16);
        }

        g.dispose();
    }

    // =========================================================
    // Váº½ Ä‘áº£o giao thÃ´ng trung tÃ¢m: vÃ²ng nhá»±a + cá» + viá»n
    // =========================================================
    private void drawFiveWayIsland(Graphics2D g2d, int cx, int cy, int islandR) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int outerR = islandR + 28;  // bÃ¡n kÃ­nh vÃ¹ng nhá»±a quanh Ä‘áº£o

        // VÃ¹ng nhá»±a hÃ¬nh vÃ nh khuyÃªn quanh Ä‘áº£o
        g.setColor(new Color(55, 55, 55));
        g.fillOval(cx - outerR, cy - outerR, outerR * 2, outerR * 2);

        // Váº¡ch trÃ²n hÆ°á»›ng dáº«n xe cháº¡y vÃ²ng (Ä‘á»©t Ä‘oáº¡n)
        float[] dash = {13.0f, 11.0f};
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        int laneR = outerR - 12;
        g.drawOval(cx - laneR, cy - laneR, laneR * 2, laneR * 2);
        g.setStroke(new BasicStroke(1f)); // reset stroke

        // Cá» Ä‘áº£o â€” lá»›p tá»‘i (viá»n)
        g.setColor(new Color(45, 100, 45));
        g.fillOval(cx - islandR, cy - islandR, islandR * 2, islandR * 2);

        // Cá» Ä‘áº£o â€” lá»›p sÃ¡ng (giá»¯a)
        g.setColor(new Color(70, 140, 60));
        g.fillOval(cx - islandR + 6, cy - islandR + 6,
                (islandR - 6) * 2, (islandR - 6) * 2);

        // Highlight cá» (báº§u dá»¥c sÃ¡ng phÃ­a trÃªn-trÃ¡i)
        g.setColor(new Color(110, 180, 90, 140));
        g.fillOval(cx - islandR / 2 - 6, cy - islandR / 2 - 10,
                islandR, islandR / 2);

        // Viá»n Ä‘áº£o (tráº¯ng má»)
        g.setColor(new Color(230, 230, 230, 220));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(cx - islandR, cy - islandR, islandR * 2, islandR * 2);

        g.dispose();
    }
}