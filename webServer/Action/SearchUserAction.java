package webServer.Action;

import MulticastServer.Webpage;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import webServer.Model.SearchBean;
import webServer.Model.SearchUserBean;
import webServer.Model.UserBean;
import webServer.Action.SearchUserAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SearchUserAction extends BeanAction {

    private SearchUserBean searchUser = new SearchUserBean();
    private String searchT = "";
    private ArrayList<Webpage> result = new ArrayList<>();

    public String searchUser() throws IOException {
        /*
        System.out.println(this.searchT);
        System.out.println(session.toString());
        System.out.println(session.get("username"));
        System.out.println(getUserBean().getUsername());
        */
        //setResult(this.searchUser.searchUser(this.searchT, this.getUsername()));
        session.put("search",true);
        return "success";
    }

    public String backWelcomePage(){
        session.put("search",false);
        return "success";
    }
    public ArrayList<Webpage> getResult() {
        return result;
    }

    public void setResult(ArrayList<Webpage> result) {
        this.result = result;
    }

    public String getUsername(){
        String aux = String.valueOf(session.get("username"));
        return aux;
    }

    public SearchUserBean getSearchUser() {
        return searchUser;
    }

    public void setSearchUser(SearchUserBean searchUser) {
        this.searchUser = searchUser;
    }


    public String getSearchT() {
        return searchT;
    }

    public void setSearchT(String searchT) {
        this.searchT = searchT;
    }


}
