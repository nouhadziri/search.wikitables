package json;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson2Example {
	public static void main(String[] args) {
		Jackson2Example obj = new Jackson2Example();
		obj.run();
	}

	private void run() {
		ObjectMapper mapper = new ObjectMapper();

		Staff staff = createDummyObject();

		try {
			// Convert object to JSON string and save into a file directly
			mapper.writeValue(new File("/Users/Nouha/Desktop/staff.json"), staff);

			// Convert object to JSON string
			String jsonInString = mapper.writeValueAsString(staff);
			System.out.println(jsonInString);

			// Convert object to JSON string and pretty print
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff);
			System.out.println(jsonInString);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Staff createDummyObject() {

		Staff staff = new Staff();

		staff.setName("mkyong");
		staff.setAge(33);
		staff.setPosition("Developer");
		staff.setSalary(new BigDecimal("7500"));

		List<String> skills = new ArrayList<>();
		skills.add("java");
		skills.add("python");

		staff.setSkills(skills);

		return staff;

	}
}
