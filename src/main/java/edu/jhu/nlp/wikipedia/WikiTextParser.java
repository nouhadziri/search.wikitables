package edu.jhu.nlp.wikipedia;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ualberta.wikipedia.tree.Node;

/**
 * For internal use only -- Used by the {@link WikiPage} class.
 * Can also be used as a stand alone class to parse wiki formatted text.
 *
 * @author Delip Rao
 */
public class WikiTextParser {
	private Vector<Pair<String, String>> linkPairs = null;
	private Vector<Pair<String, Integer>> linkPos = null;
	private String wikiText = null;
	private HashSet<String> pageCats = null;
	private HashSet<String> tables = null;
	private String[] headers = null;
	private HashSet<String> pageLinks = null;
	private boolean redirect = false;
	private String redirectString = null;
	private static Pattern redirectPattern = Pattern.compile("#REDIRECT\\s*\\[\\[(.*?)\\]\\]", Pattern.CASE_INSENSITIVE);
	private boolean stub = false;
	private boolean disambiguation = false;
	private static Pattern stubPattern = Pattern.compile("\\-stub\\}\\}", Pattern.CASE_INSENSITIVE);
	private static Pattern disambCatPattern = Pattern.compile("\\{\\{disambig\\}\\}", Pattern.CASE_INSENSITIVE);
	private InfoBox infoBox = null;




	public WikiTextParser(String wtext) {
		wikiText = wtext;
		Matcher matcher = redirectPattern.matcher(wikiText);
		if (matcher.find()) {
			redirect = true;
			if (matcher.groupCount() == 1) {
				redirectString = matcher.group(1);
			}
		}
		matcher = stubPattern.matcher(wikiText);
		stub = matcher.find();
		matcher = disambCatPattern.matcher(wikiText);
		disambiguation = matcher.find();
	}

	public boolean isRedirect() {
		return redirect;
	}

	public boolean isStub() {
		return stub;
	}

	public String getRedirectText() {
		return redirectString;
	}

	public String getText() {
		return wikiText;
	}

	public HashSet<String> getCategories() {
		if (pageCats == null) {
			parseCategories();
		}
		return pageCats;
	}

	public HashSet<String> getLinks() {
		if (pageLinks == null) {
			parseLinks();
		}
		return pageLinks;
	}

	public Vector<Pair<String, Integer>> getLinkPos() {
		if(linkPos == null) parseLinks1();
		return linkPos;
	}

	private void parseCategories() {
		pageCats = new HashSet<String>();
		Pattern catPattern = Pattern.compile("\\[\\[Category:(.*?)\\]\\]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = catPattern.matcher(wikiText);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			pageCats.add(temp[0]);
		}
	}

	/**
	 * We need to format the entity title to reduce duplicates by removing the 
	 * extra space (e.g. use " " instead of "   "), and capitalizing the first letter of the name.
	 * @param name
	 * @return
	 */
	public static String formatName(String name) {
		name = name.replaceAll("\\s+", " ");
		name = name.replace('_', ' ');

		name = name.trim();
		if (name == null || name.isEmpty())
			return null;

		if (Character.isUpperCase(name.charAt(0)))
			return name;
		else {
			StringBuilder t = new StringBuilder(name);
			t.setCharAt(0, Character.toUpperCase(name.charAt(0)));
			return t.toString();
		}
	}

	private void parseLinks() {
		pageLinks = new HashSet<String>();

		Pattern catPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(wikiText);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			if (temp == null || temp.length == 0) {
				continue;
			}
			String link = temp[0];
			if (link.contains(":") == false) {
				pageLinks.add(link);
			}
		}
	}

	private void parseLinks1() {
		pageLinks = new HashSet<String>();
		linkPairs = new Vector<Pair<String, String>>();
		linkPos = new Vector<Pair<String, Integer>>();

		Pattern catPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(wikiText);
		while(matcher.find()) {
			String [] temp = matcher.group(1).split("\\|");
			if(temp == null || temp.length == 0) continue;
			String entity = temp[0];
			if(WikiPage.isSpecialTitle(entity) == false) {
				entity = formatName(entity);
				if (entity == null || entity.isEmpty())
					continue;

				pageLinks.add(entity);
				linkPos.add(new Pair<String, Integer>(entity, matcher.start()));

				if (temp.length == 2) {
					String name = temp[1];
					if (name != null) 
						name = formatName(name);

					if (name == null || name.isEmpty())
						continue;

					linkPairs.add(new Pair<String, String>(entity, name));
				} else {
					linkPairs.add(new Pair<String, String>(entity, entity));
				}
			}
		}
	}

	private static Pattern stylesPattern = Pattern.compile("\\{\\|.*?\\|\\}$", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern infoboxCleanupPattern = Pattern.compile("\\{\\{infobox.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	private static Pattern curlyCleanupPattern0 = Pattern.compile("^\\{\\{.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern curlyCleanupPattern1 = Pattern.compile("\\{\\{.*?\\}\\}", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern cleanupPattern0 = Pattern.compile("^\\[\\[.*?:.*?\\]\\]$", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern cleanupPattern1 = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern refCleanupPattern = Pattern.compile("<ref>.*?</ref>", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern commentsCleanupPattern = Pattern.compile("<!--.*?-->", Pattern.MULTILINE | Pattern.DOTALL);

	public String getPlainText() {
		String text = wikiText.replaceAll("&gt;", ">");
		text = text.replaceAll("&lt;", "<");
		text = infoboxCleanupPattern.matcher(text).replaceAll(" ");
		text = commentsCleanupPattern.matcher(text).replaceAll(" ");
		text = stylesPattern.matcher(text).replaceAll(" ");
		text = refCleanupPattern.matcher(text).replaceAll(" ");
		text = text.replaceAll("</?.*?>", " ");
		text = curlyCleanupPattern0.matcher(text).replaceAll(" ");
		text = curlyCleanupPattern1.matcher(text).replaceAll(" ");
		text = cleanupPattern0.matcher(text).replaceAll(" ");

		Matcher m = cleanupPattern1.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			// For example: transform match to upper case
			int i = m.group().lastIndexOf('|');
			String replacement;
			if (i > 0) {
				replacement = m.group(1).substring(i - 1);
			} else {
				replacement = m.group(1);
			}
			m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}
		m.appendTail(sb);
		text = sb.toString();

		text = text.replaceAll("'{2,}", "");
		return text.trim();
	}

	public InfoBox getInfoBox() throws WikiTextParserException {
		//parseInfoBox is expensive. Doing it only once like other parse* methods
		if (infoBox == null)
			infoBox = parseInfoBox();
		return infoBox;
	}

	//TODO: ignore brackets in html/xml comments (or better still implement a formal grammar for wiki markup)
	private InfoBox parseInfoBox() throws WikiTextParserException {
		final String INFOBOX_CONST_STR = "{{Infobox";
		int startPos = wikiText.indexOf(INFOBOX_CONST_STR);
		if (startPos < 0) return null;
		int bracketCount = 2;
		int endPos = startPos + INFOBOX_CONST_STR.length();
		for (; endPos < wikiText.length(); endPos++) {
			switch (wikiText.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0) break;
		}

		if (bracketCount != 0) {
			throw new WikiTextParserException("Malformed Infobox, couldn't match the brackets.");
		}

		String infoBoxText = wikiText.substring(startPos, endPos + 1);
		infoBoxText = stripCite(infoBoxText); // strip clumsy {{cite}} tags
		// strip any html formatting
		infoBoxText = infoBoxText.replaceAll("&gt;", ">");
		infoBoxText = infoBoxText.replaceAll("&lt;", "<");
		infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
		infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
		return new InfoBox(infoBoxText);
	}

	private String stripCite(String text) {
		String CITE_CONST_STR = "{{cite";
		int startPos = text.indexOf(CITE_CONST_STR);
		if (startPos < 0) return text;
		int bracketCount = 2;
		int endPos = startPos + CITE_CONST_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0) break;
		}
		text = text.substring(0, startPos - 1) + text.substring(endPos);
		return stripCite(text);
	}

	public boolean isDisambiguationPage() {
		return disambiguation;
	}

	public String getTranslatedTitle(String languageCode) {
		Pattern pattern = Pattern.compile("^\\[\\[" + languageCode + ":(.*?)\\]\\]$", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(wikiText);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	/**
	 * 
	 * Parsing 
	 * 
	 * 
	 */

	private static Pattern tagPattern = Pattern.compile("\\{\\{#tag(.*?)\\}\\}", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern alignPattern = Pattern.compile("align=(.*?)\\|", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern bgcolorPattern = Pattern.compile("\\|\\-\\sbgcolor=(.*?)\\s", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern refPattern = Pattern.compile("<ref.*?/>", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern brPattern = Pattern.compile("<br>", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern refCleanupPattern1 = Pattern.compile("<ref(.*?)>(.*?)\\.</ref>", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern colspanCleanupPattern = Pattern.compile("colspan=(.*?)\\|", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern ntsCleanupPattern = Pattern.compile("nts\\|", Pattern.MULTILINE | Pattern.DOTALL);
	//private static Pattern classCleanupPattern = Pattern.compile("\\|class=(.*?)\\s", Pattern.MULTILINE | Pattern.DOTALL);


	public void parseTable ()
	{	   tables = new HashSet<String>();
	Pattern pattern = Pattern.compile("\\{\\|(.*?)\\|\\}", Pattern.MULTILINE | Pattern.DOTALL);

	Matcher matcher = pattern.matcher(wikiText);
	while (matcher.find()) {
		if (matcher.group().length() != 0){

			String temp= matcher.group();
			temp= tagPattern.matcher(temp).replaceAll("");
			temp= alignPattern.matcher(temp).replaceAll("");
			temp = bgcolorPattern.matcher(temp).replaceAll("");
			temp = refPattern.matcher(temp).replaceAll("");
			temp = brPattern.matcher(temp).replaceAll("");
			temp = refCleanupPattern.matcher(temp).replaceAll("");
			temp = refCleanupPattern1.matcher(temp).replaceAll("");
			temp = colspanCleanupPattern.matcher(temp).replaceAll("");
			temp = ntsCleanupPattern.matcher(temp).replaceAll("");
			// temp = classCleanupPattern.matcher(temp).replaceAll("");
			temp = temp.replaceAll("''","");
			temp = temp.replaceAll("'''",""); 
			tables.add(temp);
		}
	}
	}

	public HashSet<String> getTables() {
		if (tables == null) {
			parseTable();
		}
		return tables;
	}  
	/**
	 * Parsing headers
	 */

	/*   public String[] getHeaders(String table){

    	if (headers == null) {
            parseHeaders(table);
        }
        return headers;
    }

	 */

	/**
	 * 
	 * creating tables (tree) : this method will find the appropriate begining close ("{|")and 
	 * the closing close ("|}")
	 * 
	 */

	public Set<String> createTable()
	{
		Set<String> tables1 = new HashSet<String>();
		final String TABLE_CONST_STR = "{|";
		int startPos = wikiText.indexOf(TABLE_CONST_STR);
		if (startPos < 0) System.out.println("null");
		int bracketCount = 1;
		int endPos = startPos + TABLE_CONST_STR.length();
		while(endPos < wikiText.length()-1)
		{
			
			String text = wikiText.substring(endPos,endPos + 2); 
			switch(text)
			{
			case "|}":
				bracketCount--;
				endPos++;
				break;
			case "{|":
				bracketCount++;
				endPos++;
				break;

			default: endPos++;
			}
			if (bracketCount == 0) 	
			{  
				 
				String table = wikiText.substring(startPos, endPos + 1);
				tables1.add(table);
				startPos = endPos+2;
				//startPos= wikiText.substring(startPos, endPos + 1).indexOf("{|");
				//String restText = wikiText.substring(endPos+1, wikiText.length()-1);
				//startPos = restText.indexOf("{|");
				//Node node= new Node(table,"table","table");
				//nodes.add(node);	
			}

		}

		if (bracketCount != 0) {
			System.out.println("couldn't match the brackets.");
		}	

		return tables1;
	}
	/**
	 * Method that creates a node for each wikitext,
	 *  either it is a table or row or header or a cell
	 * 
	 */
	
	public Node createNode(String wikiText,String type, String name)
	{
		return  new Node(wikiText,type,name);
	}

	/**
	 * Parsing header row tree
	 *
	 */
	public HashSet<String> translateHeaderRow(String table){

		

		
		


		return null;
	}

	public Set<String> breakRows(String table){
	
		Set<String> rows = new HashSet<String>();
		int startRow = 0;
		int startPos = 0;
		int bracketCount=1;
		while (startPos <= table.length()-1)
		{
			String text = table.substring(startPos,startPos+2);
			switch(text)
			{
			case "|-":
				String row = table.substring(startRow, startPos+2);
				rows.add(row);
				startPos++;
				startRow = startPos +1 ;
				
				// I should call the method translate cell and check if I have a nested table
				// should create a row node
				// I shouldn't create the node here 
				break;
			
			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			default: startPos++;
			}
			
			if (bracketCount == 0) 	
			{  
				String row1 = table.substring(startRow, startPos);
				rows.add(row1);
				// I should call the method translate cell and check if I have a nested table
				// should create a row node
			}
		}
		return rows;
	}




	}
