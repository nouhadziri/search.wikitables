package ca.ualberta.elasticsearch;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.Map;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class SearchResult {
    private final String title;
    private final String url;
    private final String articleId;
    private final int tableIndex;
    private final double score;
    private final Map<String, HighlightField> highlights;


    public SearchResult(String title, String url, String articleId, int tableIndex, double score, Map<String, HighlightField> highlights) {
        this.title = title;
        this.url = url;
        this.articleId = articleId;
        this.tableIndex = tableIndex;
        this.score = score;
        this.highlights = highlights;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getArticleId() {
        return articleId;
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public double getScore() {
        return score;
    }

    public Map<String, HighlightField> getHighlights() {
        return highlights;
    }
}
