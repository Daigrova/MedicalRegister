
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
    List<Socket> sockets;


    public List<Socket> CrearSocket(String ipmaquina, IP listaip){
        //Nomenclatura SCXX: Socket para envio de datos a servidor XX
        try {
            Socket SC29 = new Socket(listaip.M29.get(0),Integer.parseInt(listaip.M29.get(1)));
            System.out.println("Socket con servidor 29 iniciado");
            Socket SC30 = new Socket(listaip.M30.get(0),Integer.parseInt(listaip.M30.get(1)));
            System.out.println("Socket con servidor 30 iniciado");
            Socket SC31 = new Socket(listaip.M31.get(0),Integer.parseInt(listaip.M31.get(1)));
            System.out.println("Socket con servidor 31 iniciado");
            Socket SC32 = new Socket(listaip.M32.get(0),Integer.parseInt(listaip.M32.get(1)));
            System.out.println("Socket con servidor 32 iniciado");
            sockets = new ArrayList<>();
            sockets.add(SC29);
            sockets.add(SC30);
            sockets.add(SC31);
            sockets.add(SC32);
            return sockets;
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, "Problema en crecion de Sockets", ex);
        }

        return null;
        /*
        if (ipmaquina.equals(listaip.M29.get(0))){
            try {
                Socket SC29 = new Socket(listaip.M29.get(0),Integer.parseInt(listaip.M29.get(1)));
                Socket SC30 = new Socket(listaip.M30.get(0),Integer.parseInt(listaip.M30.get(1)));
                Socket SC31 = new Socket(listaip.M31.get(0),Integer.parseInt(listaip.M31.get(1)));
                Socket SC32 = new Socket(listaip.M32.get(0),Integer.parseInt(listaip.M32.get(1)));
                sockets = new ArrayList<>();
                sockets.add(SC29);
                sockets.add(SC30);
                sockets.add(SC31);
                sockets.add(SC32);
                return sockets;
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, "Problema en crecion de Sockets", ex);
            }
        }
        
        if (ipmaquina.equals(listaip.M30.get(0))){
            try {
                Socket SC29 = new Socket(listaip.M29.get(0),Integer.parseInt(listaip.M29.get(1)));
                Socket SC30 = new Socket(listaip.M30.get(0),Integer.parseInt(listaip.M30.get(1)));
                Socket SC31 = new Socket(listaip.M31.get(0),Integer.parseInt(listaip.M31.get(1)));
                Socket SC32 = new Socket(listaip.M32.get(0),Integer.parseInt(listaip.M32.get(1)));
                sockets = new ArrayList<>();
                sockets.add(SC29);
                sockets.add(SC30);
                sockets.add(SC31);
                sockets.add(SC32);
                return sockets;
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, "Problema en crecion de Sockets", ex);
            }
        }
        
        if (ipmaquina.equals(listaip.M31.get(0))){
            try {
                Socket SC29 = new Socket(listaip.M29.get(0),Integer.parseInt(listaip.M29.get(1)));
                Socket SC30 = new Socket(listaip.M30.get(0),Integer.parseInt(listaip.M30.get(1)));
                Socket SC31 = new Socket(listaip.M31.get(0),Integer.parseInt(listaip.M31.get(1)));
                Socket SC32 = new Socket(listaip.M32.get(0),Integer.parseInt(listaip.M32.get(1)));
                sockets = new ArrayList<>();
                sockets.add(SC29);
                sockets.add(SC30);
                sockets.add(SC31);
                sockets.add(SC32);
                return sockets;
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, "Problema en crecion de Sockets", ex);
            }
        }
        
        if (ipmaquina.equals(listaip.M32.get(0))){
            try {
                Socket SC29 = new Socket(listaip.M29.get(0),Integer.parseInt(listaip.M29.get(1)));
                Socket SC30 = new Socket(listaip.M30.get(0),Integer.parseInt(listaip.M30.get(1)));
                Socket SC31 = new Socket(listaip.M31.get(0),Integer.parseInt(listaip.M31.get(1)));
                Socket SC32 = new Socket(listaip.M32.get(0),Integer.parseInt(listaip.M32.get(1)));
                sockets = new ArrayList<>();
                sockets.add(SC29);
                sockets.add(SC30);
                sockets.add(SC31);
                sockets.add(SC32);
                return sockets;
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, "Problema en crecion de Sockets", ex);
            }
        }
        return null;
        */
    }

    public List<Socket> SocketsExceptSelf(Socket socket){
        List<Socket> aux_list = sockets;
        aux_list.remove(socket);
        return aux_list;
    }

    public Socket SocketAsociado(String IP){
        for (Socket s: sockets){
            if (s.getInetAddress().getCanonicalHostName().equals(IP))
                System.out.println("Socket encontrado para IP " + IP);
                return s;
        }
        System.out.println("No se encontro socket para la IP " +  IP);
        return null;
    }

    public void EnviarBroadcast(String data,List<Socket> listaSocket) throws IOException{
        //SERIALIZAR DATA EN SOCKET
        DataOutputStream mensaje1 = new DataOutputStream(listaSocket.get(0).getOutputStream());
        DataOutputStream mensaje2 = new DataOutputStream(listaSocket.get(1).getOutputStream());
        DataOutputStream mensaje3 = new DataOutputStream(listaSocket.get(2).getOutputStream());
        //ENVIAR DATA
        mensaje1.writeUTF(data);
        mensaje2.writeUTF(data);
        mensaje3.writeUTF(data);
        //Cerrar Buffer
        mensaje1.close();
        mensaje2.close();
        mensaje3.close();
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
