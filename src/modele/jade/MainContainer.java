package modele.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

public class MainContainer {

    public MainContainer(String mainPropertiesFile) {
        try {
            // Création d'un Container Principal
            Profile profile = new ProfileImpl(mainPropertiesFile);

            // Lancement du Container avec mon profil
            Runtime runtime = Runtime.instance();
            runtime.createMainContainer(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        Runtime.instance().shutDown();
    }

}
