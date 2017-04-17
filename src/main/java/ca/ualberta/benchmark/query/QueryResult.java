package ca.ualberta.benchmark.query;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class QueryResult {
    private final int table;
    private final String page;

    public QueryResult(int table, String page) {
        this.table = table;
        this.page = page;
    }

    public int getTable() {
        return table;
    }

    public String getPage() {
        return page;
    }
}
