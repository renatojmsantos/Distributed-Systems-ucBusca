package RMIServer;

import java.rmi.*;

public interface Inter extends Remote {
    //public void print_on_client(String s) throws java.rmi.RemoteException;
    public String notificadmin(String s) throws java.rmi.RemoteException;
}


