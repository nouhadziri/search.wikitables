package ca.ualberta.server;

import ca.ualberta.elasticsearch.SearchResult;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nouhadziri on 2017-04-21.
 */
public class SearchResponse {
    private final boolean ok;
    private final List<String> messages;
    private final List<SearchResult> results;

    public static SearchResponse failed(String message) {
        return new SearchResponse(false, Stream.of(message).collect(Collectors.toList()), new LinkedList<>());
    }

    public static SearchResponse failed(List<String> messages) {
        return new SearchResponse(false, messages, new LinkedList<>());
    }

    public static SearchResponse successful(List<SearchResult> results) {
        return new SearchResponse(true, new LinkedList<>(), results);
    }


    private SearchResponse(boolean ok, List<String> messages, List<SearchResult> results) {
        this.messages = messages;
        this.results = results;
        this.ok = ok;
    }


    public boolean isOk() {
        return ok;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<SearchResult> getResults() {
        return results;
    }
}
