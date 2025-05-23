import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

public class NoeudBroadcast extends Node {

    private static int compte = 0;
    private static int compte_noeuds = 0;
    private boolean alreadySend = false;

    // Cette méthode est appelée lorsqu’un nouveau noeud est ajouté
    public void onStart() {

    }

    // Cette méthode est appelée lors d’un Ctrl+clic sur un noeud
    public void onSelection() {
        alreadySend = true;
        Message m = new Message("Broadcast !");

        if (compte_noeuds == getTopology().getNodes().size()) {
            System.out.println(compte);
        } else {

            compte_noeuds++;
            compte = getOutNeighbors().size();
            sendAll(m);
        }
    }

    // Cette méthode est appelée lors de la réception d’un message
    public void onMessage(Message m) {
        compte_noeuds++;
        if (!alreadySend) {
            
            setColor(Color.RED);
            alreadySend = true;
            for (Node n : this.getInNeighbors()) {
                if (n != m.getSender()) {
                    send(n, m);
                    compte++;
                }
            }

        }
        if (compte_noeuds == getTopology().getNodes().size()) {
            System.out.println("Nombre de messages : " + compte);
        }
    }
}