package webServer.Model;

import webServer.Action.HistoryAction;
import java.rmi.RemoteException;

public class HistoryBean extends Bean{

    public HistoryBean() {
        super();
    }

    public String historyUser(String username) throws RemoteException {
        String str;
        String codigo = new String();
        codigo = codigo.concat("type | history ;");
        codigo = codigo.concat(" username | "+username);
        System.out.println(codigo);
        str = server.connectMulticast(codigo);
        System.out.println(str);
        str = str.replace("\n","<br>");
        str = str.replace("Historico de pesquisas: [", " ");
        str = str.replace(",","<br>");
        str = str.replace("]"," ");
        str = str.replace("type | status ; msg | historico enviado"," ");
        return str;
    }
}
