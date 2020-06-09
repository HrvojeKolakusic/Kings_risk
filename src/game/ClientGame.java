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
							if ((fields[i][j] == Game.PLAYER_TWO || fields[i][j] == Game.KING_TWO) && power[i][j] < 9) power[i][j]++;	
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power));
				}

			}
		};
		
		cardTexts[1] = "Povecaj jacinu svoga\n kralja +5";
		actions[1] = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (isMyTurn()) {
					power[kingTwoX][kingTwoY] += 5;
					if (power[kingTwoX][kingTwoY] > 9) power[kingTwoX][kingTwoY] = 9;
					connection.sendPacket(new ClientCardPacket(fields, power));
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
							if ((fields[i][j] == Game.PLAYER_ONE || fields[i][j] == Game.KING_ONE) && power[i][j] > 1) power[i][j]--;	
						}
					}
					connection.sendPacket(new ClientCardPacket(fields, power));
				}
			}
		};
		
		cardTexts[3] = "Smanji jacinu \nneprijateljskog kralja -3";
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
	}
}
