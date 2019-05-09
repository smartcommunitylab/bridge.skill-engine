package it.smartcommunitylab.bridge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.csv.CsvManager;

@RestController
public class AdminController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	CsvManager csvManager;
	
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
}
