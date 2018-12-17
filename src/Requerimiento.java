
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hlabrana
 */
public class Requerimiento {
    public String Cargo;
    public int ID;
    public List<PacienteReq> ListaPacientes;
    
    public Requerimiento(){
        this.ListaPacientes = new ArrayList<>();
    }

    public static class PacienteReq {
        public String Nombre;
        public String Ficha;
        public String Texto;

        public PacienteReq() {
        }
    }
}
