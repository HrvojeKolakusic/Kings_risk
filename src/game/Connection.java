package game;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
/*
 * razred sluzi kao dretva koja salje i prima podatke izmedu servera i klijenta
 */
public class Connection implements Runnable {

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private Game game;
	
	private boolean running;
	
	public Connection(Game game, Socket socket) {
		this.game = game;
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread (this).start();
	}
	
	public void sendPacket(Object object) {
		try {
			outputStream.reset();
			outputStream.writeObject(object);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				Object object = inputStream.readObject();
				game.packetReceived(object);
			} catch (EOFException | SocketException e) {
				running = false;
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void close() {
		
		try {
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
