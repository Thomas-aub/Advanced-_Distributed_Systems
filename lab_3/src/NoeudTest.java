import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

public class NoeudTest extends Node {

    private boolean alive;
    

    public void onSelection() {
        this.alive = false;
        setColor(Color.RED); 
    }

    // Cette méthode est appelée lors de la réception d’un message
    public void onMessage(Message m) {
        if (alive) {  
             
        }
        
    }

    public void onClock() {
        if (alive) {
        
        }
    }
}