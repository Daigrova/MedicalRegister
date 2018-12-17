
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
public class Paciente {
    public DatosPersonales Datos_Personales;
    public Procedimiento Procedimientos;
    public Examen Examenes;
    public Medicamento Medicamentos;
    
    public Paciente (){
    }

    public static class DatosPersonales {
        public String Nombre;
        public String Rut;
        public int Edad;
        public List<String> Alergias;
        public List<String> Enfermedades;
        
        public DatosPersonales() {
        }

        public String getNombre(){
            return this.Nombre;
        }
        public String getRut(){
            return this.Rut;
        }
        public int getEdad(){
            return this.Edad;
        }
        public List<String> getAlergias(){
            return this.Alergias;
        }
        public List<String> getEnfermedades(){
            return this.Enfermedades;
        }
      
    }

    public static class Procedimiento {
        List<String> Asignados;
        List<String> Completados;

        public Procedimiento() {
            this.Asignados = new ArrayList<>();
            this.Completados = new ArrayList<>();
        }
    }

    public static class Examen {
        List<String> Realizados;
        List<String> No_Realizados;
        
        public Examen() {
            this.Realizados = new ArrayList<>();
            this.No_Realizados = new ArrayList<>();
        }
    }

    public static class Medicamento {
        List<String> Recetados;
        List<String> Suministrados;
        
        public Medicamento() {
            this.Recetados = new ArrayList<>();
            this.Suministrados = new ArrayList<>();
        }
    }
}
