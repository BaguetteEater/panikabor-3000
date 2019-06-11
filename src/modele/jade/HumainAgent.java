package modele.jade;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class HumainAgent extends Agent implements HumanAgentI {

    private int x, y;
    private boolean enAlerte = false;

    @Override
    protected void setup() {
        new AttendreAlerteBehaviour(this);
    }

    @Override
    public boolean estEnAlerte() {
        return this.enAlerte;
    }

    public void setEnAlerte(boolean enAlerte) {
        this.enAlerte = enAlerte;
    }
}
