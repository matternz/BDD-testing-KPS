package kps.client.ui;

import kps.client.Client;
import kps.client.ClientNotifiable;
import kps.server.BusinessFigures;
import kps.server.CustomerRoute;
import kps.server.Destination;
import kps.server.TransportRoute;
import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by brownhami1 on 9/06/17.
 */
public class UI implements ClientNotifiable {
    private static final Color SELECTOR_BACKGROUND_COLOR = Color.WHITE;
    private static final Color VIEW_BACKGROUND_COLOR = Color.WHITE;
    static final Color BORDER_COLOR = Color.BLACK;

    protected FormSelector formSelector;
    FormView formView;
    JFrame window;
    private JMenuBar menuBar;

    protected Client client;

    Set<Destination> destinations;
    Set<TransportRoute> transportRoutes;
    Set<CustomerRoute> customerRoutes;
    BusinessFigures businessFigures;

    public LogItem[] logs;

    public UI(Client client) {


        this.client = client;

        transportRoutes = new HashSet<>();
        customerRoutes = new HashSet<>();
        businessFigures = new BusinessFigures();

        window = new JFrame("KPSmart");
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildMenuBar();
        window.setJMenuBar(menuBar);

        formView = new FormView(this);
        formSelector = new FormSelector(this);

        window.getContentPane().add(formView, BorderLayout.CENTER);
        window.getContentPane().add(formSelector, BorderLayout.WEST);

        window.pack();
        window.setVisible(true);
    }


    private void buildMenuBar() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
        menuBar.add(fileMenu);
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setMnemonic(KeyEvent.VK_Q);
        quitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(quitItem);
    }

    @Override
    public void postTransportRoutes(Set<TransportRoute> transportRoutes) {
        this.transportRoutes = transportRoutes;
        window.repaint();
    }

    @Override
    public void postCustomerRoutes(Set<CustomerRoute> customerRoutes) {
        this.customerRoutes = customerRoutes;
        window.repaint();
    }

    @Override
    public void postBusinessFigures(BusinessFigures businessFigures) {
        this.businessFigures = businessFigures;

        formView.updateBusinessFigures();
    }

    @Override
    public void postInformationMessage(String message) {
        JOptionPane.showMessageDialog(window, "Message from server: " + message);
    }

    @Override
    public void postDestinations(Set<Destination> destinations) {
        this.destinations = destinations;
    }

    @Override
    public void postLogRangeStop() {
System.err.println("STOP");
        formView.setBFIndex(-1);
        formView.updateBusinessFigures();
    }

    @Override
    public void postRole(Role role) {
        formSelector.setLoggedIn(role);
    }

    @Override
    public void postLogItems(LogItem[] logItems) {
System.err.println("Log items");
        this.logs = logItems;
        window.repaint();
    }


}
