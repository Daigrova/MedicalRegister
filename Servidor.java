import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;
import java.util.Queue;
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
public class Servidor extends Thread {

    class waitingRequest implements Comparable<waitingRequest> {
        String cargo;
        String request;

        public waitingRequest(String _cargo, String _request){
            cargo = _cargo;
            request = _request;
        }

        public int compareTo(waitingRequest r){
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

    private class Doctor{
        String Name;
        String IP;
        int PORT;

        public Doctor(String _name, String _IP, int _PORT ) {
            Name=_name;
            IP=_IP;
            PORT=_PORT;
        }
        public String getIP() { return IP; }
        public int getPORT() { return PORT;}
        public String getName() { return Name; }
    }


    String IP;
    int puerto;
    int contador = 1;
    boolean fileInUse = false;
    Queue< waitingRequest > waitQueue = new PriorityQueue<waitingRequest>();
    Doctor[] superiores;

    Main main;
    
    public Servidor(Main main){
        if (main.listaip.M29.get(0).equals(main.ipMaquina)){
            this.puerto = Integer.parseInt(main.listaip.M29.get(1));
            Thread hebra = new Thread();
            hebra.start();

        }
        if (main.listaip.M30.get(0).equals(main.ipMaquina)){
            this.puerto = Integer.parseInt(main.listaip.M30.get(1));
            Thread hebra = new Thread();
            hebra.start();
        }
        if (main.listaip.M31.get(0).equals(main.ipMaquina)){
            this.puerto = Integer.parseInt(main.listaip.M31.get(1));
            Thread hebra = new Thread();
            hebra.start();
            
        }
        if (main.listaip.M32.get(0).equals(main.ipMaquina)){
            this.puerto = Integer.parseInt(main.listaip.M32.get(1));
            Thread hebra = new Thread();
            hebra.start();
        }
    }
    
    @Override
    public void run(){
        try {
            ServerSocket servidor = new ServerSocket(this.puerto);
            while(true){
                Socket socket = servidor.accept();
                DataInputStream mensaje = new DataInputStream(socket.getInputStream());
                String data = mensaje.readUTF();
                procesar(data, socket);
                System.out.println("\n"+data+"\n");
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void procesar(String request, Socket s) throws IOException{
        String codigo = request.substring(request.indexOf("[")+1,request.indexOf("]"));
        String msg = request.substring(request.indexOf(";")+1);
        DataOutputStream response = new DataOutputStream(s.getOutputStream());
        if (codigo.equals("FILE")){
            /* [FILE];cargo+nombre_trabajador+id_trabajador+id_paciente+accion
             - Propagar un mensaje en log
                 - Agregar "Cliente" al Servidor para usar Broadcast de este
                 - Pasar las conexiones (sockets) al servidor para implementar broadcast
             */

            synchronized (this){
                if (!fileInUse && waitQueue.peek()==null ){
                    fileInUse = true;
                    new Thread(){
                        public void run() {
                            try {
                                // Se inicia un thread auxiliar para escuchar cuando se termine de escribir el log y asi seguir escuchando request de los nodos
                                ServerSocket aux_ServerSocket = new ServerSocket(puerto+contador);
                                Socket aux_connSocket = aux_ServerSocket.accept();
                                DataInputStream aux_in = new DataInputStream(aux_connSocket.getInputStream());
                                String respuesta = aux_in.readUTF();

                                // Una vez se recibe una respuesta de confirmación se escribe el log local y se propaga al resto de los nodos el cambio
                                String log_write = respuesta.split(";")[1];
                                FileWriter writer = new FileWriter("Log.txt",true);
                                writer.write(log_write);
                                writer.close();
                                // Falta propagar el mensaje -> Problema: Es trabajo del cliente -> Insertar cliente en el servidor?
                                fileInUse = false;
                            } catch (IOException e) {

                            }
                        }
                    }.start();
                    response.writeUTF("[ACCEPTED];"+ ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()+"+"+IP+"+"+(puerto+contador));
                    contador++;
                }

                else{
                    waitQueue.add(new waitingRequest(msg.substring(0,msg.indexOf("+")), msg));
                    response.writeUTF("[DENIED]");

                }
            }
        }

        else if (codigo.equals("LOGUPDATE")){
           /* [LOGUPDATE];log_data            */
            String log_write = msg.split(";")[1];
            FileWriter writer = new FileWriter("Log.txt",true);
            writer.write(log_write);
            writer.close();

        }

        else if (codigo.equals("COORDINATORDOWN")){
            // [COORDINATORDOWN];
            if(!avisarSuperiores()){
                /*
                 - Agregar lista con los doctores superiores a cada nodo con IP y puerto o socket, para enviar aviso a los superiores (bully)
                 - ¿Al actualizar el nodo coordinador se debe guardar en alguna parte?
                 */
            }
        }

        else if (codigo.equals("NEWCOORDINATOR")){
            /* [NEWCOORDINATOR];ip_coordinador+puerto_coordinador
             * - Se actualiza coordinador ¿Se guarda esto en alguna parte?
             * *- Se necesita reenviar la ultima peticion si es que se esta en espera del archivo, al caerse el coordinador se pierde la cola
             * *-> Mejor opcion: Agregar el cliente al servidor (creo)
            */
        }

    }

    boolean avisarSuperiores() {
        boolean existe_superior = false;
        for (int i = 0; i < superiores.length; i++) {
            Doctor temp = superiores[i];
            try {
                Socket s = new Socket(temp.getIP(), temp.getPORT());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out.writeUTF("coordinatorDown");
                existe_superior = true;

            }
            catch(IOException e) {
                System.out.println("Doctor " + temp.getName() + " no esta disponible");
            }

        }
        return existe_superior;

    }
}
