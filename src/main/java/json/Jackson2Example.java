package json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ualberta.wikipedia.tablereader.Cell;
import ca.ualberta.wikipedia.tablereader.ReadFile;
import ca.ualberta.wikipedia.tablereader.WikipediaTableParser;

public class Jackson2Example {
	public static void main(String[] args) throws IOException {
		
		Jackson2Example obj = new Jackson2Example();
		obj.run();
	}

	private void run() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ReadFile read = new ReadFile();
		ArrayList<Cell[][]> liste = createDummyObject(read.readFile("/Users/Nouha/Desktop/file"));
		

		for(Cell[][] matrix: liste)
		{  for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
		
		try {
			// Convert object to JSON string and save into a file directly
			mapper.writeValue(new File("/Users/Nouha/Desktop/table.json"),matrix[i][j]);

			// Convert object to JSON string
			String jsonInString = mapper.writeValueAsString(matrix[i][j]);
			System.out.println(jsonInString);

			// Convert object to JSON string and pretty print
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(matrix[i][j]);
			System.out.println(jsonInString);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}}}
	}

	private ArrayList<Cell[][]> createDummyObject(String wikiText) {
		
		WikipediaTableParser wiki1 = new WikipediaTableParser();

		ArrayList<Cell[][]> matrixes= new ArrayList<Cell[][]>();
	//	matrixes = wiki1.getAllMatrixFromTables(wikiText);
		
		

		return matrixes;

	}
}
