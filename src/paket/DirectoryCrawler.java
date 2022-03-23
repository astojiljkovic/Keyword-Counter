package paket;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler implements Runnable{

    private CopyOnWriteArrayList<String> toDoCrawling  = new CopyOnWriteArrayList<>();
    private volatile boolean flagic = true;

    private ConcurrentHashMap<String,Long> lastMod = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<String> testLista = new CopyOnWriteArrayList<>();

    @Override
    public void run() {

        while(flagic){
            for (String s : toDoCrawling) {
                searchDir(s);
            }
            try {
                Thread.sleep(Main.getcrawlerSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


// /Users/aleksa/Desktop/kids_d1_aleksa_stojiljkovic_rn8617/kids_d1_data_primer/example/data
    public void searchDir(String putanja){

        try {


            File folder = new File(putanja);
            if(isValidPath(folder.getPath())) {

                File[] listeFileova = folder.listFiles();


                for (File f : listeFileova) {
//            System.out.println(f.getPath() + " File f ima putanju tj tostring");
                    if (f.isDirectory()) {
                        String folderinjo = f.getName();
//                System.out.println(folderinjo.toString());
                        if (folderinjo.startsWith(Main.getPrefix())) {
//                    File folderinjoFile = new File(folderinjo);
                            File[] folderinjoFileovi = f.listFiles();

                            boolean flag1 = false;
                            for (File fi : folderinjoFileovi) {

                                if (!lastMod.containsKey(fi.getPath())) {
                                    lastMod.put(fi.getPath(), fi.lastModified());

                                    flag1 = true;
                                } else {
                                    if (fi.lastModified() != lastMod.get(fi.getPath())) {
                                        lastMod.replace(fi.getPath(), fi.lastModified());
                                        flag1 = true;
                                    }
                                }

                            }
                            if (flag1) {
                                dodajNaQue(f.getPath());
                            }

                        } else {

                            searchDir(f.getPath());
                        }
                    }
                }
            }else{
                System.out.println("nema foldera na toj putanji!!");
            }
        }catch (Exception e ){
            System.out.println("nema filea na toj putanji.");
             toDoCrawling.remove(putanja);

        }


    }

    public void zaustavi(){
        flagic=false;
    }


    public void dodajNaQue(String s){
        System.out.println("dodalo se u listu: " + s);
        try {
            Main.queZaPosao.put(new FileScannerPosao(ScanType.FILE, s ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public CopyOnWriteArrayList<String> getToDoCrawling()
    {
        return toDoCrawling;
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

