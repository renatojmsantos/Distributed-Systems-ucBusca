package webServer.Action;

import webServer.Action.LinksPageAction;
import webServer.Model.LinksPageBean;
import webServer.Model.SearchUserBean;

import java.io.IOException;

public class LinksPageAction extends BeanAction {

    private LinksPageBean linksPage = new LinksPageBean();
    private String url = "";
    private String result = "";

    public String listLinks() throws IOException {
        System.out.println(this.url);
        System.out.println(session.toString());
        setResult(this.linksPage.listLinks(this.getUsername(), this.url));
        session.put("links",true);
        return "success";
    }

    public String getUsername(){
        String aux = String.valueOf(session.get("username"));
        return aux;
    }

    public LinksPageBean getLinksPage() {
        return linksPage;
    }

    public void setLinksPage(LinksPageBean linksPage) {
        this.linksPage = linksPage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
