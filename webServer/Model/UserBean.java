package webServer.Model;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

import uc.sd.apis.FacebookApi2;
import RMIServer.Inter;
import RMIServer.Interface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Scanner;

public class UserBean extends Bean {
    private static Inter cInter = null;

    private boolean isAdmin = false;
    private String username = null; // username and password supplied by the user
    private String password = null;

    private String facebookID = null;
    private String facebookToken = null;

    public UserBean() {
        super();
    }

    public String registerAccount() throws RemoteException {
        //String str = null;
        @SuppressWarnings("resource")

        String codigo = "";
        codigo = codigo.concat("type | regist ;");
        codigo = codigo.concat(" username | "+username+" ;");
        codigo = codigo.concat(" password | "+password);
        // comunica ao rmi server -> depois o rmi server pede ao multicast server
        //str=server.connectMulticast(codigo);
        System.out.println(codigo);
        String s = "";
        s = server.connectMulticast(codigo);
        System.out.println(s);
        return s;
        // imprime resposta
        //System.out.println(str);
        //return str;
    }

    public String loginAccount() throws RemoteException {
        //System.out.println("logingaccount");
        String str;
        @SuppressWarnings("resource")
        String codigo = new String();
        System.out.println(username + password);
        codigo = codigo.concat("type | login ;");
        codigo = codigo.concat(" username | " + username + " ;");
        codigo = codigo.concat(" password | " + password);

        //System.out.println(codigo);
        str = server.connectMulticast(codigo);
        System.out.println(str);
        if (str.equals("type | status ; msg | Login com sucesso ; admin | false")) {
            server.subscribe((Inter) cInter, username);
            return "true";
        }else if (str.equals("type | status ; msg | Login com sucesso ; admin | true")) {
            server.subscribe((Inter) cInter, username);
            return "trueadmin";
        }else if(str.equals("type | status ; msg | Login com sucesso ; notificacao | Tornou se administrador ; admin | true")){
            server.subscribe((Inter) cInter, username);
            return "notificacao";
        }else {
            return "false";
        }
    }

    public String logoutAccount(String username) throws RemoteException {
        System.out.println("logoutAccount");
        String str;
        @SuppressWarnings("resource")
        String codigo = new String();
        codigo = codigo.concat("type | logout ;");
        String aux = username;

        codigo = codigo.concat(" username | "+username);
        str=server.connectMulticast(codigo);
        server.unsubscribe(aux);

        System.out.println(str);
        return str;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
