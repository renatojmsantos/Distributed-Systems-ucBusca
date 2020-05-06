package webServer.Model;

import RMIServer.Inter;
import RMIServer.Interface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.Properties;

public class Bean implements Serializable {
    protected Interface server = null; //RMISERVER
    //private static Inter cinterface = null;
    private static int RMIPort;
    private static  int RMIPortBackup;

    private Map<String, Object> session;

    public Bean(){
        //System.getProperties().put("java.security.policy", "policy.all");
        try {
            Properties p = new Properties();
            FileInputStream in = new FileInputStream("config.properties");
            // load from input stream
            p.load(in);
            // le do config e atribui
            this.RMIPort = Integer.parseInt(p.getProperty("rmiport"));
            this.RMIPortBackup = Integer.parseInt(p.getProperty("rmiportbackup"));

            //server = (Interface) Naming.lookup("ucbusca");
            server = (Interface) LocateRegistry.getRegistry(RMIPort).lookup("ucbusca");
            server.verificaCliente(); // nao esta a imprimir... no RMI SERVER: "cliente ligado"
            //verificaCliente();
            System.out.println(".................Conectado! ........................");
        }catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Interface getServer() {
        return server;
    }

    public void setServer(Interface server) {
        this.server = server;
    }
}
