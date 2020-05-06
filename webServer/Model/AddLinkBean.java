package webServer.Model;

import java.rmi.RemoteException;

public class AddLinkBean extends Bean {
    public AddLinkBean() {
        super();
    }

    public boolean addLink(String user, String newUrl) throws RemoteException {
        System.out.println("addLink");
        System.out.println(newUrl);
        String str;
        String codigo = new String();
        codigo = codigo.concat("type | add_url ;");
        codigo = codigo.concat(" url | "+newUrl+" ;");
        codigo = codigo.concat(" username | "+user);
        System.out.println(codigo);
        str=server.connectMulticast(codigo);
        System.out.println(str);
        if(str.equals("type | status ; msg | Url adicionado com sucesso") ){
            return true;
        }else {
            return false;
        }
    }
}
