package ca.ualberta.wikipedia.tablereader;

import java.io.IOException;



public class TestWiki {
public static void main(String[] args) throws IOException {
		
		
		ReadFile read = new ReadFile();
		
		//System.out.println(read.readFile("/Users/Nouha/Desktop/file"));

		WikipediaTableParser wikiparser1 = new WikipediaTableParser();
		//CreateTables tables = new CreateTables();
		
		//System.out.println(wikiparser1.translateHeaderCell2(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.translateHeaderCell1(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.breakRows(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.countColumns(read.readFile("/Users/Nouha/Desktop/file")));
		 //System.out.println(wikiparser1.translateCell2(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.translateCell1(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.regexColspan(read.readFile("/Users/Nouha/Desktop/file")));
		//wikiparser1.createMatrix(read.readFile("/Users/Nouha/Desktop/file"));
		//System.out.println(wikiparser1.translateHeaderRow2(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.translateNormalRow1(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.translateNormalRow2(read.readFile("/Users/Nouha/Desktop/file")));
		//System.out.println(wikiparser1.startwith(read.readFile("/Users/Nouha/Desktop/file")));
	//System.out.println(wikiparser1.translateHeaderRow(read.readFile("/Users/Nouha/Desktop/file")));
	//wikiparser1.parseTable(read.readFile("/Users/Nouha/Desktop/file"));
	//wikiparser1.printoutMatrix(read.readFile("/Users/Nouha/Desktop/file"));
	//System.out.println(wikiparser1.translateHeaderRow("ojjp\n!colspan=\"2\"| Election !! Member !! Party"));
		
		//System.out.println(wikiparser1.cleanRows(read.readFile("/Users/Nouha/Desktop/file")));
	//System.out.println(wikiparser1.translateHeaderAndRow(read.readFile("/Users/Nouha/Desktop/file")));
		
		//System.out.println(wikiparser1.translateHeaderCell3(read.readFile("/Users/Nouha/Desktop/file")));
		
		//System.out.println(wikiparser1.regexAttributeStyle(read.readFile("/Users/Nouha/Desktop/file")));
	
	//System.out.println(wikiparser1.translateHeaderRow3(read.readFile("/Users/Nouha/Desktop/file")));
		
		//System.out.println(tables.createTable(read.readFile("/Users/Nouha/Desktop/file")));
		
		//System.out.println(wikiparser1.getAllMatrixFromTables(read.readFile("/Users/Nouha/Desktop/file")));
		
	//	wikiparser1.printAllMatrixFromTables(read.readFile("/Users/Nouha/Desktop/file"));
	/*	System.out.println("\n");
		System.out.println("***** Statistics *****");
	System.out.println("Number of tables  : "+wikiparser1.countTable(read.readFile("/Users/Nouha/Desktop/file")));
		System.out.println("Number of tables having colspan attribute : "+wikiparser1.countColspan());
		System.out.println("Number of tables having rowspan attribute : "+wikiparser1.countRowspan());
		System.out.println("Number of tables having mix rowspan and colspan attribute : "+wikiparser1.countMixColspanAndRowspan());
		System.out.println("Number of tables having nested table : "+wikiparser1.countNestedtables());
		System.out.println("Number of tables having exception : "+ wikiparser1.counthasException());
		System.out.println(wikiparser1.CountSymbole(read.readFile("/Users/Nouha/Desktop/file")));*/
	}

}
