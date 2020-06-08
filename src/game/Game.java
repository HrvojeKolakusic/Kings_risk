package game;

import javax.swing.JOptionPane;

import game.gui.GameWindow;
import game.gui.Window;

public abstract class Game {

	public static final int PORT = 55555;
	
	public static final int X = 900;
	public static final int Y = 900;
	public static final int FIELD_X = X / 5;
	public static final int FIELD_Y = Y / 5;
	public static final int NOBODY = 0;
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = 2;
	public static final int KING_ONE = 3;
	public static final int KING_TWO = 4;
	
	protected int[][] fields;
	protected int[][] power;
	
	protected int kingOneX;
	protected int kingOneY;
	protected int kingTwoX;
	protected int kingTwoY;
	
	private Window window;
	protected GameWindow gameWindow;
	protected int currentPlayer;
	protected int thisPlayer;
	
	public Game(int thisPlayer) {
		this.thisPlayer = thisPlayer;
		window = new Window(this, "Zavrsni", X, Y);
		gameWindow = new GameWindow(this);
		fields = new int[5][5];
		power = new int[5][5];
		kingOneX = 0;
		kingOneY = 0;
		kingTwoX = 4;
		kingTwoY = 4;
		fields[0][0] = KING_ONE;
		fields[4][4] = KING_TWO;
		window.add(gameWindow);
		currentPlayer = Game.PLAYER_ONE;
		window.setVisible(true);
		
	}
	
	public abstract void inputReceived(int x, int y);
	public abstract void packetReceived(Object object);
	public abstract void close();
	
	protected void showWinner(int winner) {
		JOptionPane.showMessageDialog(null, "kraj"); // update
	}
	
	protected boolean isMyTurn() {
		if (thisPlayer == currentPlayer) return  true;
		else return false;
	}
	
	public int[][] getFields() {
		return fields;
	}
}
