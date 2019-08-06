package it.smartcommunitylab.bridge;

import java.io.File;

import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TestUpload {
	@Test
	public void uploadFileRestTemplate() throws Exception {
		File file = new File("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\anonimizzazione\\dati_cinformi\\3.odt");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		FileSystemResource fsr = new FileSystemResource(file);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", fsr);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		String serverUrl = "http://deployenv3.esfinancedprojects.com:7070/bridge/analysis/all_by_file";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
		System.out.println(response.getStatusCodeValue() + "\n" + response.getBody());
	}
}
