import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.jbotsim.core.Link;
import io.jbotsim.core.Link.Orientation;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.JViewer;
import io.jbotsim.core.Node;

public class Main {

    public static final int TAILLE_ANNEAU = 10;
    public static void main(String[] args) { // On déclare le programme principal
        Topology tp = new Topology();
        tp.setTimeUnit(0);

        // Construction de la topologie ici
       //  new JViewer(tp);
        tp.setOrientation(Link.Orientation.DIRECTED);
        tp.disableWireless();

        MaListe.initialiser(TAILLE_ANNEAU);

        int rayon = 130; // rayon du cercle
        int centreX = 290;
        int centreY = 200;
        
        

        int first_x = (int) (centreX + rayon * Math.cos(0));
        int first_y = (int) (centreY + rayon * Math.sin(0));
        Node first_noeud = new ChangRoberts(0);


        tp.addNode(first_x, first_y, first_noeud);
        
        Node prevNode = first_noeud;


        for (int i = 1; i < TAILLE_ANNEAU; ++i) {
            double angle = 2 * Math.PI * i / TAILLE_ANNEAU; // angle en radians
            int x = (int) (centreX + rayon * Math.cos(angle));
            int y = (int) (centreY + rayon * Math.sin(angle));

            Node noeud = new ChangRoberts(i);

            tp.addNode(x, y, noeud);

            tp.addLink(new Link(prevNode, noeud, Orientation.DIRECTED)); 
            // Lien bidirectionnel -> Orientation.UNDIRECTED

            prevNode = noeud;
        }

        

        tp.addLink(new Link(prevNode, first_noeud, Orientation.DIRECTED));

        tp.start();
    }
}