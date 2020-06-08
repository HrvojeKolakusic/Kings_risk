package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import game.packets.ClientPlayPacket;
import game.packets.GameEndPacket;
import game.packets.UpdatePacket;

public class ServerGame extends Game {

	private ServerSocket serverSocket;
	private Socket socket;
	private Connection connection;
	
	public ServerGame () {
		super(Game.PLAYER_ONE);
		try {
			serverSocket = new ServerSocket(Game.PORT);
			socket = serverSocket.accept();
			connection = new Connection(this, socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void inputReceived(int x, int y) {
		if (isMyTurn() && validMove(x, y)) updateField(x, y);
		
	}
	
	@Override
	public void packetReceived(Object object) {
		
		if (object instanceof ClientPlayPacket) {
			ClientPlayPacket packet = (ClientPlayPacket) object;
			updateField(packet.getX(), packet.getY());
		}
	}
	
	private void updateField(int x, int y) {
		
		if (currentPlayer == Game.PLAYER_ONE) {
			fields[kingOneX][kingOneY] = currentPlayer;
			fields[x][y] = Game.KING_ONE;
			kingOneX = x;
			kingOneY = y;
			currentPlayer = Game.PLAYER_TWO;
		} else {
			fields[kingTwoX][kingTwoY] = currentPlayer;
			fields[x][y] = Game.KING_TWO;
			kingTwoX = x;
			kingTwoY = y;
			currentPlayer = Game.PLAYER_ONE;
		}
		
		connection.sendPacket(new UpdatePacket(fields, currentPlayer, kingOneX, kingOneY, kingTwoX, kingTwoY));
		gameWindow.repaint();
			
		int winner = checkWin();
		
		/*
		 * if winner
		 * 		show winner
		 */
	}
	
	private boolean validMove(int x, int y) {
		
		if (fields[x][y] == currentPlayer) return true;
		if (x == kingOneX) {
			if ((y-1) == kingOneY || (y+1) == kingOneY) return true;
		}
		if (y == kingOneY) {
			if ((x-1) == kingOneX || (x+1) == kingOneX) return true;
		}
		
		return false;
	}
	
	private int checkWin() { //update
		
		return 0;
	}
	
	private void endGame() {
		connection.sendPacket(new GameEndPacket(0)); //update
	}

	@Override
	public void close() {
		try {
			connection.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	
	

}
