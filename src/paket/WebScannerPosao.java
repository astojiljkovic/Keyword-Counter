package paket;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class WebScannerPosao implements Posao {

    private ScanType tipPosla;
    private String putanjaPosla;
    private int hopCount;

    public WebScannerPosao(ScanType tipPosla, String putanjaPosla, int hopCount) {
        this.tipPosla = tipPosla;
        this.putanjaPosla = putanjaPosla;
        this.hopCount = hopCount;
    }

    @Override
    public ScanType getType() {
        return tipPosla;
    }

    @Override
    public String getQuery() {
        return null;
    }


    @Override
    public String getPath() {
        return putanjaPosla;
    }


    public int getHopCount() {
        return hopCount;
    }

    @Override
    public void initiate() {
        Future<Map<String,Integer>> rez = Main.getWebScannerTP().submit(new WebTask(putanjaPosla,hopCount));
        Main.getRt().getMapaSvega().put(putanjaPosla,rez);
    }

}
