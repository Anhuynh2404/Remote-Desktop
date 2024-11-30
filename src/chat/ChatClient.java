package chat;

import gui.ChatPanel;
import gui.MainChatPanel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private MainChatPanel main_chat_panel;

    private Socket client;
    private boolean is_connected_server;

    public ChatClient(MainChatPanel main_chat_panel) {
        this.client = null;
        this.is_connected_server = false;
        this.main_chat_panel = main_chat_panel;
    }

    public void startConnectingToChatServer(String host, int port) throws IOException {
        if(this.is_connected_server == false) {
            this.client = new Socket(host, port);
            System.out.println("Chat Client kết nối với " + this.client);
            ChatBus chat_bus = new ChatBus(this.client);
            this.main_chat_panel.addNewConnection(chat_bus);
            this.is_connected_server = true;
        }
    }

    public void stopConnectingToTcpServer() throws IOException {
        if(this.is_connected_server == true) {
            this.client.close();
            this.is_connected_server = false;
        }
    }

    public boolean isConnectedServer() {
        return this.is_connected_server;
    }

    public void setConnectedServer(boolean b) {
        this.is_connected_server = b;
    }

    public Socket getClient() {
        return this.client;
    }
}
