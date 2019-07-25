package it.smartcommunitylab.bridge.extsource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import it.smartcommunitylab.bridge.common.Const;
import it.smartcommunitylab.bridge.common.HTTPUtils;
import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.Course;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.TextDoc;
import it.smartcommunitylab.bridge.model.cogito.CogitoProfile;
import it.smartcommunitylab.bridge.model.cogito.Degree;
import it.smartcommunitylab.bridge.model.cogito.Language;
import it.smartcommunitylab.bridge.model.cogito.PersonalData;
import it.smartcommunitylab.bridge.model.cogito.WorkExperience;
import it.smartcommunitylab.bridge.repository.OccupationRepository;

@Component
public class CogitoAnalyzer {
	private static final transient Logger logger = LoggerFactory.getLogger(CogitoAnalyzer.class);
	
	@Autowired
	@Value("${cogito.training_offers.url}")
	private String trainingOffersAnalyzerURL;
	
	@Autowired
	@Value("${cogito.personal_data.url}")
	private String personalDataAnalyzerURL;
	
	@Autowired
	LuceneManager luceneManager;
	
	@Autowired
	OccupationRepository occupationRepository;

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
	
	public CogitoProfile analyzePersonalData(InputStream stream) {
		BodyContentHandler handler = new BodyContentHandler();
    AutoDetectParser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();
    try {
    	parser.parse(stream, handler, metadata);
    	String content = handler.toString();
    	CogitoProfile profile = new CogitoProfile();
    	analyzePersonalData(content, profile);
    	analyzeDegrees(content, profile);
    	analyzeLanguages(content, profile);
    	analyzeITKnowledges(content, profile);
    	analyzeWorkExperiences(content, profile);
    	if(profile.getWorkExperiences().size() > 0) {
    		addOccupations(profile);
    	}
    	return profile;
		} catch (Exception e) {
			logger.warn("analyzePersonalData error:{}", e.getMessage());
		}
		return null;
	}
	
	private void analyzePersonalData(String content, 
			CogitoProfile profile) throws Exception {
		PersonalData result = new PersonalData();
		String aspect = analyzeAspect(personalDataAnalyzerURL + "personal_data", content);
		JsonNode rootNode = Utils.readJsonFromString(aspect);
		if(rootNode.hasNonNull("PERSONAL_DATA")) {
			result = Utils.toObject(rootNode.get("PERSONAL_DATA"), PersonalData.class);
			profile.setPersonalData(result);			
		}
	}
	
	private void analyzeLanguages(String content, 
			CogitoProfile profile) throws Exception {
		List<Language> languages = new ArrayList<>();
		String aspect = analyzeAspect(personalDataAnalyzerURL + "languages", content);
		JsonNode rootNode = Utils.readJsonFromString(aspect);
		if(rootNode.hasNonNull("LANGUAGES")) {
			for(JsonNode node : rootNode.get("LANGUAGES")) {
				Language language = Utils.toObject(node, Language.class);
				languages.add(language);
			}
		}
		if(languages.size() > 0) {
			profile.setLanguages(languages);
		}
	}
	
	private void analyzeDegrees(String content, 
			CogitoProfile profile) throws Exception {
		List<Degree> degrees = new ArrayList<>();
		String aspect = analyzeAspect(personalDataAnalyzerURL + "degrees", content);
		JsonNode rootNode = Utils.readJsonFromString(aspect);
		if(rootNode.hasNonNull("DEGREES")) {
			for(JsonNode node : rootNode.get("DEGREES")) {
				Degree degree = Utils.toObject(node, Degree.class);
				degrees.add(degree);
			}
		}
		if(degrees.size() > 0) {
			profile.setDegrees(degrees);
		}
	}
	
	private void analyzeWorkExperiences(String content, 
			CogitoProfile profile) throws Exception {
		List<WorkExperience> workExperiences = new ArrayList<>();
		String aspect = analyzeAspect(personalDataAnalyzerURL + "work_experiences", content);
		JsonNode rootNode = Utils.readJsonFromString(aspect);
		if(rootNode.hasNonNull("WORK_EXPERIENCES")) {
			for(JsonNode node : rootNode.get("WORK_EXPERIENCES")) {
				WorkExperience experience = Utils.toObject(node, WorkExperience.class);
				workExperiences.add(experience);
			}
		}
		if(workExperiences.size() > 0) {
			profile.setWorkExperiences(workExperiences);
		}
	}
	
	private void analyzeITKnowledges(String content, 
			CogitoProfile profile) throws Exception {
		List<String> itKnowledges = new ArrayList<>();
		String aspect = analyzeAspect(personalDataAnalyzerURL + "it_knowledge", content);
		JsonNode rootNode = Utils.readJsonFromString(aspect);
		if(rootNode.hasNonNull("IT_KNOWLEDGE")) {
			for(JsonNode node : rootNode.get("IT_KNOWLEDGE")) {
				itKnowledges.add(node.textValue());
			}
		}
		if(itKnowledges.size() > 0) {
			profile.setItKnowledges(itKnowledges);
		}
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
	
	private void addOccupations(CogitoProfile profile) {
		for(WorkExperience experience : profile.getWorkExperiences()) {
			for(String position : experience.getPOSITIONS()) {
				try {
					List<String> occupations = new ArrayList<>();
					List<String> iscoCodes = new ArrayList<>();
					List<TextDoc> iscoGroupTextList = luceneManager.searchByFields(position, Const.CONCEPT_ISCO_GROUP, null, 10);
					for(TextDoc textDoc : iscoGroupTextList) {
						if(textDoc.getScore() < 4.0) {
							continue;
						}
						String iscoCode = textDoc.getFields().get("iscoGroup");
						iscoCodes.add(iscoCode);
					}
					for(String iscoCode : iscoCodes) {
						List<TextDoc> occupationTextList = luceneManager.searchByFields(position, Const.CONCEPT_OCCCUPATION, iscoCode, 20);
						for(TextDoc textDoc : occupationTextList) {
							String uri = textDoc.getFields().get("uri");
							if(!occupations.contains(uri)) {
								Optional<Occupation> optional = occupationRepository.findById(uri);
								if(optional.isPresent()) {
									Occupation occupation = optional.get();
									occupations.add(occupation.getUri());
									ResourceLink link = new ResourceLink();
									link.setUri(occupation.getUri());
									link.setConceptType(occupation.getConceptType());
									link.setPreferredLabel(occupation.getPreferredLabel());
									experience.getOccupationsLink().add(link);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.warn("addOccupations error:{}", e.getMessage());
				}
			}
		}
	}

}
