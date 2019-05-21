package modele;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;

public class Environnement extends SimState {
	
	public SparseGrid2D grille = new SparseGrid2D(gui.Constantes.TAILLE_GRILLE, gui.Constantes.TAILLE_GRILLE);

    public Environnement(long seed) {
        super(seed);
    }

    @Override
    public void start() {
        super.start();
    }
}
