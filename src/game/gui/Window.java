package game.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import game.Game;

public class Window extends JFrame {

	private static final long serialVersionUID = -7801504305278205215L;
	
	private Game game;
	
	public JButton btn1;
	public JButton btn2;
	public JButton btn3;
	public JButton btn4;
	public JLabel textBox;

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
		btn1.setBounds(900, 0, 300, 300);
		add(btn1);
		
		btn2 = new JButton("btn2");
		btn2.setBounds(1200, 0, 300, 300);
		add(btn2);
		
		btn3 = new JButton("btn1");
		btn3.setBounds(900, 300, 300, 300);
		add(btn3);
		
		btn4 = new JButton("btn1");
		btn4.setBounds(1200, 300, 300, 300);
		add(btn4);
		
		textBox = new JLabel("ovo je messageBox");
		textBox.setBounds(900, 600, 600, 300);
		add(textBox);
		
		
	}
	
	class Listener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			game.close();
		}
	}
}
