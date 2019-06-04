package gui;

import java.awt.Color;

import javax.swing.JFrame;
import modele.Environnement;
import modele.Feu;
import modele.Humain;
import modele.Mur;
import modele.Sortie;
import modele.TerrainBrule;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

public class SimulationAvecUI extends GUIState {

	SparseGridPortrayal2D yardPortrayal = new SparseGridPortrayal2D();
	public Display2D display;
	public JFrame displayFrame;
	
	public SimulationAvecUI(SimState etat) {
		super(etat);
	}
	
	public static String getNom() {
		return "Panikabor-3000";
	}
	
	public void start() {
		super.start();
		creerRepresentations();
	}

	public void load(SimState etat) {
		super.load(etat);
		creerRepresentations();
	}
	
	public void creerRepresentations() {
		Environnement environnement = (Environnement) state;
		yardPortrayal.setField(environnement.grille);
		yardPortrayal.setPortrayalForClass(Humain.class, getAgentHumainRepresentation());
		yardPortrayal.setPortrayalForClass(Feu.class, getAgentFeuRepresentation());
		yardPortrayal.setPortrayalForClass(Mur.class, getAgentMurRepresentation());
		yardPortrayal.setPortrayalForClass(Sortie.class, getAgentSortieRepresentation());
		yardPortrayal.setPortrayalForClass(TerrainBrule.class, getTerrainBruleRepresentation());
		yardPortrayal.setPortrayalForNull(null);
		display.reset();
		display.setBackdrop(Color.white);
		display.repaint();
	}
	
	private OvalPortrayal2D getAgentHumainRepresentation() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.GREEN;
		r.filled = true;
		r.scale = 0.5;
		return r;
	}

	private OvalPortrayal2D getAgentFeuRepresentation() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.RED;
		r.filled = true;
		r.scale = 0.4;
		return r;
	}
	
	private OvalPortrayal2D getAgentSortieRepresentation() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.BLUE;
		r.filled = true;
		r.scale = 1;
		return r;
	}
	
	private OvalPortrayal2D getAgentMurRepresentation() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.BLACK;
		r.filled = true;
		r.scale = 0.8;
		return r;
	}
	
	private OvalPortrayal2D getTerrainBruleRepresentation() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.LIGHT_GRAY;
		r.filled = true;
		r.scale = 0.4;
		return r;
	}
	
	public void init(Controller c) {
		  super.init(c);
		  display = new Display2D(Constantes.TAILLE_SIMULATION,Constantes.TAILLE_SIMULATION,this);
		  display.setClipping(false);
		  displayFrame = display.createFrame();
		  displayFrame.setTitle("PANIKABOR-3000");
		  c.registerFrame(displayFrame);
		  displayFrame.setVisible(true);
		  display.attach( yardPortrayal, "Yard" );
		}

}
