package server;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;

public class SendScreen extends Thread {
	private Socket socket = null;
	private Robot robot = null;
	private Rectangle rectangle = null;
	private volatile boolean continueLoop = true;
	private OutputStream os = null;

	public SendScreen(Socket socket, Robot robot, Rectangle rect) {
		this.socket = socket;
		this.robot = robot;
		this.rectangle = rect;
		start();
	}

	public void stopSending() {
		continueLoop = false;
		try {
			if (os != null) os.close();
			if (socket != null && !socket.isClosed()) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			os = socket.getOutputStream();
			ImageOutputStream imageOutput = ImageIO.createImageOutputStream(os);

			// Thiết lập ImageWriter cho JPEG với chất lượng tốt nhất
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
			if (!writers.hasNext()) throw new IOException("No JPEG writer found");
			ImageWriter writer = writers.next();
			writer.setOutput(imageOutput);

			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.95f); // Đặt chất lượng ảnh cao (gần với 1.0)

			while (continueLoop && !socket.isClosed()) {
				BufferedImage image = robot.createScreenCapture(rectangle);

				try {
					writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
				} catch (IOException e) {
					System.err.println("Error sending image: " + e.getMessage());
					stopSending();
					break;
				}

				try {
					Thread.sleep(10); // Điều chỉnh thời gian nếu cần
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			writer.dispose();
			imageOutput.close();
		} catch (IOException e) {
			System.err.println("Stream or socket closed unexpectedly: " + e.getMessage());
		} finally {
			stopSending();
		}
	}
}
