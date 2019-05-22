package modele;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Humain implements Steppable {

    private Environnement environnement;
    private int x, y;

    public Humain(Environnement environnement, int x, int y) {
        this.environnement = environnement;
        this.x = x;
        this.y = y;
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
        if (peutSeDeplacer(x, y))
            return environnement.grille.setObjectLocation(this, x, y);
        return false;
    }

    private void essayerDeSortir() {
        // TODO: avancer d'une case vers la sortie (utilisation d'un attribut Pile ?)
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
