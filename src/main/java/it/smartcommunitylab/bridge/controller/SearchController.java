package it.smartcommunitylab.bridge.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.common.Const;
import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.matching.ResourceMatching;
import it.smartcommunitylab.bridge.model.Course;
import it.smartcommunitylab.bridge.model.CourseResult;
import it.smartcommunitylab.bridge.model.JobOffer;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.Skill;
import it.smartcommunitylab.bridge.model.SuggestedCourse;
import it.smartcommunitylab.bridge.model.TextDoc;

@RestController
public class SearchController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	LuceneManager luceneManager;
	@Autowired
	ResourceMatching resourceMatching;

	@GetMapping(value = "/api/search/label")
	public List<TextDoc> searchByLabel(
			@RequestParam(required = false) String conceptType,
			@RequestParam String text) throws Exception {
		List<TextDoc> result = null;
		text = StringUtils.strip(text);
		if(StringUtils.isEmpty(conceptType)) {
			result = luceneManager.searchByLabel(text, 20, "preferredLabelNormalized", "altLabelsNormalized");
		} else {
			result = luceneManager.searchByFields(text, conceptType, null, 20);
		}
		logger.info("searchByLabel:{}/{}", result.size(), text);
		return result;
	}
	
	@GetMapping(value = "/api/search/skill")
	public List<Skill> searchSkill(
			@RequestParam String text,
			@RequestParam(required = false) boolean skillGroup,
			@RequestParam int size) throws Exception {
		List<Skill> result = new ArrayList<>();
		List<TextDoc> docs = null;
		text = StringUtils.strip(text);
		if(skillGroup) {
			docs = luceneManager.searchByFields(text, Const.CONCEPT_SKILL_GROUP, null, size);
		} else {
			docs = luceneManager.searchByFields(text, Const.CONCEPT_SKILL, null, size);
		}
		for (TextDoc textDoc : docs) {
			String uri = textDoc.getFields().get("uri");
			Optional<Skill> optional = skillRepository.findById(uri);
			if(optional.isPresent()) {
				Skill skill = optional.get();
				result.add(skill);
			}
		}
		logger.info("searchSkill:{}/{}", result.size(), text);
		return result;
	}
	
	@GetMapping(value = "/api/search/occupation")
	public List<Occupation> searchOccupation(
			@RequestParam String text,
			@RequestParam(required = false) boolean iscoGroup,
			@RequestParam(required = false) String iscoCode,
			@RequestParam int size) throws Exception {
		List<Occupation> result = new ArrayList<>();
		List<TextDoc> docs = null;
		text = StringUtils.strip(text);
		if(iscoGroup) {
			docs = luceneManager.searchByFields(text, Const.CONCEPT_ISCO_GROUP, iscoCode, size);
		} else {
			docs = luceneManager.searchByFields(text, Const.CONCEPT_OCCCUPATION, iscoCode, size);
		}
		for (TextDoc textDoc : docs) {
			String uri = textDoc.getFields().get("uri");
			Optional<Occupation> optional = occupationRepository.findById(uri);
			if(optional.isPresent()) {
				Occupation occupation = optional.get();
				result.add(occupation);
			}
		}
		logger.info("searchOccupation:{}/{}", result.size(), text);
		return result;
	}
	
	@GetMapping(value = "/api/search/course")
	public List<Course> searchCourse(
			@RequestParam double latitude,
			@RequestParam double longitude,
			@RequestParam double distance,
			@RequestParam(required = false) List<String> skills) throws Exception {
		List<Course> result = courseRepository.findByLocation(latitude, longitude, distance, skills);
		logger.info("searchCourse:{}/{}/{}/{}", result.size(), latitude + "," + longitude, distance, skills);
		return result;
	}
	
	@GetMapping(value = "/api/search/joboffer")
	public List<JobOffer> searchJobOffer(
			@RequestParam double latitude,
			@RequestParam double longitude,
			@RequestParam double distance,
			@RequestParam(required = false) String iscoCode) throws Exception {
		List<JobOffer> result = jobOfferRepository.findByLocation(latitude, longitude, distance, iscoCode);
		logger.info("searchJobOffer:{}/{}/{}/{}", result.size(), latitude + "," + longitude, distance, iscoCode);
		return result;
	}
	
	@GetMapping(value = "/api/search/jobofferbyprofile")
	public List<JobOffer> searchJobOfferByProfileAndOccupation(
			@RequestParam double latitude,
			@RequestParam double longitude,
			@RequestParam double distance,
			@RequestParam String iscoCode,
			@RequestParam String extId) throws Exception {
		List<JobOffer> result = resourceMatching.findJobOfferByProfile(extId, iscoCode, 
				latitude, longitude, distance);
		logger.info("searchJobOfferByProfileAndOccupation:{}/{}/{}/{}/{}", result.size(), 
				latitude + "," + longitude, distance, iscoCode, extId);
		return result;
	}
	
	@GetMapping(value = "/api/search/coursebyprofile")
	public List<CourseResult> searchCourseByProfileAndOccupation(
			@RequestParam double latitude,
			@RequestParam double longitude,
			@RequestParam double distance,
			@RequestParam String occupationUri,
			@RequestParam String extId) throws Exception {
		List<CourseResult> result = resourceMatching.findCourseByProfile(extId, occupationUri, 
				latitude, longitude, distance);
		logger.info("searchCourseByProfileAndOccupation:{}/{}/{}/{}/{}", result.size(), 
				latitude + "," + longitude, distance, occupationUri, extId);
		return result;
	}
	
	@GetMapping(value = "/api/search/suggestedcourses")
	public Map<String, SuggestedCourse> searchSuggestedCourses() {
		Map<String, SuggestedCourse> result = resourceMatching.findSuggestedCourses();
		logger.info("searchSuggestedCourses:{}", result.size());
		return result;
	}
	
	@GetMapping(value = "/api/search/suggestedjobs")
	public List<ResourceLink> searchSuggestedJobs() {
		List<ResourceLink> result = resourceMatching.findSuggestedJobs();
		logger.info("searchSuggestedJobs:{}", result.size());
		return result;
	}
	
}
