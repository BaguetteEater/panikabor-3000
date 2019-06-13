package modele;

import gui.Constantes;

public class SuperposableFactory {

    private Superposable[][] grilleVide;
    private Superposable[][] grilleNonTraversable;

    public SuperposableFactory(){
        grilleVide = new Superposable[Constantes.TAILLE_GRILLE][Constantes.TAILLE_GRILLE];
        grilleNonTraversable = new Superposable[Constantes.TAILLE_GRILLE][Constantes.TAILLE_GRILLE];

        for(int i = 0; i < grilleVide.length; i++){
            for(int j = 0; j < grilleVide[0].length; j++){
                grilleVide[i][j] = new Superposable(i, j) {};

                grilleNonTraversable[i][j] = new Superposable(i, j) {
                    @Override
                    public boolean isTraversable() {
                        return false;
                    }
                };
            }
        }
    }

    public Superposable getVideSuperposable(int i, int j){
        return grilleVide[i][j];
    }

    public Superposable getPleinSuperposable(int i, int j){
        return grilleNonTraversable[i][j];
    }
}
