package view.panel;

import model.SimulationConfig;
import model.intersection.IntersectionType;
import util.TrafficDensity;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ControlPanel extends JPanel {

    private JComboBox<String> intersectionBox;
    private JComboBox<String> modeBox;
    private JComboBox<String> lightBox;
    private JComboBox<String> densityBox;

    //private JSlider vehicleSlider;

    private JButton startButton;

    public ControlPanel(
            Consumer<SimulationConfig> onStart
    ) {

        setLayout(new FlowLayout(
                FlowLayout.LEFT,
                10,
                10
        ));

        setBackground(new Color(40, 40, 40));

        // Intersection
        intersectionBox = new JComboBox<>(new String[]{
                "Three Way",
                "Four Way",
                "Five Way"
        });

        // Mode
        modeBox = new JComboBox<>(new String[]{
                "AUTO",
                "MANUAL"
        });

        // Light
        lightBox = new JComboBox<>(new String[]{
                "NO COUNTDOWN",
                "ALWAYS COUNTDOWN",
                "COUNT <= 10"
        });

        // Density
        densityBox = new JComboBox<>(new String[]{
                "LOW",
                "MEDIUM",
                "HIGH"
        });

        // Vehicle count
        /*vehicleSlider = new JSlider(1, 100, 20);
        vehicleSlider.setPreferredSize(
                new Dimension(120, 40)
        );*/

        // Start button
        startButton = new JButton("START");

        add(createLabel("Intersection"));
        add(intersectionBox);

        add(createLabel("Mode"));
        add(modeBox);

        add(createLabel("Light"));
        add(lightBox);

        add(createLabel("Density"));
        add(densityBox);

        //(createLabel("Vehicles"));
        //add(vehicleSlider);

        add(startButton);

        startButton.addActionListener(e ->
                onStart.accept(getConfig())
        );

        setPreferredSize(
                new Dimension(1200, 80)
        );
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setForeground(Color.WHITE);

        return label;
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
                break;
        }

        TrafficDensity density;

        switch ((String) densityBox.getSelectedItem()) {

            case "LOW":
                density = TrafficDensity.LOW;
                break;

            case "HIGH":
                density = TrafficDensity.HIGH;
                break;

            default:
                density = TrafficDensity.MEDIUM;
                break;
        }

        return new SimulationConfig(

                type,

                (String) modeBox.getSelectedItem(),

                (String) lightBox.getSelectedItem(),

                //vehicleSlider.getValue(),

                density
        );
    }
}