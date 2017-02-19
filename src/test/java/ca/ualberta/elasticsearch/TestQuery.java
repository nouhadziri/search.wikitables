package ca.ualberta.elasticsearch;

import org.junit.Test;

public class TestQuery {
	
	
	ElasticSearchManager manager = new ElasticSearchManager();
	
	@Test
	public void testKeywordQuery(){
		
		manager.getResponse("nouha");
		
	}
	

}
