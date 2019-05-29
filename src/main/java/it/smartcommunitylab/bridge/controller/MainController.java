package it.smartcommunitylab.bridge.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
import it.smartcommunitylab.bridge.exception.StorageException;
import it.smartcommunitylab.bridge.exception.UnauthorizedException;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.Skill;
import it.smartcommunitylab.bridge.repository.CourseRepository;
import it.smartcommunitylab.bridge.repository.JobOfferRepository;
import it.smartcommunitylab.bridge.repository.OccupationRepository;
import it.smartcommunitylab.bridge.repository.ProfileRepository;
import it.smartcommunitylab.bridge.repository.SkillRepository;

@Controller
public class MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(MainController.class);
			
	@Autowired
	OccupationRepository occupationRepository;
	@Autowired
	SkillRepository skillRepository;
	@Autowired
	JobOfferRepository jobOfferRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	ProfileRepository profileRepository;
	
	public List<ResourceLink> completeSkillLink(List<String> skills) {
		List<ResourceLink> result = new ArrayList<>();
		for (String uri : skills) {
			Optional<Skill> optional = skillRepository.findById(uri);
			if(optional.isPresent()) {
				Skill skill = optional.get();
				ResourceLink rLink = new ResourceLink();
				rLink.setUri(skill.getUri());
				rLink.setConceptType(skill.getConceptType());
				rLink.setPreferredLabel(skill.getPreferredLabel());
				result.add(rLink);
			}
		}
		return result;
	}
	
	public List<ResourceLink> completeOccupationLink(List<String> occupations) {
		List<ResourceLink> result = new ArrayList<>();
		for (String uri : occupations) {
			Optional<Occupation> optional = occupationRepository.findById(uri);
			if(optional.isPresent()) {
				Occupation occupation = optional.get();
				ResourceLink rLink = new ResourceLink();
				rLink.setUri(occupation.getUri());
				rLink.setConceptType(occupation.getConceptType());
				rLink.setPreferredLabel(occupation.getPreferredLabel());
				result.add(rLink);
			}
		}
		return result;
	}
	
	public void completeSkill(Skill skill) {
		List<Skill> broaderSkill = skillRepository.findByIds(skill.getBroaderSkill());
		for(Skill bSkill : broaderSkill) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(bSkill.getPreferredLabel());
			rLink.setUri(bSkill.getUri());
			skill.getBroaderSkillLink().add(rLink);
		}
		List<Skill> narrowerSkill = skillRepository.findByIds(skill.getNarrowerSkill());
		for(Skill nSkill : narrowerSkill) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(nSkill.getPreferredLabel());
			rLink.setUri(nSkill.getUri());
			skill.getNarrowerSkillLink().add(rLink);
		}
		List<Occupation> isEssentialForOccupation = occupationRepository.findByIds(skill.getIsEssentialForOccupation());
		for(Occupation eOccupation : isEssentialForOccupation) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(eOccupation.getPreferredLabel());
			rLink.setUri(eOccupation.getUri());
			skill.getIsEssentialForOccupationLink().add(rLink);
		}		
		List<Occupation> isOptionalForOccupation = occupationRepository.findByIds(skill.getIsOptionalForOccupation());
		for(Occupation oOccupation : isOptionalForOccupation) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(oOccupation.getPreferredLabel());
			rLink.setUri(oOccupation.getUri());
			skill.getIsOptionalForOccupationLink().add(rLink);
		}
	}
	
	public void completeOccupation(Occupation occupation) {
		List<Skill> essentialSkill = skillRepository.findByIds(occupation.getHasEssentialSkill());
		for(Skill eSkill : essentialSkill) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(eSkill.getPreferredLabel());
			rLink.setUri(eSkill.getUri());
			occupation.getHasEssentialSkillLink().add(rLink);
		}
		List<Skill> optionalSkill = skillRepository.findByIds(occupation.getHasOptionalSkill());
		for(Skill oSkill : optionalSkill) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(oSkill.getPreferredLabel());
			rLink.setUri(oSkill.getUri());
			occupation.getHasOptionalSkillLink().add(rLink);
		}
		List<Occupation> narrowerOccupation = occupationRepository.findByIds(occupation.getNarrowerOccupation());
		for(Occupation nOccupation : narrowerOccupation) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(nOccupation.getPreferredLabel());
			rLink.setUri(nOccupation.getUri());
			occupation.getNarrowerOccupationLink().add(rLink);
		}		
		List<Occupation> broaderOccupation = occupationRepository.findByIds(occupation.getBroaderOccupation());
		for(Occupation bOccupation : broaderOccupation) {
			ResourceLink rLink = new ResourceLink();
			rLink.setPreferredLabel(bOccupation.getPreferredLabel());
			rLink.setUri(bOccupation.getUri());
			occupation.getNarrowerOccupationLink().add(rLink);
		}
	}

	@ExceptionHandler({EntityNotFoundException.class, StorageException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error("controller error:{} / {}", request.getRequestURL(), exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error("controller error:{} / {}", request.getRequestURL(), exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error("controller error:{} / {}", request.getRequestURL(), exception.getMessage());
		return Utils.handleError(exception);
	}	
}
