package modele;

public class Sortie extends Inerte {

	private int x, y;
	
	public Sortie(int debit, int x, int y) {
		if (debit >= Constantes.CAPACITE_MAX_CELLULE)
            throw new IllegalArgumentException("Le débit doit être inférieur à la capacité maximale d'une cellule");
        if (debit <= 0)
            throw new IllegalArgumentException("Le débit doit être strictement supérieur à 0");

        setTaille(Constantes.CAPACITE_MAX_CELLULE - debit); // Si debit == 1, on ne laisse passer 1 humain à la fois
        
        this.x = x;
        this.y = y;
	}
	
    public Sortie(int debit) {
        if (debit >= Constantes.CAPACITE_MAX_CELLULE)
            throw new IllegalArgumentException("Le débit doit être inférieur à la capacité maximale d'une cellule");
        if (debit <= 0)
            throw new IllegalArgumentException("Le débit doit être strictement supérieur à 0");

        setTaille(Constantes.CAPACITE_MAX_CELLULE - debit); // Si debit == 1, on ne laisse passer 1 humain à la fois
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
