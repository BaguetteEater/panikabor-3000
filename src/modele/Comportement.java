package modele;

public class Comportement {
	
	public boolean marcherSurLeFeu;
	public boolean pousserPourPasser;
	public boolean relever;
	public boolean eteindre;
	public boolean stresse;
	public boolean alerteur;
	
	public Comportement (boolean marcherSurLeFeu,
						 boolean pousserPourPasser,
						 boolean relever,
						 boolean eteindre,
						 boolean alerteur) {
		
		this.marcherSurLeFeu = marcherSurLeFeu;
		this.pousserPourPasser = pousserPourPasser;
		this.relever = relever;
		this.eteindre = eteindre;
		this.alerteur = alerteur;
	}
	
	
	
	
}
