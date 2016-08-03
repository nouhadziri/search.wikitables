package edu.jhu.nlp.wikipedia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.wikipedia.rdf.Triple;
import ca.ualberta.wikipedia.rdf.GenerateRdf;
import ca.ualberta.wikipedia.tablereader.Cell;



/**
 * Data structures for a wikipedia page.
 *
 * @author Delip Rao
 */
public class WikiPage {

	public GenerateRdf rdf = new GenerateRdf();
    private String title = null;
    private WikiTextParser wikiTextParser = null;
    private String id = null;

    /**
     * Set the page title. This is not intended for direct use.
     *
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title.trim();
    }

    /**
     * Set the wiki text associated with this page.
     * This setter also introduces side effects. This is not intended for direct use.
     *
     * @param wtext wiki-formatted text
     */
    public void setWikiText(final String wtext) {
    	
    	//table parser does not account for extra white space 
    	//rm extra white space between new line and next char 
    	Pattern tooMuchSpace = Pattern.compile("\n\\s+");
    	Matcher matcher = tooMuchSpace.matcher(wtext);
    	String wikiText = wtext;
    	while (matcher.find()) {
			wikiText=matcher.replaceAll("\n");
		}
    	
        wikiTextParser = new WikiTextParser(wikiText);
    }

    /**
     * @return a string containing the page title.
     */
    public String getTitle() {
        return title;
    }
    
  

    private static Pattern disambCatPattern = Pattern.compile("\\(disambiguation\\)", Pattern.CASE_INSENSITIVE);

    /**
     * @return true if this a disambiguation page.
     */
    public boolean isDisambiguationPage() {
        return disambCatPattern.matcher(title).matches() || wikiTextParser.isDisambiguationPage();
    }

    /**
     * @return true for "special pages" -- like Category:, Wikipedia:, etc
     */
    public boolean isSpecialPage() {
        return title.indexOf(':') > 0;
    }
    
	public static boolean isSpecialTitle(String name) {
		if (name == null || name.isEmpty())
			return true;
		
		name = name.toLowerCase();
		if (name == null || name.isEmpty() ||
				name.startsWith("list of") || 
				name.startsWith("table of") ||
				name.startsWith("file:") ||
				name.startsWith("wikipedia:") ||
				name.startsWith("category:") ||
				name.startsWith("template:") ||
				name.startsWith("help:") ||
				name.startsWith("portal:") ||
				name.startsWith("mediaWiki:") ||
				name.startsWith("module:") ||
				name.startsWith("mos:") ||
				name.contains("#"))
			
			return true;
		
		return false;
	}

    /**
     * Use this method to get the wiki text associated with this page.
     * Useful for custom processing the wiki text.
     *
     * @return a string containing the wiki text.
     */
    public String getWikiText() {
        return wikiTextParser.getText();
    }

    /**
     * @return true if this is a redirection page
     */
    public boolean isRedirect() {
        return wikiTextParser.isRedirect();
    }

    /**
     * @return true if this is a stub page
     */
    public boolean isStub() {
        return wikiTextParser.isStub();
    }

    /**
     * @return the title of the page being redirected to.
     */
    public String getRedirectPage() {
        return wikiTextParser.getRedirectText();
    }

    /**
     * @return plain text stripped of all wiki formatting.
     */
    public String getText() {
        return wikiTextParser.getPlainText();
    }

    /**
     * @return a list of categories the page belongs to, null if this a redirection/disambiguation page
     */
    public HashSet<String> getCategories() {
        return wikiTextParser.getCategories();
    }

    public InfoBox getInfoBox() throws WikiTextParserException {
        return wikiTextParser.getInfoBox();
    }

    /**
     * @return a list of links contained in the page
     */
    public HashSet<String> getLinks() {
        return wikiTextParser.getLinks();
    }

	/**
	 * 
	 * @return a list of links with their position information <entity, pos>
	 */
	public Vector<Pair<String, Integer>> getLinkPos() {
		return wikiTextParser.getLinkPos();
	}
	
	
    
    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
    
    /**
     * Parsing tables
     * printing out tables
     */
    
    public void getAllMatrix()
    {
    	ArrayList<Cell[][]> matrixTables = new ArrayList<Cell[][]>();

		matrixTables = wikiTextParser.getAllMatrixFromTables();
		
		for (Cell[][] wikimatrix : matrixTables) {
			for (int i = 0; i < wikimatrix.length; i++) {
				for (int j = 0; j < wikimatrix[0].length; j++) {
					try {
						if (wikimatrix[i][j].getContent() == null) {
							continue;
						}
						System.out.print(wikimatrix[i][j].getContent() + " ");
					} catch (java.lang.NullPointerException e) {
						// System.out.print("");
					}
				}

				System.out.print("\n");

			}
		}

    }
	public int numtable=0;

    public void getRDFTriples() throws IOException, JSONException
    {
    	ArrayList<Cell[][]> matrixTables = new ArrayList<Cell[][]>();

		matrixTables = wikiTextParser.getAllMatrixFromTables();
		
		if (matrixTables.isEmpty())
		{
			System.out.println("There is non well-formed table to produce RDF triples");
		}
		else{
			
			//JSONObject returnObj = new JSONObject();
			File file = new File("/Users/nouhadziri/Desktop/toJson.json");
			Writer fileWriter = new FileWriter(file);
	    	JSONObject returnObj = new JSONObject();
	    	JSONObject tableJSON = new JSONObject();
			int numberColumns = 0;
			int numberRows = 0;
	    	
		for (Cell[][] matrix : matrixTables) {
			
			System.out.println("Unique column: "+rdf.checkuniqueValue(matrix, 0));
			
			System.out.println("Headers indexes"+rdf.getIndexAllHeaders(matrix));
			
			rdf.cleanUpMatrix(matrix);
			System.out.println("list of headers: "+rdf.getHeaders(matrix));
			System.out.println("This table contain a cast : "+rdf.checkCastMatrix(matrix));
		

			rdf.cleanUpMatrix(matrix);
	
			System.out.println("\n***Word Shape & Data type***\n");
		
			for (int j = 0; j < matrix[0].length; j++) {

				System.out.println("Data Type of column " + j + " : " + rdf.predicteColumnDataType(matrix, j));
				System.out.println("Word Shape of column " + j + " : " + rdf.predicteColumnShape1(matrix, j));
			}
			ArrayList<Triple<String, String, String>> listTriple=null;
			
			ArrayList<String> headers = new ArrayList<String>();
			
			 listTriple = rdf.produceRDF(matrix);
			 listTriple = rdf.produceRDF(matrix);
				rdf.cleanUpMatrix(matrix);
				numberColumns = matrix[0].length;
				numberRows = matrix.length;
				headers = rdf.getHeaders(matrix);
				numtable++;
				tableJSON.put("title", "mama mia");
				tableJSON.put("table#", "table id from wikipedia" );
				tableJSON.put("# rows", numberRows);
				tableJSON.put("# columns",numberColumns);
				tableJSON.put("list of headers ",headers);
				tableJSON.put("list of triples ",listTriple);
				returnObj.put("table"+numtable, tableJSON);
				
				tableJSON = new JSONObject();
			//createJsonFile(matrix,returnObj,numtable);
		}
		
		fileWriter.write(returnObj.toString(4));

	    fileWriter.close();
		System.out.println("\n ***Table structure*** \n");

		rdf.printOutMatrix(matrixTables);

		System.out.println("\n ***RDF Triples*** \n");
		rdf.printOutRDFTriple(matrixTables);
		
		
		System.out.println("\n***RDF triples for blank node***\n");
		rdf.printOutRDFTripleBlankNode(matrixTables);
		
		 
		
		}
    }
    
    public void createJsonFile(Cell[][] matrix,JSONObject returnObj,int numtable) throws JSONException, IOException
    {
    	JSONObject tableJSON = new JSONObject();
    
    	ArrayList<Triple<String, String, String>> listTriple=null;
		
		ArrayList<String> headers = new ArrayList<String>();
		
		 listTriple = rdf.produceRDF(matrix);
			rdf.cleanUpMatrix(matrix);
			int numberColumns = matrix[0].length;
			int numberRows = matrix.length;
			headers = rdf.getHeaders(matrix);
			numtable++;
			tableJSON.put("title", "mama mia");
			tableJSON.put("table#", "table id from wikipedia" );
			tableJSON.put("# rows", numberRows);
			tableJSON.put("# columns",numberColumns);
			tableJSON.put("list of headers ",headers);
			tableJSON.put("list of triples ",listTriple);
			returnObj.put("table"+numtable, tableJSON);
			
			tableJSON = new JSONObject();
			
			
    }
    
public int getCountColspan()
{
	return wikiTextParser.countColspan();
}

public int getCountRowspan()
{
	return wikiTextParser.countRowspan();
}

public int getCountMixRowandColumn()
{
	return wikiTextParser.countMixColspanAndRowspan();
}

public int getCountNestedTable()
{
	return wikiTextParser.countNestedtables();
}

public int getCountTable()
{
	return wikiTextParser.countTable();
}

public int getCountExceptions()
{
	return wikiTextParser.counthasException();
}

public int getCountMisuseException()
{
	return wikiTextParser.counthasMisuseException();
}

public int getCountHasNoHeader()
{
	return wikiTextParser.counthasNoHeader();
}

public int getCountCaption()
{
	return wikiTextParser.countCaption();
}

}