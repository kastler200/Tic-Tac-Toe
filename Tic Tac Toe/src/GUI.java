import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class GUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	int NUMROWS = 3;
	int XSCORE = 0;
	int OSCORE = 0;
	int NUMTURN = 0;
	Boolean currentTurn = true;
	Boolean compON = false;
	Boolean isWin = false;
	Short[][] gridVals;

	ImageIcon tilePressed = new ImageIcon(ImageIO.read(new File("bin/emoji.png")));
	ImageIcon defTile = new ImageIcon(ImageIO.read(new File("bin/blank.png")));
	ImageIcon xTile = new ImageIcon(ImageIO.read(new File("bin/X.png")));
	ImageIcon oTile = new ImageIcon(ImageIO.read(new File("bin/o.png")));

	JMenuBar mBar = new JMenuBar();;
	JMenu menu;
	JMenuItem newGameButton = new JMenuItem("New Game");
	JMenuItem pvpButton = new JMenuItem("Player vs. Player");
	JMenuItem pvcButton = new JMenuItem("Player vs. Computer");
	JMenuItem[] gridSizeMenu = new JMenuItem[4];
	JPanel bPanel;
	JPanel scorePanel = new JPanel(new BorderLayout());;
	JLabel xChar = new JLabel("  X wins: " + XSCORE);
	JLabel oChar = new JLabel("O wins: " + OSCORE + "  ");
	JLabel turnDisp = new JLabel("X is up!");
	ArrayList<ArrayList<JButton>> buttonGrid;

	public GUI() throws IOException{
		//setup graphics, add components

		//setup game menu
		menu = new JMenu("Game");
		mBar.add(menu);
		newGameButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		newGameButton.addActionListener(this);
		menu.add(newGameButton);


		//setup player menu
		menu = new JMenu("Players");
		mBar.add(menu);
		pvpButton.addActionListener(this);
		pvcButton.addActionListener(this);
		menu.add(pvpButton);
		menu.add(pvcButton);

		menu = new JMenu("Grid");
		mBar.add(menu);
		for (int i = 0; i < gridSizeMenu.length; i++) {
			gridSizeMenu[i] = (new JMenuItem((i+3) + " x " + (i+3)));
			gridSizeMenu[i].addActionListener(this);
			menu.add(gridSizeMenu[i]);
		}

		//update scoreboard
		updateScoreBoard();
		scorePanel.add(xChar, BorderLayout.WEST);
		scorePanel.add(oChar, BorderLayout.EAST);
		scorePanel.add(turnDisp, BorderLayout.CENTER);
		turnDisp.setHorizontalAlignment(SwingConstants.CENTER);

		//Setup button grid
		setupButtonGrid();
		resetGame();

		//set menu bar and finalize frame
		setTitle("Noughts and Crosses");
		setJMenuBar(mBar);
		add(scorePanel, BorderLayout.NORTH);
		setLocationRelativeTo(null);
		pack();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	//check for win after last grid value was changed at [q][p]. s denotes which player (1 = x and 2 = o)
	public void checkWin(int q, int p, short s) {
		int rowWin = 0;

		//immediate adjacent check
		for (int dy = -1; dy <= 1; dy ++) {
			for (int dx = -1; dx <= 1; dx++) {
				//check squares that are in bounds only
				int yTotal = (q + dy);
				int xTotal = (p + dx);
				
				if (yTotal >= 0 && yTotal < gridVals.length && xTotal >= 0 && xTotal < gridVals[q].length) {
					if (gridVals[yTotal][xTotal] == s) {

						//Check columns for matches
						if (dy == -1 || dy == 1) {
							for (int i = 0; i < NUMROWS; i++) {
								if (gridVals[i][p] == s) {
									rowWin++;
								}
							}

							if (rowWin == NUMROWS) {
								displayWin();
							}

							//Check rows for matches
						}else if (dx == -1 || dy == 1) {
							for (int j = 0; j < NUMROWS; j++) {
								if (gridVals[q][j] == s) {
									rowWin++;
								}
							}

							if (rowWin == NUMROWS) {
								displayWin();
							}

							//Check diagonal for matches
						}else {
							//top left to bottom right
							if (yTotal == xTotal) {
								for (int k = 0; k < NUMROWS; k++) {
									if (gridVals[k][k] == s) {
										rowWin++;
									}
								}

								if (rowWin == NUMROWS) {
									displayWin();
								}
							}
							
							rowWin = 0;
							//bottom left to top right
							if ((yTotal + xTotal) == (NUMROWS - 1)) {
								for (int l = 0; l < NUMROWS; l++) {
									if (gridVals[l][NUMROWS- (l + 1)] == s) {
										rowWin++;
									}
								}

								if (rowWin == NUMROWS) {
									displayWin();
								}
							}
						}
						//reset row count
						rowWin = 0;
					}
				}
			}
		}
	}

	public void updateScoreBoard() {
		xChar.setText("  X wins: " + XSCORE);
		oChar.setText("O wins: " + OSCORE + "  ");
		if (currentTurn == true) {
			turnDisp.setText("X is up");
		}else {
			turnDisp.setText("O is up");
		}
		
	}
	
	public void updateButton(int i, int j) {
		//change button and grid values
		if (currentTurn == true) {
			buttonGrid.get(i).get(j).setIcon(xTile);
			gridVals[i][j] = 1;
			NUMTURN++;
			if (NUMTURN > NUMROWS) {
				checkWin(i, j, gridVals[i][j]);
			}
			currentTurn ^= true;

		}else {
			buttonGrid.get(i).get(j).setIcon(oTile);
			gridVals[i][j] = 2;
			NUMTURN++;
			if (NUMTURN > NUMROWS) {
				checkWin(i, j, gridVals[i][j]);
			}
			currentTurn ^= true;
		}
		
		if (NUMTURN == (NUMROWS * NUMROWS) && isWin == false) {
			displayDraw();
		}
	}

	public void displayWin() {
		isWin = true;
		if (currentTurn == true) {
			XSCORE++;
			updateScoreBoard();
			JOptionPane.showMessageDialog(bPanel, "Crosses Wins!");
		}else {
			OSCORE++;
			updateScoreBoard();
			JOptionPane.showMessageDialog(bPanel, "Noughts Wins!");
		}
		
	}
	
	public void displayDraw() {
		JOptionPane.showMessageDialog(bPanel, "Draw!");
	}

	//reset grid values
	public void resetGame() {
		NUMTURN = 0;
		gridVals = new Short[NUMROWS][NUMROWS];
		isWin = false;

		for (int i = 0; i < gridVals.length; i ++) {
			for (int j = 0; j < gridVals[i].length; j++) {
				gridVals[i][j] = 0;
			}
		}
	}

	//Setup button grid
	public void setupButtonGrid() {
		if (bPanel != null) {
			remove(bPanel);
		}
	
		bPanel = new JPanel(new GridLayout(NUMROWS, NUMROWS));
		buttonGrid = new ArrayList<ArrayList<JButton>>();
		for (int q = 0; q < NUMROWS; ++q) {
			for (int w = 0; w < NUMROWS; ++w) {
				buttonGrid.add(new ArrayList<JButton>());
				buttonGrid.get(q).add(new JButton());
			}
		}
		for (int i = 0; i < NUMROWS; ++i) {
			for (int j = 0; j < NUMROWS; ++j) {
				buttonGrid.get(i).get(j).setIcon(defTile);
				buttonGrid.get(i).get(j).setBorder(null);
				buttonGrid.get(i).get(j).addActionListener(this);
				buttonGrid.get(i).get(j).setPressedIcon(tilePressed);
				bPanel.add(buttonGrid.get(i).get(j));
			}
		}
		add(bPanel, "Center");
		pack();
		setLocationRelativeTo(null);
	}

	//Handle button events
	public void actionPerformed(ActionEvent e) {
	
		//new game button
		if(e.getSource() == newGameButton) {
			setupButtonGrid();
			resetGame();
			updateScoreBoard();
		}else if (e.getSource() == pvpButton) {
			compON = false;
		}else if (e.getSource() == pvcButton) {
			compON = true;
		}
		else {
			//iterate over grid size menu for event
			for (int i = 0; i < gridSizeMenu.length; i++) {
				if (e.getSource() == gridSizeMenu[i]) {
					NUMROWS = (i + 3);
					NUMROWS = (i + 3);
					setupButtonGrid();
					resetGame();
					updateScoreBoard();
					if (NUMROWS != 3) {
						compON = false;
						JOptionPane.showMessageDialog(bPanel, "AI only works on 3x3");
					}
				}
			}
	
			//iterate over button grid to check for event
			for (int i = 0; i < NUMROWS; ++i) {
				for (int j = 0; j < NUMROWS; ++j) {
					if (e.getSource() == buttonGrid.get(i).get(j) && gridVals[i][j] == 0) {
						//Player's actions done here
						updateButton(i, j);
						updateScoreBoard();
						
						//auto complete AI action here
						if (compON == true) {
							runAI();
						}
					}
				}
			}
		}
	}
	
	//Not implemented yet
	public void runAI() {
		int y = 0;
		int x = 0;
		
		//check all played positions
		//Need to check if first position is corner, side or middle. Then check which positions are open for play....etc
		for (int i = 0; i < NUMROWS; ++i) {
			for (int j = 0; j < NUMROWS; ++j) {
				
			}
		}
		
		updateButton(y, x);
		updateScoreBoard();
	}

	public static void main(String[] args) throws IOException {
		new GUI().setVisible(true);
	}
}
