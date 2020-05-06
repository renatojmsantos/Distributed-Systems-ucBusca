package webServer.Action;

import webServer.Model.AddLinkBean;
import java.io.IOException;

public class AddLinkAction extends BeanAction {
    private AddLinkBean addLinkBean  = new AddLinkBean();
    private String newURL = null;

    public String addLink() throws IOException {
        System.out.println(this.newURL);

        if(this.addLinkBean.addLink(this.getUsername(),this.newURL)){
            return SUCCESS;
        }else{
            return "error";
        }
    }
    public String getUsername() {
        String aux = String.valueOf(session.get("username"));
        return aux;
    }

    public AddLinkBean getAddLinkBean() {
        return addLinkBean;
    }

    public void setAddLinkBean(AddLinkBean addLinkBean) {
        this.addLinkBean = addLinkBean;
    }

    public String getNewURL() {
        return newURL;
    }

    public void setNewURL(String newURL) {
        this.newURL = newURL;
    }
}
