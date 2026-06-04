package view.scene;

import camera.Camera;
import config.Constants;
import controller.TrafficController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import manager.VehicleSpawnManager;
import model.SimulationConfig;
import model.network.IntersectionNode;
import model.network.NetworkLayout;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import util.Direction;
import util.TrafficDensity;
import view.renderer.EnvironmentRenderer;
import view.renderer.TrafficLightRenderer;
import view.renderer.VehicleRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimulationScene {

    private final Scene  scene;
    private final Canvas canvas;
    private final Camera camera = new Camera();
    private final Stage  primaryStage;

    private final SimulationConfig      config;
    private final List<Vehicle>         vehicles      = new ArrayList<>();
    private final List<IntersectionNode> intersections = new ArrayList<>();

    private final TrafficController   ctrl;
    private final VehicleSpawnManager spawner;
    private final EnvironmentRenderer envR    = new EnvironmentRenderer();
    private final TrafficLightRenderer lightR  = new TrafficLightRenderer();
    private final VehicleRenderer      vehicleR = new VehicleRenderer();

    private Label   hudLabel;
    private int     fps=60, frames=0;
    private long    lastFps = System.nanoTime();
    private boolean flash=false;
    private int     flashTick=0, spawnTick=0;
    private String  jamLevel="LOW";
    private int     runtimeMax;
    private TrafficDensity runtimeDensity;
    private String  lightMode;
    private AnimationTimer timer;

    public SimulationScene(Stage stage, SimulationConfig cfg) {
        this.primaryStage   = stage;
        this.config         = cfg;
        this.runtimeMax     = cfg.getVehicleCount();
        this.runtimeDensity = cfg.getTrafficDensity();
        this.lightMode      = cfg.getLightType();

        buildNodes();
        ctrl    = new TrafficController();
        spawner = new VehicleSpawnManager(vehicles);

        canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        scene  = new Scene(buildUI(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        scene.setFill(Color.DIMGRAY);

        camera.centerOn(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        bindEvents();
        spawnInitial();
        startLoop();
    }

    // ── Giao lộ ────────────────────────────────────────────────
    private void buildNodes() {
        intersections.add(new IntersectionNode(
            NetworkLayout.TW_X, NetworkLayout.TW_Y,
            model.intersection.IntersectionType.THREE_WAY, null, null));

        TrafficLight fv = new TrafficLight(LightColor.GREEN, Constants.LIGHT_GREEN_DURATION);
        TrafficLight fh = new TrafficLight(LightColor.RED,   Constants.LIGHT_RED_DURATION);
        intersections.add(new IntersectionNode(
            NetworkLayout.FW_X, NetworkLayout.FW_Y,
            model.intersection.IntersectionType.FOUR_WAY, fv, fh));

        TrafficLight vv = new TrafficLight(LightColor.GREEN, Constants.LIGHT_GREEN_DURATION);
        TrafficLight vh = new TrafficLight(LightColor.RED,   Constants.LIGHT_RED_DURATION);
        intersections.add(new IntersectionNode(
            NetworkLayout.VW_X, NetworkLayout.VW_Y,
            model.intersection.IntersectionType.FIVE_WAY, vv, vh));
    }

    // ── UI ─────────────────────────────────────────────────────
    private StackPane buildUI() {
        StackPane root = new StackPane();
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        BorderPane ov = new BorderPane();
        ov.setPickOnBounds(false);
        hudLabel = new Label();
        hudLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        hudLabel.setTextFill(Color.WHITE);
        hudLabel.setStyle("-fx-background-color:rgba(0,0,0,.7);-fx-background-radius:9;-fx-padding:10;");
        hudLabel.setMouseTransparent(true);
        BorderPane.setMargin(hudLabel, new Insets(14));
        ov.setLeft(hudLabel);
        ov.setRight(buildBar());
        BorderPane.setMargin(ov.getRight(), new Insets(14));
        ov.setPickOnBounds(false);
        root.getChildren().add(ov);
        return root;
    }

    private HBox buildBar() {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.TOP_RIGHT);
        Button back  = btn("← Menu",  "#C62828");
        Button pause = btn("⏸ Pause", "#37474F");
        back.setOnAction(e  -> goMenu());
        pause.setOnAction(e -> {
            if (pause.getText().startsWith("⏸")) { timer.stop();  pause.setText("▶ Resume"); }
            else                                  { timer.start(); pause.setText("⏸ Pause"); }
        });
        bar.getChildren().addAll(back, pause, buildSettingsMenu());
        return bar;
    }

    private Button btn(String t, String bg) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:"+bg+";-fx-text-fill:white;-fx-font-size:12px;" +
                   "-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:6 12;");
        return b;
    }

    private MenuButton buildSettingsMenu() {
        MenuButton mb = new MenuButton("⚙ Settings");
        mb.setStyle("-fx-background-color:#455A64;-fx-text-fill:white;-fx-font-size:12px;" +
                    "-fx-background-radius:8;-fx-cursor:hand;");

        Menu cm = new Menu("Max Vehicles");
        for (int v : new int[]{10,20,30,50,80,100}) {
            MenuItem mi = new MenuItem(String.valueOf(v));
            mi.setOnAction(e -> runtimeMax = v);
            cm.getItems().add(mi);
        }
        Menu dm = new Menu("Density");
        ToggleGroup dg = new ToggleGroup();
        for (TrafficDensity d : TrafficDensity.values()) {
            RadioMenuItem ri = new RadioMenuItem(d.name());
            ri.setToggleGroup(dg);
            if (d==runtimeDensity) ri.setSelected(true);
            ri.setOnAction(e -> runtimeDensity = d);
            dm.getItems().add(ri);
        }
        Menu lm = new Menu("Light Timer");
        for (String s : new String[]{"NO COUNTDOWN","ALWAYS COUNTDOWN","COUNT <= 10"}) {
            MenuItem li = new MenuItem(s); li.setOnAction(e -> lightMode = s); lm.getItems().add(li);
        }
        MenuItem rc = new MenuItem("Reset Camera  [R]");   rc.setOnAction(e -> resetCam());
        MenuItem tl = new MenuItem("Toggle N-S Lights");   tl.setOnAction(e -> toggleLights());
        mb.getItems().addAll(cm, dm, lm, new SeparatorMenuItem(), rc, tl);
        return mb;
    }

    // ── Events ─────────────────────────────────────────────────
    private void bindEvents() {
        canvas.setOnScroll(camera::handleScroll);
        canvas.setOnMousePressed(e  -> camera.startDrag(e.getX(), e.getY()));
        canvas.setOnMouseDragged(e  -> camera.drag(e.getX(), e.getY()));
        canvas.setOnMouseReleased(e -> camera.stopDrag());
        scene.setOnKeyPressed(e -> { if (e.getCode()==KeyCode.R) resetCam(); });
    }

    private void resetCam() {
        camera.reset();
        camera.centerOn(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // ── Game loop ──────────────────────────────────────────────
    private void startLoop() {
        timer = new AnimationTimer() {
            @Override public void handle(long now) { update(); render(); tick(now); }
        };
        timer.start();
    }

    private void update() {
        if (!"MANUAL".equals(config.getTrafficMode())) {
            for (IntersectionNode n : intersections) n.updateLights();
            smartLights();
        }
        ctrl.updateVehicles(vehicles, intersections);
        spawner.removeOutsideVehicles();
        doSpawn();
        jam();
        if (++flashTick >= Constants.FLASH_INTERVAL_FRAMES) { flash=!flash; flashTick=0; }
    }

    // ── Render ─────────────────────────────────────────────────
    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(50,50,50));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        camera.applyTransform(gc);

        envR.renderNetworkWorld(gc);

        for (IntersectionNode n : intersections) {
            if (!n.hasTrafficLights()) continue;
            lightR.renderVertical(gc,   n.verticalLight,   n.cx, n.cy, lightMode);
            lightR.renderHorizontal(gc, n.horizontalLight, n.cx, n.cy, lightMode);
        }

        for (Vehicle v : vehicles) vehicleR.render(gc, v, flash);

        camera.restoreTransform(gc);
        hud();
    }

    private void hud() {
        IntersectionNode fw = intersections.get(1);
        IntersectionNode vw = intersections.get(2);
        hudLabel.setText(String.format(
            "  Vehicles : %d / %d%n" +
            "  Stopped  : %d%n" +
            "  FPS      : %d%n" +
            "  Jam      : %s%n" +
            "  4W N/S   : %s%n" +
            "  4W E/W   : %s%n" +
            "  5W N/S   : %s%n" +
            "  Zoom     : %.0f%%%n" +
            "  [Scroll] Zoom · [Drag] Pan · [R] Reset",
            vehicles.size(), runtimeMax,
            ctrl.countStopped(vehicles),
            fps, jamLevel,
            fw.verticalLight.getColor(), fw.horizontalLight.getColor(),
            vw.verticalLight.getColor(),
            camera.getZoom()*100));
    }

    // ── Smart lights ───────────────────────────────────────────
    private void smartLights() {
        for (IntersectionNode n : intersections) {
            if (!n.hasTrafficLights()) continue;
            int vc = ctrl.countByDirection(vehicles, Direction.NORTH, Direction.SOUTH);
            int hc = ctrl.countByDirection(vehicles, Direction.EAST, Direction.WEST);
            int TH = Constants.SMART_LIGHT_THRESHOLD, MX = Constants.SMART_LIGHT_MAX_TIMER;
            if (vc > hc+TH && n.verticalLight.getColor()==LightColor.GREEN)
                n.verticalLight.setTimer(Math.max(n.verticalLight.getTimer(), MX));
            if (hc > vc+TH && n.horizontalLight.getColor()==LightColor.GREEN)
                n.horizontalLight.setTimer(Math.max(n.horizontalLight.getTimer(), MX));
        }
    }

    private void toggleLights() {
        for (IntersectionNode n : intersections) {
            if (!n.hasTrafficLights()) continue;
            boolean vGreen = n.verticalLight.getColor()==LightColor.GREEN;
            n.verticalLight.setColor(  vGreen ? LightColor.RED   : LightColor.GREEN);
            n.horizontalLight.setColor(vGreen ? LightColor.GREEN : LightColor.RED);
            n.verticalLight.setTimer(Constants.LIGHT_GREEN_DURATION);
            n.horizontalLight.setTimer(Constants.LIGHT_GREEN_DURATION);
        }
    }

    // ── Spawn ──────────────────────────────────────────────────
    private void spawnInitial() {
        List<Direction> dirs = dirs();
        for (int i = 0; i < runtimeMax; i++) spawner.spawnRandomVehicle(dirs);
    }

    private void doSpawn() {
        if (++spawnTick >= interval()) {
            spawnTick = 0;
            if (vehicles.size() < runtimeMax) spawner.spawnRandomVehicle(dirs());
        }
    }

    private List<Direction> dirs() {
        return Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    }

    private int interval() {
        switch (runtimeDensity) {
            case LOW:  return 200;
            case HIGH: return 55;
            default:   return 110;
        }
    }

    // ── Stats ──────────────────────────────────────────────────
    private void jam() {
        int tot = vehicles.size(); if (tot==0){jamLevel="LOW";return;}
        double r = (double)ctrl.countStopped(vehicles)/tot;
        jamLevel = r>Constants.JAM_HIGH_THRESHOLD?"HIGH":r>Constants.JAM_MEDIUM_THRESHOLD?"MEDIUM":"LOW";
    }

    private void tick(long now) {
        frames++;
        if (now-lastFps >= 1_000_000_000L) { fps=frames; frames=0; lastFps=now; }
    }

    private void goMenu() {
        if (timer!=null) timer.stop();
        primaryStage.setScene(new MenuScene(primaryStage).getScene());
    }

    public Scene getScene() { return scene; }
}
