package modele;

public class TerrainBrule extends Superposable {
	
	public TerrainBrule(int x, int y) {
		super(x, y);
		this.x = x;
		this.y = y;
		setTaille(0);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
