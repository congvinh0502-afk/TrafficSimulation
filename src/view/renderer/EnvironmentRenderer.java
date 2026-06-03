package view.renderer;

import java.awt.*;

/**
 * EnvironmentRenderer Ã¢â‚¬â€œ Ã„ÂiÃ¡Â»Âu phÃ¡Â»â€˜i toÃƒÂ n bÃ¡Â»â„¢ cÃ¡ÂºÂ£nh quan mÃƒÂ´i trÃ†Â°Ã¡Â»Âng.
 *
 * PhÃƒÂ¢n cÃƒÂ´ng module:
 *   RoadRenderer      Ã¢â€ â€™ vÃ¡ÂºÂ½ lÃƒÂ²ng Ã„â€˜Ã†Â°Ã¡Â»Âng, vÃ¡ÂºÂ¡ch kÃ¡ÂºÂ», vÃ¡ÂºÂ¡ch dÃ¡Â»Â«ng, bÃƒÂ¹ng binh
 *   BuildingRenderer  Ã¢â€ â€™ vÃ¡ÂºÂ½ cÃƒÂ¡c loÃ¡ÂºÂ¡i cÃƒÂ´ng trÃƒÂ¬nh (skyscraper, factory, luxuryHouse, restaurant)
 *   NatureRenderer    Ã¢â€ â€™ vÃ¡ÂºÂ½ cÃƒÂ¢y (3 kiÃ¡Â»Æ’u), hÃƒÂ ng cÃƒÂ¢y, cÃƒÂ´ng viÃƒÂªn hÃ¡Â»â€œ nÃ†Â°Ã¡Â»â€ºc
 *   ParkingRenderer   Ã¢â€ â€™ vÃ¡ÂºÂ½ bÃƒÂ£i Ã„â€˜Ã¡Â»â€” xe cÃƒÂ³ xe Ã„â€˜Ã¡ÂºÂ­u
 *   BaseRenderer      Ã¢â€ â€™ vÃ¡ÂºÂ½ nÃ¡Â»Ân cÃ¡Â»Â, Ã„â€˜ÃƒÂ¨n Ã„â€˜Ã†Â°Ã¡Â»Âng
 *
 * EnvironmentRenderer KHÃƒâ€NG tÃ¡Â»Â± copy logic vÃ¡ÂºÂ½ Ã¢â‚¬â€œ chÃ¡Â»â€° gÃ¡Â»Âi cÃƒÂ¡c module trÃƒÂªn.
 */
public class EnvironmentRenderer {

    private final RoadRenderer      road     = new RoadRenderer();
    private final BuildingRenderer  building = new BuildingRenderer();
    private final NatureRenderer    nature   = new NatureRenderer();
    private final ParkingRenderer   parking  = new ParkingRenderer();
    private final BaseRenderer      base     = new BaseRenderer();

    // =========================================================
    // FOUR WAY Ã¢â‚¬â€œ NgÃƒÂ£ tÃ†Â°: khu Ã„â€˜ÃƒÂ´ thÃ¡Â»â€¹ hÃ¡Â»â€”n hÃ¡Â»Â£p
    //   Ã¢â‚¬Â¢ GÃƒÂ³c TL : NhÃƒÂ  cao tÃ¡ÂºÂ§ng (skyscraper) + cÃ¡Â»Â¥m biÃ¡Â»â€¡t thÃ¡Â»Â±
    //   Ã¢â‚¬Â¢ GÃƒÂ³c TR : BÃƒÂ£i Ã„â€˜Ã¡Â»â€” xe lÃ¡Â»â€ºn + nhÃƒÂ  mÃƒÂ¡y
    //   Ã¢â‚¬Â¢ GÃƒÂ³c DL : CÃƒÂ´ng viÃƒÂªn hÃ¡Â»â€œ nÃ†Â°Ã¡Â»â€ºc
    //   Ã¢â‚¬Â¢ GÃƒÂ³c DR : CÃ¡Â»Â¥m nhÃƒÂ  Ã¡Â»Å¸ + hÃƒÂ ng cÃƒÂ¢y Ã„â€˜a dÃ¡ÂºÂ¡ng
    // =========================================================
    public void renderFourWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderFourWay(g2d);

        // --- GÃƒâ€œC TRÃƒÅ N BÃƒÅ N TRÃƒÂI: NhÃƒÂ  cao tÃ¡ÂºÂ§ng + biÃ¡Â»â€¡t thÃ¡Â»Â± ---
        building.drawSkyscraper(g2d, 30, 30);
        building.drawLuxuryHouse(g2d, 160, 40);
        building.drawLuxuryHouse(g2d, 155, 140);
        nature.drawTreeRow(g2d, 30, 200, 230, true);

        // --- GÃƒâ€œC TRÃƒÅ N BÃƒÅ N PHÃ¡ÂºÂ¢I: BÃƒÂ£i Ã„â€˜Ã¡Â»â€” xe + nhÃƒÂ  mÃƒÂ¡y ---
        building.drawModernFactory(g2d, 580, 40);
        parking.drawParkingLotWithRealCars(g2d, 740, 30, 340, 180);
        nature.drawTreeRow(g2d, 560, 205, 640, true);

        // --- GÃƒâ€œC DÃ†Â¯Ã¡Â»Å¡I BÃƒÅ N TRÃƒÂI: CÃƒÂ´ng viÃƒÂªn hÃ¡Â»â€œ nÃ†Â°Ã¡Â»â€ºc ---
        nature.drawParkWithPond(g2d, 15, 560, 240, 220);

        // --- GÃƒâ€œC DÃ†Â¯Ã¡Â»Å¡I BÃƒÅ N PHÃ¡ÂºÂ¢I: Khu nhÃƒÂ  Ã¡Â»Å¸ + hÃƒÂ ng cÃƒÂ¢y Ã„â€˜a dÃ¡ÂºÂ¡ng ---
        nature.drawTreeRow(g2d, 550, 540, 630, true);
        nature.drawTreeRow(g2d, 545, 560, 220, false);
        building.drawLuxuryHouse(g2d, 600, 600);
        building.drawLuxuryHouse(g2d, 720, 650);
        building.drawLuxuryRestaurant(g2d, 870, 620);

        base.drawStreetLights(g2d);
    }

    // =========================================================
    // THREE WAY Ã¢â‚¬â€œ NgÃƒÂ£ ba: khu ngoÃ¡ÂºÂ¡i ÃƒÂ´ yÃƒÂªn tÃ„Â©nh
    //   Ã¢â‚¬Â¢ GÃƒÂ³c TL : CÃƒÂ´ng viÃƒÂªn hÃ¡Â»â€œ nÃ†Â°Ã¡Â»â€ºc
    //   Ã¢â‚¬Â¢ GÃƒÂ³c BL : CÃ¡Â»Â¥m biÃ¡Â»â€¡t thÃ¡Â»Â± nhÃ¡Â»Â
    //   Ã¢â‚¬Â¢ VÃƒÂ¹ng phÃ¡ÂºÂ£i: BÃƒÂ£i Ã„â€˜Ã¡Â»â€” xe + nhÃƒÂ  Ã¡Â»Å¸ + hÃƒÂ ng cÃƒÂ¢y dÃƒÂ i
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderThreeWay(g2d);

        // CÃ¡ÂºÂ£nh quan bÃƒÂªn trÃƒÂ¡i
        nature.drawParkWithPond(g2d, 15, 15, 240, 225);
        building.drawLuxuryHouse(g2d, 50, 600);
        building.drawLuxuryHouse(g2d, 155, 650);
        nature.drawTreeRow(g2d, 20, 560, 230, false);

        // VÃƒÂ¹ng Ã„â€˜Ã¡ÂºÂ¥t bÃƒÂªn phÃ¡ÂºÂ£i
        parking.drawParkingLotWithRealCars(g2d, 580, 40, 340, 180);
        building.drawModernFactory(g2d, 960, 30);
        building.drawLuxuryHouse(g2d, 600, 580);
        building.drawLuxuryHouse(g2d, 760, 580);
        building.drawSkyscraper(g2d, 940, 580);
        nature.drawTreeRow(g2d, 545, 20, 760, false);
    }

    // =========================================================
    // FIVE WAY Ã¢â‚¬â€œ NgÃƒÂ£ nÃ„Æ’m / bÃƒÂ¹ng binh: trung tÃƒÂ¢m Ã„â€˜ÃƒÂ´ thÃ¡Â»â€¹ sÃƒÂ´i Ã„â€˜Ã¡Â»â„¢ng
    //   Ã¢â‚¬Â¢ GÃƒÂ³c TL : NhÃƒÂ  cao tÃ¡ÂºÂ§ng + biÃ¡Â»â€¡t thÃ¡Â»Â±
    //   Ã¢â‚¬Â¢ GÃƒÂ³c TR : BÃƒÂ£i Ã„â€˜Ã¡Â»â€” xe lÃ¡Â»â€ºn + biÃ¡Â»â€¡t thÃ¡Â»Â±
    //   Ã¢â‚¬Â¢ GÃƒÂ³c DL : CÃƒÂ´ng viÃƒÂªn hÃ¡Â»â€œ nÃ†Â°Ã¡Â»â€ºc
    //   Ã¢â‚¬Â¢ GÃƒÂ³c DR : NhÃƒÂ  mÃƒÂ¡y + nhÃƒÂ  hÃƒÂ ng + khu nhÃƒÂ  Ã¡Â»Å¸
    //   Ã¢â‚¬Â¢ BÃƒÂ¹ng binh: 4 cÃƒÂ¢y trang trÃƒÂ­ trung tÃƒÂ¢m
    // =========================================================
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        base.drawGrass(g);
        road.renderFiveWay(g);

        // CÃƒÂ¢y trang trÃƒÂ­ trÃƒÂªn Ã„â€˜Ã¡ÂºÂ£o bÃƒÂ¹ng binh trung tÃƒÂ¢m (tÃƒÂ¢m 400,400)
        int cx = 500, cy = 400; // THAY DOI: 400 → 500
        nature.drawPineTree(g, cx - 18, cy - 58);
        nature.drawBeautifulTree(g, cx - 18, cy + 28);
        nature.drawBushTree(g, cx - 58, cy - 18);
        nature.drawPineTree(g, cx + 28, cy - 18);

        // --- GÃƒâ€œC TRÃƒÅ N BÃƒÅ N TRÃƒÂI ---
        building.drawSkyscraper(g, 30, 30);
        building.drawLuxuryHouse(g, 160, 40);
        building.drawLuxuryHouse(g, 155, 140);
        nature.drawTreeRow(g, 30, 205, 230, true);

        // --- GÃƒâ€œC TRÃƒÅ N BÃƒÅ N PHÃ¡ÂºÂ¢I (Ã„â€˜Ã¡ÂºÂ©y xa Ã„â€˜Ã¡Â»Æ’ nhÃ†Â°Ã¡Â»Âng Ã„â€˜Ã†Â°Ã¡Â»Âng chÃƒÂ©o) ---
        building.drawLuxuryHouse(g, 560, 40);
        parking.drawParkingLotWithRealCars(g, 875, 295, 300, 150); // THAY DOI
        nature.drawTreeRow(g, 875, 270, 300, true); // THAY DOI

        // --- GÃƒâ€œC DÃ†Â¯Ã¡Â»Å¡I BÃƒÅ N TRÃƒÂI ---
        nature.drawParkWithPond(g, 15, 560, 240, 220);

        // --- GÃƒâ€œC DÃ†Â¯Ã¡Â»Å¡I BÃƒÅ N PHÃ¡ÂºÂ¢I ---
        building.drawModernFactory(g, 580, 700); // THAY DOI
        building.drawLuxuryRestaurant(g, 600, 730); // THAY DOI
        building.drawLuxuryHouse(g, 700, 775); // THAY DOI
        nature.drawTreeRow(g, 555, 540, 625, true);
        nature.drawTreeRow(g, 545, 565, 215, false);

        base.drawStreetLights(g);
        g.dispose();
    }
}


