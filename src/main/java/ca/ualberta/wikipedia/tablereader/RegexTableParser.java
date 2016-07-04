package ca.ualberta.wikipedia.tablereader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTableParser {
	// private static Pattern styleCleanupPattern =
	// Pattern.compile("\\bstyle=\"(.*?)\"", Pattern.MULTILINE |
	// Pattern.DOTALL);

	// private static Pattern styleCleanupPattern =
	// Pattern.compile("\\bstyle\\s*=\\s*\"(.*?)\"", Pattern.MULTILINE |
	// Pattern.DOTALL);
	private static Pattern alignCleanupPattern = Pattern.compile("\\balign\\s*=\\s*\"(.*?)\"(.*?)\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern alignCleanupPatternWithNoQuotes = Pattern.compile("\\balign\\s*=\\s*\\S{6}\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern styleCleanupPattern = Pattern.compile("\\b\\s?style\\s*=\\s*\"(.*?)\"(.*?)\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern styleCleanupPatternWithNoQuotes = Pattern.compile("\\bstyle\\s*=\\s*\\S*\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern widthCleanupPattern = Pattern.compile("\\bwidth\\s*=\\s*\"(.*?)\"\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	
	private static Pattern widthCleanupPatternWithNoQuotes = Pattern.compile("\\bwidth\\s*=\\s*\\S*\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern bgcolorCleanupPattern = Pattern.compile("\\bbgcolor\\s*=\\s*\"(.*?)\"\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern bgcolorCleanupPatternWithNoQuotes = Pattern.compile("\\bgcolor\\s*=\\s*\\S*\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern scopeCleanupPattern = Pattern.compile("\\bscope\\s*=\\s*\"(.*?)\"\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	
	private static Pattern scopeCleanupPatternWithNoQuotes= Pattern.compile("\\bscope\\s*=\\s*\\S*\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern valignCleanupPattern = Pattern.compile("\\bvalign\\s*=\\s*\"(.*?)\"\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern valignCleanupPatternWithNoQuotes = Pattern.compile("\\bvalign\\s*=\\s*\\S*\\|?",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern refCleanupPattern = Pattern.compile("<ref(.*?)>.*?</ref>",
			Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern spanCleanupPattern = Pattern.compile("<span(.*?)>", Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern classCleanupPattern = Pattern.compile("\\bclass\\s*=\\s*\"(.*?)\"",
			Pattern.MULTILINE | Pattern.DOTALL);


	/**
	 * Method that extracts colspan = "?" to have the digit afterwards. It does
	 * not handle colspan =10 without quotes. should mention that to Matteo
	 * 
	 * @param colspan
	 */

	public String regexColspan(String colspan) {
		// Pattern checkRegex = Pattern.compile("\\bcolspan\\b=\"\\d+\"",
		// Pattern.MULTILINE | Pattern.DOTALL);
		Pattern checkRegex = Pattern.compile("\\bcolspan\\s*=\\s*\"\\d+\"", Pattern.MULTILINE | Pattern.DOTALL);
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
		Pattern checkRegex = Pattern.compile("\\bcolspan\\s*=\\s*(\\d)+", Pattern.MULTILINE | Pattern.DOTALL);
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
		Pattern checkRegex = Pattern.compile("\\browspan\\s*=\\s*(\\d)+", Pattern.MULTILINE | Pattern.DOTALL);
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
		Pattern checkRegex = Pattern.compile("\\browspan\\s*=\\s*\"\\d+\"", Pattern.MULTILINE | Pattern.DOTALL);
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
	public String regexAttributeStyleWitoutQuotes(String table) {

		Matcher regexMatcher = styleCleanupPatternWithNoQuotes.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			} /*
				 * else System.out.println("not found !"); }
				 */
		}
		return table;

	}
	public String regexAttributeClass(String table) {

		Matcher regexMatcher = classCleanupPattern.matcher(table);

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
	
	public String regexAttributeAlignWitoutQuotes(String table) {

		Matcher regexMatcher = alignCleanupPatternWithNoQuotes.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}
	public String regexAttributeBgcolorWitoutQuotes(String table) {

		Matcher regexMatcher = bgcolorCleanupPatternWithNoQuotes.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}
	
	public String regexAttributeValignWithoutQuotes(String table) {

		Matcher regexMatcher = valignCleanupPatternWithNoQuotes.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}
	
	public String regexAttributeWidthWithoutQuotes(String table) {

		Matcher regexMatcher = widthCleanupPatternWithNoQuotes.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}

	public String regexAttributeScopeWithoutQuotes(String table) {

		Matcher regexMatcher = scopeCleanupPatternWithNoQuotes.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
		} /*
			 * else System.out.println("not found !"); }
			 */

		return table;

	}
	

	public String regexAttributeBgcolor(String table) {

		Matcher regexMatcher = bgcolorCleanupPattern.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
			/*
			 * else System.out.println("not found !"); }
			 */}

		return table;

	}

	public String regexAttributeValign(String table) {

		Matcher regexMatcher = valignCleanupPattern.matcher(table);

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

	public String regexAttributeSpan(String table) {

		Matcher regexMatcher = spanCleanupPattern.matcher(table);

		while (regexMatcher.find()) {
			if (regexMatcher.group().length() != 0) {
				table = regexMatcher.replaceAll("");

			}
		} /*
			 * else System.out.println("not found !"); }
			 */

		return table;

	}

	public String regexRef(String table) {

		Matcher regexMatcher = refCleanupPattern.matcher(table);

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
