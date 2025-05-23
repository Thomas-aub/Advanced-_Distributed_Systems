import java.util.List;
import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

public class NoeudBroadcast extends Node {

    

    public static final int NB_EXECUTION = 100;
    public static int NB_EXECUTION_PROCESSING = 1;

    public static int TOTAL_MESSAGES = 0;

    private int id_tab;
    private static int compte = 0;
    private static int compte_noeuds = 0;
    private boolean alreadySend = false;

    public NoeudBroadcast(int id) {
        id_tab = id;
    }

    
    private int etat;
    // non candidat : 0 
    // candidat : 1 
    // élu : 2 
    // perdu : 3
    
    private int leader;

    // Cette méthode est appelée lorsqu’un nouveau noeud est ajouté
    public void onStart() {
        List<Integer> liste = MaListe.getListe();
        this.setID(liste.get(id_tab));

        etat = 1;

        
        setColor(Color.GREEN);
        alreadySend = true;

        Message m = new Message(new ContenuMessage(0, getID())); 
        compte_noeuds++;
        sendAll(m);
    }

    // Cette méthode est appelée lors d’un Ctrl+clic sur un noeud
    public void onSelection() {

       
        
    }

    // Cette méthode est appelée lors de la réception d’un message
    public void onMessage(Message m) {
        compte ++;

        int type_recu = ((ContenuMessage) m.getContent()).type;
        int id_recu = ((ContenuMessage) m.getContent()).id;
        Message msg;

        
        if(type_recu == 0) { // MSG ELEC
            if(id_recu == getID()) {  // Lancer Lead
                msg = new Message(new ContenuMessage(1, getID()));
                etat = 2;
                setColor(Color.YELLOW);
                sendAll(msg);
            } else if(this.getID() < id_recu) { // Lancer un ELEC
                    setColor(Color.RED);
                    etat = 3;
                    leader = id_recu;
                    msg = new Message(new ContenuMessage(0, this.leader));
                    sendAll(msg); 
            }               
            
        } else {      // MSG LEAD 
            if(this.getID() != id_recu) {  // Lancer Lead  
                setColor(Color.BLUE);
                leader = id_recu;
                etat = 3;
                msg = new Message(new ContenuMessage(1, this.leader)); 
                sendAll(msg); 
            } else {
                if (NB_EXECUTION_PROCESSING < NB_EXECUTION) {
                    NB_EXECUTION_PROCESSING ++;
                    TOTAL_MESSAGES = TOTAL_MESSAGES + compte;
                    compte = 0;
                    MaListe.shuffle();
                    getTopology().restart();


                } else {
                    System.out.println("\n\nNombre d'execution' : " + NB_EXECUTION);
                    System.out.println("TOTAL_MESSAGES : " + TOTAL_MESSAGES);
                    System.out.println("NB_EXECUTION : " + NB_EXECUTION);
                    float moyenne = (float)TOTAL_MESSAGES / NB_EXECUTION;
                    System.out.println("Nombre de message moyen : " + moyenne);
                }
                
                
            }     
        }

    }
}