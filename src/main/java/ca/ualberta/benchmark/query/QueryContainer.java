package ca.ualberta.benchmark.query;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class QueryContainer {
    private final List<KeywordQuery> keywordQueries = new ArrayList<>();
    private final List<RelationQuery> relationQueries = new ArrayList<>();
    private final List<CategoryQuery> categoryQueries = new ArrayList<>();

    public QueryContainer(String... fileNames) {
        if (fileNames != null) {
            for (String fileName : fileNames) {
                parse(fileName);
            }
        }
    }

    public List<KeywordQuery> getKeywordQueries() {
        return keywordQueries;
    }

    public List<RelationQuery> getRelationQueries() {
        return relationQueries;
    }

    public List<CategoryQuery> getCategoryQueries() {
        return categoryQueries;
    }

    private void parse(String fileName) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            final SAXParser parser = factory.newSAXParser();
            parser.parse(fileName, new QueryXmlHandler());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("error in parsing file: " + fileName + " - " + e.getMessage());
        }
    }

    private class QueryXmlHandler extends DefaultHandler {
        private KeywordQuery query;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {
                case "query":
                    final String queryId = attributes.getValue("id");
                    final String keyword = attributes.getValue("keyword");
                    final String relation = attributes.getValue("relation");
                    final String category = attributes.getValue("category");
                    if (relation != null) {
                        query = new RelationQuery(queryId, keyword, relation);
                    } else if (category != null) {
                        query = new CategoryQuery(queryId, keyword, category);
                    } else {
                        query = new KeywordQuery(queryId, keyword);
                    }
                    break;
                case "result":
                    try {
                        final Integer table = Integer.valueOf(attributes.getValue("table"));
                        final String page = attributes.getValue("page");
                        query.getQueryResults().add(new QueryResult(table, page));
                    } catch (NumberFormatException e) {
                        System.err.println("error in parsing query results [" + query + "] " + e.getMessage());
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("query")) {
                if (query instanceof CategoryQuery)
                    categoryQueries.add((CategoryQuery) query);
                else if (query instanceof RelationQuery)
                    relationQueries.add((RelationQuery) query);
                else
                    keywordQueries.add(query);

                query = null;
            }
        }

    }


}
