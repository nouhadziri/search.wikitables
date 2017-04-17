package ca.ualberta.wikipedia.dbpedia;

import java.util.*;
import java.util.stream.Collectors;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class DbpediaManager {


    //	private static final String SPARQL_ENDPOINT = "http://206.12.96.184:8890/sparql";
    private static final String SPARQL_ENDPOINT = "http://dbpedia.org/sparql";

    private static String getDbpediaResource(String wikiId) {
        final String prefix = wikiId.contains("\"") ? "'" : "<";
        final String suffix = wikiId.contains("\"") ? "'" : ">";
        return prefix + "http://dbpedia.org/resource/" + wikiId + suffix;
    }

    public void sparqlTest() {

        String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
                "SELECT ?abstract ?type ?redirect"
                + " WHERE {" +
                "dbr:Roger_Moore dbo:abstract ?abstract . " +
                "FILTER (lang(?abstract) = 'en') " +
                "dbr:Roger_Moore rdf:type ?type ." +
                "FILTER( REGEX(STR(?type), \"schema.org\") )" +
                "?redirect dbo:wikiPageRedirects dbr:Roger_Moore ." +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();

                System.out.println(soln.get("abstract"));

            }
        } finally {
            qexec.close();
        }

    }

    public static String getPredicate(String subject, String object) {
        String predicate = null;
        String queryString = "SELECT ?rel WHERE { " + getDbpediaResource(subject) + " ?rel " + getDbpediaResource(object) + " }";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String relationship = soln.get("rel").toString();

                if (relationship.contains("dbpedia.org/ontology/") && !relationship.contains("wikiPageWikiLink") && !relationship.contains("wikiPageRedirects")) {
                    predicate = relationship.replaceAll("http://dbpedia.org/ontology/", "");
                }
            }
        } finally {
            qexec.close();
        }

        return predicate;
    }

    public static Map<String, Set<String>> getPredicates(String subject, List<String> objects) {
        final Map<String, Set<String>> result = new HashMap<>();

        String selectClause = "SELECT DISTINCT ";
        String whereClause = "WHERE { ";
        String endClause = "}";

        Map<String, Integer> parameters = new HashMap<>();
        int i = 0;
        List<String> queryPredicates = new ArrayList<>();
        List<String> queryColumns = new ArrayList<>();
        for (String object : objects) {
            queryPredicates.add("{ " + getDbpediaResource(subject) + " ?p" + i + " " + getDbpediaResource(object) + " }");
            queryColumns.add("?p" + i);
            parameters.put(object, i++);
        }

        String queryString = queryColumns.stream().collect(Collectors.joining(" ", selectClause, "\n")) +
                queryPredicates.stream().collect(Collectors.joining(" UNION ", whereClause, endClause));

        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
            try {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    for (Map.Entry<String, Integer> parameterEntry : parameters.entrySet()) {
                        final RDFNode rdfNode = soln.get("?p" + parameterEntry.getValue());
                        if (rdfNode == null)
                            continue;

                        String predicate = rdfNode.toString();
                        if (predicate != null) {
                            if (!predicate.startsWith("http://www.w3.org") && !predicate.contains("wikiPageWikiLink") && !predicate.contains("wikiPageRedirects")) {
                                Set<String> predicateResults = result.get(parameterEntry.getKey());
                                if (predicateResults == null)
                                    result.put(parameterEntry.getKey(), predicateResults = new HashSet<>());

                                predicateResults.add(predicate.replaceAll("http://dbpedia.org/\\w+/", ""));
                                break;
                            }
                        }

                    }
                }
            } finally {
                qexec.close();
            }
        } catch (Exception e) {
            System.err.println("unable to retrieve predicate {" + subject + "} due to " + e.getMessage());
        }

        return result;
    }

    public static String getAbstractSparql(String pageTitle) {
        String pageAbstract = "";

        String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "SELECT ?abstract\n" +
                "WHERE {\n" +
                getDbpediaResource(pageTitle) + " dbo:abstract ?abstract .\n" +
                "FILTER (lang(?abstract) = 'en')\n" +
                "}";

        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
            try {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    pageAbstract = soln.get("abstract").toString();
                }
            } finally {
                qexec.close();
            }
        } catch (Exception e) {
            System.err.println("unable to retrieve abstract {" + pageTitle + "} due to " + e.getMessage());
        }

        return pageAbstract;
    }

    public static String getTypeSparql(String pageTitle) {
        String ontology = null, schema = null;

        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "SELECT ?type"
                + " WHERE { " +
                getDbpediaResource(pageTitle.replaceAll("\\s+", "_")) + " rdf:type ?type . " +
                "FILTER( REGEX(STR(?type), \"schema.org\") || REGEX(STR(?type), \"dbpedia.org/ontology\"))" +
                "}";

        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
            try {
                ResultSet results = qexec.execSelect();

                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    String typeCell = soln.get("type").toString();

                    if (typeCell.contains("dbpedia.org/ontology/"))
                        ontology = typeCell.replaceAll("http://dbpedia.org/ontology/", "");
                    else if (typeCell.contains("schema.org/"))
                        schema = typeCell.replaceAll("http://schema.org/", "");


                }
            } finally {
                qexec.close();
            }
        } catch (Exception e) {
            System.err.println("unable to retrieve abstract {" + pageTitle + "} due to " + e.getMessage());
        }


        return schema != null ? schema : ontology;
    }

    public static ArrayList<String> getRedirectSparql(String wikid) {

        ArrayList<String> listRedirects = new ArrayList<String>();
        String redirectPage = "";
        ;
        String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                "SELECT ?redirect"
                + " WHERE {" +
                "?redirect dbo:wikiPageRedirects <http://dbpedia.org/resource/" + wikid + "> ." +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                redirectPage = soln.get("redirect").toString();
                redirectPage = redirectPage.replaceAll("http://dbpedia.org/resource/", "");
                redirectPage = redirectPage.replaceAll("_", " ");
                listRedirects.add(redirectPage);
                //System.out.println(redirectPage);

            }
        } finally {
            qexec.close();
        }

        return listRedirects;
    }

    public ArrayList<String> getCategorySparql(String wikiId) {

        ArrayList<String> listCatecories = new ArrayList<String>();
        String category = "";
        ;
        String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                "PREFIX dct: <http://purl.org/dc/terms/>" +
                "SELECT ?category"
                + " WHERE {" +
                "dbr:" + wikiId + " dct:subject ?category ." +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                category = soln.get("category").toString();
                listCatecories.add(category);

                //System.out.println(soln.get("category"));
            }
        } finally {
            qexec.close();
        }

        return listCatecories;
    }


    public static void main(String[] arg) {
        //String title="Mamma_Mia!";
        //title = title.replaceAll("\\!", "\\!");
        //System.out.println(title);
        System.out.println(getAbstractSparql("Curtains_(musical)"));
        System.out.println(getAbstractSparql("Bellingham,_Washington"));
        System.out.println(getAbstractSparql("AFI's_100_Years...100_Movies_(10th_Anniversary_Edition)"));

        //getTypeSparql("James_Bond");
//		getRedirectSparql("Roger_Moore");
        //getCategorySparql("James_Bond");

        //sparqlTest();
    }

}
