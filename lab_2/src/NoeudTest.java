import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

public class NoeudTest extends Node {

    
    

    public void onSelection() {
        System.out.println("Vous avez sélectionné le noeud numéro " + getID() + "!");
        Message m = new Message("Bonjour voisin!");
        // Ici nous utilisons des chaînes, mais un message peut contenir n’importe quel
        // objet.
        sendAll(m); // sendAll envoie le message à tous mes voisins
        if (getNeighbors().size() > 0) { // Si il y a au moins un voisin...
            Node voisinPrefere = getNeighbors().get(0); // Le premier noeud de la liste des voisins
            m = new Message("Tu es mon voisin préféré!");
            send(voisinPrefere, m); // send envoie un message à un seul noeud
        }
    }

    // Cette méthode est appelée lors de la réception d’un message
    public void onMessage(Message m) {
        System.out.println("Le noeud " + getID() + " a reçu le message suivant du noeud "
                + m.getSender() + ": \"" + m.getContent() + "\"");
        if (((String) m.getContent()).contains("préféré"))
            setColor(Color.RED); // Les noeuds rougissent quand ils reçoivent un compliment
    }
}