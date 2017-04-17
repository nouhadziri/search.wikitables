package ca.ualberta.elasticsearch;

import ca.ualberta.elasticsearch.index.ElasticIndex;
import org.junit.After;
import org.junit.Test;



public class TestQuery {
	
	
	private ElasticSearchManager manager = new ElasticSearchManager();
	
	
	/**
	 * This method test a keyword query on one field, using the OR ranked boolean model
	 * A eviter
	 */
	@Test
	public void testKeywordQuery(){
		//manager.getResponse("american football "); //match multiple keywords
//		manager.getResponse("list of Bond 007 movies");
//		manager.getResponse("Disney movies");
		//manager.keywordQuery("Paul Brown Stadium location");
		//manager.keywordQuery("Francesco");
        System.out.println("******* keyword query *******");
//		manager.keywordQuery("sportspeople in tennis");
//		manager.keywordQuery(ElasticIndex.analyzed, "Broadway musicals Grammy Award");
        System.out.println("*****************************");

		//manager.keywordQuery("Disney movies");
	}


	/**
	 * tester Dimanche : marche mais pour certain query pas de resultat
	 */
	@Test
	public void testAdvancedQuery() {
		
		//manager.advancedQuery("american actor", "list of movies starring Sean Connery"); 
		//manager.advancedQuery("musical movies", "tony award"); 
		//manager.advancedQuery("grammy", "best album in 2012"); 
		//manager.advancedQuery("american movies", "directed by Woody Allen"); 
		//Canceled manager.advancedQuery("tennis", "international players"); 
		// canceled manager.advancedQuery("tennis", "french open"); 
		// canceled manager.advancedQuery("film movie", "1933"); 
//		manager.advancedQuery("United states", "professional sports teams  ");
//		 manager.advancedQuery("computer games", "developed by Ubisoft");
//		manager.advancedQuery("movies", "academy award nominations");
        //manager.advancedQuery("movies", "starring Dustin Hoffman");
       // manager.advancedQuery("movies", "best costume design");
      //  manager.advancedQuery("concert tours", "england");
      //  manager.advancedQuery("sport", "Francesco");
		System.out.println("******* advanced query *******");
		manager.advancedQuery("sportspeople", "tennis");
//		manager.advancedQuery(ElasticIndex.analyzed, "Broadway musicals", "Grammy Award");
        System.out.println("*****************************");


		
		//manager.advancedQuery("football", "italian championship");
		//manager.advancedQuery("american basketball", "team");
		 //manager.advancedQuery("Goya Award", "Winner and nominees from FRA");
		//manager.advancedQuery("films", "american comedy ");
		//manager.advancedQuery("films", "american Adventure directed by James P. Hogan");
		//manager.advancedQuery("NCAA Division", "american universities in Arkansas");
		//manager.advancedQuery("Academy award", "winners and nominees in acting in 2011");
		//manager.advancedQuery("canadian soccer", "Alain Rochat position ");

	}
	
	//@Test
	public void testAdvancedSearchPhraseQuery(){
		
//		manager.advancedSearchPhraseQuery(ElasticIndex.analyzed, "International Tennis ", "Andrés Gómez");
	}
	
	//@Test
	public void testDis_maxQuery(){
		
		//manager.Dis_maxQuery("James Bond novels movies");
		//manager.Dis_maxQuery("James Bond");
		//manager.Dis_maxQuery("american football");
		
		//manager.Dis_maxQuery("mama mia american movie");
		//manager.Dis_maxQuery("Sean Connery");
	//	manager.Dis_maxQuery("italian clubs");
		//manager.Dis_maxQuery("crime movies");
		//manager.keywordQuery("Basketball center players with award");
		//manager.keywordQuery("Adeliza of Louvain spouse");
//		manager.keywordQuery(ElasticIndex.analyzed, "sean connery");
		
	    //manager.Dis_maxQuery(" Adel 21"); catastrophe, il faut toujours specifier la categorie
		
	}
	
	/**
	 * I should not search by title, because we don't expect users to know the title
	 */
	
	//@Test
	public void testadvancedSearchTitleAndBody(){
		
//		manager.advancedSearchTitleAndBody(ElasticIndex.analyzed, "List of international tennis player "," Andre Agassi" );
	}
	
	//@Test
	public void testadvancedSearchQuery3(){
//		manager.advancedSearchQuery3(ElasticIndex.analyzed, "novels", "James", "american actor");
	}
	
	//@Test 
	void testmultiMatchSearch(){
		
//		manager.multiMatchSearch(ElasticIndex.analyzed, "alberta");
	}
	
	
//	@Test
	public void testAdvancedSearchQueryRelationship(){
//		manager.advancedSearchQueryRelationship(ElasticIndex.analyzed, "Belmont Bruins", "city");
	}
	
	@After
	public void tearDown() {
		manager.close();
	}
}
