package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JPanel;

class SendEvents implements KeyListener, MouseMotionListener, MouseListener {
	private Socket cSocket;
	private JPanel cPanel;
	private PrintWriter writer;
	private double width, height;

	public SendEvents(Socket s, JPanel p, String width, String height) {
		this.cSocket = s;
		this.cPanel = p;
		this.width = Double.parseDouble(width.trim());
		this.height = Double.parseDouble(height.trim());

		cPanel.addKeyListener(this);
		cPanel.addMouseListener(this);
		cPanel.addMouseMotionListener(this);

		try {
			writer = new PrintWriter(cSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendCommand(int command, int... args) {
		writer.println(command);
		for (int arg : args) {
			writer.println(arg);
		}
		writer.flush();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = (int) (e.getX() * width / cPanel.getWidth());
		int y = (int) (e.getY() * height / cPanel.getHeight());
		sendCommand(Commands.MOVE_MOUSE.getAbbrev(), x, y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int button = (e.getButton() == MouseEvent.BUTTON3) ? 4 : 16;
		sendCommand(Commands.PRESS_MOUSE.getAbbrev(), button);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int button = (e.getButton() == MouseEvent.BUTTON3) ? 4 : 16;
		sendCommand(Commands.RELEASE_MOUSE.getAbbrev(), button);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		sendCommand(Commands.PRESS_KEY.getAbbrev(), e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		sendCommand(Commands.RELEASE_KEY.getAbbrev(), e.getKeyCode());
	}

	// Các phương thức không dùng có thể để trống.
	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
}
