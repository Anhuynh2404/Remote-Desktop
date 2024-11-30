package gui;

import common.CommonBus;

import java.util.Objects;

import javax.swing.*;

public class MainFrame extends JFrame {

    private CommonBus common_bus;

    private ServerPanel serverPanel;
    private ClientPanel clientPanel;
    private MainChatPanel mainChatPanel;

    public MainFrame() {
        common_bus = new CommonBus();
        serverPanel = new ServerPanel(common_bus);
        clientPanel = new ClientPanel(common_bus);
        mainChatPanel = new MainChatPanel(common_bus);

        common_bus.setMainChatPanel(mainChatPanel);

        initializeUI();
    }
    private void initializeUI() {
        setTitle("Remote Desktop");
//        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/icon.png")));
//        setIconImage(icon.getImage());
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        setResizable(false); // Không cho phép phóng to thu nhỏ

        // Tạo JTabbedPane để chứa các tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // Thêm các panel vào tabbedPane
        tabbedPane.addTab("Server", serverPanel);
        tabbedPane.addTab("Client", clientPanel);
        tabbedPane.addTab("Chat", mainChatPanel);

        // Thêm tabbedPane vào JFrame
        add(tabbedPane);
    }


}
