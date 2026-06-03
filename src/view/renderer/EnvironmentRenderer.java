package view.renderer;

import javafx.scene.canvas.GraphicsContext;

/**
 * Điều phối vẽ toàn bộ cảnh quan (JavaFX).
 *
 * <p>Gọi các renderer con theo thứ tự lớp vẽ.</p>
 */
public class EnvironmentRenderer {

    private final BaseRenderer     base     = new BaseRenderer();
    private final RoadRenderer     road     = new RoadRenderer();
    private final BuildingRenderer building = new BuildingRenderer();
    private final NatureRenderer   nature   = new NatureRenderer();
    private final ParkingRenderer  parking  = new ParkingRenderer();

    public void renderFourWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderFourWay(gc);

        building.drawSkyscraper(gc, 30, 30);
        building.drawLuxuryHouse(gc, 160, 40);
        building.drawLuxuryHouse(gc, 155, 140);
        nature.drawTreeRow(gc, 30, 200, 230, true);

        building.drawModernFactory(gc, 580, 40);
        parking.drawParkingLotWithCars(gc, 740, 30, 340, 180);
        nature.drawTreeRow(gc, 560, 205, 640, true);

        nature.drawParkWithPond(gc, 15, 560, 240, 220);

        nature.drawTreeRow(gc, 550, 540, 630, true);
        nature.drawTreeRow(gc, 545, 560, 220, false);
        building.drawLuxuryHouse(gc, 600, 600);
        building.drawLuxuryHouse(gc, 720, 650);
        building.drawLuxuryRestaurant(gc, 870, 620);

        base.drawStreetLights(gc);
    }

    public void renderThreeWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderThreeWay(gc);

        nature.drawParkWithPond(gc, 15, 15, 240, 225);
        building.drawLuxuryHouse(gc, 50, 600);
        building.drawLuxuryHouse(gc, 155, 650);
        nature.drawTreeRow(gc, 20, 560, 230, false);

        parking.drawParkingLotWithCars(gc, 580, 40, 340, 180);
        building.drawModernFactory(gc, 960, 30);
        building.drawLuxuryHouse(gc, 600, 580);
        building.drawLuxuryHouse(gc, 760, 580);
        building.drawSkyscraper(gc, 940, 580);
        nature.drawTreeRow(gc, 545, 20, 760, false);
    }

    public void renderFiveWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderFiveWay(gc);

        int cx=400, cy=400;
        nature.drawPineTree(gc, cx-18, cy-58);
        nature.drawBeautifulTree(gc, cx-18, cy+28);
        nature.drawBushTree(gc, cx-58, cy-18);
        nature.drawPineTree(gc, cx+28, cy-18);

        building.drawSkyscraper(gc, 30, 30);
        building.drawLuxuryHouse(gc, 160, 40);
        building.drawLuxuryHouse(gc, 155, 140);
        nature.drawTreeRow(gc, 30, 205, 230, true);

        building.drawLuxuryHouse(gc, 560, 40);
        parking.drawParkingLotWithCars(gc, 850, 30, 320, 175);
        nature.drawTreeRow(gc, 555, 205, 300, true);

        nature.drawParkWithPond(gc, 15, 560, 240, 220);

        building.drawModernFactory(gc, 590, 615);
        building.drawLuxuryRestaurant(gc, 730, 650);
        building.drawLuxuryHouse(gc, 870, 600);
        nature.drawTreeRow(gc, 555, 540, 625, true);
        nature.drawTreeRow(gc, 545, 565, 215, false);

        base.drawStreetLights(gc);
    }
}
