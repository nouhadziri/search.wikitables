package ca.ualberta.dbpedia;

import org.junit.Test;

import ca.ualberta.wikipedia.dbpedia.DbpediaManager;

public class TestDbpedia {
	
	public DbpediaManager manager = new DbpediaManager();
	
	
	//@Test
	public void testSparql(){
		
		manager.sparqlTest();
		
	}
	
	//@Test
	public void testgetAbstractSparql(String pageTitle){
		
	System.out.println(manager.getAbstractSparql("Roger_Moore"));
	}

	//@Test
	public void testgetTypeSparql(String wikid){
		
	//manager.getTypeSparql("Roger_Moore");
	}
	
	@Test
	public void testgetRedirectSparql(String wikid ){

		System.out.println(manager.getRedirectSparql("Roger_Moore"));
	}
	
	//@Test
	public void testgetCategorySparql(String wikid ){

		System.out.println(manager.getCategorySparql("Roger_Moore"));
	}
	
}
