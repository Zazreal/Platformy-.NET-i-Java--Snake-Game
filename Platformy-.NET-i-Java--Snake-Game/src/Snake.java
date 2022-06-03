import java.util.concurrent.TimeUnit;
/**
 * @author Tomasz Nowak
 *	Class Implementing the Player Snake
 */
public class Snake implements Entity, Runnable {

	public int BodyLength;

	private int[] BodyX;
	private int[] BodyY;

	public char[] direction;

	private int StepSize;
	public boolean ThreadActive = true;
	
	/**
	 * 
	 * @param Length - Body Length of the Snake
	 * @param Size - Size of array containing the body
	 * @param dir - reference to the direction that's taken from the GamePanel's InputAdapted class
	 * @param StepSize - the amount the snake is to move every frame
	 */
	public Snake(int Length, int Size, char[] dir, int StepSize) {

		this.BodyLength = Length;
		this.StepSize = StepSize;
		direction = dir;
		//System.out.println("Dir: " + direction[0]);
		//System.out.println("StepSize" + StepSize);
		this.BodyX = new int[Size];
		this.BodyY = new int[Size];
		
		BodyX[0] = 3*StepSize;
		BodyY[0] = 3*StepSize;
	}

	/**
	 * for multithreading
	 * the delay is half of GamePanel's delay
	 */
	@Override
	public void run() {

		while(ThreadActive) {
			if(!GamePanel.DrawingPlayer && !GamePanel.IsPaused)move();
			GamePanel.DrawingPlayer=true;
			try {
				TimeUnit.MILLISECONDS.sleep(GamePanel.Delay/2);
			}
			catch (Exception e) {
				System.out.println("SNAKE: " + e.toString());
			}
		}
	}

	/**
	 * uses the direction to move the snake by the StepSize
	 * in the proper direction
	 */
	@Override
	public void move() {
		for (int i = BodyLength; i > 0; i--) {
			BodyX[i] = BodyX[i - 1];
			BodyY[i] = BodyY[i - 1];
		}

		switch (direction[0]) {
		case 'U':
			BodyY[0] = BodyY[0] - StepSize;
			break;
		case 'D':
			BodyY[0] = BodyY[0] + StepSize;
			break;
		case 'L':
			BodyX[0] = BodyX[0] - StepSize;
			break;
		case 'R':
			BodyX[0] = BodyX[0] + StepSize;
			break;
		}
	}

	/**
	 * @param i - index of body
	 * @return X Pos of Body index
	 */
	@Override
	public int GetX(int i) {
		return BodyX[i];
	}

	/**
	 * @param i - index of body
	 * @return Y Pos of Body index
	 */
	@Override
	public int GetY(int i) {
		return BodyY[i];
	}

	/**
	 * Used for Polymorphism
	 * @return "Snake"
	 */
	@Override
	public String GetName()
	{
		return "Snake";
	}
	
}
