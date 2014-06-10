package ca.ab.cbe.wahs.ajw;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;

	public Board board;
	public Canvas canvas;
	public Mouse mouse;

	/** Whether or not step() is being called every x ticks */
	public boolean ticking = false;
	public boolean gameRunning = false;
	public final Dimension SIZE = new Dimension(800, 631);

	public int ticks = 0;
	public static int tileSize = 15;
	public static int speed = 4; //The number of ticks to wait in between each iteration (While advancing)

	private static int buttonWidth = 140;
	private static int buttonHeight = 80;
	//@formatter:off
	public static Button[] buttons = new Button[] { 
			new Button(630, 15, buttonWidth, buttonHeight, "Run"), 			//0
			new Button(630, 115, buttonWidth, buttonHeight, "Step"), 		//1
			new Button(630, 215, buttonWidth, buttonHeight / 2, "Speed: " + (16 - speed)),//2
			new Button(630, 265, buttonWidth, buttonHeight / 2, "LWSS"), 	//3
			new Button(630, 315, buttonWidth, buttonHeight / 2, "Pulsar"), 	//4
			new Button(630, 365, buttonWidth, buttonHeight / 2, "GGG"), //5
			new Button(630, 415, buttonWidth, buttonHeight, "Clear"), 		//6
			new Button(630, 515, buttonWidth, buttonHeight, "Quit") }; 		//7
	//@formatter:on

	public Main() {
		super("The Game Of Life");

		board = new Board();
		mouse = new Mouse();
		canvas = new Canvas();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(SIZE);
		this.setMaximumSize(SIZE);
		this.setPreferredSize(SIZE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setFont(new Font("System", Font.PLAIN, 28));

		canvas.setMinimumSize(SIZE);
		canvas.setMaximumSize(SIZE);
		canvas.setPreferredSize(SIZE);
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		canvas.setVisible(true);

		this.add(canvas);
		this.setVisible(true);

		gameRunning = true;
		loop();
	}

	private void loop() {
		while (gameRunning) {
			update();
			render();

			try {
				Thread.sleep(32L); //30 ticks / second (ish)
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.dispose();
	}

	public void update() {
		ticks++;
		if (mouse.down) {
			updateBoard(mouse.x, mouse.y);
			mouse.down = false;
		}
		if (ticking) {
			if (ticks >= speed) {
				step();
				ticks = 0;
			}
		}
	}

	public void updateBoard(int mouseX, int mouseY) {
		if (mouseX >= 0 && mouseX <= 600) { //Mouse is in the game board area
			int row = board.getRow(mouseY);
			int column = board.getColumn(mouseX);

			board.cells[row][column] = !board.cells[row][column]; //Turn on if off, turn off if on
		} else { //Mouse is not in the game board area
			for (int i = 0; i < buttons.length; i++) {
				if (mouseX >= buttons[i].x && mouseX <= buttons[i].width + buttons[i].x && mouseY >= buttons[i].y
						&& mouseY <= buttons[i].height + buttons[i].y) {
					switch (i) {
					case 0: //Run / Pause
						ticking = !ticking;
						break;
					case 1: //Step
						ticking = false;
						step();
						break;
					case 2: //Speed
						speed = speed++ >= 15 ? 1 : speed++;
						break;
					case 3: //LWSS
						board.cells = toCells(Presets.LWSS);
						break;
					case 4: //Pulsar
						board.cells = toCells(Presets.pulsar);
						break;
					case 5: //Gosper Glider Gun
						board.cells = toCells(Presets.gosperGliderGun);
						break;
					case 6://Clear
						ticking = false;
						for (int y = 0; y < board.height; y++) {
							for (int x = 0; x < board.width; x++) {
								board.cells[y][x] = false;
							}
						}
						break;
					case 7://Quit
						System.exit(0);
						break;
					}
					buttons[2].title = "Speed: " + (16 - speed);
					buttons[0].title = ticking ? "Pause" : "Run";
				}
			}
		}
	}

	public boolean[][] toCells(int[][] coordinates) {
		boolean[][] newCells = new boolean[board.height][board.width];
		for (int k = 0; k < coordinates.length; k++) {
			int x = coordinates[k][0];
			int y = coordinates[k][1];
			newCells[y][x] = true;
		}
		return newCells;
	}

	public void step() {
		boolean[][] newCells = new boolean[board.height][board.width];

		for (int y = 0; y < board.height; y++) {
			for (int x = 0; x < board.width; x++) {
				int numberOfNeighbors = numberOfNeighbors(x, y, board.cells);
				if (board.cells[y][x]) { //Cell was alive on previous day
					newCells[y][x] = (numberOfNeighbors == 2 || numberOfNeighbors == 3); //Cell lives if it has 2 or 3 neighbours
				} else { //Cell was not alive on previous day
					newCells[y][x] = (numberOfNeighbors == 3); //Cell gets 'born' if it has 3 neighbours
				}
			}
		}
		board.cells = newCells;
	}

	public int numberOfNeighbors(int xpos, int ypos, boolean[][] cells) {
		int number = 0;
		for (int y = Math.max(0, ypos - 1); y < Math.min(ypos + 2, board.height); y++) {
			for (int x = Math.max(0, xpos - 1); x < Math.min(xpos + 2, board.width); x++) {
				if (cells[y][x]) {
					number++;
				}
			}
		}
		if (cells[ypos][xpos]) number--;
		return number;
	}

	public void render() {
		BufferStrategy buffer = canvas.getBufferStrategy();
		if (buffer == null) {
			canvas.createBufferStrategy(2);
			return;
		}

		Graphics g = buffer.getDrawGraphics();

		//Game board
		for (int y = 0; y < board.height; y++) {
			for (int x = 0; x < board.width; x++) {
				g.setColor(Color.GRAY);
				g.drawRect(x * tileSize + 1, y * tileSize + 1, tileSize, tileSize); //Gray grid
				g.setColor(board.cells[y][x] ? new Color(220, 220, 0) : new Color(0, 6, 20)); //Yellow if alive, Dark blue if dead
				g.fillRect(x * tileSize + 2, y * tileSize + 2, tileSize - 1, tileSize - 1);
			}
		}

		//Sidebar background
		g.setColor(Color.GRAY);
		g.fillRect(603, 1, 524, 749);

		//Buttons
		for (int i = 0; i < buttons.length; i++) {
			//Button
			g.setColor(new Color(180, 180, 180));
			g.fillRect(buttons[i].x, buttons[i].y, buttons[i].width, buttons[i].height);

			//Text
			FontMetrics fm = this.getFontMetrics(this.getFont());
			g.setFont(this.getFont());
			g.setColor(Color.BLACK);
			g.drawString(buttons[i].title, buttons[i].x + (buttons[i].width / 2)
					- (fm.stringWidth(buttons[i].title) / 2),
					buttons[i].y + (int) (buttons[i].height / 2) + fm.stringWidth(" "));
		}

		buffer.show();
		g.dispose();
	}

	public static void main(String[] args) {
		new Main();
	}

}
