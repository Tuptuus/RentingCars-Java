package GUI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Wypożyczalnia pojazdów");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Tworzymy DetailsPanel
        DetailsPanel detailsPanel = new DetailsPanel();
        // Przekazujemy go do VehicleListPanel
        VehicleListPanel listPanel = new VehicleListPanel(detailsPanel);

        // Dodajemy panele
        add(listPanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
    }
}
