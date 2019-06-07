package modele;

public class Mur extends Inerte {

	private int x, y;
	
    public Mur(int x, int y) {
    	this.x = x;
    	this.y = y;
        setInfranchissable();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
