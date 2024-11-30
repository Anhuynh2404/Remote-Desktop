package gui;

import common.CommonBus;
import javax.swing.*;
import java.awt.*;

public class ClientPanel extends JPanel {

    private JTextField clientIpField;
    private JTextField clientPortField;
    private JTextField clientPasswordField;

    private CommonBus common_bus;

    public ClientPanel(CommonBus common_bus) {
        this.common_bus = common_bus;
        initializeUI();
    }

    public void initializeUI() {
        setLayout(new GridLayout(5, 1, 10, 10));
        setBackground(new Color(240, 248, 255)); // Light blue background

        JLabel clientTitle = new JLabel("Connect To Server", JLabel.CENTER);
        clientTitle.setFont(new Font("Arial", Font.BOLD, 18));
        clientTitle.setForeground(new Color(0, 102, 204)); // Dark blue for title
        add(clientTitle);

        // Hàng Địa Chỉ IP Máy Chủ
        JPanel clientIpRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clientIpRow.setBackground(new Color(240, 248, 255));
        clientIpRow.add(new JLabel("Remote IP:"));
        clientIpField = new JTextField(10);
        clientIpField.setText("192.168.1.7");
        clientIpRow.add(clientIpField);
        add(clientIpRow);

        // Hàng Cổng Máy Chủ
        JPanel clientPortRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clientPortRow.setBackground(new Color(240, 248, 255));
        clientPortRow.add(new JLabel("Remote Port:"));
        clientPortField = new JTextField(10);
        clientPortField.setText("1111");
        clientPortRow.add(clientPortField);
        add(clientPortRow);

        // Hàng Mật Khẩu
        JPanel clientPasswordRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clientPasswordRow.setBackground(new Color(240, 248, 255));
        clientPasswordRow.add(new JLabel("Password:"));
        clientPasswordField = new JTextField(10);
        clientPasswordField.setText("12345");
        clientPasswordRow.add(clientPasswordField);
        add(clientPasswordRow);

        // Hàng Nút Kết Nối
        JPanel clientButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        clientButtonRow.setBackground(new Color(240, 248, 255));
        JButton connectButton = new JButton("Connect Now");
        connectButton.setBackground(new Color(34, 139, 34)); // Forest green
        connectButton.setFocusPainted(false);
        clientButtonRow.add(connectButton);
        add(clientButtonRow);

        // Thêm hành động cho nút Kết Nối
        connectButton.addActionListener(e -> {
            this.setEnabled(false);

            Thread connect_thread = new Thread(() -> {
                try {
                    String host = clientIpField.getText().trim();
                    int port = Integer.parseInt(clientPortField.getText().trim());
                    String password = clientPasswordField.getText().trim();

                    // Kiểm tra định dạng IPv4
                    if (!this.isFormatIpv4(host)) throw new Exception("Incorrect IPV4 format");

                    common_bus.startConnectingToServer(host, port, password);

                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(this, "Unable to connect to server.\n" + exception.getMessage());
                }
                this.setEnabled(true);
            });
            connect_thread.setDaemon(true);
            connect_thread.start();
        });
    }

    private boolean isFormatIpv4(String host) {
        int count = 0;
        for (int i = 0; i < host.length(); ++i) {
            if (host.charAt(i) == '.') ++count;
        }
        return count == 3 || count == 0;
    }
}
