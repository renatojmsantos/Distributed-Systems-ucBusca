package RMIClient;

import RMIServer.Inter;
import RMIServer.Interface;
import RMIServer.RMIServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RMIClient extends UnicastRemoteObject implements Inter{
    private static Interface anInterface;
    private static Inter cinterface ;

    private static int RMIPort;
    private static  int RMIPortBackup;

    public void loadConfig() throws IOException {
        Properties p = new Properties();

        FileInputStream in = new FileInputStream("config.properties");
        // load from input stream
        p.load(in);
        // le do config e atribui
        this.RMIPort = Integer.parseInt(p.getProperty("rmiport"));
        this.RMIPortBackup = Integer.parseInt(p.getProperty("rmiportbackup"));
    }

    //Menu de registo/login
    public static void menu(RMIClient c) throws RemoteException{
        Scanner sc = new Scanner (System.in);
        int opcao=0;
        System.out.println("-------- MENU -------");
        System.out.println("1. Registar");
        System.out.println("2. Login");
        System.out.println("3. Pesquisar");
        while (true) {
            if (sc.hasNextInt()) {
                opcao = sc.nextInt();
                if (opcao == 1) {
                    RMIConnect(RMIPort, c);
                    Registar();
                } else if (opcao == 2) {
                    RMIConnect(RMIPort, c);
                    Login();
                } else if (opcao == 3) {
                    RMIConnect(RMIPort, c);
                    Search();
                } else {
                    System.out.println("Introduza um numero correto");
                    sc.nextLine();
                }
            }
        }
    }
    // segundo menu -> caso o login seja feito com sucesso
    public static void menu2(RMIClient c,String username) throws RemoteException {
        RMIConnect(RMIPort, c);
        Scanner sc = new Scanner(System.in);
        int opcao = 0;
        System.out.println("-------- BEM-VINDO AO UC BUSCA -------");
        System.out.println("1. Adicionar novo URL");
        System.out.println("2. Tornar Administrador");
        System.out.println("3. Pesquisar");
        System.out.println("4. Historico");
        System.out.println("5. Ligaçoes que apontam para uma Pagina");
        System.out.println("6. Página de administração - stats");
        System.out.println("7. Logout");
        while (true) {
            if (sc.hasNextInt()) {
                opcao = sc.nextInt();
                if (opcao == 1) {
                    RMIConnect(RMIPort, c);
                    Addurl(username);
                } else if (opcao == 2) {
                    RMIConnect(RMIPort, c);
                    Admin(username);
                } else if (opcao == 3) {
                    RMIConnect(RMIPort, c);
                    Searchuti(username);
                } else if (opcao == 4) {
                    RMIConnect(RMIPort, c);
                    Historico(username);
                } else if (opcao == 5) {
                    RMIConnect(RMIPort, c);
                    Listlinks(username);
                } else if (opcao == 6) {
                    RMIConnect(RMIPort, c);
                    Stats(username);
                } else if(opcao == 7){
                    RMIConnect(RMIPort, c);
                    Logout(username);
                }
                else {
                    System.out.println("Introduza um numero correto");
                    sc.nextLine();
                }
            }
        }
    }
    public static void Registar() throws RemoteException {
        String str;
        @SuppressWarnings("resource")
        Scanner keyboardScanner = new Scanner(System.in);

        // protocolo
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | regist ;");

        // pede dados ao utilizador
        System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" username | "+readKeyboard+" ;");

        System.out.println("password: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" password | "+readKeyboard);

        // comunica ao rmi server -> depois o rmi server pede ao multicast server
        str=anInterface.connectMulticast(codigo);
        // imprime resposta
        System.out.println(str);
        RMIClient c = null;
        // volta ao menu... para as mesmas 3 opcoes
        menu(c);
    }
    public static void Login() throws RemoteException {
        // mesmo "processo" que o registo
        String str;
        @SuppressWarnings("resource")
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | login ;");

        System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();

        String aux = readKeyboard;
        codigo = codigo.concat(" username | "+readKeyboard+" ;");

        System.out.println("password: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" password | "+readKeyboard);

        str=anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;

        if(str.equals("type | status ; msg | Login com sucesso ; admin | true") || str.equals("type | status ; msg | Login com sucesso ; admin | false") || str.equals("type | status ; msg | Login com sucesso ; notificacao | Tornou se administrador ; admin | true")  || str.equals("type | status ; msg | Login com sucesso ; notificacao | Tornou se administrador ; admin | false")){
            anInterface.subscribe(cinterface,aux);
            menu2(c,readKeyboard);
        }else{
            menu(c);
        }

    }
    public static void Addurl(String username) throws RemoteException {
        String str;
        @SuppressWarnings("resource")
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | add_url ;");
        System.out.println("url: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" url | "+readKeyboard+" ;");
        /*System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();*/
        codigo = codigo.concat(" username | "+username);
        str=anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;
        menu2(c,username);
    }
    public static void Listlinks(String username) throws RemoteException{
        String str;
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | list_links ;");
        System.out.println("url: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" url | "+readKeyboard+" ;");
        /*System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();*/
        codigo = codigo.concat(" username | "+username);
        str=anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;
        menu2(c,username);
    }
    public static void Search() throws RemoteException {
        String str;
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | search ;");
        System.out.println("pesquisa: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" search_term | "+readKeyboard);
        str = anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;
        menu(c);
    }
    public static void Historico(String username) throws RemoteException {
        String str;
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        //String readKeyboard;
        codigo = codigo.concat("type | history ;");
        /*System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();*/
        codigo = codigo.concat(" username | "+username);
        str = anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;
        menu2(c,username);
    }
    public static void Searchuti(String username) throws RemoteException {
        String str;
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | search ;");
        System.out.println("pesquisa: ");
        readKeyboard = keyboardScanner.nextLine();
        codigo = codigo.concat(" search_term | " + readKeyboard + " ;");
        /*System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();*/
        codigo = codigo.concat(" username | " + username);
        str = anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;
        menu2(c,username);
    }
    public static void Admin(String username) throws RemoteException {
        String str;
        @SuppressWarnings("resource")
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | give_admin ;");
        /*System.out.println("username1: ");
        readKeyboard = keyboardScanner.nextLine();*/

        codigo = codigo.concat(" username1 | "+username+" ;");
        System.out.println("username2: ");
        readKeyboard = keyboardScanner.nextLine();

        String aux = readKeyboard;
        codigo = codigo.concat(" username2 | "+readKeyboard);
        str=anInterface.connectMulticast(codigo);
        anInterface.adminCallback(aux);

        System.out.println(str);
        RMIClient c = null;
        menu2(c,username);
    }
    public static void Logout(String username) throws RemoteException {
        String str;
        @SuppressWarnings("resource")
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | logout ;");
        /*System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();*/

        String aux = username;
        codigo = codigo.concat(" username | "+username);
        str=anInterface.connectMulticast(codigo);
        anInterface.unsubscribe(aux);

        System.out.println(str);
        RMIClient c = null;
        menu(c);
    }
    public static void Stats(String username) throws RemoteException {
        String str;
        Scanner keyboardScanner = new Scanner(System.in);
        String codigo = new String();
        String readKeyboard;
        codigo = codigo.concat("type | stats_admin ;");
        /*System.out.println("username: ");
        readKeyboard = keyboardScanner.nextLine();*/
        codigo = codigo.concat(" username | "+username);
        str=anInterface.connectMulticast(codigo);
        System.out.println(str);
        RMIClient c = null;
        menu2(c,username);
    }
    public RMIClient() throws RemoteException {
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) throws RemoteException, MalformedURLException, NotBoundException {
        //System.setProperty("java.rmi.server.hostname","172.20.10.2");
        //System.getProperties().put("java.security.policy", "policy.all");

        RMIClient c = null;
        c = new RMIClient(); //para carregar configuracoes para RMI PORT e RMI PORT backup

        RMIConnect(RMIPort, c);
        cinterface=(Inter)c;
        menu(c);
    }

    private static void RMIConnect(int port, RMIClient c){
        try {
            anInterface = (Interface) LocateRegistry.getRegistry(port).lookup("ucbusca");
            //anInterface = (Interface) Naming.lookup("//localhost:7049/RMIServer");

            anInterface.verificaCliente();
        } catch (Exception e) { // servidor backup
            RMIConnect(RMIPortBackup, c);
        }
    }

    public String notificadmin(String s){
        System.out.println(s);
        return s;
    }
}
