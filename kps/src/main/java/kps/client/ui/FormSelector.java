package kps.client.ui;

import kps.server.BusinessFigures;
import kps.server.UserRecord;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;


class FormSelector extends JPanel {
    private static final long serialVersionUID = 1L;
    private UI ui;
    private JTextField username;
    private JPasswordField password;

    private JFormattedTextField startDate;
    private JFormattedTextField endDate;

    private JPanel authPanel;
    private JPanel dateRangePanel;

    private boolean loggedIn = false;

    FormSelector(UI ui) {
        this.ui = ui;

        JButton viewBusinessFiguresButton;
        JButton mailDeliveryFormButton;
        JButton transportCostUpdateFormButton;
        JButton customerPriceUpdateFormButton;
        JButton transportDiscontinueFormButton;
        JButton authButton;
        JPanel buttonPanel;

        Border border;

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0,1));

        setLayout(new BorderLayout());

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, 1));

        authPanel = new JPanel(new BorderLayout());
        JPanel authEntryPanel = new JPanel(new GridLayout(2, 2));

        JLabel usernameLabel = new JLabel("Username");
        username = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        password = new JPasswordField();

        authEntryPanel.add(usernameLabel);
        authEntryPanel.add(username);
        authEntryPanel.add(passwordLabel);
        authEntryPanel.add(password);

        authButton = new JButton("Authenticate");
        authButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ui.client.requestAuthentication(new UserRecord(getUsername(), getPassword(), null));
                } catch (IOException e1) {
                    ui.postInformationMessage(e1.getMessage());
                }
            }
        });
        authPanel.add(authEntryPanel, BorderLayout.NORTH);
        authPanel.add(authButton, BorderLayout.SOUTH);

        JPanel logBrowsePanel = new JPanel();

        JButton logBackButton = new JButton("< Back");
        JButton logForwardButton = new JButton("Forward >");
        JButton resetButton = new JButton("Reset >>");

        logBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = ui.formView.getBFIndex();
                if (index == 0) {
                    return;
                }
                if (index == -1) {
                    ui.formView.setBFIndex(ui.logs.length - 2);
                } else {
                    ui.formView.setBFIndex(index-1);
                }
                try {
                    ui.client.requestLogsRange(ui.formView.getBFIndex());
                } catch (IOException e1) {
                    ui.postInformationMessage(e1.getMessage());
                }
            }
        });

        logForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = ui.formView.getBFIndex();
                if (index == -1) {
                    // We don't do anything here; we can't go into the future
                } else {
                    int limit = ui.logs.length - 1;
                    if (index >= limit) {
                        // We've hit the end
                        ui.formView.setBFIndex(-1);
                        try {
                            ui.client.requestLogRangeStop();
                        } catch (IOException e1) {
                            ui.postInformationMessage(e1.getMessage());
                        }
                    } else {
                        try {
                            ui.formView.setBFIndex(index+1);
                            ui.client.requestLogsRange(index+1);
                        } catch (IOException e1) {
                            ui.postInformationMessage(e1.getMessage());
                        }
                    }
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui.formView.setBFIndex(-1);
                try {
                    ui.client.requestLogRangeStop();
                } catch (IOException e1) {
                    ui.postInformationMessage(e1.getMessage());
                }
            }
        });

        logBrowsePanel.add(logBackButton);
        logBrowsePanel.add(logForwardButton);
        logBrowsePanel.add(resetButton);

        viewBusinessFiguresButton = new JButton("View Business Figures");
        viewBusinessFiguresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedIn) {
                    ui.transportRoutes = new HashSet<>();
                    ui.customerRoutes = new HashSet<>();
                    ui.businessFigures = new BusinessFigures();

                    ui.formView.showBusinessFiguresForm();
                }
            }
        });

        mailDeliveryFormButton = new JButton("Mail Delivery Form");
        mailDeliveryFormButton.addActionListener(e -> ui.formView.showMailDeliveryForm());

        transportCostUpdateFormButton = new JButton("Transport Cost Update");
        transportCostUpdateFormButton.addActionListener(e -> ui.formView.showTransportCostUpdateForm());

        customerPriceUpdateFormButton = new JButton("Customer Price Update");
        customerPriceUpdateFormButton.addActionListener(e -> ui.formView.showCustomerPriceUpdateForm());

        transportDiscontinueFormButton = new JButton("Transport Discontinue");
        transportDiscontinueFormButton.addActionListener(e -> ui.formView.showTransportDiscontinueForm());

        border = BorderFactory.createMatteBorder(0, 0, 0, 2, ui.BORDER_COLOR);

        entryPanel.add(authPanel);
        entryPanel.add(logBrowsePanel);

        this.add(entryPanel, BorderLayout.NORTH);
        this.add(buttonPanel,BorderLayout.SOUTH);

        buttonPanel.add(viewBusinessFiguresButton);
        buttonPanel.add(mailDeliveryFormButton);
        buttonPanel.add(transportCostUpdateFormButton);
        buttonPanel.add(customerPriceUpdateFormButton);
        buttonPanel.add(transportDiscontinueFormButton);

        this.setBorder(border);
    }

    /**
     * Removes the authentication panel and replaces it with the name of the user
     */
    synchronized void setLoggedIn(UserRecord.Role role) {
        authPanel.removeAll();

        Font authFont = new Font("Arial", Font.BOLD, 15);
        JLabel userLabel = new JLabel(username.getText(), JLabel.CENTER);
        userLabel.setFont(authFont);
        JLabel privLabel = new JLabel(role == UserRecord.Role.MANAGER ? "(MANAGER)" : "(CLERK)", JLabel.CENTER);
        privLabel.setFont(authFont);
        authPanel.add(userLabel, BorderLayout.NORTH);
        authPanel.add(privLabel, BorderLayout.SOUTH);

        authPanel.revalidate();

        repaint();
        loggedIn = true;
    }

    synchronized boolean isLoggedIn() {
        return this.loggedIn;
    }

    String getUsername() {
        return username.getText();
    }

    String getPassword() {
        return password.getText();
    }
}
