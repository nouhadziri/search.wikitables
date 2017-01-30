package ca.uAlbeta.cs.extractor;

import java.util.HashSet;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;

public class Wikiparser implements PageCallbackHandler {

	public Wikiparser() {

	}

	public void process(WikiPage page) {
		if (page == null)
			return;
		String title = page.getTitle().trim();

		try {
			// ignore special pages, stub pages, redirect pages, and
			// disambiguation pages.
			if (page.isSpecialPage() || page.isStub() || page.isRedirect() || page.isDisambiguationPage())

				return;

			// entity page.
			String content = page.getText();
			if (title == null || title.isEmpty() || content == null || content.isEmpty())
				return;

			String wkstrID = page.getID().trim();
			
			HashSet<String> categories = page.getCategories();
			
			for (String categorie : categories) {
			    System.out.println("categorie: "+categorie );
			}
			
			Long wikiID = Long.parseLong(wkstrID);

			
			System.out.println("\n");

			System.out.println("***** Statistics about wiki ID: " + page.getID());
			System.out.println("Title of the article : " + page.getTitle());
			System.out.println("Number of tables  : " + page.getCountTable());
			System.out.println("Number of tables having no header : " + page.getCountHasNoHeader());
			System.out.println("Number of tables having caption : " + page.getCountCaption());
			System.out.println("Number of tables having colspan attribute : " + page.getCountColspan());
			System.out.println("Number of tables having rowspan attribute : " + page.getCountRowspan());
			System.out.println(
					"Number of tables having mix rowspan and colspan attribute : " + page.getCountMixRowandColumn());
			System.out.println("Number of tables having nested tables : " + page.getCountNestedTable());
			System.out.println("Number of tables having exception : " + page.getCountExceptions());
			System.out.println(
					"Number of tables having misuse of wikimarkup specification : " + page.getCountMisuseException());

			System.out.println("***** End Statistics***");

			page.getRDFTriples();
			page.createJsonFromArticle();

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
			indexer.indexWikipedia("/Users/nouhadziri/Desktop/test-2.xml");
		} catch (Exception e) {

			System.exit(1);
		}
	}

}
