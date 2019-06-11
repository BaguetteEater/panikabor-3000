package modele.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class EnvironnementContainer {

    private Profile profile;
    private Runtime runtime;
    private ContainerController containerController;

    public EnvironnementContainer(String propertiesFile) {
        try {
            profile = new ProfileImpl(propertiesFile);
            runtime = Runtime.instance();
            containerController = runtime.createAgentContainer(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAndStartAgent(String name, HumainAgent humainAgent) {
        try {
            containerController.acceptNewAgent(name, humainAgent).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        try {
            containerController.kill();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
