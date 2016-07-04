package ca.ualberta.wikipedia.rdf;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ualberta.wikipedia.tablereader.Cell;



public class GenerateRdf {
	int i = 0;
	// public String IDWiki = null;
	public String wikiIdURI = "http://wikipedia/";
	public String predicatePrefix = "http://myprefix/";
	// URI myURI = new URI(predicatePrefix);

	String wikiText;

	@SuppressWarnings("null")
	public Cell[] readColumn(int j, Cell[][] matrix) {
		Cell[] columnMatrix = null;
		for (int i = 0; i < matrix.length; i++) {
			columnMatrix[i] = matrix[i][j];
		}
		return columnMatrix;
	}

	public Set<Triple<String, String, String>> produceRDF(Cell[][] matrix) {

		
		int j = 0;
		int k = 0;
		Set<Triple<String, String, String>> tripleList = new LinkedHashSet<Triple<String, String, String>>();
		String subject = null;
		String predicate = null;
		String object = null;
		if (matrix[0][0].getContent().contains("|+"))
		{
			i++;
		}
		if (tableHaveHeader(matrix, i)) {
			
			i++;
			k = checkColspan(matrix, i);
			j = k;
			tripleList=  getTriples(subject,object,predicate,
					matrix, i, j,k);
		} else {
			//in the case we have for the first row {| class=" dnd "|-
			//so the first row(0) will be empty that's why our headers will be in row (1)
			//and subject row (2)
			i = +2;
			while (i < matrix.length) {
				subject = matrix[i][j].getContent();
				subject = regexReplaceWhiteSpace(subject);
				subject = wikiIdURI + subject;
				try {
				//	while (j < matrix[i].length - 1) {
						try {
					int number=matrix[i].length;
					for (;j< matrix[i].length - 1;j++)
					{
						
							predicate = "has_" + matrix[1][j + 1].getContent();
							predicate = predicatePrefix + predicate;
							object = matrix[i][j + 1].getContent();
							object = regexReplaceWhiteSpace(object);
							Triple<String, String, String> tripleRDF = new Triple<String, String, String>(subject,
									predicate, object);
							tripleList.add(tripleRDF);
						}} catch (NullPointerException e) {
							// System.out.println("cell is empty");
							// i++;
						}

					}
				catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("out of size");
				}
				j = 0;
				i++;
				k = checkColspan(matrix, i);
				j = k;
			}
		}

		return tripleList;
	}

	public Set<Triple<String, String, String>> getTriples(String subject, String object, String predicate,
			Cell[][] matrix, int i, int j, int k) {
		Set<Triple<String, String, String>> tripleList = new LinkedHashSet<Triple<String, String, String>>();

		while (i < matrix.length) {
			subject = matrix[i][j].getContent();
			subject = regexReplaceWhiteSpace(subject);
			subject = wikiIdURI + subject;
			try {
				while (j < matrix[i].length - 1) {
					try {
						predicate = "has_" + matrix[0][j + 1].getContent();
						predicate = predicatePrefix + predicate;
						object = matrix[i][j + 1].getContent();
						object = regexReplaceWhiteSpace(object);
						Triple<String, String, String> tripleRDF = new Triple<String, String, String>(subject,
								predicate, object);
						tripleList.add(tripleRDF);
						j++;
					} catch (ArrayIndexOutOfBoundsException e) {
						// System.out.println("cell is empty");
						// i++;
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("out of size");
			}
			j = 0;
			i++;
			k = checkColspan(matrix, i);
			j = k;
		}

		return tripleList;
	}

	public void printOutRDFTriple(ArrayList<Cell[][]> listMatrix) {
		for (Cell[][] matrix : listMatrix) {
			Set<Triple<String, String, String>> listTriple = produceRDF(matrix);
			for (Triple<String, String, String> triple : listTriple) {
				System.out.println("RDF Triple:  " + triple);
			}

		}
	}

	public int getTableStartRow(Cell[][] matrix, int i) {

		int j = 0;
		while (matrix[i][j] != null) {
			j++;
			if (matrix[i][j] == null) {
				break;
			}
		}

		return j;
	}

	public void createLabelPredicate() {

	}

	public boolean tableHaveHeader(Cell[][] matrix, int i) {
		boolean haveHeader = false;

		for (int j = 0; j < matrix.length; j++) {
			try {
				if (matrix[i][j].getType() == "header") {
					haveHeader = true;
				}
				else {
					haveHeader = false;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("");
			} catch (NullPointerException e) {
				System.out.println("row is empty");
			}
		}
		return haveHeader;

	}

	public void createURI(String subject) {

	}

	public String regexReplaceWhiteSpace(String subject) {
		Pattern checkRegex = Pattern.compile("\\s", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher regexMatcher = checkRegex.matcher(subject);
		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				subject = regexMatcher.replaceAll("_");
			}
		}
		return subject;
	}

	public int checkColspan(Cell[][] matrix, int i) {
		int j = 0;
		try {
			while (matrix[i][j].getContent() == matrix[i][j + 1].getContent()) {
				j++;
				if (j == matrix[0].length) {
					break;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ok");
		}
		return j;
	}

	/*
	 * public int checkEmptyCell(Cell[][] matrix) { int i = 0; int j = 0; matrix
	 * = new Cell[0][matrix[0].length]; try { while
	 * (matrix[i][j].getContent().isEmpty()) { j++; if (j == matrix[0].length) {
	 * break; } } } catch (ArrayIndexOutOfBoundsException e) {
	 * System.out.println("ok"); } try{ if (j==matrix[0].length-1 ) { i++; }}
	 * catch(ArrayIndexOutOfBoundsException e) {
	 * 
	 * } return j;
	 * 
	 * }
	 */
}
