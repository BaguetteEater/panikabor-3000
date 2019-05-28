package modele;

import javafx.util.Pair;
import modele.pathfinding.AStar;
import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.List;

public class Humain extends Superposable implements Steppable {

    private Environnement environnement;
    private int x, y;
    private AStar cerveau;

    public Humain(Environnement environnement, int x, int y) {
        this.environnement = environnement;
        this.x = x;
        this.y = y;
        setTaille(1);

        this.cerveau = new AStar(environnement.grille.getHeight(), environnement.grille.getWidth(), this, environnement.getSortie().getKey(), environnement.getSortie().getValue());
    }

    private boolean peutSeDeplacer(int x, int y) {
        return environnement.grille.getObjectsAtLocation(x, y).isEmpty() // vérifie que la cellule visée est vide (pas de superposition)
                && Math.abs(this.x - x) <= 1 // vérifie qu'on se déplace d'une seule case
                && Math.abs(this.y - y) <= 1
                && (this.x == x || this.y == y); // vérifie qu'on ne se déplace pas en diagonale
    }

    /**
     * Déplace l'objet si les coordonnées le permettent
     * @param x
     * @param y
     * @return true si l'Humain à pu être deplacé,
     * false si l'Humain n'était pas dans la grille ou s'il n'avait pas le droit de se déplacer aux coordonnées indiquées
     */
    private boolean essayerDeSeDeplacer(int x, int y) {
        if (peutSeDeplacer(x, y) && (environnement.grille.setObjectLocation(this, x, y))) {
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

    private void essayerDeSortir() {
        List<Pair<Integer, Integer>> murs = environnement.getMurs();

        int[][] mursArray = new int[murs.size()][2];

        for(int i = 0; i < mursArray.length; i++){
            for(int j = 0; j < mursArray[0].length; j++){
                if(j == 0)
                    mursArray[i][j] = murs.get(i).getKey();
                else
                    mursArray[i][j] = murs.get(i).getValue();
            }
        }

        cerveau.setBlocks(mursArray);
        cerveau.findPath();
    }

    @Override
    public void step(SimState simState) {
        essayerDeSortir();
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
