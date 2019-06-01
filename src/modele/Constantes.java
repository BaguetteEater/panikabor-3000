package modele;

public class Constantes {

    public final static int NOMBRE_HUMAINS = 1;
    public final static int NOMBRE_FOYERS = 1;
    public final static int NOMBRE_MURS = 1;
    public final static int PROBABILITE_PROPAGATION = 3;
    public final static int VIE_MAX = 5;

    public final static int DOULEUR_BRULURE = 2; // Nombre de points de vie perdus à chaque tours lorsque l'on brule
    public final static double PROBABILITE_SE_RELEVER = 1/4; // Probabilité qu'un humain puisse se relever si il est par terre

    /**
     * Nombre de places sur une cellule. Cf. Superposable
     */
    public final static int CAPACITE_MAX_CELLULE = 3;
}

