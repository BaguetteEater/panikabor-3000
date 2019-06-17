package gui;

import modele.*;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

import javax.swing.*;
import java.awt.*;

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
		yardPortrayal.setPortrayalForClass(FausseSortie.class, getAgentFausseSortieRepresentation());
		yardPortrayal.setPortrayalForClass(TerrainBrule.class, getTerrainBruleRepresentation());
		yardPortrayal.setPortrayalForClass(Corps.class, getAgentCorpsRepresentation());
		yardPortrayal.setPortrayalForClass(Meuble.class, getAgentMeubleRepresentation());
		display.reset();
		display.setBackdrop(Color.white);
		display.repaint();
	}

    @Override
    public Object getSimulationInspectedObject() {
        return super.state;
    }

    private ImagePortrayal2D getAgentHumainRepresentation() {
	    return new ImagePortrayal2D(new ImageIcon("img/happy.png")) {
            @Override
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                if (object instanceof Humain) {

					if (((Humain) object).est(Statut.PAR_TERRE)) {
						if (((Humain) object).est(Statut.EN_FEU))
							image = new ImageIcon("img/par_terre_en_feu.png").getImage();
						else
							image = new ImageIcon("img/par_terre.png").getImage();

					} else if (((Humain) object).est(Statut.EN_ALERTE)) {
						if (((Humain) object).est(Statut.EN_FEU))
							image = new ImageIcon("img/en_alerte_en_feu.png").getImage();
						else
							image = new ImageIcon("img/en_alerte.png").getImage();

					}  else {
						image = new ImageIcon("img/happy.png").getImage();
					}
                }

                super.draw(object, graphics, info);
            }
        };
	}

	private ImagePortrayal2D getAgentCorpsRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("img/body.png"));
    }

	private RectanglePortrayal2D getAgentFeuRepresentation() {
		RectanglePortrayal2D r = new RectanglePortrayal2D();
		r.paint = new Color(200, 0, 0, 100);
		return r;
	}
	
	private ImagePortrayal2D getAgentSortieRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("img/sortie.png")) {
            @Override
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                if (object instanceof Sortie)
                    if (((Sortie) object).getX() == 0 || ((Sortie) object).getX() == Constantes.TAILLE_GRILLE - 1)
                        image = new ImageIcon("img/sortie-horizontale.png").getImage();
                    else
						image = new ImageIcon("img/sortie.png").getImage();

				super.draw(object, graphics, info);
            }
        };
    }

	private ImagePortrayal2D getAgentFausseSortieRepresentation() {
		return new ImagePortrayal2D(new ImageIcon("img/sortie.png")) {
			@Override
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
				if (object instanceof FausseSortie)
					if (((FausseSortie) object).getX() == 0 || ((FausseSortie) object).getX() == Constantes.TAILLE_GRILLE - 1)
						image = new ImageIcon("img/sortie-horizontale.png").getImage();
					else
						image = new ImageIcon("img/sortie.png").getImage();

				super.draw(object, graphics, info);
			}
		};
	}

	private ImagePortrayal2D getAgentMurRepresentation() {
		return new ImagePortrayal2D(new ImageIcon("img/wall.png"));
	}

	private RectanglePortrayal2D getTerrainBruleRepresentation() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = new Color(200, 50, 0, 100);
        return r;
	}

	private ImagePortrayal2D getAgentMeubleRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("img/meuble.png"));
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
