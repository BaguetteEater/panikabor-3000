package modele;

public class TerrainBrule extends Superposable {
	
	private int x, y;
	
	public TerrainBrule(int x, int y) {
		this.x = x;
		this.y = y;
		setTaille(0);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
