package client;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ReceivingScreen extends Thread {
	private JPanel cPanel;
	private InputStream oin;
	private Image image1;
	private volatile boolean continueLoop = true;

	public ReceivingScreen(InputStream in, JPanel p) {
		oin = in;
		cPanel = p;
		start();
	}

	public void stopReceiving() {
		continueLoop = false;
		try {
			if (oin != null) oin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while(true) {
				byte[] bytes = new byte[1024*1024];
				int count = 0;
				do {
					count += oin.read(bytes, count, bytes.length-count);
				} while(!(count > 4 && bytes[count-2] == (byte)-1 && bytes[count-1] == (byte)-39));
				image1 = ImageIO.read(new ByteArrayInputStream(bytes));
				image1 = image1.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);
				Graphics graphics = cPanel.getGraphics();
				graphics.drawImage(image1, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
