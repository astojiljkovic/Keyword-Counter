package paket;

import java.util.Map;
import java.util.concurrent.Future;

public interface Posao {

    ScanType getType();

    String getQuery();

//    Future<Map<String,Integer>> initiate();
    void initiate();

    String getPath();

}
