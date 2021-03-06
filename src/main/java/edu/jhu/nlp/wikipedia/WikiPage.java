package edu.jhu.nlp.wikipedia;

import ca.ualberta.wikipedia.dbpedia.DbpediaManager;
import ca.ualberta.wikipedia.rdf.GenerateRdf;
import ca.ualberta.wikipedia.rdf.Triple;
import ca.ualberta.wikipedia.tablereader.Cell;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	private static Pattern disambCatPattern = Pattern.compile("\\(disambiguation\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern upperCasePattern = Pattern.compile("[A-Z]");

	private int numberOfColumns = 0;
	private int numberOfExtractedHeaderTypes = 0;
	private int numberOfExtractedRelationships = 0;

	/**
	 * Set the page title. This is not intended for direct use.
	 *
	 * @param title
	 */
	public void setTitle(final String title) {
		this.title = title.trim();
	}

	/**
	 * Set the wiki text associated with this page. This setter also introduces
	 * side effects. This is not intended for direct use.
	 *
	 * @param wtext
	 *            wiki-formatted text
	 */
	public void setWikiText(final String wtext) {

		// table parser does not account for extra white space
		// remove extra white space between new line and next char
		Pattern tooMuchSpace = Pattern.compile("\n\\s+");
		Matcher matcher = tooMuchSpace.matcher(wtext);
		String wikiText = wtext;
		while (matcher.find()) {
			wikiText = matcher.replaceAll("\n");
		}

		wikiTextParser = new WikiTextParser(wikiText);
	}

	/**
	 * @return a string containing the page title.
	 */
	public String getTitle() {
		return title;
	}

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
		if (name == null || name.isEmpty() || name.startsWith("list of") || name.startsWith("table of")
				|| name.startsWith("file:") || name.startsWith("wikipedia:") || name.startsWith("category:")
				|| name.startsWith("template:") || name.startsWith("help:") || name.startsWith("portal:")
				|| name.startsWith("mediaWiki:") || name.startsWith("module:") || name.startsWith("mos:")
				|| name.contains("#"))

			return true;

		return false;
	}

	/**
	 * Use this method to get the wiki text associated with this page. Useful
	 * for custom processing the wiki text.
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
	 * @return a list of categories the page belongs to, null if this a
	 *         redirection/disambiguation page
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
	 * Parsing tables printing out tables
	 */

	public void getAllMatrix() {
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

					}
				}

				System.out.print("\n");

			}
		}

	}

	public int numtable = 0;

	public ArrayList<String> getMatrixRow(Cell[][] matrix, int i) {
		ArrayList<String> rows = new ArrayList<String>();

		for (int j = 0; j < matrix[0].length; j++) {
			rows.add(matrix[i][j].getContent());
		}

		return rows;
	}

	public String regexReplaceWhiteSpace(String subject) {
		Pattern checkRegex = Pattern.compile("\\s+", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher regexMatcher = checkRegex.matcher(subject);
		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				subject = regexMatcher.replaceAll("_");
			}
		}
		return subject;
	}

	/**
	 * store all wikilinks of each cell in the table, so we can afterwards get
	 * their wikiID and look them up in DBpedia: I probably don't need this one,
	 * I will write a program : that takes a matrix and for each cell check if
	 * there is a wikilink if yes, go and get the wikiId of that entity and
	 * query DBpedia : Not the good one
	 */
	/*
	 * public ArrayList<String> storeWikiLink(Cell[][] matrix) {
	 * ArrayList<String> wikilinks = new ArrayList<String>(); for(int i=0;
	 * i<matrix.length;i++) { for(int j=0;j<matrix[0].length;i++) {
	 * if(!CheckWikid(matrix[i][j].getContent())) { continue; }
	 * 
	 * else{
	 * 
	 * 
	 * } } }
	 * 
	 * return wikilinks; }
	 */

	/**
	 * create a Json file from each article that contains tables First, it
	 * cleans matrix (without tokenization), keeping the wikid of each cell.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */

	int counter = 0;

	public List<String> createJsonFromArticle(Writer fileWriter) throws IOException, JSONException {
		ArrayList<Cell[][]> matrixTables = wikiTextParser.getAllMatrixFromTables();

		ArrayList<String> tables = new ArrayList<>();

		if (matrixTables.isEmpty()) {
			System.out.println("There is non well-formed table to produce RDF triples");
		} else {

			JSONArray returnObj = new JSONArray();

			final String articleId = getID().trim();

			counter++;

			int numberColumns = 0;
			int numberRows = 0;

			for (Cell[][] matrix : matrixTables) {

				JSONObject tableJSON = new JSONObject();
				JSONObject wordShapeJson = new JSONObject();
				JSONObject dataTypeJson = new JSONObject();

				// rdf.cleanUpMatrix(matrix);

				numberColumns = matrix[0].length;
                this.numberOfColumns += numberColumns;
				numberRows = matrix.length;
                ArrayList<String> headers = rdf.getHeaders(matrix);
				numtable++;

				// tableJSON.put("wikiId", getWikidFromCell(matrix));
				rdf.cleanUpMatrix(matrix);
				/*
				 * for (int j = 0; j < matrix[0].length; j++) {
				 * 
				 * wordShapeJson.put("column" + j,
				 * rdf.predicteColumnDataType(matrix, j));
				 * dataTypeJson.put("column" + j,
				 * rdf.predicteColumnShape1(matrix, j)); }
				 */

				JSONArray rowJSON = new JSONArray();
				for (int i = rdf.getIndexRowHeader(matrix) + 1; i < matrix.length; i++) {
					ArrayList<String> rows = getMatrixRow(matrix, i);
					ArrayList<String> rowAbstracts = getMatrixRowAbstract(matrix, i);
					ArrayList<String> allPairRelationships = getAllPairRelationships(matrix, i);
					this.numberOfExtractedRelationships += allPairRelationships.size();

					final JSONObject row = new JSONObject();
					row.put("idx", i);
					row.put("values", rows);
					row.put("abstracts", rowAbstracts);
					row.put("relationships", allPairRelationships);
					rowJSON.put(row);
				}

				// tableJSON.put("Word shape"+ numtable, wordShapeJson);
				// tableJSON.put("Data type"+ numtable, dataTypeJson);

				tableJSON.put("headers", headers);
				tableJSON.put("contents", rowJSON);

				String[] headerTypes = wikiTextParser.predictLabelClass(matrix);
                this.numberOfExtractedHeaderTypes += Arrays.stream(headerTypes).filter(ht -> !ht.isEmpty()).count();
                tableJSON.put("headerTypes", Arrays.stream(headerTypes).collect(Collectors.toList()));

				String title = regexReplaceWhiteSpace(getTitle());
				title = title.replaceAll("/!", "");
				System.out.println(title);
				tableJSON.put("tableIdx", numtable);
				tableJSON.put("articleId", articleId);
				tableJSON.put("title", getTitle());
				tableJSON.put("url", "https://en.wikipedia.org/wiki/" + regexReplaceWhiteSpace(getTitle()));
				tableJSON.put("categories", getCategories());
				tableJSON.put("abstract", DbpediaManager.getAbstractSparql(regexReplaceWhiteSpace(getTitle())));
				tableJSON.put("redirects", DbpediaManager.getRedirectSparql(regexReplaceWhiteSpace(getTitle())));

				tables.add(tableJSON.toString(4));

				tableJSON.put("Number of rows", numberRows);
				tableJSON.put("Number of columns", numberColumns);
				returnObj.put(tableJSON);
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("tables" + articleId, returnObj);
			fileWriter.write(jsonObject.toString(4));
		}

		return tables;
	}

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfExtractedHeaderTypes() {
        return numberOfExtractedHeaderTypes;
    }

    public int getNumberOfExtractedRelationships() {
        return numberOfExtractedRelationships;
    }

    private ArrayList<String> getAllPairRelationships(Cell[][] matrix, int i) {
		Map<Integer, Set<String>> wikiIdMap = new HashMap<>();
		
		for (int j = 0; j < matrix[i].length; j++) {
			if (matrix[i][j].getWikiLinks() != null) {
				wikiIdMap.put(j, 
						matrix[i][j].getWikiLinks().stream()
						.map(this::regexReplaceWhiteSpace)
						.collect(Collectors.toSet())
						);
			}
		}
		
		Set<String> result = new TreeSet<>();
		
		ArrayList<Integer> wikiIdKeys = new ArrayList<>(wikiIdMap.keySet());
		
		for (int j = 0; j < wikiIdKeys.size(); j++) {
			for (String candidateSubject : wikiIdMap.get(wikiIdKeys.get(j))) {

			    final int pivot = j;
                final List<String> candidateObjects = wikiIdKeys.stream()
                        .filter(k -> k != pivot)
                        .flatMap(k -> wikiIdMap.get(k).stream())
                        .collect(Collectors.toList());
                candidateObjects.add(getTitle().replaceAll("\\s+", "_"));

                final Map<String, Set<String>> predicates = DbpediaManager.getPredicates(candidateSubject, candidateObjects);

                for (Map.Entry<String, Set<String>> predicateEntry : predicates.entrySet()) {
					for (String foundPredicate : predicateEntry.getValue()) {
						final String reformedPredicate = upperCasePattern.matcher(foundPredicate).replaceAll(" $0");
						result.add(String.format("%s %s %s", candidateSubject.replaceAll("_", " "), reformedPredicate, predicateEntry.getKey().replaceAll("_", " ")));
					}
                }

			}
			
		}

		return new ArrayList<>(result);
	}
	
	private ArrayList<String> getMatrixRowAbstract(Cell[][] matrix, int i) {
		Set<String> wikiIds = new HashSet<>();
		for (int j = 0; j < matrix[i].length; j++) {
			if (matrix[i][j].getWikiLinks() != null)
				for (String wikiId : matrix[i][j].getWikiLinks()) {
					wikiIds.add(regexReplaceWhiteSpace(wikiId));
				}
		}
		
		ArrayList<String> result = new ArrayList<String>();
		for (String wikiId : wikiIds) {
			String wholeAbstract = DbpediaManager.getAbstractSparql(wikiId);
			if (wholeAbstract != null && !wholeAbstract.equals("")) {
				DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(wholeAbstract));
				dp.setTokenizerFactory(PTBTokenizer.PTBTokenizerFactory.newCoreLabelTokenizerFactory("normalizeParentheses=false,normalizeOtherBrackets=false"));
				List<HasWord> firstSentence = dp.iterator().next();
				result.add(firstSentence.stream().map(HasWord::word).collect(Collectors.joining(" ")));
			}
		}

		return result;
	}

	/**
	 * printout RDF triples and also produce JSON file
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public void getRDFTriples() throws IOException, JSONException {
		ArrayList<Cell[][]> matrixTables = new ArrayList<Cell[][]>();

		matrixTables = wikiTextParser.getAllMatrixFromTables();

		if (matrixTables.isEmpty()) {
			System.out.println("There is non well-formed table to produce RDF triples");

		} else {
			File file = new File("/Users/nouhadziri/Desktop/toJson.json");
			Writer fileWriter = new FileWriter(file);
			JSONObject returnObj = new JSONObject();
			JSONObject tableJSON = new JSONObject();
			int numberColumns = 0;
			int numberRows = 0;

			for (Cell[][] matrix : matrixTables) {

				System.out.println("Unique column: " + rdf.checkuniqueValue(matrix, 0));

				System.out.println("Headers indexes" + rdf.getIndexAllHeaders(matrix));

				rdf.cleanUpMatrix(matrix);
				System.out.println("list of headers: " + rdf.getHeaders(matrix));
				System.out.println("This table contain a cast : " + rdf.checkCastMatrix(matrix));

				rdf.cleanUpMatrix(matrix);

				System.out.println("\n***Word Shape & Data type***\n");

				for (int j = 0; j < matrix[0].length; j++) {

					System.out.println("Data Type of column " + j + " : " + rdf.predicteColumnDataType(matrix, j));
					System.out.println("Word Shape of column " + j + " : " + rdf.predicteColumnShape1(matrix, j));
					
				}

				ArrayList<Triple<String, String, String>> listTriple = null;

				ArrayList<String> headers = new ArrayList<String>();

				listTriple = rdf.produceRDF(matrix);
				listTriple = rdf.produceRDF(matrix);
				rdf.cleanUpMatrix(matrix);
				numberColumns = matrix[0].length;
				numberRows = matrix.length;
				headers = rdf.getHeaders(matrix);
				numtable++;
				tableJSON.put("title", "mama mia");
				tableJSON.put("table#", "table id from wikipedia");
				tableJSON.put("# rows", numberRows);
				tableJSON.put("# columns", numberColumns);
				tableJSON.put("list of headers ", headers);
				tableJSON.put("list of triples ", listTriple);
				returnObj.put("table" + numtable, tableJSON);

				tableJSON = new JSONObject();
				// createJsonFile(matrix,returnObj,numtable);
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

	/*
	 * public void createJsonFile(Cell[][] matrix,JSONObject returnObj,int
	 * numtable) throws JSONException, IOException { JSONObject tableJSON = new
	 * JSONObject();
	 * 
	 * ArrayList<Triple<String, String, String>> listTriple=null;
	 * 
	 * ArrayList<String> headers = new ArrayList<String>();
	 * 
	 * listTriple = rdf.produceRDF(matrix); rdf.cleanUpMatrix(matrix); int
	 * numberColumns = matrix[0].length; int numberRows = matrix.length; headers
	 * = rdf.getHeaders(matrix); numtable++; tableJSON.put("title", "mama mia");
	 * tableJSON.put("table#", "table id from wikipedia" );
	 * tableJSON.put("# rows", numberRows);
	 * tableJSON.put("# columns",numberColumns);
	 * tableJSON.put("list of headers ",headers);
	 * tableJSON.put("list of triples ",listTriple);
	 * returnObj.put("table"+numtable, tableJSON);
	 * 
	 * tableJSON = new JSONObject();
	 * 
	 * 
	 * }
	 */

	public int getCountColspan() {
		return wikiTextParser.countColspan();
	}

	public int getCountRowspan() {
		return wikiTextParser.countRowspan();
	}

	public int getCountMixRowandColumn() {
		return wikiTextParser.countMixColspanAndRowspan();
	}

	public int getCountNestedTable() {
		return wikiTextParser.countNestedtables();
	}

	public int getCountTable() {
		return wikiTextParser.countTable();
	}

	public int getCountExceptions() {
		return wikiTextParser.counthasException();
	}

	public int getCountMisuseException() {
		return wikiTextParser.counthasMisuseException();
	}

	public int getCountHasNoHeader() {
		return wikiTextParser.counthasNoHeader();
	}

	public int getCountCaption() {
		return wikiTextParser.countCaption();
	}

}