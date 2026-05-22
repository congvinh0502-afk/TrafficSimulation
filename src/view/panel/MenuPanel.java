package view.panel;

import model.SimulationConfig;

import javax.swing.*;
import java.awt.*;
import model.intersection.IntersectionType;

public class MenuPanel extends JPanel {

    private JComboBox<String> intersectionBox;

    private JComboBox<String> modeBox;

    private JComboBox<String> lightBox;

    private JSlider vehicleSlider;

    private JButton startButton;

    public MenuPanel(Runnable onStart) {

        setLayout(null);

        setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("SMART TRAFFIC SIMULATION");

        title.setForeground(Color.WHITE);

        title.setFont(new Font("Arial", Font.BOLD, 28));

        title.setBounds(350, 50, 500, 40);

        add(title);

        // intersection

        JLabel intersectionLabel = new JLabel("Intersection:");

        intersectionLabel.setForeground(Color.WHITE);

        intersectionLabel.setBounds(350, 150, 150, 30);

        add(intersectionLabel);

        intersectionBox = new JComboBox<>(new String[] {
                "Three Way",
                "Four Way",
                "Five Way"
        });

        intersectionBox.setBounds(500, 150, 200, 30);

        add(intersectionBox);

        // mode

        JLabel modeLabel = new JLabel("Traffic Mode:");

        modeLabel.setForeground(Color.WHITE);

        modeLabel.setBounds(350, 220, 150, 30);

        add(modeLabel);

        modeBox = new JComboBox<>(new String[] {
                "AUTO",
                "MANUAL"
        });

        modeBox.setBounds(500, 220, 200, 30);

        add(modeBox);

        // light type

        JLabel lightLabel = new JLabel("Light Type:");

        lightLabel.setForeground(Color.WHITE);

        lightLabel.setBounds(350, 290, 150, 30);

        add(lightLabel);

        lightBox = new JComboBox<>(new String[] {
                "NO COUNTDOWN",
                "ALWAYS COUNTDOWN",
                "COUNT <= 10"
        });

        lightBox.setBounds(500, 290, 200, 30);

        add(lightBox);

        // vehicle count

        JLabel vehicleLabel = new JLabel("Vehicle Count:");

        vehicleLabel.setForeground(Color.WHITE);

        vehicleLabel.setBounds(350, 360, 150, 30);

        add(vehicleLabel);

        vehicleSlider = new JSlider(1, 100, 20);

        vehicleSlider.setBounds(500, 360, 200, 50);

        vehicleSlider.setMajorTickSpacing(10);

        vehicleSlider.setPaintTicks(true);

        vehicleSlider.setPaintLabels(true);

        add(vehicleSlider);

        // start button

        startButton = new JButton("START SIMULATION");

        startButton.setBounds(450, 500, 250, 50);

        add(startButton);

        startButton.addActionListener(e -> {

            onStart.run();

        });
    }

    public SimulationConfig getConfig() {

    IntersectionType type;

    switch ((String) intersectionBox.getSelectedItem()) {

        case "Three Way":

            type = IntersectionType.THREE_WAY;
            break;

        case "Five Way":

            type = IntersectionType.FIVE_WAY;
            break;

        default:

            type = IntersectionType.FOUR_WAY;
    }

    return new SimulationConfig(

            type,

            (String) modeBox.getSelectedItem(),

            (String) lightBox.getSelectedItem(),

            vehicleSlider.getValue()
    );
}
}