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

import static spark.Spark.get;
import static spark.Spark.halt;

/**
 * Created by nouhadziri on 2017-04-21.
 */
public class WikitablesServer {
    public static void main(String[] args) {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(SearchResponse.class, "/");



        get("/", (req, res) -> {
            StringWriter writer = new StringWriter();
            final Template template = configuration.getTemplate("templates/search.ftl");
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
                    try (final ElasticSearchManager elasticSearchManager = new ElasticSearchManager()) {
                        final List<SearchResult> results = elasticSearchManager.keywordSearch(keyword, ElasticIndex.analyzed, from,10);
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
                if (!errors.isEmpty())
                    return SearchResponse.failed(errors);

                try (final ElasticSearchManager elasticSearchManager = new ElasticSearchManager()) {
                    final List<SearchResult> results = elasticSearchManager.categorySearch(keyword, category, ElasticIndex.analyzed, from, 10);
                    return SearchResponse.successful(results);
                }

            } catch (Exception e) {
                e.printStackTrace();
                res.status(403);
                return SearchResponse.failed("Oops! something went wrong, please try again!");
            }
        });

        get("/relsearch", (req, res) -> {
            final String keyword = req.queryParams("q");
            final String relation = req.queryParams("r");
            final String fromParam = req.queryParams("_s");

            try {
                List<String> errors = new ArrayList<>();

                if (Strings.isNullOrEmpty(keyword))
                    errors.add("no keyword is provided");

                if (Strings.isNullOrEmpty(relation))
                    errors.add("no relation is provided");

                int from = 0;
                if (!Strings.isNullOrEmpty(fromParam))
                    try {
                        from = Integer.valueOf(fromParam);
                    } catch (NumberFormatException ignored) {
                    }

                res.status(200);
                if (!errors.isEmpty())
                    return SearchResponse.failed(errors);

                try (final ElasticSearchManager elasticSearchManager = new ElasticSearchManager()) {
                    final List<SearchResult> results = elasticSearchManager.relationSearch(keyword, relation, ElasticIndex.analyzed, from, 10);
                    return SearchResponse.successful(results);
                }

            } catch (Exception e) {
                e.printStackTrace();
                res.status(403);
                return SearchResponse.failed("Oops! something went wrong, please try again!");
            }
        });
    }
}
