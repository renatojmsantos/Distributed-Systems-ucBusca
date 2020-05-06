package RMIServer;

import MulticastServer.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CopyOnWriteArrayList;



import java.io.FileInputStream;
import java.util.*;

public class RMIServer extends UnicastRemoteObject implements Interface {

    private static final long serialVersionUID = 1L;
    private String MULTICAST_ADDRESS;
    private int PORT;
    private static int RMIPort;
    private static  int RMIPortBackup;
    private static boolean master;
    MulticastSocket socket = null;

    private static Inter cinterface;
    private static CopyOnWriteArrayList<User> logged = new CopyOnWriteArrayList(); //LOG

    public RMIServer() throws RemoteException {
        super();
        try {
            loadConfig();
            //System.out.println(MULTICAST_ADDRESS);
            //System.out.println(RMIPort);
            //System.out.println(RMIPortBackup);
            //System.out.println(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() throws IOException{
        Properties p = new Properties();

        FileInputStream in = new FileInputStream("config.properties");

        // load from input stream
        p.load(in);
        this.MULTICAST_ADDRESS = p.getProperty("multicast_address");
        this.PORT = Integer.parseInt(p.getProperty("port"));
        this.RMIPort = Integer.parseInt(p.getProperty("rmiport"));
        this.RMIPortBackup = Integer.parseInt(p.getProperty("rmiportbackup"));
    }

    public String verificaCliente() throws RemoteException{
        System.out.println("Cliente ligado"); // RMI client liga-se ao RMI server
        return "Cliente ligado";
    }

    public String getMessage() throws RemoteException{
        String s;
        s = "Conexao realizada com sucesso\n";
        return s;
    }

    //função para ligar o RMI server
    private static RMIServer connect(RMIServer serv){
        try {
            serv = new RMIServer();
            Registry r = LocateRegistry.createRegistry(RMIPort); //RMIPORT
            r.rebind("ucbusca", serv);
            System.out.println("Master Server ready on port " + RMIPort);

            master = true; // servidor principal
        } catch (RemoteException re) {
            //System.out.println("Exception in RMIServer.main: " + re);
            master = false;
        }
        if (master) {
            // se for o servidor principal, fica no servidor principal
            return serv;
        }
        else {
            // se nao, testa o servidor secundario de backup -> visto que houve algo q falhou no servidor principal
            try{
                serv = new RMIServer();
                Registry r = LocateRegistry.createRegistry(RMIPortBackup);
                r.rebind("ucbusca", serv);
                RMIPort = RMIPortBackup;
                System.out.println("Backup server ready on port " + RMIPortBackup);
            }catch (RemoteException re){
                System.out.println("Exception in RMIServer.main: " + re);
            }
            return  serv;
        }
    }

    // troca de informação entre o RMIServer e o Multicast Server
    public String connectMulticast(String s) throws RemoteException{
        //System.out.println("connectMulticast");
        String mensagem = new String();
        try {
            //System.out.println("connectMulticast2");

            //System.out.println("1");
            // create socket and bind it
            socket = new MulticastSocket(PORT);
            System.out.println(s);
            // vai buscar a informacao que esta na String s
            byte[] buffer = s.getBytes(); // coloca no buffer
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            // Datagram Packet UDP
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            // envia informacao atraves do socket
            socket.send(packet);
            //System.out.println(packet);

            while(packet.getPort() == PORT) {
                System.out.println("connecting Multicast...");
                //System.out.println("3");
                // se o PORT do packet for igual ao PORT do multicast server
                buffer = new byte[10000000];
                // novo DatagramPacket UDP para receber a informacao vinda do multicast server
                packet = new DatagramPacket(buffer, buffer.length);

                //socket.setTimeToLive(255); // ?
                //System.out.println(packet.getPort()+" *socketS "+socket.getLocalPort()+"*");
                // recebe informacao
                socket.receive(packet);
                //System.out.println("packet received");
                // transforma os dados recebidos numa String para serem impressos
                mensagem = new String(packet.getData(), 0, packet.getLength());
                // imprime mensagem recebida
                System.out.println(mensagem);

                System.out.println("... connected Multicast");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mensagem;
    }

    public void adminCallback(String s) throws RemoteException {
        String message = "";
        System.out.println(message);

        for (User l : logged) {
            if (l.getUsername().compareTo(s) == 0) {
                l.notifica();
            }
        }
    }

    //serve para saber qual o cliente
    public void subscribe(Inter cinterface,String nome) throws RemoteException{
        User o = new User(cinterface,nome);
        logged.add(o);
    }

    public void unsubscribe(String nome) throws  RemoteException{
        User aux = null;
        for(User l:logged){
            if(l.getUsername().compareTo(nome) == 0){
                aux = l;
            }
        }
        logged.remove(aux);
    }

    public static void main (String args[]){
        //System.setProperty("java.rmi.server.hostname","172.20.10.2");
        //System.getProperties().put("java.security.policy", "policy.all");
        RMIServer serv =  null;
        connect(serv);
    }
}
