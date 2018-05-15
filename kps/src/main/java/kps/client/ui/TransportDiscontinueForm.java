package kps.client.ui;

import kps.server.Destination;
import kps.util.MailPriority;
import kps.util.StringStuff;

import javax.swing.*;
import java.awt.*;


public class TransportDiscontinueForm extends JPanel{
    UI ui;
    private JTextField companyField = new JTextField("", 10);
    private JComboBox<String> toField;
    private JComboBox<String> fromField;
    private JComboBox<String> typeField;

    private JButton sendButton = new JButton("Discontinue Transport Route");

    TransportDiscontinueForm(UI ui) {
        this.ui = ui;
        String[] priorities = StringStuff.enumValuesToHumanReadable(MailPriority.values());
        typeField = new JComboBox<>(priorities);
        toField = new JComboBox<>();
        fromField = new JComboBox<>();

        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("Company: "));
        this.add(companyField);
        this.add(new JLabel("To: "));
        this.add(toField);
        this.add(new JLabel("From: "));
        this.add(fromField);
        this.add(new JLabel("Type: "));
        this.add(typeField);

        sendButton.addActionListener(e->ui.client.sendTransportDiscontinue(
                companyField.getText(),
                (String)toField.getSelectedItem(),
                (String)fromField.getSelectedItem(),
                (String)typeField.getSelectedItem()
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
