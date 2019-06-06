package modele;

import javafx.util.Pair;
import modele.pathfinding.AStar;
import modele.pathfinding.Node;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;

import java.util.List;

public class Humain extends Superposable implements Steppable {

    private SparseGrid2D vision;

    public Humain(Environnement environnement, int x, int y) {
        super(x, y);
        setTaille(1);
        vision = new SparseGrid2D(gui.Constantes.TAILLE_GRILLE, gui.Constantes.TAILLE_GRILLE);
        percevoir(environnement);
    }

    @Override
    public void step(SimState simState) {
        Environnement environnement = (Environnement) simState;

        if(!estSorti(environnement))
            essayerDeSortir(environnement);
        else
            System.out.println("L'humain est sorti");
    }

    private boolean peutSeDeplacer(Environnement environnement, int x, int y) {
        return !Superposable.isCellulePleine(environnement, x, y) // vérifie que la cellule visée est accessible (capacité max non atteinte)
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
    private boolean essayerDeSeDeplacer(Environnement environnement, int x, int y) {
        if (peutSeDeplacer(environnement, x, y) && (environnement.grille.setObjectLocation(this, x, y))) {
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

    /**
     * Cette fonction trouve la case la plus proche entre l'humain et la sortie et essayer de s'y deplacer
     * @param environnement L'ensemble de l'environnement dans lequel se deplace l'humain, contenant l'emplacement des murs et de la sortie
     * @return true si il a reussi a se deplacer, false sinon
     */
    private boolean essayerDeSortir(Environnement environnement) {
        AStar cerveau = new AStar(
                environnement.grille.getHeight(),
                environnement.grille.getWidth(),
                this,
                environnement.getSortie().getKey(),
                environnement.getSortie().getValue());

        List<Pair<Integer, Integer>> nonTraversables = environnement.getNonTraversables();
        List<Node> path;
        int[][] mursArray = new int[nonTraversables.size()][2];

        for(int i = 0; i < mursArray.length; i++){
            for(int j = 0; j < mursArray[0].length; j++){
                if(j == 0)
                    mursArray[i][j] = nonTraversables.get(i).getKey();
                else
                    mursArray[i][j] = nonTraversables.get(i).getValue();
            }
        }

        cerveau.setBlocks(mursArray);
        path = cerveau.findPath();
        //Le path retourne en premiere position la position actuelle de l'humain, on veut la case d'après d'ou le get(1)
        try {
            essayerDeSeDeplacer(environnement, path.get(1).getRow(), path.get(1).getCol());
        } catch(IndexOutOfBoundsException e){
            return false;
        }
        return true;
    }

    /**
     *
     * @param environnement L'environnement dans lequel se deplace l'humain, contenant la sortie.
     * @return Vrai si l'homme est sur la case de la sortie, false sinon
     */
    public boolean estSorti(Environnement environnement){
        return environnement.getSortie().getKey() == this.x && environnement.getSortie().getValue() == this.getY();
    }


    /**
     * Cette methode permet d'obtenir le champ de vision d'un humain via un methode simple
     * On parcourt chaque case et on regarde si il existe une case entre celle ci et l'agent humain.
     *
     * Pour verifier cela, on considère le triangle ABC avec A, l'humain, B la premiere case et C la deuxième case.
     * Si l'aire d'ABC est egale à 0, alors les trois points sont sur la même ligne et l'agent ne peut voir la plus eloignée des 2
     *
     * @param e L'environnement complet utilisé
     */
    private void percevoir(Environnement e){


    }
}
