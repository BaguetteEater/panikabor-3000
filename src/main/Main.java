package main;

import gui.SimulationAvecUI;
import modele.Environnement;
import modele.jade.MainContainer;
import sim.display.Console;

public class Main {

    public static void main(String[] args) {
		new MainContainer("modele/jade/main.properties");
		runUI();
	}

	public static void runUI() {
		Environnement model = new Environnement(System.currentTimeMillis());
		SimulationAvecUI gui = new SimulationAvecUI(model);
		Console console = new Console(gui);
		console.setVisible(true);
	}
}
