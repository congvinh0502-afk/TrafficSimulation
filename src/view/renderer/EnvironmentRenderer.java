package view.renderer;

import java.awt.*;

/**
 * EnvironmentRenderer â€“ Äiá»u phá»‘i toÃ n bá»™ cáº£nh quan mÃ´i trÆ°á»ng.
 *
 * PhÃ¢n cÃ´ng module:
 *   RoadRenderer      â†’ váº½ lÃ²ng Ä‘Æ°á»ng, váº¡ch káº», váº¡ch dá»«ng, bÃ¹ng binh
 *   BuildingRenderer  â†’ váº½ cÃ¡c loáº¡i cÃ´ng trÃ¬nh (skyscraper, factory, luxuryHouse, restaurant)
 *   NatureRenderer    â†’ váº½ cÃ¢y (3 kiá»ƒu), hÃ ng cÃ¢y, cÃ´ng viÃªn há»“ nÆ°á»›c
 *   ParkingRenderer   â†’ váº½ bÃ£i Ä‘á»— xe cÃ³ xe Ä‘áº­u
 *   BaseRenderer      â†’ váº½ ná»n cá», Ä‘Ã¨n Ä‘Æ°á»ng
 *
 * EnvironmentRenderer KHÃ”NG tá»± copy logic váº½ â€“ chá»‰ gá»i cÃ¡c module trÃªn.
 */
public class EnvironmentRenderer {

    private final RoadRenderer      road     = new RoadRenderer();
    private final BuildingRenderer  building = new BuildingRenderer();
    private final NatureRenderer    nature   = new NatureRenderer();
    private final ParkingRenderer   parking  = new ParkingRenderer();
    private final BaseRenderer      base     = new BaseRenderer();

    // =========================================================
    // FOUR WAY â€“ NgÃ£ tÆ°: khu Ä‘Ã´ thá»‹ há»—n há»£p
    //   â€¢ GÃ³c TL : NhÃ  cao táº§ng (skyscraper) + cá»¥m biá»‡t thá»±
    //   â€¢ GÃ³c TR : BÃ£i Ä‘á»— xe lá»›n + nhÃ  mÃ¡y
    //   â€¢ GÃ³c DL : CÃ´ng viÃªn há»“ nÆ°á»›c
    //   â€¢ GÃ³c DR : Cá»¥m nhÃ  á»Ÿ + hÃ ng cÃ¢y Ä‘a dáº¡ng
    // =========================================================
    public void renderFourWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderFourWay(g2d);

        // --- GÃ“C TRÃŠN BÃŠN TRÃI: NhÃ  cao táº§ng + biá»‡t thá»± ---
        building.drawSkyscraper(g2d, 30, 30);
        building.drawLuxuryHouse(g2d, 160, 40);
        building.drawLuxuryHouse(g2d, 155, 140);
        nature.drawTreeRow(g2d, 30, 200, 230, true);

        // --- GÃ“C TRÃŠN BÃŠN PHáº¢I: BÃ£i Ä‘á»— xe + nhÃ  mÃ¡y ---
        building.drawModernFactory(g2d, 580, 40);
        parking.drawParkingLotWithRealCars(g2d, 740, 30, 340, 180);
        nature.drawTreeRow(g2d, 560, 205, 640, true);

        // --- GÃ“C DÆ¯á»šI BÃŠN TRÃI: CÃ´ng viÃªn há»“ nÆ°á»›c ---
        nature.drawParkWithPond(g2d, 15, 560, 240, 220);

        // --- GÃ“C DÆ¯á»šI BÃŠN PHáº¢I: Khu nhÃ  á»Ÿ + hÃ ng cÃ¢y Ä‘a dáº¡ng ---
        nature.drawTreeRow(g2d, 550, 540, 630, true);
        nature.drawTreeRow(g2d, 545, 560, 220, false);
        building.drawLuxuryHouse(g2d, 600, 600);
        building.drawLuxuryHouse(g2d, 720, 650);
        building.drawLuxuryRestaurant(g2d, 870, 620);

        base.drawStreetLights(g2d);
    }

    // =========================================================
    // THREE WAY â€“ NgÃ£ ba: khu ngoáº¡i Ã´ yÃªn tÄ©nh
    //   â€¢ GÃ³c TL : CÃ´ng viÃªn há»“ nÆ°á»›c
    //   â€¢ GÃ³c BL : Cá»¥m biá»‡t thá»± nhá»
    //   â€¢ VÃ¹ng pháº£i: BÃ£i Ä‘á»— xe + nhÃ  á»Ÿ + hÃ ng cÃ¢y dÃ i
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderThreeWay(g2d);

        // Cáº£nh quan bÃªn trÃ¡i
        nature.drawParkWithPond(g2d, 15, 15, 240, 225);
        building.drawLuxuryHouse(g2d, 50, 600);
        building.drawLuxuryHouse(g2d, 155, 650);
        nature.drawTreeRow(g2d, 20, 560, 230, false);

        // VÃ¹ng Ä‘áº¥t bÃªn pháº£i
        parking.drawParkingLotWithRealCars(g2d, 580, 40, 340, 180);
        building.drawModernFactory(g2d, 960, 30);
        building.drawLuxuryHouse(g2d, 600, 580);
        building.drawLuxuryHouse(g2d, 760, 580);
        building.drawSkyscraper(g2d, 940, 580);
        nature.drawTreeRow(g2d, 545, 20, 760, false);
    }

    // =========================================================
    // FIVE WAY â€“ NgÃ£ nÄƒm / bÃ¹ng binh: trung tÃ¢m Ä‘Ã´ thá»‹ sÃ´i Ä‘á»™ng
    //   â€¢ GÃ³c TL : NhÃ  cao táº§ng + biá»‡t thá»±
    //   â€¢ GÃ³c TR : BÃ£i Ä‘á»— xe lá»›n + biá»‡t thá»±
    //   â€¢ GÃ³c DL : CÃ´ng viÃªn há»“ nÆ°á»›c
    //   â€¢ GÃ³c DR : NhÃ  mÃ¡y + nhÃ  hÃ ng + khu nhÃ  á»Ÿ
    //   â€¢ BÃ¹ng binh: 4 cÃ¢y trang trÃ­ trung tÃ¢m
    // =========================================================
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        base.drawGrass(g);
        road.renderFiveWay(g);

        // CÃ¢y trang trÃ­ trÃªn Ä‘áº£o bÃ¹ng binh trung tÃ¢m (tÃ¢m 400,400)
        int cx = 400, cy = 400;
        nature.drawPineTree(g, cx - 18, cy - 58);
        nature.drawBeautifulTree(g, cx - 18, cy + 28);
        nature.drawBushTree(g, cx - 58, cy - 18);
        nature.drawPineTree(g, cx + 28, cy - 18);

        // --- GÃ“C TRÃŠN BÃŠN TRÃI ---
        building.drawSkyscraper(g, 30, 30);
        building.drawLuxuryHouse(g, 160, 40);
        building.drawLuxuryHouse(g, 155, 140);
        nature.drawTreeRow(g, 30, 205, 230, true);

        // --- GÃ“C TRÃŠN BÃŠN PHáº¢I (Ä‘áº©y xa Ä‘á»ƒ nhÆ°á»ng Ä‘Æ°á»ng chÃ©o) ---
        building.drawLuxuryHouse(g, 560, 40);
        parking.drawParkingLotWithRealCars(g, 850, 30, 320, 175);
        nature.drawTreeRow(g, 555, 205, 300, true);

        // --- GÃ“C DÆ¯á»šI BÃŠN TRÃI ---
        nature.drawParkWithPond(g, 15, 560, 240, 220);

        // --- GÃ“C DÆ¯á»šI BÃŠN PHáº¢I ---
        building.drawModernFactory(g, 590, 615);
        building.drawLuxuryRestaurant(g, 730, 650);
        building.drawLuxuryHouse(g, 870, 600);
        nature.drawTreeRow(g, 555, 540, 625, true);
        nature.drawTreeRow(g, 545, 565, 215, false);

        base.drawStreetLights(g);
        g.dispose();
    }
}