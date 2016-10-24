package ca.ualberta.wikipedia.rdf;

import java.util.ArrayList;
import java.util.Collections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ualberta.wikipedia.rdf.Triple;
import ca.ualberta.wikipedia.rdf.WordShapeClassifier;
import ca.ualberta.wikipedia.tablereader.Cell;

public class GenerateRdf {

	int i = 0;
	public WordShapeClassifier wordClassifier = new WordShapeClassifier();

	public String wikiIdURI = "http://wikipedia/";
	public String predicatePrefix = "http://myprefix/";

	String wikiText;

	public ArrayList<Triple<String, String, String>> produceRDF(Cell[][] matrix) {

		int j = 0;
		int k = 0;
		ArrayList<Triple<String, String, String>> tripleList = new ArrayList<Triple<String, String, String>>();
		String subject = null;
		String predicate = null;
		String object = null;

		matrix = deleteEmptyColumn(matrix);
		cleanUpMatrix(matrix);
		if (checkDigital(matrix)) {
			System.out.println("No triple");
			return tripleList;
		}
		matrix = predicteEntityColumn1(matrix, "Xx Xx", 0);

		int i = getIndexRowHeader(matrix);

		if (i == 0) {
			i++;
			// we have a header in the first row of the matrix
			// we don't have an empty row
			k = checkColspan(matrix, i);
			j = k;
			tripleList = getTriples(subject, object, predicate, matrix, i, j, k);

		}

		else if (i == -1) {

			System.out.println("this table does not have a header !");
			System.out.println("No triple");
			String predicateHeader = "property";
			for (int p = 0; p < matrix[0].length; p++) {
				matrix[0][p].setContent(predicateHeader);

			}
			i = 1;
			k = checkColspan(matrix, i);
			j = k;

		}

		else {
			i++;
			// in this case we have for the first row {| class=" dnd "|-
			// so the first row(0) will be empty that's why our headers will be
			// in row (1)
			// and subject row (2)
			int n = i - 1;
			while (i < matrix.length) {
				subject = matrix[i][j].getContent().trim();
				subject = parseCurlCell(subject);
				subject = parseLinkCell1(subject);
				subject = regexReplaceWhiteSpace(subject);
				subject = wikiIdURI + subject;
				try {

					try {

						for (; j < matrix[i].length - 1; j++) {

							predicate = "has_" + parseLinkCell1(parseCurlCell(matrix[n][j + 1].getContent().trim()));
							predicate = predicatePrefix + predicate;
							object = parseLinkCell1(parseCurlCell(parseLinkCell(matrix[i][j + 1].getContent().trim())));
							object = regexReplaceWhiteSpace(object);

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
				for (; j < matrix[i].length - 1; j++) {
					try {

						predicate = "has_" + parseLinkCell1(parseCurlCell(matrix[0][j + 1].getContent()));
						predicate = predicatePrefix + predicate;
						object = parseLinkCell1(parseCurlCell(matrix[i][j + 1].getContent()));
						object = regexReplaceWhiteSpace(object);
						if (!wordClassifier.wordShape(object.trim(), 2).contains("d")) {
							String[] temp = object.split("\\s*,\\s*");
							for (int m = 0; m < temp.length; m++) {

								object = temp[m];
								Triple<String, String, String> tripleRDF = new Triple<String, String, String>(subject,
										predicate, object);
								tripleList.add(tripleRDF);
							}

						} else {
							Triple<String, String, String> tripleRDF = new Triple<String, String, String>(subject,
									predicate, object);
							tripleList.add(tripleRDF);
						}

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

	/**
	 * tells us if a table has more than one header row
	 * 
	 * @param matrix
	 * @return
	 */
	public ArrayList<Integer> getIndexAllHeaders(Cell[][] matrix) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < matrix.length; i++) {
			if (tableHaveHeader(matrix, i)) {
				list.add(i);
			}
		}
		return list;
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

	/**
	 * this method returns the index of the header row
	 * 
	 * @param matrix
	 * @return
	 */
	public int getIndexRowHeader(Cell[][] matrix) {
		int i = 0;
		int k = 0;
		try {
			while (i < matrix.length) {
				while (tableHaveHeader(matrix, i)) {
					return i;

				}
				i++;
				if (i == matrix.length) {
					System.out.println("Table with no headers");
					k = -1;
				}

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Index out of bounds");

		}
		i = k;
		return i;
	}

	public Cell[] readColumn(int j, Cell[][] matrix) {

		int row = matrix.length;

		Cell[] columnMatrix = new Cell[row];

		for (int i = 0; i < matrix.length; i++) {
			try {
				Cell cell = new Cell(matrix[i][j].getType(), matrix[i][j].getContent(),
						matrix[i][j].isCellHasRowspan());
				columnMatrix[i] = cell;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("it runs out of bounds: read column");
			}

		}

		return columnMatrix;
	}

	public Cell[] readRow(int i, Cell[][] matrix) {

		int column = matrix[0].length;

		Cell[] rowMatrix = new Cell[column];

		for (int j = 0; j < matrix[0].length; j++) {
			try {
				Cell cell = new Cell(matrix[i][j].getType(), matrix[i][j].getContent());
				rowMatrix[j] = cell;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("it runs out of bounds: read column");
			}
		}

		return rowMatrix;
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

		// we're going to reorder our matrix only if the first column is not our
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

	/**
	 * deletes one given row it's not good!!!
	 * 
	 * @param matrix
	 * @param col
	 * @return
	 */
	public Cell[][] deleteRow(Cell[][] matrix, int row) {
		Cell[][] matrix1 = new Cell[matrix.length - 1][matrix[0].length];
		if (matrix != null && matrix.length > row && matrix[0].length > 0) {

			for (int i = 0; i < matrix.length; i++) {
				int newColIdx = 0;
				for (int j = 0; j < matrix[i].length; j++) {
					if (j != row) {
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
		String digitRegex = "[J-j]anuary|[F-f]ebruary|[M-m]ars|[A-a]pril|[M-m]ay|[J-j]une|[J-j]uly|[A-a]ugust|[S-s]eptember|[O-o]ctober|[N-n]ovember|[D-d]ecember|JANUARY|FEBRUARY|MARS|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER";
		Pattern pattern = Pattern.compile(digitRegex);

		return pattern.matcher(cell).find();

	}

	/**
	 * checks if the cells of the column has month data
	 * 
	 * @param column
	 * @return
	 */
	public boolean checkColumnMonth(Cell[] column) {
		int positive = 0;
		int negative = 0;
		boolean hasMonth = false;
		ArrayList<Boolean> listboolean = new ArrayList<Boolean>();
		for (int i = 0; i < column.length; i++) {
			listboolean.add(checkMonth(column[i].getContent()));
		}

		for (boolean result : listboolean) {
			if (result) {
				positive++;
			} else {
				negative++;
			}
		}

		if (positive > negative) {
			hasMonth = true;
		} else {
			hasMonth = false;
		}
		return hasMonth;
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
	 * this method gives us informations about repeated headers,so we will be
	 * able to divide our matrix
	 * 
	 * @param matrix
	 * @return
	 */

	public ArrayList<Integer> getIndexRepeatedHeaders(Cell[][] matrix) {
		ArrayList<Integer> listIndex = new ArrayList<Integer>();
		int j = 0;
		int i = getIndexRowHeader(matrix);
		if (i != -1) {
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
					System.out.println("");
					j++;

				}
			}
		}

		return listIndex;
	}

	/**
	 * Whenever we have duplicated headers we' re going to divide the matrix
	 * mais il faut generaliser car cette methode divise une matrice en deux
	 * seulement car on a suppose que la matrice pourrait avoir seulement une
	 * seule fois la duplication.
	 * 
	 * @param matrix
	 * @return
	 */

	public ArrayList<Cell[][]> divideMatrix(Cell[][] matrix) {

		ArrayList<Cell[][]> listMatrix = new ArrayList<Cell[][]>();
		ArrayList<Integer> listIndex = getIndexRepeatedHeaders(matrix);
		int n = 0;
		int p = 0;
		if (!listIndex.isEmpty()) {
			Cell[][] matrix1 = new Cell[matrix.length][listIndex.get(0) + 1];
			Cell[][] matrix2 = new Cell[matrix.length][listIndex.get(0) + 1];
			for (int m = 0; m < listIndex.size(); m++) {

				int k = listIndex.get(m);

				for (int i = 0; i < matrix.length; i++) {
					for (int j = n; j < k + 1; j++) {
						Cell cell1 = new Cell(matrix[i][j].getType(), matrix[i][j].getContent());
						matrix1[i][j - p] = cell1;

					}
				}
				listMatrix.add(matrix1);

				n = listIndex.get(m) + 1;
				matrix1 = new Cell[matrix.length][listIndex.get(0) + 1];
				p = n;
			}

			for (int i = 0; i < matrix.length; i++) {
				for (int j = n; j < matrix[0].length; j++) {
					try {
						String var1 = matrix[i][j].getType();
						String var2 = matrix[i][j].getContent();
						Cell cell2 = new Cell(var1, var2);
						matrix2[i][j - n] = cell2;
					} catch (ArrayIndexOutOfBoundsException e) {

					}

				}
			}
			listMatrix.add(matrix2);
		} else {
			System.out.println("we don't have duplicated headers, table cannot be divided");
		}
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

	public String parseFlag(String cell) {

		String link = cell;
		Pattern catPattern = Pattern.compile("\\{\\{flagcountry\\|(.*?)\\}\\}", Pattern.MULTILINE);
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

	public String regexRefCleanup2(String cell) {

		Matcher regexMatcher = Pattern.compile("<ref(.*?)\\/>", Pattern.MULTILINE).matcher(cell);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				cell = regexMatcher.replaceAll("");

			}
		}

		return cell;

	}

	public String regexRefCleanup3(String cell) {

		Matcher regexMatcher = Pattern.compile("\\{\\{ref(.*?)\\}\\}", Pattern.MULTILINE).matcher(cell);

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
		Pattern centerPattern = Pattern.compile("<center>", Pattern.MULTILINE | Pattern.DOTALL);
		Pattern smallPattern = Pattern.compile("<small>", Pattern.MULTILINE | Pattern.DOTALL);
		Pattern smallPattern1 = Pattern.compile("</small>", Pattern.MULTILINE | Pattern.DOTALL);
		Pattern ClassCleanupPatternWithNoQuotes = Pattern.compile("\\bclass\\s*=\\s*[A-Za-z]*",
				Pattern.MULTILINE | Pattern.DOTALL);
		Pattern bgcolorPatternWithNoQuotes = Pattern.compile("\\bbgcolor\\s*=\\s*[#A-Za-z0123456789]*",
				Pattern.MULTILINE | Pattern.DOTALL);

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				try {
					if (matrix[i][j].getContent() == "null") {
						continue;
					}

					String varCell = matrix[i][j].getContent();

					varCell = regexRefCleanup1(varCell);
					varCell = regexRefCleanup2(varCell);
					varCell = regexRefCleanup3(varCell);
					varCell = markPattern.matcher(varCell).replaceAll("");
					varCell = brPattern.matcher(varCell).replaceAll("");
					varCell = quotePattern.matcher(varCell).replaceAll("");
					varCell = quotePattern1.matcher(varCell).replaceAll("");
					varCell = centerPattern.matcher(varCell).replaceAll("");
					varCell = smallPattern1.matcher(varCell).replaceAll("");
					varCell = smallPattern.matcher(varCell).replaceAll("");
					varCell = ClassCleanupPatternWithNoQuotes.matcher(varCell).replaceAll("");
					varCell = bgcolorPatternWithNoQuotes.matcher(varCell).replaceAll("");
					varCell = parseFlag(varCell);
					varCell = parseLinkCell(varCell);
					varCell = parseCurlCell(varCell);
					varCell = parseLinkCell1(varCell);
					varCell = parseLinkCellAge(varCell);
					varCell = varCell.trim();

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

	public int getNumberTokens(String s) {
		StringTokenizer st = new StringTokenizer(s);
		// counting tokens

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

				listPredictionShape.add(entry.getKey());
			}
		}

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

				listPredictionShape.add(entry.getKey());
			}
		}
		return listPredictionShape;

	}

	public Cell[][] predicteEntityColumn(Cell[][] matrix) {
		Cell[] columnMatrix = null;
		cleanUpMatrix(matrix);

		// we're going to reorder our matrix if the first column is not our
		// entity. (How to know that this is not our entity, we know that by
		// checking
		// the first column, if it's numerical than more likely it's not our
		// entity
		// we check also the second column which is in most cases our entity, if
		// the shape word is
		// [Xx Xx] than more likely we have a name, so it could be our entity
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
			Cell[] column = readColumn(j, matrix);
			for (String shape : shapeColumn) {
				if (shape.equals(wordShape)) {
					var = true;
					// here we have to check if the column values are not month.
					boolean check = checkColumnMonth(column);
					if (!checkColumnMonth(column)) {
						return j;
					}

				} else {
					continue;
				}
			}
			j++;

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
		// if there's empty column we delete it
		// matrix = deleteEmptyColumn(matrix);
		cleanUpMatrix(matrix);

		// we don't have to reorder the matrix, even we don't have
		// to search for the column entity
		// We're just added an empty node column, the advantage
		// of doing this,for example we do not record in the same
		// fact that <an_actor in this movie in this year>
		// this is another way to encode every row in the table with a blank
		// node so
		// that all the facts are expressed relatively to the node

		int i = getIndexRowHeader(matrix);

		if (checkDigital(matrix)) {
			System.out.println("No triple");
			return tripleList;
		}

		if (i == 0) {
			i++;
			// we have a header in the first row of the matrix
			// we don't have an empty row
			k = checkColspan(matrix, i);
			j = k;
			tripleList = getTriples(subject, object, predicate, matrix, i, j, k);

		}

		else if (i == -1) {

			System.out.println("this table does not have a header !");
			System.out.println("No triple");

			String predicateHeader = "property";
			for (int p = 0; p < matrix[0].length; p++) {
				matrix[0][p].setContent(predicateHeader);
				// System.out.println(p);
			}
			i = 1;
			k = checkColspan(matrix, i);
			j = k;
			// tripleList = getTriples(subject, object, predicate, matrix, i, j,
			// k);
		}

		else {
			i++;
			// in this case we have for the first row {| class=" and "|-
			// so the first row(0) will be empty that's why our headers will be
			// in row (1)
			// and subject row (2)
			int n = i - 1;
			while (i < matrix.length) {
				subject = matrix[i][j].getContent().trim();

				try {
					// while (j < matrix[i].length - 1) {
					try {
						for (; j < matrix[i].length - 1; j++) {

							predicate = "has_" + parseLinkCell1(parseCurlCell(matrix[n][j + 1].getContent().trim()));
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
		if (column < matrix[0].length) {
			Cell[] columnMatrix = readColumn(0, matrix);
			for (int i = 0; i < matrix.length; i++) {
				matrix[i][0] = matrix[i][column];
			}

			for (int i = 0; i < matrix.length; i++) {
				matrix[i][column] = columnMatrix[i];
			}
		}

		else {
			System.out.println("There is no subject column !!! reorder not possible");
		}
		return matrix;
	}

	/**
	 * predicts the subject column the good one
	 * 
	 * @param matrix
	 * @param wordShape
	 * @param i
	 *            to increment the counter of columnShape because our code is
	 *            recursive
	 * @return
	 */

	public Cell[][] predicteEntityColumn1(Cell[][] matrix, String wordShape, int i) {

		int numberOfColumns = matrix[0].length + 1;
		cleanUpMatrix(matrix);
		String[] columnShape = { "Xx Xx Xx", "Xx" };

		// we're going to reorder our matrix only if the first column is not our
		// subject column. (How to know that ? we know that by
		// searching for [Xx Xx] if we find it then we reorder
		// otherwise we search for [Xx Xx Xx] if OK we reorder
		// otherwise we search for [Xx]

		// [Xx Xx] : we have likely a name, so it could be our subject column
		// and we reorder our matrix
		// otherwise our matrix remain the same

		if (checkDigital(matrix)) {
			System.out.println("This table doesn't have a key column");
			return matrix;
		}

		int m = getIndexWordShape(matrix, wordShape);

		if (m == -1) {
			if (i == 2) {
				// if there is no "Xx Xx" and no "Xx Xx Xx" and no "Xx"
				// we're going to reorder the first column with the first non
				// numerical column
				// if the first column is non numerical so it's our key column
				// System.out.println("This table doesn't have a key column");
				m = getIndexNonNumericalColumn(matrix);
				Cell[] column = readColumn(m, matrix);
				if (checkColumnMonth(column)) {
					m++;
				}
				if (!checkuniqueValue(matrix, m)) {
					m++;
				}

				m = getNonNumericalWordShape(matrix, m);

				matrix = reOrderMatrix(matrix, m);
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

		if (m == 0 && checkuniqueValue(matrix, m)) {
			// our first column is our subject column, so we don't reorder our
			// matrix
			return matrix;
		}

		else {

			// if the number of columns is even, so our subject column must be
			// the most left
			// so if we have 4 columns our subject column should be in the first
			// or second column
			// pair
			// even
			boolean uni = checkuniqueValue(matrix, m);
			if (numberOfColumns == 4) {
				if (m <= 1 && checkuniqueValue(matrix, m)) {
					matrix = reOrderMatrix(matrix, m);
				} else {
					// search for the other word shape
					// search for the other word shape
					if (i == 2) {
						// if there is no "Xx Xx" and no "Xx Xx Xx" and no "Xx"
						// we're going to reorder the first column with the
						// first non
						// numerical column
						// if the first column is non numerical so it's our key
						// column
						// System.out.println("This table doesn't have a key
						// column");
						m = getIndexNonNumericalColumn(matrix);
						Cell[] column = readColumn(m, matrix);
						if (checkColumnMonth(column)) {
							m++;
						}
						if (!checkuniqueValue(matrix, m)) {
							m++;
						}

						m = getNonNumericalWordShape(matrix, m);
						matrix = reOrderMatrix(matrix, m);
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

			} else {
				boolean var = checkuniqueValue(matrix, m);
				if (m <= 1 && checkuniqueValue(matrix, m)) {
					matrix = reOrderMatrix(matrix, m);
				} else {
					// search for the other word shape
					// search for the other word shape
					if (i == 2) {
						// if there is no "Xx Xx" and no "Xx Xx Xx" and no "Xx"
						// we're going to reorder the first column with the
						// first non
						// numerical column
						// if the first column is non numerical so it's our key
						// column
						// System.out.println("This table doesn't have a key
						// column");
						m = getIndexNonNumericalColumn(matrix);
						Cell[] column = readColumn(m, matrix);
						if (checkColumnMonth(column)) {
							m++;
						}
						if (!checkuniqueValue(matrix, m)) {
							m++;
						}

						m = getNonNumericalWordShape(matrix, m);
						matrix = reOrderMatrix(matrix, m);
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
			}

		}
		return matrix;
	}

	/**
	 * 
	 * predicts subject column without checking weather the column has unique
	 * value.
	 * 
	 * @param matrix
	 * @param wordShape
	 * @param i
	 * @return
	 */
	public Cell[][] predicteEntityColumnWithoutCheckingUniqueValue(Cell[][] matrix, String wordShape, int i) {

		int numberOfColumns = matrix[0].length + 1;
		cleanUpMatrix(matrix);

		String[] columnShape = { "Xx Xx Xx", "Xx" };

		// we're going to reorder our matrix if only the first column is not our
		// entity. (How to know that this is not our entity, we know that by
		// searching for [Xx Xx] if we find it then we reorder
		// otherwise we search for [Xx Xx Xx] if OK we reorder
		// otherwise we search for [Xx]

		// [Xx Xx] : we have likely a name, so it could be our entity
		// and we reorder our matrix
		// otherwise our matrix remain the same

		if (checkDigital(matrix)) {
			System.out.println("This table doesn't have a key column");
			return matrix;
		}

		int m = getIndexWordShape(matrix, wordShape);

		if (m == -1) {
			if (i == 2) {
				// if there is no "Xx Xx" and no "Xx Xx Xx" and no "Xx"
				// we're going to reorder the first column with the first non
				// numerical column
				// if the first column is non numerical so it's our key column
				// System.out.println("This table doesn't have a key column");
				m = getIndexNonNumericalColumn(matrix);
				Cell[] column = readColumn(m, matrix);
				if (checkColumnMonth(column)) {
					m++;
				}
				matrix = reOrderMatrix(matrix, m);
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

			// if the number of columns is even, so our subject column must be
			// the most left
			// so if we have 4 columns our subject column should be in the first
			// or second column
			// pair
			// even
			if (numberOfColumns == 4) {
				if (m <= 1) {
					matrix = reOrderMatrix(matrix, m);
				} else {
					// search for the other word shape
					// search for the other word shape
					if (i == 2) {
						// if there is no "Xx Xx" and no "Xx Xx Xx" and no "Xx"
						// we're going to reorder the first column with the
						// first non
						// numerical column
						// if the first column is non numerical so it's our key
						// column
						// System.out.println("This table doesn't have a key
						// column");
						m = getIndexNonNumericalColumn(matrix);
						Cell[] column = readColumn(m, matrix);
						if (checkColumnMonth(column)) {
							m++;
						}
						matrix = reOrderMatrix(matrix, m);
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

			} else {
				if (m <= 1) {
					matrix = reOrderMatrix(matrix, m);
				} else {
					// search for the other word shape
					// search for the other word shape
					if (i == 2) {
						// if there is no "Xx Xx" and no "Xx Xx Xx" and no "Xx"
						// we're going to reorder the first column with the
						// first non
						// numerical column
						// if the first column is non numerical so it's our key
						// column
						// System.out.println("This table doesn't have a key
						// column");
						m = getIndexNonNumericalColumn(matrix);
						Cell[] column = readColumn(m, matrix);
						if (checkColumnMonth(column)) {
							m++;
						}
						matrix = reOrderMatrix(matrix, m);
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
			}

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
		if (hasdigit) {
			System.out.println("No column key!!!");
		}
		return hasdigit;
	}

	/**
	 * this method checks if all cell of column j are unique I'm not using this
	 * method for instance
	 * 
	 * @param matrix
	 * @param j
	 * @return
	 */
	public boolean checkuniqueValue(Cell[][] matrix, int j) {
		Cell[] column = readColumn(j, matrix);
		boolean empty = true;
		for (int i = 0; i < column.length; i++) {
			if (column[i] != null) {
				empty = false;
				break;
			}
		}
		boolean isUnique = true;
		int i = getIndexRowHeader(matrix) + 1;
		if (i != -1 && !empty) {
			for (; i < column.length - 1; i++) {
				for (int k = i + 1; k < column.length; k++) {
					if ((column[i].getContent().equals(column[k].getContent()) && (column[i].isCellHasRowspan() == true)
							&& (column[k].isCellHasRowspan()) == true)) {
						continue;
					} else if (column[i].getContent().equals(column[k].getContent())
							&& (column[i].isCellHasRowspan() == false || column[i].isCellHasRowspan() == true)
							&& (column[k].isCellHasRowspan()) == false) {
						isUnique = false;
						return isUnique;
					}
				}
			}
		} else {
			System.out.println("we cannot read the column to check if it has unique value");
		}
		return isUnique;

	}

	public boolean checkuniqueValue1(Cell[][] matrix, int j) {
		Cell[] column = readColumn(j, matrix);
		int i = getIndexRowHeader(matrix) + 1;
		if (i != -1) {

		}

		return false;
	}

	/**
	 * returns the index of the first non numerical column in the matrix
	 * 
	 * @param matrix
	 * @return
	 */

	public int getIndexNonNumericalColumn(Cell[][] matrix) {

		int j = 0;
		boolean var = false;
		while (j < matrix[0].length) {
			ArrayList<String> shapeColumn = predicteColumnShape1(matrix, j);
			for (String shape : shapeColumn) {
				if (!shape.contains("d")) {
					var = true;
					return j;
				} else {
					continue;
				}
			}
			j++;

		}
		if (!var) {
			System.out.println("No ");
			j = -1;
		}

		return j;
	}

	public ArrayList<Integer> getIndexEmptyColumn(Cell[][] matrix) {
		cleanUpMatrix(matrix);

		int k = 0;
		ArrayList<Integer> listIndex = new ArrayList<Integer>();
		int i = getIndexRowHeader(matrix) + 1;
		boolean var = true;
		int var2 = matrix.length;
		for (int j = 0; j < matrix[0].length; j++) {
			Cell[] column = readColumn(j, matrix);
			for (; i < column.length; i++) {
				String var5 = column[i].getContent();
				if (column[i].getContent().equals("")) {
					k++;
					var = true;
				} else {
					var = false;
					break;
				}

			}
			if ((k == (matrix.length - getIndexRowHeader(matrix) - 1)) && (var)) {
				listIndex.add(j);
			} else {
				System.out.println("Column " + j + "is not empty");
			}
			i = getIndexRowHeader(matrix) + 1;

		}

		return listIndex;
	}

	/**
	 * We're going to delete empty rows in case there is misuse of wikimarkup
	 * like |- |- consecutively, in this case we will have
	 * emptyCell("emptycell","null") In most cases, there is not empty rows like
	 * ""
	 * 
	 * @param matrix
	 * @return
	 */
	public ArrayList<Integer> getIndexEmptyRow(Cell[][] matrix) {

		cleanUpMatrix(matrix);
		int k = 0;
		ArrayList<Integer> listIndex = new ArrayList<Integer>();

		boolean var = true;
		int var2 = matrix.length;
		for (int i = 0; i < matrix.length; i++) {
			Cell[] row = readRow(i, matrix);
			for (int j = 0; j < row.length; j++) {

				if (row[j].getContent().equals("null")) {
					k++;
					var = true;
				}

				else {
					var = false;
					break;
				}

			}
			if ((k == matrix[0].length) && (var)) {
				listIndex.add(i);
			} else {
				System.out.println("Row " + i + "is not empty");
			}

		}

		return listIndex;
	}

	/**
	 * deletes empty columns
	 * 
	 * @param matrix
	 * @return
	 */
	public Cell[][] deleteEmptyColumn(Cell[][] matrix) {
		ArrayList<Integer> list = getIndexEmptyColumn(matrix);

		if (list != null) {
			int k = 0;
			for (int index : list) {
				if (k == 0) {
					matrix = deleteColumn(matrix, index);
					k++;
				} else {
					matrix = deleteColumn(matrix, index - 1);
				}

			}
			int var = matrix[0].length;

			if (matrix[0].length == 1) {
				System.out.println("The matrix has 1 column, no RDF triples !!");
				Cell emptyCell = new Cell("emptyCell", "null");
				for (int m = 0; m < matrix.length; m++) {
					for (int n = 0; n < matrix[0].length; n++) {
						if (matrix[m][n] == null) {
							matrix[m][n] = emptyCell;
						}
					}
				}
			}

			// if the word shape is ":0" then this column is empty
			// therefore we delete it
			int m = getIndexWordShape(matrix, ":0");
			if (m != -1) {
				matrix = deleteColumn(matrix, m);
			}
		}
		return matrix;
	}

	public Cell[][] deleteEmptyRow(Cell[][] matrix) {
		ArrayList<Integer> list = getIndexEmptyRow(matrix);

		if (list != null) {
			for (int index : list) {

				matrix = deleteRow(matrix, index);
			}
		}

		return null;
	}

	/**
	 * this methods returns matrix's headers
	 * 
	 * @param matrix
	 * @return
	 */
	public ArrayList<String> getHeaders(Cell[][] matrix) {
		ArrayList<String> headers = new ArrayList<String>();
		int i = getIndexRowHeader(matrix);
		if (i != -1) {
			for (int j = 0; j < matrix[0].length; j++) {
				headers.add(matrix[i][j].getContent());
			}
		} else {
			System.out.println("No headers");
			return headers;
		}
		return headers;

	}

	public int getNonNumericalWordShape(Cell[][] matrix, int m) {
		while (m < matrix[0].length) {
			boolean notdigits = false;
			ArrayList<String> words = predicteColumnShape1(matrix, m);
			for (String word : words) {
				if (word.contains("d")) {
					m++;

				} else {
					notdigits = true;
					break;
				}

			}
			if (notdigits) {
				break;
			}
		}

		if (m >= matrix[0].length) {
			m = 0;
			ArrayList<String> words1 = predicteColumnShape1(matrix, m);
			for (String word : words1) {
				if (word.contains("d")) {
					m++;

				}
			}
		}

		return m;
	}

}
