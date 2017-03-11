package ca.ualberta.elasticsearch;

import org.junit.After;
import org.junit.Test;



public class TestQuery {
	
	
	ElasticSearchManager manager = new ElasticSearchManager();
	
	
	//@Test
	public void testgetResponse(){
		
		manager.getResponse("tennis pl");
	}
	
	//@Test
	public void testKeywordQuery(){
		//manager.getResponse("american football "); //match multiple keywords
		manager.getResponse("list of Bond 007 movies");
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
		
		manager.advancedSearchPhraseQuery("International Tennis ", "Andrés Gómez");
	}
	
	@Test
	public void testDis_maxQuery(){
		
		manager.Dis_maxQuery("list of Bond 007 movies");
		
	}
	
	//@Test
	public void testadvancedSearchTitleAndBody(){
		
		manager.advancedSearchTitleAndBody("List of international tennis player "," Andre Agassi" );
	}
	
	//@Test
	public void testadvancedSearchQuery3(){
		manager.advancedSearchQuery3("novels", "James", "american actor");
	}
	
	//@Test 
	void testmultiMatchSearch(){
		
		manager.multiMatchSearch("alberta");
	}
	
	@After
	public void tearDown() {
		manager.close();
	}
}
