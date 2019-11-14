package it.smartcommunitylab.bridge.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.matching.ResourceMatching;
import it.smartcommunitylab.bridge.model.CourseResult;
import it.smartcommunitylab.bridge.model.JobOffer;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.Profile;
import it.smartcommunitylab.bridge.model.ProfileResult;

@RestController
public class KpiController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(KpiController.class);
	
	@Autowired
	ResourceMatching resourceMatching;
	
	List<String> preferredOccupations = new ArrayList<String>();
	
	@PostConstruct
	public void init() {
		preferredOccupations.add("http://data.europa.eu/esco/occupation/1c1e86f9-6347-42d6-aa6e-c95b138bf640");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/fb7e2f4f-1545-42f1-972e-94082e49c6dc");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/731ecac4-06e8-4ec2-a559-101fecbd9183");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/303a1e34-cb16-4054-b323-81e5eec17397");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/572c9810-e1ee-4f7a-a71f-820f17c0c25b");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/f756fdab-7726-4c48-bfcc-94ff8810fc08");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/47300afa-5496-484c-8f65-14247a98b2b8");	
		preferredOccupations.add("http://data.europa.eu/esco/occupation/245be6d1-fe9a-4ac8-9f81-122a687e4724");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/05f321f8-055b-407d-bf19-e0ddabda56b7");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/90f75f67-495d-49fa-ab57-2f320e251d7e");
		preferredOccupations.add("http://data.europa.eu/esco/occupation/a10eb17a-3c78-4f7a-a1da-8f31146339d3");
	}
	
	@GetMapping(value = "/api/kpi/all")
	public List<ProfileResult> getAllKpi(
			@RequestParam double latitude,
			@RequestParam double longitude,
			@RequestParam double distance) {
		Map<String, Occupation> occupationMap = new HashMap<>();
		List<Occupation> occupationList = occupationRepository.findAll();
		for(Occupation occupation : occupationList) {
			occupationMap.put(occupation.getUri(), occupation);
		}		
		List<ProfileResult> result = new ArrayList<>();
		List<Profile> profiles = profileRepository.findAll(Sort.by(Direction.ASC, "extId"));
		for(Profile profile : profiles) {
			String preferredOccupationUri = checkOccupation(profile);
			if(preferredOccupationUri != null) {
				Occupation preferredOccupation = occupationMap.get(preferredOccupationUri);
				if(preferredOccupation != null) {
					try {
						List<CourseResult> courseByProfile = resourceMatching.findCourseByProfile(profile.getExtId(), 
								preferredOccupationUri, latitude, longitude, distance);
						List<JobOffer> jobOfferByProfile = resourceMatching.findJobOfferByProfile(profile.getExtId(), 
								preferredOccupation.getIscoCode(), latitude, longitude, distance);
						ProfileResult profileResult = new ProfileResult();
						profileResult.setExtId(profile.getExtId());
						profileResult.setOccupationUri(preferredOccupationUri);
						profileResult.setIscoCode(preferredOccupation.getIscoCode());
						profileResult.getCourses().addAll(courseByProfile);
						profileResult.getJobOffers().addAll(jobOfferByProfile);
						result.add(profileResult);
					} catch (Exception e) {
						logger.warn("getAllKpi error:{}/{}/{}", profile.getExtId(), preferredOccupationUri, e.getMessage());
					}							
				}
			}			
			logger.info("getAllKpi:{}", profile.getExtId());
		}
		logger.info("getAllKpi:{}", result.size());
		return result;
	}
	
	private String checkOccupation(Profile profile) {
		for(String occupationUri : preferredOccupations) {
			if(profile.getOccupations().contains(occupationUri)) {
				return occupationUri;
			}
		}
		return null;
	}
	
}
