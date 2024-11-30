package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpClient {

    private Socket client;
    private boolean is_connected_server;
    private String width = "", height = "";

    public TcpClient() {
        this.client = null;
        this.is_connected_server = false;
    }

    public void startConnectingToTcpServer(String host, int port, String password) throws IOException {
        if(this.is_connected_server == false) {
            this.client = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(this.client.getOutputStream());
            DataInputStream dis = new DataInputStream(this.client.getInputStream());

            dos.writeUTF(password);
            String result = dis.readUTF();

            if (result.equals("valid")) {
                width = dis.readUTF();
                height = dis.readUTF();
                new CreateFrame(this.client, width, height);
            } else {
                this.client.close();
                throw new IOException("Wrong password of server");
            }
        }
    }

    public void stopConnectingToTcpServer() throws IOException {
        if(this.is_connected_server = true) {
            this.client.close();
            //this.chat_bus.setSocket(null);
            this.is_connected_server = false;
        }
    }

    public boolean isConnectedServer() {
        return this.is_connected_server;
    }

    public Socket getClient() {
        return this.client;
    }
}
