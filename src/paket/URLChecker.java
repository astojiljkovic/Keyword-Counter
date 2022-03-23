package paket;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class URLChecker implements Runnable {


    private CopyOnWriteArrayList<String> listaURL = new CopyOnWriteArrayList<>();
    private volatile boolean flag= true;

    public URLChecker(CopyOnWriteArrayList<String> listaURL) {

        this.listaURL = listaURL;
    }

    @Override
    public void run() {

        long startTime = (new Date()).getTime();
        while(flag) {
            try {
                long timeNow = (new Date()).getTime();
                if (startTime + Main.getUrlRefreshTime() < timeNow){
                    Main.skeniraniURL.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
