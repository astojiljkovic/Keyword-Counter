package paket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class WebTask extends RecursiveTask<Map<String,Integer>> {


    private String putanja;
    private int hop_count;
//    private int x = 0;


    public WebTask(String putanja, int hop_count) {
        this.putanja = putanja;
        this.hop_count = hop_count;
//        System.out.println(hop_count);
    }

    @Override
    protected Map<String, Integer> compute() {

        Map<String,Integer> temp = new ConcurrentHashMap<>();
        for (String s: Main.getKeywords()) {
            if(!temp.containsKey(s)) {
                temp.put(s, 0);
            }
        }
        Map<String,Integer> mapaSaNulama = new ConcurrentHashMap<>();
        for(String s: Main.getKeywords()){
            if(mapaSaNulama.containsKey(s)){
                mapaSaNulama.put(s,0);
            }
        }
        try {

            Document document = Jsoup.connect(putanja).ignoreContentType(true).get();
            Elements linkovi = document.select("a[href]");
            URL url = new URL(putanja);
//            try {
//                url.toURI();
//            } catch (URISyntaxException e) {
//                System.out.println("Puca URIIIIIIII na: " + url.getPath());
//
//            }
            String host = url.getHost();

//            http://www.rense.com/

            if(!Main.skeniraniURL.contains(putanja)){
                Main.skeniraniURL.add(putanja);
            }

            if(!Main.hostovi.contains(host)){
                Main.hostovi.add(host);
                System.out.println("dodat host: " + host);
            }

            String text = document.text();
            String[] razbijenText = text.split(" ");
            for (int i = 0; i < razbijenText.length; i++) {
                if(temp.containsKey(razbijenText[i])){
                    temp.put(razbijenText[i],temp.get(razbijenText[i])+1 );
                }
            }
//za test da vidim dal radi
//            System.out.println("Sajt: " + putanja);
//            for (String s :temp.keySet()) {
//
//                System.out.println("keyword: " + s + " = " + temp.get(s));
//            }
//            System.out.println(temp.toString());
            if(hop_count > 0 ){

                for (Element link: linkovi) {
                    String stemp = link.attr("abs:href");
                    if (!Main.skeniraniURL.contains(stemp)){
                        Main.skeniraniURL.add(stemp);

                        try {
                            Main.queZaPosao.put(new WebScannerPosao(ScanType.WEB, stemp,hop_count - 1 ));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }


        } catch (IOException e) {
            System.out.println("Pukao je sajt sa putanjom: " + putanja );
            return mapaSaNulama;
        }
        return temp;
    }
}
