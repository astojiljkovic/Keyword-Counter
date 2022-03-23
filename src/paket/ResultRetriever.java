package paket;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ResultRetriever implements ResultRetrieverI {

    public static ForkJoinPool resultRetrieverTP = new ForkJoinPool();

    public static volatile Map<String, Future<Map<String, Integer>>> mapaSvega = new ConcurrentHashMap<>();

    private volatile Map<String, Map<String, Integer>> cache = new ConcurrentHashMap<>();
    private volatile Map<String, Future<Map<String, Integer>>> webSUM = new ConcurrentHashMap<>();
    private volatile Map<String, Map<String, Integer>> fileSUM = new ConcurrentHashMap<>();

    public ResultRetriever() {

    }

    @Override
    public Map<String, Integer> getResult(String query) {
        Map<String, Integer> temp = new ConcurrentHashMap<>();

        String[] delovi = query.split("\\|");
        String levi = delovi[0];
        String desni = delovi[1];

        Iterator<Map.Entry<String, Future<Map<String, Integer>>>> it = mapaSvega.entrySet().iterator();

        if (levi.equals("web")){
            ResultRetrieverPosao RRP = new ResultRetrieverPosao(desni);
            Future<Map<String,Integer>> rez = resultRetrieverTP.submit(RRP);

            for(String s: Main.getKeywords()){
                try {
                    System.out.println(s + ": " + rez.get().get(s));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }else {

            while (it.hasNext()) {
                Map.Entry<String, Future<Map<String, Integer>>> pair = it.next();

                if (pair.getKey().contains(desni)) {
                    try {
                        Map<String, Integer> res = pair.getValue().get();
                        System.out.println(res.toString());
                        return pair.getValue().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return temp;
    }

    @Override
    public Map<String, Integer> queryResult(String query) {
        return null;
    }

    @Override
    public void clearSummary(ScanType summaryType) {
        if (summaryType.equals(ScanType.FILE)) {
//            System.out.println("clearovao sam file summary mapu");
            if(!fileSUM.isEmpty()) {
                System.out.println("brisemo fileSUM");
                fileSUM.clear();
            }else {
                System.out.println("Nema sta da se obrise");
            }

        }else{
//            System.out.println("clearovao sam web summary mapu");
            if(!webSUM.isEmpty()) {
                System.out.println("brisemo webSUM!");
                webSUM.clear();
            }else{
                System.out.println("Nema sta da se obrise za web");
            }

        }

    }

    @Override
    public Map<String, Map<String, Integer>> getSummary(ScanType summaryType) {
        Map<String,Map<String,Integer>> temp = new ConcurrentHashMap<>();
        Map<String,Integer> zbirFILE = new ConcurrentHashMap<>();
        Map<String,Integer> asdf = new ConcurrentHashMap<>();

        for(String s: Main.getKeywords()){
            zbirFILE.put(s,0);
        }

        if (summaryType.equals(ScanType.FILE)) {
            if(fileSUM.isEmpty()) {
                System.out.println("Racunamo file summary");
                for (String s : getMapaSvega().keySet()) {
                    if (s.contains(Main.getPrefix())) {
                        if (fileSUM.containsKey(s)) {
                            System.out.println("izvlacimo iz cacheSum za file!");
                            try {
                                Map<String, Integer> mapica = getMapaSvega().get(s).get();
                                for (String see : fileSUM.keySet()) {
                                    if (see.equals(s)) {
                                        temp.put(see, mapica);
                                    }
                                }
//                            temp.put(fileSUM, mapica);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Map<String, Integer> mapica = getMapaSvega().get(s).get();
                                fileSUM.put(s, mapica);
                                temp.put(s, mapica);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

                for (String s : temp.keySet()) {
                    System.out.println(s + " " + temp.get(s));
                    asdf = temp.get(s);
                    asdf.forEach((k, v) -> zbirFILE.merge(k, v, Integer::sum));
                }
                fileSUM.put("Sumarno", zbirFILE);
                System.out.println("Zbir: " + zbirFILE.toString());
            }else{
                System.out.println("vec imamo summary u cache");
                System.out.println("File summary: " + fileSUM.get("Sumarno"));
            }

        }else{
//            System.out.println(Main.hostovi.toString());
            Map<String,Future<Map<String,Integer>>> temperoni = new ConcurrentHashMap<>();
            Future<Map<String,Integer>>zbir;

            if (webSUM.isEmpty()) {
                for (String h : Main.hostovi) {
                    SumPosao sp = new SumPosao(h);
                    zbir = resultRetrieverTP.submit(sp);
                    temperoni.put(h, zbir);
                    webSUM.put(h,zbir);
                }
                for(String s : temperoni.keySet()){
                    try {
                        System.out.println("Host: " + s + " " + temperoni.get(s).get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }else{
                System.out.println("Izvlacimo iz Web SUM Cache!");
                for(String p : webSUM.keySet() ){
                    try {
                        System.out.println("Host: " + p + " " + webSUM.get(p).get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return temp;
    }

    @Override
    public Map<String, Map<String, Integer>> querySummary(ScanType summaryType) {
        return null;
    }

    public Map<String, Future<Map<String, Integer>>> getMapaSvega() {
        return mapaSvega;
    }
}
