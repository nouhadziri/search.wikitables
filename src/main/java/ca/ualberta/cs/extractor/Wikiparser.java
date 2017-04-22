package ca.ualberta.cs.extractor;

import ca.ualberta.elasticsearch.ElasticSearchManager;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLSAXParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;

public class Wikiparser implements PageCallbackHandler {

    private Writer fileWriter;
    private final ElasticSearchManager manager = new ElasticSearchManager();

    private final IntSummaryStatistics numberOfColumns = new IntSummaryStatistics();
    private final IntSummaryStatistics numberOfTables = new IntSummaryStatistics();
    private final IntSummaryStatistics numberOfExtractedHeaderTypes = new IntSummaryStatistics();
    private final IntSummaryStatistics numberOfExtractedRelationships = new IntSummaryStatistics();


    public Wikiparser(boolean isCreateSchema) throws IOException {
        File file = new File("toJsonNew.json");
        fileWriter = new FileWriter(file);
        if (isCreateSchema) {
            manager.createSchema();
            manager.createAllIndices();
        }
    }

    public Wikiparser() throws IOException {
        this(true);
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
                System.out.println("categorie: " + categorie);
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

            System.out.println("*** End Statistics***");

            //page.getRDFTriples();
//			manager.saveDocumentsOnNonTokenizedIndex(page.createJsonFromArticle(fileWriter));
            final List<String> jsonArticle = page.createJsonFromArticle(fileWriter);
//            manager.saveTables(jsonArticle);
            manager.saveDocumentsOnAllIndices(jsonArticle);
            numberOfTables.accept(page.numtable);
            numberOfColumns.accept(page.getNumberOfColumns());
            numberOfExtractedHeaderTypes.accept(page.getNumberOfExtractedHeaderTypes());
            numberOfExtractedRelationships.accept(page.getNumberOfExtractedRelationships());
//			page.createJsonFromArticle(fileWriter);

            //System.out.println("wikilink: "+page.CheckWikid("[[Siobhán|  McCarthy]]"));

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

        System.out.println("-------------------------");
        System.out.printf("number of tables - total: %d  avg per page: %.3f\n", numberOfTables.getSum(), numberOfTables.getAverage());
        System.out.printf("number of columns - total: %d  avg per page: %.3f\n", numberOfColumns.getSum(), numberOfColumns.getAverage());
        System.out.printf("number of header types - total: %d  avg per page: %.3f\n", numberOfExtractedHeaderTypes.getSum(), numberOfExtractedHeaderTypes.getAverage());
        System.out.printf("number of relationships - total: %d  avg per page: %.3f\n", numberOfExtractedRelationships.getSum(), numberOfExtractedRelationships.getAverage());
        System.out.println();

        System.out.println("DONE!!!!!!!1");

    }

    public void close() throws IOException {
        manager.close();
        fileWriter.close();
    }

    public static void main(String[] args) {
        try {
            Wikiparser indexer = new Wikiparser(true);
            indexer.indexWikipedia("data/pages.xml");
			indexer.indexWikipedia("data/additional_pages.xml");
			indexer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
