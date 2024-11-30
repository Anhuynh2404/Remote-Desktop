package common;

import client.TcpClient;
import chat.ChatClient;
import chat.ChatServer;
import gui.ChatPanel;
import gui.MainChatPanel;
import server.TcpServer;

public class CommonBus {
    // TODO: for server
    private TcpServer tcp_server;
    private ChatServer chat_server;

    // TODO: for client
    private TcpClient tcp_client;
    private ChatClient chat_client;

    public CommonBus() {
        this.tcp_server = new TcpServer();
        this.tcp_client = new TcpClient();
    }

    public void setMainChatPanel(MainChatPanel main_chat_panel) {
        this.chat_server = new ChatServer(main_chat_panel);
        this.chat_client = new ChatClient(main_chat_panel);
    }

    public TcpServer getTcpServer() {
        return this.tcp_server;
    }

    public ChatServer getChatServer() { return this.chat_server; }

    public TcpClient getTcpClient() { return this.tcp_client; }

    public ChatClient getChatClient() { return this.chat_client; }

    // TODO: handle events of server
    public void startListeningOnServer(String host, int port, String password) throws Exception {
        if(!this.tcp_server.isListening() && !this.chat_server.isListening()) {
            // Port chat = port tcp + 1
            this.tcp_server.startListeningOnTcpServer(host, port, password);
            this.chat_server.startListeningOnChatServer(host, port + 1);
        }
    }

    public void stopListeningOnServer() throws Exception {
        if(this.tcp_server.isListening() && this.chat_server.isListening()) {
            this.tcp_server.stopListeningOnTcpServer();
            this.chat_server.stopListeningOnChatServer();
        }
    }

    public void startConnectingToServer(String host, int port, String password) throws Exception {
        if(this.tcp_client.isConnectedServer()) throw new Exception("You are remoting!");
        this.tcp_client.startConnectingToTcpServer(host, port, password);
        this.chat_client.startConnectingToChatServer(host, port + 1);
    }
}
