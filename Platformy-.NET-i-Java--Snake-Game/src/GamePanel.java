
/**
 * 
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

//TO DO:
//2nd SNAKE

/**
 * @author Tomasz Nowak This class is responsible for running the game
 */
public class GamePanel extends JPanel {
	static final int ScreenWidth = 1600;
	static final int ScreenHeight = 800;
	static final int ElementSize = 50;
	static final int ChunkSize = (ScreenWidth * ScreenHeight) / (ElementSize * ElementSize);
	private File file = new File("HighScore.txt");

	static final int Delay = 150;

	boolean running = false;
	static boolean IsPaused = false;
	boolean ResetGame = false;
	boolean QuitGame = false;
	boolean SaveHighScore = true;

	static boolean DrawingPlayer = false;
	Snake PlayerSnake;
	char[] direction = { 'R', '\n' };

	int HighScore = 0;
	int FruitsEaten = 0;
	boolean IsFruitSpawned = false;
	FruitGenerator FGenerator;
	Rabbit rabbit;
	Obstacles[] Obstacles = { new Obstacles(ScreenWidth / 4, ScreenHeight / 2, 0, ScreenWidth / 2),
			new Obstacles(ChunkSize, Delay, Delay, ScreenWidth) };
	Obstacles Obstacle;

	Random random;
	Thread PlayerThread;
	Thread FruitGeneratorThread;
	Thread RabbitThread;

	/**
	 * Constructor, sets up the Panel size, starts the random number generation and
	 * activates User Input
	 */
	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(ScreenWidth, ScreenHeight));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new UserInputAdapter());
	}

	/**
	 * Main loop of the Game. Does everything.
	 * Creates the necessary objects like the snake and the generator.
	 * Creates Threads for the said objects.
	 * Generates random obstacle.
	 * Has delay of GamePanel Delay
	 */
	public void startGame() {
		// Creating objects
		PlayerSnake = new Snake(3, 1000, direction, ElementSize);
		FGenerator = new FruitGenerator(3);
		rabbit = new Rabbit(PlayerSnake, ElementSize);

		rabbit.SpawnRabbit();

		// Creating Threads
		PlayerThread = new Thread(PlayerSnake, "PlayerThread");
		RabbitThread = new Thread(rabbit, "RabbitThread");
		FruitGeneratorThread = new Thread(FGenerator, "FruitThread");

		//Generating Obstacle
		Obstacle = Obstacles[0];
		if (random.nextInt() > 50)
			Obstacle = Obstacles[1];
		
		// starting threads
		PlayerThread.start();
		FruitGeneratorThread.start();
		RabbitThread.start();

		running = true;
		//Runs until the player wants to quit
		while (!QuitGame) {
			
			//This part is for when the game resets
			//It resets all the values to the default ones
			//and restarts everything that is necessary
			if (ResetGame) {
				ResetGame();
			}
			//main loop
			//checks collisions and draws the graphics
			
			if (running) {
				paintComponent(getGraphics());
				CheckCollisions();
			}
			repaint();
			try {
				TimeUnit.MILLISECONDS.sleep(GamePanel.Delay);
			} catch (Exception e) {
				System.out.println("Main: " + e.toString());
			}
		}
		QuitGame = true;
	}

	/**
	 * Resets the game state to the default one.
	 * Basically repeats the first part of startGame()
	 */
	private void ResetGame()
	{
		System.out.println("Game Reseting\n");
		PlayerSnake.ThreadActive = false;
		rabbit.ThreadActive = false;
		FruitsEaten = 0;
		ResetGame = false;
		running = true;
		SaveHighScore = true;
		direction[0] = 'R';
		// FGenerator.ThreadActive = false;
		try {
			PlayerThread.join();
			RabbitThread.join();
			// FruitGeneratorThread.join();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		PlayerSnake = new Snake(3, 1000, direction, ElementSize);
		rabbit = new Rabbit(PlayerSnake, ElementSize);

		rabbit.SpawnRabbit();

		PlayerThread = new Thread(PlayerSnake);
		RabbitThread = new Thread(rabbit);

		PlayerSnake.ThreadActive = true;
		rabbit.ThreadActive = true;
		// FGenerator.ThreadActive = true;

		PlayerThread.start();
		// FruitGeneratorThread.start();
		RabbitThread.start();

		Obstacle = Obstacles[0];
		if (random.nextInt() > 50)
			Obstacle = Obstacles[1];
		
		System.out.println("Game Reset\n");
	}
	
	/**
	 * Paints everything,
	 * checks for whether the game is paused or not and 
	 * draws the appropriate elements.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!IsPaused)
			draw(g);
		else if (IsPaused) {
			DrawPauseScreen(g);
		}
	}

	/**
	 * The main drawing function.
	 * Draws all the main game elements like the Snake, the Fruits etc.
	 * @param g - Graphics for drawing
	 */
	public void draw(Graphics g) {

		if (running) {

			// draws lines (the grid)
			for (int i = 0; i < ScreenWidth / ElementSize; i++) {
				g.drawLine(i * ElementSize, 0, i * ElementSize, ScreenHeight);
				g.drawLine(0, i * ElementSize, ScreenWidth, i * ElementSize);
			}

			// Draws The Rabbit
			if (rabbit.isAlive)
				g.setColor(Color.white);
			else
				g.setColor(Color.black);
			g.fillRect(rabbit.PosX, rabbit.PosY, ElementSize, ElementSize);

			// Draws the Fruits with proper colors
			g.setColor(Color.red);
			Color color;
			for (int i = 0; i < FGenerator.FruitAmount; i++) {
				try {
					Field field = Class.forName("java.awt.Color").getField(FGenerator.Colors[i]);
					color = (Color) field.get(null);
				} catch (Exception e) {
					color = Color.red; // Not defined
				}
				g.setColor(color);
				g.fillOval(FGenerator.PosX[i], FGenerator.PosY[i], ElementSize, ElementSize);
			}

			// Draws the Obstacle
			g.setColor(Color.gray);
			for (int i = Obstacle.PosX[0]; i <= Obstacle.PosX[1]; i += ElementSize) {
				for (int j = Obstacle.PosY[0]; j <= Obstacle.PosY[1]; j += ElementSize) {
					g.fillRect(i, j, ElementSize, ElementSize);
				}
			}

			// Draws the Snake
			DrawingPlayer = true;
			for (int i = 0; i < PlayerSnake.BodyLength; i++) {
				if (i == 0) {
					g.setColor(new Color(133, 87, 35));
					g.fillRect(PlayerSnake.GetX(i), PlayerSnake.GetY(i), ElementSize, ElementSize);
				} else {
					g.setColor(new Color(165, 113, 68));
					g.fillRect(PlayerSnake.GetX(i), PlayerSnake.GetY(i), ElementSize, ElementSize);
				}
			}
			DrawingPlayer = false;
			
			//Draws the Score
			g.setColor(Color.red);
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
			g.drawString("Score: " + FruitsEaten, 100, ScreenHeight - 25);
			
		} else {
			
			try {
				//The Game over screen
				GameOver(g);
			} catch (Exception e) {
				System.out.println("GameOver:" + e.toString());
			}
		}

	}

	/**
	 * When the game is paused, this function draws the Pause Screen
	 * @param g - Graphics for drawing
	 */
	public void DrawPauseScreen(Graphics g) {
		g.setColor(Color.PINK);
		g.setFont(new Font("Arial", Font.BOLD, 150));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Game Paused", (ScreenWidth - metrics1.stringWidth("Game Paused")) / 2, ScreenHeight / 2);

		g.setColor(Color.PINK);
		g.setFont(new Font("Arial", Font.BOLD, 50));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Press \' Enter \' to continue",
				(ScreenWidth - metrics2.stringWidth("Press \' Enter \' to continue")) * 2 / 5, ScreenHeight * 4 / 6);
	}

	/**
	 * Draws the GameOver screen and handles the High Score.
	 * First it reads from the .txt File, then compares the scores and saves the higher one
	 * The game can be quit from this screen.
	 * @param g - Graphics for drawing
	 * @throws IOException - because file operations
	 */
	public void GameOver(Graphics g) throws IOException {
		// Score
		if (SaveHighScore) {
			String temp;
			Scanner scan = new Scanner(file);
			// Load it and shit
			while (scan.hasNextLine()) {
				temp = scan.nextLine();
				HighScore = Integer.parseInt(temp);
			}
			if (FruitsEaten > HighScore) {
				HighScore = FruitsEaten;
				FileWriter writer = new FileWriter("HighScore.txt");
				temp = Integer.toString(HighScore);
				writer.write(temp);
				writer.close();

			}

			SaveHighScore = false;
		}
		g.setColor(Color.red);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: " + FruitsEaten + ", High Score: " + HighScore,
				(ScreenWidth - metrics1.stringWidth("Score: " + FruitsEaten + ", High Score: " + HighScore)) / 2,
				g.getFont().getSize());

		// Game Over text
		g.setColor(Color.red);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (ScreenWidth - metrics2.stringWidth("Game Over")) / 2, ScreenHeight / 2);

		// PlayAgain = new JButton("PlayAgainButton");
		g.setColor(Color.red);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press \'Enter\' to to Play Again?",
				(ScreenWidth - metrics3.stringWidth("Press \'Enter\' to Again?")) / 2, ScreenHeight * 3 / 4);

		g.setColor(Color.red);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
		FontMetrics metrics4 = getFontMetrics(g.getFont());
		g.drawString("Press \'ESC\' to Quit", (ScreenWidth - metrics4.stringWidth("Press \'ESC\' to Quit")) / 2,
				ScreenHeight * 6 / 7);
		// Check wether the user pressed on the button

	}

	/**
	 * Checks whether the fruits were eaten and acts appropriately
	 */
	public void CheckFruits() {
		for (int i = 0; i < FGenerator.FruitAmount; i++) {
			if ((PlayerSnake.GetX(0) == FGenerator.PosX[i]) && (PlayerSnake.GetY(0) == FGenerator.PosY[i])) {

				if (FGenerator.Colors[i] == "gray") {
					FruitsEaten--;
					PlayerSnake.BodyLength--;
				} else {
					FruitsEaten++;
					PlayerSnake.BodyLength++;
				}
				FGenerator.IsSpawned[i] = false;
			}
		}
	}

	/**
	 * Checks whether the Rabbit was eaten and acts appropriately
	 * The Rabbit does not respawn
	 */
	public void CheckRabbit() {
		if ((PlayerSnake.GetX(0) == rabbit.PosX) && (PlayerSnake.GetY(0) == rabbit.PosY) && rabbit.isAlive) {

			PlayerSnake.BodyLength += 2;
			FruitsEaten += 2;
			rabbit.isAlive = false;
		}
	}

	/**
	 * Checks for collisions with the obstacles
	 * @param OB - The obstacle to check
	 * @param EN - The Entity to check collision with
	 */
	public void CheckObstacles(Obstacles OB, Entity EN) {
		int ENX = EN.GetX(0);
		int ENY = EN.GetY(0);
		if (ENX >= OB.PosX[0] && ENY >= OB.PosY[0])
			if (ENX <= OB.PosX[1] && ENY <= OB.PosY[1]) {
				{
					if (EN.GetName() == "Snake")
						running = false;
					if (EN.GetName() == "Rabbit")
						((Rabbit) EN).SpawnRabbit();
				}
			}
	}

	/**
	 * Checks whether any fruits spawned inside the Obstacle
	 * @param OB - Obstacle to check
	 */
	public void CheckFruitSpawn(Obstacles OB) {
		for (int i = 0; i < FGenerator.FruitAmount; i++) {
			if (FGenerator.PosX[i] >= OB.PosX[0] && FGenerator.PosY[i] >= OB.PosY[0])
				if (FGenerator.PosX[i] <= OB.PosX[1] && FGenerator.PosY[i] <= OB.PosY[1]) {
					{
						FGenerator.IsSpawned[i] = false;
						// if(EN.GetName() == "Rabbit") ((Rabbit)EN).isAlive = false;
					}
				}
		}
	}

	/**
	 * Checks all collisions,uses:
	 * CheckFruits(),
	 * CheckRabbit(),
	 * CheckObstacles() on Player and Rabbit,
	 * CheckFruitSpawn()
	 * Then checks if the player crossed the Panel boundaries
	 */
	public void CheckCollisions() {
		// checks if head collides with body
		CheckFruits();
		CheckRabbit();
		DrawingPlayer = true;
		CheckObstacles(Obstacle, PlayerSnake);
		CheckObstacles(Obstacle, rabbit);
		DrawingPlayer = false;
		CheckFruitSpawn(Obstacle);
		// CheckObstacles(Obstacle, rabbit);

		for (int i = PlayerSnake.BodyLength; i > 0; i--) {
			if ((PlayerSnake.GetX(0) == PlayerSnake.GetX(i)) && (PlayerSnake.GetY(0) == PlayerSnake.GetY(i))) {
				running = false;
			}
		}

		if (FruitsEaten < 0)
			running = false;
		// check if head touches left border
		if (PlayerSnake.GetX(0) < 0) {
			running = false;
		}
		// check if head touches right border
		if (PlayerSnake.GetX(0) > ScreenWidth) {
			running = false;
		}
		// check if head touches top border
		if (PlayerSnake.GetY(0) < 0) {
			running = false;
		}
		// check if head touches bottom border
		if (PlayerSnake.GetY(0) > ScreenHeight) {
			running = false;
		}
		// Add collision For Obstacles

	}

	/**
	 * 
	 * @author Tomasz Nowak
	 *	Class responsible for User Input
	 */
	public class UserInputAdapter extends KeyAdapter {

		/**
		 *  Function responsible for user input
		 *  runs on a separate thread, changes direction[0] to proper direction
		 *  also handles the input for stopping, resuming and quitting of the game
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			// Left
			case KeyEvent.VK_A:
				if (direction[0] != 'R') {
					direction[0] = 'L';
				}
				break;
			case KeyEvent.VK_LEFT:
				if (direction[0] != 'R') {
					direction[0] = 'L';
				}
				break;

			// Right
			case KeyEvent.VK_D:
				if (direction[0] != 'L') {
					direction[0] = 'R';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction[0] != 'L') {
					direction[0] = 'R';
				}
				break;

			// Up
			case KeyEvent.VK_W:
				if (direction[0] != 'D') {
					direction[0] = 'U';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction[0] != 'D') {
					direction[0] = 'U';
				}
				break;

			// Down
			case KeyEvent.VK_S:
				if (direction[0] != 'U') {
					direction[0] = 'D';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction[0] != 'U') {
					direction[0] = 'D';
				}
				break;
			case KeyEvent.VK_ESCAPE:
				if (!running)
					QuitGame = true;
				IsPaused = true;
				break;

			case KeyEvent.VK_ENTER:
				if (!running && !IsPaused)
					ResetGame = true;
				IsPaused = false;
				break;
			}
		}
	}
}
