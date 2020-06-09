package game;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import game.packets.ClientCardPacket;
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
			initCards();
			//update
			window.btn1.addActionListener(actions[0]);
			window.btn1.setText(cardTexts[0]);
			window.btn2.addActionListener(actions[1]);
			window.btn2.setText(cardTexts[1]);
			window.btn3.addActionListener(actions[2]);
			window.btn3.setText(cardTexts[2]);
			window.btn4.addActionListener(actions[3]);
			window.btn4.setText(cardTexts[3]);
			//update
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
		} else if (object instanceof ClientCardPacket) {
			ClientCardPacket packet = (ClientCardPacket) object;
			fields = packet.getFields();
			power = packet.getPower();
			updateField(-1, -1);
		}
	}
	
	private void updateField(int x, int y) {
		
		if (x == -1 && y == -1) {
			if (currentPlayer == Game.PLAYER_ONE) currentPlayer = Game.PLAYER_TWO;
			else currentPlayer = Game.PLAYER_ONE;
		} else {
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
		gameWindow.repaint(new Rectangle(900, 900));
			
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
	
	// CARDS
	private void initCards() {
		cardTexts = new String[4];
		actions = new ActionListener[4];
		
		cardTexts[0] = "Povecaj jacinu svojih\n polja +1";
		actions[0] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if ((fields[i][j] == PLAYER_ONE || fields[i][j] == KING_ONE) && power[i][j] < 9) power[i][j]++;
						}
					}
					updateField(-1, -1);
				}
			}
		};
		
		cardTexts[1] = "Povecaj jacinu svoga\n kralja +5";
		actions[1] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingOneX][kingOneY] += 5;
					if (power[kingOneX][kingOneY] > 9) power[kingOneX][kingOneY] = 9;
					updateField(-1, -1);
				}
			}
		};
		
		cardTexts[2] = "Smanji jacinu \nneprijateljskih polja -1";
		actions[2] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if ((fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.KING_TWO) && power[i][j] > 1) power[i][j]--;	
						}
					}
					updateField(-1, -1);
				}
			}
		};
		
		cardTexts[3] = "Smanji jacinu \nneprijateljskog kralja -3";
		actions[3] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingTwoX][kingTwoY] -= 3;
					if (power[kingTwoX][kingTwoY] < 1) power[kingTwoX][kingTwoY] = 1;
					updateField(-1, -1);
				}
			}
		};
	}


}
