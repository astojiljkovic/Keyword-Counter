package paket;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class FileScannerPosao implements Posao {


    private ScanType tipPosla;
    private String putanjaPosla;

    public FileScannerPosao(ScanType tipPosla, String putanjaPosla) {
        this.tipPosla = tipPosla;
        this.putanjaPosla = putanjaPosla;
    }

    @Override
    public ScanType getType() {
        return tipPosla;
    }

    @Override
    public String getPath() {
        return putanjaPosla;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public void initiate() {
        File corpus = new File(putanjaPosla);
        File[] fajlovi = corpus.listFiles();
        
        Future<Map<String,Integer>> rez = Main.getFileScannerTP().submit(new FileTask(fajlovi));
        Main.getRt().getMapaSvega().put(putanjaPosla,rez);
    }

    
}
