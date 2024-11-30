package server;

import java.awt.Robot;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ReceiveEvents extends Thread {
	private Socket socket = null;
	private Robot robot = null;
	private boolean continueLoop = true;
	private Scanner scanner = null;

	public ReceiveEvents(Socket socket, Robot robot) {
		this.socket = socket;
		this.robot = robot;
		start();
	}

	// Phương thức dừng nhận sự kiện
	public void stopReceiving() {
		continueLoop = false;
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			scanner = new Scanner(socket.getInputStream());

			while (continueLoop && scanner.hasNextInt()) {
				int command = scanner.nextInt();
				System.out.println("Received command: " + command);
				switch (command) {
					case -1:
						robot.mousePress(scanner.nextInt());
						break;
					case -2:
						robot.mouseRelease(scanner.nextInt());
						break;
					case -3:
						robot.keyPress(scanner.nextInt());
						break;
					case -4:
						robot.keyRelease(scanner.nextInt());
						break;
					case -5:
						robot.mouseMove(scanner.nextInt(), scanner.nextInt());
						break;
					default:
						System.out.println("Unrecognized command: " + command);
				}
			}
		} catch (IOException e) {
			System.err.println("Error in ReceiveEvents: " + e.getMessage());
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}

	// Phương thức đóng tài nguyên
	private void cleanup() {
		if (scanner != null) {
			scanner.close();
		}
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
