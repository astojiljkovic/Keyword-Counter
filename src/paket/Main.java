package paket;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static String[] keywords;
    private static String prefix;
    private static long crawlerSleepTime;
    private static long fileScanningSize;
    private static int hopCount;
    private static long urlRefreshTime;
    private static volatile ForkJoinPool fileScannerTP = new ForkJoinPool();
    private static volatile ForkJoinPool webScannerTP = new ForkJoinPool();

    public static volatile BlockingQueue<Posao> queZaPosao = new LinkedBlockingQueue<>();

    public static volatile ResultRetriever rt = new ResultRetriever();

    public static volatile CopyOnWriteArrayList<String> skeniraniURL = new CopyOnWriteArrayList<>();
    public static volatile CopyOnWriteArrayList<String> hostovi = new CopyOnWriteArrayList<>();

    private static AtomicBoolean flagic = new AtomicBoolean(true);


    public static void main(String[] args) throws IOException {

        loadProps();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        DirectoryCrawler dc = new DirectoryCrawler();
        Thread dirCrawlerThread = new Thread(dc);
        dirCrawlerThread.start();
        Dispatcher disp = new Dispatcher();
        disp.start();
        URLChecker URLcheck = new URLChecker(skeniraniURL);
        Thread urlcheckthread = new Thread(URLcheck);
        urlcheckthread.start();


        while (flagic.get()){
            String unos = reader.readLine();
            String[] presecen = unos.split(" ");
            if(presecen[0].equals("ad")){
                if(presecen.length == 2) {
                    if(isValidPath(new File(presecen[1]).getPath())) {
                        if (!dc.getToDoCrawling().contains(presecen[1])) {
                            dc.getToDoCrawling().add(presecen[1]);
                        }
                    }else{
                        System.out.println("Nema foldera na toj putanji");
                    }
                }else {
                    System.out.println("pogresan unos komande ad!");
                    continue;
                }

            }else if(presecen[0].equals("aw")) {

                if(presecen.length == 2) {

                    try {
//                    System.out.println(presecen[1]);
                        Main.queZaPosao.put(new WebScannerPosao(ScanType.WEB, presecen[1], Main.getHopCount()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("pogresan unos za aw!");
                    continue;
                }
            }else if(presecen[0].equals("get")){

                if(presecen[1].contains("SUMMARY") || presecen[1].contains("summary")){
                    String[] sec = presecen[1].split("\\|");
                    if(sec[0].equals("web")){
                        rt.getSummary(ScanType.WEB);
                    }else{
                        rt.getSummary(ScanType.FILE);
                    }
                }else if((presecen[1].contains("|") && presecen[1].contains("file")) || (presecen[1].contains("|") && presecen[1].contains("web"))){
                    getRt().getResult(presecen[1]);
//                    System.out.println("prosao je get u ifu");
                } else {
                    System.out.println("los unos za get! ");
                    continue;
                }

            }else if(presecen[0].equals("query")){
                System.out.println("prosao je query u ifu");

            }else if(presecen[0].equals("cws")){
                if (presecen.length == 1 ) {
//                    System.out.println("prosao je cws u ifu");
                    Main.getRt().clearSummary(ScanType.WEB);
                }else {
                    System.out.println("Pogresan unos za cws");
                    continue;
                }

            }else if(unos.equals("stop")){
                dc.zaustavi();
                flagic.set(false);
                URLcheck.setFlag(false);
                Posao p = new FileScannerPosao(null,null);
                try {
                    queZaPosao.put(p);

                }catch (Exception e ){
                    e.printStackTrace();
                }
                reader.close();
                return;
            }else if(unos.equals("cfs")){
                if (presecen.length == 1) {
                    Main.getRt().clearSummary(ScanType.FILE);
                }else {
                    System.out.println("Pogresan unos za cfs!");
                    continue;
                }
            }
            else{
                System.out.println("Promasen unos");
                continue;
            }
        }



    }

    public static void loadProps() {
        try {
            FileInputStream is = new FileInputStream("app.properties");
            Properties props = new Properties();
            props.load(is);
            String temp  = props.getProperty("keywords");
            keywords = temp.split(",");
            prefix = props.getProperty("file_corpus_prefix");
            crawlerSleepTime = Integer.parseInt(props.getProperty("dir_crawler_sleep_time"));
            fileScanningSize = Integer.parseInt(props.getProperty("file_scanning_size_limit"));
            hopCount = Integer.parseInt(props.getProperty("hop_count"));
            urlRefreshTime = Long.parseLong(props.getProperty("url_refresh_time"));


        }catch (IOException ioe) {
            System.err.println("Exception u ucitavanju propertija");
        }
    //ad kids_d1_data_primer/example/data2
    }
    public static long getcrawlerSleepTime()
    {
        return crawlerSleepTime;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String[] getKeywords() {
        return keywords;
    }

    public static int getHopCount() {
        return hopCount;
    }

    public static long getFileScanningSize() {
        return fileScanningSize;
    }

    public static ResultRetriever getRt() {
        return rt;
    }

    public static ForkJoinPool getFileScannerTP() {
        return fileScannerTP;
    }

    public static ForkJoinPool getWebScannerTP() {
        return webScannerTP;
    }

    public static long getUrlRefreshTime() {
        return urlRefreshTime;
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {

            return false;
        }
        return true;
    }
}
