package game.packets;

import java.io.Serializable;

public class GameEndPacket implements Serializable {

	private int winner;

	public GameEndPacket(int winner) {
		super();
		this.winner = winner;
	}

	public int getWinner() {
		return winner;
	}
	
	
}
