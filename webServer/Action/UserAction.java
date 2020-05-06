package webServer.Action;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uc.sd.apis.FacebookApi2;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webServer.Model.UserBean;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class UserAction extends BeanAction{
    private static final long serialVersionUID = 4L;

    private String username = null, password = null;
    private String facebookID = null;


    public String registar() throws IOException {
        System.out.println("registar");
        if(this.username != null && !this.username.equals("")){
            this.getUserBean().setUsername(this.username);
            this.getUserBean().setPassword(this.password);
            String aviso = this.getUserBean().registerAccount();
            System.out.println(aviso);
            if(aviso.equals("type | status ; msg | Utilizador registado. Inicie sessao...")){
                session.put("aviso", false);
                System.out.println("novo user");
                return SUCCESS;
            }else {
                System.out.println("user ja registado...");
                session.put("aviso", true);
                return "error";
            }
        }else {
            return "error";
        }
    }

    public String login() throws IOException {
        System.out.println("login");
        System.out.println(this.username + this.password);
        this.getUserBean().setUsername(this.username);
        this.getUserBean().setPassword(this.password);
        System.out.println(this.username + this.password);
        String resposta = this.getUserBean().loginAccount();
        if (resposta.equals("trueadmin")) {
            session.put("username", this.username);
            session.put("password", this.password);
            session.put("loggedin", true);// this marks the user as logged in
            session.put("notificacao", false);
            session.put("aviso", false);
            session.put("admin", true);
            //session.put("message", "Login successful!");
            return SUCCESS;
        }else if (resposta.equals("true")) {
            session.put("username", this.username);
            session.put("password", this.password);
            session.put("loggedin", true);// this marks the user as logged in
            session.put("notificacao", false);
            session.put("aviso", false);
            session.put("admin", false);
            //session.put("message", "Login successful!");
            return SUCCESS;
        }else if(resposta.equals("notificacao")){
            session.put("username", this.username);
            session.put("password", this.password);
            session.put("loggedin", true);// this marks the user as logged in
            session.put("notificacao", true);
            session.put("aviso", false);
            session.put("admin", true);
            //session.put("message", "Login successful!");
            return SUCCESS;
        }else {
            session.put("aviso", true);
            return "error";
        }
    }

    public String logout() throws IOException {
        System.out.println("logout");
        this.session.remove("loggedin");
        this.session.remove("notificacao");
        this.session.remove("aviso");
        this.session.remove("userBean");
        System.out.println(session.get("username"));
        String aux = String.valueOf(session.get("username"));
        System.out.println(aux);
        this.getUserBean().logoutAccount(aux);
        return SUCCESS;
    }


    public String loginFacebook() throws IOException {
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi2.class)
                .apiKey("852831725181876")
                .apiSecret("1efbf81075593bfc46aa48a1103a3912")
                .callback("http://localhost:8080/ucBusca/loginFacebook")
                .scope("public_profile")
                .build();

        Scanner in = new Scanner(System.in);
        String authorizationUrl = service.getAuthorizationUrl(null);
        System.out.println(authorizationUrl);

        System.out.println("paste code ");
        Verifier v = new Verifier(in.nextLine());

        Token accessToken = service.getAccessToken(null, v);
        System.out.println("token: " + accessToken);

        OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me", service);
        service.signRequest(accessToken, request);
        Response response = request.send();

        System.out.println(response.getCode());
        System.out.println(response.getBody());

        JSONParser jsonParser = new JSONParser();
        if (response.getCode() == 200){
            //login
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
                this.username = (String) jsonObject.get("username");
                System.out.println("Name from json:" + this.username);
                this.password = "h89y7y7y7y89yh89huiebuifbwuh89h983h890";
                //login, registo...
                return SUCCESS;
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "error";
    }

    private void setLoginBean(UserBean userBean) {
        session.put("UserBean", userBean);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
