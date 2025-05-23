import java.util.List;
import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

public class ItaiRodeh extends Node {

    public static final int NB_EXECUTION = 100;
    public static int NB_EXECUTION_PROCESSING = 1;
    public static int CANDIDATS = 0;

    public static int TOTAL_MESSAGES = 0;

    private int id_tab;
    private static int compte = 0;

    public ItaiRodeh(int id) {
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
        double ran = Math.random();

        if ( ran > 0.20 && id_tab > 0) {
            setColor(Color.GRAY);
            etat = 0;
        } else {
            CANDIDATS ++;
            etat = 1;
            setColor(Color.GREEN);
            Message m = new Message(new ContenuMessage(0, getID())); 
            sendAll(m);
        }
        
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


        System.out.println("Message :" + compte);
        System.out.println("Type :" + type_recu + ", ID :" + id_recu + ", MON ID :" + getID() + ", MON ETAT :" + etat);

        
        if(type_recu == 0) { // MSG ELEC

            if(id_recu == getID()) {  // Lancer Lead
                msg = new Message(new ContenuMessage(1, getID()));
                etat = 2;
                setColor(Color.YELLOW);
                sendAll(msg);
            } 
            
            if( id_recu > getID()) { // Lancer un ELEC
                    setColor(Color.RED);
                    etat = 3;
                    leader = id_recu;
                    msg = new Message(new ContenuMessage(0, this.leader));
                    sendAll(msg); 
            } 
            
            if ( getID() > id_recu  && etat == 0) { // Lancer un ELEC
                System.out.println("OOOOOOOK");
                setColor(Color.GREEN);
                etat = 1;
                msg = new Message(new ContenuMessage(0, getID()));
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
                NB_EXECUTION_PROCESSING ++;
                TOTAL_MESSAGES = TOTAL_MESSAGES + compte;
                if (NB_EXECUTION_PROCESSING < NB_EXECUTION) {
                    compte = 0;
                    MaListe.shuffle();
                    getTopology().restart();
                } else {
                    System.out.println("\n\nNombre d'execution' : " + NB_EXECUTION);
                    System.out.println("TOTAL_MESSAGES : " + TOTAL_MESSAGES);
                    System.out.println("NB_EXECUTION : " + NB_EXECUTION);
                    float moyenne = (float)TOTAL_MESSAGES / NB_EXECUTION;
                    System.out.println("Nombre de message moyen : " + moyenne);

                    float candidats_moyen = (float)CANDIDATS / NB_EXECUTION;
                    System.out.println("Nombre de candidat moyen : " + candidats_moyen);
                }
                
                
            }     
        }

    }
}