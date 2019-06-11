package modele;

import gui.Constantes;

public class SuperposableVideFactory {

    private Superposable[][] grille;

    public SuperposableVideFactory(){
        grille = new Superposable[Constantes.TAILLE_GRILLE][Constantes.TAILLE_GRILLE];

        for(int i = 0; i < grille.length; i++){
            for(int j = 0; j < grille[0].length; j++){
                grille[i][j] = new Superposable(i, j) {};
            }
        }
    }

    public Superposable getVideSuperposable(int i, int j){
        return grille[i][j];
    }
}
