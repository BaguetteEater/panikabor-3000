package modele.jade;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AttendreAlerteBehaviour extends CyclicBehaviour {

    private HumainAgent humainAgent;

    public AttendreAlerteBehaviour(HumainAgent humainAgent) {
        this.humainAgent = humainAgent;
    }

    @Override
    public void action() {
        ACLMessage alerte = myAgent.receive();

        if (alerte != null && alerte.getPerformative() == ACLMessage.INFORM) {
            humainAgent.setEnAlerte(true);
        } else {
            block();
        }
    }
}
