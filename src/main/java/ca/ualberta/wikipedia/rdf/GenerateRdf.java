package ca.ualberta.wikipedia.rdf;

import java.util.ArrayList;
import java.util.Collections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ualberta.wikipedia.rdf.WordShapeClassifier;
import ca.ualberta.wikipedia.tablereader.Cell;



public class GenerateRdf {
	int i = 0;
	public WordShapeClassifier wordClassifier = new WordShapeClassifier();
	// public String IDWiki = null;
	public String wikiIdURI = "http://wikipedia/";
	public String predicatePrefix = "http://myprefix/";
	// URI myURI = new URI(predicatePrefix);

	String wikiText;

	public ArrayList<Triple<String, String, String>> produceRDF(Cell[][] matrix) {

		int j = 0;
		int k = 0;
		ArrayList<Triple<String, String, String>> tripleList = new ArrayList<Triple<String, String, String>>();
		String subject = null;
		String predicate = null;
		String object = null;
		/*
		 * if (matrix[0][0].getContent().contains("|+")) { i++; }
		 */
		cleanUpMatrix(matrix);
		matrix = predicteEntityColumn1(matrix, "Xx Xx", 0);
		// matrix = predicteEntityColumn(matrix);
		// matrix = reOrderMatrix(matrix);
		int i = getIndexRowHeader(matrix);

		if (i == 0) {
			i++;
			// we have a header in the first row of the matrix
			// we don't have an empty row
			k = checkColspan(matrix, i);
			j = k;
			tripleList = getTriples(subject, object, predicate, matrix, i, j, k);

		} else if (i == 1) {
			i++;
			// in this case we have for the first row {| class=" dnd "|-
			// so the first row(0) will be empty that's why our headers will be
			// in row (1)
			// and subject row (2)
			while (i < matrix.length) {
				subject = matrix[i][j].getContent().trim();
				subject = parseCurlCell(subject);
				subject = parseLinkCell1(subject);
				subject = regexReplaceWhiteSpace(subject);
				subject = wikiIdURI + subject;
				try {

					try {

						for (; j < matrix[i].length - 1; j++) {

							predicate = "has_" + parseLinkCell1(parseCurlCell(matrix[1][j + 1].getContent().trim()));
							predicate = predicatePrefix + predicate;
							object = parseLinkCell1(parseCurlCell(parseLinkCell(matrix[i][j + 1].getContent().trim())));
							object = regexReplaceWhiteSpace(object);
							// this condition because we may have $163,98
							if (!wordClassifier.wordShape(object.trim(), 2).contains("d")) {
								String[] temp = object.split("\\s*,\\s*");
								for (int m = 0; m < temp.length; m++) {

									object = temp[m];
									Triple<String, String, String> tripleRDF = new Triple<String, String, String>(
											subject, predicate, object);
									tripleList.add(tripleRDF);
								}

							} else {
								Triple<String, String, String> tripleRDF = new Triple<String, String, String>(subject,
										predicate, object);
								tripleList.add(tripleRDF);
							}

						}
					} catch (NullPointerException e) {
						// System.out.println("cell is empty");
						// i++;
					}

				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("out of size");
				}
				j = 0;
				i++;
				k = checkColspan(matrix, i);
				j = k;
			}
		}

		else if (i == matrix.length) {

			System.out.println("this table does not have a header !");
			String predicateHeader = "property";
			for (int p = 0; p < matrix[0].length; p++) {
				matrix[0][p].setContent(predicateHeader);
				// System.out.println(p);
			}
			i = 1;
			k = checkColspan(matrix, i);
			j = k;
			tripleList = getTriples(subject, object, predicate, matrix, i, j, k);
		}

		return tripleList;
	}

	public ArrayList<Triple<String, String, String>> getTriples(String subject, String object, String predicate,
			Cell[][] matrix, int i, int j, int k) {
		ArrayList<Triple<String, String, String>> tripleList = new ArrayList<Triple<String, String, String>>();

		while (i < matrix.length) {
			subject = parseCurlCell(matrix[i][j].getContent());
			subject = parseLinkCell1(subject);
			subject = regexReplaceWhiteSpace(subject);
			subject = wikiIdURI + subject;
			try {
				while (j < matrix[i].length - 1) {
					try {

						predicate = "has_" + parseLinkCell1(parseCurlCell(matrix[0][j + 1].getContent()));
						predicate = predicatePrefix + predicate;
						object = parseLinkCell1(parseCurlCell(matrix[i][j + 1].getContent()));
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
			ArrayList<Triple<String, String, String>> listTriple = produceRDF(matrix);
			for (Triple<String, String, String> triple : listTriple) {
				System.out.println("RDF Triple:  " + triple);

			}

		}
	}

	/*
	 * public int getTableStartRow(Cell[][] matrix, int i) {
	 * 
	 * int j = 0; while (matrix[i][j] != null) { j++; if (matrix[i][j] == null)
	 * { break; } }
	 * 
	 * return j; }
	 */
	public boolean tableHaveHeader(Cell[][] matrix, int i) {
		boolean haveHeader = false;

		for (int j = 0; j < matrix[0].length; j++) {
			try {
				if (matrix[i][j].getType() == "header") {
					haveHeader = true;
				} else {
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
			System.out.println("ok ");
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
	/**
	 * this method returns the index of the header row
	 * 
	 * @param matrix
	 * @return
	 */
	public int getIndexRowHeader(Cell[][] matrix) {
		int i = 0;

		try {
			while (i < matrix.length) {
				while (tableHaveHeader(matrix, i)) {
					return i;

				}
				i++;

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Index out of bounds");
		}
		return i;
	}

	public Cell[] readColumn(int j, Cell[][] matrix) {

		int row = matrix.length;

		Cell[] columnMatrix = new Cell[row];

		for (int i = 0; i < matrix.length; i++) {
			try {
				Cell cell = new Cell(matrix[i][j].getType(), matrix[i][j].getContent());
				columnMatrix[i] = cell;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("it runs out of bounds: read column");
			}
		}

		return columnMatrix;
	}

	public int getSizeOfItemssplitted(String cell) {

		String[] cellArray = cell.split("\\s*,\\s*");

		int size = cellArray.length;

		return size;
	}

	/**
	 * this method checks for a given column if it has cast or not
	 * 
	 * @param j
	 * @param matrix
	 * @return
	 */
	public boolean checkCastColumn(int j, Cell[][] matrix) {
		boolean result = false;
		ArrayList<Integer> listSize = new ArrayList<Integer>();
		Cell[] columnMatrix = readColumn(j, matrix);
		int size = 0;
		for (int i = 0; i < columnMatrix.length; i++) {
			size = getSizeOfItemssplitted(columnMatrix[i].getContent());
			listSize.add(size);
		}

		for (int var : listSize) {
			if (var > 1) {
				result = true;
			} else {
				result = false;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public boolean checkCastMatrix(Cell[][] matrix) {
		boolean result = false;
		for (int j = 0; j < matrix[0].length; j++) {
			result = checkCastColumn(j, matrix);
			if (result) {
				break;
			}
		}

		return result;
	}

	/**
	 * 
	 * this method will check for every column if we have a year or rank or
	 * month and return the index of this column
	 * 
	 * @param matrix
	 * @return
	 */
	public int getIndexYearColumn(Cell[][] matrix) {
		int k = -1;
		Cell[] columnMatrix = null;
		int i = 0;
		boolean var = false;
		for (int j = 0; j < matrix[0].length; j++) {
			columnMatrix = readColumn(j, matrix);
			// this is the first column of our matrix
			// we're going to check if there is a column contains digits like
			// year or rank or it has month
			while (i < columnMatrix.length) {
				if (matrix[i][j].getContent() == "null") {
					i++;
				}
				if ((matrix[i][j].getType() == "header")) {
					i++;
				}
				// normally we should return only one j
				try {
					if (checkYear(matrix[i][j].getContent()) && checkYear(matrix[i + 1][j].getContent())) {
						var = true;
						i++;

					}

					else {
						i++;
						var = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					i++;
					System.out.println("");
				}

			}
			if (var == true) {
				k = j;
				break;
			}

		}
		return k;
	}

	public int getIndexRankColumn(Cell[][] matrix) {
		int k = -1;
		Cell[] columnMatrix = null;
		int i = 0;
		boolean var = false;
		for (int j = 0; j < matrix[0].length; j++) {
			columnMatrix = readColumn(j, matrix);
			// this is the first column of our matrix
			// we're going to check if there is a column contains digits like
			// year or rank or it has month
			while (i < columnMatrix.length) {
				if (matrix[i][j].getContent() == "null") {
					i++;
				}
				if ((matrix[i][j].getType() == "header")) {
					i++;
				}
				// normally we should return only one j
				try {
					if (checkRank(matrix[i][j].getContent()) && checkRank(matrix[i + 1][j].getContent())) {
						var = true;
						i++;

					}

					else {
						i++;
						var = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					i++;
					System.out.println("");
				}

			}
			if (var == true) {
				k = j;
				break;
			}

		}
		return k;
	}

	/**
	 * Not the good one we're going to reorder the first column with the second
	 * column if there is no year,no rank, matrix will remain the same
	 * 
	 * @param matrix
	 * @return
	 */
	public Cell[][] reOrderMatrix(Cell[][] matrix) {

		boolean hasRank = false;
		Cell[] columnMatrix = null;
		int k = getIndexYearColumn(matrix);
		if (k == -1) {
			System.out.println("there is no year column");
			int m = getIndexRankColumn(matrix);
			hasRank = true;
			k = m;
		}

		// we're going to reorder our matrix unless the first column is not our
		// entity.
		// sinon on retourne our matrix sans la modifier
		if (k == 0) {
			columnMatrix = readColumn(k, matrix);
			for (int i = 0; i < matrix.length; i++) {
				matrix[i][0] = matrix[i][1];
			}

			for (int i = 0; i < matrix.length; i++) {
				matrix[i][1] = columnMatrix[i];
			}

		}

		else if ((k == 0) && (hasRank = true)) {
			matrix = deleteColumn(matrix, 0);
		}
		return matrix;
	}

	/**
	 * this method deletes one given column
	 * 
	 * @param matrix
	 * @param col
	 * @return
	 */

	public Cell[][] deleteColumn(Cell[][] matrix, int col) {
		Cell[][] matrix1 = new Cell[matrix.length][matrix[0].length - 1];
		if (matrix != null && matrix.length > 0 && matrix[0].length > col) {

			for (int i = 0; i < matrix.length; i++) {
				int newColIdx = 0;
				for (int j = 0; j < matrix[i].length; j++) {
					if (j != col) {
						matrix1[i][newColIdx] = new Cell(matrix[i][j].getType(), matrix[i][j].getContent());
						newColIdx++;
					}
				}
			}
		}

		return matrix1;
	}

	public boolean checkYear(String cell) {
		cell = cell.trim();
		boolean var = false;
		String digitRegex = "^\\d{4}$";
		Pattern pattern = Pattern.compile(digitRegex);

		var = pattern.matcher(cell).matches();

		return var;
	}

	// checks if in our cell we have a digit : it could be a rank, a date
	public boolean checkRank(String cell) {
		Pattern patternCleanup = Pattern.compile("\\.");
		cell = patternCleanup.matcher(cell).replaceAll("");
		String digitRegex = "\\d+";
		Pattern pattern = Pattern.compile(digitRegex);
		return pattern.matcher(cell).find();

	}

	public boolean checkMonth(String cell) {
		Pattern patternCleanup = Pattern.compile("\\.");
		cell = patternCleanup.matcher(cell).replaceAll("");
		String digitRegex = "[J-j]anuary|[F-f]ebruary|[M-m]ars|[A-a]pril|[M-m]ay|[J-j]une|[J-j]ully|[A-a]ugust|[S-s]eptember|[O-o]ctober|[N-n]ovember|[D-d]ecember";
		Pattern pattern = Pattern.compile(digitRegex);

		return pattern.matcher(cell).find();

	}

	public boolean checkFlag(String cell) {

		String digitRegex = "\\{\\{flag\\|(.*?)\\}\\}";
		Pattern pattern = Pattern.compile(digitRegex);
		return pattern.matcher(cell).matches();

	}

	public boolean checkdate(String cell) {

		String digitRegex = "^\\d{4}-\\d{2}-\\d{2}$";
		Pattern pattern = Pattern.compile(digitRegex);
		return pattern.matcher(cell).matches();
	}

	/**
	 * this method gives us informations about repeated headers,so we could be
	 * able to divide our matrix
	 * 
	 * @param matrix
	 * @return
	 */

	public ArrayList<Integer> getIndexRepeatedHeaders(Cell[][] matrix) {
		ArrayList<Integer> listIndex = new ArrayList<Integer>();
		int j = 0;
		int i = getIndexRowHeader(matrix);
		while (j < matrix[0].length) {
			try {
				String cell2 = matrix[i][j + 1].getContent();

				while (!(matrix[i][0].getContent().equals(cell2))) {
					j++;
					cell2 = matrix[i][j + 1].getContent();

				}
				listIndex.add(j);
				j++;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("repeated headers ,run out of size");
				j++;

			}
		}

		return listIndex;
	}

	/**
	 * Whenever we have duplicated headers we re going to divide the matrix mais
	 * il faut generaliser car cette methode divise une matrice en deux
	 * seulement car on a suppose que la matrice pourrait avoir seulement une
	 * seule fois la duplication
	 * 
	 * @param matrix
	 * @return
	 */

	public ArrayList<Cell[][]> divideMatrix(Cell[][] matrix) {

		ArrayList<Cell[][]> listMatrix = new ArrayList<Cell[][]>();
		ArrayList<Integer> listIndex = getIndexRepeatedHeaders(matrix);
		int n = 0;
		Cell[][] matrix1 = new Cell[matrix.length][listIndex.get(0) + 1];
		Cell[][] matrix2 = new Cell[matrix.length][listIndex.get(0) + 1];
		for (int m = 0; m < listIndex.size(); m++) {

			int k = listIndex.get(m);

			for (int i = 0; i < matrix.length; i++) {
				for (int j = n; j < k + 1; j++) {
					Cell cell1 = new Cell(matrix[i][j].getType(), matrix[i][j].getContent());
					matrix1[i][j] = cell1;

				}
			}
			listMatrix.add(matrix1);

			n = listIndex.get(m) + 1;
		}

		for (int i = 0; i < matrix.length; i++) {
			for (int j = n; j < matrix[0].length; j++) {
				try {
					String var1 = matrix[i][j].getType();
					String var2 = matrix[i][j].getContent();
					// WHY ????????? NULLL??
					Cell cell2 = new Cell(var1, var2);
					matrix2[i][j] = cell2;

				} catch (ArrayIndexOutOfBoundsException e) {
					// System.out.println("");
				}
			}
		}
		listMatrix.add(matrix2);

		return listMatrix;
	}

	public void printOutMatrix(ArrayList<Cell[][]> matrixes) {
		for (Cell[][] matrix : matrixes) {
			cleanUpMatrix(matrix);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					try {
						if (matrix[i][j].getContent() == "null") {
							continue;
						}
						System.out.print(matrix[i][j].getContent() + " ");
					} catch (java.lang.NullPointerException e) {
						// System.out.print("");
					}
				}
				System.out.print("\n");
			}
		}
	}

	public String parseLinkCell(String cell) {
		String listString = "";
		ArrayList<String> listLink = new ArrayList<String>();
		// listLink=null;
		String link = cell;
		boolean var = false;
		Pattern catPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(cell);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			if (temp == null || temp.length == 0) {
				return listString;
			}

			if (temp.length == 2) {
				link = temp[1];
				listLink.add(link);
				var = true;
			} else {
				link = temp[0];
				listLink.add(link);
				var = true;

			}
		}

		for (String s : listLink) {
			listString += s + ",";
		}
		// if we have a list of string for one cell
		// we have to delete the last ","
		if (var) {
			listString = removeLastChar(listString);
		}
		if (listString.equals("")) {
			listString = cell;
		}
		return listString;
	}

	public String parseLinkCell1(String cell) {

		String link = cell;
		Pattern catPattern = Pattern.compile("\\{\\{(.*?)\\}\\}", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(cell);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");

			if (temp == null || temp.length == 0) {
				return link;
			}

			if (temp.length > 1) {
				link = temp[1];
			} else {
				link = temp[0];
			}
		}
		return link;
	}

	public String parseLinkCellAge(String cell) {

		String link = cell;

		ArrayList<String> listYear = new ArrayList<String>();
		Pattern catPattern = Pattern.compile("\\{\\{age in years and days(.*?)\\}\\}", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(cell);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");

			if (temp == null || temp.length == 0) {
				return link;
			}

			if (temp.length >= 2) {
				int k = 0;
				for (int i = 0; i < temp.length; i++) {
					if (checkYear(temp[i])) {
						k++;
						listYear.add(temp[i]);
					}
				}
				String[] tableyear = new String[k];
				int m = 0;
				while (m < tableyear.length) {
					for (String element : listYear) {
						tableyear[m] = element;
						m++;
					}

				}
				if (tableyear.length == 2) {
					int age = Integer.parseInt(tableyear[1]) - Integer.parseInt(tableyear[0]);
					String agevar = Integer.toString(age);
					link = agevar;
				}

			} else {
				link = temp[0];
			}
		}
		return link;
	}

	public String parseCurlCell(String cell) {

		String link = cell;
		Pattern catPattern = Pattern.compile("\\{\\{sortname(.*?)\\}\\}", Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(cell);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");

			if (temp == null || temp.length == 0) {
				return link;
			}

			if (temp.length > 1) {
				link = temp[1] + " " + temp[2];
			} else {
				link = temp[0];
			}
		}
		return link;
	}

	public String regexRefCleanup(String cell) {

		Matcher regexMatcher = Pattern.compile("\\{\\{ref(.*?)\\}\\}", Pattern.MULTILINE).matcher(cell);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				cell = regexMatcher.replaceAll("");

			}
		}

		return cell;

	}

	public String regexRefCleanup1(String cell) {

		Matcher regexMatcher = Pattern.compile("<ref(.*?)>(.*?)</ref>", Pattern.MULTILINE).matcher(cell);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				cell = regexMatcher.replaceAll("");

			}
		}

		return cell;

	}

	private static Pattern pipePattern = Pattern.compile("\\|", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern markPattern = Pattern.compile("\\!", Pattern.MULTILINE | Pattern.DOTALL);

	public void cleanUpMatrix(Cell[][] matrix) {

		Pattern brPattern = Pattern.compile("<br\\s*\\/>", Pattern.MULTILINE | Pattern.DOTALL);
		Pattern quotePattern = Pattern.compile("'''", Pattern.MULTILINE | Pattern.DOTALL);
		Pattern quotePattern1 = Pattern.compile("''", Pattern.MULTILINE | Pattern.DOTALL);

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				try {
					if (matrix[i][j].getContent() == "null") {
						continue;
					}

					String varCell = matrix[i][j].getContent();
					varCell = regexRefCleanup1(varCell);
					varCell = markPattern.matcher(varCell).replaceAll("");
					varCell = brPattern.matcher(varCell).replaceAll("");
					varCell = quotePattern.matcher(varCell).replaceAll("");
					varCell = quotePattern1.matcher(varCell).replaceAll("");
					varCell = parseLinkCell(varCell);

					varCell = parseLinkCell1(varCell);
					varCell = parseLinkCellAge(varCell);

					varCell = parseCurlCell(varCell);

					varCell = pipePattern.matcher(varCell).replaceAll("");

					matrix[i][j].setContent(varCell);
				} catch (java.lang.NullPointerException e) {
					// System.out.print("");
				}
			}
		}

	}

	public String removeLastChar(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		return s.substring(0, s.length() - 1);
	}

	public void annotateColumns(Cell[][] matrix) {

		cleanUpMatrix(matrix);
		Cell[] column = new Cell[matrix.length];
		for (int j = 0; j < matrix[0].length; j++) {
			int i = 0;
			column = readColumn(j, matrix);
			boolean allEquals = true;
			String wordShape = null;
			String dataType = null;
			while (i < matrix.length) {
				if (matrix[i][j].getContent().contains("+")) {
					i++;
				}
				if (matrix[i][j].getContent() == "null") {
					i++;
				}
				if ((matrix[i][j].getType() == "header")) {
					i++;
				}

				try {
					String phrase1 = column[i].getContent().trim();
					String phrase2 = column[i + 1].getContent().trim();
					String var1 = wordClassifier.wordShape(column[i].getContent().trim(), 8);
					String var2 = wordClassifier.wordShape(column[i + 1].getContent().trim(), 8);

					if (!wordClassifier.wordShape(var1, 8).equals(wordClassifier.wordShape(var2, 8))) {
						allEquals = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {

				}
				i++;
			}
			if (allEquals) {
				wordShape = wordClassifier.wordShape(column[2].getContent().trim(), 8);
				dataType = wordClassifier.wordShape(column[2].getContent().trim(), 0);
				if ((dataType.equals("ALL-DIGITS")) || (dataType.equals("OTHER"))) {
					if (checkYear(column[2].getContent().trim())) {
						System.out.println("Data type of column " + j + ": " + "Numerical :Year");
					} else if (checkRank(column[2].getContent().trim())
							&& (!column[2].getContent().trim().contains("$"))) {
						System.out.println("Data type of column " + j + ": " + "Numerical :Rank");
					} else if ((column[2].getContent().trim().contains("$"))) {
						System.out.println("Data type of column " + j + ": " + "Numerical :Gross");
					}
				}
				System.out.println("Data type of column " + j + ": " + dataType);
				System.out.println("Word Shape of column " + j + ": " + wordShape);
			} else {
				System.out.println("nothing");
			}

		}
	}

	/*	*//**
			 * Not the right one
			 * 
			 * @param matrix
			 * @param j
			 * @return
			 *//*
			 * public Map<String, Integer> buildHistogram(Cell[][] matrix, int
			 * j) {
			 * 
			 * Cell[] column = readColumn(j, matrix); Map<String, Integer> hm =
			 * new LinkedHashMap<String, Integer>();
			 * 
			 * // HashMap<String,Integer> hm = new HashMap<String, Integer>();
			 * int i = 0; if (matrix[i][j].getContent().contains("+")) { i++; }
			 * if (matrix[i][j].getContent() == "null") { i++; } if
			 * ((matrix[i][j].getType() == "header")) { i++; } for (; i <
			 * column.length; i++) { String wordShape =
			 * wordClassifier.wordShape(column[i].getContent().trim(), 2);
			 * hm.put(" shape " + i + ":" + wordShape + " ",
			 * getNumberTokens(wordShape)); }
			 * 
			 * return hm; }
			 */

	/*
	 * public Pair[] buildHistogram(Cell[][] matrix, int j) { Cell[] column =
	 * readColumn(j, matrix); Pair[] pairArray = new Pair[column.length];
	 * 
	 * int i = 0; if (matrix[i][j].getContent().contains("+")) { i++; } if
	 * (matrix[i][j].getContent() == "null") { i++; } if
	 * ((matrix[i][j].getType() == "header")) { i++; } for (; i < column.length;
	 * i++) { String wordShape =
	 * wordClassifier.wordShape(column[i].getContent().trim(), 2); Pair pair =
	 * new Pair(wordShape, getNumberTokens(wordShape)); pairArray[i] = pair; }
	 * 
	 * return pairArray; }
	 */
	/*	*//**
			 * Not the right one
			 * 
			 * @param matrix
			 * @param j
			 * @return
			 *//*
			 * public ArrayList<String> predicateColumnShape(Cell[][] matrix,
			 * int j) { int count = 0; ArrayList<String> listCount = new
			 * ArrayList<String>(); Map<String, Integer> hm =
			 * buildHistogram(matrix, j); Set<String> set =
			 * buildHistogram(matrix, 1).keySet(); for (String element : set) {
			 * String key = element.toString(); String[] temp =
			 * key.split("\\|"); String wordShape = temp[1]; count =
			 * countHM(wordShape, hm);
			 * 
			 * } return null; }
			 */

	public int getNumberTokens(String s) {
		StringTokenizer st = new StringTokenizer(s);
		// counting tokens
		// System.out.println("Total tokens : " + st.countTokens());
		return st.countTokens();
	}

	/**
	 * The good one
	 * 
	 * @param matrix
	 * @param j
	 * @return
	 */

	public String[] tableShapes(Cell[][] matrix, int j) {
		Cell[] column = readColumn(j, matrix);
		// String[] tableShape = new String[column.length];

		int i = 0;
		if (matrix[i][j].getContent().contains("+")) {
			i++;
		}
		if (matrix[i][j].getContent() == "null") {
			i++;

		}
		if ((matrix[i][j].getType() == "header")) {
			i++;
		}
		String[] tableShape = new String[column.length - i];
		for (int k = 0; k < column.length; k++) {
			try {
				String wordShape = wordClassifier.wordShape(column[i].getContent().trim(), 2);
				tableShape[k] = wordShape;

				i++;
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}

		return tableShape;
	}

	public String[] tableDataType(Cell[][] matrix, int j) {
		Cell[] column = readColumn(j, matrix);
		// String[] tableShape = new String[column.length];

		int i = 0;
		if (matrix[i][j].getContent().contains("+")) {
			i++;
		}
		if (matrix[i][j].getContent() == "null") {
			i++;

		}
		if ((matrix[i][j].getType() == "header")) {
			i++;
		}
		String[] tableShape = new String[column.length - i];
		for (int k = 0; k < column.length; k++) {
			try {
				String wordShape = wordClassifier.wordShape(column[i].getContent().trim(), 0);
				tableShape[k] = wordShape;

				i++;
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}

		return tableShape;
	}

	/**
	 * The good one
	 * 
	 * @param sampled
	 * @param val
	 * @return
	 */
	public int count(String[] sampled, String val) {
		int count = 0;
		for (int i = 0; i < sampled.length; i++) {
			if (sampled[i].equals(val)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * The good one this method will identify the word shape of a given column
	 * 
	 * @param matrix
	 * @param j
	 * @return
	 */
	public ArrayList<String> predicteColumnShape1(Cell[][] matrix, int j) {
		ArrayList<String> listPredictionShape = new ArrayList<String>();
		String[] table = tableShapes(matrix, j);
		Map<String, Integer> hm = new LinkedHashMap<String, Integer>();

		for (int i = 0; i < table.length; i++) {
			String val = table[i];
			int count = count(table, val);
			hm.put(val, count);
		}

		int maxValueInMap = (Collections.max(hm.values())); // This will return
															// max value in the
															// Hashmap
		for (Entry<String, Integer> entry : hm.entrySet()) { // Itrate through
																// hashmap
			if (entry.getValue() == maxValueInMap) {
				// System.out.println(entry.getKey()); // Print the key with max
				// value
				listPredictionShape.add(entry.getKey());
			}
		}
		// if listPredictionShape has more than one shape ,so all shapes word
		// has the same probability
		// Like we said before When we encounter Xx Xx, it's most likely to be a
		// name so likely
		// it's the entity word shape. That's why if we found more than one
		// element in the shape list
		// we're going to choose "Xx Xx"

		ArrayList<String> shapeListnew = new ArrayList<String>();
		if (listPredictionShape.size() > 1) {
			for (String shape : listPredictionShape) {
				if (shape.equals("Xx Xx")) {
					shapeListnew.add(shape);
					listPredictionShape = shapeListnew;
				}
			}
		}
		return listPredictionShape;

	}

	public ArrayList<String> predicteColumnDataType(Cell[][] matrix, int j) {
		ArrayList<String> listPredictionShape = new ArrayList<String>();
		String[] table = tableDataType(matrix, j);
		Map<String, Integer> hm = new LinkedHashMap<String, Integer>();

		for (int i = 0; i < table.length; i++) {
			String val = table[i];
			int count = count(table, val);
			hm.put(val, count);
		}

		int maxValueInMap = (Collections.max(hm.values())); // This will return
															// max value in the
															// Hashmap
		for (Entry<String, Integer> entry : hm.entrySet()) { // Itrate through
																// hashmap
			if (entry.getValue() == maxValueInMap) {
				// System.out.println(entry.getKey()); // Print the key with max
				// value
				listPredictionShape.add(entry.getKey());
			}
		}
		return listPredictionShape;

	}

	/**
	 * not very good, that's why I'm gonna improve it this method tries to
	 * predicate column entity and call reorderMatrix() function
	 * 
	 * @param matrix
	 * @param columnToReorder
	 */
	public Cell[][] predicteEntityColumn(Cell[][] matrix) {
		Cell[] columnMatrix = null;
		cleanUpMatrix(matrix);

		// we're going to reorder our matrix ssi the first column is not our
		// entity. (How to know that this is not our entity, we know that by
		// checking
		// the first column, if it's numerical than the most likely it's not our
		// entity
		// we check also the second column which is in most cases our entity, if
		// the shape word is
		// [Xx Xx] than most likely we have a name, so it could be our entity
		// and we reorder our matrix
		// sinon on retourne our matrix sans la modifier

		ArrayList<String> columnDataType = predicteColumnDataType(matrix, 0);
		ArrayList<String> columnWordShape = predicteColumnShape1(matrix, 0);

		String dataType = "";
		String wordShape = "";

		if (columnDataType.size() == 1) {
			dataType = convertListToString(columnDataType);
			dataType = removeLastChar(dataType);

		}
		if (columnWordShape.size() == 1) {
			wordShape = convertListToString(columnWordShape);
			wordShape = removeLastChar(wordShape);
		}
		if (dataType.equals("ALL-DIGITS")) {
			String dataTypeColumn2 = removeLastChar(convertListToString(predicteColumnDataType(matrix, 1)));
			String wordShapeColumn2 = removeLastChar(convertListToString(predicteColumnShape1(matrix, 1)));
			if ((dataTypeColumn2.equals("OTHER")) && (wordShapeColumn2.equals("Xx Xx"))) {
				columnMatrix = readColumn(0, matrix);
				for (int i = 0; i < matrix.length; i++) {
					matrix[i][0] = matrix[i][1];
				}

				for (int i = 0; i < matrix.length; i++) {
					matrix[i][1] = columnMatrix[i];
				}
				// we might have a person name here so we have to reorder our
				// matrix
				// to replace the first column with the second column
			}
		}

		return matrix;

	}

	/**
	 * returns the index of the first column that has the word shape wanted
	 * 
	 * @param matrix
	 * @param wordShape
	 * @return
	 */
	public int getIndexWordShape(Cell[][] matrix, String wordShape) {
		int j = 0;
		boolean var = false;
		while (j < matrix[0].length) {
			ArrayList<String> shapeColumn = predicteColumnShape1(matrix, j);
			for (String shape : shapeColumn) {
				if (shape.equals(wordShape)) {
					var = true;
					return j;
				} else {
					j++;
				}
			}

		}
		if (!var) {
			System.out.println("No " + wordShape);
			j = -1;
		}

		return j;
	}

	/**
	 * this method converts list to a string
	 * 
	 * @param list
	 * @return
	 */

	public String convertListToString(ArrayList<String> list) {
		String var = "";
		for (String s : list) {
			var = s + "@";
		}
		return var;
	}

	/**
	 * Adding a blank node column to the previous matrix
	 * 
	 * @param matrix
	 * @return
	 */

	public Cell[][] addBlankNode(Cell[][] matrix) {

		Cell[][] matrixnode = new Cell[matrix.length][matrix[0].length + 1];

		for (int i = 0; i < matrixnode.length; i++) {
			Cell blanknode = new Cell("blank node", "http://blanknode/row" + i);
			matrixnode[i][0] = blanknode;
		}

		for (i = 0; i < matrix.length; i++) {
			int k = 1;
			for (int j = 0; j < matrix[0].length; j++) {
				try {
					Cell regularCell = new Cell(matrix[i][j].getType(), matrix[i][j].getContent());
					matrixnode[i][k] = regularCell;
					k++;
				} catch (ArrayIndexOutOfBoundsException e) {

				}

			}
		}

		return matrixnode;
	}

	/**
	 * printing out one single matrix
	 * 
	 * @param matrix
	 */
	public void printOutOneMatrix(Cell[][] matrix) {

		cleanUpMatrix(matrix);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				try {
					if (matrix[i][j].getContent() == "null") {
						continue;
					}
					System.out.print(matrix[i][j].getContent() + " ");
				} catch (java.lang.NullPointerException e) {
					// System.out.print("");
				}
			}
			System.out.print("\n");
		}
	}

	/**
	 * we don't have to reorder the matrix, even we don't have to search for the
	 * column entity We're just adding an empty node column, the advantage of
	 * doing this,for example we do not record in the same fact that <an_actor
	 * in this movie in this year> this is another way to encode every row in
	 * the table with a blank node so that all the facts are expressed
	 * relatively to the node
	 * 
	 * @param matrix
	 * @return
	 */
	public ArrayList<Triple<String, String, String>> produceRDFforBlankNode(Cell[][] matrix) {

		int j = 0;
		int k = 0;
		ArrayList<Triple<String, String, String>> tripleList = new ArrayList<Triple<String, String, String>>();
		String subject = null;
		String predicate = null;
		String object = null;

		cleanUpMatrix(matrix);
		// we don't have to reorder the matrix, even we don't have
		// to search for the column entity
		// We're just added an empty node column, the advantage
		// of doing this,for example we do not record in the same
		// fact that <an_actor in this movie in this year>
		// this is another way to encode every row in the table with a blank
		// node so
		// that all the facts are expressed relatively to the node

		// matrix = predicateEntityColumn(matrix);
		// matrix = reOrderMatrix(matrix);
		int i = getIndexRowHeader(matrix);

		if (i == 0) {
			i++;
			// we have a header in the first row of the matrix
			// we don't have an empty row
			k = checkColspan(matrix, i);
			j = k;
			tripleList = getTriples(subject, object, predicate, matrix, i, j, k);

		} else if (i == 1) {
			i++;
			// in this case we have for the first row {| class=" and "|-
			// so the first row(0) will be empty that's why our headers will be
			// in row (1)
			// and subject row (2)
			while (i < matrix.length) {
				subject = matrix[i][j].getContent().trim();

				try {
					// while (j < matrix[i].length - 1) {
					try {
						for (; j < matrix[i].length - 1; j++) {

							predicate = "has_" + parseLinkCell1(parseCurlCell(matrix[1][j + 1].getContent().trim()));
							predicate = predicatePrefix + predicate;
							object = parseLinkCell1(parseCurlCell(parseLinkCell(matrix[i][j + 1].getContent().trim())));

							object = regexReplaceWhiteSpace(object);
							Triple<String, String, String> tripleRDF = new Triple<String, String, String>(subject,
									predicate, object);
							tripleList.add(tripleRDF);
						}
					} catch (NullPointerException e) {
						// System.out.println("cell is empty");
						// i++;
					}

				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("out of size");
				}
				j = 0;
				i++;
				k = checkColspan(matrix, i);
				j = k;
			}
		}

		else if (i == matrix.length) {

			System.out.println("this table does not have a header !");
			String predicateHeader = "property";
			for (int p = 0; p < matrix[0].length; p++) {
				matrix[0][p].setContent(predicateHeader);
				// System.out.println(p);
			}
			i = 1;
			k = checkColspan(matrix, i);
			j = k;
			tripleList = getTriples(subject, object, predicate, matrix, i, j, k);
		}

		return tripleList;
	}

	/**
	 * this method add a blank node column to the previous matrix and then print
	 * it out
	 * 
	 * @param listMatrix
	 */
	public void printOutRDFTripleBlankNode(ArrayList<Cell[][]> listMatrix) {
		for (Cell[][] matrix : listMatrix) {
			matrix = addBlankNode(matrix);
			ArrayList<Triple<String, String, String>> listTriple = produceRDFforBlankNode(matrix);
			for (Triple<String, String, String> triple : listTriple) {
				System.out.println("RDF Triple:  " + triple);

			}

		}
	}

	public Cell[][] reOrderMatrix(Cell[][] matrix, int column) {

		// we're going to reorder the first column with our wordShape
		// column(entity)
		Cell[] columnMatrix = readColumn(0, matrix);
		for (int i = 0; i < matrix.length; i++) {
			matrix[i][0] = matrix[i][column];
		}

		for (int i = 0; i < matrix.length; i++) {
			matrix[i][column] = columnMatrix[i];
		}
		return matrix;
	}

	/**
	 * predicts entity column the good one
	 * 
	 * @param matrix
	 * @param wordShape
	 * @param i
	 *            to increment the counter of columnShape because our code is
	 *            recursive
	 * @return
	 */

	public Cell[][] predicteEntityColumn1(Cell[][] matrix, String wordShape, int i) {

		cleanUpMatrix(matrix);
		// String[] columnShape ={"Xx","Xx Xx Xx"};
		String[] columnShape = { "Xx Xx Xx", "Xx" };

		// we're going to reorder our matrix if only the first column is not our
		// entity. (How to know that this is not our entity, we know that by
		// searching for [Xx Xx] if we find it then we reorder
		// otherwise we search for [Xx Xx Xx] if OK we reorder
		// otherwise we search for [Xx]

		// [Xx Xx] : we have most likely a name, so it could be our entity
		// and we reorder our matrix
		// otherwise our matrix remain the same

		if (checkDigital(matrix)) {
			System.out.println("This table doesn't have an entity column");
			return matrix;
		}

		int m = getIndexWordShape(matrix, wordShape);

		if (m == -1) {
			if (i == 2) {
				return matrix;
			}

			wordShape = columnShape[i];
			if (i < columnShape.length) {
				i++;
			}
			// System.out.println(wordShape + " Not found !!");
			matrix = predicteEntityColumn1(matrix, wordShape, i);

			return matrix;
		}

		if (m == 0) {
			// our first column is our entity column, so we don't reorder our
			// matrix
			return matrix;
		}

		else {
			matrix = reOrderMatrix(matrix, m);
		}

		return matrix;
	}

	/**
	 * this method will return true if all the columns are numerical
	 */
	public boolean checkDigital(Cell[][] matrix) {

		boolean hasdigit = true;
		for (int j = 0; j < matrix[0].length; j++) {
			ArrayList<String> listshape = predicteColumnShape1(matrix, j);
			for (String shape : listshape) {
				if (!shape.contains("d")) {
					hasdigit = false;
				}
			}
		}

		return hasdigit;
	}
}
