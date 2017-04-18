package ca.ualberta.benchmark.evaluation;

import ca.ualberta.benchmark.evaluation.IREvaluator;
import ca.ualberta.benchmark.evaluation.QueryEvaluationResult;
import ca.ualberta.benchmark.query.*;
import ca.ualberta.elasticsearch.ElasticSearchManager;
import ca.ualberta.elasticsearch.SearchResult;
import ca.ualberta.elasticsearch.index.ElasticIndex;

import java.util.List;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class Experiment {

    private static final ElasticSearchManager elasticSearchManager = new ElasticSearchManager();

    private static final int RESULTS_SIZE = 50;

    private static QueryEvaluationResult runKeywordQuery(KeywordQuery query, ElasticIndex index) {
        final List<SearchResult> searchResults = elasticSearchManager.keywordSearch(query.getKeyword(), index, RESULTS_SIZE);
        return new QueryEvaluationResult(query, searchResults);
    }

    private static QueryEvaluationResult runCategoryQuery(CategoryQuery query, ElasticIndex index) {
        final List<SearchResult> searchResults = elasticSearchManager.categorySearch(query.getKeyword(), query.getCategory(), index, RESULTS_SIZE);
        return new QueryEvaluationResult(query, searchResults);
    }

    private static QueryEvaluationResult runRelationQuery(RelationQuery query, ElasticIndex index) {
        final List<SearchResult> searchResults = elasticSearchManager.categorySearch(query.getKeyword(), query.getRelation(), index, RESULTS_SIZE);
        return new QueryEvaluationResult(query, searchResults);
    }

    static void runCategorySearchOnAnalyzedIndex() {
        final List<CategoryQuery> queries = new QueryContainer("data/category_queries.xml").getCategoryQueries();
        final IREvaluator evaluator = new IREvaluator();
        for (CategoryQuery query : queries) {
            evaluator.add(runCategoryQuery(query, ElasticIndex.analyzed));
        }

        System.out.println("categorySeach analyzed");
        System.out.println(evaluator.getSummary());
    }

    static void runKeywordSearchOnAllIndices() {
        final List<KeywordQuery> queries = new QueryContainer("data/keyword_queries.xml").getKeywordQueries();

        for (ElasticIndex elasticIndex : ElasticIndex.indices()) {
            final IREvaluator evaluator = new IREvaluator();

            for (KeywordQuery query : queries) {
                evaluator.add(runKeywordQuery(query, elasticIndex));
            }

            System.out.println("keyword search on " + elasticIndex.getName());
            System.out.println(evaluator.getSummary());
            System.out.println("--------------------");
        }
    }

    public static void main(String[] args) {
        runKeywordSearchOnAllIndices();
//        runCategorySearchOnAnalyzedIndex();
    }
}
