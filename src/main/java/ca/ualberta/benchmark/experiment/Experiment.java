package ca.ualberta.benchmark.experiment;

import ca.ualberta.benchmark.query.CategoryQuery;
import ca.ualberta.benchmark.query.KeywordQuery;
import ca.ualberta.benchmark.query.QueryContainer;
import ca.ualberta.benchmark.query.RelationQuery;
import ca.ualberta.elasticsearch.ElasticSearchManager;
import ca.ualberta.elasticsearch.SearchResult;
import ca.ualberta.elasticsearch.index.ElasticIndex;

import javax.management.Query;
import java.util.List;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class Experiment {

    private static final ElasticSearchManager elasticSearchManager = new ElasticSearchManager();

    private static final int RESULTS_SIZE = 50;

    private static void runKeywordQuery(KeywordQuery query, ElasticIndex index) {
        query.getQueryResults();
        final List<SearchResult> searchResults = elasticSearchManager.keywordSearch(query.getKeyword(), index, RESULTS_SIZE);
    }

    private static void runCategoryQuery(CategoryQuery query, ElasticIndex index) {
        query.getQueryResults();
        final List<SearchResult> searchResults = elasticSearchManager.categorySearch(query.getKeyword(), query.getCategory(), index, RESULTS_SIZE);
    }

    private static void runRelationQuery(RelationQuery query, ElasticIndex index) {
        query.getQueryResults();
        final List<SearchResult> searchResults = elasticSearchManager.categorySearch(query.getKeyword(), query.getRelation(), index, RESULTS_SIZE);
    }
}
