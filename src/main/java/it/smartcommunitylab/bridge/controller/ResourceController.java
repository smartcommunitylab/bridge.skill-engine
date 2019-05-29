package it.smartcommunitylab.bridge.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
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
	
	@GetMapping(value = "/api/skills")
	public List<Skill> findSkill(
			@RequestParam(required = false) List<String> isEssentialForOccupation,
			@RequestParam(required = false) List<String> isOptionalForOccupation,
			@RequestParam(required = false) boolean skillGroup,
			Pageable pageable) throws Exception {
		List<Skill> list = skillRepository.findSkill(skillGroup, isEssentialForOccupation, 
				isOptionalForOccupation, pageable);
		logger.debug("findSkill:{}", list.size());
		return list;
	}
	
	@GetMapping(value = "/api/occupations")
	public List<Occupation> findOccupation(
			@RequestParam(required = false) List<String> hasEssentialSkill,
			@RequestParam(required = false) List<String> hasOptionalSkill,
			@RequestParam(required = false) String iscoCode,
			@RequestParam(required = false) boolean iscoGroup,
			Pageable pageable) throws Exception {
		List<Occupation> list = occupationRepository.findOccupation(iscoGroup, iscoCode, 
				hasEssentialSkill, hasOptionalSkill, pageable);
		logger.debug("findOccupation:{}", list.size());
		return list;
	}
	
}
