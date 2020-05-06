package webServer.Model;

import RMIClient.RMIClient;

import java.rmi.RemoteException;
import java.util.Scanner;

public class LinksPageBean extends Bean {
    public LinksPageBean() {
        super();
    }

    public String listLinks(String username, String URL) throws RemoteException {
        String str;
        String codigo = new String();
        codigo = codigo.concat("type | list_links ;");
        codigo = codigo.concat(" url | "+URL+" ;");
        codigo = codigo.concat(" username | "+username);
        System.out.println(codigo);
        str=server.connectMulticast(codigo);
        System.out.println(str);
        str = str.replace("<---","<br>--------------------------------------------<br>");

        str = str.replace("[","");
        str = str.replace(",","<br>");
        str = str.replace("]","");
        str = str.replace("type | status ; msg | printed list of links to server"," ");
        return str;
    }
}
