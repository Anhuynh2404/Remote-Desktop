package chat;

import gui.MainChatPanel;

import java.io.IOException;
import java.net.*;

public class ChatServer {
    private MainChatPanel main_chat_panel;

    private ServerSocket server;
    private Socket client;
    private boolean is_listening;
    private boolean is_has_partner;

    public ChatServer(MainChatPanel main_chat_panel) {
        this.server = null;
        this.client = null;
        this.is_listening = false;
        this.is_has_partner = false;
        this.main_chat_panel = main_chat_panel;
    }

    public void startListeningOnChatServer(String host, int port) throws IOException {
        if(this.is_listening == false) {
            InetSocketAddress endpoint = new InetSocketAddress(host, port);
            this.server = new ServerSocket();
            this.server.bind(endpoint);
            this.is_listening = true;
        }
    }

    public void waitingConnectionFromClient() throws IOException {
        try {
            System.out.println("Đang chờ kết nối từ client...");
            this.client = this.server.accept();
            System.out.println("Chat Server kết nối với " + client);
            ChatBus chat_bus = new ChatBus(this.client);
            this.main_chat_panel.addNewConnection(chat_bus);
            this.is_has_partner = true;
        } catch (IOException e) {
            System.err.println("Lỗi khi kết nối với client: " + e.getMessage());
        }
    }

    public void stopListeningOnChatServer() throws IOException {
        if(this.is_listening == true) {
            this.is_listening = false;
            this.is_has_partner = false;
            this.server.close();
        }
    }

    public boolean isListening() {
        return this.is_listening;
    }

    public boolean isHasPartner() {
        return this.is_has_partner;
    }

    public void setHasPartner(boolean b) {
        this.is_has_partner = b;
    }

    public ServerSocket getServer() {
        return this.server;
    }
}
