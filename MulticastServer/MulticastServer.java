package MulticastServer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.stream.Collectors;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author renatosantos
 * @author pedrosimoes
 */

public class MulticastServer extends Thread implements Serializable {
    private String MULTICAST_ADDRESS;
    private int PORT;

    private HashMap<String, Integer> serversMultiCastActive = new HashMap<>();

    private static ArrayList<User> users = new ArrayList<>(); //todos os utilizadores
    private static ArrayList<User> login = new ArrayList<>(); // utilizadores com login feito

    private static ArrayList<Webpage> webArray = new ArrayList<>(); //todos os websites

    //private static HashSet<String> link = new HashSet<>(); //
    //private static HashSet<String> pals = new HashSet<>();

    private static  HashSet<String> websites = new HashSet<>();

    //private static HashMap<String, ArrayList<Webpage>> crawler = new HashMap<>(); // url -> site1, url-> site2

    // index invertido. url -> [url1,url2] ... url aponta para url1 e para url2
    private static HashMap<String, HashSet<String>> inverted = new HashMap<String, HashSet<String>>();


    private Webpage wp;

    // carrega configuracoes para nao ser necessario recompilacao
    public void loadConfig() throws IOException {
        Properties p = new Properties();
        FileInputStream in = new FileInputStream("config.properties");
        p.load(in);
        this.MULTICAST_ADDRESS = p.getProperty("multicast_address");
        this.PORT = Integer.parseInt(p.getProperty("port"));
    }

    // vai buscar as palavras do website
    public void countWords(String text, HashSet<String> pals) {
        //pals = new HashSet<>();
        Map<String, Integer> countMap = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
        String line;

        // Get words and respective count
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
                String[] words = line.split("[ ,;:.?!“”(){}\\[\\]<>']+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if ("".equals(word)) {
                        continue;
                    }
                    if (!countMap.containsKey(word)) {
                        countMap.put(word, 1);
                    } else {
                        countMap.put(word, countMap.get(word) + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Close reader
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Display words and counts
        for (String word : countMap.keySet()) {
            if (word.length() >= 3) { // Shall we ignore small words?
                pals.add(word);
            }
        }
    }

    //adiciona url
    public void addLink(String ws, String firstLINK) {
        //link = new HashSet<>();
        HashSet<String> ligacao = new HashSet<>();
        HashSet<String> pals = new HashSet<>();

        try {
            System.out.println("jsoup URL: " + ws);
            if (!ws.startsWith("http://") && !ws.startsWith("https://"))
                ws = "http://".concat(ws);

            // Attempt to connect and get the document
            Document doc = Jsoup.connect(ws).timeout(10000).get();  // Documentation: https://jsoup.org/

            // Title
            System.out.println("jsoup Titulo: " + doc.title());

            // Get all links
            Elements links = doc.select("a[href]");
            //System.out.println("all links:" + links);
            for (Element l : links) {
                // Ignore bookmarks within the page
                if (l.attr("href").startsWith("#")) {
                    continue;
                }
                // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
                if (!l.attr("href").startsWith("http")) {
                    continue;
                }
                // impressao dos links
                //System.out.println("Link w: " + l.attr("href"));

                //System.out.println("Text: " + l.text() + "\n");
                //adiciona link relacionado com o link principal
                // link -> [link1, link2, link3, ...]
                //link.add(l.attr("href"));
                ligacao.add(l.attr("href"));
                //System.out.println(l.attr("href"));
                //System.out.println(l.attr("href"));
            }
            //http://ucx.dei.uc.pt/sd/entry.html
            /*
           */
            // imprime as palavras e o nr de vezes que aparecem
            // Get website text and count words
            String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
            //System.out.println("------> " + text);
            countWords(text, pals);
            // https://codesjava.com/jsoup-get-metadata-from-html-example
            // limita a 40 caracteres
            //text = text.substring(0, Math.min(text.length(), 40));

            /*
            if (text.length() == 0){
                text = "";
            }*/

            String s = "";
            try {
                // descricao do site ... caso tenha
                s = doc.select("meta[name=description]").get(0).attr("content");
            } catch (Exception e) {
                s = text; //texto do body
            }

            // convert hashset to arraylist
            /*
            if(ligacao.isEmpty()){
                ligacao.add("");
            }*/

            //http://ucx.dei.uc.pt/sd/entry.html

            System.out.println(ligacao);
            /*
            ArrayList<String> li = new ArrayList<String>();
            if(ligacao.isEmpty()){
                System.out.println("vaziooo");
                return;
            }else {
                li = new ArrayList<String>(ligacao);
            }
           */

            //System.out.println(pals);

            ArrayList<String> li = new ArrayList<String>(ligacao);
            ArrayList<String> p = new ArrayList<String>(pals);

            //System.out.println(p);
            /*ArrayList<String> k = new ArrayList<>();
            try {
                String keywords = doc.select("meta[name=keywords]").first().attr("content");
                k.add(keywords);
            }catch (Exception e){
                k = p;
            }*/

            // palavras
            //System.out.println("Palavras jsoup = " + p);
            websites.add(ws); //hashmap

            //if(!ws.equals(wp.getUrl())){
            //}
            //Webpage webpage = new Webpage(ws, doc.title(), s, li, p);
            wp = new Webpage(ws, doc.title(), s, li, p); // cria nova webpage .. s = texto

            webArray.add(wp); //adiciona o website ao arraylist com todas as informacoes encontradas
            writeWebsitesOBJ(webArray); // escreve no fich obj


            // --- inverter -----
            //System.out.println("LINKS wp = " + ligacao);
            System.out.println("LINKS w = " + wp.getLink());
            //System.out.println("sites = " + websites);

            // ws(l1) -> [l2,l3,l4] ==> l2=[ws], l3 = [ws], l3=[ws]
            // tinyurl.com/sistemas19

            //System.out.println("ws = " + ws + "\nfirst link = " + firstLINK);
            if(ws.equals(firstLINK)){
                System.out.println("primeiro link...");
                //continue;
            }
            else {

                for (int i = 0; i < wp.getLink().size(); i++) {
                    HashSet<String> invertedLinks = new HashSet<>();
                    if (inverted.containsKey(wp.getLink().get(i))) {  // if ligacoes contem URL ja adicionado... entao ja foi adicionado como KEY do inverted
                        invertedLinks = inverted.get(wp.getLink().get(i));
                        invertedLinks.add(ws);
                        //System.out.println("inverted links = " + invertedLinks);
                        //inverted.put(wp.getLink().get(i),invertedLinks);
                    } else {
                        HashSet<String> l = new HashSet<>();
                        l.add(ws);
                        inverted.put(wp.getLink().get(i), l);
                    }
                    //System.out.println("inverted links = " + invertedLinks);
                }
            }
            // tinyurl.com/sistemas19

            writeInvertedOBJ(inverted); // escreve ficheiro obj
            //System.out.println("----- INVERTIDOS -------\n"+inverted);

        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
        }
        //System.out.println(webArray);
    }


    // tinyurl.com/sistemas19

    // -> adicionar URLS encontrados num HASHSET numa fila.... enquanto a fila nao for vazia, vai adicionando
    public void addALL(String url){
        String firstLINK = "";
        firstLINK = url;
        if (!firstLINK.startsWith("http://") && !firstLINK.startsWith("https://"))
            firstLINK = "http://".concat(firstLINK);

        addLink(url, firstLINK);

        //PriorityQueue<String> queue = new PriorityQueue<String>();
        // queue
        //Queue<String> queue = new LinkedList<String>();

        //    http://ucx.dei.uc.pt/sd/entry.html
        int tam = wp.getLink().size();

        for(int i=0; i < tam; i++){
            //addLink(wp.getLink().get(i));
            int c = 0;
            //queue.add(wp.getLink().get(i));
            while (true){ //!queue.isEmpty()
                //System.out.println(wp.getLink().get(i));
                //System.out.println(firstLINK);

                if(i < wp.getLink().size()){
                    addLink(wp.getLink().get(i), firstLINK); // certo
                    /*
                    System.out.println(wp.getLink().size());
                    int tam2 = wp.getLink().size();
                    for (int j = 0;j <tam2; j++){
                        //System.out.println(wp.getLink());
                        System.out.println("-----");
                        System.out.println(wp.getLink().get(j));
                        String url2 = wp.getLink().get(j);
                        System.out.println(url2);
                        if(j < wp.getLink().size()){
                            addLink(url2, firstLINK);
                        }

                        /*
                        if (!url2.isEmpty()){

                        }*7
                        /*
                        //System.out.println(wp.getUrl());
                        if(j < wp.getLink().size()){
                            System.out.println(wp.getLink().get(j));
                            addLink(wp.getLink().get(j),firstLINK);
                        }*/

                        /*
                        if(!wp.getLink().get(j).equals("")){
                            System.out.println(wp.getLink().get(j));
                            addLink(wp.getUrl().get(j),firstLINK);
                        }*/
                        //addLink(wp.getLink().get(j),firstLINK);

                    //}

                    //System.out.println(wp.getLink().get(i).length());

                    /*
                    int tam2 = wp.getLink().get(i).length();
                    System.out.println(tam2);

                    for(int j = 0; j < tam2; j++){
                        if(j < wp.getLink().size() ){
                            System.out.println(wp.getLink().get(j));
                        }
                    }*/
                }

                /*
                else {

                    //addLink(wp.getLink(), firstLINK);
                }*/
                /*
                if(i >= wp.getLink().size()){
                    break;
                    //index not exists
                }else{
                    // index exists
                    addLink(wp.getLink().get(i), firstLINK);
                }*/

                //addLink(wp.getLink().get(i), firstLINK);
                c++;
                if(c == 11){ //11
                    break;
                }
                //queue.remove(); // remove head
            }
            //System.out.println(wp.getLink().size());

            //queue.remove();
            //queue.remove();
            //System.out.println(i);
        }


    }

    //ordena por ordem decrescente os urls por nr de ligacoes de paginas
    public void sortInverted(List<HashMap.Entry<String, HashSet<String>>> list){
        Collections.sort(list, new EntryComparator());
    }

    // https://stackoverflow.com/questions/27204792/sort-hashmap-of-arraylist-based-on-arraylist5-size
    private static class EntryComparator implements Comparator<Map.Entry<String, HashSet<String>>> {
        public int compare(Map.Entry<String, HashSet<String>> left, Map.Entry<String, HashSet<String>> right) {
            // Right then left to get a descending order
            return Integer.compare(right.getValue().size(), left.getValue().size());
        }
    }

    public static ArrayList<String> removeRepetidos(ArrayList<String> listURLS)
    {
        ArrayList<String> urls = new ArrayList<String>();
        for (String l : listURLS) {
            if (!urls.contains(l)) {
                urls.add(l);
            }
        }
        return urls;
    }

    // PESQUISA e ordena os resultados da pesquisa
    public String pesquisaOrdenada(String s) {
        int conta = 0;
        String aux = new String();

        // divide dados[1] (input) por palavras para pesquisar
        String pesquisa[] = s.split(" ");
        /*
        for (int i=0;i<pesquisa.length; i++){
            System.out.println(pesquisa[i]);
        }*/

        // ordena todos os URL's por nr de ligacoes urls a que apontam
        List<HashMap.Entry<String, HashSet<String>>> list = new ArrayList<>(inverted.entrySet());
        sortInverted(list);

        /*
        // imprimir lista toda ordenada consoante nr de ligacoes
        for (Map.Entry<String, HashSet<String>> entry : list){
            System.out.println(entry.getKey() + " = " + entry.getValue().size());
        }*/

        ArrayList<String> urls = new ArrayList<>(); // verificar se o resultado da pesquisa ja foi inserido ou nao... para nao repetir
        ArrayList<String> result = new ArrayList<>();

        // ordena lista por nr de ligacoes de forma decrescente
        for(Map.Entry<String,HashSet<String>> entry : list) { // list -> ja vem o inverted ordenado
            System.out.println(" URL  = " + entry.getKey());
            System.out.println(" nr de ligacoes = " + entry.getValue().size());

            for (int i = 0; i < webArray.size(); i++) { //websites or webArray.size ??????
                for(int j=0; j<pesquisa.length; j++) { //nr de palavras pesquisadas
                    if ((webArray.get(i).getPals().contains(pesquisa[j])) ){//&& (entry.getKey().equals(webArray.get(i).getUrl()))) {
                        // se as palavras pesquisadas estiverem nos websites presentes. E coincide com a lista ordenada -> assim imprime logo ordenado

                        //System.out.println(" URL  = " + entry.getKey());
                        //System.out.println(" nr de ligacoes = " + entry.getValue().size());
                        //aux = aux.concat(" nr de ligacoes = " + entry.getValue().size()+"\n");
                        //System.out.println();
                        //System.out.println(webArray.get(i));

                        String url = "";
                        url = webArray.get(i).getUrl();

                        //if(inverted.containsKey(url)){
                        //    urls.add(url);
                        //}
                        if(entry.getKey().equals(url)){
                            urls.add(url);
                        }

                        //urls.add(url);
                        // remove repetidos
                        //System.out.println(urls);
                        result = removeRepetidos(urls);
                    }
                }
            }
        }
        // imprimir resultados sem serem repetidos
        System.out.println(result);

        String rep = "";
        for(int i=0; i <result.size(); i++) {
            for (int j = 0; j < webArray.size(); j++) {
                if (webArray.get(j).getUrl().equals(result.get(i))) {
                    if(rep.equals(webArray.get(j).getUrl())){
                        continue;
                    }
                    else {
                        // se o url esta na lista de resultados ordenados
                        conta++; //incrementa nr de resultados de pesquisa

                        //System.out.println(" nr de ligacoes = " + entry.getValue().size());
                        //aux = aux.concat(" nr de ligacoes = " + entry.getValue().size()+"\n");

                        // imprime resultados
                        //titulo do website
                        System.out.println(webArray.get(j).getTitle());
                        aux = aux.concat(webArray.get(j).getTitle() + "\n");

                        // url
                        /*
                        System.out.println(entry.getKey());
                        aux = aux.concat(entry.getKey() + "\n");
                         */
                        System.out.println(result.get(i));
                        aux = aux.concat(result.get(i) + "\n");

                        // texto / descricao
                        String t;
                        t = webArray.get(j).getText();
                        // if ( t.lenght() == 0 ) => t = ""
                        t = t.substring(0, Math.min(t.length(), 100));

                        System.out.println(t);
                        aux = aux.concat(t + " ...\n\n");

                        System.out.println();

                        rep = result.get(i);
                    }
                }
            }
        }
        aux=aux.concat("Numero de resultados = " + conta+"\n"); //imprime para o RMI client
        System.out.println("Numero de resultados = " + conta); // imprime para o Multicast Client
        System.out.println("========================================================================================================");
        return aux;
    }

    //pesquisas mais comuns
    public String top10pesquisas(){
        ArrayList<String> pesquisas = new ArrayList<>();
        String aux;
        System.out.println(" ----- TOP 10 Pesquisas ---- ");
        aux = " ----- TOP 10 Pesquisas ---- \n";
        for (int i=0; i<users.size(); i++){
            for (int j=0; j<users.get(i).getHistory().size(); j++){
                String s;
                s = users.get(i).getHistory().get(j);
                pesquisas.add(s);
            }
        }
        /* https://stackoverflow.com/questions/35992891/java-how-to-find-top-10-most-common-string-frequency-in-arraylist */
        // encontrar as 10 pesquisas mais frequentes e ordenar por ordem decrescente
        Map<String, Long> map = pesquisas.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        List<Map.Entry<String, Long>> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());

        int l=1;
        // imprime ranking
        for (Map.Entry<String, Long> e : result) {
            aux = aux.concat("Ranking #" + l + " " + e.getKey() + " -> " + e.getValue() + "x vezes \n");
            System.out.println("Ranking #" + l + " " + e.getKey() + " -> " + e.getValue() + "x vezes");
            l++;
        }
        return aux;
    }

    public static void main(String[] args) {
        //System.getProperties().put("java.security.policy", "policy.all");

        MulticastServer server = new MulticastServer();

        // ler ficheiros de objetos
        users = readUsersOBJ(users);
        webArray = readWebsitesOBJ(webArray);
        //crawler = readCrawlerOBJ(crawler);
        inverted = readInvertedOBJ(inverted);

        // arrancar servidor
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000)); //1000

        try {
            loadConfig();
            //System.out.println(MULTICAST_ADDRESS);
            //System.out.println(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        // UDP
        MulticastSocket socketS = null; //sending
        MulticastSocket socketR = null; //receber

        System.out.println(this.getName() + " running...");
        //System.out.println(this. + " running...");

        try {
            socketS = new MulticastSocket();  // create socket without binding it (only for sending)
            socketR = new MulticastSocket(PORT); // create socket with binding it (only for recebing)
            // multicast server recebe e envia pedidos para o rmi server (e tambem o multicast client caso seja usado)
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socketS.joinGroup(group);
            socketR.joinGroup(group);

            while (true) {
                int flag = 0;

                //System.out.println("recebe pedido");

                //RECEBE pedidos
                byte[] buffer = new byte[100000]; // 700000
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length); //UDP

                //System.out.println(packet);

                //socketR.setTimeToLive(255);

                socketR.receive(packet);

                //System.out.println(packet);

                System.out.println(packet.getPort()+" *socketS "+socketR.getLocalPort()+"*");
                System.out.println(packet.getPort()+" *socketR "+socketS.getLocalPort()+"*");

                while(packet.getPort() == socketS.getLocalPort()) {
                    buffer = new byte[100000]; // 256
                    packet = new DatagramPacket(buffer, buffer.length);
                    socketR.receive(packet);
                }
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + " : " + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                String[] dados = message.split(" ; ");
                if(dados != null) {
                    System.out.println("=======================================================");
                    System.out.println("=======================================================");
                    System.out.println("=======================================================");

                    for(int j = 0 ; j < dados.length;j++){
                        dados[j] = dados[j].substring(dados[j].indexOf("|") +2);

                        //String[] comandos = dados[j].split(" | ");
                        /*
	                	for(int i = 0 ; i < comandos.length;i++){
	                		System.out.println(comandos[i]+"\n");
	                	}
                         */
                        //dados[j]=comandos[2];
                        //System.out.println(dados[j]);
                        //System.out.println("----->>> " + comandos[0] + " " + comandos[1] + " " + comandos[2] );
                    }
                    //System.out.println(dados[1]);
                }
                switch (dados[0]){
                    case "regist":
                        User u = new User(dados[1], dados[2]);// novo user
                        if (users.size() == 0) {
                            //primeiro user e administrador
                            u.setAdmin(true);
                            users.add(u);
                            writeUsersOBJ(users);
                            // envia msg para multicast client
                            buffer = " type | status ;  msg | Primeiro user! => ADMIN! Utilizador registado. Inicie sessao...".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else {
                            for (User us : users) {
                                if (us.getUsername().equals(dados[1])) {
                                    buffer = "type | status ; logged | failed ; msg | Username ja utilizado".getBytes();
                                    flag=1;
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if(flag==0){
                                users.add(u);

                                buffer = "type | status ; msg | Utilizador registado. Inicie sessao...".getBytes();
                                writeUsersOBJ(users);
                                try {
                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                    socketS.send(packet);
                                    System.out.println("enviou resposta");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;

                    case "login":
                        for (User us : users) {
                            System.out.println(us.getUsername() + " ---- " + us.getPassword());
                            if(us.getUsername().compareTo(dados[1]) == 0 && us.getPassword().compareTo(dados[2])!=0){
                                flag=3;
                            }
                            else if(us.getUsername().equals(dados[1]) && us.getPassword().equals(dados[2])){
                                flag=1;
                                for(User log : login){
                                    if(log.getUsername().equals(dados[1])){
                                        flag = 2;
                                        buffer = "type | status ; msg | User ja fez login".getBytes();
                                        try {
                                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                            socketS.send(packet);
                                            System.out.println("enviou resposta");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if(flag == 1) {
                                    login.add(us);
                                    if(!us.getNotification()) {
                                        String sr = "type | status ; msg | Login com sucesso ; admin | ";
                                        String t = "true";
                                        String f = "false";
                                        if(us.isAdmin()){
                                            System.out.println("administrador");
                                            sr = sr.concat(t);
                                        }else{
                                            System.out.println("nao administrador");
                                            sr = sr.concat(f);
                                        }

                                        buffer = sr.getBytes();

                                        try {
                                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                            socketS.send(packet);
                                            System.out.println("enviou resposta");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else{
                                        us.setNotification(false);
                                        String sr = "type | status ; msg | Login com sucesso ; notificacao | Tornou se administrador ; admin | ";
                                        String t = "true";
                                        String f = "false";
                                        if(us.isAdmin()){
                                            System.out.println("administrador");
                                            sr = sr.concat(t);
                                        }else{
                                            System.out.println("nao administrador");
                                            sr = sr.concat(f);
                                        }
                                        buffer = sr.getBytes();
                                        try {
                                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                            socketS.send(packet);
                                            System.out.println("enviou resposta");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        if(flag==0){
                            buffer = "type | status ; msg | Necessita de criar utilizador".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if(flag==3){
                            buffer = "type | status ; msg | Password errada".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "search":
                        System.out.println("===============================================");
                        System.out.println("========== A pesquisar ... " + dados[1] + " ==========");
                        System.out.println("===============================================");
                        String str1;

                        // pesquisa e ordena os resultados pela relevancia dos sites
                        /* ==================== VAI PESQUISAR ==================== */
                        str1 = pesquisaOrdenada(dados[1]);
                        /* ======================================================= */

                        System.out.println(" ================================================================================================ ");
                        if(dados.length<3) {
                            // pesquisa sem conta
                            str1 = str1.concat( "type | status ; msg | Pesquisa realizada s/conta");
                            buffer = str1.getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta - nao tem conta e pesquisou");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            // pesquisa com conta => vai guardar no historico do utilizador
                            for (User log : login) {
                                if (log.getUsername().equals(dados[2])) {
                                /*
                                System.out.println(dados[1]);
                                int c = 0;
                                for (int i=0; i<webArray.size();i++){
                                    //System.out.println(webArray.get(i));
                                    if(webArray.get(i).getPals().contains(dados[1])){
                                        System.out.println(webArray.get(i).getTitle());
                                        System.out.println(webArray.get(i).getUrl());
                                        String t;
                                        t = webArray.get(i).getText();
                                        t = t.substring(0, Math.min(t.length(), 100));
                                        System.out.println(t);
                                        System.out.println();
                                        c++;
                                    }else{
                                        flag = 1;
                                    }
                                }*/
                                    System.out.println("Historico antes: " + log.getHistory());
                                    String s = dados[1]; // palavra(s) pesquisadas
                                    for (User us : users) {
                                        if (log.equals(us)) {
                                            if (us.history.size() == 0) {
                                                // se nao tem historico, ou seja, nunca pesquisou
                                                ArrayList<String> h = new ArrayList<>();
                                                h.add(s);
                                                log.setHistory(h);
                                            } else {
                                                us.addHistory(s);
                                            }
                                            // ---- GUARDA O HISTORICO NO FICHEIRO OBJ dos users -----
                                            writeUsersOBJ(users);
                                        }
                                    }
                                    System.out.println("Historico depois: " + log.getHistory());
                                    /*
                                    // nr total de resultados
                                    System.out.println("Numero de resultados = " + c);
                                     */
                                    str1 = str1.concat("type | status ; msg | Pesquisa realizada c/ conta\n");
                                    buffer =str1.getBytes();
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        break;

                    case "history":
                        String str = new String();
                        for (User log : login){
                            if(log.getUsername().equals(dados[1])) {
                                flag = 1;
                            }
                        }
                        if(flag==1){
                            for (User log : login){
                                if(log.getUsername().equals(dados[1])) {
                                    System.out.println("Historico de pesquisas: " + log.getHistory());
                                    str = "Historico de pesquisas: " + log.getHistory() + "\n";
                                    str = str.concat("type | status ; msg | historico enviado\n");
                                    buffer = str.getBytes();
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        else {
                            buffer = "type | status ; msg | nao iniciou sessao".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "list_links":
                        //System.out.println("Consulta da lista de páginas com ligaçao para uma");
                        for (User log : login) {
                            if (log.getUsername().equals(dados[2])) {
                                flag = 1;
                            }
                        }
                        if(flag==1) {
                            for (User log : login) {
                                if (log.getUsername().equals(dados[2])) {
                                    // sessao do utilizador iniciada
                                    System.out.println("============================================");
                                    System.out.println(inverted);
                                    System.out.println("============================================");

                                    System.out.println(dados[1] + " <--- " + inverted.get(dados[1]));
                                    // envia resultado
                                    str = dados[1] + " <--- " + inverted.get(dados[1]) + "\n";
                                    str = str.concat("type | status ; msg | printed list of links to server ");

                                    buffer = str.getBytes();
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }else {
                            // sessao por iniciar
                            System.out.println(dados[2]);
                            buffer = "type | status ; msg | nao iniciou sessao... ".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "add_url":
                        String s = "";
                        for (User log : login) {
                            if(log.getUsername().equals(dados[2]) && log.isAdmin()) {
                                flag = 1;
                                for(Webpage w : webArray) {
                                    if(w.getUrl().equals(dados[1])) {
                                        flag = 2;
                                        buffer = "type | status ; msg | Url ja esta presente...".getBytes();
                                        try {
                                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                            socketS.send(packet);
                                            System.out.println("enviou resposta");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if(flag == 1) {
                                    // adicionar url
                                    //addLink(dados[1]);
                                    addALL(dados[1]);
                                    // concatena para depois enviar resultado
                                    s = s.concat("type | status ; msg | URL adicionado com sucesso! \n");

                                    // adicionar recursivamente todos os URLS encontrados
                                    //System.out.println("======================================================");
                                    //System.out.println("======================================================");
                                    //System.out.println(wp.getLink()); // imprime links encontrados
                                    //System.out.println(wp.getLink().size()); // imprime nr de links encontrados
                                    System.out.println("======================================================");

                                    //int tam = wp.getLink().size(); // tamanho do array de links encontrados

                                    //tinyurl.com/sistemas19

                                    /*
                                    for(int i=0; i < tam; i++){
                                        addLink(wp.getLink().get(i));
                                        //System.out.println("--> links = " + wp.getLink()); // imprime links encontrados
                                        //System.out.println(" --> tam = " + wp.getLink().size()); // imprime nr de links encontrados
                                        l++;
                                        System.out.println(l);
                                        s = s.concat("type | status ; msg | URL encontrado adicionado com sucesso! \n");

                                        String url = wp.getLink().get(i);
                                        int subLinks = 0;
                                        //int subTam = wp.getLink().size();
                                        //System.out.println(" -----> subTAM = " + subTam + " -----");
                                        for(int j=0; j < wp.getLink().size(); j++){
                                            subLinks++;
                                            System.out.println(" ================" + subLinks + "=============== ");
                                            //System.out.println("A adicionar .... " + wp.getLink().get(j));

                                            addLink(wp.getLink().get(j));

                                            //System.out.println(" links asssociados = " + wp.getLink());
                                            //System.out.println(" size = " + wp.getLink().size());
                                            System.out.println(" ========================================== ");
                                        }
                                        */

                                        /*
                                        if(l == tam) {
                                            // deixa de adicionar... pois ja adicionou todos os encontrados
                                            buffer = s.getBytes();
                                            try {
                                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                                socketS.send(packet);
                                                System.out.println("enviou resposta");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        }*/
                                    buffer = s.getBytes();
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //}
                            }
                        }
                        if (flag == 0) {
                            // nao é admin
                            buffer = "type | status ; msg | Nao tem permissao".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "give_admin":
                        for (User log : login) {
                            for (User us : users) {
                                if (us.getUsername().equals(dados[2]) && (log.getUsername().equals(dados[1])  && log.isAdmin())) {
                                    //se esta com o login feito e se o username existe
                                    flag = 1;
                                    if (us.isAdmin()) {
                                        buffer = "type | status ; msg | O user2 ja e administrador".getBytes();
                                        try {
                                            packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                            socketS.send(packet);
                                            System.out.println("enviou resposta");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        us.setAdmin(true); //utilizador passa a ser administrador
                                        writeUsersOBJ(users); // guarda no fich obj
                                        for(User l : login){
                                            if(us.getUsername().compareTo(l.getUsername()) == 0){//o user esta online
                                                flag = 2;
                                                s = "type | status ; msg | O user2 "+us.getUsername()+" agora e administrador";
                                                buffer = s.getBytes();
                                                try {
                                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                                    socketS.send(packet);
                                                    System.out.println("enviou resposta");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        if(flag != 2){ //o user esta off
                                            us.setNotification(true);
                                            buffer = "type | status ; msg | O user agora2 e administrador".getBytes();
                                            try {
                                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                                socketS.send(packet);
                                                System.out.println("enviou resposta");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (flag == 0) {
                            buffer = "type | status ; msg | Nao tem permissao".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "stats_admin":
                        for (User log : login) {
                            if (log.getUsername().equals(dados[1]) && log.isAdmin()) {
                                flag = 1;
                                // ordenar inverted
                                List<HashMap.Entry<String, HashSet<String>>> list = new ArrayList<>(inverted.entrySet());
                                sortInverted(list);
                                str = null;
                                System.out.println(" ----- TOP 10 PAGES -----");
                                str = " ----- TOP 10 PAGES -----\n";
                                int lugar = 1;
                                for (Map.Entry<String, HashSet<String>> entry : list) {
                                    // ------ top 10 pages------
                                    while (lugar <= 10) {
                                        System.out.println("RANKING #" + lugar);
                                        str = str.concat("RANKING #" + lugar + "\n");

                                        System.out.println("nr de ligacoes = " + entry.getValue().size());
                                        str = str.concat("nr de ligacoes = " + entry.getValue().size() + "\n");

                                        System.out.println(webArray.get(lugar).getTitle());
                                        str = str.concat(webArray.get(lugar).getTitle() + "\n");

                                        System.out.println(entry.getKey());
                                        str = str.concat(entry.getKey() + "\n");
                                        lugar++;
                                    }
                                }
                                str = str.concat("type | status ; msg |  sent ..... top 10 pages\n\n");
                                /*
                                buffer = str.getBytes();
                                try {
                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                    socketS.send(packet);
                                    System.out.println("enviou resposta top 10 pages");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/

                                // ------- top 10 pesquisas mais comuns--------
                                str = str.concat(top10pesquisas());
                                str = str.concat("\ntype | status ; msg |  sent ..... top 10 pesquisas mais comuns\n");
                                /*
                                buffer = str.getBytes();
                                try {
                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                    socketS.send(packet);
                                    System.out.println("enviou resposta top 10 pesquisas");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                */
                                // -------------- lista de servidores multicast ativos (IP e Porto) ---------------------
                                System.out.println("--------- lista de servidores multicast ativos (IP e Porto) --------- ");
                                serversMultiCastActive.put(MULTICAST_ADDRESS,PORT); // IP : PORT
                                //System.out.println(serversMultiCastActive);
                                List<HashMap.Entry<String, Integer>> listServers = new ArrayList<>(serversMultiCastActive.entrySet()); //inverted.entrySet();
                                int nr = 1;
                                for (Map.Entry<String, Integer> e : listServers) {
                                    // imprime ranking
                                    System.out.println(" -> IP Servidor Multicast ativo " + nr + ":    " + e.getKey());
                                    str = str.concat(" -> IP Servidor Multicast ativo " + nr + ":    " + e.getKey() + "\n");

                                    System.out.println(" -> PORTO Servidor Multicast ativo " + nr + ":    " + e.getValue());
                                    str = str.concat(" -> PORTO Servidor Multicast ativo " + nr + ":    " + e.getValue() + "\n");
                                    nr++;
                                }
                                str = str.concat("\ntype | status ; msg |  sent ..... stats admin\n");

                                buffer = str.getBytes();
                                try {
                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                    socketS.send(packet);
                                    //System.out.println("enviou resposta ... lista de servidores multicast ativos (IP e Porto)");
                                    System.out.println("enviou resposta ... stats admin");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(flag==0) {
                            for (User log : login) {
                                if (log.getUsername().equals(dados[1]) && !log.isAdmin()) {
                                    flag = 2;
                                    buffer = "type | status ; msg |  sent ..... Nao tem permissao. So o admin! ".getBytes();
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if(flag==0) {
                            buffer = "type | status ; msg | Nao tem sessao iniciada... ".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "logout":
                        if(dados[1].equals(" ") ){
                            buffer = "type | status ; msg | logout com sucesso".getBytes();
                            try {
                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socketS.send(packet);
                                System.out.println("enviou resposta");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if(login.size() == 1){
                                if(login.get(0).getUsername().compareTo(dados[1]) == 0){
                                    flag = 2;
                                }
                            }
                            if(flag == 2){
                                login.remove(login.get(0));
                                buffer = "type | status ; msg | logout com sucesso".getBytes();
                                try {
                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                    socketS.send(packet);
                                    System.out.println("enviou resposta");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(login != null && login.size() >  1) {
                                User temp = null;
                                for (User log : login) {
                                    if (log.getUsername().compareTo(dados[1]) == 0) {
                                        temp = log;
                                        flag = 20;
                                    }
                                }
                                if(flag == 20){
                                    login.remove(temp);
                                    buffer = "type | status ; msg | Logout com sucesso".getBytes();
                                    try {
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socketS.send(packet);
                                        System.out.println("enviou resposta");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else{
                                buffer = "type |status ; msg | nao tinha sessao iniciada".getBytes();
                                try {
                                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                    socketS.send(packet);
                                    System.out.println("enviou resposta");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    default:
                        System.out.println("comando invalido");
                        break;
                    //return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socketS.close();
            socketR.close();
        }
    }

    // ================================== FICHEIROS =================================================

    private static ArrayList<User> readUsersOBJ(ArrayList<User> users) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("utilz.obj"));
            User u;
            while ((u = (User) ois.readObject()) != null) {
                users.add(u);
            }
            ois.close();
        } catch (FileNotFoundException e) {
            //System.out.println("Ocorreu a excepcao "+ e ");
            writeUsersOBJ(users);
        } catch (ClassNotFoundException | IOException e) {
            //System.out.println("Ocorreu a excepcao "+ e ");
        }
        return users;
    }

    private static void writeUsersOBJ(ArrayList<User> users) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("utilz.obj")));
            for (User u : users) {
                System.out.println(u.getUsername());
                if(u.isAdmin()) {
                    System.out.println("true");
                }
                oos.writeObject(u);
            }
            oos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private static ArrayList<Webpage> readWebsitesOBJ(ArrayList<Webpage> websites) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("wbpages.obj"));
            Webpage a;
            while ((a = (Webpage) ois.readObject()) != null) {
                //System.out.println("a ler.." + a);
                websites.add(a);
            }
            ois.close();
        } catch (FileNotFoundException e) {
            writeWebsitesOBJ(websites);
        } catch (ClassNotFoundException | IOException | OutOfMemoryError e) {
            System.out.println("expection "+ e);
        }
        return websites;
    }

    private static void writeWebsitesOBJ(ArrayList<Webpage> websites) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("wbpages.obj")));
            for (Webpage a : websites) {
                //System.out.println("a escrever ..." + a.getUrl());
                oos.writeObject(a);
            }
            oos.close();
        } catch (IOException | OutOfMemoryError e2) {
            System.out.println("expection " + e2);
        }
    }

    private static HashMap<String, HashSet<String>> readInvertedOBJ(HashMap<String, HashSet<String>> hm) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("wpinv.obj"));
            hm = (HashMap<String, HashSet<String>>)ois.readObject();
            ois.close();

            /*
            for(Map.Entry<String, HashSet<String>> m :hm.entrySet()){
                System.out.println(m.getKey()+" : "+m.getValue());
            }*/
        } catch (FileNotFoundException e) {
            writeInvertedOBJ(hm);
        } catch (ClassNotFoundException | IOException | OutOfMemoryError e) {
            System.out.println("expection "+ e);
        }
        return hm;
    }

    private static void writeInvertedOBJ(HashMap<String, HashSet<String>> hm) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("wpinv.obj")));
            oos.writeObject(hm);
            oos.close();
        } catch (IOException | OutOfMemoryError e2) {
            System.out.println("expection " + e2);
        }
    }
}
