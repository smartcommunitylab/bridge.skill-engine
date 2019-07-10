package it.smartcommunitylab.bridge.controller;

import org.reflections.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
import it.smartcommunitylab.bridge.exception.StorageException;
import it.smartcommunitylab.bridge.model.Profile;

@RestController
public class ProfileController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(ProfileController.class);
	
	@GetMapping(value = "/api/profile")
	public Profile getProfile(@RequestParam String extId) throws Exception {
		Profile profile = profileRepository.findByExtId(extId);
		if(profile == null) {
			throw new EntityNotFoundException("entity not found");
		}
		logger.info("getProfile:{}", extId);
		return profile;
	}
	
	@PostMapping(value = "/api/profile")
	public Profile saveProfile(@RequestBody Profile profile) throws Exception {
		if(Utils.isEmpty(profile.getExtId())) {
			throw new StorageException("extId not present");
		}
		Profile profileDb = profileRepository.findByExtId(profile.getExtId());
		if(profileDb != null) {
			profile.setId(profileDb.getId());
		}
		profile.setOccupationsLink(completeOccupationLink(profile.getOccupations()));
		profile.setSkillsLink(completeSkillLink(profile.getSkills()));
		profileRepository.save(profile);
		logger.info("saveProfile:{}", profile.getExtId());
		return profile;
	}
	
	@DeleteMapping(value = "/api/profile")
	public Profile deleteProfile(@RequestParam String extId) throws Exception {
		Profile profile = profileRepository.findByExtId(extId);
		if(profile == null) {
			throw new EntityNotFoundException("entity not found");
		}
		profileRepository.delete(profile);
		logger.info("deleteProfile:{}", extId);
		return profile;
	}
	
}
