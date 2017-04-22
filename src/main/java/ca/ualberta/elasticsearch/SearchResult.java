package ca.ualberta.elasticsearch;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class SearchResult {
    private final String title;
    private final String titleForUI;
    private final String url;
    private final String articleId;
    private final int tableIndex;
    private final double score;
    private final List<String> categories;
    private final List<String> headers;
    private final List<TableContent> contents;
    private final Map<String, HighlightField> highlights;

    public SearchResult(String title, String url, String articleId, int tableIndex, double score, List<String> categories, List<String> headers, List<Map<String, Object>> contents, Map<String, HighlightField> highlights) {
        this.title = title;
        this.url = url;
        this.articleId = articleId;
        this.tableIndex = tableIndex;
        this.score = score;

        final HighlightField highlightedHeaders = highlights.get("headers");
        final HashMap<String, String> headerFragments = new HashMap<>();
        if (highlightedHeaders != null) {
            for (Text fragment : highlightedHeaders.getFragments()) {
                headerFragments.put(fragment.string().replaceAll("<mark>", "").replaceAll("</mark>", ""), fragment.string());
            }
        }

        this.headers = new ArrayList<>();
        boolean hasHeader = false;
        for (String header : headers) {
            if (header.equals("null") || header.isEmpty()) {
                this.headers.add("");
            } else {
                hasHeader = true;
                this.headers.add(headerFragments.getOrDefault(header, header));
            }
        }
        if (!hasHeader)
            this.headers.clear();

        final HighlightField highlightedCategories = highlights.get("categories");
        final HashMap<String, String> categoryFragments = new HashMap<>();
        if (highlightedCategories != null) {
            for (Text fragment : highlightedCategories.getFragments()) {
                categoryFragments.put(fragment.string().replaceAll("<mark>", "").replaceAll("</mark>", ""), fragment.string());
            }
        }

        this.categories = new ArrayList<>();
        for (String category : categories) {
            this.categories.add(categoryFragments.getOrDefault(category, category));
        }

        final HighlightField highlightedRows = highlights.get("contents.values");
        final HashMap<String, String> rowFragments = new HashMap<>();
        if (highlightedRows != null) {
            for (Text fragment : highlightedRows.getFragments()) {
                rowFragments.put(fragment.string().replaceAll("<mark>", "").replaceAll("</mark>", ""), fragment.string());
            }
        }

        this.contents = new ArrayList<>();
        for (Map<String, Object> content : contents) {
            final List<String> values = (List<String>) content.get("values");
            final List<String> matchedValues = new ArrayList<>();
            boolean hasValue = false;
            for (String value : values) {
                if (value.isEmpty() || value.equalsIgnoreCase("null")) {
                    matchedValues.add("");
                } else {
                    matchedValues.add(rowFragments.getOrDefault(value, value));
                    hasValue = true;
                }
            }

            if (hasValue)
                this.contents.add(new TableContent(Integer.valueOf(content.get("idx").toString()), matchedValues));
        }

        final HighlightField highlightedTitle = highlights.get("title");
        if (highlightedTitle != null) {
            this.titleForUI = highlightedTitle.fragments()[0].string();
        } else {
            this.titleForUI = title;
        }

        this.highlights = highlights;
    }

    public static class TableContent {
        private final int rowIndex;
        private final List<String> values;

        public TableContent(int rowIndex, List<String> values) {
            this.rowIndex = rowIndex;
            this.values = values;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public List<String> getValues() {
            return values;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getTitleForUI() {
        return titleForUI;
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

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<TableContent> getContents() {
        return contents;
    }

    public Map<String, HighlightField> getHighlights() {
        return highlights;
    }
}
