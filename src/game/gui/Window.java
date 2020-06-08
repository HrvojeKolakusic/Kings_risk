package game.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import game.Game;

public class Window extends JFrame {

	private Game game;

	public Window(Game game, String title, int x, int y) {
		super(title);
		this.game = game;
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(x, y));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		addWindowListener(new Listener());	
	}
	
	class Listener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			game.close();
		}
	}
}
