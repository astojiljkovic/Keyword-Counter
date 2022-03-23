package paket;

import java.util.Map;
import java.util.concurrent.Future;

public interface ResultRetrieverI {

    public Map<String, Integer> getResult(String query);

    public Map<String, Integer> queryResult(String query);

    public void clearSummary(ScanType summaryType);

    public Map<String, Map<String, Integer>> getSummary(ScanType summaryType);

    public Map<String, Map<String, Integer>> querySummary(ScanType summaryType);

}
