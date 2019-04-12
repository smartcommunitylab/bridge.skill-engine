package it.smartcommunitylab.bridge.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.matching.OccupationMatching;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.TextDoc;
import it.smartcommunitylab.bridge.repository.OccupationRepository;
import it.smartcommunitylab.bridge.repository.SkillRepository;

@RestController
public class SearchController {
	private static final transient Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	LuceneManager luceneManager;
	@Autowired
	OccupationRepository occupationRepository;
	@Autowired
	SkillRepository skillRepository;
	@Autowired
	OccupationMatching occupationMatching;

	@GetMapping(value = "/api/search/label")
	public List<TextDoc> searchByLabel(
			@RequestParam(required = false) String concetType,
			@RequestParam String text) throws Exception {
		List<TextDoc> result = null;
		text = StringUtils.strip(text);
		if(StringUtils.isEmpty(concetType)) {
			result = luceneManager.searchByLabel(text, 20, "preferredLabel", "altLabels");
		} else {
			result = luceneManager.searchByLabelAndType(text, concetType, 20, 
					"preferredLabel", "altLabels");
		}
		logger.debug("searchByLabel:{} / {}", result.size(), text);
		return result;
	}
	
	@PostMapping(value = "/api/match/occupation/skills")
	public List<Occupation> findOccupationBySkills(
			@RequestBody List<String> skills) throws Exception {
		List<Occupation> result = occupationRepository.findByMandatorySkill(skills);
		logger.debug("findOccupationBySkills:{}", result.size());
		return result;
	}
	
	@GetMapping(value = "/api/match/occupation/isco")
	public List<Occupation> findOccupationByIscoCode(
			@RequestParam String iscoCode) throws Exception {
		List<Occupation> result = occupationRepository.findByIscoCode("^" + iscoCode);
		logger.debug("findOccupationByIscoCode:{}", result.size());
		return result;
	}
}
