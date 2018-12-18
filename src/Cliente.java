
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hlabrana
 */
public class Cliente {


    boolean waitingLog = false;
    public boolean newCoordinator = false;
    Socket coordinatorSocket;
    List<Socket> sockets = new ArrayList<>();


    public List<Socket> CrearSocket(String ipmaquina, IP listaip){
        //Nomenclatura SCXX: Socket para envio de datos a servidor XX
        try {
            Socket SC29 = new Socket(listaip.M29.get(0),Integer.parseInt(listaip.M29.get(1)));
            Socket SC30 = new Socket(listaip.M30.get(0),Integer.parseInt(listaip.M30.get(1)));
            Socket SC31 = new Socket(listaip.M31.get(0),Integer.parseInt(listaip.M31.get(1)));
            Socket SC32 = new Socket(listaip.M32.get(0),Integer.parseInt(listaip.M32.get(1)));
            sockets.add(SC29);
            sockets.add(SC30);
            sockets.add(SC31);
            sockets.add(SC32);
            return sockets;
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, "Problema en crecion de Sockets", ex);
        }

        return null;
    }

    public List<Socket> SocketsExceptSelf(Socket socket){
        List<Socket> aux_list = sockets;
        aux_list.remove(socket);
        return aux_list;
    }

    public Socket SocketAsociado(String IP){
        for (Socket s: sockets){
            System.out.println("Buscando socket para IP: "+IP);
            System.out.println("Socket actual: "+s.getInetAddress().getHostAddress());
            if (s.getInetAddress().getHostAddress().equals(IP))
                return s;
        }
        System.out.println("No se encontro socket para la IP " + IP);
        return null;
    }

    public void EnviarBroadcast(String data,List<Socket> listaSocket) throws IOException {

        for (Socket s : listaSocket) {
            //SERIALIZAR DATA EN SOCKET
            DataOutputStream mensaje = new DataOutputStream(s.getOutputStream());
            //ENVIAR DATA
            mensaje.writeUTF(data);
            //Cerrar Buffer
            mensaje.close();
        }
    }

    public void EnviarBroadcast(String data) {
        System.out.println("Enviando mensaje a " + sockets.size() + " nodos");
        try {
            for (Socket s : sockets) {
                System.out.println("Abriendo stream");
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                System.out.println("Stream abierto");
                out.writeUTF(data);
                System.out.println("Mensaje enviado");
                out.close();
            }
            System.out.println("Something wrong happened");
        }
        catch (IOException e){ System.out.println("Here is a problem"); }
    }


        public void EnviarIndividual(String data,Socket socket) throws IOException{
        DataOutputStream mensaje = new DataOutputStream(socket.getOutputStream());
        mensaje.writeUTF(data);
        mensaje.close();
    }

    public String RecibirIndividual(Socket socket) throws  IOException{
        DataInputStream mensaje = new DataInputStream(socket.getInputStream());
        String msg = mensaje.readUTF();
        mensaje.close();
        return msg;
    }



}
