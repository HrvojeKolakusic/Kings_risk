package game.packets;

import java.io.Serializable;

public class UpdatePacket implements Serializable {
	

	private static final long serialVersionUID = 6767077442609585034L;
	
	private int[][] fields;
	private int[][] power;
	private int currentPlayer;
	private int kingOneX;
	private int kingOneY;
	private int kingTwoX;
	private int kingTwoY;
	
	public UpdatePacket(int[][] fields, int[][] power, int currentPlayer, int kingOneX, int kingOneY, int kingTwoX, int kingTwoY) {
		this.fields = fields;
		this.power = power;
		this.currentPlayer = currentPlayer;
		this.kingOneX = kingOneX;
		this.kingOneY = kingOneY;
		this.kingTwoX = kingTwoX;
		this.kingTwoY = kingTwoY;
	}

	public int[][] getFields() {
		return fields;
	}
	
	public int[][] getPower() {
		return power;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
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
	
	

}
