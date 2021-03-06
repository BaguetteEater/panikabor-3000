package modele;

public abstract class Superposable {

	private int taille = 0;

	int x;
	int y;

	public Superposable(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		if (taille < 0 || taille > Constantes.CAPACITE_MAX_CELLULE)
			throw new IllegalArgumentException("La taille doit être comprise entre 0 et CAPACITE_MAX_CELLULE");

		this.taille = taille;
	}

	public boolean isTraversable() {
		return taille < Constantes.CAPACITE_MAX_CELLULE;
	}

	public void setInfranchissable() {
		this.setTaille(Constantes.CAPACITE_MAX_CELLULE);
	}

	public static int getTailleCellule(Environnement environnement, int xCellule, int yCellule) {
		int taille = 0;
		if (environnement.grille.getObjectsAtLocation(xCellule, yCellule) != null) {
			for (Object superposable : environnement.grille.getObjectsAtLocation(xCellule, yCellule)) {
				if (superposable instanceof Superposable)
					taille += ((Superposable) superposable).getTaille();
			}
		}

		return taille;
	}

	public static boolean isCellulePleine(Environnement environnement, int xCellule, int yCellule) {
		return getTailleCellule(environnement, xCellule, yCellule) >= Constantes.CAPACITE_MAX_CELLULE;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
