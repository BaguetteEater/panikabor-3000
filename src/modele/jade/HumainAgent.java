package modele.jade;

import jade.core.AID;
import jade.core.Agent;

public class HumainAgent extends Agent implements HumanAgentI {

    private boolean enAlerte = false;

    @Override
    protected void setup() {
        addBehaviour(new AttendreAlerteBehaviour(this));
    }

    @Override
    public boolean alerteRecue() {
        return this.enAlerte;
    }

    void setEnAlerte(boolean enAlerte) {
        this.enAlerte = enAlerte;
    }

    @Override
    public void alerter(String humainAgentName) {
        addBehaviour(new AlerterBehaviour(this, new AID(humainAgentName, true)));
    }
}
