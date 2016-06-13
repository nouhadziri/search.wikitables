package edu.jhu.nlp.wikipedia;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Data structures for a wikipedia page.
 *
 * @author Delip Rao
 */
public class WikiPage {

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
        wikiTextParser = new WikiTextParser(wtext);
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
     * Use this method to get wiki tables
     * @return
     */
    public HashSet<String> getWikiTable(){
    	return wikiTextParser.getTables();
    }
    
    /**
     * getTables tree
     * @param table
     */
    
    public Set<String> getTablesTree()
    {
    	//System.out.println(wikiTextParser.getText());
    	return wikiTextParser.createTable();
    	//return new HashSet<String>();
    }
    
    /**
     * getTable row tree
     */
    
    public Set<String> getTableROw(String table)
    {
    	
    	//return wikiTextParser.translateNormalRow(table);
    	return null;
    }
    
    public void parseHeaders (String table)
    {
    	//headers= new HashSet<String>();
    	 Pattern pattern = Pattern.compile("!(.*?)\\|\\-", Pattern.MULTILINE | Pattern.DOTALL);
    	   Matcher matcher = pattern.matcher(table);
           while (matcher.find()) {
        	   if (matcher.group().length() != 0){
        		   
        		   String[] headers= matcher.group(1).split("!");
        		   
        		   
        		   for (int i=0;i< headers.length;i++)
        	         {
        			    headers[i]= headers[i].replaceAll("<ref.*?>.*?</ref>", " ");
        			    headers[i]= headers[i].replaceAll("<ref.*?/>", " ");
        			    headers[i]= headers[i].replaceAll("<br>", " ");
        			    headers[i]= headers[i].replaceAll("colspan=.*?\\|", " ");
        			    headers[i]= headers[i].replaceAll("'''", " ");
        			    
        	        	System.out.println("headers: "+ headers[i]); 
        	         }
        		   /*for(int i=0; i<header.length; i++){
        		   headers.add(header[i]);
        		   }*/
     			  }
       
           		}
    	 
    }  
    
   

    
    /**
     * Parsing rows
     */
 
    
    public void parseRows (String table)
    {
    	Pattern pattern = Pattern.compile("\\|(.*?)\\|\\-", Pattern.MULTILINE | Pattern.DOTALL);
  	   Matcher matcher = pattern.matcher(table);
  	 while(matcher.find()) {
		  if (matcher.group().length() != 0){
			 String regex = matcher.group(1).substring(2);
			 //System.out.println("le regex: "+regex);
			// System.out.println("hello");
			String[]   rows= regex.split("\\|\\|");
			 
			for (int i=0;i<rows.length;i++)
			{
		  System.out.println("rows: "+rows[i]);
			}
			
			}
    }}
    
    
    public void parseRows1 (String table)
    {
    	Pattern pattern = Pattern.compile("\\|\\-(.*?)\\|\\-", Pattern.MULTILINE | Pattern.DOTALL);
  	   Matcher matcher = pattern.matcher(table);
  	 while(matcher.find()) {
		  if (matcher.group().length() != 0){
			 String regex = matcher.group(1).substring(2);
			 //System.out.println("le regex: "+regex);
			// System.out.println("hello");
			String[]   rows= regex.split("\\|");
			 
			for (int i=0;i<rows.length;i++)
			{
		  System.out.println("rows: "+rows[i]);
			}
			
			}
    }}
    

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
}