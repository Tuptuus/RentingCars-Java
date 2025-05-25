// src/GUI/DetailsPanel.java
package GUI;

import db.VehicleRepository;
import model.Vehicle;
import model.Car;
import model.Bike;
import model.Scooter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class DetailsPanel extends JPanel {
    private final DefaultListModel<Vehicle> model = new DefaultListModel<>();
    private final JList<Vehicle> list = new JList<>(model);

    // Komponenty szczegółów
    private final JLabel lblBrandModel    = new JLabel("–");
    private final JLabel lblYear          = new JLabel("–");
    private final JLabel lblColor         = new JLabel("–");
    private final JLabel lblSerialOrPlate = new JLabel("–");
    private final JLabel lblPricePerHour  = new JLabel("–");
    private final JLabel lblStatus        = new JLabel("–");

    // Panele dat (CardLayout: input vs. label)
    private final JPanel cardDates;
    private final JPanel inputDatesPanel;
    private final JSpinner spinnerFrom;
    private final JSpinner spinnerTo;
    private final JSpinner spinnerDiscount;
    private final JPanel labelDatesPanel;
    private final JLabel lblFromValue;
    private final JLabel lblToValue;

    private final JLabel lblTotalPrice    = new JLabel("–");

    // Przyciski akcji
    private final JButton btnCalc;
    private final JButton btnReset;
    private final JButton btnReserve;

    public DetailsPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Szczegóły i rezerwacja"));

        // --- Lista pojazdów ---
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer((JList<? extends Vehicle> l, Vehicle v, int idx, boolean sel, boolean foc) -> {
            JLabel lbl = new JLabel(v.getBrand() + " " + v.getModel());
            lbl.setOpaque(sel);
            if (sel) lbl.setBackground(l.getSelectionBackground());
            return lbl;
        });
        list.addListSelectionListener(this::onSelectVehicle);
        JScrollPane listScroll = new JScrollPane(list);

        // --- Panel szczegółów i rezerwacji ---
        JPanel detailsAndBooking = new JPanel(new BorderLayout());

        // 1) Dane pojazdu
        JPanel info = new JPanel(new GridLayout(0,2,5,5));
        info.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        info.add(new JLabel("Pojazd:"));    info.add(lblBrandModel);
        info.add(new JLabel("Status:"));    info.add(lblStatus);
        info.add(new JLabel("Rok:"));       info.add(lblYear);
        info.add(new JLabel("Kolor:"));     info.add(lblColor);
        info.add(new JLabel("Nr seryjny/Tablica:")); info.add(lblSerialOrPlate);
        info.add(new JLabel("Cena za godzinę:"));    info.add(lblPricePerHour);
        detailsAndBooking.add(info, BorderLayout.NORTH);

        // 2) Panel dat i zniżki
        inputDatesPanel = new JPanel(new GridLayout(3,2,5,5));
        inputDatesPanel.setBorder(BorderFactory.createTitledBorder("Okres wypożyczenia i zniżka"));
        spinnerFrom     = createDateSpinner();
        spinnerTo       = createDateSpinner();
        spinnerDiscount = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));

        inputDatesPanel.add(new JLabel("Data od:"));    inputDatesPanel.add(spinnerFrom);
        inputDatesPanel.add(new JLabel("Data do:"));    inputDatesPanel.add(spinnerTo);
        inputDatesPanel.add(new JLabel("Zniżka [%]:")); inputDatesPanel.add(spinnerDiscount);

        labelDatesPanel = new JPanel(new GridLayout(2,2,5,5));
        labelDatesPanel.setBorder(BorderFactory.createTitledBorder("Okres wypożyczenia"));
        lblFromValue = new JLabel("–");
        lblToValue   = new JLabel("–");
        labelDatesPanel.add(new JLabel("Data od:")); labelDatesPanel.add(lblFromValue);
        labelDatesPanel.add(new JLabel("Data do:")); labelDatesPanel.add(lblToValue);

        cardDates = new JPanel(new CardLayout());
        cardDates.add(inputDatesPanel, "INPUT");
        cardDates.add(labelDatesPanel, "LABEL");
        detailsAndBooking.add(cardDates, BorderLayout.CENTER);

        // 3) Obliczanie ceny i przyciski
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        btnCalc    = new JButton("Oblicz cenę");
        btnReset   = new JButton("Resetuj");
        btnReserve = new JButton("Rezerwuj");

        btnCalc.addActionListener(e -> calculateTotal());
        btnReset.addActionListener(e -> resetSelection());
        btnReserve.addActionListener(e -> handleReserveOrCancel());
        btnReserve.setEnabled(false);

        bottom.add(btnCalc);
        bottom.add(btnReset);
        bottom.add(btnReserve);
        bottom.add(new JLabel("Cena łącznie:"));
        bottom.add(lblTotalPrice);
        detailsAndBooking.add(bottom, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                listScroll,
                detailsAndBooking
        );
        split.setResizeWeight(0.5);
        split.setOneTouchExpandable(true);

        add(split, BorderLayout.CENTER);
    }

    public void showVehicles(List<Vehicle> vehicles) {
        model.clear();
        vehicles.forEach(model::addElement);
        if (!vehicles.isEmpty()) list.setSelectedIndex(0);
    }

    private void onSelectVehicle(ListSelectionEvent ev) {
        if (ev.getValueIsAdjusting()) return;
        Vehicle v = list.getSelectedValue();
        if (v == null) return;

        boolean isRented      = "RENTED".equalsIgnoreCase(v.getStatus());
        boolean isMaintenance = "MAINTENANCE".equalsIgnoreCase(v.getStatus());

        lblBrandModel.setText(v.getBrand() + " " + v.getModel());
        lblYear      .setText(v.getYear() != 0 ? String.valueOf(v.getYear()) : "–");
        lblColor     .setText(v.getColor() != null ? v.getColor() : "–");

        if (isRented) {
            lblStatus.setText("Wypożyczony");
            btnReserve.setText("Anuluj");
        } else if (isMaintenance) {
            lblStatus.setText("Serwisowany");
            btnReserve.setText("Rezerwuj");
        } else {
            lblStatus.setText("Dostępny");
            btnReserve.setText("Rezerwuj");
        }

        String serialOrPlate = "–";
        if (v instanceof Bike)    serialOrPlate = ((Bike)v).getSerialNumber();
        if (v instanceof Scooter) serialOrPlate = ((Scooter)v).getSerialNumber();
        if (v instanceof Car)     serialOrPlate = ((Car)v).getLicensePlate();
        lblSerialOrPlate.setText(serialOrPlate);

        lblPricePerHour.setText(String.format("%.2f zł", v.getPricePerHour()));

        CardLayout cl = (CardLayout)cardDates.getLayout();
        if (isRented) {
            cl.show(cardDates, "LABEL");
            lblFromValue.setText(String.format("%tF", v.getRentedFrom()));
            lblToValue  .setText(String.format("%tF", v.getRentedTo()));

            spinnerFrom.setEnabled(false);
            spinnerTo.setEnabled(false);
            spinnerDiscount.setEnabled(false);
            btnCalc.setEnabled(false);
            btnReserve.setEnabled(true);
            btnReset.setEnabled(false);

            double total = v.calculatePrice(
                    v.getRentedFrom(),
                    v.getRentedTo(),
                    v.getDiscountPercent()
            );
            lblTotalPrice.setText(String.format("%.2f zł", total));

        } else if(isMaintenance){
            cl.show(cardDates, "INPUT");
            spinnerFrom.setEnabled(true);
            spinnerTo.setEnabled(true);
            spinnerDiscount.setEnabled(true);
            btnCalc.setEnabled(true);
            btnReserve.setEnabled(false);
            btnReset.setEnabled(true);
            lblTotalPrice.setText("–");
        } else{
            cl.show(cardDates, "INPUT");
            spinnerFrom.setEnabled(true);
            spinnerTo.setEnabled(true);
            spinnerDiscount.setEnabled(true);
            btnCalc.setEnabled(true);
            btnReserve.setEnabled(true);
            btnReset.setEnabled(true);
            lblTotalPrice.setText("–");
        }
    }

    private void handleReserveOrCancel() {
        Vehicle v = list.getSelectedValue();
        if (v == null) return;
        boolean isRented = "RENTED".equalsIgnoreCase(v.getStatus());

        if (isRented) {
            if (!VehicleRepository.cancelRental(v)) {
                JOptionPane.showMessageDialog(this,
                        "Nie udało się anulować wypożyczenia.",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Wypożyczenie zostało anulowane.",
                    "Anuluj", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Date from     = (Date)spinnerFrom.getValue();
            Date to       = (Date)spinnerTo.getValue();
            double discount = (Double)spinnerDiscount.getValue();

            if (to.before(from)) {
                JOptionPane.showMessageDialog(this,
                        "Data zakończenia musi być po dacie rozpoczęcia!",
                        "Błąd dat", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!VehicleRepository.rentVehicle(v, from, to, discount)) {
                JOptionPane.showMessageDialog(this,
                        "Nie udało się zarezerwować pojazdu.",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Pojazd został pomyślnie zarezerwowany.",
                    "Rezerwacja", JOptionPane.INFORMATION_MESSAGE);
        }

        model.removeElement(v);
        resetSelection();
    }

    private void calculateTotal() {
        Vehicle v = list.getSelectedValue();
        if (v == null) return;
        Date from     = (Date)spinnerFrom.getValue();
        Date to       = (Date)spinnerTo.getValue();
        double discount = (Double)spinnerDiscount.getValue();

        double total = v.calculatePrice(from, to, discount);
        lblTotalPrice.setText(String.format("%.2f zł", total));
    }

    private void resetSelection() {
        list.clearSelection();
        lblBrandModel.setText("–");
        lblYear.setText("–");
        lblColor.setText("–");
        lblSerialOrPlate.setText("–");
        lblPricePerHour.setText("–");
        lblStatus.setText("–");
        lblTotalPrice.setText("–");

        // Reset dat i zniżki:
        spinnerFrom.setValue(new Date());
        spinnerTo.setValue(new Date());
        spinnerDiscount.setValue(0.0);

        ((CardLayout)cardDates.getLayout()).show(cardDates, "INPUT");
        spinnerFrom.setEnabled(true);
        spinnerTo.setEnabled(true);
        spinnerDiscount.setEnabled(true);
        btnCalc.setEnabled(true);
        btnReserve.setEnabled(false);
    }

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null,
                java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }
}
