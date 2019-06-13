package modele;

import javafx.util.Pair;
import modele.jade.HumainAgent;
import modele.jade.HumanAgentI;
import modele.pathfinding.AStar;
import modele.pathfinding.Node;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Humain extends Superposable implements Steppable, HumanAgentI {

    private boolean[][] visionMasque;
    private int pointsDeVie = Constantes.VIE_MAX;
    private List<Statut> statuts;
    private Stoppable stoppable;
    private HumainAgent agent;

    public Humain(int x, int y, HumainAgent agent) {
        super(x, y);

        visionMasque = new boolean[gui.Constantes.TAILLE_GRILLE][gui.Constantes.TAILLE_GRILLE];
        resetMasque();

        this.statuts = new ArrayList<>();
        setTaille(1);

        this.agent = agent;
    }

    @Override
    public void step(SimState simState) {
        Environnement environnement = (Environnement) simState;

        Sortie sortieLaPlusProche = environnement.getSortieLaPlusProche(x, y);

        if (pointsDeVie <= 0 || sortieLaPlusProche == null || estSorti(environnement, sortieLaPlusProche))
            return;

        if (!est(Statut.EN_ALERTE) && alerteRecue()) {
            ajouterStatut(Statut.EN_ALERTE);
            System.out.println(agent.getName() + " -> " + "EN ALERTE (à cause du Behaviour)");
        }

        if (est(Statut.PAR_TERRE)) {
            seFairePietiner(environnement);
            essayerDeSeRelever(environnement);
        }

        if (est(Statut.EN_FEU))
            bruler();
        else
            potentiellementPrendreFeu(environnement);

        percevoir(environnement);

        if (est(Statut.EN_ALERTE)) {
            alerterHumains(environnement);
            essayerDeSortir(environnement, sortieLaPlusProche);
        } else {
            sAlerter(environnement);
        }

        if (estSorti(environnement, sortieLaPlusProche))
            environnement.sortir(this);

        if (pointsDeVie <= 0)
            environnement.tuer(this);
    }

    private void sAlerter(Environnement environnement) {
        for (int i = 0; i < gui.Constantes.TAILLE_GRILLE; i++) {
            for (int j = 0; j < gui.Constantes.TAILLE_GRILLE; j++) {
                Bag bag = environnement.grille.getObjectsAtLocation(i, j);

                if (visionMasque[i][j] && bag != null && Arrays.stream(bag.objs).anyMatch(s -> s instanceof Feu)) {
                    ajouterStatut(Statut.EN_ALERTE);
                    System.out.println(agent.getName() + " -> " + "EN ALERTE (à cause du feu)");
                    return;
                }
            }
        }
    }

    private void alerterHumains(Environnement environnement) {
        for (int i = 0; i < gui.Constantes.TAILLE_GRILLE; i++) {
            for (int j = 0; j < gui.Constantes.TAILLE_GRILLE; j++) {
                Bag bag = environnement.grille.getObjectsAtLocation(i, j);

                if (visionMasque[i][j] && bag != null) {
                    Arrays.stream(bag.objs).forEach(s -> {
                        if (s instanceof Humain && !((Humain) s).est(Statut.EN_ALERTE))
                            alerter(((Humain) s).getAgent().getName());
                    });
                }
            }
        }
    }

    @Override
    public void alerter(String humainAgentName) {
        this.agent.alerter(humainAgentName);
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
    private boolean essayerDeSortir(Environnement environnement, Sortie sortie) {
        AStar cerveau = new AStar(
                environnement.grille.getHeight(),
                environnement.grille.getWidth(),
                this,
                sortie.getX(),
                sortie.getY());

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
    public boolean estSorti(Environnement environnement, Sortie sortie){
        return sortie.getX() == this.x && sortie.getY() == this.getY();
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
    private void percevoir(Environnement e) {

        List<Superposable> objSorted = e.getSortedObjectInList(this);
        List<Superposable> objVisibles = new ArrayList<>();
        boolean isObjetEntreAB = false;

        //Le premier est toujours visible vu que c'est l'objet le plus proche de l'humain et on le retire pour eviter de l'ajouter deux fois
        objVisibles.add(objSorted.get(0));
        objSorted.remove(0);

        for (Superposable b : objSorted) {
            for (Superposable c : objVisibles) {

                //Si il n'y a pas d'objet entre A et B et que ABC sont collinéaires
                if (!isObjetEntreAB ) {
                    if ( sontCollineraires(b.x, b.y, c.x, c.y) ) {
                        //Alors on regarde si C est entre A et B et si il est traversable
                        isObjetEntreAB = estEntreDeuxPoints(b.x, b.y, c.x, c.y) && !c.isTraversable();

                    }
                }
            }
            if (!isObjetEntreAB)
                objVisibles.add(b);
            isObjetEntreAB = false;
        }

        updateMasque(objVisibles);
    }

    private void updateMasque(List<Superposable> objVisibles){
        resetMasque();
        for(Superposable visible : objVisibles)
            this.visionMasque[visible.x][visible.y] = true;
    }

    public void tomber(Environnement environnement) {
        if (!est(Statut.PAR_TERRE) && Superposable.isCellulePleine(environnement, x, y))
            ajouterStatut(Statut.PAR_TERRE);
    }

    private void essayerDeSeRelever(Environnement environnement) {
        if (est(Statut.EN_FEU))
            return;

        if (Superposable.getTailleCellule(environnement, x, y) == getTaille() || Math.random() < Constantes.PROBABILITE_SE_RELEVER)
            retirerStatut(Statut.PAR_TERRE);
    }

    private void seFairePietiner(Environnement environnement) {
        int tailleCellule = Superposable.getTailleCellule(environnement, x, y);
        pointsDeVie -= (tailleCellule + getTaille());
    }

    private void potentiellementPrendreFeu(Environnement environnement) {
        if (environnement.grille.getObjectsAtLocationOfObject(this) == null)
            return ;

        long nombreDeFeux = Arrays.stream(environnement.grille.getObjectsAtLocationOfObject(this).objs).filter(obj -> obj instanceof Feu).count();
        if (nombreDeFeux >= 1) {
            ajouterStatut(Statut.EN_FEU);
        }
    }

    private void bruler() {
        pointsDeVie -= Constantes.DOULEUR_BRULURE;
    }

    public int getPointsDeVie() {
        return pointsDeVie;
    }

    /**
     * Verifie que trois points A, B et C tel que A(Ax, Ay), B(Bx, By), C(Cx, Cy) sont collinéaires avec le point A obligatoirement assimilé à l'humain
     * @param Bx les coordonnées x de B
     * @param By les coordonnées y de B
     * @param Cx les coordonnées x de C
     * @param Cy les coordonnées y de C
     * @return true c'est ils sont collineaires, false sinon
     */
    private boolean sontCollineraires(int Bx, int By, int Cx, int Cy){
        //TODO : implementer Bresmann

        int ACx = Cx-this.x;
        int ACy = Cy-this.y;
        int ABx = Cx-Bx;
        int ABy = Cy-By;

        double determinant = ACx*ABy - ABx*ABy;

        return determinant >= -2 && determinant <= 2;
    }

    /**
     * Verifie si le point C est situé sur le segment AB respectivement situé en (Cx, Cy), (Ax, Ay) et (Bx, By) avec A obligatoirement assimilé à l'humain
     * @param Bx les coordonnées x de B
     * @param By les coordonnées y de B
     * @param Cx les coordonnées x de C
     * @param Cy les coordonnées y de C
     * @return true si C est entre A et B, false sinon.
     */
    private boolean estEntreDeuxPoints(int Bx, int By, int Cx, int Cy){

        int CAx = this.x-Cx;
        int CAy = this.y-Cy;
        int CBx = Bx-Cx;
        int CBy = By-Cy;

        double produitScalaire = (CAx*CBx) + (CAy*CBy);

        return produitScalaire <= 0;
        //return produitScalaire <= 5;
    }

    private void resetMasque(){
        Arrays.stream(this.visionMasque).forEach(ligne -> {
            Arrays.fill(ligne, false);
        });
    }

    /**
     * Retourne si l'humain possède un statut particulier
     * @param statut
     * @return Retourne vrai si l'humain possède le statut en paramètre
     */
    public boolean est(Statut statut) {
        return this.statuts.contains(statut);
    }

    public void ajouterStatut(Statut statut) {
        if (!est(statut))
            this.statuts.add(statut);
    }

    public void retirerStatut(Statut statut) {
        if (est(statut))
            this.statuts.remove(statut);
    }

    public Stoppable getStoppable() {
        return stoppable;
    }

    public void setStoppable(Stoppable stoppable) {
        this.stoppable = stoppable;
    }

    @Override
    public boolean alerteRecue() {
        return this.agent.alerteRecue();
    }

    public HumainAgent getAgent() {
        return agent;
    }
}
