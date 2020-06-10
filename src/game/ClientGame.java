package game;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import game.packets.ClientCardPacket;
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
			initCards();
			newCards();
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
			if (currentPlayer == thisPlayer) newCards();
			kingOneX = packet.getKingOneX();
			kingOneY = packet.getKingOneY();
			kingTwoX = packet.getKingTwoX();
			kingTwoY = packet.getKingTwoY();
		} else if (object instanceof GameEndPacket) {
			GameEndPacket packet = (GameEndPacket) object;
			showWinner(packet.getWinner());
		}
		
		gameWindow.repaint(new Rectangle(900, 900));
		
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


	private void newCards() {
		for (int i = 0; i < MAX_CARDS; ++i) {
			takenCards[i] = false;
		}
		
		int card = (int)(Math.random() * MAX_CARDS);
		while (true) {
			if (!takenCards[card]) {
				takenCards[card] = true;
				for (ActionListener l : window.btn1.getActionListeners()) {
					window.btn1.removeActionListener(l);
				}
				window.btn1.addActionListener(actions[card]);
				window.btn1.setText(cardTexts[card]);
				break;
			} else {
				card++;
				card = card % MAX_CARDS;
			}
		}
		
		card = (int)(Math.random() * MAX_CARDS);
		while (true) {
			if (!takenCards[card]) {
				takenCards[card] = true;
				for (ActionListener l : window.btn2.getActionListeners()) {
					window.btn2.removeActionListener(l);
				}
				window.btn2.addActionListener(actions[card]);
				window.btn2.setText(cardTexts[card]);
				break;
			} else {
				card++;
				card = card % MAX_CARDS;
			}
		}
		
		card = (int)(Math.random() * MAX_CARDS);
		while (true) {
			if (!takenCards[card]) {
				takenCards[card] = true;
				for (ActionListener l : window.btn3.getActionListeners()) {
					window.btn3.removeActionListener(l);
				}
				window.btn3.addActionListener(actions[card]);
				window.btn3.setText(cardTexts[card]);
				break;
			} else {
				card++;
				card = card % MAX_CARDS;
			}
		}
		
		card = (int)(Math.random() * MAX_CARDS);
		while (true) {
			if (!takenCards[card]) {
				takenCards[card] = true;
				for (ActionListener l : window.btn4.getActionListeners()) {
					window.btn4.removeActionListener(l);
				}
				window.btn4.addActionListener(actions[card]);
				window.btn4.setText(cardTexts[card]);
				break;
			} else {
				card++;
				card = card % MAX_CARDS;
			}
		}
	}
	
	// CARDS
	private void initCards() {
		cardTexts = new String[8];
		actions = new ActionListener[8];
		
		cardTexts[0] = "Svoja polja +1, svoj kralj =1";
		actions[0] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if (fields[i][j] == Game.PLAYER_TWO && power[i][j] < 9) power[i][j]++;	
						}
					}
					power[kingTwoX][kingTwoY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power));
				}

			}
		};
		
		cardTexts[1] = "Svoj kralj +3";
		actions[1] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingTwoX][kingTwoY] += 3;
					if (power[kingTwoX][kingTwoY] > 9) power[kingTwoX][kingTwoY] = 9;
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[2] = "Neprijateljska polja -1, svoj kralj =1";
		actions[2] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if ((fields[i][j] == Game.PLAYER_ONE || fields[i][j] == Game.KING_ONE) && power[i][j] > 1) power[i][j]--;	
						}
					}
					power[kingTwoX][kingTwoY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[3] = "Neprijateljski kralj -3";
		actions[3] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingOneX][kingOneY] -= 3;
					if (power[kingOneX][kingOneY] < 1) power[kingOneX][kingOneY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[4] = "Zauzmi kraljev red, kralj =1";
		actions[4] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					
					for (int j = 0; j < 5; ++j) {
						if (fields[kingTwoX][j] == Game.PLAYER_ONE || fields[kingTwoX][j] == Game.NOBODY) {
							fields[kingTwoX][j] = Game.PLAYER_TWO;
							power[kingTwoX][j] = 1;
						}
					}
					power[kingTwoX][kingTwoY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[5] = "Zauzmi kraljev stupac, kralj =1";
		actions[5] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					
					for (int i = 0; i < 5; ++i) {
						if (fields[i][kingTwoY] == Game.PLAYER_ONE || fields[i][kingTwoY] == Game.NOBODY) {
							fields[i][kingTwoY] = Game.PLAYER_TWO;
							power[i][kingTwoY] = 1;
						}
					}
					power[kingTwoX][kingTwoY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[6] = "Zauzmi kriz, polja =1";
		actions[6] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						if (fields[i][2] == Game.PLAYER_ONE || fields[i][2] == Game.NOBODY) {
							fields[i][2] = Game.PLAYER_TWO;
							power[i][2] = 1;
						}
					}
					
					for (int j = 0; j < 5; ++j) {
						if (fields[2][j] == Game.PLAYER_ONE || fields[2][j] == Game.NOBODY) {
							fields[2][j] = Game.PLAYER_TWO;
							power[2][j] = 1;
						}
					}
					
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if (fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.KING_TWO) power[i][j] = 1;
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[7] = "Zauzmi dijagonale, polja =1";
		actions[7] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0, j = 0; i < 5; ++i, ++j) {
						if (fields[i][j] == Game.PLAYER_ONE || fields[i][j] == Game.NOBODY) {
							fields[i][j] = Game.PLAYER_TWO;
							power[i][j] = 1;
						}
					}
					
					for (int i = 0, j = 4; i < 5; ++i, --j) {
						if (fields[i][j] == Game.PLAYER_ONE || fields[i][j] == Game.NOBODY) {
							fields[i][j] = Game.PLAYER_TWO;
							power[i][j] = 1;
						}
					}
					
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if (fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.KING_TWO) power[i][j] = 1;
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
	}
}
