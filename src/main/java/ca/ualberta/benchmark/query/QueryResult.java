package ca.ualberta.benchmark.query;

import ca.ualberta.elasticsearch.SearchResult;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class QueryResult {
    private final int table;
    private final String page;

    public static QueryResult from(SearchResult searchResult) {
        return new QueryResult(searchResult.getTableIndex(), searchResult.getTitle());
    }

    public QueryResult(int table, String page) {
        this.table = table;
        this.page = page;
    }

    @Override
    public String toString() {
        return String.format("#%d %s", table, page);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryResult that = (QueryResult) o;

        return table == that.table && page.equals(that.page);
    }

    @Override
    public int hashCode() {
        int result = table;
        result = 31 * result + page.hashCode();
        return result;
    }

    public int getTable() {
        return table;
    }

    public String getPage() {
        return page;
    }
}
