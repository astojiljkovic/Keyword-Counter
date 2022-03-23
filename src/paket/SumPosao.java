package paket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class SumPosao extends RecursiveTask<Map<String, Integer>> {

    private String putanja;

    public SumPosao(String putanja) {
        this.putanja = putanja;
    }

    @Override
    protected Map<String, Integer> compute() {

        Map<String, Integer> temp = new HashMap<>();
        Map<String,Integer> asdf = new ConcurrentHashMap<>();

        for(String s : Main.getKeywords()){
            temp.put(s,0);
        }
        for(String s : Main.getRt().getMapaSvega().keySet()){
            if(s.contains(putanja)){
                try {
                    asdf = Main.getRt().getMapaSvega().get(s).get();
                    asdf.forEach((k, v) -> temp.merge(k, v, Integer::sum));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return temp;
    }
}
