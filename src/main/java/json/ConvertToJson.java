package json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.jhu.nlp.wikipedia.WikiPage;

public class ConvertToJson {
	public static void main(String[] args) throws IOException, JSONException {

		ArrayList<String> headers = new ArrayList<String>();
	headers.add("nouha");
	headers.add("nedia");
		JSONObject returnObj = new JSONObject();

		WikiPage page = new WikiPage();
		//String title = page.getTitle().trim();
		JSONObject tableJSON = new JSONObject();

		tableJSON.put("title", "sjjs");
		tableJSON.put("table#", 1);
		JSONArray headerArray = new JSONArray(headers);
		tableJSON.put("headers", headerArray);
		JSONArray rows = new JSONArray();
		for (int i = 0; i < 3; i++) {
			JSONArray row = new JSONArray(Arrays.asList(i, i, i));
			rows.put(row);
		}
		tableJSON.put("rows", rows);

		returnObj.put("table1", tableJSON);

		tableJSON = new JSONObject();
		tableJSON.put("art", "art2");
		tableJSON.put("table#", 2);
		headerArray = new JSONArray(Arrays.asList("h12", "h22", "h32"));
		tableJSON.put("headers", headerArray);
		rows = new JSONArray();
		for (int i = 0; i < 3; i++) {
			JSONArray row = new JSONArray(Arrays.asList(i, i, i));
			rows.put(row);
		}
		tableJSON.put("rows", rows);

		returnObj.put("table2", tableJSON);

		System.out.println(returnObj.toString(4));

	}
}
