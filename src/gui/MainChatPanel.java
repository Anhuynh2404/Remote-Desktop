package gui;

import chat.ChatBus;
import common.CommonBus;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainChatPanel extends JPanel {
    private JLabel connectionsLabel;
    private ArrayList<ChatPanel> chat_panels;
    private CommonBus common_bus;
    private int count;

    public MainChatPanel(CommonBus common_bus) {
        this.common_bus = common_bus;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Soft gray background

        this.count = 0;
        this.chat_panels = new ArrayList<>();

        // Panel hiển thị số lượng kết nối
        JPanel connectionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionsPanel.setBackground(new Color(70, 130, 180)); // Steel blue for title bar
        connectionsLabel = new JLabel();
        connectionsLabel.setText("<html><font color='white'>All connections </font><font color='yellow'>(" + this.count + ")</font></html>");
        connectionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        connectionsPanel.add(connectionsLabel);
        add(connectionsPanel, BorderLayout.NORTH);
    }

    public void addCount(int n) {
        this.count += n;
        connectionsLabel.setText("<html><font color='white'>All connections </font><font color='yellow'>(" + this.count + ")</font></html>");
    }

    public ArrayList<ChatPanel> getChatPanels() {
        return this.chat_panels;
    }

    public void addNewConnection(ChatBus chat_bus) {
        ChatPanel chatPanel = new ChatPanel(this, this.common_bus, chat_bus);
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.add(chatPanel, BorderLayout.CENTER);
        this.chat_panels.add(chatPanel);

        this.addCount(1);
        this.validate();
        this.revalidate();
        this.repaint();
    }
}
