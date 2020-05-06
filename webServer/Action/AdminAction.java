package webServer.Action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webServer.Model.AdminBean;
import webServer.Model.UserBean;

import java.io.IOException;
import java.util.Map;

public class AdminAction extends BeanAction{

    private AdminBean adminBean = new AdminBean();
    private String newAdmin = null;

    public String giveAdmin() throws IOException {
        System.out.println(this.newAdmin);
        //this.adminBean.setNewAdmin(this.newAdmin);
        if(this.adminBean.giveAdmin(this.getUsername(),this.newAdmin)){
            return SUCCESS;
        }else{
            return "error";
        }
    }
    public String getUsername() {
        String aux = String.valueOf(session.get("username"));
        return aux;
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public String getNewAdmin() {
        return newAdmin;
    }

    public void setNewAdmin(String newAdmin) {
        this.newAdmin = newAdmin;
    }
}
