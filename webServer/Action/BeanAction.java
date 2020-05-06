package webServer.Action;

import RMIServer.Inter;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;
import RMIServer.Interface;
import webServer.Model.SearchUserBean;
import webServer.Model.UserBean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.Properties;

public abstract class BeanAction extends ActionSupport implements SessionAware {
    protected Map<String, Object> session;

    public String execute() throws Exception{
        return "success";
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = (Map<String, Object>) session;
    }

    public UserBean getUserBean() {
        if (!session.containsKey("userBean")) {
            this.setUserBean(new UserBean());
        }
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }
}
