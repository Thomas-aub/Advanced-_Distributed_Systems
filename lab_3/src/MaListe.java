import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaListe {
    // Attribut statique pour être accessible depuis n'importe où
    private static List<Integer> maListe;
    
    // Constructeur privé pour empêcher l'instanciation directe
    private MaListe() {
    }
    
    // Méthode statique d'initialisation
    public static void initialiser(int taille) {
        maListe = new ArrayList<>(taille);
        for (int i = 0; i < taille; ++i) {
            maListe.add(i);
        }
        shuffle();
    }
    
    // Méthode statique pour mélanger la liste
    public static void shuffle() {
        if (maListe != null) {
            Collections.shuffle(maListe);
        }
    }
    
    // Méthode statique pour accéder à la liste
    public static List<Integer> getListe() {
        return maListe;
    }
}