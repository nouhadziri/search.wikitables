package ca.ualberta.benchmark.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class KeywordQuery {
    final String id;
    final String keyword;
    final List<QueryResult> queryResults = new ArrayList<>();

    public KeywordQuery(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return String.format("(%s) %s", id, keyword);
    }

    public String getKeyword() {
        return keyword;
    }

    public String getId() {
        return id;
    }

    public List<QueryResult> getQueryResults() {
        return queryResults;
    }

}
