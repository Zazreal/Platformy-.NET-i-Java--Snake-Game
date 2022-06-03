import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Tomasz Nowak
 *	Class responsible for the generation of Fruits on the map
 *	Red and Orange fruits are beneficial, while the gray one is detrimental
 *	Can Run on a separate thread
 */
public class FruitGenerator implements Runnable {

	boolean[] IsSpawned = {false,false,false};
	public int FruitAmount = 3;
	int[] PosX = new int[3];
	int[] PosY = new int[3];
	Random random;
	String[] Colors = {"red", "orange", "gray"};
	
	public boolean ThreadActive = true;
	
	/**
	 * Constructor, FA is FruitAmount (not implemented here)
	 */
	FruitGenerator(int FA)
	{
		//FruitAmount=FA;
		random = new Random();
		PosX = new int[FruitAmount];
		PosY = new int[FruitAmount];
		IsSpawned = new boolean[FruitAmount];
		for(int i = 0; i<FruitAmount;i++)
		{
			IsSpawned[i] = false;
		}
	}
	
	/**
	 * Function generating the fruits
	 */
	public void GenerateFruits()
	{
		for(int i = 0; i<3;i++) {
			if(!IsSpawned[i])
			{
				PosX[i] = random.nextInt((int)(GamePanel.ScreenWidth/GamePanel.ElementSize))*GamePanel.ElementSize;
				PosY[i] = random.nextInt((int)(GamePanel.ScreenHeight/GamePanel.ElementSize))*GamePanel.ElementSize;
				IsSpawned[i] = true;
			}
		}
	}
	
	/**
	 * for multithreading
	 * the delay is equal to the GamePanel delay
	 */
	@Override
	public void run() {
		
		while(ThreadActive) {
			GenerateFruits();
			try {
				TimeUnit.MILLISECONDS.sleep(GamePanel.Delay);
			}
			catch (Exception e) {
				System.out.println("Fruit Generator: " + e.toString());
			}
		}
	}

	/**
	 * returns the X position of fruit
	 * @param i - index of the fruit
	 */
	public int GetX(int i) {
		return PosX[i];
	}

	/**
	 * returns the Y position of fruit
	 * @param i - index of the fruit
	 */
	public int GetY(int i) {
		return PosY[i];
	}

}