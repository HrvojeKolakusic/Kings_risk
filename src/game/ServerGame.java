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
	private int turnCounter;
	
	public ServerGame () {
		super(Game.PLAYER_ONE);
		try {
			serverSocket = new ServerSocket(Game.PORT);
			socket = serverSocket.accept();
			connection = new Connection(this, socket);
			turnCounter = 1;
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
			
			if (fields[x][y] == Game.PLAYER_ONE) {
				int carryPower = power[kingOneX][kingOneY];
				int leavePower = power[x][y] + carryPower - 9;
				if (leavePower <= 0) leavePower = 1;
				power[kingOneX][kingOneY] = leavePower;
				power[x][y] += carryPower - leavePower;
			} else if (fields[x][y] == Game.PLAYER_TWO) {
				int pom = power[kingOneX][kingOneY];
				power[kingOneX][kingOneY] = 1;
				power[x][y] = pom - power[x][y];
			} else {
				power[x][y] = power[kingOneX][kingOneY] - 1;
				power[kingOneX][kingOneY] = 1;
			}
			
			fields[kingOneX][kingOneY] = currentPlayer;
			fields[x][y] = Game.KING_ONE;
			kingOneX = x;
			kingOneY = y;
			currentPlayer = Game.PLAYER_TWO;
		} else {
			
			if (fields[x][y] == Game.PLAYER_TWO) {
				int carryPower = power[kingTwoX][kingTwoY];
				int leavePower = power[x][y] + carryPower - 9;
				if (leavePower <= 0) leavePower = 1;
				power[kingTwoX][kingTwoY] = leavePower;
				power[x][y] += carryPower - leavePower;
			} else if (fields[x][y] == Game.PLAYER_ONE) {
				int pom = power[kingTwoX][kingTwoY];
				power[kingTwoX][kingTwoY] = 1;
				power[x][y] = pom - power[x][y];
			} else {
				power[x][y] = power[kingTwoX][kingTwoY] - 1;
				power[kingTwoX][kingTwoY] = 1;
			}
			
			fields[kingTwoX][kingTwoY] = currentPlayer;
			fields[x][y] = Game.KING_TWO;
			kingTwoX = x;
			kingTwoY = y;
			currentPlayer = Game.PLAYER_ONE;
		}
		
		turnCounter++;
		if (turnCounter >= 7) {
			for (int i = 0; i < 5; ++i) {
				for (int j = 0; j < 5; ++j) {
					if (fields[i][j] != Game.NOBODY) {
						if (power[i][j] < 9) power[i][j]++;
					}
				}
			}
			turnCounter = 1;
		}
		
		connection.sendPacket(new UpdatePacket(fields, power, currentPlayer, kingOneX, kingOneY, kingTwoX, kingTwoY));
		gameWindow.repaint();
			
		int winner = checkWin();
		if (winner != Game.NOBODY) endGame(winner);
		
		if (!hasValidMove()) {
			endGame(Game.NOBODY);
		}
	}
	
	private boolean validMove(int x, int y) {
		
		if (currentPlayer == Game.PLAYER_ONE) {
			if (fields[x][y] == currentPlayer) return true;
			if (power[kingOneX][kingOneY] > power[x][y]) {
				if (x == kingOneX) {
					if ((y-1) == kingOneY || (y+1) == kingOneY) return true;
				}
				if (y == kingOneY) {
					if ((x-1) == kingOneX || (x+1) == kingOneX) return true;
				}
			}
			return false;
		} else {
			if (fields[x][y] == currentPlayer) return true;
			if (power[kingTwoX][kingTwoY] > power[x][y]) {
				if (x == kingTwoX) {
					if ((y-1) == kingTwoY || (y+1) == kingTwoY) return true;
				}
				if (y == kingTwoY) {
					if ((x-1) == kingTwoX || (x+1) == kingTwoX) return true;
				}
			}
			return false;
		}

	}
	
	private int checkWin() {
		
		if (kingOneX == kingTwoX && kingOneY == kingTwoY) {
			if (currentPlayer == Game.PLAYER_ONE) return Game.PLAYER_TWO;
			else return Game.PLAYER_ONE;
		}
		
		return Game.NOBODY;
	}
	
	private boolean hasValidMove() {
		if (currentPlayer == Game.PLAYER_ONE) {
			for (int i = 0; i < 5; ++i) {
				for (int j = 0; j < 5; ++j) {
					if (i == kingOneX && j == kingOneY) continue;
					if (validMove(i, j)) return true;
				}
			}
		} else {
			for (int i = 0; i < 5; ++i) {
				for (int j = 0; j < 5; ++j) {
					if (i == kingTwoX && j == kingTwoY) continue;
					if (validMove(i, j)) return true;
				}
			}
		}
		
		return false;
	}
	
	private void endGame(int winner) {
		showWinner(winner);
		connection.sendPacket(new GameEndPacket(winner));
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
