package modele;

public class Corps extends Inerte {

    private int x, y;

    public Corps(int x, int y) {
        this.x = x;
        this.y = y;
        setTaille(1);
    }
}
