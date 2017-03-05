package ca.ualberta.elasticsearch;

import org.junit.After;
import org.junit.Test;



public class TestQuery {
	
	
	ElasticSearchManager manager = new ElasticSearchManager();
	
	
//	@Test
	public void testKeywordQuery(){
		manager.getResponse("Norman Reynolds"); //match multiple keywords
		
	}
	
	//@Test
	public void testMultiSearch(){
		manager.getResponse("cast");
	}
	
	//@Test
	public void testAdvancedQuery() {
		manager.advancedQuery("tennis", "french");
	}
	
	@Test
	public void testAdvancedSearchPhraseQuery(){
		
		manager.advancedSearchPhraseQuery("Tennis hall", "french open");
	}
	
	@After
	public void tearDown() {
		manager.close();
	}
}
