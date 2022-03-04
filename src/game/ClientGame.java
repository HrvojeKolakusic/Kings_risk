package game;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import game.packets.ClientCardPacket;
import game.packets.ClientPlayPacket;
import game.packets.GameEndPacket;
import game.packets.LoadGamePacket;
import game.packets.UpdatePacket;

public class ClientGame extends Game {
	
	private Socket socket;
	private Connection connection;

	public ClientGame(String ip, int port) {
		super(Game.PLAYER_TWO);
		try {
			socket = new Socket(ip, port);
			connection = new Connection(this, socket);
			initCards();	//funkcija stvara nove ActionListener-e i tekstove za karte
			newCards();		//funkcija odabire 4 nove karte
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void inputReceived(int x, int y) {		//funkcija se poziva kada se mišem pritisne na neko polje
		if (isMyTurn() && validMove(x, y)) {
			connection.sendPacket(new ClientPlayPacket(x, y));		//klijent šalje paket serveru da je odigrao potez
		}
	}
	
	@Override
	public void packetReceived(Object object) {		//funkcija se poziva kada klijent primi paket
		
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
			window.textBox.setText(packet.getText());
			
		} else if (object instanceof GameEndPacket) {
			GameEndPacket packet = (GameEndPacket) object;
			showWinner(packet.getWinner());
			
		} /*else if (object instanceof LoadGamePacket) {		//kod koji bi sluzio za ucitavanje spremljene igre
			LoadGamePacket packet = (LoadGamePacket) object;
			
			currentPlayer = packet.getCurrentPlayer();
			if (currentPlayer == Game.PLAYER_TWO) {
				setCards(packet.getTakenCards());
			}
			fields = packet.getFields();
			power = packet.getPower();
			kingOneX = packet.getKingOneX();
			kingOneY = packet.getKingOneY();
			kingTwoX = packet.getKingTwoX();
			kingTwoY = packet.getKingTwoY();
			window.textBox.setText(packet.getText());
		}*/
		
		gameWindow.repaint(new Rectangle(X, Y));		//ponovno se poziva funkcija paint()
															
	}
	
	private boolean validMove(int x, int y) {				//funkcija provjerava da li je pritisnuto polje uz kralja
		
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
	public void close() {	//funkcija se poziva pri gasenju aplikacije
		try {
			connection.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	private void newCards() {	// funkcija odabire 4 nove karte
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
	
	/*private void setCards(boolean[] taken) {				//kod koji bi sluzio da ucita karte koje su spremljene
		for (int i = 0; i < Game.MAX_CARDS; ++i) {
			if (taken[i]) {
				window.btn1.addActionListener(actions[i]);
				window.btn1.setText(cardTexts[i]);
				taken[i] = false;
				break;
			}
		}
		
		for (int i = 0; i < Game.MAX_CARDS; ++i) {
			if (taken[i]) {
				window.btn2.addActionListener(actions[i]);
				window.btn2.setText(cardTexts[i]);
				taken[i] = false;
				break;
			}
		}
		
		for (int i = 0; i < Game.MAX_CARDS; ++i) {
			if (taken[i]) {
				window.btn3.addActionListener(actions[i]);
				window.btn3.setText(cardTexts[i]);
				taken[i] = false;
				break;
			}
		}
		
		for (int i = 0; i < Game.MAX_CARDS; ++i) {
			if (taken[i]) {
				window.btn4.addActionListener(actions[i]);
				window.btn4.setText(cardTexts[i]);
				taken[i] = false;
				break;
			}
		}
	}*/
	
	// CARDS
	private void initCards() {				//funkcija stvara nove ActionListener-e i tekstove za karte
		cardTexts = new String[MAX_CARDS];
		actions = new ActionListener[MAX_CARDS];
		
		cardTexts[0] = "<html>Svoja polja +1,<br>kralj =1</html>";
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
					connection.sendPacket(new ClientCardPacket(fields, power, 0));
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
					connection.sendPacket(new ClientCardPacket(fields, power, 1));
				}
			}
		};
		
		cardTexts[2] = "<html>Neprijateljska<br>polja -1,<br>kralj=1</html>";
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
					connection.sendPacket(new ClientCardPacket(fields, power, 2));
				}
			}
		};
		
		cardTexts[3] = "<html>Neprijateljski<br>kralj -3</html>";
		actions[3] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingOneX][kingOneY] -= 3;
					if (power[kingOneX][kingOneY] < 1) power[kingOneX][kingOneY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power, 3));
				}
			}
		};
		
		cardTexts[4] = "<html>Zauzmi kraljev<br>stupac,<br>kralj =1</html>";
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
					connection.sendPacket(new ClientCardPacket(fields, power, 4));
				}
			}
		};
		
		cardTexts[5] = "<html>Zauzmi<br>kraljev red,<br>kralj =1</html>";
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
					connection.sendPacket(new ClientCardPacket(fields, power, 5));
				}
			}
		};
		
		cardTexts[6] = "<html>Zauzmi kriz,<br>polja =1</html>";
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
					connection.sendPacket(new ClientCardPacket(fields, power, 6));
				}
			}
		};
		
		cardTexts[7] = "<html>Zauzmi<br>dijagonale,<br>polja =1</html>";
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
					connection.sendPacket(new ClientCardPacket(fields, power, 7));
				}
			}
		};
		
		cardTexts[8] = "<html>Zauzmi susjedna,<br>kralj -3</html>";
		actions[8] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = -1; i < 2; ++i) {
						for (int j = -1; j < 2; ++j) {
							try {
								if (fields[kingTwoX + i][kingTwoY + j] != Game.KING_TWO &&
										(fields[kingTwoX + i][kingTwoY + j] == Game.PLAYER_ONE || fields[kingTwoX + i][kingTwoY + j] == Game.NOBODY)) {
									fields[kingTwoX + i][kingTwoY + j] = Game.PLAYER_TWO;
									power[kingTwoX + i][kingTwoY + j] = 1;
								}
							} catch (ArrayIndexOutOfBoundsException exc) {
								//nista
							}
						}
					}
					power[kingTwoX][kingTwoY] -= 3;
					if (power[kingTwoX][kingTwoY] < 1) power[kingTwoX][kingTwoY] = 1;
					connection.sendPacket(new ClientCardPacket(fields, power, 8));
				}
			}
		};
		
		cardTexts[9] = "Obrisi susjedna";
		actions[9] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = -1; i < 2; ++i) {
						for (int j = -1; j < 2; ++j) {
							try {
								if (fields[kingTwoX + i][kingTwoY + j] != Game.KING_ONE && fields[kingTwoX + i][kingTwoY + j] != Game.KING_TWO) {
									fields[kingTwoX + i][kingTwoY + j] = Game.NOBODY;
									power[kingTwoX + i][kingTwoY + j] = 1;
								}
							} catch (ArrayIndexOutOfBoundsException exc) {
								//nista
							}
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power, 9));
				}
			}
		};
		
		cardTexts[10] = "<html>Obrisi sredinu<br>(9 polja)</html>";
		actions[10] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 1; i < 4; ++i) {
						for (int j = 1; j < 4; ++j) {
							if (fields[i][j] != Game.KING_ONE && fields[i][j] != Game.KING_TWO) {
								fields[i][j] = Game.NOBODY;
								power[i][j] = 1;
							}
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power, 10));
				}
			}
		};
		
		cardTexts[11] = "Obrisi rubove";
		actions[11] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						if (fields[i][0] != Game.KING_ONE && fields[i][0] != Game.KING_TWO) {
							fields[i][0] = Game.NOBODY;
							power[i][0] = 1;
						}
						if (fields[i][4] != Game.KING_ONE && fields[i][4] != Game.KING_TWO) {
							fields[i][4] = Game.NOBODY;
							power[i][4] = 1;
						}
					}
					for (int j = 0; j < 5; ++j) {
						if (fields[0][j] != Game.KING_ONE && fields[0][j] != Game.KING_TWO) {
							fields[0][j] = Game.NOBODY;
							power[0][j] = 1;
						}
						if (fields[4][j] != Game.KING_ONE && fields[4][j] != Game.KING_TWO) {
							fields[4][j] = Game.NOBODY;
							power[4][j] = 1;
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power, 11));
				}
			}
		};
	}
}
