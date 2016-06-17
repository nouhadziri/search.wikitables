package ca.ualberta.wikipedia.tablereader;

import java.util.ArrayList;

public class CreateTables {
	// WikiTableRowspan wikipedia = new WikiTableRowspan();

		public ArrayList<String> createTable(String wikiText) {

			ArrayList<String> tables1 = new ArrayList<String>();
			String wiki = wikiText;
			int bracketCount = 1;
			final String TABLE_CONST_STR = "{|";
			int startPos = wiki.indexOf(TABLE_CONST_STR);
			/*
			 * if (startPos < 0) {System.out.println("null haha");}
			 */
			int endPos = startPos + TABLE_CONST_STR.length();
			while (endPos < wiki.length() - 1) {
				String text = wiki.substring(endPos, endPos + 2);
				switch (text) {
				case "|}":
					bracketCount--;
					endPos++;
					break;
				case "{|":
					bracketCount++;
					endPos++;
					break;

				default:
					endPos++;
				}
				if ((bracketCount == 0) && (text.equals("|}"))) {
					try {
						String table = wiki.substring(startPos, endPos + 1);
						tables1.add(table);
						wiki = wiki.substring(endPos + 1, wiki.length());
						startPos = wiki.indexOf(TABLE_CONST_STR);
						endPos = startPos + TABLE_CONST_STR.length();
						bracketCount = 1;
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Row unstructured");
					}
					

				}

			}

			if (bracketCount != 0) {
				// System.out.println("couldn't match the brackets.");
			}

			return tables1;
		}
}
