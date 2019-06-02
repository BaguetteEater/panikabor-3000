package modele;

import javafx.util.Pair;
import modele.pathfinding.AStar;
import modele.pathfinding.Node;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;

import java.util.List;

public class Humain extends Superposable implements Steppable {

    private int x, y;
    private SparseGrid2D vision;

    public Humain(Environnement environnement, int x, int y) {
        this.x = x;
        this.y = y;
        setTaille(1);
        vision = new SparseGrid2D(gui.Constantes.TAILLE_GRILLE, gui.Constantes.TAILLE_GRILLE);
    }

    @Override
    public void step(SimState simState) {
        Environnement environnement = (Environnement) simState;
        System.out.println(reduceGridBetweenTwoPoints(environnement, 0, 0, 5, 5));
        if(!estSorti(environnement))
            essayerDeSortir(environnement);
        else
            System.out.println("L'humain est sorti");
    }

    private boolean peutSeDeplacer(Environnement environnement, int x, int y) {
        return Superposable.isCellulePleine(environnement, x, y) // vérifie que la cellule visée est accessible (capacité max non atteinte)
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

        List<Pair<Integer, Integer>> murs = environnement.getMurs();
        List<Node> path;
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
     * @param e
     */
    private void percevoir(Environnement e){

        int aireTriangle = 0;
        for(int Bx = 0 ; Bx < e.grille.getWidth(); Bx++){
            for(int  By = 0 ; By < e.grille.getHeight(); By++) {

                for(int Cx = 0; Cx < e.grille.getWidth(); Cx++){
                    for(int Cy = 0; Cy < e.grille.getHeight(); Cy++){

                        aireTriangle = this.x * (By - Cy) + Bx * (Cy - this.y) + Cx * (this.y - By);

                        if(aireTriangle == 0){

                        }
                    }
                }
            }
        }
    }

    /**
     * Cette fonction copie une partie la grille de l'environnement entre un point A et un point B tel que :
     * Pour A(x, y) et B(l, c) la grille obtenue sera de taille (l-x) sur (c-y) avec l > x et c > y
     * Ce traitement a pour but de reduire le nombre de cases parcouru lors du traitement de la perception et aussi d'obtenir les points appartenant au segment [AB] et non pas la droite (AB)
     * @param Ax Les coordonnées x du point A
     * @param Ay Les coordonnées y du point A
     * @param Bx Les coordonnées x du point B
     * @param By Les coordonnées y du point B
     * @param e L'environnement complet
     * @return Une copie plus petite de la grille originale faisant (l-x) sur (c-y) avec l > x et c > y ou bien (x-l) sur (y-c) avec x > l et y > c
     */
    private SparseGrid2D reduceGridBetweenTwoPoints(Environnement e, int Ax, int Ay, int Bx, int By){

        // iDepart  et jDepart sont les points de depart de la boucles, ils doivent etre les coordonnees les plus petites parmis les deux points
        int iDepart = (Ax < Bx) ? Ax : Bx;
        int jDepart = (Ay < By) ? Ay : By;

        // iLimite et jLimite sont les limite de fin de boucle, ils doivent etre les plus grandes coordonnees parmis les deux points
        int iLimite = (Ax < Bx) ? Bx : Ax;
        int jLimite = (Ay < By) ? By : Ay;

        SparseGrid2D res = new SparseGrid2D(iLimite - iDepart, jLimite - jDepart);

        int a = 0;
        int b = 0;
        for(int i = iDepart; i < iLimite; i++){
            for(int j = jDepart; j < jLimite; j++){
                res.setObjectLocation(e.grille.getObjectsAtLocation(i, j), a, b);
                b++;
            }
            a++;
            b = 0;
        }
        return res;
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
