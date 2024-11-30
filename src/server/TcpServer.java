package server;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

public class TcpServer {

	private ServerSocket server;
	private Socket client;
	private String password;
	private boolean is_listening;
	private SendScreen sendScreenThread;

	public TcpServer() {
		this.server = null;
		this.client = null;
		this.password = null;
		this.is_listening = false;
	}

	public void startListeningOnTcpServer(String host, int port, String password) throws IOException {
		if(this.is_listening == false) {
			InetSocketAddress endpoint = new InetSocketAddress(host, port);
			this.password = password;
			this.server = new ServerSocket();
			this.server.bind(endpoint);
			this.is_listening = true;
		}
	}

	public void waitingConnectionFromClient() {
		System.out.println("Đang chờ kết nối với " + this.server + this.password);
		new InitConnection(this.server, this.password);
	}

	public void stopListeningOnTcpServer() throws IOException {
		if(this.is_listening == true) {
			this.is_listening = false;
			if (this.sendScreenThread != null) {
				this.sendScreenThread.stopSending(); // Dừng SendScreen
			}
			this.server.close();
		}
	}

	public Vector<String> getAllIpv4AddressesOnLocal() throws SocketException {
		Vector<String> ipv4_addresses = new Vector<>();
		Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
		while(networks.hasMoreElements()) {
			NetworkInterface sub_networks = (NetworkInterface) networks.nextElement();
			Enumeration<InetAddress> inet_addresses = sub_networks.getInetAddresses();
			while(inet_addresses.hasMoreElements()) {
				try {
					Inet4Address ipv4 = (Inet4Address) inet_addresses.nextElement();
					ipv4_addresses.add(ipv4.getHostAddress());
				}
				catch(Exception e) {
					// TODO: pass ip version 6
				}
			}
		}
		return ipv4_addresses;
	}

	public boolean isListening() {
		return this.is_listening;
	}
}
