package ca.ualberta.elasticsearch.index;

/**
 * Created by nouhadziri on 2017-04-14.
 */
public class LuceneScoringStopwordsIncludedElasticIndex implements ElasticIndex {
    @Override
    public String getName() {
        return "lucenestopwordsincluded";
    }

    @Override
    public String getSettings(){
        return "{\n" +
                "    \"index\": {\n" +
                "      \"similarity\": {\n" +
                "        \"default\": {\n" +
                "          \"type\": \"classic\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "   \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "           \"stopwords_included_analyzer2\": {\n" +
                "               \"tokenizer\": \"standard\",\n" +
                "               \"filter\" : [ \"lowercase\", \"my_stemmer2\"]" +
                "           }\n" +
                "       },\n" +
                "       \"filter\" : {\n" +
                "           \"my_stemmer2\" : {\n" +
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
                "				\"analyzer\": \"stopwords_included_analyzer2\"\n" +
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
                "				\"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "			},\n" +
                "			\"redirects\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "			},\n" +
                "			\"categories\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "			},\n" +
                "			\"headers\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "			},\n" +
                "			\"headerTypes\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "			},\n" +
                "			\"contents\": {\n" +
                "				\"type\": \"nested\",\n" +
                "				\"properties\": {\n" +
                "					\"idx\": { \"type\": \"integer\" },\n" +
                "					\"values\": {\n" +
                "						\"type\": \"string\",\n" +
                "						\"index\": \"analyzed\",\n" +
                "				        \"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "					},\n" +
                "					\"abstracts\": {\n" +
                "						\"type\": \"string\",\n" +
                "						\"index\": \"analyzed\",\n" +
                "				        \"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "					},\n" +
                "					\"relationships\": {\n" +
                "						\"type\": \"string\",\n" +
                "				        \"index\": \"analyzed\",\n" +
                "				        \"analyzer\": \"stopwords_included_analyzer2\"\n" +
                "					}\n" +
                "				}\n" +
                "			}\n" +
                "		}\n" +
                "	}\n" +
                "}";
    }
}
