import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.HashSet;
import java.util.Set;

public class Heartbeat extends Node {
    protected boolean alive = true;
    protected int clock = 0;

    protected Set<Node> trusted;
    protected Set<Node> rencentlyReceived;

    public void onStart() {
        trusted = new HashSet<>();
        trusted.add(this);
        rencentlyReceived = new HashSet<>();
        setColor(Color.GREEN); // Actif au départ
    }

    public void onSelection() {
        alive = false;
        setColor(Color.RED); // Simule une panne
        System.out.println("Node " + getID() + " is now down.");
    }

    public void onMessage(Message m) {
        if (alive) {
            rencentlyReceived.add(m.getSender());
            }
        
    }

    public void onClock() {
        clock++;
        if (alive) {
            if (clock % 10 == 0) {
                sendAll(new Message(new ContenuMessage("Heartbeat", getID())));
            }
            
            if (clock % 20 == 0) {
                trusted.clear();
                trusted.addAll(rencentlyReceived);
                rencentlyReceived.clear();
                            }
        }
    }
}
