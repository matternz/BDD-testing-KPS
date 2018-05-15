package kps.client.ui;

import kps.server.CustomerRoute;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


class BusinessFiguresView extends JPanel {
    private UI ui;
    private JPanel mainGrid;
    private JPanel largeGrid;
    private JTextArea descriptionLabel;
    private JLabel countValue;
    private JLabel mailCountValue;
    private JLabel volumeValue;
    private JLabel weightValue;
    private JLabel dayValue;
    private JLabel expValue;
    private JLabel revValue;

    private JTextArea criticalRoutes;

    public int index = -1; // -1 means we're at the current figures

    BusinessFiguresView(UI ui) {
        this.ui = ui;
        mainGrid = new JPanel();
        largeGrid = new JPanel();
        descriptionLabel = new JTextArea(2,2);
        descriptionLabel.setWrapStyleWord (true);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setEditable(false);
        mailCountValue = new JLabel();
        countValue = new JLabel();
        volumeValue = new JLabel();
        weightValue = new JLabel();
        dayValue = new JLabel();
        expValue = new JLabel();
        revValue = new JLabel();
        criticalRoutes = new JTextArea();
        criticalRoutes.setEditable(false);
        update();
        mainGrid.setLayout(new GridLayout(7, 2, 10, 0));
        mainGrid.add(new JLabel("Total event count: "));
        mainGrid.add(countValue);
        mainGrid.add(new JLabel("Total mail count: "));
        mainGrid.add(mailCountValue);
        mainGrid.add(new JLabel("Total volume sent: "));
        mainGrid.add(volumeValue);
        mainGrid.add(new JLabel("Total weight sent: "));
        mainGrid.add(weightValue);
        mainGrid.add(new JLabel("Average delivery days: "));
        mainGrid.add(dayValue);
        mainGrid.add(new JLabel("Expenditure: "));
        mainGrid.add(expValue);
        mainGrid.add(new JLabel("Revenue: "));
        mainGrid.add(revValue);

        largeGrid.setLayout(new GridLayout(2,1));
        largeGrid.add(new JLabel("Critical Routes:"));
        largeGrid.add(criticalRoutes);

        this.setLayout(new GridLayout(3,1));
        this.add(descriptionLabel);
        this.add(mainGrid);
        this.add(largeGrid);
    }

    void update() {
        if (ui.logs != null && ui.logs.length != 0) {
            int tmpIndex = index == -1 ? ui.logs.length-1 : index;
            this.descriptionLabel.setText("Last log: "+ui.logs[tmpIndex].toString());
            countValue.setText(Integer.toString(tmpIndex+1));
        } else {
            countValue.setText("0");
        }
        mailCountValue.setText(Long.toString(ui.businessFigures.getMailCount()));
        volumeValue.setText(Double.toString(ui.businessFigures.getTotalVolume()));
        weightValue.setText(Double.toString(ui.businessFigures.getTotalWeight()));
        dayValue.setText(Double.toString(ui.businessFigures.getAverageDeliveryDays()));
        expValue.setText(Double.toString(ui.businessFigures.getExpenditure()));
        revValue.setText(Double.toString(ui.businessFigures.getRevenue()));

        criticalRoutes.setText("");
        for (Map.Entry<CustomerRoute, Double> critical
             : ui.businessFigures.getCriticalRoutes().entrySet()) {
            CustomerRoute route = critical.getKey();
            double profit = critical.getValue();

            criticalRoutes.append(route.from + " --> " + route.to + ": loses "
                    + (-profit) + " per delivery on average.\n");
        }
    }
}
