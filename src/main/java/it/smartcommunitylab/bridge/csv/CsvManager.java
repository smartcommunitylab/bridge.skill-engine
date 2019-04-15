package it.smartcommunitylab.bridge.csv;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.Skill;
import it.smartcommunitylab.bridge.repository.OccupationRepository;
import it.smartcommunitylab.bridge.repository.SkillRepository;

@Component
public class CsvManager {
	private static final transient Logger logger = LoggerFactory.getLogger(CsvManager.class);
	
	@Autowired
	private LuceneManager luceneManager;
	@Autowired
	private SkillRepository skillRepository;
	@Autowired
	private OccupationRepository occupationRepository;
	
	public void importSkills(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType",
				"conceptUri","skillType","reuseLevel","preferredLabel","altLabels",
				"description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			String uri = record.get("conceptUri");
			String conceptType = record.get("conceptType");
			String preferredLabel = record.get("preferredLabel");
			String altLabels = record.get("altLabels");
			String description = record.get("description");
			Skill skill = new Skill();
			skill.setUri(uri);
			skill.setConceptType(conceptType);
			skill.setPreferredLabel(preferredLabel);
			skill.setAltLabels(altLabels);
			skill.setDescription(description);
			skillRepository.save(skill);
			logger.info("importSkills:{}", uri);
		}
		csvParser.close();
		reader.close();
	}
	
	public void importSkillGroups(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType","conceptUri",
				"preferredLabel","altLabels","description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			String uri = record.get("conceptUri");
			String conceptType = record.get("conceptType");
			String preferredLabel = record.get("preferredLabel");
			String altLabels = record.get("altLabels");
			String description = record.get("description");
			Skill skill = new Skill();
			skill.setUri(uri);
			skill.setConceptType(conceptType);
			skill.setPreferredLabel(preferredLabel);
			skill.setAltLabels(altLabels);
			skill.setDescription(description);
			skillRepository.save(skill);
			logger.info("importSkillGroups:{}", uri);
		}
		csvParser.close();
		reader.close();
	}
	
	public void importSkillRelations(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType","conceptUri", 
				"broaderType", "broaderUri").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);	
		for (CSVRecord record : csvParser) {
			String skillUri = record.get("conceptUri");
			String broaderSkillUri = record.get("broaderUri");
			Optional<Skill> optionalSkill = skillRepository.findById(skillUri);
			Optional<Skill> optionalBroaderSkill = skillRepository.findById(broaderSkillUri);
			if(optionalSkill.isEmpty() || optionalBroaderSkill.isEmpty())  {
				logger.info("skip skill relation:{} / {}", skillUri, broaderSkillUri);
				continue;
			}
			Skill skill = optionalSkill.get();
			Skill broaderSkill = optionalBroaderSkill.get();
			if(!skill.getBroaderSkill().contains(broaderSkillUri)) {
				skill.getBroaderSkill().add(broaderSkillUri);
				skillRepository.save(skill);
			}
			if(!broaderSkill.getNarrowerSkill().contains(skillUri)) {
				broaderSkill.getNarrowerSkill().add(skillUri);
				skillRepository.save(broaderSkill);
			}
			logger.info("add skill relation:{} / {}", skillUri, broaderSkillUri);
		}
		csvParser.close();
		reader.close();	
	}
	
	public void importOccupations(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType",
				"conceptUri","iscoGroup","preferredLabel","altLabels",
				"description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			String uri = record.get("conceptUri");
			String conceptType = record.get("conceptType");
			String preferredLabel = record.get("preferredLabel");
			String altLabels = record.get("altLabels");
			String description = record.get("description");
			String iscoGroup = record.get("iscoGroup");
			Occupation occupation = new Occupation();
			occupation.setUri(uri);
			occupation.setConceptType(conceptType);
			occupation.setPreferredLabel(preferredLabel);
			occupation.setAltLabels(altLabels);
			occupation.setDescription(description);
			occupation.setIscoCode(iscoGroup);
			occupationRepository.save(occupation);
			logger.info("importOccupations:{}", uri);
		}
		csvParser.close();
		reader.close();
	}
	
	public void importOccupationIscoGroup(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType",
				"conceptUri","code","preferredLabel","altLabels",
				"description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			String uri = record.get("conceptUri");
			String conceptType = record.get("conceptType");
			String preferredLabel = record.get("preferredLabel");
			String altLabels = record.get("altLabels");
			String description = record.get("description");
			String iscoGroup = record.get("code");
			Occupation occupation = new Occupation();
			occupation.setUri(uri);
			occupation.setConceptType(conceptType);
			occupation.setPreferredLabel(preferredLabel);
			occupation.setAltLabels(altLabels);
			occupation.setDescription(description);
			occupation.setIscoCode(iscoGroup);
			occupationRepository.save(occupation);
			logger.info("importOccupationIscoGroup:{}", uri);
		}
		csvParser.close();
		reader.close();
	}
	
	public void importOccupationRelations(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType","conceptUri", 
				"broaderType", "broaderUri").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);	
		for (CSVRecord record : csvParser) {
			String occUri = record.get("conceptUri");
			String broaderOccUri = record.get("broaderUri");
			Optional<Occupation> optionalOcc = occupationRepository.findById(occUri);
			Optional<Occupation> optionalBroaderOcc = occupationRepository.findById(broaderOccUri);
			if(optionalOcc.isEmpty() || optionalBroaderOcc.isEmpty())  {
				logger.info("skip occupation relation:{} / {}", occUri, broaderOccUri);
				continue;
			}
			Occupation occupation = optionalOcc.get();
			Occupation broaderOccupation = optionalBroaderOcc.get();
			if(!occupation.getBroaderOccupation().contains(broaderOccUri)) {
				occupation.getBroaderOccupation().add(broaderOccUri);
				occupationRepository.save(occupation);
			}
			if(!broaderOccupation.getNarrowerOccupation().contains(occUri)) {
				broaderOccupation.getNarrowerOccupation().add(occUri);
				occupationRepository.save(broaderOccupation);
			}
			logger.info("add occupation relation:{} / {}", occUri, broaderOccUri);
		}
		csvParser.close();
		reader.close();	
	}
	
	public void importOccupationSkillRelations(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("occupationUri","relationType", 
				"skillType", "skillUri").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);	
		for (CSVRecord record : csvParser) {
			String occupationUri = record.get("occupationUri");
			Optional<Occupation> optional = occupationRepository.findById(occupationUri);
			if(optional.isEmpty()) {
				logger.info("occupation not found:{}", occupationUri);
			} else {
				Occupation occupation = optional.get();
				String skillUri = record.get("skillUri");
				String relationType = record.get("relationType");
				if("essential".equals(relationType)) {
					if(!occupation.getHasEssentialSkill().contains(skillUri)) {
						occupation.getHasEssentialSkill().add(skillUri);
						occupation.getTotalSkill().add(skillUri);
						occupationRepository.save(occupation);
					}
					Optional<Skill> optionalSkill = skillRepository.findById(skillUri);
					if(optionalSkill.isPresent()) {
						Skill skill = optionalSkill.get();
						if(!skill.getIsEssentialForOccupation().contains(occupationUri)) {
							skill.getIsEssentialForOccupation().add(occupationUri);
							skillRepository.save(skill);
						}
					}
				} else {
					if(!occupation.getHasOptionalSkill().contains(skillUri)) {
						occupation.getHasOptionalSkill().add(skillUri);
						occupation.getTotalSkill().add(skillUri);
						occupationRepository.save(occupation);
					}
					Optional<Skill> optionalSkill = skillRepository.findById(skillUri);
					if(optionalSkill.isPresent()) {
						Skill skill = optionalSkill.get();
						if(!skill.getIsOptionalForOccupation().contains(occupationUri)) {
							skill.getIsOptionalForOccupation().add(occupationUri);
							skillRepository.save(skill);
						}
					}					
				}
				occupationRepository.save(occupation);
				logger.info("update occupation:{}", occupationUri);
			}
		}
		csvParser.close();
		reader.close();	
	}
	
	
	public void indexSkills(String csvFilePath) throws IOException {
		List<Document> docs = new ArrayList<Document>();
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType",
				"conceptUri","skillType","reuseLevel","preferredLabel","altLabels",
				"description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			Document doc = new Document();
			doc.add(new Field("uri", record.get("conceptUri"), TextField.TYPE_STORED));
			doc.add(new Field("conceptType", record.get("conceptType"), TextField.TYPE_STORED));
			doc.add(new Field("preferredLabel", record.get("preferredLabel"), TextField.TYPE_STORED));
			doc.add(new Field("altLabels", record.get("altLabels"), TextField.TYPE_STORED));
			docs.add(doc);
		}
		if(docs.size() > 0) {
			luceneManager.indexDocuments(docs);
		}
		csvParser.close();
		reader.close();
		logger.info("indexSkills:{}", docs.size());
	}
	
	public void indexSkillGroups(String csvFilePath) throws IOException {
		List<Document> docs = new ArrayList<Document>();
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType","conceptUri",
				"preferredLabel","altLabels","description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			Document doc = new Document();
			doc.add(new Field("uri", record.get("conceptUri"), TextField.TYPE_STORED));
			doc.add(new Field("conceptType", record.get("conceptType"), TextField.TYPE_STORED));
			doc.add(new Field("preferredLabel", record.get("preferredLabel"), TextField.TYPE_STORED));
			doc.add(new Field("altLabels", record.get("altLabels"), TextField.TYPE_STORED));
			docs.add(doc);
		}
		if(docs.size() > 0) {
			luceneManager.indexDocuments(docs);
		}
		csvParser.close();
		reader.close();
		logger.info("indexSkillGroups:{}", docs.size());
	}
	
	public void indexOccupations(String csvFilePath) throws IOException {
		List<Document> docs = new ArrayList<Document>();
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType",
				"conceptUri","iscoGroup","preferredLabel","altLabels",
				"description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			Document doc = new Document();
			doc.add(new Field("uri", record.get("conceptUri"), TextField.TYPE_STORED));
			doc.add(new Field("conceptType", record.get("conceptType"), TextField.TYPE_STORED));
			doc.add(new Field("preferredLabel", record.get("preferredLabel"), TextField.TYPE_STORED));
			doc.add(new Field("altLabels", record.get("altLabels"), TextField.TYPE_STORED));
			doc.add(new Field("iscoGroup", record.get("iscoGroup"), TextField.TYPE_STORED));
			docs.add(doc);
		}
		if(docs.size() > 0) {
			luceneManager.indexDocuments(docs);
		}
		csvParser.close();
		reader.close();
		logger.info("indexOccupations:{}", docs.size());
	}

	public void indexOccupationIscoGroups(String csvFilePath) throws IOException {
		List<Document> docs = new ArrayList<Document>();
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("conceptType",
				"conceptUri","code","preferredLabel","altLabels",
				"description").withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			Document doc = new Document();
			doc.add(new Field("uri", record.get("conceptUri"), TextField.TYPE_STORED));
			doc.add(new Field("conceptType", record.get("conceptType"), TextField.TYPE_STORED));
			doc.add(new Field("preferredLabel", record.get("preferredLabel"), TextField.TYPE_STORED));
			doc.add(new Field("altLabels", record.get("altLabels"), TextField.TYPE_STORED));
			doc.add(new Field("iscoGroup", record.get("code"), TextField.TYPE_STORED));
			docs.add(doc);
		}
		if(docs.size() > 0) {
			luceneManager.indexDocuments(docs);
		}
		csvParser.close();
		reader.close();
		logger.info("indexOccupationIscoGroups:{}", docs.size());
	}

}
