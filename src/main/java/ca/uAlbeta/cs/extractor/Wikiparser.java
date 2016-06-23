package ca.uAlbeta.cs.extractor;


import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;

public class Wikiparser implements PageCallbackHandler {
	
	public Wikiparser(){
		
	}
	
	
	public void process(WikiPage page) {
		if (page == null) return;
		String title = page.getTitle().trim();
		
		try {
			//ignore special pages, stub pages, redirect pages, and disambiguation pages.
			if (page.isSpecialPage() ||
				page.isStub() || 
				page.isRedirect() ||
				page.isDisambiguationPage())
				
				return;
			
			//entity page.
			String content = page.getText();
			if ( title == null || title.isEmpty() || content == null || content.isEmpty() )
				return;
			
		
			String wkstrID = page.getID().trim();
		//	System.out.println(wkstrID);
			Long wikiID = Long.parseLong( wkstrID );
			
		//	System.out.println("Here is the title : "+page.getTitle());
			
			//System.out.println("Here is the infobox : "+page.getInfoBox());
			
			//System.out.println(page.getWikiText());
		//	System.out.println("=============");
			
			//here I have to add page.parseTable where parseTable method will be on 
		  // System.out.println(page.getWikiTable());
		//	HashSet<String> tables=page.getWikiTable();
		
			
			page.getAllMatrix();
			System.out.println("\n");
			System.out.println("***** Statistics about wiki ID: "+page.getID());
			System.out.println("Number of tables  : "+page.getCountTable());
			System.out.println("Number of tables having colspan attribute : "+page.getCountColspan());
			System.out.println("Number of tables having rowspan attribute : "+page.getCountRowspan());
			System.out.println("Number of tables having mix rowspan and colspan attribute : "+page.getCountMixRowandColumn());
			System.out.println("Number of tables having nested tables : "+page.getCountNestedTable());
			System.out.println("Number of tables having exception : "+page.getCountExceptions());
			
			
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void indexWikipedia(String wikiFile) {

		try {
			WikiXMLSAXParser.parseWikipediaDump(wikiFile, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("DONE!!!!!!!1");
	  
		
	}
	
	public static void main(String[] args) {
		Wikiparser indexer = new Wikiparser();
		try {
			//indexer.indexWikipedia(args[0]);
			indexer.indexWikipedia("/Users/Nouha/Desktop/test.xml");
			//indexer.indexWikipedia("/Users/Nouha/Desktop/infobox.xml");
		} catch (Exception e) {
			
			System.exit(1);
		}
	}


	
	
	
	

}
