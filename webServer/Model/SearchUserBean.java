package webServer.Model;

import MulticastServer.Webpage;
import RMIClient.RMIClient;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import webServer.Action.SearchUserAction;
import webServer.Action.UserAction;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class SearchUserBean extends Bean {

    public SearchUserBean() {
        super();
    }

    private static final String APIkey = "trnsl.1.1.20191215T010954Z.34fcedd4a8857fbc.76ba5cc22307a6f9fb58ee246a4e9931634c2129";
    private static final String requestDetect = "https://translate.yandex.net/api/v1.5/tr/detect";
    private static final String requestTranslate = "https://translate.yandex.net/api/v1.5/tr/translate";

    public ArrayList<Webpage> lista = new ArrayList<Webpage>();
    private ArrayList<Webpage> translated = new ArrayList<Webpage>();

    public ArrayList<Webpage> searchUser(String searchT, String username) throws RemoteException {
        String str;
        String codigo = new String();
        codigo = codigo.concat("type | search ;");
        codigo = codigo.concat(" search_term | " + searchT + " ;");
        System.out.println(username);
        codigo = codigo.concat(" username | " + username);
        System.out.println(codigo);
        str = server.connectMulticast(codigo);
        //System.out.println(str);
        //str = str.replace("\n","<br>");
        //str = str.replace("type | status ; msg | Pesquisa realizada c/ conta"," ");
        //return str;
        String u,d,t;
        String[] str1  = str.split("\n");
        /*
        for(int i = 0 ; i< str1.length ; i++){
            System.out.println(str1[i]);
        }*/
        int limite = (str1.length-1)/4;
        for(int i = 0 ; i< limite ; i++){
            t = str1[i*4];
            u = str1[i*4+1];
            d =str1[i*4+2];
            Webpage web = new Webpage(u,t,d);
            lista.add(web);
        }
        return translation();
    }


    //https://www.codota.com/code/java/methods/java.net.HttpURLConnection/getResponseCode
    // slides - ficha 8
    private void connectionHttpURL(HttpURLConnection con) throws IOException {
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestProperty("Accept","application/xml");
        con.connect();
        //https://stackoverflow.com/questions/9129766/urlconnection-is-not-allowing-me-to-access-data-on-http-errors-404-500-etc
        if (con.getResponseCode() >= 300) {
            con.getErrorStream();
        }
    }

    // https://tech.yandex.com/translate/doc/dg/reference/detect-docpage/
    private String detectLanguage(String text){
        try {
            URL url = new URL(requestDetect + "?key=" + APIkey + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8.toString()));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            connectionHttpURL(con);

            InputSource is = new InputSource(con.getInputStream());
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList no = (NodeList) xpath.evaluate("//@lang", is, XPathConstants.NODESET);
            con.disconnect();

            if (no.getLength() > 0)
                return no.item(0).getNodeValue();
        } catch (IOException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //https://tech.yandex.com/translate/doc/dg/reference/translate-docpage/
    private String traduzirTexto(String text, String lang){
        try {
            URL url = new URL(requestTranslate + "?key=" + APIkey + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8.toString()) + "&lang=" + lang);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            connectionHttpURL(con);

            InputSource is = new InputSource(con.getInputStream());
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList no = (NodeList) xpath.evaluate("/Translation/text", is, XPathConstants.NODESET);
            con.disconnect();
            if (no.getLength() > 0)
                return no.item(0).getFirstChild().getNodeValue();
        } catch (IOException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // traduz o titulo e a descricao
    public ArrayList<Webpage> translation(){
        for(Webpage wp : lista){
            String lingTitulo = detectLanguage(wp.getTitle());
            /*
            //emoji FLAG
            StringBuffer sb = new StringBuffer();
            sb.append(Character.toChars(127467));
            sb.append(Character.toChars(127479));
            //System.out.println(sb);
             */
            String tituloTraduzido = traduzirTexto(wp.getTitle(),"pt") + "      ["+lingTitulo.toUpperCase() +"]";
            String translatedDescription = traduzirTexto(wp.getText(), "pt");
            translated.add(new Webpage(wp.getUrl(), tituloTraduzido, translatedDescription));
        }
        return translated;
    }

}