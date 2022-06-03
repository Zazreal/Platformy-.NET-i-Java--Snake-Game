/**
 * 
 */

/**
 * @author Tomasz Nowak
 *	Used for the creation of Obstacles
 *	Obstacles can only be rectangles
 */
public class Obstacles {

	public int[] PosX = new int[2];
	public int[] PosY = new int[2];
	public int Length;
	public int Width;

	/**
	 * Constructor
	 * @param X - Spawn X position
	 * @param Y - Spawn Y position
	 * @param L - Length
	 * @param W - Width
	 * The parameters are processed so they fit in the grid
	 * Example: grid is every 30 units, Length is 67.
	 * The Length will be (always) rounded down, here to 60
	 */
	public Obstacles(int X, int Y, int L, int W) {
		PosX[0] = (int)(X/GamePanel.ElementSize) * GamePanel.ElementSize;
		PosY[0] = (int)(Y/GamePanel.ElementSize) * GamePanel.ElementSize;
		Length = (int)(L/GamePanel.ElementSize) * GamePanel.ElementSize;
		Width = (int)(W/GamePanel.ElementSize) * GamePanel.ElementSize;
		PosX[1] = PosX[0]+W;
		PosY[1] = PosY[0]+L;
	}

}
