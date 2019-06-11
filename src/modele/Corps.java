package modele;

public class Corps extends Inerte {

    public Corps(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
        setTaille(1);
    }
}
