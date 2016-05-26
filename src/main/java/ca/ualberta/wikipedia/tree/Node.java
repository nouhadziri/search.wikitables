package ca.ualberta.wikipedia.tree;

import java.util.HashSet;

public class Node {

	
	String wikiText;
	String type;
	String name;
	private HashSet<Node> children = null;
	
	
	public Node(String wikiText, String type, String name)
	{	this.type = type;
		this.name = name;
		this.wikiText = wikiText;
		WikiTableParser wikiTableParser= new WikiTableParser();
		children = wikiTableParser.parseTable(wikiText);
	}
}
