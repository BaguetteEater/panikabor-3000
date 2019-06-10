package modele;

public class Mur extends Inerte {
	
    public Mur(int x, int y) {
    	super(x, y);
        setInfranchissable();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
