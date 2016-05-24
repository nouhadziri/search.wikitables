package ca.uAlbeta.cs.extractor;

import java.util.HashSet;
import java.util.Iterator;

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
			System.out.println(wkstrID);
			Long wikiID = Long.parseLong( wkstrID );
			
			System.out.println("Here is the title : "+page.getTitle());
			
			//System.out.println(page.getWikiText());
			System.out.println("=============");
			//here I have to add page.parseTable where parseTable method will be on 
		    System.out.println(page.getWikiTable());
			HashSet<String> tables=page.getWikiTable();
			
			//System.out.println(page.getText());
			
			Iterator<String> itr = tables.iterator();
			while(itr.hasNext()){
	          // page.parseHeaders(itr.next());
	           page.parseRows(itr.next());
	        }
		
			
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
			indexer.indexWikipedia("/Users/Nouha/Desktop/enwiki_list_of_cities_in_canada.xml");
		
		} catch (Exception e) {
			
			System.exit(1);
		}
	}
	
	
	

}
