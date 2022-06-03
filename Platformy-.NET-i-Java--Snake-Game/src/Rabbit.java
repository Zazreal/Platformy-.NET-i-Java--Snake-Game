/**
 * 
 */
import java.util.Random;
import java.util.concurrent.TimeUnit;
/**
 * @author Tomasz Nowak
 *	Class containing the Rabbit.
 *	Has simple AI that runs away from the player + random movement
 *	Can be multithreaded
 */
public class Rabbit implements Entity, Runnable {

	int PosX;
	int PosY;
	int StepSize = GamePanel.ElementSize;
	public boolean ThreadActive = true;
	boolean isAlive;
	Snake Player;
	Random random;
	char direction = 'R';

	/**
	 * Constructor
	 * @param player - Reference to the player, so the Rabbit can escape properly
	 * @param StepSize - Size of its "jumps" (not implemented)
	 */
	public Rabbit(Snake player, int StepSize) {
		this.Player = player;
		random = new Random();
	}
	
	/**
	 * returns the X position
	 * @param i - does nothing, remnant of Entity
	 */
	@Override
	public int GetX(int i) {
		return PosX;
	}

	/**
	 * returns the Y position
	 * @param i - does nothing, remnant of Entity
	 */
	@Override
	public int GetY(int i) {
		return PosY;
	}
	
	/**
	 * returns the Name, used for Polymorphism
	 */
	@Override
	public String GetName()
	{
		return "Rabbit";
	}
	
	/**
	 * Spawns the rabbit in a random location
	 */
	public void SpawnRabbit()
	{
		PosX = random.nextInt((int)(GamePanel.ScreenWidth/GamePanel.ElementSize))*GamePanel.ElementSize;
		PosY = random.nextInt((int)(GamePanel.ScreenHeight/GamePanel.ElementSize))*GamePanel.ElementSize;
		isAlive = true;
	}
	
	/**
	 * Responsible for the rabbits movement
	 * Has checks to make sure it cant go out of bounds
	 * If the player is going towards the rabbit, it is set to try and run away
	 */
	public void move() {
		int TempPosX = PosX;
		int TempPosY = PosY;
		if(isAlive)
		{
			if(Player.direction[0] == 'L' && this.PosX-Player.GetX(0) < 0)
			{
				direction = 'L';
			}
			else if(Player.direction[0] == 'R' && this.PosX-Player.GetX(0) > 0)
			{
				direction = 'R';
			}
			else if(Player.direction[0] == 'U' && this.PosY-Player.GetY(0) > 0)
			{
				direction = 'U';
			}
			else if(Player.direction[0] == 'D' && this.PosY-Player.GetY(0) < 0)
			{
				direction = 'D';
			}else direction = '?';
			
			switch (direction) {
			case 'U':
				TempPosY = PosY - 2*StepSize;
				//One of the checks
				if(TempPosY < GamePanel.ScreenHeight) PosY = TempPosY;
				break;
			case 'D':
				TempPosY = PosY + 2*StepSize;
				if(TempPosY>0) PosY = TempPosY;
				break;
			case 'L':
				TempPosX = PosX - 2*StepSize;
				if(TempPosX>0) PosX = TempPosX;
				break;
			case 'R':
				TempPosX = PosX + 2*StepSize;
				if(TempPosX<GamePanel.ScreenHeight) PosX = TempPosX;
				break;
			default:
				RandomMovement(StepSize);
				break;
			}
			RandomMovement(StepSize);
		}
	}

	/**
	 * Runnable, Delay is 6*the GamePanel, because it jumps
	 */
	@Override
	public void run() {
		while(ThreadActive) {
			if(!GamePanel.IsPaused)move();
			try {
				TimeUnit.MILLISECONDS.sleep(GamePanel.Delay*6);
			}
			catch (Exception e) {
				System.out.println("Prey: " + e.toString());
			}
		}
	}
	
	/**
	 * Adds random movement to the rabbit, so it can wander and so it does not move straight
	 * @param Step - Distance to 'jump'
	 */
	private void RandomMovement(int Step)
	{
		int TempPosX = PosX;
		int TempPosY = PosY;
		if(random.nextInt(100) > 50)
		{
			TempPosX += Step;
			if(TempPosX < GamePanel.ScreenWidth) PosX = TempPosX;
		}
		if(random.nextInt(100) > 50)
		{
			TempPosX -= Step;
			if(TempPosX > 0) PosX = TempPosX;
		}
		if(random.nextInt(100) > 50)
		{
			TempPosY += Step;
			if(TempPosY < GamePanel.ScreenHeight) PosY = TempPosY;
		}
		if(random.nextInt(100) > 50)
		{
			TempPosY -= Step;
			if(TempPosY > 0) PosY = TempPosY;
		}
	}
}
