package ca.ualberta.elasticsearch;

import ca.ualberta.elasticsearch.index.AnalyzedElasticIndex;
import ca.ualberta.elasticsearch.index.ElasticIndex;
import org.junit.After;
import org.junit.Test;



public class TestQuery {
	
	
	private ElasticSearchManager manager = new ElasticSearchManager();
	private ElasticIndex anlyzedIndex = new AnalyzedElasticIndex();

	/**
	 * This method test a keyword query on one field, using the OR ranked boolean model
	 * A eviter
	 */
	//
    @Test
	public void testKeywordQuery(){
		//manager.getResponse("american football "); //match multiple keywords
//		manager.getResponse("list of Bond 007 movies");
//	manager.getResponse("Disney movies");
		//manager.keywordQuery("Paul Brown Stadium location");
		//manager.keywordQuery("0Francesco");
        System.out.println("******* keyword query *******");
		//manager.keywordQuery("sportspeople in tennis");
		//manager.keywordSearch("list of movies starring Sean Connery",ElasticIndex.analyzed,100 );
//		manager.keywordSearch("movies starring Sean Connery",ElasticIndex.notStemmed,100 );
//        manager.keywordSearch("musical movies tony award",ElasticIndex.analyzed,100 );
//        manager.keywordSearch("movies directed by Woody Allen",ElasticIndex.analyzed,100 );
//        manager.keywordSearch("United states professional sports teams",ElasticIndex.analyzed,100 );
//        manager.keywordSearch("computer games developed by Ubisoft",ElasticIndex.analyzed,100 );
//        manager.keywordSearch("computer games developed by Ubisoft",ElasticIndex.notStemmed,100 );
//        manager.keywordSearch("movies academy award nominations",ElasticIndex.notStemmed,100 );
        System.out.println(SearchResultUtil.toSummary(manager.keywordSearch("movies directed by Woody Allen",ElasticIndex.notLowercased,20 )));
   //manager.keywordSearch("grammy best album in 2012",ElasticIndex.notAnalyzed,100 );
      //(better than analyzed)  manager.keywordSearch("grammy best album in 2012",ElasticIndex.notStemmed,100 );
        System.out.println("*****************************");

		//manager.keywordQuery("Disney movies");
	}

  //  @Test
    public void testRelationSearch(){

	    manager.relationSearch("Paul Brown Stadium", "location", ElasticIndex.analyzed,30);
    }


	/**
	 * tester Dimanche : marche mais pour certain query pas de resultat
	 */
	//@Test
	public void testAdvancedQuery() {
		//manager.advancedQuery(ElasticIndex.analyzed, "metropolitan areas", "sports clubs");
		//manager.advancedQuery(ElasticIndex.analyzed, "", "Award");

	//manager.advancedQuery(ElasticIndex.analyzed,"american actor", "list of movies starring Sean Connery");
//	manager.categorySearch("Sportspeople", "tennis",ElasticIndex.analyzed,30);
	//manager.advancedQuery(ElasticIndex.analyzed,"Broadway musicals", "Grammy Award");
	//manager.advancedQuery(ElasticIndex.analyzed,"musical movies", "Tony Award");
	//manager.advancedQuery(ElasticIndex.analyzed,"Top level football leagues", "teams");
	//manager.advancedQuery(ElasticIndex.analyzed,"american movies", "directed by Woody Allen");
	//manager.advancedQuery(ElasticIndex.analyzed,"United states", "professional sports teams");
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
		//System.out.println("******* advanced query *******");
		//manager.advancedQuery("sportspeople", "tennis");
//		manager.advancedQuery(ElasticIndex.analyzed, "Broadway musicals", "Grammy Award");
        //System.out.println("*****************************");


		
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
