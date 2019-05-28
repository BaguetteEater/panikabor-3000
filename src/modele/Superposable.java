package modele;

public abstract class Superposable {

    private int taille = 0;

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        if (taille < 0 || taille > Constantes.CAPACITE_MAX_CELLULE)
            throw new IllegalArgumentException("La taille doit être comprise entre 0 et CAPACITE_MAX_CELLULE");

        this.taille = taille;
    }

    public boolean isTraversable() {
        return taille <= Constantes.CAPACITE_MAX_CELLULE;
    }

    public void setInfranchissable() {
        this.setTaille(Constantes.CAPACITE_MAX_CELLULE);
    }
}
