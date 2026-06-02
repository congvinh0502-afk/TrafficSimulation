package view.frame;

import layout.IntersectionLayoutFactory;
import manager.LaneManager;
import model.SimulationConfig;
import model.intersection.IntersectionType;
import util.TrafficDensity;

import view.panel.ControlPanel;
import view.panel.SimulationPanel;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private SimulationPanel simulationPanel;
    private ControlPanel    controlPanel;

    public MainFrame() {

        setTitle("Smart City Traffic Simulation");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        SimulationConfig defaultConfig = new SimulationConfig(
                IntersectionType.FOUR_WAY,
                "AUTO",
                "NO COUNTDOWN",
                TrafficDensity.LOW
        );

        // *** FIX: inject layout trước khi SimulationPanel dùng LaneManager ***
        LaneManager.setLayout(
                IntersectionLayoutFactory.create(defaultConfig.getIntersectionType())
        );

        simulationPanel = new SimulationPanel(defaultConfig);

        controlPanel = new ControlPanel(config -> {
            simulationPanel.applyConfig(config);
        });

        add(controlPanel,    BorderLayout.NORTH);
        add(simulationPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
