package it.smartcommunitylab.bridge.extsource;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import it.smartcommunitylab.bridge.common.HTTPUtils;
import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.model.Course;

@Component
public class CogitoAnalyzer {
	private static final transient Logger logger = LoggerFactory.getLogger(CogitoAnalyzer.class);
	
	@Autowired
	@Value("${cogito.training_offers.url}")
	private String trainingOffersAnalyzerURL;
	
	@Autowired
	@Value("${cogito.personal_data.url}")
	private String personalDataAnalyzerURL;
	
	public void analyzeCourse(Course course) {
		try {
			String jsonString = HTTPUtils.post(trainingOffersAnalyzerURL, course.getContent(), null, null, null);
			JsonNode jsonNode = Utils.readJsonFromString(jsonString);
			JsonNode tjNode = jsonNode.get("TRAINING_JOB");
			for (JsonNode node : tjNode) {
				String training = node.get("TRAINING").asText();
				if(Utils.isNotEmpty(training)) {
					course.getCogitoAnalysis().add(training);
				}
			}
		} catch (Exception e) {
			logger.warn("analyzeCourse error:{}", e.getMessage());
		}
	}
	
	public String analyzePersonalData(InputStream stream) {
		BodyContentHandler handler = new BodyContentHandler();
    AutoDetectParser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();
    try {
    	parser.parse(stream, handler, metadata);
    	String jsonString = null;
    	String content = handler.toString();
    	jsonString = analyzeAspect(personalDataAnalyzerURL + "personal_data", content);
    	JsonNode pdNode = Utils.readJsonFromString(jsonString);
    	jsonString = analyzeAspect(personalDataAnalyzerURL + "languages", content);
    	JsonNode langNode = Utils.readJsonFromString(jsonString);
    	jsonString = analyzeAspect(personalDataAnalyzerURL + "degrees", content);
    	JsonNode degNode = Utils.readJsonFromString(jsonString);
    	jsonString = analyzeAspect(personalDataAnalyzerURL + "work_experiences", content);
    	JsonNode weNode = Utils.readJsonFromString(jsonString);
    	jsonString = analyzeAspect(personalDataAnalyzerURL + "it_knowledge", content);
    	JsonNode itNode = Utils.readJsonFromString(jsonString);
    	ArrayNode arrayNode = Utils.createJsonArray();
    	arrayNode.add(pdNode);
    	arrayNode.add(langNode);
    	arrayNode.add(degNode);
    	arrayNode.add(weNode);
    	arrayNode.add(itNode);
    	return arrayNode.toString();
		} catch (Exception e) {
			logger.warn("analyzePersonalData error:{}", e.getMessage());
		}
		return null;
	}
	
	private String analyzeAspect(String url, String content) {
		String jsonString = "{}";
		try {
			jsonString = HTTPUtils.post(url, content, null, null, null);
		} catch (Exception e) {
			logger.warn("analyzeAspect error:{}", e.getMessage());
		}
		return jsonString;
	}

}
