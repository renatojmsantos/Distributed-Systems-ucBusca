package webServer.Action;

import webServer.Model.HistoryBean;
import webServer.Model.StatsAdminBean;

import java.io.IOException;

public class StatsAdminAction extends BeanAction{

    private StatsAdminBean stats = new StatsAdminBean();

    private String result = null;

    public String statsAdmin() throws IOException {
        System.out.println(session.toString());
        System.out.println(getUserBean().getUsername());
        setResult(this.stats.statsAdmin(this.getUsername()));
        return "success";
    }

    public String getUsername(){
        String aux = String.valueOf(session.get("username"));
        return aux;
    }

    public StatsAdminBean getStats() {
        return stats;
    }

    public void setStats(StatsAdminBean stats) {
        this.stats = stats;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
