package ca.ualberta.elasticsearch;

import ca.ualberta.elasticsearch.index.ElasticIndex;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ElasticSearchManager implements Closeable {

    final TransportClient client;

    public ElasticSearchManager() {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            throw new ExceptionInInitializerError("unable to connect to ElasticSearch: " + e.getMessage());
        }
    }

    public void createAllIndices() {
        for (ElasticIndex elasticIndex : ElasticIndex.indices()) {
            createSchema(elasticIndex);
        }
    }

    void createSchema(ElasticIndex elasticIndex) {
        IndicesExistsResponse existsResponse = client.admin().indices().prepareExists(elasticIndex.getName()).execute().actionGet();

        if (existsResponse.isExists()) {
            client.admin().indices().prepareDelete(elasticIndex.getName()).execute().actionGet();
        }

        final CreateIndexRequestBuilder requestBuilder = client.admin().indices().prepareCreate(elasticIndex.getName());

        if (!elasticIndex.getSettings().isEmpty())
            requestBuilder.setSettings(elasticIndex.getSettings());

        requestBuilder.addMapping(ElasticIndex.TYPE, elasticIndex.getMapping()).execute().actionGet();
    }

    public void createSchema() {
        IndicesExistsResponse existsResponse = client.admin().indices().prepareExists("wikipedia").execute().actionGet();

        if (existsResponse.isExists()) {
            client.admin().indices().prepareDelete("wikipedia").execute().actionGet();
        }

        client.admin().indices()
                .prepareCreate("wikipedia")
                .addMapping("wikitables", "{\n" +
                        "	\"wikitables\": {\n" +
                        "		\"properties\": {\n" +
                        "			\"title\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"analyzed\",\n" +
                        "				\"analyzer\": \"english\",\n" +
                        "				\"fields\": {\n" +
                        "						\"std\":{\n" +
                        "									\"type\": \"string\",\n" +
                        "									\"analyzer\": \"standard\"\n" +
                        "								}\n" +
                        "							}\n" +
                        "			},\n" +
                        "			\"articleId\": {\n" +
                        "				\"type\": \"integer\",\n" +
                        "				\"index\": \"not_analyzed\"\n" +
                        "			},\n" +
                        "			\"tableIdx\": {\n" +
                        "				\"type\": \"integer\",\n" +
                        "				\"index\": \"no\"\n" +
                        "			},\n" +
                        "			\"url\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"no\"\n" +
                        "			},\n" +
                        "			\"abstract\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"analyzed\",\n" +
                        "				\"analyzer\": \"english\"\n" +
                        "			},\n" +
                        "			\"redirects\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"analyzed\",\n" +
                        "				\"analyzer\": \"english\"\n" +
                        "			},\n" +
                        "			\"categories\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"analyzed\",\n" +
                        "				\"analyzer\": \"english\"\n" +
                        "			},\n" +
                        "			\"headers\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"analyzed\",\n" +
                        "				\"analyzer\": \"english\"\n" +
                        "			},\n" +
                        "			\"headerTypes\": {\n" +
                        "				\"type\": \"string\",\n" +
                        "				\"index\": \"analyzed\",\n" +
                        "				\"analyzer\": \"english\"\n" +
                        "			},\n" +
                        "			\"contents\": {\n" +
                        "				\"type\": \"nested\",\n" +
                        "				\"properties\": {\n" +
                        "					\"idx\": { \"type\": \"integer\" },\n" +
                        "					\"values\": {\n" +
                        "						\"type\": \"string\",\n" +
                        "						\"index\": \"analyzed\",\n" +
                        "						\"analyzer\": \"english\"\n" +
                        "					},\n" +
                        "					\"abstracts\": {\n" +
                        "						\"type\": \"string\",\n" +
                        "						\"index\": \"analyzed\",\n" +
                        "						\"analyzer\": \"english\"\n" +
                        "					},\n" +
                        "					\"relationships\": {\n" +
                        "						\"type\": \"string\",\n" +
                        "						\"index\": \"analyzed\",\n" +
                        "						\"analyzer\": \"english\"\n" +
                        "					}\n" +
                        "				}\n" +
                        "			}\n" +
                        "		}\n" +
                        "	}\n" +
                        "}").execute().actionGet();
        /*.addMapping("wikitables", "{\n" +
				"	\"wikitables\": {\n" +
				"		\"properties\": {\n" +
				"			\"title\": {\n" +
				"				\"type\": \"string\",\n" +
				"				\"index\": \"analyzed\",\n" +
				"				\"analyzer\": \"english\",\n" +
				"				\"similarity\": \"BM25\"\n" +
				"			},\n" +
				"			\"article.id\": {\n" +
				"				\"type\": \"integer\",\n" +
				"				\"index\": \"not_analyzed\"\n" +
				"			},\n" +
				"			\"table.number\": {\n" +
				"				\"type\": \"integer\",\n" +
				"				\"index\": \"no\"\n" +
				"			},\n" +
				"			\"url\": {\n" +
				"				\"type\": \"string\",\n" +
				"				\"index\": \"no\"\n" +
				"			},\n" +
				"			\"categories\": {\n" +
				"				\"type\": \"string\",\n" +
				"				\"index\": \"analyzed\",\n" +
				"				\"analyzer\": \"english\",\n" +
				"				\"similarity\": \"BM25\"\n" +
				"			},\n" +
				"			\"headers\": {\n" +
				"				\"type\": \"string\",\n" +
				"				\"index\": \"analyzed\",\n" +
				"				\"analyzer\": \"english\",\n" +
				"				\"similarity\": \"BM25\"\n" +
				"			},\n" +
				"			\"content\": {\n" +
				"				\"type\": \"nested\",\n" +
				"				\"properties\": {\n" +
				"					\"row.idx\": { \"type\": \"integer\" },\n" +
				"					\"row.value\": {\n" +
				"						\"type\": \"string\",\n" +
				"						\"index\": \"analyzed\",\n" +
				"						\"analyzer\": \"english\",\n" +
				"						\"similarity\": \"BM25\"\n" +
				"					}\n" +
				"				}\n" +
				"			}\n" +
				"		}\n" +
				"	}\n" +
				"}").execute().actionGet();*/
    }

    // the english analyzer perform stemming,removing stop words, lowercase
    // I'm not happy about the foxes --> i'm, happi, about, fox
    // While the language analyzers can be used out of the box without any
    // configuration,
    // most of them do allow you to control aspects of their behavior,
    // specifically: p354

    // we can customize the behavior of the English analyzer

    @Override
    public void close() {
        client.close();
    }

    public void saveDocumentsOnAllIndices(List<String> contents) {
        if (contents.isEmpty())
            return;

        for (ElasticIndex elasticIndex : ElasticIndex.indices()) {
            final BulkRequestBuilder bulkRequest = client.prepareBulk();

            for (String content : contents) {
                bulkRequest.add(client.prepareIndex(elasticIndex.getName(), ElasticIndex.TYPE).setSource(content));
            }

            BulkResponse responses = bulkRequest.get();
            if (responses.hasFailures())
                for (BulkItemResponse resp : responses) {
                    System.err.println(String.format("[%s] {%s} %s", resp.getIndex(), resp.getId(), resp.getFailureMessage()));
                }
        }
    }

    void saveDocument(XContentBuilder content, ElasticIndex index) {
        client.prepareIndex(index.getName(), ElasticIndex.TYPE).setSource(content).get();
    }

    public void saveTables(List<String> contents) {
        if (contents.isEmpty())
            return;

        final BulkRequestBuilder bulkRequest = client.prepareBulk();

        for (String content : contents) {
            bulkRequest.add(client.prepareIndex("wikipedia", "wikitables").setSource(content));
        }

        BulkResponse responses = bulkRequest.get();
        if (responses.hasFailures())
            for (BulkItemResponse resp : responses) {
                System.err.println(String.format("[%s] {%s} %s", resp.getIndex(), resp.getId(), resp.getFailureMessage()));
            }
    }

    SearchResponse submitSearchRequest(ElasticIndex index, int maxSize, QueryBuilder queryBuilder, HighlightBuilder highlightBuilder) {
        final SearchRequestBuilder requestBuilder = client.prepareSearch(index.getName()).setQuery(queryBuilder);
        if (highlightBuilder != null)
            requestBuilder.highlighter(highlightBuilder);
        if (maxSize > 0)
            requestBuilder.setSize(maxSize);

        return requestBuilder.get();
    }

    public List<SearchResult> keywordSearch(String keyword, ElasticIndex index, int from, int maxSize) {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.should(QueryBuilders.matchQuery("headerTypes", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("headers", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("redirects", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("title", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("categories", keyword));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.values", keyword), ScoreMode.Avg)).boost(3);
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.abstracts", keyword), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.relationships", keyword), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.matchQuery("abstract", keyword));

        SearchResponse searchResponse = client.prepareSearch(index.getName())
                .setQuery(queryBuilder)
                .highlighter(new HighlightBuilder()
                        .field("headers")
                        .field("headerTypes")
                        .field("contents.abstracts")
                        .field("abstract")
                        .field("contents.values")
                        .field("title")
                        .field("categories")
                        .preTags("<mark>").postTags("</mark>"))
                .setSize(maxSize).setFrom(from).get(); // max of 100 hits will be returned for

        return SearchResultUtil.toSearchResults(searchResponse);
    }

    public List<SearchResult> keywordSearch(String keyword, ElasticIndex index, int maxSize) {
        return keywordSearch(keyword, index, 0, maxSize);
    }


    public List<SearchResult> categorySearch(String category, String contains, ElasticIndex index, int from, int maxSize) {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.should(QueryBuilders.matchQuery("redirects", category));
        queryBuilder.should(QueryBuilders.matchQuery("title", category));
        queryBuilder.should(QueryBuilders.matchQuery("categories", category).boost(3));
        queryBuilder.should(QueryBuilders.matchQuery("headers", contains));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.values", contains), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.abstracts", contains), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.values", category), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.relationships", contains), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.matchQuery("abstract", contains));

        SearchResponse searchResponse = client.prepareSearch(index.getName())
                .setQuery(queryBuilder)
                .highlighter(new HighlightBuilder()
                        .field("headers")
                        .field("headerTypes")
                        .field("contents.abstracts")
                        .field("abstract")
                        .field("contents.values")
                        .field("title")
                        .field("categories")
                        .preTags("<mark>").postTags("</mark>"))
                .setSize(maxSize).setFrom(from).get();
        int i = 0;
  /*      // Scroll until no hits are returned
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println("(" + ++i + ")");
            // System.out.println("explanation:" + hit.getExplanation());
            System.out.println("title: " + hit.getSource().get("title"));
            System.out.println("url: " + hit.getSource().get("url"));
            System.out.println("articleId: " + hit.getSource().get("articleId"));
            System.out.println("table: " + hit.getSource().get("tableIdx"));
            System.out.println("score: " + hit.getScore());
            System.out.println(hit.getHighlightFields());
            System.out.println("----------------------------");
            // System.out.println(hit.getSourceAsString());

        }*/
        return SearchResultUtil.toSearchResults(searchResponse);
    }

    public List<SearchResult> categorySearch(String category, String contains, ElasticIndex index, int maxSize) {
        return categorySearch(category, contains, index, 0, maxSize);
    }

    public List<SearchResult> relationSearch(String keyword, String relation ,ElasticIndex index, int from, int maxSize) {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //queryBuilder.should(QueryBuilders.matchQuery("headerTypes", query));
        queryBuilder.should(QueryBuilders.matchQuery("headers", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("redirects", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("title", keyword));
        queryBuilder.should(QueryBuilders.matchQuery("categories", keyword));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.values", keyword), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.abstracts", keyword), ScoreMode.Avg));
        queryBuilder.should(QueryBuilders.nestedQuery("contents",
                QueryBuilders.matchQuery("contents.relationships", relation), ScoreMode.Avg)).boost(3);
        queryBuilder.should(QueryBuilders.matchQuery("abstract", keyword));


        SearchResponse searchResponse = client.prepareSearch(index.getName())
                .setQuery(queryBuilder)
                .highlighter(new HighlightBuilder()
                        .field("headers")
                        .field("headerTypes")
                        .field("contents.relationships")
                        .field("abstract")
                        .field("contents.values")
                        .field("title")
                        .field("categories")
                        .preTags("<mark>").postTags("</mark>"))
                .setSize(maxSize).setFrom(from).get(); // max of 100 hits will be returned for

        return SearchResultUtil.toSearchResults(searchResponse);
    }

    public List<SearchResult> relationSearch(String keyword, String relation ,ElasticIndex index, int maxSize) {
        return relationSearch(keyword, relation, index, 0, maxSize);
    }




    /**
     * return documents that match any of the query keywords and
     * return the score of the best matching query
     * https://www.elastic.co/guide/en/elasticsearch/guide/current/_tuning_best_fields_queries.html
     *
     * @param query
     */

    public void Dis_maxQuery(String query) {

        QueryBuilder qb = null;
        // create the query
        qb = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.matchQuery("title", query))
                .boost(10)
                //.tieBreaker(0.3f)
                .add(QueryBuilders.matchQuery("abstract", query))
                .boost(2)
                .add(QueryBuilders.nestedQuery("content", QueryBuilders.matchQuery("content.row.value", query), ScoreMode.Avg))
                .add(QueryBuilders.matchQuery("categories", query))
                .add(QueryBuilders.matchQuery("redirects", query));

        SearchResponse searchResponse = client.prepareSearch("wikipedia")
                .setQuery(qb)
                .highlighter(new HighlightBuilder()
                        .field("abstract").preTags("__").postTags("__")
                        .field("title").preTags("__").postTags("__")
                        .field("categories").preTags("__").postTags("__")
                        .field("redirects").preTags("__").postTags("__"))
                .setSize(20).get();

        for (SearchHit hit : searchResponse.getHits().getHits()) {

            System.out.println("title: " + hit.getSource().get("title"));
            System.out.println("url: " + hit.getSource().get("url"));
            System.out.println("id: " + hit.getSource().get("article.id"));
            System.out.println("table: " + hit.getSource().get("table.number"));
            System.out.println("score: " + hit.getScore());
            System.out.println(hit.getHighlightFields());
            System.out.println("----------------------------");


        }

    }



    /**
     * multi-keywords should match the title and the content of the table
     *
     * @param title
     * @param contains
     */

    public void advancedSearchTitleAndBody(String title, String contains) {

        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //queryBuilder.must(QueryBuilders.matchQuery("categories", category));
        queryBuilder.must(QueryBuilders.matchQuery("title", title));
        queryBuilder.must(QueryBuilders.nestedQuery("content", QueryBuilders.matchPhraseQuery("content.row.value", contains), ScoreMode.Avg));

        SearchResponse searchResponse = client.prepareSearch("wikipedia")
                .setQuery(queryBuilder)
                .highlighter(new HighlightBuilder()
                        .field("content.row.value").preTags("__").postTags("__")
                        .field("title").preTags("__").postTags("__"))
//			 .setExplain(true)
                .setSize(20).get(); // max of 100 hits will be returned for
        // each scroll

        // Scroll until no hits are returned
        for (SearchHit hit : searchResponse.getHits().getHits()) {
//		 System.out.println("explanation:" + hit.getExplanation());
            System.out.println("title: " + hit.getSource().get("title"));
            System.out.println("url: " + hit.getSource().get("url"));
            System.out.println("id: " + hit.getSource().get("article.id"));
            System.out.println("table: " + hit.getSource().get("table.number"));
            System.out.println("score: " + hit.getScore());
            System.out.println(hit.getHighlightFields());
            System.out.println("----------------------------");
            // System.out.println(hit.getSourceAsString());

        }


    }





}
