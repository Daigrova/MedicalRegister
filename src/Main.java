import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
public class Main implements Runnable {
    Trabajadores personal;
    Requerimientos requerimientos;
    Pacientes pacientes;
    IP listaip;
    String ipMaquina;
    Servidor servidor;
    Cliente cliente;
    List<String> candidatos = new ArrayList<>();
    Queue<WaitingRequest> waitQueue = new PriorityQueue<>();
    boolean Is_Coordinador;
    boolean LogInUse = false;
    int contador=1;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException{
        
        //PROCESA JSON TRABAJADORES Y LOS AGREGA A UNA LISTA personal
        Trabajadores personal = new Trabajadores();
        personal = personal.ProcesarJSON("JSON/Trabajadores.JSON");
        
        ////PROCESA JSON REQUERIMIENTOS Y LOS AGREGA A UNA LISTA requerimientos
        Requerimientos requerimientos = new Requerimientos();
        requerimientos = requerimientos.ProcesarJSON("JSON/Requerimientos.JSON");
        
        ////PROCESA JSON Pacientes Y LOS AGREGA A UNA LISTA pacientes
        Pacientes pacientes = new Pacientes();
        pacientes = pacientes.ProcesarJSON("JSON/Pacientes.JSON");
        
        //PROCESA JSON IP e instancia una clase IP
        IP listaip = new IP();
        listaip = listaip.ProcesarJSON("JSON/IP.JSON");
        
        //Consulta por IP de maquina
        String ipMaquina = ConsultarIPMaquina();
        
        //Crear Socket Servidor
        Servidor servidor = new Servidor(ipMaquina,listaip);

        //Crear Socket Cliente
        Cliente cliente = new Cliente();

        // Crear HEBRA
        Main main = new Main();
        main.ipMaquina = ipMaquina;
        main.listaip = listaip;
        main.pacientes = pacientes;
        main.personal = personal;
        main.requerimientos = requerimientos;
        main.servidor = servidor;
        main.cliente = cliente;
        Runnable subproceso = main;
        new Thread(subproceso).start();
        
        System.out.print("\nIniciar: ");
        Scanner in = new Scanner(System.in);
        String ip = in.nextLine();
        

        List<Socket> listasockets = cliente.CrearSocket(ipMaquina, listaip);

        //El primer coordinador es la maquina con ip 10.4.60.169
        //las demas maquinas envian su mejor candidato para algortimo de bully
        System.out.println("\nAplicando Algortimo Bully...");
        Doctor candidato = personal.getMejorDoctor();
        main.candidatos.add(ipMaquina+";"+"Bully;"+String.valueOf(candidato.Estudios+candidato.Experiencia));
        EnviarCandidato(candidato,listasockets,ipMaquina,cliente);
        
        //El coordinador espera el inicio de otras MV
        if(ipMaquina.equals("10.6.40.169")){
            Thread.sleep(5000);
            String mensaje = EscogerCoordinador(main);
            System.out.println("[Algoritmo Bully] Nuevo Coordinador con IP: "+mensaje.split(";")[0]);
            System.out.println("[Algoritmo Bully] Avisando resultado..");
                cliente.EnviarBroadcast(mensaje, listasockets);
        }
        
    }
    
    /**
     *
     * @return
     */
    public static String ConsultarIPMaquina(){
        System.out.print("\nIngrese IP de la Maquina: ");
        Scanner in = new Scanner(System.in);
        String ip = in.nextLine();
        return ip;
    }
    
    public static void EnviarCandidato(Doctor candidato,List<Socket> listasockets,String ipmaquina,Cliente cliente) throws IOException{
        if(ipmaquina.equals("10.6.40.169") == false){
            String experiencia = String.valueOf(candidato.getEstudios()+candidato.getExperiencia());
            System.out.println("[Algortimo Bully] Enviando Candidato a Coordinador...");
            for(int i=0;i<listasockets.size();i++){
                if (listasockets.get(i).getInetAddress().getCanonicalHostName().equals("10.6.40.169")){
                    cliente.EnviarIndividual(ipmaquina+";"+"Bully;"+experiencia,listasockets.get(i));
                }
            }
        }
    }



    public static void ProcesarMensaje(Main main,String mensaje,List<String> candidatos, Socket socket){
        // mensaje: IP_EMISOR;CODIGO_MENSAJE;DATA_MENSAJE
        String IP_emisor = mensaje.split(";")[0];
        String Codigo = mensaje.split(";")[1];
        String Data = mensaje.split(";")[2];



        if (Codigo.equals("Bully")) {
            candidatos.add(mensaje);
            String escogido = EscogerCoordinador(main);
        } else if (Codigo.equals("R_Bully")) {
            System.out.println("[Algoritmo Bully] Resultado: Nuevo Coordinador con IP: " + mensaje.split(";")[0]);
            if (mensaje.split(";")[0].equals(main.ipMaquina)) {
                main.Is_Coordinador = true;
                System.out.println("[Algoritmo Bully] Esta maquina es el nuevo Coordinador");
            } else {
                main.Is_Coordinador = false;
            }
        }
        else if (Codigo.equals("LOG_REQUEST")){
            //Data: CARGO + NOMBRE_SOLICITANTE + ID_SOLICITANTE + ID_PACIENTE + ACCION;
            try {
                DataOutputStream response = new DataOutputStream(socket.getOutputStream());
                if (!main.LogInUse && main.waitQueue.peek() == null) {
                    main.LogInUse = true;
                    new Thread() {
                        public void run() {
                            try {
                                // Se inicia un thread auxiliar para escuchar cuando se termine de escribir el log y asi seguir escuchando request de los nodos
                                ServerSocket aux_ServerSocket = new ServerSocket(main.servidor.puerto + main.contador);
                                Socket aux_connSocket = aux_ServerSocket.accept();
                                DataInputStream aux_in = new DataInputStream(aux_connSocket.getInputStream());
                                String respuesta = aux_in.readUTF();

                                // Una vez se recibe una respuesta de confirmación se escribe el log local
                                String log_write = respuesta.split(";")[1];
                                FileWriter writer = new FileWriter("Log.txt", true);
                                writer.write(log_write);
                                writer.close();

                                // Se propaga el cambio en el log
                                main.cliente.EnviarBroadcast(IP_emisor+";LOG_UPDATE;"+log_write,main.cliente.SocketsExceptSelf(socket));

                                // Se libera el Log
                                main.LogInUse = false;
                            } catch (IOException e) {

                            }
                        }
                    }.start();
                    response.writeUTF(main.servidor.ipMaquina+";ACCEPTED;" + ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "+" + main.servidor.ipMaquina + "+" + (main.servidor.puerto + main.contador));
                    main.contador++;
                } else {
                    main.waitQueue.add(new WaitingRequest(Data.split("\\+")[0], Data));
                    response.writeUTF(main.servidor.ipMaquina+";DENIED; ");

                }

            }
            catch (IOException e) {}
        }
        else if (Codigo.equals("LOG_UPDATE")){
            try {
                FileWriter writer = new FileWriter("Log.txt", true);
                writer.write(Data);
                writer.close();
            }
            catch(IOException e){

            }
        }

        else if (Codigo.equals("NEWCOORDINATOR")){
            // [NEWCOORDINATOR];ip_coordinador+puerto_coordinador
            String IPCoordinador = Data.split("\\+")[0];
            main.cliente.coordinatorSocket = main.cliente.SocketAsociado(IPCoordinador);
            try {
                FileWriter writer = new FileWriter("Log.txt", true);
                writer.write("Nuevo coordinador seleccionado con IP:"+IPCoordinador);
                writer.close();
            }
            catch(IOException e){

            }
            main.cliente.newCoordinator = true;

        }


    }

    
    public static String EscogerCoordinador(Main main){
        if(main.candidatos.size() == 4){
            String ipCoordinador = main.candidatos.get(0).split(";")[0];
            int expCoordinador = Integer.parseInt(main.candidatos.get(0).split(";")[2]);
            for(int i=1;i<main.candidatos.size();i++){
                if(Integer.parseInt(main.candidatos.get(i).split(";")[2]) > expCoordinador){
                    ipCoordinador = main.candidatos.get(i).split(";")[0];
                    expCoordinador = Integer.parseInt(main.candidatos.get(i).split(";")[2]);
                }
            }
        return ipCoordinador+";R_Bully;"+String.valueOf(expCoordinador);
        }
        return null;
    }

    public void SolicitarArchivo(String cargo, String nombreApellido, int id_trabajador, int id_paciente,String accion, Cliente cliente){
        //REQUEST : IP_SOLICITANTE; LOG_REQUEST; DATA
        //  DATA: CARGO + NOMBRE_SOLICITANTE + ID_SOLICITANTE + ID_PACIENTE + ACCION;
        String request = ipMaquina+"[LOG_REQUEST];"+cargo+"+"+nombreApellido+"+"+id_trabajador+"+"+id_paciente+"+"+accion;
        try {
            cliente.EnviarIndividual(request, cliente.coordinatorSocket);
            String answer = cliente.RecibirIndividual(cliente.coordinatorSocket);
            String mensaje = answer.substring(answer.indexOf(";")+1);

            if(answer.substring(request.indexOf("[")+1,request.indexOf("]")).equals("ACCEPTED")){
                String hora = mensaje.split("\\+")[0];
                String IP_destino = mensaje.split("\\+")[1];
                int puerto_destino = Integer.parseInt(mensaje.split("\\+")[2]);
                FileWriter writer = new FileWriter("Log.txt",true);

                // Log Format : HORA_ESCRITURA CARGO_SOLICITANTE NOMBRE_SOLICITANTE SOLICITUD
                String log_write = hora+" "+cargo+" "+nombreApellido+" "+ request;
                writer.write(log_write);
                writer.close();
                Socket aux_socket = new Socket(IP_destino, puerto_destino);
                cliente.EnviarIndividual("[WRITED];"+log_write, aux_socket);
                aux_socket.close();
            }
            else{
                cliente.waitingLog = true;
                new Thread(){
                    public void run(){
                        try{
                            cliente.RecibirIndividual(cliente.coordinatorSocket);  // Es este el socket correcto?
                            cliente.waitingLog = false;
                        }
                        catch (IOException e) {

                        }
                    }
                }.start();
                while(cliente.waitingLog || !cliente.newCoordinator){
                    try {
                        wait(100);
                    }
                    catch (InterruptedException e){}
                }
                if (cliente.newCoordinator)
                {
                    cliente.newCoordinator = false;
                    SolicitarArchivo(cargo, nombreApellido, id_trabajador, id_paciente, accion, cliente); // Cambiar a socket del nuevo coordinador
                    return;
                }
                else{
                    String hora = mensaje.split("\\+")[0];
                    String IP_destino = mensaje.split("\\+")[1];
                    int puerto_destino = Integer.parseInt(mensaje.split("\\+")[2]);
                    FileWriter writer = new FileWriter("Log.txt",true);

                    // Log Format : HORA_ESCRITURA CARGO_SOLICITANTE NOMBRE_SOLICITANTE SOLICITUD
                    String log_write = hora+" "+cargo+" "+nombreApellido+" "+ request;
                    writer.write(log_write);
                    writer.close();
                    cliente.EnviarIndividual("[OK]", cliente.coordinatorSocket);


                }

            }

        }
        catch (IOException e){

        }

    }
    
    @Override
    public void run(){
        try {
            ServerSocket servidor = new ServerSocket(this.servidor.puerto);
            while(true){
                Socket socket = servidor.accept();
                DataInputStream mensaje = new DataInputStream(socket.getInputStream());
                String data = mensaje.readUTF();
                ProcesarMensaje(this,data,this.candidatos, socket);
                //System.out.println("\n"+data+"\n");
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
