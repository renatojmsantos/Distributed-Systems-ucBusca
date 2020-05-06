package webServer.Action;

import webServer.Model.HistoryBean;
import webServer.Model.SearchUserBean;

import java.io.IOException;

public class HistoryAction extends BeanAction {

    private HistoryBean history = new HistoryBean();

    private String result = new String();

    public String historyUser() throws IOException {
        System.out.println(session.toString());
        System.out.println(session.get("username"));
        System.out.println(getUserBean().getUsername());
        setResult(this.history.historyUser(this.getUsername()));
        return "success";
    }

    public String getUsername(){
        String aux = String.valueOf(session.get("username"));
        return aux;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public HistoryBean getHistory() {
        return history;
    }

    public void setHistory(HistoryBean history) {
        this.history = history;
    }
}
