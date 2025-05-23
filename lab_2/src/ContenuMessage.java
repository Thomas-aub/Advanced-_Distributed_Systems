public class ContenuMessage {

    int type; 
    // Elec : 0 
    // Lead : 1 
    int id;

    public ContenuMessage(int type, int id) {
        this.type = type;
         this.id = id;
    }


    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }


}
