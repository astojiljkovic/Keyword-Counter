package paket;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class FileTask extends RecursiveTask<Map<String, Integer>> {

    private File[] files;

    public FileTask(File[] fajlovi) {
        this.files = fajlovi;
    }

    @Override
    protected Map<String, Integer> compute() {

        int n = files.length;

        ArrayList<File> fajloviZaSkeniranje = new ArrayList<>();

        ConcurrentHashMap<String, Integer> temp = new ConcurrentHashMap<>();

        for (String s : Main.getKeywords()) {
            if (!temp.containsKey(s)) {
                temp.put(s, 0);
            }
        }

        long velicina = 0;

        for (File f : files) {
            velicina += f.length();

        }
        if(velicina < Main.getFileScanningSize() || n == 1){
            for(File f : files){
                Map<String,Integer> m = readFile(f);
                for(String s : m.keySet()){
                    temp.merge(s,m.get(s),Integer::sum);
                }
            }
        }else{
            File[] a = Arrays.copyOfRange(files,0, (n+1)/2);
            File[] b = Arrays.copyOfRange(files,(n+1)/2, n);
            FileTask t1 = new FileTask(a);
            FileTask t2 = new FileTask(b);

            t1.fork();
            Map<String, Integer> levaMapa = t2.compute();
            Map<String,Integer> desnaMapa = t1.join();
            for(String s : levaMapa.keySet()){
                temp.merge(s,levaMapa.get(s),Integer::sum);
            }
            for(String s : levaMapa.keySet()){
                temp.merge(s,desnaMapa.get(s),Integer::sum);
            }
        }

        System.out.println(temp.toString());
        return temp;
    }

    public Map<String, Integer> readFile(File f) {
        Map<String, Integer> tempReadFile = new ConcurrentHashMap<>();
        String reci[] = null;

        for (int i = 0; i < Main.getKeywords().length ; i++) {
            tempReadFile.put(Main.getKeywords()[i], 0);
        }
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String s;
            try {
                while (((s = br.readLine()) != null)) {
                    reci = s.split(" ");
                    for (int i = 0; i < reci.length; i++) {
                        if (tempReadFile.containsKey(reci[i])) {
                            tempReadFile.put(reci[i], tempReadFile.get(reci[i]) + 1);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempReadFile;
    }


}
