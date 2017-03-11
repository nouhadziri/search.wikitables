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
	
	public static String getPredicate(String subject, String object) {
		String predicate = null;
		String queryString = "SELECT ?rel WHERE { <http://dbpedia.org/resource/" + subject + "> ?rel <http://dbpedia.org/resource/" + object + "> }";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://206.12.96.184:8890/sparql", query);
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
	
	public static String getAbstractSparql(String pageTitle) {
		String pageAbstract="";
		String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
		"SELECT ?abstract"
		+ " WHERE {" 
		+ "<http://dbpedia.org/resource/"+ pageTitle + "> dbo:abstract ?abstract . " +
		"FILTER (lang(?abstract) = 'en') " +
		"}";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://206.12.96.184:8890/sparql", query);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				pageAbstract=soln.get("abstract").toString();
			}
		} finally {
			qexec.close();
		}
		return pageAbstract;
	}
	
	public static String getTypeSparql(String wikiId) {
		String ontology = null, schema = null;
		
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"SELECT ?type"
				+ " WHERE {" +
				" <http://dbpedia.org/resource/"+ wikiId.replaceAll("\\s+", "_") + "> rdf:type ?type ." +
				"FILTER( REGEX(STR(?type), \"schema.org\") || REGEX(STR(?type), \"dbpedia.org/ontology\"))"+
				"}";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://206.12.96.184:8890/sparql", query);
		try {
			ResultSet results = qexec.execSelect();

			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String typeCell=soln.get("type").toString();
				
				if (typeCell.contains("dbpedia.org/ontology/"))
					ontology = typeCell.replaceAll("http://dbpedia.org/ontology/", "");
				else if (typeCell.contains("schema.org/"))
					schema = typeCell.replaceAll("http://schema.org/", "");
				
		


			}
		} finally {
			qexec.close();
		}
		
	
		return schema != null ? schema : ontology;
	}
	
	public static ArrayList<String> getRedirectSparql(String wikid){
		
		ArrayList<String> listRedirects = new ArrayList<String>();
		String redirectPage= "";;
		String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>"+
				"SELECT ?redirect"
				+ " WHERE {" +
				"?redirect dbo:wikiPageRedirects <http://dbpedia.org/resource/"+wikid+"> ."+
				"}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://206.12.96.184:8890/sparql", query);
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
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://206.12.96.184:8890/sparql", query);
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
		System.out.println(getAbstractSparql("Curtains_(musical)"));
		System.out.println(getAbstractSparql("Bellingham,_Washington"));
		System.out.println(getAbstractSparql("AFI's_100_Years...100_Movies_(10th_Anniversary_Edition)"));
		
		//getTypeSparql("James_Bond");
//		getRedirectSparql("Roger_Moore");
	//getCategorySparql("James_Bond");
		
		//sparqlTest();
	}
	
}
