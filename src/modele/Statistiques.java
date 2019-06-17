package modele;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Statistiques {

    private Map<Comportement, Integer> nombreDeMorts;
    private Map<Comportement, Integer> nombreDeSorties;

    public Statistiques() {
        nombreDeMorts = new HashMap<>();
        nombreDeSorties = new HashMap<>();
    }

    public void tuer(Comportement comportement) {
        if (nombreDeMorts.containsKey(comportement))
            nombreDeMorts.replace(comportement, nombreDeMorts.get(comportement) + 1);
        else
            nombreDeMorts.put(comportement, 1);
    }

    public void sortir(Comportement comportement) {
        if (nombreDeSorties.containsKey(comportement))
            nombreDeSorties.replace(comportement, nombreDeSorties.get(comportement) + 1);
        else
            nombreDeSorties.put(comportement, 1);
    }

    public int getNombreDeMorts() {
        return nombreDeMorts.values().stream().reduce(0, Integer::sum);
    }

    public Map<Comportement, Integer> getNombreDeMortsParComportement() {
        return nombreDeMorts;
    }

    public int getNombreDeSorties() {
        return nombreDeSorties.values().stream().reduce(0, Integer::sum);
    }

    public Map<Comportement, Integer> getNombreDeSortiesParComportement() {
        return nombreDeSorties;
    }


    public String getRapportDeFin() {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#.0");

        stringBuilder.append("\n### Rapport de fin ###\n\n");

        stringBuilder
                .append("Nombre de survivants : ")
                .append(this.getNombreDeSorties())
                .append("/")
                .append(Constantes.NOMBRE_HUMAINS)
                .append(" (")
                .append(decimalFormat.format(((double) this.getNombreDeSorties() / Constantes.NOMBRE_HUMAINS) * 100))
                .append("%)\n");

        this.getNombreDeSortiesParComportement().forEach((comportement, integer) -> {
            stringBuilder
                    .append("| ")
                    .append(comportement.getName())
                    .append(" : ")
                    .append(integer)
                    .append("/")
                    .append(this.getNombreDeSorties())
                    .append(" (")
                    .append(decimalFormat.format(((double) integer / this.getNombreDeSorties()) * 100))
                    .append("%)\n");
        });

        stringBuilder.append("\n");

        stringBuilder
                .append("Nombre de morts : ")
                .append(this.getNombreDeMorts())
                .append("/")
                .append(Constantes.NOMBRE_HUMAINS)
                .append(" (")
                .append(decimalFormat.format(((double) this.getNombreDeMorts() / Constantes.NOMBRE_HUMAINS) * 100))
                .append("%)\n");

        this.getNombreDeMortsParComportement().forEach((comportement, integer) -> {
            stringBuilder
                    .append("| ")
                    .append(comportement.getName())
                    .append(" : ")
                    .append(integer)
                    .append("/")
                    .append(this.getNombreDeMorts())
                    .append(" (")
                    .append(decimalFormat.format(((double) integer / this.getNombreDeMorts()) * 100))
                    .append("%)\n");
        });


        return stringBuilder.toString();
    }
}
