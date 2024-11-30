package gui;

import chat.*;
import common.CommonBus;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class ChatPanel extends JPanel implements Runnable {

    private JPanel chatDisplay;
    private JScrollPane scrollPane;
    private JTextField messageField;
    private MainChatPanel root;

    private CommonBus common_bus;
    private ChatBus chat_bus;

    public ChatPanel(MainChatPanel root, CommonBus common_bus, ChatBus chat_bus) {
        this.common_bus = common_bus;
        this.chat_bus = chat_bus;
        this.root = root;

        initializeUI();

        new Thread(this).start();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        chatDisplay = new JPanel();
        chatDisplay.setLayout(new BoxLayout(chatDisplay, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(chatDisplay);
        add(scrollPane, BorderLayout.CENTER);

        // Khu vực nhập tin nhắn và các nút
        JPanel chatInputRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messageField = new JTextField(20);
        JButton sendFileButton = new JButton("Send File");
        JButton sendMessageButton = new JButton("Send");

        chatInputRow.add(new JScrollPane(messageField));
        chatInputRow.add(sendMessageButton);
        chatInputRow.add(sendFileButton);
        add(chatInputRow, BorderLayout.SOUTH);

        sendMessageButton.addActionListener(e -> this.sendMessage());
        sendFileButton.addActionListener(e -> this.sendFile());
    }

    private void sendMessage() {
        try {
            String content = this.messageField.getText().trim();
            if (!content.isEmpty()) {
                StringMessage strMessage = new StringMessage(InetAddress.getLocalHost().getHostName(), content);
                this.chat_bus.sendMessage(strMessage);
                addMessageToPanel("You: ",  content, "right");
                messageField.setText("");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to send message.\n" + e.getMessage());
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.showDialog(this, "Send");

        File file = fileChooser.getSelectedFile();
        if (file != null && file.length() <= 30 * 1024 * 1024) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileMessage file_message = new FileMessage(InetAddress.getLocalHost().getHostName(), file.getName(), file.length(), fis.readAllBytes());
                chat_bus.sendMessage(file_message);
                addMessageToPanel("You: ", file.getName(), "right");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Unable to send file.\n" + e.getMessage());
            }
        }
    }

    public void addMessageToPanel(String sender, String message, String position) {
        String color = position.equals("right") ? "green" : "blue";
        Color bg_color = position.equals("right") ? new Color(144, 238, 144) : new Color(230, 230, 250);

        JLabel messageLabel = new JLabel("<html><font color='" + color + "'>" + sender + "</font><br>" + message + "</html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        messageLabel.setBackground(bg_color);

        // Tạo một JPanel chứa messageLabel với layout FlowLayout
        JPanel messagePanel = new JPanel(new FlowLayout(position.equals("right") ? FlowLayout.RIGHT : FlowLayout.LEFT));
        messagePanel.add(messageLabel);

        // Đặt khoảng cách giữa các tin nhắn
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        chatDisplay.add(messagePanel);
        chatDisplay.revalidate();
        chatDisplay.repaint();
    }

    public void addFileToPanel(FileMessage fileMessage) {

        JLabel fileLabel = new JLabel("<html><font color='blue'>" + fileMessage.getSender() + "</font><br><u>" + fileMessage.getName() + "</u></html>");
        fileLabel.setOpaque(true);
        fileLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        fileLabel.setBackground(new Color(230, 230, 250));

        // Tạo một JPanel chứa messageLabel với layout FlowLayout
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(fileLabel);

        // Đặt khoảng cách giữa các tin nhắn
        filePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        fileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    saveFile(fileMessage);
                }
            }
        });
        chatDisplay.add(filePanel);
        chatDisplay.revalidate();
    }

    private void saveFile(FileMessage fileMessage) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setSelectedFile(new File(fileMessage.getName()));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(fileMessage.getData());
                JOptionPane.showMessageDialog(this, "File saved successfully");
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, "Unable to save file." + exception.getMessage());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (common_bus.getChatServer().isHasPartner() || common_bus.getChatClient().isConnectedServer()) {
                    Message message = chat_bus.recvMessage();
                    if (message != null) {
                        if (message instanceof StringMessage) {
                            StringMessage strMessage = (StringMessage) message;
                            addMessageToPanel(strMessage.getSender() + ": ", strMessage.getContent(), "left");
                        } else if (message instanceof FileMessage) {
                            FileMessage fileMessage = (FileMessage) message;
                            addFileToPanel(fileMessage);
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                this.common_bus.getChatServer().setHasPartner(false);
                this.common_bus.getChatClient().setConnectedServer(false);

                this.root.remove(this);
                this.root.addCount(-1);
                this.root.getChatPanels().remove(this);
                this.root.validate();
                this.root.revalidate();
                this.root.repaint();

                try {
                    this.chat_bus.getSocket().close();
                }
                catch(Exception exception) {}
                break;
            }
        }
    }
}
