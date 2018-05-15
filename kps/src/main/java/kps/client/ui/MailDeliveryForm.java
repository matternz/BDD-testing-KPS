package kps.client.ui;

import kps.server.Destination;
import kps.util.MailPriority;
import kps.util.StringStuff;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;


class MailDeliveryForm extends JPanel {
    //GridLayout will resize all cells to be as wide as the widest cell, so only one width needs to be set
    private JComboBox<String> dayField;
    private JComboBox<String> toField;
    private JComboBox<String> fromField;
    private JTextField weightField = new JTextField();
    private JTextField volumeField = new JTextField();
    private JComboBox<String> priorityField;

    private JButton sendButton = new JButton("Send");

    UI ui;
    
    MailDeliveryForm(UI ui) {
        this.ui = ui;

        String[] days = StringStuff.enumValuesToHumanReadable(DayOfWeek.values());
        String[] priorities = StringStuff.enumValuesToHumanReadable(MailPriority.values());
        dayField = new JComboBox<>(days);
        priorityField = new JComboBox<>(priorities);
        toField = new JComboBox<>();
        fromField = new JComboBox<>();
        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("Day: "));
        this.add(dayField);
        this.add(new JLabel("To: "));
        this.add(toField);
        this.add(new JLabel("From: "));
        this.add(fromField);
        this.add(new JLabel("Weight: "));
        this.add(weightField);
        this.add(new JLabel("Volume: "));
        this.add(volumeField);
        this.add(new JLabel("Priority: "));
        this.add(priorityField);
        //TODO implement sending
        sendButton.addActionListener(e->this.sendLog());
        this.add(sendButton);
    }

    private void sendLog() {
        ui.client.sendMailDelivery(
                (String)dayField.getSelectedItem(),
                toField.getSelectedItem().toString(),
                fromField.getSelectedItem().toString(),
                weightField.getText(),
                volumeField.getText(),
                (String)priorityField.getSelectedItem()
                );

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
