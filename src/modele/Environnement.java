package modele;

import javafx.util.Pair;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;

import java.util.ArrayList;
import java.util.List;

public class Environnement extends SimState {
	
	public SparseGrid2D grille = new SparseGrid2D(gui.Constantes.TAILLE_GRILLE, gui.Constantes.TAILLE_GRILLE);

    public Environnement(long seed) {
        super(seed);
    }

    @Override
    public void start() {
        super.start();
    }

    public Pair<Integer, Integer> getSortie(){

        for(int i = 0; i < grille.getHeight(); i++){
            for(int j = 0; j < grille.getWidth(); j++){
                for(Object o : grille.getObjectsAtLocation(i, j).objs){
                    if(o instanceof Sortie) return new Pair<>(i, j);
                }
            }
        }
        return new Pair<>(null, null);
    }

    public List<Pair<Integer, Integer>> getMurs(){

        List<Pair<Integer, Integer>> listMurs = new ArrayList<>();

        for(int i = 0; i < grille.getHeight(); i++){
            for(int j = 0; j < grille.getWidth(); j++){
                for(Object o : grille.getObjectsAtLocation(i, j).objs){
                    if(o instanceof Mur) listMurs.add(new Pair<>(i, j));
                }
            }
        }
        return listMurs;
    }
}
