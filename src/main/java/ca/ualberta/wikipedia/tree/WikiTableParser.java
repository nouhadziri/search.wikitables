package ca.ualberta.wikipedia.tree;

import java.util.HashSet;
import java.util.List;



public class WikiTableParser {
	
	
	public HashSet<String> tables1 = null;
	public HashSet<Node> nodes = null;
	public String wikiText;
	
	/**
	 * 
	 * 
	 */
	
  public HashSet<Node> parseTable(String wikiText)
	{
		return null;
		
	}

	/**
	 * 
	 * 
	 */
   public HashSet<String> createTable()
   {
	   final String TABLE_CONST_STR = "{|";
	     int startPos = wikiText.indexOf(TABLE_CONST_STR);
	     if (startPos < 0) System.out.println("null");;
	     int bracketCount = 1;
	     int endPos = startPos + TABLE_CONST_STR.length();
	     	while(endPos < wikiText.length())
	     	{
	     		String text = wikiText.substring(endPos,endPos + 1); 
	     		switch(text)
	     		{
	     		case "|}":
	               bracketCount--;
	               break;
	           case "{|":
	               bracketCount++;
	               break;
	           default:
	     		}
	     		if (bracketCount == 0) 	
	     		{  
	     			String table = wikiText.substring(startPos, endPos + 1);
	     			tables1.add(table);
	     			Node node= new Node(table,"table","table");
	     			nodes.add(node);	
	     		}
	     		endPos++;
	     	}
	     
	     if (bracketCount != 0) {
	     	System.out.println("Malformed table, couldn't match the brackets.");
	     }	
	     
	     return tables1;
   }
   
  

	/**
	 * 
	 * 
	 */
   
   public void translateHeaderRow()
   {
	   
	   
	   
   }

	/**
	 * 
	 * 
	 */
   
   public void translateNormalRow()
   {
	   
	   
   }
   

	/**
	 * 
	 * 
	 */
	
   public void translateCell()
   {
	   
	   
	   
   }
	

}
