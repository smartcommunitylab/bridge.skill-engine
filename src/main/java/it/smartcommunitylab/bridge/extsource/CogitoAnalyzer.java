package it.smartcommunitylab.bridge.extsource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.lucene.queryparser.classic.ParseException;
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
import it.smartcommunitylab.bridge.model.JobOffer;
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
			JsonNode tjNode = jsonNode.get("TRAININGS");
			if(tjNode != null) {
				for (JsonNode tnode : tjNode) {
					JsonNode tList = tnode.get("TRAININGS");
					for(JsonNode node : tList) {
						String training = node.textValue();
						if(Utils.isNotEmpty(training)) {
							training = training.toLowerCase();
							if(!course.getCogitoAnalysis().contains(training)) {
								course.getCogitoAnalysis().add(training);
							}
						}
					}
				}				
			}
		} catch (Exception e) {
			logger.warn("analyzeCourse error:{}", e.getMessage());
		}
	}
	
	public void analyzeJobOffer(JobOffer jobOffer) {
		try {
			String jsonString = HTTPUtils.post(trainingOffersAnalyzerURL, jobOffer.getDescription(), null, null, null);
			JsonNode jsonNode = Utils.readJsonFromString(jsonString);
			JsonNode tjNode = jsonNode.get("JOB_OFFERS");
			if(tjNode != null) {
				for (JsonNode tnode : tjNode) {
					JsonNode tList = tnode.get("OFFERS");
					for(JsonNode node : tList) {
						String offer = node.textValue();
						if(Utils.isNotEmpty(offer)) {
							offer = offer.toLowerCase();
							if(!jobOffer.getCogitoAnalysis().contains(offer)) {
								jobOffer.getCogitoAnalysis().add(offer);
							}
						}
					}
				}				
			}
		} catch (Exception e) {
			logger.warn("analyzeJobOffer error:{}", e.getMessage());
		}
	}
	
	public CogitoProfile analyzePersonalData(File file) {
    try {
    	String json = HTTPUtils.uploadMultipartFile(personalDataAnalyzerURL + "all_by_file", file);
    	JsonNode rootNode = Utils.readJsonFromString(json);
    	CogitoProfile profile = new CogitoProfile();
    	profile.setFilename(file.getName());
    	analyzePersonalData(rootNode, profile);
    	analyzeDegrees(rootNode, profile);
    	analyzeLanguages(rootNode, profile);
    	analyzeITKnowledges(rootNode, profile);
    	analyzeWorkExperiences(rootNode, profile);
    	if(profile.getWorkExperiences().size() > 0) {
    		addOccupationsByIscoGroupAndName(profile);
    	}
    	return profile;
		} catch (Exception e) {
			logger.warn("analyzePersonalData error:{}", e.getMessage());
		}
		return null;
	}
	
	private List<String> getMultipleValues(JsonNode jsonNode, String arrayField, String... fields) {
		List<String> result = new ArrayList<>();
		JsonNode arrayNode = jsonNode.findValue(arrayField);
		if(arrayNode != null) {
			for(JsonNode node : arrayNode) {
				for(String field : fields) {
					if(node.hasNonNull(field)) {
						result.add(node.get(field).asText());
					}
				}
			}							
		}
		return result;
	}
	
	private List<JsonNode> mergeArrayNodes(JsonNode jsonNode, String... fields) {
		List<JsonNode> result = new ArrayList<>();
		for(String field :  fields) {
			JsonNode arrayNode = jsonNode.findValue(field);
			if(arrayNode != null) {
				for(JsonNode node : arrayNode) {
					result.add(node);
				}				
			}
		}
		return result;
	}
	
	private void analyzePersonalData(JsonNode rootNode, 
			CogitoProfile profile) throws Exception {
		PersonalData result = new PersonalData();
		JsonNode jsonNode = rootNode.findValue("PERSONAL_DATA");
		if(jsonNode != null) {
			result = Utils.toObject(jsonNode, PersonalData.class);
			profile.setPersonalData(result);			
		}
	}
	
	private void analyzeLanguages(JsonNode rootNode, 
			CogitoProfile profile) throws Exception {
		List<Language> languages = new ArrayList<>();
		JsonNode jsonNode = rootNode.findValue("LANGUAGES");
		if(jsonNode != null) {
			for(JsonNode node : jsonNode) {
				Language language = Utils.toObject(node, Language.class);
				languages.add(language);
			}
		}
		if(languages.size() > 0) {
			profile.setLanguages(languages);
		}
	}
	
	private void analyzeDegrees(JsonNode rootNode, 
			CogitoProfile profile) throws Exception {
		List<Degree> degrees = new ArrayList<>();
		List<JsonNode> mergeNodes = mergeArrayNodes(rootNode, "DEGREES", "DEGREES_NO_CV");
		for(JsonNode node : mergeNodes) {
			Degree degree = new Degree();
			if(node.hasNonNull("DESCRIPTION")) {
				degree.setDescription(node.get("DESCRIPTION").asText());
			}
			degree.setSubjects(getMultipleValues(node, "SUBJECTS", "SUBJECT", "SUBJECT_NO_CV"));
			degree.setYears(getMultipleValues(node, "YEARS", "YEAR", "YEAR_NO_CV"));
			degree.setPatenti(getMultipleValues(node, "PATENTI", "PATENTE"));
			degree.setOrganizations(getMultipleValues(node, "ORGANIZATIONS", "ORGANIZATION", "ORGANIZATION_NO_CV"));
			degrees.add(degree);
		}
		if(degrees.size() > 0) {
			profile.setDegrees(degrees);
		}
	}
	
	private void analyzeWorkExperiences(JsonNode rootNode, 
			CogitoProfile profile) throws Exception {
		List<WorkExperience> workExperiences = new ArrayList<>();
		JsonNode jsonNode = rootNode.findValue("WORK_EXPERIENCES");
		if(jsonNode != null) {
			for(JsonNode node : jsonNode) {
				WorkExperience experience = Utils.toObject(node, WorkExperience.class);
				workExperiences.add(experience);
			}
		}
		if(workExperiences.size() > 0) {
			profile.setWorkExperiences(workExperiences);
		}
	}
	
	private void analyzeITKnowledges(JsonNode rootNode, 
			CogitoProfile profile) throws Exception {
		List<String> itKnowledges = new ArrayList<>();
		List<JsonNode> mergeNodes = mergeArrayNodes(rootNode, "IT_KNOWLEDGE", "IT_KNOWLEDGE_NO_CV");
		for(JsonNode node : mergeNodes) {
			itKnowledges.add(node.textValue());
		}
		if(itKnowledges.size() > 0) {
			profile.setItKnowledges(itKnowledges);
		}
	}
	
	@SuppressWarnings("unused")
	private void addOccupationsByName(CogitoProfile profile) {
		for(WorkExperience experience : profile.getWorkExperiences()) {
			if(experience.getPOSITIONS().size() == 0) {
				continue;
			}
			List<String> occupations = new ArrayList<>();
			for(String position : experience.getPOSITIONS()) {
				try {
					List<TextDoc> iscoGroupTextList = luceneManager.searchByFields(position, 
							Const.CONCEPT_OCCCUPATION, null, 10);
					addOccupation(experience, occupations, iscoGroupTextList);
				} catch (Exception e) {
					logger.warn("addOccupations error:{}", e.getMessage());
				}
			}
			logger.info("addOccupations:{}", Utils.writeJson(experience));
		}
	}
	
	private void addOccupationsByIscoGroupAndName(CogitoProfile profile) {
		for(WorkExperience experience : profile.getWorkExperiences()) {
			if(experience.getPOSITIONS().size() == 0) {
				continue;
			}
			//String position = String.join(",", experience.getPOSITIONS());
			for(String position : experience.getPOSITIONS()) {
				if(Utils.isNotEmpty(position)) {
					List<String> occupations = new ArrayList<>();
					String iscoCode = null;
					try {
						List<TextDoc> iscoGroupTextList = luceneManager.searchByFields(position, Const.CONCEPT_ISCO_GROUP, null, 1);
						if(!iscoGroupTextList.isEmpty()) {
							TextDoc textDoc = iscoGroupTextList.get(0);
							double roundOff = Utils.round(textDoc.getScore(), 1);
							if(roundOff >= 4.5) {
								iscoCode = textDoc.getFields().get("iscoGroup");
							}
						}
					} catch (Exception e) {
						logger.warn("addOccupations error:{}", e.getMessage());
					}
					if(Utils.isNotEmpty(iscoCode) && (iscoCode.length() > 3)) {
						try {
							List<TextDoc> occupationTextList = luceneManager.searchByFields(position, Const.CONCEPT_OCCCUPATION, iscoCode, 3);
							addOccupation(experience, occupations, occupationTextList);
						} catch (Exception e) {
							logger.warn("addOccupations error:{}", e.getMessage());
						}
					}
					if(occupations.size() == 0) {
						try {
							List<TextDoc> occupationTextList = luceneManager.searchByFields(position, Const.CONCEPT_OCCCUPATION, null, 3);
							addOccupation(experience, occupations, occupationTextList);
						} catch (ParseException | IOException e) {
							logger.warn("addOccupations error:{}", e.getMessage());
						}
					}
				}
			}
			logger.info("addOccupations:{}", Utils.writeJson(experience));
		}
	}

	private void addOccupation(WorkExperience experience, List<String> occupations, List<TextDoc> occupationTextList) {
		for(TextDoc textDoc : occupationTextList) {
			addOccupation(experience, occupations, textDoc);
		}
	}
	
	private boolean addOccupation(WorkExperience experience, List<String> occupations, TextDoc textDoc) {
		double roundOff = Utils.round(textDoc.getScore(), 1);
		if(roundOff < 4.5) {
			return false;
		}
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
				return true;
			}
		}
		return false;
	}
	
}
