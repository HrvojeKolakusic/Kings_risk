package game.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import game.Game;

/*
 * razred koji sluzi kao glavni prozor u koji stavljamo, izmedu ostalog, gameWindow
 */
public class Window extends JFrame {

	private static final long serialVersionUID = -7801504305278205215L;
	
	private Game game;
	
	public JButton btn1;
	public JButton btn2;
	public JButton btn3;
	public JButton btn4;
	public TextArea textBox;

	public Window(Game game, String title, int x, int y) {
		super(title);
		this.game = game;
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(x, y));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		addWindowListener(new Listener());
		
		btn1 = new JButton("btn1");
		btn1.setBounds(game.X, 0, 200, 200);
		btn1.setFont(new Font("Arial", Font.PLAIN, 16));
		add(btn1);
		
		btn2 = new JButton("btn2");
		btn2.setBounds(game.X + 200, 0, 200, 200);
		btn2.setFont(new Font("Arial", Font.PLAIN, 16));
		add(btn2);
		
		btn3 = new JButton("btn1");
		btn3.setBounds(game.X, 200, 200, 200);
		btn3.setFont(new Font("Arial", Font.PLAIN, 16));
		add(btn3);
		
		btn4 = new JButton("btn1");
		btn4.setBounds(game.X + 200, 200, 200, 200);
		btn4.setFont(new Font("Arial", Font.PLAIN, 16));
		add(btn4);
		
		textBox = new TextArea("Potez: 1\nTrenutni igrac: Plavi");
		textBox.setBounds(game.X, 400, 400, 400);
		textBox.setFont(new Font("Arial", Font.PLAIN, 20));
		textBox.setEditable(false);
		add(textBox);
		
		
	}
	
	class Listener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			game.close();
		}
	}
}
