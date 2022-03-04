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
//import game.packets.LoadGamePacket;
import game.packets.UpdatePacket;

public class ServerGame extends Game {

	private ServerSocket serverSocket;
	private Socket socket;
	private Connection connection;
	private int turnCounter;
	private int textTurn;
	private String text = "";
	
	public ServerGame(int port) {
		super(Game.PLAYER_ONE);
		try {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();			//funkcija ceka da se klijent prikljuci na socket
			connection = new Connection(this, socket);
			turnCounter = 1;
			textTurn = 1;
			initCards();		//funkcija stvara nove ActionListener-e i tekstove za kartice
			newCards();			//funkcija odabire 4 nove kartice
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public ServerGame(LoadGamePacket packet, int port) {	//kod koji bi sluzio za ucitavanje spremljene igre
		super(Game.PLAYER_ONE);
		try {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			connection = new Connection(this, socket);
			
			currentPlayer = packet.getCurrentPlayer();
			
			if (currentPlayer == Game.PLAYER_ONE) {
				setCards(packet.getTakenCards());
			}
			
			fields = packet.getFields();
			power = packet.getPower();
			kingOneX = packet.getKingOneX();
			kingOneY = packet.getKingOneY();
			kingTwoX = packet.getKingTwoX();
			kingTwoY = packet.getKingTwoY();
			turnCounter = packet.getTurnCounter();
			textTurn = packet.getTurnText();
			text = packet.getText();
			
			if (currentPlayer == Game.PLAYER_ONE) {
				window.textBox.setText("Potez: " + String.valueOf(textTurn) + 
						"\nTrenutni igrac: Plavi\n" + text);
			} else {
				window.textBox.setText("Potez: " + String.valueOf(textTurn) + 
						"\nTrenutni igrac: Crveni\n" + text);
			}
			
			packet.setText(window.textBox.getText());
			
			connection.sendPacket(packet);
			gameWindow.repaint(new Rectangle(900, 900));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	@Override
	public void inputReceived(int x, int y) {		//funkcija se poziva kada se mišem pritisne na neko polje
		if (isMyTurn() && validMove(x, y)) updateField(x, y, -1);
		
	}
	
	@Override
	public void packetReceived(Object object) {		//funkcija se poziva kada server primi paket
		
		if (object instanceof ClientPlayPacket) {
			ClientPlayPacket packet = (ClientPlayPacket) object;
			updateField(packet.getX(), packet.getY(), -1);
		} else if (object instanceof ClientCardPacket) {
			ClientCardPacket packet = (ClientCardPacket) object;
			fields = packet.getFields();
			power = packet.getPower();
			updateField(-1, -1, packet.getCard());
		}
	}
	
	private void updateField(int x, int y, int card) {	//funkcija koja izracunava novo stanje igre
		
		if (x == -1 && y == -1) {	//x = -1 i y = -1 su oznake da je odigrana karta
			
			 
			if (currentPlayer == Game.PLAYER_ONE) {	//ako je potez odigrao server
				text = "Potez: " + String.valueOf(textTurn+1) + "\nTrenutni igrac: Crveni\nPlavi je odigrao: " + cardTexts[card];
				currentPlayer = Game.PLAYER_TWO;
			} else {								//ako je potez odigrao klijent
				text = "Potez: " + String.valueOf(textTurn+1) + "\nTrenutni igrac: Plavi\nCrveni je odigrao: " + cardTexts[card];
				currentPlayer = Game.PLAYER_ONE;
			}
			
		} else {
			if (currentPlayer == Game.PLAYER_ONE) {	//ako je potez odigrao server
				
				text = "Potez: " + String.valueOf(textTurn+1) + "\nTrenutni igrac: Crveni\nPlavi je odigrao: Kralj na (" 
				+ String.valueOf(y+1) + "," + String.valueOf(x+1) + ")";
				
				if (fields[x][y] == Game.PLAYER_ONE) {			//kralj je pomaknut na prijateljsko polje		
					int carryPower = power[kingOneX][kingOneY];
					int leavePower = power[x][y] + carryPower - 9;
					if (leavePower <= 0) leavePower = 1;
					power[kingOneX][kingOneY] = leavePower;
					power[x][y] += carryPower - leavePower;
				} else if (fields[x][y] == Game.PLAYER_TWO) {	//kralj je pomaknut na neprijateljsko polje
					int pom = power[kingOneX][kingOneY];
					power[kingOneX][kingOneY] = 1;
					power[x][y] = pom - power[x][y];
				} else {										//kralj je pomaknut na prazno polje
					power[x][y] = power[kingOneX][kingOneY] - 1;
					power[kingOneX][kingOneY] = 1;
				}
				
				fields[kingOneX][kingOneY] = currentPlayer;
				fields[x][y] = Game.KING_ONE;
				kingOneX = x;
				kingOneY = y;
				currentPlayer = Game.PLAYER_TWO;				//promjena igraca
				
			} else {								//ako je potez odigrao klijent
				
				text = "Potez: " + String.valueOf(textTurn+1) + "\nTrenutni igrac: Plavi\nCrveni je odigrao: Kralj na (" 
				+ String.valueOf(y+1) + "," + String.valueOf(x+1) + ")";
				
				if (fields[x][y] == Game.PLAYER_TWO) {			//kralj je pomaknut na prijateljsko polje
					int carryPower = power[kingTwoX][kingTwoY];
					int leavePower = power[x][y] + carryPower - 9;
					if (leavePower <= 0) leavePower = 1;
					power[kingTwoX][kingTwoY] = leavePower;
					power[x][y] += carryPower - leavePower;
				} else if (fields[x][y] == Game.PLAYER_ONE) {	//kralj je pomaknut na neprijateljsko polje
					int pom = power[kingTwoX][kingTwoY];
					power[kingTwoX][kingTwoY] = 1;
					power[x][y] = pom - power[x][y];
				} else {										//kralj je pomaknut na prazno polje
					power[x][y] = power[kingTwoX][kingTwoY] - 1;
					power[kingTwoX][kingTwoY] = 1;
				}
				
				fields[kingTwoX][kingTwoY] = currentPlayer;
				fields[x][y] = Game.KING_TWO;
				kingTwoX = x;
				kingTwoY = y;
				currentPlayer = Game.PLAYER_ONE;				//promjena igraca
			}
		}
		
		if (currentPlayer == thisPlayer) newCards();
		/*
		 * turnCounter sluzi kako bi se jacina svih zauzetih polja povecala za 1 svakih 6 poteza
		 */
		turnCounter++;
		textTurn++;
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
		
		/*
		 * update textBox u dolje desnom kutu i javi klijentu promjene
		 */
		
		text = text.replaceAll("<html>", "").replaceAll("</html>", "").replaceAll("<br>", "");
		window.textBox.setText(text);
		connection.sendPacket(new UpdatePacket(fields, power, currentPlayer, kingOneX, kingOneY, kingTwoX, kingTwoY, text));
		gameWindow.repaint(new Rectangle(800, 800));
			
		int winner = checkWin();
		if (winner != Game.NOBODY) endGame(winner); //ako je partija gotova
	}
	
	private boolean validMove(int x, int y) {			//funkcija provjerava da li je pritisnuto polje uz kralja
		
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
	}
	
	private int checkWin() {	//ako se kraljevi nalaze na istom polju
		
		if (kingOneX == kingTwoX && kingOneY == kingTwoY) {
			if (currentPlayer == Game.PLAYER_ONE) return Game.PLAYER_TWO;
			else return Game.PLAYER_ONE;
		}
		
		return Game.NOBODY;
	}
	
	private void endGame(int winner) {
		showWinner(winner);
		connection.sendPacket(new GameEndPacket(winner));
	}
	
	@Override
	public void close() {		//funkcija se poziva pri gasenju aplikacije
		try {
			connection.close();
			serverSocket.close();
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
	
	//kod koji bi sluzio da se ucitaju spremljene karte
	/*private void setCards(boolean[] taken) {
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
	private void initCards() {			//funkcija stvara nove ActionListener-e i tekstove za karte
		cardTexts = new String[MAX_CARDS];
		actions = new ActionListener[MAX_CARDS];
		
		cardTexts[0] = "<html>Svoja polja +1,<br>kralj =1</html>";
		actions[0] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if (fields[i][j] == PLAYER_ONE && power[i][j] < 9) power[i][j]++;
						}
					}
					power[kingOneX][kingOneY] = 1;
					updateField(-1, -1, 0);
				}
			}
		};
		
		cardTexts[1] = "Kralj +3";
		actions[1] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingOneX][kingOneY] += 3;
					if (power[kingOneX][kingOneY] > 9) power[kingOneX][kingOneY] = 9;
					updateField(-1, -1, 1);
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
							if ((fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.KING_TWO) && power[i][j] > 1) power[i][j]--;	
						}
					}
					power[kingOneX][kingOneY] = 1;
					updateField(-1, -1, 2);
				}
			}
		};
		
		cardTexts[3] = "<html>Neprijateljski<br>kralj -3</html>";
		actions[3] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingTwoX][kingTwoY] -= 3;
					if (power[kingTwoX][kingTwoY] < 1) power[kingTwoX][kingTwoY] = 1;
					updateField(-1, -1, 3);
				}
			}
		};
		
		cardTexts[4] = "<html>Zauzmi kraljev<br>stupac,<br>kralj =1</html>";
		actions[4] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					
					for (int j = 0; j < 5; ++j) {
						if (fields[kingOneX][j] == Game.PLAYER_TWO || fields[kingOneX][j] == Game.NOBODY) {
							fields[kingOneX][j] = Game.PLAYER_ONE;
							power[kingOneX][j] = 1;
						}
					}
					power[kingOneX][kingOneY] = 1;
					updateField(-1, -1, 4);
				}
			}
		};
		
		cardTexts[5] = "<html>Zauzmi<br>kraljev red,<br>kralj =1</html>";
		actions[5] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					
					for (int i = 0; i < 5; ++i) {
						if (fields[i][kingOneY] == Game.PLAYER_TWO || fields[i][kingOneY] == Game.NOBODY) {
							fields[i][kingOneY] = Game.PLAYER_ONE;
							power[i][kingOneY] = 1;
						}
					}
					power[kingOneX][kingOneY] = 1;
					updateField(-1, -1, 5);
				}
			}
		};
		
		cardTexts[6] = "<html>Zauzmi kriz,<br>polja =1</html>";
		actions[6] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0; i < 5; ++i) {
						if (fields[i][2] == Game.PLAYER_TWO || fields[i][2] == Game.NOBODY) {
							fields[i][2] = Game.PLAYER_ONE;
							power[i][2] = 1;
						}
					}
					
					for (int j = 0; j < 5; ++j) {
						if (fields[2][j] == Game.PLAYER_TWO || fields[2][j] == Game.NOBODY) {
							fields[2][j] = Game.PLAYER_ONE;
							power[2][j] = 1;
						}
					}
					
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if (fields[i][j] == Game.PLAYER_ONE || fields[i][j] == Game.KING_ONE) power[i][j] = 1;
						}
					}
					updateField(-1, -1, 6);
				}
			}
		};
		
		cardTexts[7] = "<html>Zauzmi<br>dijagonale,<br>polja =1</html>";
		actions[7] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					for (int i = 0, j = 0; i < 5; ++i, ++j) {
						if (fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.NOBODY) {
							fields[i][j] = Game.PLAYER_ONE;
							power[i][j] = 1;
						}
					}
					
					for (int i = 0, j = 4; i < 5; ++i, --j) {
						if (fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.NOBODY) {
							fields[i][j] = Game.PLAYER_ONE;
							power[i][j] = 1;
						}
					}
					
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							if (fields[i][j] == Game.PLAYER_ONE || fields[i][j] == Game.KING_ONE) power[i][j] = 1;
						}
					}
					updateField(-1, -1, 7);
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
								if (fields[kingOneX + i][kingOneY + j] != Game.KING_ONE &&
										(fields[kingOneX + i][kingOneY + j] == Game.PLAYER_TWO || fields[kingOneX + i][kingOneY + j] == Game.NOBODY)) {
									fields[kingOneX + i][kingOneY + j] = Game.PLAYER_ONE;
									power[kingOneX + i][kingOneY + j] = 1;
								}
							} catch (ArrayIndexOutOfBoundsException exc) {
								//nista
							}
						}
					}
					power[kingOneX][kingOneY] -= 3;
					if (power[kingOneX][kingOneY] < 1) power[kingOneX][kingOneY] = 1;
					updateField(-1, -1, 8);
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
								if (fields[kingOneX + i][kingOneY + j] != Game.KING_ONE && fields[kingOneX + i][kingOneY + j] != Game.KING_TWO) {
									fields[kingOneX + i][kingOneY + j] = Game.NOBODY;
									power[kingOneX + i][kingOneY + j] = 1;
								}
							} catch (ArrayIndexOutOfBoundsException exc) {
								//nista
							}
						}
					}
					updateField(-1, -1, 9);
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
					updateField(-1, -1, 10);
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
					updateField(-1, -1, 11);
				}
			}
		};
	}
}
