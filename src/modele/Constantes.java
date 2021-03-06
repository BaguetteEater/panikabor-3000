package modele;

public class Constantes {

    public final static int NOMBRE_FOYERS = 1;
    public final static int NOMBRE_MURS = 1;
    public static final int NOMBRE_MEUBLES = 100;
    public static final int NOMBRE_SORTIES = 3;

    public static final int PORTEE_VISON = 70;

    public final static int VIE_MAX = 5;
    public final static int DOULEUR_BRULURE = 2; // Nombre de points de vie perdus à chaque tours lorsque l'on brule
    public final static double PROBABILITE_SE_RELEVER = 4; // Probabilité qu'un humain puisse se relever si il est par terre
    public final static double DEGATS_PIETINEMENT = 1;

    public final static int PROBABILITE_ETEINDRE = 10;
    public final static int DUREE_DE_VIE_FEU = 15;
    public final static int PROBABILITE_PROPAGATION = 3;

    /**
     * Nombre de places sur une cellule. Cf. Superposable
     */
    public final static int CAPACITE_MAX_CELLULE = 3;

    // Constantes pour les comportements
    public final static Comportement HERO = new Comportement("Héros", true, false, true, true, true);
    public final static Comportement EGOISTE = new Comportement("Égoiste", false, true, false, false, false);
    public final static Comportement PEUREUX = new Comportement("Peureux", false, false, false, false, true);

    // Nombre d'humains
    public final static int NOMBRE_HUMAINS_HERO = 10;
    public final static int NOMBRE_HUMAINS_EGOISTE = 10;
    public final static int NOMBRE_HUMAINS_PEUREUX = 10;
    public final static int NOMBRE_HUMAINS = NOMBRE_HUMAINS_HERO + NOMBRE_HUMAINS_EGOISTE + NOMBRE_HUMAINS_PEUREUX;
}

