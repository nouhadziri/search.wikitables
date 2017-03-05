package ca.ualberta.wikipedia.dbpedia;

import com.hp.hpl.jena.query.*;


public class DbpediaManager {

	
	public void sparqlTest() {
		
		String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
				"PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
				"SELECT ?abstract ?type ?redirect"
				+ " WHERE {" +
				"dbr:Roger_Moore dbo:abstract ?abstract . " +
				"FILTER (lang(?abstract) = 'en') " +
				"dbr:Roger_Moore rdf:type ?type ." +
				"FILTER( REGEX(STR(?type), \"schema.org\") )"+
				"?redirect dbo:wikiPageRedirects dbr:Roger_Moore ."+
				"}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				//System.out.println(soln.get("type"));
				//System.out.println(soln.get("redirect"));
				System.out.println(soln.get("abstract"));

				//System.out.println(soln);
			}
		} finally {
			qexec.close();
		}

	}
	
	public static String getAbstractSparql(String pageTitle)
	{
		String pageAbstract="";;
		String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
				"SELECT ?abstract"
				+ " WHERE {" 
				+ "dbr:"+ pageTitle + " dbo:abstract ?abstract . " +
				"FILTER (lang(?abstract) = 'en') " +
				"}";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				pageAbstract=soln.get("abstract").toString();
				//System.out.println(soln.get("type"));
				//System.out.println(soln.get("redirect"));
				
				System.out.println(soln.get("abstract"));

				//System.out.println(soln);
			}
		} finally {
			qexec.close();
		}
		return pageAbstract;
	}
	
	public static String getTypeSparql(String wikid)
	{
		String typeCell="";;
		String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"SELECT ?type"
				+ " WHERE {" +
				" dbr:"+ wikid+ " rdf:type ?type ." +
				"FILTER( REGEX(STR(?type), \"schema.org\") )"+
				"}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				typeCell=soln.get("type").toString();
				//System.out.println(soln.get("type"));
				//System.out.println(soln.get("redirect"));
				
				System.out.println(soln.get("type"));

				//System.out.println(soln);
			}
		} finally {
			qexec.close();
		}
		return typeCell;
	}
	
	public static String getRedirectSparql(String wikid){
		
		String redirectPage= "";;
		String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>"+
				"SELECT ?redirect"
				+ " WHERE {" +
				"?redirect dbo:wikiPageRedirects dbr:"+wikid+" ."+
				"}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				redirectPage=soln.get("redirect").toString();
				//System.out.println(soln.get("type"));
				//System.out.println(soln.get("redirect"));
				
				System.out.println(soln.get("redirect"));

				//System.out.println(soln);
			}
		} finally {
			qexec.close();
		}
		
		return redirectPage;
	}
	
	
	
	
	
	
	
	public static void main(String[] arg)
	{
		//getAbstractSparql("Roger_Moore");
		//getTypeSparql("Roger_Moore");
		getRedirectSparql("Roger_Moore");
		
		//sparqlTest();
	}
}
