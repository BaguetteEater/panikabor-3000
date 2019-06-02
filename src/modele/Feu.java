package modele;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Feu extends Superposable implements Steppable {
	
	private int x, y;
	
	public Feu(int x, int y) {
		this.x = x;
		this.y = y;
		setTaille(0);
	}
	
    @Override
    public void step(SimState simState) {
    	
    	Environnement environnement = (Environnement)simState;
    	
    	// Probabilité de propagation
    	// Le feu se propage si la probabilité vaut 0
    	propager(environnement);
    }
    
    
    private boolean propageable(Environnement environnement, int x, int y) {
    	if (environnement.grille.getObjectsAtLocation(x, y) == null) {
			return true;
		}
    	for(Object object : environnement.grille.getObjectsAtLocation(x, y).objs) {
    		if(object instanceof Feu || object instanceof Mur) return false;
    	}
    	return true;
    }
    
    private void propager(Environnement environnement) {

		int probabilite = (int)(Math.random() * Constantes.PROBABILITE_PROPAGATION);

    	if(propageable(environnement, this.x+1, this.y) && probabilite == 0)
    		environnement.ajoutFeu(this.x+1, this.y);

		probabilite = (int)(Math.random() * Constantes.PROBABILITE_PROPAGATION);

    	if(propageable(environnement, x-1, y) && probabilite == 0)
			environnement.ajoutFeu(this.x-1, this.y);

		probabilite = (int)(Math.random() * Constantes.PROBABILITE_PROPAGATION);

    	if(propageable(environnement, x, y+1) && probabilite == 0)
			environnement.ajoutFeu(this.x, this.y+1);

		probabilite = (int)(Math.random() * Constantes.PROBABILITE_PROPAGATION);

    	if(propageable(environnement, x, y-1) && probabilite == 0)
			environnement.ajoutFeu(this.x, this.y-1);
    	
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
