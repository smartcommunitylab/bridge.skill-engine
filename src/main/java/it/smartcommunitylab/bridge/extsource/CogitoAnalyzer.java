package it.smartcommunitylab.bridge.extsource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import it.smartcommunitylab.bridge.common.HTTPUtils;
import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.model.Course;

@Component
public class CogitoAnalyzer {
	private static final transient Logger logger = LoggerFactory.getLogger(CogitoAnalyzer.class);
	
	@Autowired
	@Value("${cogito.training_offers.url}")
	private String analyzerURL;
	
	public void analyzeCourse(Course course) {
		try {
			String jsonString = HTTPUtils.post(analyzerURL, course.getContent(), null, null, null);
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

}
