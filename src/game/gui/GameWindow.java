package game.gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import game.Game;
import game.res.Resources;

/*
 * razred sluzi kao glavni prozor na kojem se crta polje
 */
public class GameWindow extends JPanel {
	

	private static final long serialVersionUID = 8326219303276685725L;
	
	private Game game;
	
	private Resources res = new Resources();

	public GameWindow(Game game) {
		this.game = game;
		addMouseListener(new Input());
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setStroke(new BasicStroke(5));
		
		for (int x = Game.FIELD_X; x <= 4 * Game.FIELD_X; x += Game.FIELD_X) {
			g2D.drawLine(x, 0, x, Game.Y - 5);
		}
		
		for (int y = Game.FIELD_Y; y <= 4 * Game.FIELD_Y; y += Game.FIELD_Y) {
			g2D.drawLine(0, y, Game.Y, y);
		}
		
		for (int x = 0; x < 5; ++x) {
			for (int y = 0; y < 5; ++y) {
				int field = game.getFields()[x][y];
				if (field != Game.NOBODY) {
					g2D.drawImage(res.colors[field - 1], x * Game.FIELD_X + 5, y * Game.FIELD_Y + 5, Game.FIELD_X - 10, Game.FIELD_Y - 10, null);
					g2D.drawImage(res.numbers[game.getPower()[x][y] - 1], x * Game.FIELD_X + 5, y * Game.FIELD_Y + 5, 30, 30, null);
				}
			}
		}
	}
	
	class Input extends MouseAdapter {
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				game.inputReceived(e.getX() / Game.FIELD_X, e.getY() / Game.FIELD_Y);
			}
		}
	}
	
}
