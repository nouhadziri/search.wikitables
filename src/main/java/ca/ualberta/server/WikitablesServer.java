package ca.ualberta.server;

import ca.ualberta.elasticsearch.ElasticSearchManager;
import ca.ualberta.elasticsearch.SearchResult;
import ca.ualberta.elasticsearch.index.ElasticIndex;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.elasticsearch.common.Strings;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.*;


/**
 * Created by nouhadziri on 2017-04-21.
 */
public class WikitablesServer {
    public static void main(String[] args) {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(SearchResult.class, "/");
        staticFileLocation("/public");

        get("/", (req, res) -> {
            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/home.ftl");
            Map<String, Object> params = new HashMap<>();
            template.process(params, writer);
            return writer;
        });

        get("/sinit", (req, res) -> {
            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/search.ftl");
            Map<String, Object> params = new HashMap<>();
            template.process(params, writer);
            return writer;
        });

        get("/cinit", (req, res) -> {
            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/catsearch.ftl");
            Map<String, Object> params = new HashMap<>();
            template.process(params, writer);
            return writer;
        });

        get("/rinit", (req, res) -> {
            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/relsearch.ftl");
            Map<String, Object> params = new HashMap<>();
            template.process(params, writer);
            return writer;
        });

        get("/search", (req, res) -> {
            final String keyword = req.queryParams("q");
            final String fromParam = req.queryParams("_s");

            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/search.ftl");
            Map<String, Object> params = new HashMap<>();

            try {
                List<String> errors = new ArrayList<>();

                if (Strings.isNullOrEmpty(keyword))
                    errors.add("no keyword is provided");

                int from = 0;
                if (!Strings.isNullOrEmpty(fromParam))
                    try {
                        from = Integer.valueOf(fromParam);
                    } catch (NumberFormatException ignored) {
                    }

                res.status(200);
                if (!errors.isEmpty()) {
                    params.put("errors", errors.stream().collect(Collectors.joining("\n")));

                } else {
                    params.put("query", keyword);
                    params.put("next", from+10);
                    if (from - 10 >= 0)
                        params.put("previous", from-10);

                    try (final ElasticSearchManager elasticSearchManager = new ElasticSearchManager()) {
                        final List<SearchResult> results = elasticSearchManager.keywordSearch(keyword, ElasticIndex.luceneScoring, from,10);
                        params.put("results", results);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                halt(500);
                params.put("errors", "Ah! something went wrong, please try again.");
            }

            template.process(params, writer);
            return writer;
        });

        get("/catsearch", (req, res) -> {
            final String keyword = req.queryParams("q");
            final String category = req.queryParams("c");
            final String fromParam = req.queryParams("_s");

            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/catsearch.ftl");
            Map<String, Object> params = new HashMap<>();

            try {
                List<String> errors = new ArrayList<>();

                if (Strings.isNullOrEmpty(keyword))
                    errors.add("no keyword is provided");

                if (Strings.isNullOrEmpty(category))
                    errors.add("no category is provided");

                int from = 0;
                if (!Strings.isNullOrEmpty(fromParam))
                    try {
                        from = Integer.valueOf(fromParam);
                    } catch (NumberFormatException ignored) {
                    }

                res.status(200);
                if (!errors.isEmpty()) {
                    params.put("errors", errors.stream().collect(Collectors.joining("\n")));

                } else {
                    params.put("query", keyword);
                    params.put("category", category);
                    params.put("next", from+10);
                    if (from - 10 >= 0)
                        params.put("previous", from-10);

                    try (final ElasticSearchManager elasticSearchManager = new ElasticSearchManager()) {
                        final List<SearchResult> results = elasticSearchManager.categorySearch(category, keyword, ElasticIndex.analyzed, from,10);
                        params.put("results", results);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                halt(500);
                params.put("errors", "Ah! something went wrong, please try again.");
            }

            template.process(params, writer);
            return writer;
        });

        get("/relsearch", (req, res) -> {
            final String keyword = req.queryParams("q");
            final String relation = req.queryParams("r");
            final String fromParam = req.queryParams("_s");

            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/relsearch.ftl");
            Map<String, Object> params = new HashMap<>();

            try {
                List<String> errors = new ArrayList<>();

                if (Strings.isNullOrEmpty(keyword))
                    errors.add("no keyword is provided");

                if (Strings.isNullOrEmpty(relation))
                    errors.add("no relationship is provided");

                int from = 0;
                if (!Strings.isNullOrEmpty(fromParam))
                    try {
                        from = Integer.valueOf(fromParam);
                    } catch (NumberFormatException ignored) {
                    }

                res.status(200);
                if (!errors.isEmpty()) {
                    params.put("errors", errors.stream().collect(Collectors.joining("\n")));

                } else {
                    params.put("query", keyword);
                    params.put("relation", relation);
                    params.put("next", from+10);
                    if (from - 10 >= 0)
                        params.put("previous", from-10);

                    try (final ElasticSearchManager elasticSearchManager = new ElasticSearchManager()) {
                        final List<SearchResult> results = elasticSearchManager.relationSearch(keyword, relation, ElasticIndex.analyzed, from,10);
                        params.put("results", results);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                halt(500);
                params.put("errors", "Ah! something went wrong, please try again.");
            }

            template.process(params, writer);
            return writer;
        });
    }
}
