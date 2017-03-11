package ca.ualberta.elasticsearch;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ElasticSearchManager testelas = new ElasticSearchManager();
		
		
		//testelas.advancedSearchTitleAndBody("tennis"," Andre");
		//testelas.advancedQuery("tennis", "Andre");
		testelas.multiMatchSearch("american football");
	}

}
