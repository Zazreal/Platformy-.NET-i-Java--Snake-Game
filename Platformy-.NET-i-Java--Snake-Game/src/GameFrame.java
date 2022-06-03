/**
 * 
 */

import javax.swing.JFrame;
/**
 * @author Tomasz Nowak
 * GameFrame is olny responsible for setting up the Window.
 * The Game is run in GamePanel
 */
public class GameFrame extends JFrame
{
	GameFrame()
	{
		this.setTitle("Snake");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		GamePanel Game = new GamePanel();;
		this.add(Game);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		Game.startGame();
		this.setVisible(false);
		this.dispose(); 
	}

}