package game.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.nio.file.Path;
//import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import game.ClientGame;
//import game.Game;
import game.ServerGame;
//import game.packets.LoadGamePacket;


/*
 * razred koji služi kao glavni izbornik igrice
 */
public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 852763954330865658L;
	
	public JButton serverBtn;
	public JButton clientBtn;
	public JButton loadBtn;
	
	public MainWindow() {
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(500, 500));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Main menu");

		
		serverBtn = new JButton("Start as server");
		serverBtn.setBounds(150, 50, 200, 100);
		add(serverBtn);
		
		clientBtn = new JButton("Join as client");
		clientBtn.setBounds(150, 200, 200, 100);
		add(clientBtn);
		
		loadBtn = new JButton("Load game");
		loadBtn.setBounds(150, 350, 200, 100);
		loadBtn.setEnabled(false);
		add(loadBtn);
		
		setVisible(true);
		
		serverBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port:"));
				new ServerGame(port);
				setVisible(false);
				
			}
		});
		
		clientBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = JOptionPane.showInputDialog("Enter IP:");
				int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port:"));
				new ClientGame(ip, port);
				setVisible(false);
				
			}
		});
		
		/*	kod koji bi sluzio za stvaranje servera i ucitavanje spremljene partije
		loadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port:"));
				
				Path path = Paths.get(System.getProperty("user.dir" + "/savedGame/savedGame.txt"));
				String line;
				String[] split;
				int currentPlayer = 0;
				boolean[] takenCards = new boolean[Game.MAX_CARDS];
				int[][] fields = new int[5][5];
				int[][] power = new int[5][5];
				int kingOneX = 0;
				int kingOneY = 0;
				int kingTwoX = 0;
				int kingTwoY = 0;
				int turnCounter = 0;
				int turnText = 0;
				String text = "";
				
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(
									new BufferedInputStream(
											new FileInputStream(path.toString()))));
					
					line = br.readLine();
					currentPlayer = Integer.parseInt(line);
					
					line = br.readLine();
					split = line.split(" ");
					for (int i = 0; i < Game.MAX_CARDS; ++i) {
						if (split[i].equals("0")) takenCards[i] = false;
						else takenCards[i] = true;
					}
					
					line = br.readLine();
					split = line.split(" ");
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							fields[i][j] = Integer.parseInt(split[i * 5 + j]);
						}
					}
					
					line = br.readLine();
					split = line.split(" ");
					for (int i = 0; i < 5; ++i) {
						for (int j = 0; j < 5; ++j) {
							power[i][j] = Integer.parseInt(split[i * 5 + j]);
						}
					}
					
					line = br.readLine();
					split = line.split(" ");
					kingOneX = Integer.parseInt(split[0]);
					kingOneY = Integer.parseInt(split[1]);
					
					line = br.readLine();
					split = line.split(" ");
					kingTwoX = Integer.parseInt(split[0]);
					kingTwoY = Integer.parseInt(split[1]);
					
					turnCounter = Integer.parseInt(br.readLine());
					turnText = Integer.parseInt(br.readLine());
					text = br.readLine();
					
					br.close();
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				LoadGamePacket packet = new LoadGamePacket(currentPlayer, takenCards, fields, power, kingOneX, 
						kingOneY, kingTwoX, kingTwoY, turnCounter, turnText, text);
				
				new ServerGame(packet, port);
				setVisible(false);
				
			}
		});
		*/
	}
}
