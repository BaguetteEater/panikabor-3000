package modele;

public class Comportement {

	public String name;
	public boolean marcherSurLeFeu;
	public boolean pousserPourPasser;
	public boolean relever;
	public boolean eteindre;
	public boolean alerteur;
	
	public Comportement (String name,
						 boolean marcherSurLeFeu,
						 boolean pousserPourPasser,
						 boolean relever,
						 boolean eteindre,
						 boolean alerteur) {

		this.name = name;
		this.marcherSurLeFeu = marcherSurLeFeu;
		this.pousserPourPasser = pousserPourPasser;
		this.relever = relever;
		this.eteindre = eteindre;
		this.alerteur = alerteur;
	}


    public String getName() {
		return this.name;
    }
}
