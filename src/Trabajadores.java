import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class Trabajadores {
    public List<Doctor> Doctores;
    public List<Enfermero> Enfermeros;
    public List<Paramedico> Paramedicos;
    
    public Trabajadores(){
        this.Doctores= new ArrayList<>();
        this.Enfermeros= new ArrayList<>();
        this.Paramedicos= new ArrayList<>();
    }
    
    public Trabajadores ProcesarJSON(String nameFile) throws IOException{
        final Gson gson = new GsonBuilder().create();
	final InputStream is = GsonTest.class.getClassLoader().getResourceAsStream(nameFile);
	final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
	final Trabajadores personal = gson.fromJson(bufferedReader, Trabajadores.class);
	bufferedReader.close();
        return personal;
    }

    private static class GsonTest {

        public GsonTest() {
        }
    }
    
    public Doctor getMejorDoctor(){
        Doctor candidato = this.Doctores.get(0);
        for (int i=1;i<this.Doctores.size();i++){
            if ((candidato.Estudios+candidato.Experiencia)<(this.Doctores.get(i).Estudios+this.Doctores.get(i).Experiencia)){
                candidato = this.Doctores.get(i);
            }
        }
        return candidato;
    }
    
}
