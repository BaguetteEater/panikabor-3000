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
import java.util.Random;

public class Humain extends Superposable implements Steppable, HumanAgentI {

    private boolean[][] visionMasque;
    private int pointsDeVie = Constantes.VIE_MAX;
    private List<Statut> statuts;
    private Stoppable stoppable;
    private Comportement comportement;
    private HumainAgent agent;

    public Humain(int x, int y, HumainAgent agent, Comportement comportement) {
        super(x, y);

        visionMasque = new boolean[gui.Constantes.TAILLE_GRILLE][gui.Constantes.TAILLE_GRILLE];
        resetMasque();

        this.statuts = new ArrayList<>();
        setTaille(1);

        this.comportement = comportement;
        this.agent = agent;
    }

    @Override
    public void step(SimState simState) {
        Environnement environnement = (Environnement) simState;

        Sortie sortieLaPlusProche = environnement.getSortieLaPlusProche(x, y);

        if (pointsDeVie <= 0 || (sortieLaPlusProche != null && estSorti(environnement, sortieLaPlusProche)))
            return;

        if (!est(Statut.EN_ALERTE) && alerteRecue())
            ajouterStatut(Statut.EN_ALERTE);

        if (est(Statut.PAR_TERRE)) {
            seFairePietiner(environnement);
            essayerDeSeRelever(environnement);
        }

        if(this.comportement.pousserPourPasser && !this.est(Statut.PAR_TERRE)) {
        	pousser(environnement);
        }

        if (est(Statut.EN_FEU)) {
        	bruler();
        }
        else {
            potentiellementPrendreFeu(environnement);
        }

        percevoir(environnement);

        if (est(Statut.EN_ALERTE) && !this.est(Statut.PAR_TERRE)) {
            alerterHumains(environnement);

            if (sortieLaPlusProche == null)
                deplacementAleatoire(environnement);
            else if(!this.comportement.eteindre && !this.comportement.relever) {
        		essayerDeSortir(environnement, sortieLaPlusProche);
        	}
        	else {
        		if(this.comportement.eteindre) {
            		boolean aEteint = eteindre(environnement);
            		if(!aEteint) {
            			essayerDeSortir(environnement, sortieLaPlusProche);
            		}
            	}
            	if(this.comportement.relever) {
                	boolean aReleve = releve(environnement);
                	if(!aReleve) {
                		essayerDeSortir(environnement, sortieLaPlusProche);
                	}
                }
        	}
        } else {
            sAlerter(environnement);
        }

        if(this.comportement.pousserPourPasser && !this.est(Statut.PAR_TERRE)) {
        	pousser(environnement);
        }

        if (sortieLaPlusProche != null && estSorti(environnement, sortieLaPlusProche))
            environnement.sortir(this);

        if (pointsDeVie <= 0)
            environnement.tuer(this);
    }

    private boolean deplacementAleatoire(Environnement environnement) {
        switch(new Random().nextInt(4 - 1 + 1) + 1) {
            case 1:
                return essayerDeSeDeplacer(environnement, x + 1, y);
            case 2:
                return essayerDeSeDeplacer(environnement, x, y + 1);
            case 3:
                return essayerDeSeDeplacer(environnement, x - 1, y);
            case 4:
                return essayerDeSeDeplacer(environnement, x, y - 1);
        }
        return false;
    }

    private void sAlerter(Environnement environnement) {
        for (int i = 0; i < gui.Constantes.TAILLE_GRILLE; i++) {
            for (int j = 0; j < gui.Constantes.TAILLE_GRILLE; j++) {
                Bag bag = environnement.grille.getObjectsAtLocation(i, j);

                if (visionMasque[i][j] && bag != null && Arrays.stream(bag.objs).anyMatch(s -> s instanceof Feu)) {
                    ajouterStatut(Statut.EN_ALERTE);
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

    private void pousser (Environnement environnement) {
    	if (environnement.grille.getObjectsAtLocation(this.x, this.y) == null) {
			return;
		}
		for(Object object : environnement.grille.getObjectsAtLocation(this.x, this.y).objs) {
    		if(object instanceof Humain && object != this && !((Humain)object).est(Statut.PAR_TERRE)) {
    			((Humain)object).ajouterStatut(Statut.PAR_TERRE);
    		}
    	}
    }

    private boolean eteindre(Environnement environnement) {
    	boolean aEteint = false;
    	if(environnement.grille.getObjectsAtLocation(this.x, this.y) == null) {
    		return aEteint = false;
    	}
    	for(Object object : environnement.grille.getObjectsAtLocation(this.x, this.y).objs) {
    		if(object instanceof Humain && object != this && ((Humain)object).est(Statut.EN_FEU)) {
    			((Humain)object).retirerStatut(Statut.EN_FEU);
    			int chanceDeBruler = (int) (Math.random()*4);
    			if(chanceDeBruler == 0 && !this.est(Statut.EN_FEU)) {
    				this.ajouterStatut(Statut.EN_FEU);
    			}
    			aEteint = true;
    		}
    	}
    	return aEteint;
    }

    private boolean releve(Environnement environnement) {
    	boolean aReleve = false;
    	if (environnement.grille.getObjectsAtLocation(this.x, this.y) == null) {
			return aReleve;
		}
		for(Object object : environnement.grille.getObjectsAtLocation(this.x, this.y).objs) {
    		if(object instanceof Humain && object != this && ((Humain)object).est(Statut.PAR_TERRE)) {
    			((Humain)object).retirerStatut(Statut.PAR_TERRE);
    			aReleve = true;
    		}
    	}
		return aReleve;
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

        List<Pair<Integer, Integer>> nonTraversables = environnement.getNonTraversables(this.comportement.marcherSurLeFeu);
        List<Node> path;
        int[][] bloquerArray = new int[nonTraversables.size()][2];

        for(int i = 0; i < bloquerArray.length; i++){
            for(int j = 0; j < bloquerArray[0].length; j++){
                if(j == 0)
                	bloquerArray[i][j] = nonTraversables.get(i).getKey();
                else
                	bloquerArray[i][j] = nonTraversables.get(i).getValue();
            }
        }

        cerveau.setBlocks(bloquerArray);
        path = cerveau.findPath();

        if (path.size() == 0 && !est(Statut.EN_FEU)) {
            environnement.grille.remove(sortie);
            environnement.grille.setObjectLocation(new FausseSortie(sortie.getX(), sortie.getY()), sortie.getX(), sortie.getY());
        }

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

        List<Superposable> objSorted = e.getSortedObjectInList(this, Constantes.PORTEE_VISON);
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
        int probabilite = (int) (Math.random() * Constantes.PROBABILITE_SE_RELEVER);
        if (probabilite == 0)
            retirerStatut(Statut.PAR_TERRE);
    }

    private void seFairePietiner(Environnement environnement) {
        //int tailleCellule = Superposable.getTailleCellule(environnement, x, y);
    	if (environnement.grille.getObjectsAtLocation(this.x, this.y) == null) {
			return;
		}
		for(Object object : environnement.grille.getObjectsAtLocation(this.x, this.y).objs) {
    		if(object instanceof Humain && object != this && !((Humain)object).est(Statut.PAR_TERRE)) {
    			pointsDeVie -= Constantes.DEGATS_PIETINEMENT;
    		}
    	}

    }

    private void potentiellementPrendreFeu(Environnement environnement) {
        for(Object object : environnement.grille.getObjectsAtLocation(this.x, this.y).objs) {
        	if (object instanceof Feu) {
        		this.ajouterStatut(Statut.EN_FEU);
        		return;
        	}
        	else if(object instanceof Humain && object != this && ((Humain)object).est(Statut.EN_FEU)) {
    			this.ajouterStatut(Statut.EN_FEU);
    			return;
    		}
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

    public Comportement getComportement() {
        return comportement;
    }
}
