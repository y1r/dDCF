package dDCF.InterConnects;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Accepter {
	private ServerSocket sock;
	private Thread th;

	public Accepter(int port) throws IOException {
		sock = new ServerSocket(port);
	}

	public void start() {
		th = new Thread(() -> {
			while (true) {
				try {
					Socket con = sock.accept();

				} catch (IOException e) {
				}
			}
		});
	}
}
