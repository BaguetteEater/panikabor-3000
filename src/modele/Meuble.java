package modele;

public class Meuble extends Inerte {

    private int x, y;

    public Meuble(int x, int y, int taille) {
        this.x = x;
        this.y = y;
        setTaille(taille);
    }
}
