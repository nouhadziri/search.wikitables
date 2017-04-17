package ca.ualberta.elasticsearch.index;

/**
 * Created by nouhadziri on 2017-04-14.
 */
public class StopwordsIncludedElasticIndex implements ElasticIndex {
    @Override
    public String getName() {
        return "stopwordsincluded";
    }

    @Override
    public String getSettings(){
        return "{\n" +
                "   \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "           \"stopwords_included_analyzer\": {\n" +
                "               \"type\": \"standard\",\n" +
                "               \"filter\" : [\"standard\", \"lowercase\", \"my_stemmer\"]" +
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

    @Override
    public String getMapping() {
        return "{\n" +
                "	\"wikitables\": {\n" +
                "		\"properties\": {\n" +
                "			\"title\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer\"\n" +
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
                "				\"analyzer\": \"stopwords_included_analyzer\"\n" +
                "			},\n" +
                "			\"redirects\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer\"\n" +
                "			},\n" +
                "			\"categories\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer\"\n" +
                "			},\n" +
                "			\"headers\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer\"\n" +
                "			},\n" +
                "			\"headerTypes\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer\"\n" +
                "			},\n" +
                "			\"contents\": {\n" +
                "				\"type\": \"nested\",\n" +
                "				\"properties\": {\n" +
                "					\"idx\": { \"type\": \"integer\" },\n" +
                "					\"values\": {\n" +
                "						\"type\": \"string\",\n" +
                "						\"index\": \"analyzed\",\n" +
                "				        \"analyzer\": \"stopwords_included_analyzer\"\n" +
                "					},\n" +
                "					\"abstracts\": {\n" +
                "						\"type\": \"string\",\n" +
                "						\"index\": \"analyzed\",\n" +
                "				        \"analyzer\": \"stopwords_included_analyzer\"\n" +
                "					},\n" +
                "					\"relationships\": {\n" +
                "						\"type\": \"string\",\n" +
                "				        \"index\": \"analyzed\",\n" +
                "				        \"analyzer\": \"stopwords_included_analyzer\"\n" +
                "					}\n" +
                "				}\n" +
                "			}\n" +
                "		}\n" +
                "	}\n" +
                "}";
    }
}
