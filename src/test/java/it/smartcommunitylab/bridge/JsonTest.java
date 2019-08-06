package it.smartcommunitylab.bridge;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.bridge.model.cogito.PersonalData;

public class JsonTest {
	@Test
	public void parseJson() throws Exception {
		String json = "{\"PERSONAL_DATA\":{\"FIRST_NAME\":\"Egiziana\",\"NAZIONALITA\":\"egiziano\",\"SEX\":\"F\",\"STATO_CIVILE\":\"moglie\"}}";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
		PersonalData personalData = mapper.treeToValue(jsonNode.get("PERSONAL_DATA"), PersonalData.class);
		personalData.getE_MAIL();
	}
}
