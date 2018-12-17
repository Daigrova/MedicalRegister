public class WaitingRequest implements Comparable<WaitingRequest> {
    String cargo;
    String request;

    public WaitingRequest(String _cargo, String _request){
        cargo = _cargo;
        request = _request;
    }

    public int compareTo(WaitingRequest r){
        if (this.cargo.equals(r.cargo))
            return 0;
        else if (this.cargo.equals("Doctor"))
            return 1;
        else if (r.cargo.equals("Doctor"))
            return -1;
        else if (this.cargo.equals("Enfermero") && r.cargo.equals("Paramedico"))
            return 1;
        else
            return -1;
    }
}
