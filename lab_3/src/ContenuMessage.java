public class ContenuMessage {
    private String type;
    private int val;
    private int rondeProposee;
    private boolean ack;

    // Pour messages Estimation, Proposition, ACK
    public ContenuMessage(String type, int val, int rondeProposee, boolean ack) {
        this.type = type;
        this.val = val;
        this.rondeProposee = rondeProposee;
        this.ack = ack;
    }

    // Pour messages Heartbeat
    public ContenuMessage(String type, int val) {
        this.type = type;
        this.val = val;
        this.rondeProposee = -1;
        this.ack = false;
    }

    public String getType() { return type; }
    public int getVal() { return val; }
    public int getRondeProposee() { return rondeProposee; }
    public boolean getAck() { return ack; }
}

