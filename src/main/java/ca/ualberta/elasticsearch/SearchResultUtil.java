package ca.ualberta.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by nouhadziri on 2017-04-18.
 */
public interface SearchResultUtil {
    static List<SearchResult> toSearchResults(SearchResponse searchResponse) {
        List<SearchResult> searchResults = new ArrayList<>();
        // Scroll until no hits are returned
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // System.out.println("explanation:" + hit.getExplanation());
            final Map<String, Object> source = hit.getSource();

            searchResults.add(
                    new SearchResult(
                            source.get("title").toString(),
                            source.get("url").toString(),
                            source.get("articleId").toString(),
                            Integer.valueOf(source.get("tableIdx").toString()),
                            hit.getScore(),
                            hit.getHighlightFields())
            );

        }

        return searchResults;
    }

    public static String toSummary(List<SearchResult> searchResults) {
        StringBuilder summary = new StringBuilder();

        int i=0;
        for (SearchResult searchResult : searchResults) {
            summary.append("(").append(++i).append(")").append("\n");
            // System.out.println("explanation:" + hit.getExplanation());
            summary.append("title: ").append(searchResult.getTitle()).append("\n");
            summary.append("table: ").append(searchResult.getTableIndex()).append("\n");
            summary.append("url: ").append(searchResult.getUrl()).append("\n");
            summary.append("id: ").append(searchResult.getArticleId()).append("\n");
            summary.append("score: ").append(searchResult.getScore()).append("\n");

            for (Map.Entry<String, HighlightField> entry : searchResult.getHighlights().entrySet()) {
                summary.append("field: ").append(entry.getKey()).append("\n");
                summary.append("fragments: ").append(Arrays.toString(entry.getValue().getFragments())).append("\n");
            }
            summary.append("----------------------------").append("\n");
        }

        return summary.toString();
    }
}
