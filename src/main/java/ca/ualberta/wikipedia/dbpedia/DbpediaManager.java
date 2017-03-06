package ca.ualberta.wikipedia.dbpedia;

import java.util.ArrayList;

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
			
				System.out.println(soln.get("abstract"));

			}
		} finally {
			qexec.close();
		}

	}
	
	public static String getAbstractSparql(String pageTitle)
	{
		String pageAbstract="";
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
			
				System.out.println(soln.get("abstract"));

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
		
				System.out.println(soln.get("type"));

			}
		} finally {
			qexec.close();
		}
		return typeCell;
	}
	
	public static ArrayList<String> getRedirectSparql(String wikid){
		
		ArrayList<String> listRedirects = new ArrayList<String>();
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
	
	public  ArrayList<String> getCategorySparql(String wikid){
		
		ArrayList<String> listCatecories = new ArrayList<String>();
		String category= "";;
		String queryString = "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
				"PREFIX dct: <http://purl.org/dc/terms/>"+
				"SELECT ?category"
				+ " WHERE {" +
				"dbr:"+ wikid+" dct:subject ?category ."+
				"}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				category=soln.get("category").toString();
				listCatecories.add(category);
			
				//System.out.println(soln.get("category"));
			}
		} finally {
			qexec.close();
		}
		
		return listCatecories;
	}
	
	
	public static void main(String[] arg)
	{
		//String title="Mamma_Mia!";
		 //title = title.replaceAll("\\!", "\\!");
		 //System.out.println(title);
		//getAbstractSparql("James_Bond");
		
		//getTypeSparql("James_Bond");
		getRedirectSparql("Roger_Moore");
	//getCategorySparql("James_Bond");
		
		//sparqlTest();
	}
	
}
