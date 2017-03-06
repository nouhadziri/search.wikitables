package ca.ualberta.elasticsearch;

import org.junit.After;
import org.junit.Test;



public class TestQuery {
	
	
	ElasticSearchManager manager = new ElasticSearchManager();
	
	
	//@Test
	public void testKeywordQuery(){
		manager.getResponse("western province of Canada"); //match multiple keywords
		
	}
	
	//@Test
	public void testMultiSearch(){
		manager.getResponse("cast");
	}
	
	//@Test
	public void testAdvancedQuery() {
		manager.advancedQuery("tennis", "french");
	}
	
	//@Test
	public void testAdvancedSearchPhraseQuery(){
		
		manager.advancedSearchPhraseQuery("Tennis hall", "french open");
	}
	
	@Test
	public void testDis_maxQuery(){
		
		manager.Dis_maxQuery("western province of Canada");
		
	}
	
	//@Test
	public void testadvancedSearchTitleAndBody(){
		
		manager.advancedSearchTitleAndBody("List of international tennis player "," Andre Agassi" );
	}
	
	//@Test
	public void testadvancedSearchQuery3(){
		manager.advancedSearchQuery3("tennis", "Andre agassi", "score");
	}
	
	@After
	public void tearDown() {
		manager.close();
	}
}
