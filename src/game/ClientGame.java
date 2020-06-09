package game;

import java.io.IOException;
import java.net.Socket;

import game.packets.ClientPlayPacket;
import game.packets.GameEndPacket;
import game.packets.UpdatePacket;

public class ClientGame extends Game {
	
	private Socket socket;
	private Connection connection;

	
	public ClientGame() {
		super(Game.PLAYER_TWO);
		try {
			socket = new Socket("localhost", Game.PORT);
			connection = new Connection(this, socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void inputReceived(int x, int y) {
		if (isMyTurn() && validMove(x, y)) {
			connection.sendPacket(new ClientPlayPacket(x, y));
		}
	}
	
	@Override
	public void packetReceived(Object object) {
		
		if (object instanceof UpdatePacket) {
			UpdatePacket packet = (UpdatePacket) object;
			fields = packet.getFields();
			power = packet.getPower();
			currentPlayer = packet.getCurrentPlayer();
			kingOneX = packet.getKingOneX();
			kingOneY = packet.getKingOneY();
			kingTwoX = packet.getKingTwoX();
			kingTwoY = packet.getKingTwoY();
		} else if (object instanceof GameEndPacket) {
			GameEndPacket packet = (GameEndPacket) object;
			showWinner(packet.getWinner());
		}
		
		gameWindow.repaint();
		
	}
	
	private boolean validMove(int x, int y) {
		
		if (fields[x][y] == currentPlayer) return true;
		if (power[kingTwoX][kingTwoY] > power[x][y]) {
			if (x == this.kingTwoX) {
				if ((y-1) == this.kingTwoY || (y+1) == this.kingTwoY) return true;
			}
			if (y == this.kingTwoY) {
				if ((x-1) == this.kingTwoX || (x+1) == this.kingTwoX) return true;
			}
		}
		
		return false;
	}


	@Override
	public void close() {
		try {
			connection.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


}
