package ca.ualberta.wikipedia.tablereader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaTableParser {

	// ArrayList<String> rows = new ArrayList<String>();
	ArrayList<String> headerAndRow = new ArrayList<String>();

	int column = 0;
	int row = 0;
	String[][] matrix = new String[row][column];

	private static Pattern colspanCleanupPattern = Pattern.compile("colspan=\"\\d+\"",
			Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern rowspanCleanupPattern = Pattern.compile("rowspan=\"\\d+\"",
			Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern classCleanupPattern = Pattern.compile("class=\"(.*?)\"", Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern attributeCleanupPattern = Pattern.compile("(.*?)=\"(.*?)\"",
			Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern styleCleanupPattern = Pattern.compile("style=\"(.*?)\"", Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern alignCleanupPattern = Pattern.compile("align=\"(.*?)\"", Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern widthCleanupPattern = Pattern.compile("width=\"(.*?)\"", Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern scopeCleanupPattern = Pattern.compile("scope=\"(.*?)\"", Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern pipePattern = Pattern.compile("\\|", Pattern.MULTILINE | Pattern.DOTALL);

	public ArrayList<String> breakRows(String table) {

		ArrayList<String> rows = new ArrayList<String>();

		int startRow = 2;
		int startPos = 2;
		int bracketCount = 0;
		while (startPos < table.length() - 1) {
			String text = table.substring(startPos, startPos + 2);
			switch (text) {
			case "|-":
				if (bracketCount == 0) {
					// String row = table.substring(startRow, startPos +2);
					String row = table.substring(startRow, startPos + 2);
					rows.add(row);
					startRow = startPos + 2;
				}
				startPos++;
				break;
			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				int bracketindex = table.indexOf("|}", startPos);
				int tablelength = table.length() - 3;
				String rowdelimiter = table.substring(startPos - 3, startPos - 1);
				if (bracketindex == tablelength) {
					if (!rowdelimiter.equalsIgnoreCase("|-")) {
						String row2 = table.substring(startRow, startPos + 2);
						rows.add(row2);
					}
				}
				bracketCount--;
				startPos++;
				break;

			default:
				startPos++;
			}

		}
		return rows;

	}

	public int countRows(String table) {

		ArrayList<String> rows = new ArrayList<String>();
		rows = breakRows(table);
		return rows.size();
	}

	/**
	 * count how many columns the table has
	 * 
	 * @param headerRow
	 * @return
	 */

	public int countColumns(String headerRow) {

		ArrayList<String> headerCells = new ArrayList<String>();

		Matcher regexMatcher = classCleanupPattern.matcher(headerRow);

		if ((translateHeaderRow2(headerRow))) {
			headerCells = translateHeaderCell2(headerRow);
		}

		else if ((!translateHeaderRow2(headerRow)) && (regexMatcher.find())) {
			headerCells = translateHeaderCell1(headerRow);
		} else if ((!translateHeaderRow2(headerRow)) && (!regexMatcher.find())) {
			headerCells = translateHeaderCell3(headerRow);
		}
		int columnCount = 0;

		Iterator<String> iter = headerCells.iterator();
		while (iter.hasNext()) {
			String varHeaderCell = iter.next();
			if (varHeaderCell.contains("colspan")) {
				String colspan = regexColspan(varHeaderCell);
				if (colspan == null) {
					colspan = regexColspanWithNoQuotes(varHeaderCell);
				}
				int digit = Integer.parseInt(extractDigits(colspan));
				columnCount = columnCount + digit;
			} else {
				columnCount = columnCount + 1;
			}
		}
		return columnCount;
	}

	/**
	 * Creates the structure of table as an array of array
	 * 
	 * @param table
	 */
	public String[][] createMatrix(String table) {

		ArrayList<String> rows = new ArrayList<String>();
		int row = countRows(table);
		rows = breakRows(table);
		String headerRow = null;
		Iterator<String> iter = rows.iterator();

		while (iter.hasNext()) {
			String varrow = iter.next();
			if (translateHeaderRow(varrow)) {
				headerRow = varrow;
				break;
			}

		}
		int column = countColumns(headerRow);

		String[][] matrix = new String[row][column];

		return matrix;
	}

	/**
	 * check if cells in this normal row are separated by "||"
	 * 
	 * @param row
	 * @return
	 */

	public boolean translateNormalRow1(String normalrow) {
		int startPos = 1;
		int bracketCount = 0;
		boolean var = false;
		while (startPos < normalrow.length() - 1) {
			String text = normalrow.substring(startPos, startPos + 2);
			switch (text) {
			case "||":
				if (bracketCount == 0) {
					var = true;
				}

				startPos++;
				break;
			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			default:
				startPos++;

			}
			if (var == true) {
				break;
			}
		}

		if (var == true)
			return true;
		else
			return false;

	}

	/**
	 * check if cells in this normal row are separated by "|"
	 * 
	 * @param row
	 * @return
	 */

	public boolean translateNormalRow2(String normalrow) {
		if (!translateNormalRow1(normalrow))
			return true;
		else
			return false;
	}

	/**
	 * The good one ,Method that splits the row into cells and checks if there
	 * is nested tables. the cells here are are separated by "||" we didn't
	 * parse each cell,this will be done by parseCell()
	 * 
	 * @param row
	 * @return
	 */

	public ArrayList<String> translateCell1(String row) {

		ArrayList<String> cells = new ArrayList<String>();
		int startRow = 2;
		int startPos = 2;
		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "||":
				if (bracketCount == 0) {
					String cell = row.substring(startRow, startPos - 1);// startPos-1
					cells.add(cell);
					startRow = startPos + 2;
				}
				startPos++;
				break;
			case "{|":

				bracketCount++;
				startPos++;
				break;

			case "|}":
				int bracketindex = row.indexOf("|}", startPos);
				int tablelength = row.length() - 3;
				if (bracketindex == tablelength) {

					String cell2 = row.substring(startRow, startPos - 1);
					cells.add(cell2);

				}
				bracketCount--;
				startPos++;
				break;

			case "|-":
				if (bracketCount == 0) {
					String cell = row.substring(startRow, startPos - 1);
					cells.add(cell);
					startRow = startPos + 2;
				}

				startPos++;
				break;

			default:
				startPos++;

			}

		}
		return cells;
	}

	/**
	 * the good one Method that splits the row into cells and check if there is
	 * nested tables. the cells here are separated by "| " we didn't parse each
	 * cell,this will be done by parseCell()
	 * 
	 * @param row
	 * @return
	 */

	public ArrayList<String> translateCell2(String row) {

		// Set<String> cells = new HashSet<String>();
		ArrayList<String> cells = new ArrayList<String>();
		int startRow = 2;
		int startPos = 2;
		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "\n|":
				// if (row.startsWith("|")){
				if (bracketCount == 0) {
					String cell = row.substring(startRow, startPos);
					cells.add(cell);
					startRow = startPos + 2;
				}
				startPos++;
				break;
			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			/*
			 * case "|-": if (bracketCount == 0) {String cell =
			 * row.substring(startRow,startPos -1); cells.add(cell); startRow =
			 * startPos + 2; }
			 * 
			 * startPos++; break;
			 */

			default:
				startPos++;

			}

		}
		return cells;
	}

	/**
	 * Method that extracts colspan = "?" to have the digit afterwards. It does
	 * not handle colspan =10 without quotes. should mention that to Matteo
	 * 
	 * @param colspan
	 */

	public String regexColspan(String colspan) {
		Pattern checkRegex = Pattern.compile("colspan=\"\\d+\"", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher regexMatcher = checkRegex.matcher(colspan);
		String var = null;
		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {

				var = regexMatcher.group(0);

			}
		}
		return var;
	}

	public String regexColspanWithNoQuotes(String colspan) {
		Pattern checkRegex = Pattern.compile("colspan=(\\d)+", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher regexMatcher = checkRegex.matcher(colspan);
		String var = null;
		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {

				var = regexMatcher.group(0);

			}
		}
		return var;
	}

	/**
	 * Method that extracts rowspan = "?" to have the digit afterwards
	 * 
	 * @param rowspan
	 * @return
	 */
	public String regexRowspanWithNoQuotes(String rowspan) {
		Pattern checkRegex = Pattern.compile("rowspan=(\\d)+", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher regexMatcher = checkRegex.matcher(rowspan);
		String var = null;
		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				var = regexMatcher.group(0);

			}
		}
		return var;
	}

	public String regexRowspan(String rowspan) {
		Pattern checkRegex = Pattern.compile("rowspan=\"\\d+\"", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher regexMatcher = checkRegex.matcher(rowspan);
		String var = null;
		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {

				var = regexMatcher.group(0);

			}
		}
		return var;
	}

	/**
	 * Extract the digit of colspan
	 * 
	 * @param colspan
	 * @return
	 */
	public String extractDigits(String src) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (Character.isDigit(c)) {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * checks if this row is a header
	 * 
	 * @param headerrow
	 * @return
	 */

	public boolean translateHeaderRow(String headerrow) {
		int startPos = 0;
		int bracketCount = 0;
		boolean var = false;
		while (startPos < headerrow.length() - 1) {

			String text = headerrow.substring(startPos, startPos + 2);
			switch (text) {
			case "\n!":
				if (bracketCount == 0) {
					var = true;
				}

				startPos++;
				break;

			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			default:
				startPos++;

			}
			if (var == true) {
				break;
			}
		}

		if (var == true)
			return true;
		else
			return false;

	}

	/**
	 * methods that return true if the header's cells are splitted by \n!
	 * 
	 * @param header
	 * @return
	 */
	public boolean translateHeaderRow2(String header) {

		int startPos = 2;

		int bracketCount = 0;
		boolean var = false;
		while (startPos < header.length() - 1) {
			String text = header.substring(startPos, startPos + 2);
			switch (text) {
			case "!!":
				if (bracketCount == 0) {
					var = true;
				}

				startPos++;
				break;
			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			default:
				startPos++;

			}
			if (var == true) {
				break;
			}
		}

		if (var == true)
			return true;
		else
			return false;
	}

	public boolean translateHeaderRow3(String header) {

		int startPos = 2;

		int bracketCount = 0;
		boolean var = false;
		while (startPos < header.length() - 1) {
			String text = header.substring(startPos, startPos + 2);
			switch (text) {
			case "\n|":
				if (bracketCount == 0) {
					var = true;
				}

				startPos++;
				break;
			case "{|":
				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			default:
				startPos++;

			}
			if (var == true) {
				break;
			}
		}

		if (var == true)
			return true;
		else
			return false;

	}

	public String rowWithoutDelimiter(String header) {
		String header1 = "";
		char[] headerchar = header.toCharArray();
		for (int i = 0; i < header.length() - 3; i++) {
			header1 = header1 + headerchar[i];
		}
		return header1;
	}

	/**
	 * Y'a un probleme la dessus car on doit extraire les captions a part. Pour
	 * le moment je vais supposer qu'il n'y a pas de caption Method that splits
	 * row into headers This method split headers cells that are separated with
	 * "!"
	 * 
	 * @param row
	 * @return
	 */

	public ArrayList<String> translateHeaderCell1(String row) {

		int i = 0;
		ArrayList<String> headers = new ArrayList<String>();
		int startRow = row.indexOf("!");
		// int startRow = 2;
		int startPos = 2;
		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "\n!":
				if (bracketCount == 0 && i != 0) {
					String header = row.substring(startRow, startPos);
					headers.add(header);
					startRow = startPos + 2;
				}
				i++;
				startPos++;
				break;
			case "{|":

				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			case "|-":
				if (bracketCount == 0) {
					String header1 = row.substring(startRow, startPos);
					headers.add(header1);
					startRow = startPos + 2;
				}
				startPos++;
				break;

			default:
				startPos++;

			}
		}
		return headers;
	}

	/**
	 * Method that splits headers cell that are separated with "!!"
	 * 
	 * @param row
	 * @return
	 */

	public ArrayList<String> translateHeaderCell2(String row) {

		ArrayList<String> headers = new ArrayList<String>();

		int startRow = row.indexOf("!");
		// int startRow = 2;
		int startPos = 2;

		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "!!":
				if (bracketCount == 0) {
					String header = row.substring(startRow + 1, startPos);
					headers.add(header);
					startRow = startPos + 2;
				}
				startPos++;
				break;
			case "{|":

				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			case "|-":
				if (bracketCount == 0) {
					String header1 = row.substring(startRow, startPos);
					headers.add(header1);
					startRow = startPos + 2;
				}
				startPos++;
				break;
			/*
			 * case "| ": if (bracketCount == 0) {String caption =
			 * row.substring(startPos,row.indexOf("!")); captions.add(caption);
			 * startRow = startPos + 2; } startPos++; break;
			 */

			default:
				startPos++;

			}
		}
		return headers;
	}

	public String[][] parseTable(String table) {

		// create the matrix
		String[][] matrix = createMatrix(table);

		ArrayList<String> rows = new ArrayList<String>();
		table = regexAttributeStyle(table);
		table = regexAttributeAlign(table);
		table = regexAttributeWidth(table);
		table = regexAttributeScope(table);
		rows = breakRows(table);

		int i = 0;
		for (String row : rows) {
			String varRow = row;
			if (translateHeaderRow(row)) {
				// column = countColumns(row);
				matrix = parseHeader(row, i, matrix);
			}

			if (row.startsWith("\n|")) {

				matrix = parseNormalRow(row, i, matrix);
			}

			i++;
			if (!translateHeaderRow(row) && i == 0) {
				i = 0;
			}
		}
		return matrix;
	}

	private String[][] parseNormalRow(String row, int i, String[][] matrix) {

		ArrayList<String> rowcells = new ArrayList<String>();

		if (translateNormalRow2(row)) {
			rowcells = translateCell2(row);
		}

		if ((translateNormalRow1(row))) {
			rowcells = translateCell1(row);
		}

		// for each header cell parse the cell

		for (String cell : rowcells) {

			cell = pipePattern.matcher(cell).replaceAll("");
			if ((cell.contains("colspan")) && (!cell.contains("rowspan"))) {

				int countercolumn = extractColspanDigit(cell);
				// fill table
				matrix = fillHorizontal(cell, countercolumn, matrix, i);
			} else if ((cell.contains("rowspan")) && (!cell.contains("colspan"))) {

				int counterrow = extractRowspanDigit(cell);
				// fill table
				matrix = fillVertical(cell, counterrow, matrix, i);

			} else if ((cell.contains("rowspan")) && (cell.contains("colspan"))) {

				int countercolumn = extractColspanDigit(cell);
				int counterRow = extractRowspanDigit(cell);
				// fill table
				matrix = fillHorizontalVertical(cell, countercolumn, counterRow, matrix, i);

			}

			else {

				matrix = fillVertical(cell, 1, matrix, i);

			}

		}
		return matrix;
	}

	public String[][] parseHeader(String row, int i, String[][] matrix) {

		ArrayList<String> headers = new ArrayList<String>();

		Matcher regexMatcher = classCleanupPattern.matcher(row);

		String rowWithoutDelimiter = rowWithoutDelimiter(row);
		if ((translateHeaderRow2(row))) {
			headers = translateHeaderCell2(row);
		}

		else if ((!translateHeaderRow2(row)) && (regexMatcher.find()) && (!translateHeaderRow3(rowWithoutDelimiter))) {
			headers = translateHeaderCell1(row);
		} else if ((!translateHeaderRow2(row)) && (!regexMatcher.find())
				&& (!translateHeaderRow3(rowWithoutDelimiter))) {
			headers = translateHeaderCell3(row);
		} else {
			headers = translateCell2(row);
			// this will work only if we have ! header than normal row separated
			// by |
		}

		// for each header cell parse the cell
		for (String cell : headers) {
			if ((cell.contains("colspan")) && (!cell.contains("rowspan"))) {
				// extract the colspan number of the column

				int countercolumn = extractColspanDigit(cell);
				// fill table
				matrix = fillHorizontal(cell, countercolumn, matrix, i);

			} else if ((cell.contains("rowspan")) && (!cell.contains("colspan"))) {

				// extract the rowspan number of the column

				int counterrow = extractRowspanDigit(cell);
				// fill table
				matrix = fillVertical(cell, counterrow, matrix, i);

			} else if ((cell.contains("rowspan")) && (cell.contains("colspan"))) {

				// extract the colspan number of the column
				String rowspan = regexRowspan(cell);

				int countercolumn = extractColspanDigit(cell);
				int counterRow = extractRowspanDigit(cell);

				matrix = fillHorizontalVertical(cell, countercolumn, counterRow, matrix, i);

			} else {
				matrix = fillVertical(cell, 1, matrix, i);
			}

		}

		return matrix;

	}

	public ArrayList<String> translateHeaderCell3(String row) {

		ArrayList<String> headers = new ArrayList<String>();
		// int startRow = row.indexOf("!");
		int startRow = 2;
		int startPos = 2;
		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "\n!":
				if (bracketCount == 0) {
					String header = row.substring(startRow, startPos);
					headers.add(header);
					startRow = startPos + 2;
				}

				startPos++;
				break;
			case "{|":

				bracketCount++;
				startPos++;
				break;

			case "|}":
				bracketCount--;
				startPos++;
				break;

			case "|-":
				if (bracketCount == 0) {
					String header1 = row.substring(startRow, startPos);
					headers.add(header1);
					startRow = startPos + 2;
				}
				startPos++;
				break;

			default:
				startPos++;

			}
		}
		return headers;
	}

	/**
	 * Method that fills table horizontally
	 * 
	 * @param cellvalue
	 * @param numberContent
	 * @param table
	 * @param i
	 * @return
	 */
	public String[][] fillHorizontal(String cellvalue, int numberContent, String[][] table, int i) {

		cellvalue = colspanCleanupPattern.matcher(cellvalue).replaceAll("");

		int j = 0;
		while (table[i][j] != null) {
			try {
				j++;
				if (table[i][j] == null) {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
			}
		}

		while (numberContent > 0) {
			try {
				table[i][j] = cellvalue;
				numberContent--;
				j++;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
				break;
			}

		}

		return table;
	}

	/**
	 * Method that fills table vertically
	 * 
	 * @param cellvalue
	 * @param numberContent
	 * @param table
	 * @param i
	 * @return
	 */

	public String[][] fillVertical(String cellvalue, int numberContent, String[][] table, int i) {

		if (cellvalue.contains("rowspan")) {
			cellvalue = rowspanCleanupPattern.matcher(cellvalue).replaceAll("");
		}
		int j = 0;

		while (table[i][j] != null) {
			try {
				j++;
				if (table[i][j] == null) {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
			}
		}

		while (numberContent > 0) {
			try {
				table[i][j] = cellvalue;
				numberContent--;
				i++;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
				break;
			}
		}

		return table;
	}

	/**
	 * Method that fills table vertically and horizontally when we have colspan
	 * and rowspan in the same cell
	 * 
	 * @param cellvalue
	 * @param counterColumn
	 * @param counterRow
	 * @param table
	 * @param i
	 * @return
	 */

	public String[][] fillHorizontalVertical(String cellvalue, int counterColumn, int counterRow, String[][] table,
			int i) {
		if (cellvalue.contains("rowspan") && cellvalue.contains("colspan")) {
			cellvalue = rowspanCleanupPattern.matcher(cellvalue).replaceAll("");
			cellvalue = colspanCleanupPattern.matcher(cellvalue).replaceAll("");
		}
		int j = 0;
		while (table[i][j] != null) {
			try {
				j++;
				if (table[i][j] == null) {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
			}
		}

		int m = i;
		int n = j;
		int finrow = m + counterRow;
		int fincolumn = j + counterColumn;
		for (m = i; m < finrow; m++) {
			for (n = j; n < fincolumn; n++) {
				try {
					table[m][n] = cellvalue;
				}

				catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("table isn't well structured");
				}
			}

		}

		return table;
	}

	/**
	 * printing out the matrix
	 * 
	 * @param table
	 */
	public void printoutMatrix(String table) {
		String[][] matrix = parseTable(table);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.print("\n");
		}
	}

	public boolean startwith(String row) {
		if (row.startsWith("\n|")) {
			return true;
		} else
			return false;
	}

	public ArrayList<String> cleanRows(String table) {

		ArrayList<String> rows = new ArrayList<String>();
		ArrayList<String> rows1 = new ArrayList<String>();
		rows = breakRows(table);
		// System.out.println(rows);
		for (String row : rows) {

			row = classCleanupPattern.matcher(row).replaceAll("");
			if (row == "\n|-") {
				row = row.replaceAll("\n|-", "");
			}
			rows1.add(row);
			// System.out.println(row);

		}

		return rows1;
	}

	public String regexAttributeStyle(String table) {

		Matcher regexMatcher = styleCleanupPattern.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			} /*
				 * else System.out.println("not found !"); }
				 */
		}
		return table;

	}

	public String regexAttributeAlign(String table) {

		Matcher regexMatcher = alignCleanupPattern.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}

	public String regexAttributeWidth(String table) {

		Matcher regexMatcher = widthCleanupPattern.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}

	public String regexAttributeScope(String table) {

		Matcher regexMatcher = scopeCleanupPattern.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
		} /*
			 * else System.out.println("not found !"); }
			 */

		return table;

	}

	public int extractColspanDigit(String cell) {
		String colspan = regexColspan(cell);
		if (colspan == null) {
			colspan = regexColspanWithNoQuotes(cell);
		}
		String counter = extractDigits(colspan);
		int countercolumn = Integer.parseInt(counter);

		return countercolumn;
	}

	public int extractRowspanDigit(String cell) {
		String rowspan = regexRowspan(cell);

		if (rowspan == null) {
			rowspan = regexRowspanWithNoQuotes(cell);
		}
		String counter = extractDigits(rowspan);
		int counterrow = Integer.parseInt(counter);

		return counterrow;

	}

}
