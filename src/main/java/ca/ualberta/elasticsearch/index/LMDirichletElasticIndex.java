package ca.ualberta.elasticsearch.index;

/**
 * Created by nouhadziri on 2017-04-14.
 */
public class LMDirichletElasticIndex implements ElasticIndex {
    @Override
    public String getName() {
        return "lmdirichlet";
    }

    @Override
    public String getSettings() {
        return "{\n" +
                "    \"index\": {\n" +
                "      \"similarity\": {\n" +
                "        \"default\": {\n" +
                "          \"type\": \"LMDirichlet\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
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
                "				\"analyzer\": \"english\"\n" +
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
                "}";
    }
}
