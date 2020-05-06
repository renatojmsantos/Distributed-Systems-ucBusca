package webServer.Action;

import MulticastServer.Webpage;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.SessionMap;
import webServer.Model.SearchBean;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class SearchAction extends ActionSupport {

    SearchBean search = new SearchBean();
    private String searchTerm = null;
    private ArrayList<Webpage> searchResults = new ArrayList<>();

    public String search() throws IOException {
        setSearchResults(this.getSearch().search(this.searchTerm));
        return "success";
    }

    public ArrayList<Webpage> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<Webpage> searchResults) {
        this.searchResults = searchResults;
    }

    public SearchBean getSearch() {
        return search;
    }

    public void setSearch(SearchBean search) {
        this.search = search;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
