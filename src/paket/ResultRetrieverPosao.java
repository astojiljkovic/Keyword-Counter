package paket;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ResultRetrieverPosao extends RecursiveTask<Map<String,Integer>> {

    private String putanja;

    public ResultRetrieverPosao(String putanja) {
        this.putanja = putanja;
    }

    @Override
    protected Map<String, Integer> compute() {
        Map<String,Integer> temp = new ConcurrentHashMap<>();

        for(String s : Main.getKeywords()){
            temp.put(s,0);
        }

        Iterator<Map.Entry<String, Future<Map<String, Integer>>>> iterator = Main.getRt().getMapaSvega().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,Future<Map<String,Integer>>> p = iterator.next();
            if(p.getKey().contains(putanja)){
                try {
                    Map<String,Integer> rezultat = p.getValue().get();
                    rezultat.forEach((k,v) -> {
                        temp.merge(k, v, Integer::sum);
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        return temp;
    }
}
