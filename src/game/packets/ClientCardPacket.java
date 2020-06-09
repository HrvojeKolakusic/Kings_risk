package game.packets;

import java.io.Serializable;

public class ClientCardPacket implements Serializable {

	private static final long serialVersionUID = -8847210896839442128L;
	
	private int[][] fields;
	private int[][] power;
	
	public ClientCardPacket(int[][] fields, int[][] power) {
		this.fields = fields;
		this.power = power;
	}

	public int[][] getFields() {
		return fields;
	}

	public int[][] getPower() {
		return power;
	}
}
