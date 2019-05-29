package modele;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Feu implements Steppable {
	
	private Environnement environnement;
	private int x, y;
	
	public Feu(Environnement environnement, int x, int y) {
		this.x = x;
		this.y = y;
		this.environnement = environnement;
	}
	
    @Override
    public void step(SimState simState) {
    	
    	Environnement environnement = (Environnement)simState;
    	
    	// Probabilité de propagation
    	// Le feu se propage si la probabilité vaut 0
    	
    	int probabilite = (int)(Math.random() * Constantes.PROBABILITE_PROPAGATION);
    	if (probabilite == 0) {
    		propager(environnement);
    	}
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
    	    	
    	if(propageable(environnement, x+1, y))
    		environnement.grille.setObjectLocation(new Feu(environnement, x+1, y), x+1, y);
    	if(propageable(environnement, x-1, y))
    		environnement.grille.setObjectLocation(new Feu(environnement, x-1, y), x-1, y);
    	if(propageable(environnement, x, y+1))
    		environnement.grille.setObjectLocation(new Feu(environnement, x, y+1), x, y+1);
    	if(propageable(environnement, x, y-1))
    		environnement.grille.setObjectLocation(new Feu(environnement, x, y-1), x, y-1);
    	
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
