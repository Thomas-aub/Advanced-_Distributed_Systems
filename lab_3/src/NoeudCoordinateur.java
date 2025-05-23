import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;
import java.util.*;



public class NoeudCoordinateur extends Heartbeat {

    // Définir des constantes pour les phases
    private static final int PHASE_ESTIMATION = 0;
    private static final int PHASE_PROPOSITION = 1;
    private static final int PHASE_ACK = 2;
    private static final int PHASE_DECISION = 3;


    protected int v;
    protected int ts;

    protected int ronde;
    protected int phase;

    protected Map<Integer, Set<Message>> estimationsReceived;
    protected int nbTotalNodes;  
    protected Map<Integer, Message> propsReceived;
    protected Set<Message> acksReceived;

    private Node coordinateur(int ronde) {
        // Hypothèse: les noeuds connaissent le nombre de noeuds.
        int id = ronde % getTopology().getNodes().size();
        return getTopology().findNodeById(id);
    }


    public void onStart() {
        super.onStart();

        v = (int) Math.round(Math.random() * 100);

        System.out.println("Noeud " + getID() + " démarre avec v=" + v);
        ts = 0;
        ronde = 0;
        phase = 0;

        estimationsReceived = new HashMap<>();
        nbTotalNodes = getTopology().getNodes().size(); 
        propsReceived = new HashMap<>();
        acksReceived = new HashSet<>();

        nouvelleRonde();
    }


    public void nouvelleRonde() {
        ronde++;
        phase = PHASE_ESTIMATION;
        estimationsReceived.put(ronde, new HashSet<>());
    
        System.out.println("Noeud " + getID() + " passe à la ronde " + ronde +
                           " (coordinateur: " + coordinateur(ronde).getID() + ")");
    
        Node coord = coordinateur(ronde);
    
        // Vérifier si une proposition pour cette ronde a déjà été reçue
        if (propsReceived.containsKey(ronde)) {
            Message m = propsReceived.remove(ronde);
            ContenuMessage cm = (ContenuMessage) m.getContent();
    
            if (trusted.contains(coord)) {
                v = cm.getVal();
                ts = cm.getRondeProposee(); // ou `ronde`, si tu préfères l'uniformité
                send(coord, new Message(new ContenuMessage("ACK", 0, ts, true)));
            } else {
                send(coord, new Message(new ContenuMessage("ACK", 0, cm.getRondeProposee(), false)));
            }
    
            nouvelleRonde(); // relancer une nouvelle ronde après avoir traité la proposition
            return;
        }
    
        // Si ce noeud n'est pas coordinateur, il envoie une estimation
        if (!this.equals(coord) && trusted.contains(coord)) {
            send(coord, new Message(new ContenuMessage("Estimation", v, ronde, false)));
        }
    }
    


    public void onSelection() {
        super.onSelection();
    }


    public void onMessage(Message m) {
        if (alive) {
            ContenuMessage cm = (ContenuMessage) m.getContent();
    
            switch (cm.getType()) {
                case "Heartbeat":
                    super.onMessage(m);
                    break;
    
                case "Estimation":
                    int msgRonde = cm.getRondeProposee();
                    System.out.println("Estimation à la ronde " + ronde);
                   
                    
                    // Trop vieux
                    if (msgRonde < ronde) {
                        System.out.println("Message trop vieux (msgRonde = " + msgRonde + ", ronde actuelle = " + ronde + ")");
                        return;  
                    } else if (msgRonde < ronde){
                        System.out.println("Message actuel (msgRonde = " + msgRonde + ", ronde actuelle = " + ronde + ")");
                    }
                    
                    
                    // Stocker le message
                    estimationsReceived.putIfAbsent(msgRonde, new HashSet<>());
                    estimationsReceived.get(msgRonde).add(m);

                    System.out.println(msgRonde == ronde);
                   
                    // Si je suis le coordinateur de cette ronde
                    if (msgRonde == ronde) {
                        System.out.println("\n\n LE coordianteur vérifie \n\n");
                        int received = estimationsReceived.get(ronde).size() + 1; // +1 pour moi-même
                        int majority = (nbTotalNodes / 2) ;
                        
                        if (received >= majority) {
                            int maxTs = -1;
                            int selectedVal = v; 
                            
                            for (Message msg : estimationsReceived.get(ronde)) {
                                ContenuMessage content = (ContenuMessage) msg.getContent();
                                if (content.getRondeProposee() > maxTs) {
                                    maxTs = content.getRondeProposee();
                                    selectedVal = content.getVal();
                                }
                            }
                            
                            v = selectedVal;
                            ts = ronde;
                            phase = PHASE_PROPOSITION;
                            
                            // Envoyer la proposition à tous
                            sendAll(new Message(new ContenuMessage("Proposition", v, ronde, false)));
                        }
                    }
                    break;
                case "Proposition":
                    int propRonde = cm.getRondeProposee();

                    System.out.println("Noeud " + getID() + " a reçu une proposition pour la ronde " + propRonde);

                    // Trop vieux 
                    if (propRonde < ronde) {
                        acksReceived.clear(); 
                        send(m.getSender(), new Message(new ContenuMessage("ACK", 0, propRonde, false)));
                    } 

                    // Actuelle 
                    else if (propRonde == ronde) {
                        v = cm.getVal();
                        ts = propRonde;

                        acksReceived.clear(); 
                        send(coordinateur(ronde), new Message(new ContenuMessage("ACK", 0, propRonde, true)));
                
                    } 

                    // Futur
                    else {
                        propsReceived.put(propRonde, m);
                    }        
                    
                
                    break;
    
                case "ACK":
                    int ackRonde = cm.getRondeProposee();
                    
                    // Trop vieux
                    if (ackRonde < ronde) return;
                    
                    // Si je suis le coordinateur de cette ronde et en phase ACK
                    if (ackRonde == ronde && coordinateur(ronde) == this && phase == PHASE_ACK) {
                        acksReceived.add(m);
                        int nbAcks = acksReceived.size() + 1; // +1 pour moi-même
                        int majority = (nbTotalNodes / 2) + 1;
                        
                        if (nbAcks >= majority) {
                            int cmpAccepted = 0;
                            int cmpRefused = 0;
                            for (Message ackMsg : acksReceived) {
                                ContenuMessage ackContent = (ContenuMessage) ackMsg.getContent();
                                if (ackContent.getAck()) {
                                    cmpAccepted ++;
                                } else {
                                    cmpRefused ++;
                                }
                            }

                            // Décision finale
                            if (cmpAccepted >= cmpRefused ) {
                                phase = PHASE_DECISION;
                                System.out.println("Coordinateur " + getID() + " décide la valeur: " + v);
                                sendAll(new Message(new ContenuMessage("Decision", v, ronde, false)));
                                alive = false;
                                setColor(Color.BLUE);
                            } else {
                                // Passer à la ronde suivante
                                nouvelleRonde();
                            }
                        }
                    }
                    break;
                
                
    
                case "Decision":
                    this.v = cm.getVal();
                    System.out.println("Noeud " + getID() + " a adopté la valeur décidée: " + v);
                    alive = false;
                    setColor(Color.BLUE); // Pour visualiser la décision
                    break;
                
            }
        }
    }
    

    public void onClock() {

        if (alive) {
            super.onClock();
            if( !trusted.contains(coordinateur(ronde)) ) {
                nouvelleRonde();
            }
        }
        
    }
}
