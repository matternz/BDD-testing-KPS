package kps.client.ui;

import kps.server.Destination;
import kps.util.MailPriority;
import kps.util.RouteType;
import kps.util.StringStuff;

import java.time.DayOfWeek;
import javax.swing.*;
import java.awt.*;


public class TransportCostUpdateForm extends JPanel{
    UI ui;
    private JTextField companyField = new JTextField("", 10);
    private JComboBox<String> toField;
    private JComboBox<String> fromField;
    private JComboBox<String> typeField;
    private JTextField weightCostField = new JTextField();
    private JTextField volumeCostField = new JTextField();
    private JTextField maxWeightField = new JTextField();
    private JTextField maxVolumeField = new JTextField();
    private JTextField durationField = new JTextField();
    private JTextField frequencyField = new JTextField();
    private JComboBox<String> dayField;

    private JButton sendButton = new JButton("Update Transport Route");

    TransportCostUpdateForm(UI ui) {
        this.ui = ui;
        String[] days = StringStuff.enumValuesToHumanReadable(DayOfWeek.values());
        String[] priorities = StringStuff.enumValuesToHumanReadable(RouteType.values());
        dayField = new JComboBox<>(days);
        typeField = new JComboBox<>(priorities);
        toField = new JComboBox<>();
        fromField = new JComboBox<>();
        toField.setEditable(true);
        fromField.setEditable(true);

        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("Company: "));
        this.add(companyField);
        this.add(new JLabel("To: "));
        this.add(toField);
        this.add(new JLabel("From: "));
        this.add(fromField);
        this.add(new JLabel("Type: "));
        this.add(typeField);
        this.add(new JLabel("Weight Cost: "));
        this.add(weightCostField);
        this.add(new JLabel("Volume Cost: "));
        this.add(volumeCostField);
        this.add(new JLabel("Max Weight: "));
        this.add(maxWeightField);
        this.add(new JLabel("Max Volume: "));
        this.add(maxVolumeField);
        this.add(new JLabel("Duration: "));
        this.add(durationField);
        this.add(new JLabel("Frequency: "));
        this.add(frequencyField);
        this.add(new JLabel("Day: "));
        this.add(dayField);
        //TODO implement sending
        sendButton.addActionListener(e->ui.client.sendTransportCostUpdate(
                companyField.getText(),
                (String)toField.getSelectedItem(),
                (String)fromField.getSelectedItem(),
                (String)typeField.getSelectedItem(),
                weightCostField.getText(),
                volumeCostField.getText(),
                maxWeightField.getText(),
                maxVolumeField.getText(),
                durationField.getText(),
                frequencyField.getText(),
                (String)dayField.getSelectedItem()
        ));
        this.add(sendButton);
    }

    public void updateDestinations() {
        String[] destinations = StringStuff.enumValuesToHumanReadable(ui.destinations.toArray());
        toField.removeAllItems();
        fromField.removeAllItems();
        for (String destination : destinations) {
            destination = destination.toUpperCase();
            fromField.addItem(destination);
            toField.addItem(destination);
        }
    }
}

