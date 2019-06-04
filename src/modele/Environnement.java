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

	}

	public void ajoutFeu(int x, int y){
		Feu newFeu = new Feu(x, y);
		grille.setObjectLocation(newFeu, x, y);
		schedule.scheduleRepeating(newFeu);
	}
	
	public void supprimerFeu(Feu feu) {
		grille.setObjectLocation(new TerrainBrule(feu.getX(), feu.getY()), feu.getX(), feu.getY());
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
