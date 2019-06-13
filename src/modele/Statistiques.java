package modele;

public class Statistiques {

    private int nombreDeMorts;
    private int nombreDeSorties;

    public Statistiques() {
        nombreDeMorts = 0;
        nombreDeSorties = 0;
    }

    public int tuer() {
        return ++nombreDeMorts;
    }

    public int sortir() {
        return ++nombreDeSorties;
    }

    public int getNombreDeMorts() {
        return nombreDeMorts;
    }

    public int getNombreDeSorties() {
        return nombreDeSorties;
    }

    public String getResume() {
        return "Nombre de morts : " + nombreDeMorts + " (" + ((double) nombreDeMorts / Constantes.NOMBRE_HUMAINS) * 100 + "%)\n" +
                "Nombre de survivants : " + nombreDeSorties + " (" + ((double) nombreDeSorties / Constantes.NOMBRE_HUMAINS) * 100 + "%)";
    }
}
