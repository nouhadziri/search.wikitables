package ca.ualberta.elasticsearch.index;

/**
 * Created by nouhadziri on 2017-04-14.
 */
public class NotStemmedElasticIndex implements ElasticIndex {
    @Override
    public String getName() {
        return "notstemmed";
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
                "           \"not_stemmed_analyzer\": {\n" +
                "               \"tokenizer\": \"standard\",\n" +
                "               \"filter\" : [ \"lowercase\", \"my_stop\"]\n" +
                "           }\n" +
                "      },\n" +
                "      \"filter\" : {\n" +
                "           \"my_stop\" : {\n" +
                "               \"type\" : \"stop\",\n" +
                "               \"stopwords\" : \"_english_\"\n" +
                "           }\n" +
                "      }\n" +
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
                "				\"analyzer\": \"not_stemmed_analyzer\"\n" +
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
                "				\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "			},\n" +
                "			\"redirects\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "			},\n" +
                "			\"categories\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "			},\n" +
                "			\"headers\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "			},\n" +
                "			\"headerTypes\": {\n" +
                "				\"type\": \"string\",\n" +
                "				\"index\": \"analyzed\",\n" +
                "				\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "			},\n" +
                "			\"contents\": {\n" +
                "				\"type\": \"nested\",\n" +
                "				\"properties\": {\n" +
                "					\"idx\": { \"type\": \"integer\" },\n" +
                "					\"values\": {\n" +
                "						\"type\": \"string\",\n" +
                "	        			\"index\": \"analyzed\",\n" +
                "		        		\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "					},\n" +
                "					\"abstracts\": {\n" +
                "						\"type\": \"string\",\n" +
                "		        		\"index\": \"analyzed\",\n" +
                "			        	\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "					},\n" +
                "					\"relationships\": {\n" +
                "						\"type\": \"string\",\n" +
                "		        		\"index\": \"analyzed\",\n" +
                "			        	\"analyzer\": \"not_stemmed_analyzer\"\n" +
                "					}\n" +
                "				}\n" +
                "			}\n" +
                "		}\n" +
                "	}\n" +
                "}";
    }
}
