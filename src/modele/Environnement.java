package modele;

import javafx.util.Pair;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.List;

public class Environnement extends SimState {

	public SparseGrid2D grille = new SparseGrid2D(gui.Constantes.TAILLE_GRILLE, gui.Constantes.TAILLE_GRILLE);

	public Environnement(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		System.out.println("Simulation intialisee");
		super.start();
		grille.clear();
		ajouterAgentMur();
		ajouterAgentSortie();
		ajouterAgentHumain();
		ajouterAgentFeu();

		this.getSortedObjectInList(new Humain(this, 4, 4));
	}

	private void ajouterAgentHumain() {
		// TODO : modifier les valeurs de x et y en fonction du placement initial des
		// humains
		for (int i = 0; i < Constantes.NOMBRE_HUMAINS; i++) {
			Int2D location = recupererEmplacementVide();
			Humain humain = new Humain(this, location.x, location.y);
			grille.setObjectLocation(humain, location.x, location.y);
			schedule.scheduleRepeating(humain);
		}
	}

	private void ajouterAgentFeu() {
		// TODO : modifier les valeurs de x et y en fonction du placement initial du feu
		for (int i = 0; i < Constantes.NOMBRE_FOYERS; i++) {
			Int2D location = recupererEmplacementVide();
			Feu feu = new Feu(location.x, location.y);
			grille.setObjectLocation(feu, location.x, location.y);
			schedule.scheduleRepeating(feu);
		}
	}

	private void ajouterAgentSortie() {
		// TODO : modifier les valeurs de x et y en fonction du placement initial des
		// sorties
		Sortie sortie = new Sortie(1, 10, 0);
		grille.setObjectLocation(sortie, 10, 0);
	}

	private void ajouterAgentMur() {
		// TODO : modifier les valeurs de x et y en fonction du placement initial des
		// murs
		for (int i = 0; i < grille.getHeight(); i++) {
			if (i != 10) {
				grille.setObjectLocation(new Mur(i, 0), i, 0);
			}
			grille.setObjectLocation(new Mur(i, grille.getWidth() - 1), i, grille.getWidth() - 1);
		}
		for (int j = 1; j < grille.getWidth()-1; j++) {
			grille.setObjectLocation(new Mur(0, j), 0, j);
			grille.setObjectLocation(new Mur(grille.getHeight() - 1, j), grille.getHeight() - 1, j);
		}
		////
		grille.setObjectLocation(new Mur(5, 4), 5, 4);
		grille.setObjectLocation(new Mur(5, 5), 5, 5);
		grille.setObjectLocation(new Mur(5, 6), 5, 6);
		/////
	}

	public void ajoutFeu(int x, int y){
		Feu newFeu = new Feu(x, y);
		grille.setObjectLocation(newFeu, x, y);
		schedule.scheduleRepeating(newFeu);
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
}
