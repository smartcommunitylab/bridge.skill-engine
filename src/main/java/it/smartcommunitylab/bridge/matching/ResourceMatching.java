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
import it.smartcommunitylab.bridge.model.ResourceLink;
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
			throw new EntityNotFoundException("occupation not found");
		}
		Occupation occupation = optional.get();
		String iscoCode = occupation.getIscoCode();
		iscoCode = iscoCode.length() > 3 ? iscoCode.substring(0, 3) : iscoCode;
		List<JobOffer> offers = jobOfferRepository.findByLocation(latitude, longitude, distance, iscoCode);
		for(JobOffer jobOffer : offers) {
			for(ResourceLink resourceLink : jobOffer.getOccupationsLink()) {
				if(resourceLink.getConceptType().equals(Const.CONCEPT_ISCO_GROUP)) {
					continue;
				}
				Optional<Occupation> optionalOcc = occupationRepository.findById(resourceLink.getUri());
				if(optionalOcc.isPresent()) {
					Occupation occ = optionalOcc.get();
					resourceLink.setMatching(checkSkillMatching(occ.getHasEssentialSkill(), profile));
				}
			}
		}		
		return offers;
	}
	
	private int checkSkillMatching(List<String> skills, Profile profile) {
		int tot = skills.size();
		int count = 0;
		if(tot == 0) {
			return 0;
		}
		for(String skill : skills) {
			if(profile.getSkills().contains(skill)) {
				count++;
			}
		}
		int percent = count * 100 / tot;
		return percent;
	}

}