package MulticastServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Webpage implements Serializable {
    private static final long serialVersionUID = 2917824285517909218L;

    private String url;
    private String title; // titulo da pagina
    private String text; // descricao, se tiver. Se nao, fica o texto presente na pagina web

    private ArrayList<String> link = new ArrayList<>(); //links que website introduzido tem
    private ArrayList<String> pals = new ArrayList<>(); // palavras presentes no website introduzido

    public Webpage(){}

    public Webpage(String url, String title, String text) {
        this.url = url;
        this.title = title;
        this.text = text;
    }

    public Webpage(String url, String title, String text, ArrayList<String> link, ArrayList<String> pals) {
        this.url = url;
        this.title = title;
        this.text = text;
        this.link = link;
        this.pals = pals;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getLink() {
        return link;
    }

    public void setLinks(ArrayList<String> link) {
        this.link = link;
    }

    public ArrayList<String> getPals() {
        return pals;
    }

    public void setPals(ArrayList<String> pals) {
        this.pals = pals;
    }

    @Override
    public String toString() {
        return "Webpage{" +
                "url='" + url + '\'' + "\n" +
                ", title='" + title + '\'' +"\n" +
                ", text='" + text + '\'' +"\n" +
                '}' + "\n";
    }
}
