package ca.ualberta.benchmark.evaluation;

import ca.ualberta.benchmark.query.KeywordQuery;
import ca.ualberta.benchmark.query.QueryResult;
import ca.ualberta.elasticsearch.SearchResult;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Created by nouhadziri on 2017-04-17.
 */
public class QueryEvaluationResult {
    private final KeywordQuery query;
    private final List<SearchResult> searchResults;

    static final int POINTS_11 = 11;

    public QueryEvaluationResult(KeywordQuery query, List<SearchResult> searchResults) {
        this.query = query;
        this.searchResults = searchResults;

        this.calcMetrics();
    }

    private double averagePrecision;
    private double rPrecision;
    private double[] interpolatedPrecision;

    private void calcMetrics() {
        final int totalRelevants = query.getQueryResults().size();

        int i = 0;
        int relevants = 0;
        int relevantsInTopR = 0;
        this.interpolatedPrecision = new double[POINTS_11];
        Arrays.fill(this.interpolatedPrecision, 0);


        DoubleSummaryStatistics avgPrecisionSummary = new DoubleSummaryStatistics();

        for (SearchResult searchResult : searchResults) {

            if (query.getQueryResults().contains(QueryResult.from(searchResult))) {
                relevants++;
                double recall = (double) relevants / totalRelevants;
                double precisionAtI = (double) relevants / (i + 1);
                avgPrecisionSummary.accept(precisionAtI);

                for (int j = 0; j < this.interpolatedPrecision.length; j++) {
                    if (recall >= 0.1 * j) {
                        if (this.interpolatedPrecision[j] == 0)
                            this.interpolatedPrecision[j] = precisionAtI;
                    } else
                        break;
                }

                if (i < totalRelevants)
                    relevantsInTopR++;
            }

            i++;
        }

        this.averagePrecision = avgPrecisionSummary.getAverage();
        this.rPrecision = (double) relevantsInTopR / totalRelevants;
    }

    public double getAveragePrecision() {
        return averagePrecision;
    }

    public double getRPrecision() {
        return rPrecision;
    }

    public double[] getInterpolatedPrecision() {
        return interpolatedPrecision;
    }
}
