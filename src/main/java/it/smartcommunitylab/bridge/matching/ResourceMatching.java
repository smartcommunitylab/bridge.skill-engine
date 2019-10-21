package it.smartcommunitylab.bridge.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import it.smartcommunitylab.bridge.common.Const;
import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.Course;
import it.smartcommunitylab.bridge.model.CourseResult;
import it.smartcommunitylab.bridge.model.JobOffer;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.Profile;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.SuggestedCourse;
import it.smartcommunitylab.bridge.model.TextDoc;
import it.smartcommunitylab.bridge.repository.CourseRepository;
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
	@Autowired
	CourseRepository courseRepository;
	
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
	
	public List<JobOffer> findJobOfferByProfile(String profileExtId, String iscoCode,
			double latitude, double longitude, double distance) throws Exception {
		Profile profile = profileRepository.findByExtId(profileExtId);
		if(profile == null) {
			throw new EntityNotFoundException("profile not found");
		}
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
	
	public List<CourseResult> findCourseByProfile(String profileExtId, String uri, 
			double latitude, double longitude, double distance) throws Exception {
		Profile profile = profileRepository.findByExtId(profileExtId);
		if(profile == null) {
			throw new EntityNotFoundException("profile not found");
		}
		Optional<Occupation> optional = occupationRepository.findById(uri);
		if(optional.isEmpty()) {
			throw new EntityNotFoundException("occupation not found");
		}
		Occupation occupation = optional.get();
		List<String> skillsToSearch = new ArrayList<String>();
		for(String skill : occupation.getHasEssentialSkill()) {
			if(!profile.getSkills().contains(skill)) {
				skillsToSearch.add(skill);
			}
		}
		List<Course> courses = courseRepository.findByLocation(latitude, longitude, distance, skillsToSearch);
		Map<String, CourseResult> result = new HashMap<>(); 
		for(Course course : courses) {
			CourseResult courseResult = result.get(course.getTitle());
			if(courseResult == null) {
				courseResult = new CourseResult();
				courseResult.setTitle(course.getTitle());
				courseResult.setMatching(checkSkillMissed(course, skillsToSearch));
				courseResult.setCoverage(countSkillMissed(course, skillsToSearch));
				courseResult.getCourses().add(course);
				result.put(course.getTitle(), courseResult);
			} else {
				courseResult.getCourses().add(course);
			}
		}
		return new ArrayList<CourseResult>(result.values());
	}
	
	private int countSkillMissed(Course course, List<String> skillsToSearch) {
		int count = 0;
		for(String skill : course.getSkills()) {
			if(skillsToSearch.contains(skill)) {
				count++;
			}
		}
		return count;
	}
	
	private int checkSkillMissed(Course course, List<String> skillsToSearch) {
		int tot = skillsToSearch.size();
		int count = 0;
		if(tot == 0) {
			return 0;
		}
		for(String skill : course.getSkills()) {
			if(skillsToSearch.contains(skill)) {
				count++;
			}
		}
		int percent = count * 100 / tot;
		return percent;
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
	
	public Map<String, SuggestedCourse> findSuggestedCourses() {
		//<occupation, count profiles>
		Map<Occupation, Integer> occupationMap = new HashMap<Occupation, Integer>();
		//<skillUri, count skill missing>
		Map<String, Integer> skillMap = new HashMap<String, Integer>();
		List<Profile> profiles = profileRepository.findAll();
		for(Profile profile : profiles) {
			for(String occupationUri : profile.getOccupations()) {
				Optional<Occupation> optionalOccupation = occupationRepository.findById(occupationUri);
				if(optionalOccupation.isPresent()) {
					Occupation occupation = optionalOccupation.get();
					if(occupationMap.containsKey(occupation)) {
						occupationMap.put(occupation, occupationMap.get(occupation) + 1);
					} else {
						occupationMap.put(occupation, 1);
					}
					for(String skillUri : occupation.getHasEssentialSkill()) {
						if(!profile.getSkills().contains(skillUri)) {
							if(skillMap.containsKey(skillUri)) {
								skillMap.put(skillUri, skillMap.get(skillUri) + 1);
							} else {
								skillMap.put(skillUri, 1);
							}
						}
					}
				}
			}
		}
		Map<String, SuggestedCourse> suggestedCourseMap = new HashMap<>(); 
		List<Entry<Occupation, Integer>> occupationEntrySet = new ArrayList<>(occupationMap.entrySet());
		occupationEntrySet.sort(Entry.comparingByValue());
		Collections.reverse(occupationEntrySet);
		for(int i=0; (i < occupationEntrySet.size() && i < 20); i++) {
			Entry<Occupation, Integer> entry = occupationEntrySet.get(i);
			Occupation occupation = entry.getKey();
			List<Course> courses = courseRepository.findBySkillsIn(occupation.getHasEssentialSkill());
			for(Course course : courses) {
				SuggestedCourse suggestedCourse = suggestedCourseMap.get(course.getTitle());
				if(suggestedCourse == null) {
					suggestedCourse = new SuggestedCourse();
					suggestedCourse.setTitle(course.getTitle());
					suggestedCourse.setContent(course.getContent());
					suggestedCourseMap.put(course.getTitle(), suggestedCourse);
				}
				addOccupationLink(occupation, suggestedCourse, occupationMap);
				addSkillLink(course, suggestedCourse, skillMap);
			}
			i++;
		}
		return suggestedCourseMap;
	}
	
	private void addOccupationLink(Occupation occupation, SuggestedCourse suggestedCourse, 
			Map<Occupation, Integer> occupationMap) {
		boolean found = false;
		for(ResourceLink occupationLink : suggestedCourse.getOccupationsLink()) {
			if(occupation.getUri().equals(occupationLink.getUri())) {
				found = true;
				break;
			}
		}
		if(!found) {
			ResourceLink occupationLink = new ResourceLink();
			occupationLink.setConceptType(occupation.getConceptType());
			occupationLink.setUri(occupation.getUri());
			occupationLink.setPreferredLabel(occupation.getPreferredLabel());
			occupationLink.setMatching(occupationMap.get(occupation));
			suggestedCourse.getOccupationsLink().add(occupationLink);
		}
	}
	
	private void addSkillLink(Course course, SuggestedCourse suggestedCourse, 
			Map<String, Integer> skillMap) {
		for(ResourceLink skillLink : course.getSkillsLink()) {
			boolean found = false;
			for(ResourceLink suggestedSkillLink : suggestedCourse.getSkillsLink()) {
				if(skillLink.getUri().equals(suggestedSkillLink.getUri())) {
					found = true;
					break;
				}
			}
			if(!found) {
				if(skillMap.containsKey(skillLink.getUri())) {
					skillLink.setMatching(skillMap.get(skillLink.getUri()));
					suggestedCourse.getSkillsLink().add(skillLink);
				}
			}
		}
	}
	
	public List<ResourceLink> findSuggestedJobs() {
		Map<String, Occupation> occupationMap = new HashMap<>();
		Map<String, Integer> jobCountMap = new HashMap<>();
		Map<String, ResourceLink> iscoGroupMap = new HashMap<>();
		List<Occupation> occupationList = occupationRepository.findAll();
		for(Occupation occupation : occupationList) {
			occupationMap.put(occupation.getUri(), occupation);
		}
		List<Profile> profiles = profileRepository.findAll();
		List<JobOffer> jobList = jobOfferRepository.findAll();
		for(JobOffer jobOffer : jobList) {
			for(ResourceLink link : jobOffer.getOccupationsLink()) {
				ResourceLink iscoGroupLink = getIscoGroup(link.getUri(), iscoGroupMap, occupationMap);
				if(iscoGroupLink != null) {
					int count = checkOccupation(iscoGroupLink.getUri(), profiles, iscoGroupMap, occupationMap);
					if(!jobCountMap.containsKey(iscoGroupLink.getUri())) {
						jobCountMap.put(iscoGroupLink.getUri(), count);
					} else {
						jobCountMap.put(iscoGroupLink.getUri(), jobCountMap.get(iscoGroupLink.getUri()) + count);
					}
				}
			}
		}
		List<Entry<String, Integer>> entrySet = new ArrayList<>(jobCountMap.entrySet());
		entrySet.sort(Entry.comparingByValue());
		Collections.reverse(entrySet);
		List<ResourceLink> result = new ArrayList<>();
		for(int i=0; (i < entrySet.size() && i < 20); i++) {
			Entry<String, Integer> entry = entrySet.get(i);
			ResourceLink link = iscoGroupMap.get(entry.getKey());
			link.setMatching(entry.getValue());
			result.add(link);
		}
		return result;		
	}
	
	private ResourceLink getIscoGroup(String occupationUri, 
			Map<String, ResourceLink> iscoGroupMap, Map<String, Occupation> occupationMap) {
		if(iscoGroupMap.containsKey(occupationUri)) {
			return iscoGroupMap.get(occupationUri);
		}
		Occupation occupation = occupationMap.get(occupationUri);
		if(occupation != null) {
			if(occupation.getConceptType().equals(Const.CONCEPT_ISCO_GROUP)) {
				if(occupation.getIscoCode().length() < 4) {
					return null;
				}
				ResourceLink occupationLink = new ResourceLink();
				occupationLink.setConceptType(occupation.getConceptType());
				occupationLink.setUri(occupation.getUri());
				occupationLink.setPreferredLabel(occupation.getPreferredLabel());
				iscoGroupMap.put(occupation.getUri(), occupationLink);
				return occupationLink;
			} else {
				String iscoCode = occupation.getIscoCode();
				String uri = "http://data.europa.eu/esco/isco/C" + iscoCode;
				Occupation iscoGroupOcc = occupationMap.get(uri);
				if(iscoGroupOcc != null) {
					if(iscoGroupMap.containsKey(iscoGroupOcc.getUri())) {
						return iscoGroupMap.get(iscoGroupOcc.getUri());
					} else {
						ResourceLink occupationLink = new ResourceLink();
						occupationLink.setConceptType(iscoGroupOcc.getConceptType());
						occupationLink.setUri(iscoGroupOcc.getUri());
						occupationLink.setPreferredLabel(iscoGroupOcc.getPreferredLabel());
						iscoGroupMap.put(iscoGroupOcc.getUri(), occupationLink);
						return occupationLink;					
					}
				}
			}
		}
		return null;
	}
	
	private int checkOccupation(String occupation, List<Profile> profiles, 
			Map<String, ResourceLink> iscoGroupMap, Map<String, Occupation> occupationMap) {
		int result = 0;
		for(Profile profile : profiles) {
			boolean found = false;
			for(String occupationUri : profile.getOccupations()) {
				ResourceLink iscoGroupLink = getIscoGroup(occupationUri, iscoGroupMap, occupationMap);
				if(iscoGroupLink.getUri().equals(occupation)) {
					found = true;
					break;
				}
			}
			if(!found) {
				result++;
			}
		}
		return result;
	}
}
