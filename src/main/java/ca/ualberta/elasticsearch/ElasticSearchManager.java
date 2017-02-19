package ca.ualberta.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticSearchManager {

	final TransportClient client;


	public ElasticSearchManager() {
		try {
			client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9301));
		} catch (UnknownHostException e) {
			throw new ExceptionInInitializerError("unable to connect to ElasticSearch: " + e.getMessage());
		}
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
				"				\"analyzer\": \"english\"\n" +
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
				"				\"analyzer\": \"english\"\n" +
				"			},\n" +
				"			\"headers\": {\n" +
				"				\"type\": \"string\",\n" +
				"				\"index\": \"analyzed\",\n" +
				"				\"analyzer\": \"english\"\n" +
				"			},\n" +
				"			\"content\": {\n" +
				"				\"type\": \"nested\",\n" +
				"				\"properties\": {\n" +
				"					\"row.idx\": { \"type\": \"integer\" },\n" +
				"					\"row.value\": {\n" +
				"						\"type\": \"string\",\n" +
				"						\"index\": \"analyzed\",\n" +
				"						\"analyzer\": \"english\"\n" +
				"					}\n" +
				"				}\n" +
				"			}\n" +
				"		}\n" +
				"	}\n" +
				"}").execute().actionGet();
	}

	public void close() {
		client.close();
	}

	public void saveTables(List<String> contents) {
		if (contents.isEmpty())
			return;

		final BulkRequestBuilder bulkRequest = client.prepareBulk();

		for (String content : contents) {
			bulkRequest.add(client.prepareIndex("wikipedia", "wikitables").setSource(content));
		}

		BulkResponse response = bulkRequest.get();
		if (response.hasFailures())
			System.out.println("ERROR!");
	}

	public void getResponse(String query){

		SearchResponse scrollResp = client.prepareSearch("wikipedia")
				.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				.setScroll(new TimeValue(60000))
				.setQuery(QueryBuilders.matchQuery("headers", query))
				.setSize(100).get(); //max of 100 hits will be returned for each scroll
		//Scroll until no hits are returned
		do {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				System.out.println(hit.getSourceAsString());
			}

			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		} while(scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.


	}


}
