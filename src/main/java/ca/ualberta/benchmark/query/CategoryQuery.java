package ca.ualberta.benchmark.query;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class CategoryQuery extends KeywordQuery {
    private final String category;

    public CategoryQuery(String id, String keyword, String category) {
        super(id, keyword);
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%s category:\"%s\"", super.toString(), category);
    }

    public String getCategory() {
        return category;
    }
}
