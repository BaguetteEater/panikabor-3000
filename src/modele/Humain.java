package modele;

import javafx.util.Pair;
import modele.pathfinding.AStar;
import modele.pathfinding.Node;
import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.ArrayList;
import java.util.List;

public class Humain extends Superposable implements Steppable {

    private int x, y;
    private int pointsDeVie = Constantes.VIE_MAX;
    private List<Statut> statuts;

    public Humain(Environnement environnement, int x, int y) {
        this.x = x;
        this.y = y;
        this.statuts = new ArrayList<>();
        setTaille(1);
    }

    /**
     * Retourne si l'humain possède un statut particulier
     * @param statut
     * @return Retourne vrai si l'humain possède le statut en paramètre
     */
    public boolean est(Statut statut) {
        return this.statuts.contains(statut);
    }

    private boolean peutSeDeplacer(Environnement environnement, int x, int y) {
        return !Superposable.isCellulePleine(environnement, x, y) // vérifie que la cellule visée est accessible (capacité max non atteinte)
                && !est(Statut.PAR_TERRE) // vérifie que l'humain n'est pas par terre
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

    public void tomber() {
        if (!est(Statut.PAR_TERRE))
            statuts.add(Statut.PAR_TERRE);
    }

    public void essayerDeSeRelever() {
        if (Math.random() < Constantes.PROBABILITE_SE_RELEVER)
            statuts.remove(Statut.PAR_TERRE);
    }

    public void seFairePietiner(Environnement environnement) {
        int tailleCellule = Superposable.getTailleCellule(environnement, x, y);
        pointsDeVie -= (tailleCellule + getTaille());
    }

    public void bruler() {
        pointsDeVie -= Constantes.DOULEUR_BRULURE;
    }

    public void sAlerter(Environnement environnement) {
        // TODO: vérifier dans la perception si un Humain est EN_ALERTE
        statuts.add(Statut.EN_ALERTE);
    }

    @Override
    public void step(SimState simState) {
        Environnement environnement = (Environnement) simState;

        if (est(Statut.PAR_TERRE)) {
            seFairePietiner(environnement);
            essayerDeSeRelever();
        }

        if (est(Statut.EN_FEU))
            bruler();

        if (est(Statut.EN_ALERTE) && !estSorti(environnement))
            essayerDeSortir(environnement);
        else
            System.out.println("L'humain est sorti");

        if (pointsDeVie <= 0) {
            // todo: tuer l'humain
            // todo: placer un cadre aux coordonnées
        }
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
