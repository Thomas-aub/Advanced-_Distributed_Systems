import io.jbotsim.contrib.messaging.AsyncMessageEngine;
import io.jbotsim.core.Link;
import io.jbotsim.core.Link.Orientation;
import io.jbotsim.core.MessageEngine;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.JViewer;
import io.jbotsim.core.Node;

public class Main {
    public static final int NB_NOEUDS = 5;

    public static void main(String[] args) {
        Topology tp = new Topology();
        tp.setTimeUnit(50);
        tp.setOrientation(Orientation.UNDIRECTED); 
        tp.disableWireless();
        new JViewer(tp);

        int rayon = 130;
        int centreX = 290;
        int centreY = 200;

        Node[] noeuds = new Node[NB_NOEUDS];

        for (int i = 0; i < NB_NOEUDS; i++) {
            double angle = 2 * Math.PI * i / NB_NOEUDS;
            int x = (int) (centreX + rayon * Math.cos(angle));
            int y = (int) (centreY + rayon * Math.sin(angle));
            Node n = new NoeudCoordinateur();
            n.setID(i); 
            tp.addNode(x, y, n);
            noeuds[i] = n;
        }

        for (int i = 0; i < NB_NOEUDS; i++) {
            for (int j = i + 1; j < NB_NOEUDS; j++) {
                tp.addLink(new Link(noeuds[i], noeuds[j], Orientation.UNDIRECTED));
            }
        }
        MessageEngine me = new AsyncMessageEngine(tp, 1, AsyncMessageEngine.Type.FIFO);
        tp.setMessageEngine(me);
        tp.start();
    }
}
