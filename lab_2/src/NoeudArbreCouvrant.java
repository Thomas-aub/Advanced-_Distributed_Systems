import java.util.ArrayList;
import java.util.List;
import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

public class NoeudArbreCouvrant extends Node {

    private static int compte_arbre = 0;
    private static int compte_bcast = 0;
    private Node pere;
    private List<Node> fils = new ArrayList<Node>();
    private int nb_env = 0;
    private int nb_rec = 0;
    private int nb_fils = 0;

    // Cette méthode est appelée lorsqu’un nouveau noeud est ajouté
    public void onStart() {

    }

    // Cette méthode est appelée lors d’un Ctrl+clic sur un noeud
    public void onSelection() {
        setColor(Color.YELLOW);

        Message m = new Message("JOIN");
        compte_arbre = this.getNeighbors().size();
        sendAll(m);
    }

    // Cette méthode est appelée lors de la réception d’un message
    public void onMessage(Message m) {
        
        if (m.getContent() == "BCAST") {
            setColor(Color.RED);
            for (Node n : fils) {
                    send(n, m);
                    compte_bcast ++;
            }
            if (fils.size() == 0) {
                int nb_tot = compte_arbre + compte_bcast;
                System.out.println("Nombre de message de création : " + compte_arbre);
                System.out.println("Nombre de message de bcast : " + compte_bcast);
                System.out.println("Nombre de message au total : " + nb_tot + "\n\n\n\n\n");

            }
        } else {
            if (pere == null) {
                if (m.getContent().equals("JOIN")) {
                    pere = m.getSender();
                    setColor(Color.YELLOW);
                    getCommonLinkWith(pere).setColor(Color.WHITE);
                    getCommonLinkWith(pere).setWidth(4);
                    for (Node n : this.getInNeighbors()) {
                        if (n != m.getSender()) {
                            nb_env++;
                            compte_arbre ++;
                            send(n, m);
                        }
        
                    }
                    if (nb_env == 0) {
                        Message m_feuille = new Message("BACK");
                        send(pere, m_feuille);
                        compte_arbre ++;
                        getCommonLinkWith(pere).setColor(Color.GREEN);
                        getCommonLinkWith(pere).setWidth(4);
                    }
                } else {
                    nb_fils ++;
    
                    // La racine a bien tous ses fils dans l'arbre couvrant
                    if (nb_fils == this.getNeighbors().size()) {
                        setColor(Color.RED);
                        Message m_bcast = new Message("BCAST");
                        compte_bcast = this.getNeighbors().size();
                        sendAll(m_bcast);
                    }
                    
                }
            } else  {
                if (m.getContent().equals("JOIN")) {
                    Message m_no = new Message("BACKNO");
                    send(m.getSender(), m_no);
                    compte_arbre ++;
                } else {
                    if (m.getContent().equals("BACK")) {
                        Message m_fils = new Message("BACK");
                        send(pere, m_fils);
                        compte_arbre ++;
                        fils.add(m.getSender());
                        getCommonLinkWith(pere).setColor(Color.GREEN);
                        getCommonLinkWith(pere).setWidth(4);
            
                    }
            
                    if (m.getContent().equals("BACKNO")) {
                        nb_rec++;
                    }
            
                    if (nb_rec == nb_env) {
                        Message m_feuille = new Message("BACK");
                        send(pere, m_feuille);
                        compte_arbre ++;
                        getCommonLinkWith(pere).setColor(Color.GREEN);
                        getCommonLinkWith(pere).setWidth(4);
                    }
                }            
            }
        }
    }
}