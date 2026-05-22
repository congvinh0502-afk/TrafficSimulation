package view.frame;

import model.SimulationConfig;
import view.panel.MenuPanel;
import view.panel.SimulationPanel;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("Smart City Traffic Simulation");

        setSize(1200, 800);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        showMenu();

        setVisible(true);
    }

  private void showMenu() {

    final MenuPanel[] menuPanelRef = new MenuPanel[1];

    menuPanelRef[0] = new MenuPanel(() -> {

        SimulationConfig config =
                menuPanelRef[0].getConfig();

        startSimulation(config);
    });

    setContentPane(menuPanelRef[0]);

    revalidate();
}

    private void startSimulation(SimulationConfig config) {

        SimulationPanel simulationPanel =
                new SimulationPanel(config);

        setContentPane(simulationPanel);

        revalidate();
    }
}