import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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

	//game variables
	int NUMROWS = 3;
	int XSCORE = 0;
	int OSCORE = 0;
	int NUMTURN = 0;
	Boolean currentTurn = true;
	Boolean compON = true;
	Boolean isWin = false;
	Short[][] gridVals;

	//AI variables
	Random random = new Random();
	int AILOSECHANCE = 2;
	boolean isCorner = false;
	int lastX = 0;
	int lastY = 0;
	int y = 0;
	int x = 0;

	//GUI variables
	ImageIcon tilePressed = new ImageIcon(ImageIO.read(new File("lib/emoji.png")));
	ImageIcon defTile = new ImageIcon(ImageIO.read(new File("lib/blank.png")));
	ImageIcon xTile = new ImageIcon(ImageIO.read(new File("lib/X.png")));
	ImageIcon oTile = new ImageIcon(ImageIO.read(new File("lib/o.png")));
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
		pack();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
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
			lastY = i;
			lastX = j;
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
		setupButtonGrid();
		resetGame();

	}

	public void displayDraw() {
		JOptionPane.showMessageDialog(bPanel, "Draw!");
		updateScoreBoard();
		setupButtonGrid();
		resetGame();
	}

	//reset grid values
	public void resetGame() {
		NUMTURN = 0;
		gridVals = new Short[NUMROWS][NUMROWS];
		isWin = false;
		isCorner = false;
		x = 0;
		y = 0;
		lastY = 0;
		lastX = 0;

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
						if (compON == true && currentTurn == false) {
							runAI();
						}
					}
				}
			}
		}
	}

	//checks if AI needs to block player win 
	public boolean aiCheckPlayer() {
		boolean spotFound = false;
		int line = 0;
		//horizontal check
		for (int i = 0; i < NUMROWS; i++) {
			line = 0;
			for (int j = 0; j < NUMROWS; j++) {
				if (gridVals[i][j] == 1) {
					line++;
				}
			}
			if (line > 1) {
				for (int k = 0; k < NUMROWS; k++) {
					if (gridVals[i][k] == 0) {
						y = i;
						x = k;
						spotFound = true;
					}
				}
				if (spotFound) {
					break;
				}
			}
		}

		//vertical check
		for (int i = 0; i < NUMROWS; i++) {
			line = 0;
			for (int j = 0; j < NUMROWS; j++) {
				if (gridVals[j][i] == 1) {
					line++;
				}
			}
			if (line > 1) {
				for (int k = 0; k < NUMROWS; k++) {
					if (gridVals[k][i] == 0) {
						y = k;
						x = i;
						spotFound = true;
					}
				}
				if (spotFound) {
					break;
				}
			}
		}

		//diagonal L to R
		line = 0;
		for (int i = 0; i < NUMROWS; i++) {
			if (gridVals[i][i] == 1) {
				line++;
			}

			if (line > 1) {
				for (int j = 0; j < NUMROWS; j++) {
					if (gridVals[j][j] == 0) {
						y = j;
						x = j;
						spotFound = true;
					}
				}
				if (spotFound) {
					break;
				}
			}
		}

		//diagnoal R to L
		line = 0;
		if (gridVals[0][2] == 1) {
			line++;
		}
		if (gridVals[1][1] == 1) {
			line++;
		}
		if (gridVals[2][0] == 1) {
			line++;
		}

		if (line > 1) {
			if (gridVals[0][2] == 0) {
				x = 2;
				y = 0;
				spotFound = true;
			}
			if (gridVals[1][1] == 0) {
				x = 1;
				y = 1;
				spotFound = true;
			}
			if (gridVals[2][0] == 0) {
				x = 0;
				y = 2;
				spotFound = true;
			}
		}

		return spotFound;
	}

	//checks if AI can play a winning spot
	public boolean aiCheckSelf() {
		boolean spotFound = false;
		//horizontal check
		for (int i = 0; i < NUMROWS; i++) {
			int line = 0;
			for (int j = 0; j < NUMROWS; j++) {
				if (gridVals[i][j] == 2) {
					line++;
				}
			}
			if (line > 1) {
				for (int k = 0; k < NUMROWS; k++) {
					if (gridVals[i][k] == 0) {
						y = i;
						x = k;
						spotFound = true;
					}
				}
				if (spotFound) {
					break;
				}
			}
		}

		//vertical check
		for (int i = 0; i < NUMROWS; i++) {
			int line = 0;
			for (int j = 0; j < NUMROWS; j++) {
				if (gridVals[j][i] == 2) {
					line++;
				}
			}
			if (line > 1) {
				for (int k = 0; k < NUMROWS; k++) {
					if (gridVals[k][i] == 0) {
						y = k;
						x = i;
						spotFound = true;
					}
				}
				if (spotFound) {
					break;
				}
			}
		}

		//diagonal L to R
		for (int i = 0; i < NUMROWS; i++) {
			int line = 0;
			if (gridVals[i][i] == 2) {
				line++;
			}

			if (line > 1) {
				for (int j = 0; j < NUMROWS; j++) {
					if (gridVals[j][j] == 0) {
						y = j;
						x = j;
						spotFound = true;
					}
				}
				if (spotFound) {
					break;
				}
			}
		}

		//diagnoal R to L
		int line = 0;
		if (gridVals[0][2] == 2) {
			line++;
		}
		if (gridVals[1][1] == 2) {
			line++;
		}
		if (gridVals[2][0] == 2) {
			line++;
		}

		if (line > 1) {
			if (gridVals[0][2] == 0) {
				x = 2;
				y = 0;
				spotFound = true;
			}
			if (gridVals[1][1] == 0) {
				x = 1;
				y = 1;
				spotFound = true;
			}
			if (gridVals[2][0] == 0) {
				x = 0;
				y = 2;
				spotFound = true;
			}
		}
		return spotFound;
	}

	//Not implemented yet
	public void runAI() {
		switch (NUMTURN) {

		//case = each turn - did not implement AI going first
		case 0:
			int n = random.nextInt(4);
			switch(n) {
			case 1:
				x = 2;
				break;
			case 2:
				y = 2;
				break;
			case 3:
				x = 2;
				y = 2;
				break;
			}
			break;
		case 1:
			//corner spots
			if ((lastY == 0 && lastX == 0) || (lastY == 2 && lastX == 2) || (lastY == 0 && lastX == 2) || (lastY == 2 && lastX == 0)) {
				//take center spot
				x = 1;
				y = 1;
				isCorner = true;

				//chance to make a losing move
				if (random.nextInt(AILOSECHANCE) == 0) {
					for (int i = 0; i < NUMROWS; i ++) {
						for (int j = 0; j < NUMROWS; j++) {
							if (gridVals[i][j] == 0) {
								y = i;
								x = j;
							}
						}
					}
				}
				//center spot or side spot
			}else if ((lastY == 1 && lastX == 1) || (lastY == 1 && lastX == 0) || (lastY == 1 && lastX == 2) || (lastY == 2 && lastX == 1) || (lastY == 0 && lastX == 1)) {
				//random corner spot
				n = random.nextInt(4);
				switch(n) {
				case 1:
					x = 2;
					break;
				case 2:
					y = 2;
					break;
				case 3:
					x = 2;
					y = 2;
					break;
				}
			}

			break;
		case 2:
			if (lastY == 1 && lastX == 1) {
				//if player chose center, choose the opposite diagonal tile from the original
				if (x == 2) {
					x = 0;
				}else {
					x = 2;
				}
				if (y ==2) {
					y = 0;
				}else {
					y = 2;
				}
			}else {
				//random corner spot
				boolean cornerFound = false;
				while (cornerFound == false) {
					n = random.nextInt(4);
					switch(n) {
					case 1:
						x = 2;
						y = 0;
						break;
					case 2:
						y = 2;
						x = 0;
						break;
					case 3:
						x = 2;
						y = 2;
						break;
					}
					if (gridVals[x][y] == 0) {
						cornerFound = true;
					}
				}
			}
			break;
		case 3:
			if (aiCheckPlayer() == false) {
				if (gridVals[2][1] == 0) {
					x = 1;
					y = 0;
				}else {
					x = 0;
					y = 1;
				}
			}

			break;
		case 4: case 5: case 6: case 7: case 8: case 9:
			if (aiCheckSelf() == false) {
				if (aiCheckPlayer() == false) {
					for (int i = 0; i < NUMROWS; i ++) {
						for (int j = 0; j < NUMROWS; j++) {
							if (gridVals[i][j] == 0) {
								y = i;
								x = j;
							}
						}
					}
				}
			}
			break;
		}

		if (gridVals[y][x] == 0) {
			updateButton(y, x);
			updateScoreBoard();
		}
	}

	public static void main(String[] args) throws IOException {
		new GUI().setVisible(true);
	}
}
