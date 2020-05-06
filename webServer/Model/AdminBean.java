package webServer.Model;

import java.rmi.RemoteException;

public class AdminBean extends Bean {

    public AdminBean() {
        super();
    }
    public boolean giveAdmin(String user1,String newAdmin) throws RemoteException {
        System.out.println("giveAdmin");
        System.out.println(newAdmin);
        String str;
        String codigo = new String();
        System.out.println(user1);
        System.out.println(newAdmin);
        codigo = codigo.concat("type | give_admin ;");
        codigo = codigo.concat(" username1 | "+user1+" ;");
        codigo = codigo.concat(" username2 | "+newAdmin); //new admin
        System.out.println(codigo);
        str=server.connectMulticast(codigo);
        System.out.println(str);
        if(str.equals("type | status ; msg | O user agora2 e administrador") ){
            return true;
        }else {
            return false;
        }
    }

}
