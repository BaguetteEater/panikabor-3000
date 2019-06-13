package modele.jade;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class AlerterBehaviour extends OneShotBehaviour {
    private HumainAgent sender;
    private AID receiver;

    public AlerterBehaviour(HumainAgent humainAgent, AID aid) {
        this.sender = humainAgent;
        this.receiver = aid;
    }

    @Override
    public void action() {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        message.addReceiver(receiver);
        sender.send(message);
    }
}
