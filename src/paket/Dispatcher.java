package paket;

public class Dispatcher extends Thread {

    @Override
    public void run() {
        while (true){
            try {
                Posao posao = Main.queZaPosao.take();
                if(posao.getType() == null){
                    break;
                }
                if (posao.getType() == ScanType.WEB){
                    WebScannerPosao wsp = new WebScannerPosao(posao.getType(),posao.getPath(), ((WebScannerPosao)posao).getHopCount());
//                    System.out.println(wsp.getPath() + " putanja za web job koj treba da se resi");
                    wsp.initiate();
                }else{
//                    System.out.println("Skinuo sam file scanner posao woooooooooo:" + posao.getPath());
                    FileScannerPosao fcp = new FileScannerPosao(ScanType.FILE,posao.getPath());
                    fcp.initiate();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
