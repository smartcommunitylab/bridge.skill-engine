package it.smartcommunitylab.bridge.controller;

import java.io.File;
import java.io.FileInputStream;
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

@RestController
public class AdminController {
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
	public void importJobOffers(@RequestParam int endCount) throws Exception {
		int jobOffers = agenziaLavoroWrapper.getJobOffers(endCount);
		logger.info("importJobOffers:{}", jobOffers);
	}
	
	@GetMapping(value = "/admin/import/course")
	public void importCourses() throws Exception {
		int courses = agenziaLavoroWrapper.getCourses();
		logger.info("importCourses:{}", courses);
	}
	
	@GetMapping(value = "/admin/import/personaldata")
	public void importPesonalData(@RequestParam String path) throws Exception {
		List<File> files = new ArrayList<>();
		File inputFolder = new File(path);
		Utils.traverse(inputFolder, files);
		for(File file : files) {
			String inputFile = file.getAbsolutePath();
			if(!inputFile.toLowerCase().endsWith(".odt")) {
				logger.info("importPesonalData: skip {}", inputFile);
				continue;
			}
			String json = cogitoAnalyzer.analyzePersonalData(new FileInputStream(inputFile));
			logger.info("importPesonalData:{} / {}", json, inputFile);
		}
	}
	
}
