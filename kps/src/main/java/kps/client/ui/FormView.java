package kps.client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;


class FormView extends JPanel {
    private UI ui;
    private String mode = "none";


    private BusinessFiguresView bfView;
    private MailDeliveryForm mdForm;
    private TransportCostUpdateForm tcuForm;
    private CustomerPriceUpdateForm cpuForm;
    private TransportDiscontinueForm tdForm;

    FormView(UI ui) {
        Border border;
        this.ui = ui;
        this.setLayout(new FlowLayout(FlowLayout.LEADING));
        this.setPreferredSize(new Dimension(500, 500));
        border = BorderFactory.createMatteBorder(0, 2, 0, 0, UI.BORDER_COLOR);
        this.setBorder(border);

        bfView = new BusinessFiguresView(ui);
        this.add(bfView);
        bfView.setVisible(false);

        mdForm = new MailDeliveryForm(ui);
        this.add(mdForm);
        mdForm.setVisible(false);

        tcuForm = new TransportCostUpdateForm(ui);
        this.add(tcuForm);
        tcuForm.setVisible(false);

        cpuForm = new CustomerPriceUpdateForm(ui);
        this.add(cpuForm);
        cpuForm.setVisible(false);

        tdForm = new TransportDiscontinueForm(ui);
        this.add(tdForm);
        tdForm.setVisible(false);
    }

    void showBusinessFiguresForm() {
        //this.mode = "businessFigures";

        if (ui.formSelector.isLoggedIn()) {
            try {
                ui.client.requestBusinessFigures();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to request business figures.");
            }
            hideAllForms();
            bfView.setVisible(true);
            this.revalidate();
        }
    }

    void showMailDeliveryForm() {
        hideAllForms();
        if (ui.formSelector.isLoggedIn()) {
            mdForm.updateDestinations();
            mdForm.setVisible(true);
        }
    }

    void showTransportCostUpdateForm() {
        hideAllForms();
        if (ui.formSelector.isLoggedIn()) {
            tcuForm.updateDestinations();
            tcuForm.setVisible(true);
            this.revalidate();
        }
    }

    void showCustomerPriceUpdateForm() {
        hideAllForms();
        if (ui.formSelector.isLoggedIn()) {
            cpuForm.updateDestinations();
            cpuForm.setVisible(true);
            this.revalidate();
        }
    }

    void showTransportDiscontinueForm() {
        hideAllForms();
        if (ui.formSelector.isLoggedIn()) {
            tdForm.updateDestinations();
            tdForm.setVisible(true);
            this.revalidate();
        }
    }

    private void hideAllForms() {
        bfView.setVisible(false);
        mdForm.setVisible(false);
        tcuForm.setVisible(false);
        cpuForm.setVisible(false);
        tdForm.setVisible(false);
    }

    void updateBusinessFigures() {
        bfView.update();
    }

    synchronized public int getBFIndex() { return bfView.index; }
    synchronized public void setBFIndex(int i) { bfView.index = i; }
}
