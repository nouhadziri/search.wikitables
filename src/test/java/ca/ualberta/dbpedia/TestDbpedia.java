package ca.ualberta.dbpedia;

import org.junit.Test;

import ca.ualberta.wikipedia.dbpedia.DbpediaManager;

public class TestDbpedia {
	
	public DbpediaManager manager = new DbpediaManager();
	
	
	//@Test
	public void testSparql(){
		
		manager.sparqlTest();
		
	}
	
	@Test
	public void testgetAbstractSparql(String pageTitle){
		
	System.out.println(manager.getAbstractSparql("Roger_Moore"));
	}

}
