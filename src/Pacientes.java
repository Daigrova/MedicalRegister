
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
public class Pacientes {
    public List<Paciente> Pacientes;
    
    public Pacientes(){
        this.Pacientes = new ArrayList<>();
    }
    
    public Pacientes ProcesarJSON(String nameFile) throws IOException{
    final Gson gson = new GsonBuilder().create();
    final InputStream is = GsonTest.class.getClassLoader().getResourceAsStream(nameFile);
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
    final Pacientes pacientes = gson.fromJson(bufferedReader, Pacientes.class);
    bufferedReader.close();
    return pacientes;
    }

    private static class GsonTest {

        public GsonTest() {
        }
    }
    
}
