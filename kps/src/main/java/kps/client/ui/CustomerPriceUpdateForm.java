package kps.client.ui;

import kps.server.Destination;
import kps.util.MailPriority;
import kps.util.StringStuff;

import javax.swing.*;
import java.awt.*;

/**
 * Created by The Gibbon on 16/06/2017.
 */
public class CustomerPriceUpdateForm extends JPanel {
    UI ui;
    private JComboBox<String> toField;
    private JComboBox<String> fromField;
    private JComboBox<String> priorityField;
    private JTextField weightCostField = new JTextField();
    private JTextField volumeCostField = new JTextField();


    private JButton sendButton = new JButton("Update Customer Price");

    CustomerPriceUpdateForm(UI ui) {
        this.ui = ui;
        String[] priorities = StringStuff.enumValuesToHumanReadable(MailPriority.values());
        priorityField = new JComboBox<>(priorities);
        toField = new JComboBox<>();
        fromField = new JComboBox<>();

        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("To: "));
        this.add(toField);
        this.add(new JLabel("From: "));
        this.add(fromField);
        this.add(new JLabel("Priority: "));
        this.add(priorityField);
        this.add(new JLabel("Weight Cost: "));
        this.add(weightCostField);
        this.add(new JLabel("Volume Cost: "));
        this.add(volumeCostField);


        //TODO implement sending
        sendButton.addActionListener(e -> ui.client.sendCustomerPriceUpdate(
                (String)toField.getSelectedItem(),
                (String)fromField.getSelectedItem(),
                (String)priorityField.getSelectedItem(),
                weightCostField.getText(),
                volumeCostField.getText()
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
