package webServer.Model;

import RMIClient.RMIClient;

import java.rmi.RemoteException;
import java.util.Scanner;

public class StatsAdminBean extends Bean{

    public StatsAdminBean() {
        super();
    }

    public String statsAdmin(String username) throws RemoteException {
        String str;
        String codigo = new String();
        codigo = codigo.concat("type | stats_admin ;");
        codigo = codigo.concat(" username | "+username);
        System.out.println(codigo);
        str=server.connectMulticast(codigo);
        System.out.println(str);
        str = str.replace("\n","<br>");
        str = str.replace("type | status ; msg |  sent ..... top 10 pages"," ");
        str = str.replace("type | status ; msg |  sent ..... top 10 pesquisas mais comuns", " ");
        str = str.replace("type | status ; msg |  sent ..... stats admin"," ");
        return str;
    }
}
