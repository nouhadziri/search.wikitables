package ca.ualberta.elasticsearch.index;

/**
 * Created by nouhadziri on 2017-04-14.
 */
public interface ElasticIndex {
    public String getName();
    public String getMapping();

    default String getSettings() {
        return "";
    }

    public static ElasticIndex[] indices() {
        return new ElasticIndex[] {
                analyzed, notAnalyzed, notLowercased, notStemmed, stopwordsIncluded, luceneScoring, lmDirichlet, lmJelinek
        };
    }

    String TYPE = "wikitables";

    AnalyzedElasticIndex analyzed = new AnalyzedElasticIndex();
    NotAnalyzedElasticIndex notAnalyzed = new NotAnalyzedElasticIndex();
    NotLowercasedElasticIndex notLowercased = new NotLowercasedElasticIndex();
    NotStemmedElasticIndex notStemmed = new NotStemmedElasticIndex();
    StopwordsIncludedElasticIndex stopwordsIncluded = new StopwordsIncludedElasticIndex();
    LuceneScoringElasticIndex luceneScoring = new LuceneScoringElasticIndex();
    LMDirichletElasticIndex lmDirichlet = new LMDirichletElasticIndex();
    LMJelinekElasticIndex lmJelinek = new LMJelinekElasticIndex();
}
