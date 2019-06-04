package modele;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class Feu extends Superposable implements Steppable {
	
	private int x, y;
	private int dureeDeVie;
	private Stoppable stoppable;
	
	public Feu(int x, int y) {
		this.x = x;
		this.y = y;
		this.dureeDeVie = Constantes.DUREE_DE_VIE_FEU;
		setTaille(0);
	}
	
	@Override
	public void finalize( ) {
		System.out.println("je suis un feu mort");
	}
	
    @Override
    public void step(SimState simState) {
    	
    	Environnement environnement = (Environnement)simState;
    	
    	int probabiliteEteindre = (int) (Math.random()*Constantes.PROBABILITE_ETEINDRE); 
    	if(probabiliteEteindre == 0 && nombreFeuxAdjacents(environnement) < 3) 
    		dureeDeVie -= 6;
    	
    	if(dureeDeVie > 0) {
    		propager(environnement);
    		dureeDeVie --;
    	}
    	else 
    		eteindre(environnement);
    }
    
    
    private boolean propageable(Environnement environnement, int x, int y) {
    	if (environnement.grille.getObjectsAtLocation(x, y) == null) {
			return true;
		}
    	for(Object object : environnement.grille.getObjectsAtLocation(x, y).objs) {
    		if(object instanceof Feu || object instanceof Mur || object instanceof TerrainBrule) return false;
    	}
    	return true;
    }
    
    // Probabilité de propagation
	// Le feu se propage si la variable probabilite vaut 0
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
    
    private void eteindre(Environnement environnement) {
    	environnement.supprimerFeu(this);
    }
    
    private int nombreFeuxAdjacents(Environnement environnement) {

		int nombreFeuxAdj = 0;

		if (environnement.grille.getObjectsAtLocation(this.x + 1, this.y) != null) {
			for (Object o : environnement.grille.getObjectsAtLocation(this.x + 1, this.y).objs) {
				if (o instanceof Feu)
					nombreFeuxAdj++;
			}
		}
		if (environnement.grille.getObjectsAtLocation(this.x - 1, this.y) != null) {
			for (Object o : environnement.grille.getObjectsAtLocation(this.x - 1, this.y).objs) {
				if (o instanceof Feu)
					nombreFeuxAdj++;
			}
		}
		if (environnement.grille.getObjectsAtLocation(this.x, this.y + 1) != null) {
			for (Object o : environnement.grille.getObjectsAtLocation(this.x, this.y + 1).objs) {
				if (o instanceof Feu)
					nombreFeuxAdj++;
			}
		}
		if (environnement.grille.getObjectsAtLocation(this.x, this.y - 1) != null) {
			for (Object o : environnement.grille.getObjectsAtLocation(this.x, this.y - 1).objs) {
				if (o instanceof Feu)
					nombreFeuxAdj++;
			}
		}
		return nombreFeuxAdj;
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
    
    public void setStoppable(Stoppable stop) {
        this.stoppable = stop;
    }
    
    public Stoppable getStoppable() {
        return stoppable;
    }
    
}
