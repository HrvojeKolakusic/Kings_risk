package game.packets;

import java.io.Serializable;

public class ClientCardPacket implements Serializable {

	private static final long serialVersionUID = -8847210896839442128L;
	
	private int[][] fields;
	private int[][] power;
	private int card;
	
	public ClientCardPacket(int[][] fields, int[][] power, int card) {
		this.fields = fields;
		this.power = power;
		this.card = card;
	}

	public int[][] getFields() {
		return fields;
	}

	public int[][] getPower() {
		return power;
	}
	
	public int getCard() {
		return card;
	}
}
