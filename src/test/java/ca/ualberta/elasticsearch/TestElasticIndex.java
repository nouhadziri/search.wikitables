package ca.ualberta.elasticsearch;

import ca.ualberta.elasticsearch.index.ElasticIndex;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by nouhadziri on 2017-04-18.
 */
public class TestElasticIndex {
    private final ElasticSearchManager manager = new ElasticSearchManager();

//    @Test
    public void testNotLowercasedIndex() {
        try {
            final ElasticIndex index = new ElasticIndex() {
                @Override
                public String getName() {
                    return "testnotlowercased";
                }

                @Override
                public String getMapping() {
                    return "{\n" +
                            "	\"wikitables\": {\n" +
                            "		\"properties\": {\n" +
                            "			\"sentence\": {\n" +
                            "				\"type\": \"string\",\n" +
                            "				\"index\": \"analyzed\",\n" +
                            "				\"analyzer\": \"not_lowered_analyzer\"\n" +
                            "			}\n" +
                            "		}\n" +
                            "	}\n" +
                            "}";
                }

                @Override
                public String getSettings() {
                    return "{\n" +
                            "   \"analysis\": {\n" +
                            "      \"analyzer\": {\n" +
                            "           \"not_lowered_analyzer\": {\n" +
                            "               \"tokenizer\": \"standard\",\n" +
                            "               \"filter\" : [\"my_stop\", \"my_stemmer\"]" +
                            "           }\n" +
                            "       },\n" +
                            "       \"filter\" : {\n" +
                            "           \"my_stemmer\" : {\n" +
                            "               \"type\" : \"stemmer\",\n" +
                            "               \"name\" : \"english\"\n" +
                            "           },\n" +
                            "           \"my_stop\" : {\n" +
                            "               \"type\" : \"stop\",\n" +
                            "               \"stopwords\" : \"_english_\"\n" +
                            "           }\n" +
                            "       }" +
                            "   }\n" +
                            "}";
                }
            };

            manager.createSchema(index);
            manager.saveDocument(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("sentence", "List of (MOVIES) directed by Woody Allen")
                    .endObject(), index);

            final HighlightBuilder highlightBuilder = new HighlightBuilder().field("sentence");

            final SearchResponse resp1 = manager.submitSearchRequest(index, 0, QueryBuilders.matchQuery("sentence", "Movies"), highlightBuilder);
            Assert.assertTrue(resp1.getHits().getTotalHits() == 0);

            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException ignored) {
            }

            final SearchResponse resp2 = manager.submitSearchRequest(index, 0, QueryBuilders.matchQuery("sentence", "direct"), highlightBuilder);
            Assert.assertTrue(resp2.getHits().getTotalHits() > 0);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

//    @Test
    public void testStopwordsIncludedIndex() {
        try {
            final ElasticIndex index = new ElasticIndex() {
                @Override
                public String getName() {
                    return "teststopwordsincl";
                }

                @Override
                public String getMapping() {
                    return "{\n" +
                            "	\"wikitables\": {\n" +
                            "		\"properties\": {\n" +
                            "			\"sentence\": {\n" +
                            "				\"type\": \"string\",\n" +
                            "				\"index\": \"analyzed\",\n" +
                            "				\"analyzer\": \"stopincl_analyzer\"\n" +
                            "			}\n" +
                            "		}\n" +
                            "	}\n" +
                            "}";
                }

                @Override
                public String getSettings() {
                    return "{\n" +
                            "   \"analysis\": {\n" +
                            "      \"analyzer\": {\n" +
                            "           \"stopincl_analyzer\": {\n" +
                            "               \"tokenizer\": \"standard\",\n" +
                            "               \"filter\" : [ \"lowercase\", \"my_stemmer\"]" +
                            "           }\n" +
                            "       },\n" +
                            "       \"filter\" : {\n" +
                            "           \"my_stemmer\" : {\n" +
                            "               \"type\" : \"stemmer\",\n" +
                            "               \"name\" : \"english\"\n" +
                            "           }\n" +
                            "       }" +
                            "   }\n" +
                            "}";
                }
            };

            manager.createSchema(index);
            manager.saveDocument(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("sentence", "List of (MOVIES) directed by Woody Allen")
                    .endObject(), index);

            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException ignored) {
            }

            final HighlightBuilder highlightBuilder = new HighlightBuilder().field("sentence");

            final SearchResponse resp1 = manager.submitSearchRequest(index, 0, QueryBuilders.matchQuery("sentence", "BY"), highlightBuilder);
            Assert.assertTrue(resp1.getHits().getTotalHits() > 0);

            final SearchResponse resp2 = manager.submitSearchRequest(index, 0, QueryBuilders.matchQuery("sentence", "direct"), highlightBuilder);
            Assert.assertTrue(resp2.getHits().getTotalHits() > 0);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

//    @Test
    public void testNotStemmedIndex() {
        try {
            final ElasticIndex index = new ElasticIndex() {
                @Override
                public String getName() {
                    return "testnotstemmed";
                }

                @Override
                public String getMapping() {
                    return "{\n" +
                            "	\"wikitables\": {\n" +
                            "		\"properties\": {\n" +
                            "			\"sentence\": {\n" +
                            "				\"type\": \"string\",\n" +
                            "				\"index\": \"analyzed\",\n" +
                            "				\"analyzer\": \"nostem_analyzer\"\n" +
                            "			}\n" +
                            "		}\n" +
                            "	}\n" +
                            "}";
                }

                @Override
                public String getSettings() {
                    return "{\n" +
                            "   \"analysis\": {\n" +
                            "      \"analyzer\": {\n" +
                            "           \"nostem_analyzer\": {\n" +
                            "               \"tokenizer\": \"standard\",\n" +
                            "               \"filter\" : [ \"lowercase\", \"my_stop\"]" +
                            "           }\n" +
                            "       },\n" +
                            "       \"filter\" : {\n" +
                            "           \"my_stop\" : {\n" +
                            "               \"type\" : \"stop\",\n" +
                            "               \"stopwords\" : \"_english_\"\n" +
                            "           }\n" +
                            "       }\n" +
                            "   }\n" +
                            "}";
                }
            };

            manager.createSchema(index);
            manager.saveDocument(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("sentence", "List of (MOVIES) directed by Woody Allen")
                    .endObject(), index);

            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException ignored) {
            }

            final HighlightBuilder highlightBuilder = new HighlightBuilder().field("sentence");

            final SearchResponse resp1 = manager.submitSearchRequest(index, 0, QueryBuilders.matchQuery("sentence", "direct"), highlightBuilder);
            Assert.assertTrue(resp1.getHits().getTotalHits() == 0);

            final SearchResponse resp2 = manager.submitSearchRequest(index, 0, QueryBuilders.matchQuery("sentence", "the woody"), highlightBuilder);
            Assert.assertTrue(resp2.getHits().getTotalHits() > 0);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }



    @After
    public void tearDown() {
        manager.close();
    }
}
