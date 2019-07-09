package it.smartcommunitylab.bridge.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import it.smartcommunitylab.bridge.common.Const;
import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.JobOffer;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.Profile;
import it.smartcommunitylab.bridge.model.TextDoc;
import it.smartcommunitylab.bridge.repository.JobOfferRepository;
import it.smartcommunitylab.bridge.repository.OccupationRepository;
import it.smartcommunitylab.bridge.repository.ProfileRepository;
import it.smartcommunitylab.bridge.repository.SkillRepository;

@Component
public class ResourceMatching {
	
	@Autowired
	LuceneManager luceneManager;
	@Autowired
	OccupationRepository occupationRepository;
	@Autowired
	SkillRepository skillRepository;
	@Autowired
	ProfileRepository profileRepository;
	@Autowired
	JobOfferRepository jobOfferRepository;
	
	public List<Occupation> findOccupationBySkills(String text, 
			List<String> mandatorySkills) throws Exception {
		List<Occupation> result = new ArrayList<Occupation>();
		text = StringUtils.strip(text);
		List<TextDoc> docs = luceneManager.searchByFields(text, Const.CONCEPT_SKILL, null, 10);
		for(TextDoc textDoc : docs) {
			String uri = textDoc.getFields().get("uri");
			if(StringUtils.isEmpty(uri)) {
				continue;
			}
			List<String> skills = Lists.newArrayList(mandatorySkills);
			List<Occupation> list = occupationRepository.findByMandatorySkill(skills);
			for(Occupation occupation : list) {
				if(!result.contains(occupation)) {
					result.add(occupation);
				}
			}
		}
		return result;
	}
	
	public List<JobOffer> findJobOfferByProfile(String profileExtId, String occupationUri,
			double latitude, double longitude, double distance) throws Exception {
		Profile profile = profileRepository.findByExtId(profileExtId);
		if(profile == null) {
			throw new EntityNotFoundException("profile not found");
		}
		Optional<Occupation> optional = occupationRepository.findById(occupationUri);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException("profile not found");
		}
		Occupation occupation = optional.get();
		String iscoCode = occupation.getIscoCode();
		if(iscoCode.length() > 3) {
			iscoCode = iscoCode.substring(0, 3);
		}
		List<JobOffer> offers = jobOfferRepository.findByLocation(latitude, longitude, distance, iscoCode);
		
		return null;
	}

}
