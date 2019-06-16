package modele;

import javafx.util.Pair;
import modele.pathfinding.AStar;
import modele.pathfinding.Node;
import sim.app.woims.Vector2D;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Humain extends Superposable implements Steppable {

    private boolean[][] visionMasque;
    private int pointsDeVie = Constantes.VIE_MAX;
    private List<Statut> statuts;
    private Stoppable stoppable;
    private Comportement comportement;

    public Humain(Environnement environnement, int x, int y, Comportement comportement) {
        super(x, y);

        visionMasque = new boolean[gui.Constantes.TAILLE_GRILLE][gui.Constantes.TAILLE_GRILLE];
        resetMasque();

        this.statuts = new ArrayList<>();
        setTaille(1);

        ajouterStatut(Statut.EN_ALERTE); // todo: remplacer par la propagation des alertes
        
        this.comportement = comportement;
    }

    @Override
    public void step(SimState simState) {
        Environnement environnement = (Environnement) simState;

        if (pointsDeVie <= 0 || estSorti(environnement))
            return;
        
        if (est(Statut.PAR_TERRE)) {
            seFairePietiner(environnement);
            essayerDeSeRelever(environnement);
        }

        if (est(Statut.EN_FEU)) {
        	bruler();      	
        }
        else {
            potentiellementPrendreFeu(environnement);
        }

        percevoir(environnement);
        
        
        if (est(Statut.EN_ALERTE) && !this.est(Statut.PAR_TERRE)) {
        	
        	if(!this.comportement.eteindre && !this.comportement.relever) {
        		essayerDeSortir(environnement);
        	}
        	else {
        		if(this.comportement.eteindre) {
            		boolean aEteint = eteindre(environnement);
            		if(!aEteint) {
            			essayerDeSortir(environnement);
            		}
            	} 
            	if(this.comportement.relever) {
                	boolean aReleve = releve(environnement);
                	if(!aReleve) {
                		essayerDeSortir(environnement);
                	}
                } 
        	}   	
        }
            
        
        if(this.comportement.pousserPourPasser && !this.est(Statut.PAR_TERRE)) {
        	pousser(environnement);
        }

        if (estSorti(environnement))
            environnement.sortir(this);

        if (pointsDeVie <= 0)
            environnement.tuer(this);

        //percevoir(environnement);
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
    private boolean essayerDeSortir(Environnement environnement) {
        AStar cerveau = new AStar(
                environnement.grille.getHeight(),
                environnement.grille.getWidth(),
                this,
                environnement.getSortie().getKey(),
                environnement.getSortie().getValue());

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

    public void sAlerter(Environnement environnement) {
        // TODO: vérifier dans la perception si un Humain est EN_ALERTE
        ajouterStatut(Statut.EN_ALERTE);
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
        Vector2D AC = new Vector2D(Cx-this.x, Cy-this.y);
        Vector2D AB = new Vector2D(Cx-Bx, Cy-By);

        double determinant = AC.x*AB.y - AB.x*AC.y;

        return determinant >= -2 && determinant <= 2;
        //return determinant == 0;
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

        Vector2D CA = new Vector2D(this.x-Cx,this.y-Cy);
        Vector2D CB = new Vector2D(Bx-Cx,By-Cy);
        double produitScalaire = (CA.x*CB.x) + (CA.y*CB.y);

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
}
