
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class Requerimientos {
    public List<Requerimiento> Requerimientos;
    
    public Requerimientos ProcesarJSON(String nameFile) throws IOException{
    final Gson gson = new GsonBuilder().create();
    final InputStream is = GsonTest.class.getClassLoader().getResourceAsStream(nameFile);
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
    final Requerimientos Listarequerimientos = gson.fromJson(bufferedReader, Requerimientos.class);
    bufferedReader.close();
    return Listarequerimientos;
    }

    private static class GsonTest {

        public GsonTest() {
        }
    }
}
