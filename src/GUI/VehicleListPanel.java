// src/GUI/VehicleListPanel.java
package GUI;

import db.VehicleRepository;
import model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleListPanel extends JPanel {
    private final JButton carsButton;
    private final JButton bikesButton;
    private final JButton scootersButton;
    private final JButton rentedButton;
    private final DetailsPanel detailsPanel;

    public VehicleListPanel(DetailsPanel detailsPanel) {
        this.detailsPanel = detailsPanel;

        setBorder(BorderFactory.createTitledBorder("Kategorie pojazdów"));
        setLayout(new GridLayout(4, 1, 10, 10));

        carsButton     = createTile("Auta");
        bikesButton    = createTile("Rowery");
        scootersButton = createTile("Skutery");
        rentedButton   = createTile("Wypożyczone");

        add(carsButton);
        add(bikesButton);
        add(scootersButton);
        add(rentedButton);

        carsButton    .addActionListener(e -> show(VehicleRepository.findAllCars()));
        bikesButton   .addActionListener(e -> show(VehicleRepository.findAllBikes()));
        scootersButton.addActionListener(e -> show(VehicleRepository.findAllScooters()));
        rentedButton  .addActionListener(e -> show(VehicleRepository.findAllRented()));
    }

    private JButton createTile(String title) {
        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(16f));
        return btn;
    }

    private void show(List<? extends Vehicle> list) {
        detailsPanel.showVehicles(new ArrayList<>(list));
    }
}
