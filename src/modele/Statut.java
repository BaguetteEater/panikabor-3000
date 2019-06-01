package modele;

public enum Statut {
    // Cherche la sortie, alerte les autres autour
    EN_ALERTE,

    // Perd de la vie à chaque tour, a des difficultés pour se déplacer
    EN_FEU,

    // Ne peut plus marcher
    PAR_TERRE
}
