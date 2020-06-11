package game.packets;

import java.io.Serializable;

import game.Game;

public class LoadGamePacket implements Serializable {

	private static final long serialVersionUID = -8271674072254462382L;
	
	int currentPlayer;
	boolean[] takenCards = new boolean[Game.MAX_CARDS];
	int[][] fields = new int[5][5];
	int[][] power = new int[5][5];
	int kingOneX;
	int kingOneY;
	int kingTwoX;
	int kingTwoY;
	int turnCounter;
	int turnText;
	String text;
	
	public LoadGamePacket(int currentPlayer, boolean[] takenCards, int[][] fields, int[][] power, int kingOneX,
			int kingOneY, int kingTwoX, int kingTwoY, int turnCounter, int turnText, String text) {
		this.currentPlayer = currentPlayer;
		this.takenCards = takenCards;
		this.fields = fields;
		this.power = power;
		this.kingOneX = kingOneX;
		this.kingOneY = kingOneY;
		this.kingTwoX = kingTwoX;
		this.kingTwoY = kingTwoY;
		this.turnCounter = turnCounter;
		this.turnText = turnText;
		this.text = text;
	}
	
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	public boolean[] getTakenCards() {
		return takenCards;
	}
	public int[][] getFields() {
		return fields;
	}
	public int[][] getPower() {
		return power;
	}
	public int getKingOneX() {
		return kingOneX;
	}
	public int getKingOneY() {
		return kingOneY;
	}
	public int getKingTwoX() {
		return kingTwoX;
	}
	public int getKingTwoY() {
		return kingTwoY;
	}
	public int getTurnCounter() {
		return turnCounter;
	}
	public int getTurnText() {
		return turnText;
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
