package gui;

import common.CommonBus;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.util.Objects;
import java.util.Vector;

public class ServerPanel extends JPanel implements Runnable {

    private JComboBox<String> serverIpComboBox;
    private JTextField serverPortField;
    private JTextField serverPasswordField;
    private JLabel statusLabel;
    private JButton startListeningButton;
    private JButton stopListeningButton;

    private CommonBus common_bus;
    private Thread listen_thread;

    public ServerPanel(CommonBus common_bus) {
        this.common_bus = common_bus;
        initializeUI();
    }

    public void initializeUI() {
        setLayout(new GridLayout(6, 1, 10, 10));
        setBackground(new Color(240, 248, 255)); // Light blue background

        JLabel serverTitle = new JLabel("Server Listening", JLabel.CENTER);
        serverTitle.setFont(new Font("Arial", Font.BOLD, 18));
        serverTitle.setForeground(new Color(0, 102, 204)); // Darker blue for title
        add(serverTitle);

        // Hàng IP
        JPanel serverIpRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        serverIpRow.setBackground(new Color(240, 248, 255));
        serverIpRow.add(new JLabel("Server IP:"));
        serverIpComboBox = new JComboBox<>();
        try {
            Vector<String> ipv4_addresses = common_bus.getTcpServer().getAllIpv4AddressesOnLocal();
            for (String ipv4 : ipv4_addresses) {
                serverIpComboBox.addItem(ipv4);
            }
        } catch (SocketException exception) {
            exception.printStackTrace();
        }
        serverIpRow.add(serverIpComboBox);
        add(serverIpRow);

        // Hàng Port
        JPanel serverPortRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        serverPortRow.setBackground(new Color(240, 248, 255));
        serverPortRow.add(new JLabel("Server Port:"));
        serverPortField = new JTextField(10);
        serverPortField.setText("1111");
        serverPortRow.add(serverPortField);
        add(serverPortRow);

        // Hàng Mật Khẩu
        JPanel serverPasswordRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        serverPasswordRow.setBackground(new Color(240, 248, 255));
        serverPasswordRow.add(new JLabel("Set Password:"));
        serverPasswordField = new JTextField(10);
        serverPasswordField.setText("12345");
        serverPasswordRow.add(serverPasswordField);
        add(serverPasswordRow);

        // Hàng Trạng Thái
        JPanel serverStatusRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        serverStatusRow.setBackground(new Color(240, 248, 255));
        statusLabel = new JLabel("Status: Stopped");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(Color.RED);
        serverStatusRow.add(statusLabel);
        add(serverStatusRow);

        // Hàng Nút Bấm
        JPanel serverButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startListeningButton = new JButton("Start Listening");
        startListeningButton.setBackground(Color.GREEN); // Forest green
//        startListeningButton.setForeground(Color.WHITE);
        startListeningButton.setFocusPainted(false);

        stopListeningButton = new JButton("Stop Listening");
        stopListeningButton.setBackground(Color.RED); // Firebrick
//        stopListeningButton.setForeground(Color.WHITE);
        stopListeningButton.setFocusPainted(false);
        stopListeningButton.setEnabled(false);

        serverButtonRow.add(startListeningButton);
        serverButtonRow.add(stopListeningButton);
        add(serverButtonRow);

        // Thêm hành động cho nút Bắt Đầu Lắng Nghe
        startListeningButton.addActionListener(e -> startListening());

        // Thêm hành động cho nút Dừng Lắng Nghe
        stopListeningButton.addActionListener(e -> stopListening());
    }

    private void startListening() {
        try {
            String host = Objects.requireNonNull(serverIpComboBox.getSelectedItem()).toString().trim();
            int port = Integer.parseInt(serverPortField.getText().trim());
            String password = serverPasswordField.getText().trim();
            common_bus.startListeningOnServer(host, port, password);

            listen_thread = new Thread(this);
            listen_thread.setDaemon(true);
            listen_thread.start();

            startListeningButton.setEnabled(false);
            stopListeningButton.setEnabled(true);
            statusLabel.setText("Status: Listening...");
            statusLabel.setForeground(Color.GREEN);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to start server.\n" + ex.getMessage());
        }
    }

    private void stopListening() {
        try {
            common_bus.stopListeningOnServer();
            stopListeningButton.setEnabled(false);
            startListeningButton.setEnabled(true);
            statusLabel.setText("Status: Stopped");
            statusLabel.setForeground(Color.RED);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to stop server.\n" + ex.getMessage());
        }
    }

    @Override
    public void run() {
        // Implement run as before
        Thread tcpThread = new Thread(() -> {
            while (common_bus.getTcpServer().isListening()) {
                try {
                    common_bus.getTcpServer().waitingConnectionFromClient();
                } catch (Exception e) {
                    System.err.println("Error TCP: " + e.getMessage());
                }
            }
        });

        Thread chatThread = new Thread(() -> {
            while (common_bus.getChatServer().isListening()) {
                try {
                    common_bus.getChatServer().waitingConnectionFromClient();
                } catch (Exception e) {
                    System.err.println("Error Chat: " + e.getMessage());
                }
            }
        });

        tcpThread.start();
        chatThread.start();
    }
}
