package it.smartcommunitylab.bridge.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.csv.CsvManager;
import it.smartcommunitylab.bridge.extsource.AgenziaLavoroWrapper;
import it.smartcommunitylab.bridge.extsource.CogitoAnalyzer;
import it.smartcommunitylab.bridge.model.Profile;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.cogito.CogitoProfile;
import it.smartcommunitylab.bridge.model.cogito.WorkExperience;

@RestController
public class AdminController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	CsvManager csvManager;
	@Autowired
	AgenziaLavoroWrapper agenziaLavoroWrapper;
	@Autowired
	CogitoAnalyzer cogitoAnalyzer;
	
	@GetMapping(value = "/admin/import/all")
	public void importAll(@RequestParam String path) throws Exception {
		csvManager.importSkills(path + "/skills_it.csv", "it");
		csvManager.importSkills(path + "/skills_en.csv", "en");
		csvManager.importSkills(path + "/skills_fr.csv", "fr");
		
		csvManager.importSkillGroups(path + "/skillGroups_it.csv", "it");
		csvManager.importSkillGroups(path + "/skillGroups_en.csv", "en");
		csvManager.importSkillGroups(path + "/skillGroups_fr.csv", "fr");

		csvManager.importSkillRelations(path + "/broaderRelationsSkillPillar.csv");

		csvManager.importOccupations(path + "/occupations_it.csv", "it");
		csvManager.importOccupations(path + "/occupations_en.csv", "en");
		csvManager.importOccupations(path + "/occupations_fr.csv", "fr");

		csvManager.importOccupationIscoGroup(path + "/ISCOGroups_it.csv", "it");
		csvManager.importOccupationIscoGroup(path + "/ISCOGroups_en.csv", "en");
		csvManager.importOccupationIscoGroup(path + "/ISCOGroups_fr.csv", "fr");

		csvManager.importOccupationRelations(path + "/broaderRelationsOccPillar.csv");

		csvManager.importOccupationSkillRelations(path + "/occupationSkillRelations.csv");
		logger.info("importAll:{}", path);
	}
	
	@GetMapping(value = "/admin/index/all")
	public void indexAll(@RequestParam String path) throws Exception {
		csvManager.indexSkills(path + "/skills_it.csv");
		csvManager.indexSkillGroups(path + "/skillGroups_it.csv");
		csvManager.indexOccupations(path + "/occupations_it.csv");
		csvManager.indexOccupationIscoGroups(path + "/ISCOGroups_it.csv");
		logger.info("indexAll:{}", path);
	}
	
	@GetMapping(value = "/admin/import/iscoistat")
	public void importIscoIstat(@RequestParam String path) throws Exception {
		csvManager.importIscoIstatMap(path);
		logger.info("importIscoIstat:{}", path);
	}
	
	@GetMapping(value = "/admin/import/job")
	public void importJobOffers(@RequestParam int startCount, 
			@RequestParam int endCount) throws Exception {
		int jobOffers = agenziaLavoroWrapper.getJobOffers(startCount, endCount);
		logger.info("importJobOffers:{}", jobOffers);
	}
	
	@GetMapping(value = "/admin/import/course")
	public void importCourses() throws Exception {
		int courses = agenziaLavoroWrapper.getCourses();
		logger.info("importCourses:{}", courses);
	}
	
	@GetMapping(value = "/admin/import/personaldata")
	public void importPesonalData(@RequestParam String path,
			@RequestParam(required=false) boolean addProfile) throws Exception {
		List<File> files = new ArrayList<>();
		File inputFolder = new File(path);
		Utils.traverse(inputFolder, files);
		for(File file : files) {
			String inputFile = file.getAbsolutePath();
			if(!inputFile.toLowerCase().endsWith(".odt")) {
				logger.debug("importPesonalData: skip {}", inputFile);
				continue;
			}
			CogitoProfile cogitoProfile = cogitoAnalyzer.analyzePersonalData(file);
			if(addProfile) {
				CogitoProfile cogitoProfileDb = cogitoProfileRepository.findByFilename(file.getName());
				if(cogitoProfileDb != null) {
					cogitoProfile.setId(cogitoProfile.getId());
				}
				cogitoProfileRepository.save(cogitoProfile);
				Profile profile = new Profile();
				profile.setExtId(file.getName());
				for(WorkExperience experience : cogitoProfile.getWorkExperiences()) {
					for(ResourceLink link : experience.getOccupationsLink()) {
						if(!profile.getOccupations().contains(link.getUri()))  {
							profile.getOccupations().add(link.getUri());
						}
					}
				}
				if(profile.getOccupations().size() > 0) {
					Profile profileDb = profileRepository.findByExtId(file.getName());
					if(profileDb != null) {
						profile.setId(profileDb.getId());
					}
					profile.setOccupationsLink(completeOccupationLink(profile.getOccupations()));
					profileRepository.save(profile);
				}
			}
			logger.info("importPesonalData:{} / {}", cogitoProfile, inputFile);
		}
	}
	
	@GetMapping(value = "/admin/import/personaldata/file")
	public void importSinglePesonalData(@RequestParam String path) throws Exception {
		File file = new File(path);
		CogitoProfile profile = cogitoAnalyzer.analyzePersonalData(file);
		logger.info("importSinglePesonalData:{} / {}", profile, path);
	}
	
}
