package ca.ualberta.wikipedia.tablereader;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class WikipediaTableParser {
	
	//private String wikiText = null;

	ArrayList<String> headerAndRow = new ArrayList<String>();

	RegexTableParser regexparser = new RegexTableParser();

	CreateTables varTable = new CreateTables();
	
	ArrayList<Table> tableswikipedia = new 	ArrayList<Table>();
	
	int numTable =0;
	public boolean hasOnlyRowspan;
	public boolean hasOnlyColspan;

	public boolean hasNoHeader;
	public boolean hasCaption;
	public boolean hasMisuseException;
	
	public boolean hasRowspan;
	public boolean hasColspan;
	public boolean hasMixRowspanAndColspan;
	public boolean hasNestedTable;
	public boolean hasException;

	int column = 0;
	int row = 0;
	String[][] matrix = new String[row][column];

	private static Pattern colspanCleanupPattern = Pattern.compile("\\bcolspan\\s*=\\s*\"\\d+\"",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern colspanCleanupPattern2 = Pattern.compile("\\bcolspan\\s*=\\s*(\\d)+",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern rowspanCleanupPattern = Pattern.compile("\\browspan\\s*=\\s*\"\\d+\"",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern rowspanCleanupPattern2 = Pattern.compile("\\browspan\\s*=\\s*(\\d)+",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern classCleanupPattern = Pattern.compile("\\bclass\\s*=\\s*\"(.*?)\"",
			Pattern.MULTILINE | Pattern.DOTALL);

	private static Pattern pipePattern = Pattern.compile("\\|", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern newlinePattern = Pattern.compile("\\n", Pattern.MULTILINE | Pattern.DOTALL);
	

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
				int tablelength = table.length() - 2;
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

	public int countColumns(String headerRow) throws HeaderException {

		ArrayList<String> headerCells = new ArrayList<String>();
		boolean var = false;
		Matcher regexMatcher = classCleanupPattern.matcher(headerRow);
		
		
		if ((translateHeaderRow2(headerRow))) {
			headerCells = translateHeaderCell2(headerRow);
		} 
	
		else if ((translateHeaderRow4(headerRow)) && (regexMatcher.find())) {
			headerCells = translateHeaderCell1(headerRow);
		} 
		else if ((translateHeaderRow4(headerRow)) && (!regexMatcher.find())) {
			headerCells = translateHeaderCell3(headerRow);
		} 
		else if ((translateHeaderRow(headerRow)) && (regexMatcher.find())) {
			headerCells = translateHeaderCell1(headerRow);
		} 
		else if ((translateHeaderRow(headerRow)) && (!regexMatcher.find())) {
			headerCells = translateHeaderCell3(headerRow);
		}

		/*
		 * else if ((!translateHeaderRow2(headerRow)) && (regexMatcher.find()))
		 * { headerCells = translateHeaderCell1(headerRow); } else if
		 * ((!translateHeaderRow2(headerRow)) && (!regexMatcher.find())) {
		 * headerCells = translateHeaderCell3(headerRow); }
		 */
		else {
			throw new HeaderException("Misuse of header's wiki markup language ");
		}

		int columnCount = 0;

		Iterator<String> iter = headerCells.iterator();
		while (iter.hasNext()) {
			String varHeaderCell = iter.next();
			if (varHeaderCell.contains("colspan")) {
				String colspan = regexparser.regexColspan(varHeaderCell);
				if (colspan == null) {
					colspan = regexparser.regexColspanWithNoQuotes(varHeaderCell);
				}
				int digit = Integer.parseInt(regexparser.extractDigits(colspan));
				columnCount = columnCount + digit;
			} else {
				columnCount = columnCount + 1;
			}
		}
		return columnCount;
	}

	/**
	 * this methods for counting how many columns if we have a table without
	 * headers which is very rare
	 * 
	 * @param normalRow
	 * @return
	 * @throws RowException
	 */
	public int countColumnsNormalRow(String normalRow) throws RowException {

		ArrayList<String> rowCells = new ArrayList<String>();

		// Matcher regexMatcher = classCleanupPattern.matcher(normalRow);

		if ((translateNormalRow1(normalRow))) {
			rowCells = translateCell1(normalRow);
		}

		else if (translateNormalRow2((normalRow))) {
			rowCells = translateCell2(normalRow);
		} else {
			throw new RowException("Misuse of row's wiki markup language ");
		}
		int columnCount = 0;

		Iterator<String> iter = rowCells.iterator();
		while (iter.hasNext()) {
			String varRowCell = iter.next();
			if (varRowCell.contains("colspan")) {
				String colspan = regexparser.regexColspan(varRowCell);
				if (colspan == null) {
					colspan = regexparser.regexColspanWithNoQuotes(varRowCell);
				}
				int digit = Integer.parseInt(regexparser.extractDigits(colspan));
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
	public Cell[][] createMatrix(String table) {

		ArrayList<String> rows = new ArrayList<String>();
		int row = countRows(table);
		rows = breakRows(table);
		String headerRow = null;

		int column = 0;

		Iterator<String> iter = rows.iterator();

		while (iter.hasNext()) {
			String varrow = iter.next();
			Matcher regexMatcher = classCleanupPattern.matcher(varrow);

			if (translateHeaderRow(varrow)) {
				headerRow = varrow;
				try {
					column = countColumns(headerRow);
				} catch (HeaderException e) {
					// TODO Auto-generated catch block
					System.out.println("Misuse of header wiki markup language");
				}
				break;
			}

			else if (translateNormalRow1(varrow)) {
				headerRow = varrow;
				try {
					column = countColumnsNormalRow(headerRow);
				} catch (RowException e) {

					System.out.println("Misuse of row's wiki markup language");
				}
				break;
			}
			// rowWithoutDelimiter because |- is causing problems so we want to
			// know if there
			// is normal row separated by /n|
			else if (translateHeaderRow3(rowWithoutDelimiter(varrow)) && (!varrow.contains("|+"))
					&& (!regexMatcher.find())) {
				String var = rowWithoutDelimiter(varrow);
				headerRow = varrow;
				try {
					column = countColumnsNormalRow(headerRow);
				} catch (RowException e) {
					System.out.println("Misuse of row's wiki markup language");

				}
				break;
			} /*
				 * else if (varrow.startsWith("\n|")) { headerRow = varrow; try
				 * { column = countColumnsNormalRow(headerRow); } catch
				 * (RowException e) { // TODO Auto-generated catch block
				 * System.out.println("Misuse of row's wiki markup language"); }
				 */
			else if (translateNormalRow2(varrow) && (!varrow.contains("|+"))) {
				headerRow = varrow;
				try {
					column = countColumnsNormalRow(headerRow);
				} catch (RowException e) {
					// TODO Auto-generated catch block
					System.out.println("Misuse of row's wiki markup language");
				}

				break;
			}
		}

		Cell[][] matrix = new Cell[row][column];

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
		/*
		 * if (!translateNormalRow1(normalrow)) return true; else return false;
		 */
		int startPos = normalrow.indexOf("\n|") + 2;
		int bracketCount = 0;
		boolean var = false;
		while (startPos < normalrow.length() - 1) {
			String text = normalrow.substring(startPos, startPos + 2);
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

	/**
	 * The good one ,Method that splits the row into cells and checks if there
	 * are nested tables. the cells here are are separated by "||" we didn't
	 * parse each cell,this will be done by parseCell()
	 * 
	 * @param row
	 * @return
	 */

	public ArrayList<String> translateCell1(String row) {

		ArrayList<String> cells = new ArrayList<String>();
		int startRow = 2;
		int startPos = 2;
		/*
		 * int startRow = row.indexOf("|"); int startPos = row.indexOf("|");
		 */
		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "||":
				if (bracketCount == 0) {
					try {
						String cell = row.substring(startRow, startPos - 1);// startPos-1
						cells.add(cell);
						startRow = startPos + 1;
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Cell separated by \"||\" is not well structured in table n : "+ numTable);
						hasException=true;
						
					}
					
				}
				
				startPos++;
				break;
			case "{|":

				bracketCount++;
				startPos++;
				break;

			case "|}":
				int bracketindex = row.indexOf("|}", startPos);
				int tablelength = row.length() - 2;
				if (bracketindex == tablelength) {

					String cell2 = row.substring(startRow, startPos - 1);
					cells.add(cell2);

				}
				bracketCount--;
				startPos++;
				break;

			case "|-":
				if (bracketCount == 0) {
					try{
					String cell = row.substring(startRow, startPos - 1);
					cells.add(cell);
					startRow = startPos + 2;}
					catch(IndexOutOfBoundsException e){
						System.out.println("");
					}
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
	 * the good one: Method that splits the row into cells and check if there is
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
		// int startRow = row.indexOf("|")+1;
		// int startPos = row.indexOf("|")+1;
		int bracketCount = 0;
		while (startPos < row.length() - 1) {
			String text = row.substring(startPos, startPos + 2);
			switch (text) {
			case "\n|":
				if (bracketCount == 0) {
					try{
					String cell = row.substring(startRow, startPos);
					cells.add(cell);
					startRow = startPos + 2;}
				catch(IndexOutOfBoundsException e)
				{
					System.out.println("Cell separated by \"|\" is not well structured in table n : "+ numTable);
					hasException=true;
				}
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
			 * startPos++; break;// I commented this because we are using \n| to
			 * split and we don't need "|-" as our end delimiter cuz we have \n
			 * and | wich is from "|-"
			 */

			default:
				startPos++;

			}

		}
		return cells;
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

	public boolean translateHeaderRow4(String header) {

		int startPos = header.indexOf("\n!") + 2;

		int bracketCount = 0;
		boolean var = false;
		while (startPos < header.length() - 1) {
			String text = header.substring(startPos, startPos + 2);
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
	 * useful for the case where we have in one single row a header and a row,
	 * we delete this "|-" delimiter because we want to separate cell using
	 * "\n|" otherwise it will not work properly
	 * 
	 * @param header
	 * @return
	 */
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
					String header = row.substring(startRow+1 , startPos);
					headers.add(header);
					startRow = startPos + 1; // I did a change here
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

	/**
	 * useful for separating cell in a row which is a heder and a normal row at
	 * the same time
	 * 
	 * @param row
	 * @return
	 */

	public ArrayList<String> translateHeaderCell3(String row) {

		ArrayList<String> headers = new ArrayList<String>();
		// int startRow = row.indexOf("!");
		// int startRow = 2;
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
	
	

	public Cell[][] parseTable(String table) {

		// create the matrix
		Cell[][] matrix = createMatrix(table);

		ArrayList<String> rows = new ArrayList<String>();
		table = regexparser.regexAttributeStyle(table);
		table = regexparser.regexAttributeAlign(table);
		table = regexparser.regexAttributeValign(table);
		table = regexparser.regexAttributeWidth(table);
		table = regexparser.regexAttributeScope(table);
		table = regexparser.regexAttributeSpan(table);
		table = regexparser.regexRef(table);
		table = regexparser.regexAttributeBgcolor(table);
		rows = breakRows(table);

		int i = 0;
		for (String row : rows) {
			if (translateHeaderRow(row)) {
				matrix = parseHeader(row, i, matrix);
			}
			if ((row.startsWith("\n|")) || (row.startsWith(" \n|")) || (row.startsWith("  \n|"))) {

				matrix = parseNormalRow(row, i, matrix);
			}

			i++;
			if (!translateHeaderRow(row) && i == 0) {
				i = 0;
			}
		}
		Table tableObject = new Table(hasOnlyRowspan,hasOnlyColspan,hasMixRowspanAndColspan,hasNestedTable,hasException,hasNoHeader
				,hasCaption,hasMisuseException);
		tableswikipedia.add(tableObject);
		hasRowspan=false;
		hasColspan=false;
		hasMixRowspanAndColspan=false;
		hasNestedTable=false;
		hasException = false;

		return matrix;
		
	}

	
	public void printAllMatrixFromTables() {
		ArrayList<Cell[][]> matrixTables = new ArrayList<Cell[][]>();

		matrixTables = getAllMatrixFromTables();

		for (Cell[][] wikimatrix : matrixTables) {
			for (int i = 0; i < wikimatrix.length; i++) {
				for (int j = 0; j < wikimatrix[0].length; j++) {
					try {
						if (wikimatrix[i][j].getContent() == null) {
							continue;
						}
						System.out.print(wikimatrix[i][j].getContent() + " ");
					} catch (java.lang.NullPointerException e) {
						// System.out.print("");
					}
				}

				System.out.print("\n");

			}
		}

	}

	public ArrayList<Cell[][]> getAllMatrixFromTables() {

	
		ArrayList<Cell[][]> matrixTables = new ArrayList<Cell[][]>();
		Cell[][] matrix = null;
		//ArrayList<String> tables = varTable.createTable(wikiText);
		/*for (String table : tables) {
			matrix = parseTable(table);
			numTable++;
			
			matrixTables.add(matrix);
		}
*/
		return matrixTables;
	}

	private Cell[][] parseNormalRow(String row, int i, Cell[][] matrix) {

		ArrayList<String> rowcells = new ArrayList<String>();
		String type = "NormalCell";
		if (translateNormalRow2(row)) {
			rowcells = translateCell2(row);
		}

		if ((translateNormalRow1(row))) {
			rowcells = translateCell1(row);
		}

		// for each header cell parse the cell

		for (String cell : rowcells) {

			cell = cell.trim();
			matrix = parseCell(cell, i, matrix, type);
		}
		return matrix;
	}

	public Cell[][] parseHeader(String row, int i, Cell[][] matrix) {

		ArrayList<String> headers = new ArrayList<String>();
		boolean header = false;

		Matcher regexMatcher = classCleanupPattern.matcher(row);
		String type = "header";

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
			header = true;
			// this will work only if we have ! header than normal row separated
			// by |
		}

		// for each header cell parse the cell
		if (header == true) {
			for (String cell : headers) {
				cell = pipePattern.matcher(cell).replaceAll("");
				cell = newlinePattern.matcher(cell).replaceAll("");
				cell = cell.trim();
				matrix = parseCell(cell, i, matrix, type);
				type = "NormalCell";

			}
		} else {
			for (String cell : headers) {
				cell = pipePattern.matcher(cell).replaceAll("");
				cell = newlinePattern.matcher(cell).replaceAll("");
				cell = cell.trim();

				matrix = parseCell(cell, i, matrix, type);

			}
		}

		return matrix;
	}

	public Cell[][] parseCell(String cell, int i, Cell[][] matrix, String type) {
		Cell[][] cellTable;
		ArrayList<String> var = varTable.createTable(cell);
		

		if (var.isEmpty()) {
			// cell = pipePattern.matcher(cell).replaceAll("");
			if ((cell.contains("colspan")) && (!cell.contains("rowspan"))) {
				// extract the colspan number of the column

				int countercolumn = regexparser.extractColspanDigit(cell);
				// fill table
				matrix = fillHorizontal(cell, countercolumn, matrix, i, type);
				hasColspan = true;

			} else if ((cell.contains("rowspan")) && (!cell.contains("colspan"))) {

				// extract the rowspan number of the column

				int counterrow = regexparser.extractRowspanDigit(cell);
				// fill table
				matrix = fillVertical(cell, counterrow, matrix, i, type);
				hasRowspan = true;

			} else if ((cell.contains("rowspan")) && (cell.contains("colspan"))) {

				// extract the colspan number of the column
				String rowspan = regexparser.regexRowspan(cell);

				int countercolumn = regexparser.extractColspanDigit(cell);
				int counterRow = regexparser.extractRowspanDigit(cell);
				
				matrix = fillHorizontalVertical(cell, countercolumn, counterRow, matrix, i, type);
				hasMixRowspanAndColspan= true;
			} else {
				matrix = fillVertical(cell, 1, matrix, i, type);
			}
		} else {
			hasNestedTable=true;
			cellTable = parseTable(cell);
			String cellTableString = ConvertArrayToString(cell);
			type = "nested table";
			matrix = fillVertical(cellTableString, 1, matrix, i, type);
		}
	
		return matrix;
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
	public Cell[][] fillHorizontal(String cellvalue, int numberContent, Cell[][] table, int i, String type) {

		cellvalue = colspanCleanupPattern.matcher(cellvalue).replaceAll("");
		cellvalue = colspanCleanupPattern2.matcher(cellvalue).replaceAll("");
		cellvalue = pipePattern.matcher(cellvalue).replaceAll("");
		cellvalue = regexparser.regexAttributeStyle(cellvalue);

		// cellvalue = .matcher(cellvalue).replaceAll("");
		int j = 0;

		while (table[i][j] != null) {
			try {
				j++;
				if (table[i][j] == null) {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
				hasException = true;
			}

		}

		while (numberContent > 0) {
			try {
				Cell cell1 = new Cell(type, cellvalue);
				table[i][j] = cell1;
				numberContent--;
				j++;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
				hasException = true;
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

	public Cell[][] fillVertical(String cellvalue, int numberContent, Cell[][] table, int i, String type) {

		if (cellvalue.contains("rowspan")) {
			cellvalue = rowspanCleanupPattern.matcher(cellvalue).replaceAll("");
			cellvalue = rowspanCleanupPattern2.matcher(cellvalue).replaceAll("");
		}
		cellvalue = pipePattern.matcher(cellvalue).replaceAll("");
		cellvalue = regexparser.regexAttributeStyle(cellvalue);
		int j = 0;
		try {
			while (table[i][j] != null) {
				try {
					j++;
					if (table[i][j] == null) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("table " + numTable +" isn't well structured: It runs out of size ");
					hasException = true;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("table " + numTable +" isn't well structured: It runs out of size");
		}

		while (numberContent > 0) {
			try {
				Cell cell1 = new Cell(type, cellvalue);
				table[i][j] = cell1;

				numberContent--;
				i++;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table " + numTable +" isn't well structured: It runs out of size");
				hasException = true;
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

	public Cell[][] fillHorizontalVertical(String cellvalue, int counterColumn, int counterRow, Cell[][] table, int i,
			String type) {
		if (cellvalue.contains("rowspan") && cellvalue.contains("colspan")) {
			cellvalue = rowspanCleanupPattern.matcher(cellvalue).replaceAll("");
			cellvalue = colspanCleanupPattern.matcher(cellvalue).replaceAll("");

		}
		cellvalue = pipePattern.matcher(cellvalue).replaceAll("");
		cellvalue = regexparser.regexAttributeStyle(cellvalue);
		int j = 0;
		while (table[i][j] != null) {
			try {
				j++;
				if (table[i][j] == null) {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("table isn't well structured");
				hasException = true;
			}
		}

		int m = i;
		int n = j;
		int finrow = m + counterRow;
		int fincolumn = j + counterColumn;
		for (m = i; m < finrow; m++) {
			for (n = j; n < fincolumn; n++) {
				try {
					
					Cell cell1 = new Cell(type, cellvalue);
					table[m][n] = cell1;
				}

				catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("table isn't well structured");
					hasException = true;	
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
		Cell[][] matrix = parseTable(table);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				try {
					if (matrix[i][j].getContent() == null) {
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

	public String ConvertArrayToString(String table) {
		StringBuilder builder = new StringBuilder();
		Cell[][] matrix = parseTable(table);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				builder.append(matrix[i][j].getContent());
			}
		}
		return builder.toString();
	}
	
	public int countRowspan()
	{
		int rowspanCount =0;
		for (Table table: tableswikipedia)
		{
			if (table.hasRowspan)
			{
				rowspanCount++;
			}
		}
		return rowspanCount;
	}
	
	public int countColspan()
	{
		int colspanCount =0;
		for (Table table: tableswikipedia)
		{
			if (table.hasColspan)
			{
				colspanCount++;
			}
		}
		return colspanCount;
	}
	
	public int countMixColspanAndRowspan()
	{
		int colspanRowspanCount =0;
		for (Table table: tableswikipedia)
		{
			if (table.hasMixRowspanAndColspan)
			{
				colspanRowspanCount++;
			}
		}
		return colspanRowspanCount;
	}
	
	public int counthasException()
	{
		int countException =0;
		for (Table table: tableswikipedia)
		{
			if (table.hasException)
			{
				countException++;
			}
		}
		return countException;
	}
	
	public int countNestedtables()
	{
		int nestedTableCount =0;
		for (Table table: tableswikipedia)
		{
			if (table.hasNestedTable)
			{
				nestedTableCount++;
			}
		}
		return nestedTableCount;
	}
	
	public int countTable()
	{
	//	ArrayList<String> tables = varTable.createTable(wikiText);
		int countTable=0;
		//countTable = tables.size();
		
		return countTable;
		
	}
	
	
	
	
}
