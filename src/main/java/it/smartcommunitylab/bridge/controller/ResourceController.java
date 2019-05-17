package it.smartcommunitylab.bridge.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
import it.smartcommunitylab.bridge.model.Course;
import it.smartcommunitylab.bridge.model.JobOffer;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.Skill;

@RestController
public class ResourceController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(ResourceController.class);
	
	@GetMapping(value = "/api/skill")
	public Skill getSkill(@RequestParam String uri) throws Exception {
		Optional<Skill> optional = skillRepository.findById(uri);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException("resource not found");
		}
		logger.debug("getSkill:{}", uri);
		return optional.get();
	}
	
	@GetMapping(value = "/api/occupation")
	public Occupation getOccupation(@RequestParam String uri) throws Exception {
		Optional<Occupation> optional = occupationRepository.findById(uri);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException("resource not found");
		}
		logger.debug("getOccupation:{}", uri);
		return optional.get();		
	}
	
	@GetMapping(value = "/api/joboffer")
	public JobOffer getJobOffer(@RequestParam String id) throws Exception {
		Optional<JobOffer> optional = jobOfferRepository.findById(id);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException("resource not found");
		}
		logger.debug("getJobOffer:{}", id);
		return optional.get();				
	}
	
	@GetMapping(value = "/api/course")
	public Course getCourse(@RequestParam String id) throws Exception {
		Optional<Course> optional = courseRepository.findById(id);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException("resource not found");
		}
		logger.debug("getCourse:{}", id);
		return optional.get();				
	}

}
