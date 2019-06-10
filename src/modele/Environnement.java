package modele;

import javafx.util.Pair;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.List;

public class Environnement extends SimState {

	public int coordonneeSortieX, coordonneeSortieY;
	public SparseGrid2D grille = new SparseGrid2D(gui.Constantes.TAILLE_GRILLE, gui.Constantes.TAILLE_GRILLE);

	public Environnement(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		System.out.println("Simulation intialisee");
		super.start();
		grille.clear();
		ajouterContour();
		ajouterAgentsSortie();
		ajouterAgentsHumain();
		ajouterAgentsFeu();
		ajouterAgentsMeuble();

		this.getSortedObjectInList(new Humain(this, 4, 4));
	}

	private void ajouterAgentsMeuble() {
		for (int i = 0; i < Constantes.NOMBRE_MEUBLES; i++) {
			Int2D location = recupererEmplacementVide();
			Meuble meuble = new Meuble(location.x, location.y, 3);
			grille.setObjectLocation(meuble, location.x, location.y);
		}
	}

	private void ajouterAgentsHumain() {
		// TODO : modifier les valeurs de x et y en fonction du placement initial des
		// humains
		for (int i = 0; i < Constantes.NOMBRE_HUMAINS; i++) {
			Int2D location = recupererEmplacementVide();
			Humain humain = new Humain(this, location.x, location.y);
			grille.setObjectLocation(humain, location.x, location.y);
			schedule.scheduleRepeating(humain);
		}
	}

	private void ajouterAgentsFeu() {
		// TODO : modifier les valeurs de x et y en fonction du placement initial du feu
		for (int i = 0; i < Constantes.NOMBRE_FOYERS; i++) {
			Int2D location = recupererEmplacementVide();
			Feu feu = new Feu(location.x, location.y);
			grille.setObjectLocation(feu, location.x, location.y);
			feu.setStoppable(schedule.scheduleRepeating(feu));
		}
	}

	private void ajouterAgentsSortie() {
		int quel_cote = (int) (Math.random() * 4);
		int coordonneeSortieX = 0, coordonneeSortieY = 0;
		switch (quel_cote) {
		case 0:
			coordonneeSortieX = 0;
			coordonneeSortieY = (int) (Math.random() * grille.getHeight() - 1) + 1;
			break;
		case 1:
			coordonneeSortieX = grille.getWidth() - 1;
			coordonneeSortieY = (int) (Math.random() * grille.getHeight() - 1) + 1;
			break;
		case 2:
			coordonneeSortieX = (int) (Math.random() * grille.getWidth() - 1) + 1;
			coordonneeSortieY = 0;
			break;
		case 3:
			coordonneeSortieX = (int) (Math.random() * grille.getWidth() - 1) + 1;
			coordonneeSortieY = grille.getHeight() - 1;
			break;
		}
		grille.removeObjectsAtLocation(coordonneeSortieX, coordonneeSortieY);
		Sortie sortie = new Sortie(1, coordonneeSortieX, coordonneeSortieY);
		grille.setObjectLocation(sortie, coordonneeSortieX, coordonneeSortieY);
	}

	private void ajouterContour() {
		for (int i = 0; i < grille.getWidth(); i++) {
			grille.setObjectLocation(new Mur(i, 0), i, 0);
			grille.setObjectLocation(new Mur(i, grille.getHeight() - 1), i, grille.getHeight() - 1);
		}
		for (int j = 0; j < grille.getHeight(); j++) {
			grille.setObjectLocation(new Mur(0, j), 0, j);
			grille.setObjectLocation(new Mur(grille.getWidth() - 1, j), grille.getWidth() - 1, j);
		}
		////
		grille.setObjectLocation(new Mur(5, 4), 5, 4);
		grille.setObjectLocation(new Mur(5, 5), 5, 5);
		grille.setObjectLocation(new Mur(5, 6), 5, 6);
		/////
	}

	public void ajoutFeu(int x, int y) {
		Feu newFeu = new Feu(x, y);
		grille.setObjectLocation(newFeu, x, y);
		newFeu.setStoppable(schedule.scheduleRepeating(newFeu));
	
	}

	public void supprimerFeu(Feu feu) {
		grille.setObjectLocation(new TerrainBrule(feu.getX(), feu.getY()), feu.getX(), feu.getY());
		feu.getStoppable().stop();
		grille.remove(feu);
	}

	public Pair<Integer, Integer> getSortie() {

		for (int i = 0; i < grille.getHeight(); i++) {
			for (int j = 0; j < grille.getWidth(); j++) {
				if (grille.getObjectsAtLocation(i, j) != null) {
					for (Object o : grille.getObjectsAtLocation(i, j).objs) {
						if (o instanceof Sortie)
							return new Pair<>(i, j);
					}
				}
			}
		}
		return new Pair<>(null, null);
	}

	public List<Pair<Integer, Integer>> getNonTraversables() {

		List<Pair<Integer, Integer>> nonTraversables = new ArrayList<>();

		for (int i = 0; i < grille.getHeight(); i++) {
			for (int j = 0; j < grille.getWidth(); j++) {
				if (grille.getObjectsAtLocation(i, j) != null) {
					if (Superposable.isCellulePleine(this, i, j))
						nonTraversables.add(new Pair<>(i, j));
				}
			}
		}
		return nonTraversables;
	}

	private List<Superposable> getObjetsDeGrilleEnListe(){

		List<Superposable> res = new ArrayList<>();
		int sizeBag;

		for(int i = 0; i < grille.getHeight(); i++){
			for(int j = 0; j < grille.getWidth(); j++){

				if(grille.getObjectsAtLocation(i, j) != null) {
					sizeBag = grille.getObjectsAtLocation(i, j).numObjs;

					for (int idx = 0; idx < sizeBag; idx++)
						res.add((Superposable) grille.getObjectsAtLocation(i, j).objs[idx]);

				} else {
					res.add(new Superposable(i, j) {});
				}
			}
		}

		return res;
	}

	public List<Superposable> getSortedObjectInList(Humain h){

		List<Superposable> toSortList = getObjetsDeGrilleEnListe();

		toSortList.sort((s1, s2) -> {

			if(calculateDistance(h.getX(), h.getY(), s1.getX(), s1.getY()) == calculateDistance(h.getX(), h.getY(), s2.getX(), s2.getY()))
				return 0;

			return calculateDistance(h.getX(), h.getY(), s1.getX(), s1.getY()) < calculateDistance(h.getX(), h.getY(), s2.getX(), s2.getY()) ? -1 : 1;

		});

		toSortList.remove(0);

		return toSortList;
	}

	public static int calculateDistance( int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	private Int2D recupererEmplacementVide() {
		Int2D location = new Int2D(random.nextInt(grille.getWidth()), random.nextInt(grille.getHeight()));
		Object ag;
		while ((ag = grille.getObjectsAtLocation(location.x, location.y)) != null) {
			location = new Int2D(random.nextInt(grille.getWidth()), random.nextInt(grille.getHeight()));
		}
		return location;
	}

	public void tuer(Humain humain) {
		grille.remove(humain);
		grille.setObjectLocation(new Corps(humain.getX(), humain.getY()), humain.getX(), humain.getY());
		System.out.println("Humain a été tué");
	}

	public void sortir(Humain humain) {
		grille.remove(humain);
		System.out.println("Humain est sorti");
	}
}
