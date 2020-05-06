package RMIServer;

import java.io.IOException;
import java.rmi.*;

public interface Interface extends Remote {
    //public void verificaCliente() throws RemoteException;

    public String connectMulticast(String s) throws RemoteException;
    public String getMessage() throws RemoteException;
    public void subscribe(Inter cinterface,String nome) throws RemoteException;
    public void unsubscribe(String nome) throws RemoteException;
    public void adminCallback(String s) throws java.rmi.RemoteException;

    public String verificaCliente() throws RemoteException;
    //public void loadConfig() throws IOException;
}
