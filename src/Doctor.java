/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hlabrana
 */
public class Doctor {
    public String Nombre;
    public String Apellido;
    public int Estudios;
    public int Experiencia;
    
    public Doctor(String name, String apellido, int estudios,int experiencia){
        this.Nombre=name;
        this.Apellido=apellido;
        this.Estudios=estudios;
        this.Experiencia=experiencia;
    }
    
    public String getNombre(){
        return this.Nombre;
    }
    
    public String getApellido(){
        return this.Apellido;
    }
    
    public int getEstudios(){
        return this.Estudios;
    }
    
    public int getExperiencia(){
        return this.Experiencia;
    }
    
}
