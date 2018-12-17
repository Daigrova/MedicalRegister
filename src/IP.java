
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
public class IP {
    List<String> M29; // Index (0) -> IP , Index (1) -> Puerto
    List<String> M30; // Index (0) -> IP , Index (1) -> Puerto
    List<String> M31; // Index (0) -> IP , Index (1) -> Puerto
    List<String> M32; // Index (0) -> IP , Index (1) -> Puerto
    
    public IP(){
        this.M29 = new ArrayList<>();
        this.M30 = new ArrayList<>();
        this.M31 = new ArrayList<>();
        this.M32 = new ArrayList<>();
    }
    
    public IP ProcesarJSON(String nameFile) throws IOException{
    final Gson gson = new GsonBuilder().create();
    final InputStream is = GsonTest.class.getClassLoader().getResourceAsStream(nameFile);
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
    final IP lista = gson.fromJson(bufferedReader, IP.class);
    bufferedReader.close();
    return lista;
    }

    private static class GsonTest {

        public GsonTest() {
        }
    }
    
}
