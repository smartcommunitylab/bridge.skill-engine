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
import it.smartcommunitylab.bridge.model.IscoIstat;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.Skill;
import it.smartcommunitylab.bridge.repository.IscoIstatRepository;
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
	@Autowired
	private IscoIstatRepository iscoIstatRepository;
	
	public void importSkills(String csvFilePath, String lang) throws IOException {
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
			Skill skill = null;
			Optional<Skill> optionalSkill = skillRepository.findById(uri);
			if(optionalSkill.isEmpty()) {
				skill = new Skill();
				skill.setUri(uri);
				skill.setConceptType(conceptType);
			} else {
				skill = optionalSkill.get();
			}
			skill.getPreferredLabel().put(lang, preferredLabel);
			skill.getAltLabels().put(lang, altLabels);
			skill.getDescription().put(lang, description);
			skillRepository.save(skill);
			logger.info("importSkills:{}/{}", lang, uri);
		}
		csvParser.close();
		reader.close();
	}
	
	public void importSkillGroups(String csvFilePath, String lang) throws IOException {
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
			Skill skill = null;
			Optional<Skill> optionalSkill = skillRepository.findById(uri);
			if(optionalSkill.isEmpty()) {
				skill = new Skill();
				skill.setUri(uri);
				skill.setConceptType(conceptType);
			} else {
				skill = optionalSkill.get();
			}
			skill.getPreferredLabel().put(lang, preferredLabel);
			skill.getAltLabels().put(lang, altLabels);
			skill.getDescription().put(lang, description);
			skillRepository.save(skill);
			logger.info("importSkillGroups:{}/{}", lang, uri);
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
				ResourceLink rLink = new ResourceLink();
				rLink.setPreferredLabel(broaderSkill.getPreferredLabel());
				rLink.setUri(broaderSkill.getUri());
				rLink.setConceptType(broaderSkill.getConceptType());
				skill.getBroaderSkillLink().add(rLink);
				skillRepository.save(skill);
			}
			if(!broaderSkill.getNarrowerSkill().contains(skillUri)) {
				broaderSkill.getNarrowerSkill().add(skillUri);
				ResourceLink rLink = new ResourceLink();
				rLink.setPreferredLabel(skill.getPreferredLabel());
				rLink.setUri(skill.getUri());
				rLink.setConceptType(skill.getConceptType());
				broaderSkill.getNarrowerSkillLink().add(rLink);
				skillRepository.save(broaderSkill);
			}
			logger.info("importSkillRelations:{}/{}", skillUri, broaderSkillUri);
		}
		csvParser.close();
		reader.close();	
	}
	
	public void importOccupations(String csvFilePath, String lang) throws IOException {
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
			Occupation occupation = null;
			Optional<Occupation> optionalOccupation = occupationRepository.findById(uri);
			if(optionalOccupation.isEmpty()) {
				occupation = new Occupation();
				occupation.setUri(uri);
				occupation.setConceptType(conceptType);
				occupation.setIscoCode(iscoGroup);
			} else {
				occupation = optionalOccupation.get();
			}
			occupation.getPreferredLabel().put(lang, preferredLabel);
			occupation.getAltLabels().put(lang, altLabels);
			occupation.getDescription().put(lang, description);
			occupationRepository.save(occupation);
			logger.info("importOccupations:{}/{}", lang, uri);
		}
		csvParser.close();
		reader.close();
	}
	
	public void importOccupationIscoGroup(String csvFilePath, String lang) throws IOException {
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
			Occupation occupation = null;
			Optional<Occupation> optionalOccupation = occupationRepository.findById(uri);
			if(optionalOccupation.isEmpty()) {
				occupation = new Occupation();
				occupation.setUri(uri);
				occupation.setConceptType(conceptType);
				occupation.setIscoCode(iscoGroup);
			} else {
				occupation = optionalOccupation.get();
			}
			occupation.getPreferredLabel().put(lang, preferredLabel);
			occupation.getAltLabels().put(lang, altLabels);
			occupation.getDescription().put(lang, description);
			occupationRepository.save(occupation);
			logger.info("importOccupationIscoGroup:{}/{}", lang, uri);
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
				ResourceLink rLink = new ResourceLink();
				rLink.setPreferredLabel(broaderOccupation.getPreferredLabel());
				rLink.setUri(broaderOccupation.getUri());
				rLink.setConceptType(broaderOccupation.getConceptType());
				occupation.getBroaderOccupationLink().add(rLink);
				occupationRepository.save(occupation);
			}
			if(!broaderOccupation.getNarrowerOccupation().contains(occUri)) {
				broaderOccupation.getNarrowerOccupation().add(occUri);
				ResourceLink rLink = new ResourceLink();
				rLink.setPreferredLabel(occupation.getPreferredLabel());
				rLink.setUri(occupation.getUri());
				rLink.setConceptType(occupation.getConceptType());
				broaderOccupation.getNarrowerOccupationLink().add(rLink);
				occupationRepository.save(broaderOccupation);
			}
			logger.info("importOccupationRelations:{}/{}", occUri, broaderOccUri);
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
				Optional<Skill> optionalSkill = skillRepository.findById(skillUri);
				if(optionalSkill.isPresent()) {
					Skill skill = optionalSkill.get();
					if("essential".equals(relationType)) {
						if(!occupation.getHasEssentialSkill().contains(skillUri)) {
							occupation.getHasEssentialSkill().add(skillUri);
							occupation.getTotalSkill().add(skillUri);
							ResourceLink rLink = new ResourceLink();
							rLink.setPreferredLabel(skill.getPreferredLabel());
							rLink.setUri(skill.getUri());
							rLink.setConceptType(skill.getConceptType());
							occupation.getHasEssentialSkillLink().add(rLink);
							occupationRepository.save(occupation);
						}
						if(!skill.getIsEssentialForOccupation().contains(occupationUri)) {
							skill.getIsEssentialForOccupation().add(occupationUri);
							ResourceLink rLink = new ResourceLink();
							rLink.setPreferredLabel(occupation.getPreferredLabel());
							rLink.setUri(occupation.getUri());				
							rLink.setConceptType(occupation.getConceptType());
							skill.getIsEssentialForOccupationLink().add(rLink);
							skillRepository.save(skill);
						}						
					} else {
						if(!occupation.getHasOptionalSkill().contains(skillUri)) {
							occupation.getHasOptionalSkill().add(skillUri);
							occupation.getTotalSkill().add(skillUri);
							ResourceLink rLink = new ResourceLink();
							rLink.setPreferredLabel(skill.getPreferredLabel());
							rLink.setUri(skill.getUri());
							rLink.setConceptType(skill.getConceptType());
							occupation.getHasOptionalSkillLink().add(rLink);
							occupationRepository.save(occupation);
						}						
						if(!skill.getIsOptionalForOccupation().contains(occupationUri)) {
							skill.getIsOptionalForOccupation().add(occupationUri);
							ResourceLink rLink = new ResourceLink();
							rLink.setPreferredLabel(occupation.getPreferredLabel());
							rLink.setUri(occupation.getUri());
							rLink.setConceptType(occupation.getConceptType());
							skill.getIsOptionalForOccupationLink().add(rLink);
							skillRepository.save(skill);
						}
					}
				}
				occupationRepository.save(occupation);
				logger.info("importOccupationSkillRelations:{}", occupationUri);
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
			doc.add(new Field("preferredLabelNormalized", luceneManager.normalizeText(record.get("preferredLabel")), 
					TextField.TYPE_STORED));
			doc.add(new Field("altLabelsNormalized", luceneManager.normalizeText(record.get("altLabels")), 
					TextField.TYPE_STORED));
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
			doc.add(new Field("preferredLabelNormalized", luceneManager.normalizeText(record.get("preferredLabel")), 
					TextField.TYPE_STORED));
			doc.add(new Field("altLabelsNormalized", luceneManager.normalizeText(record.get("altLabels")), 
					TextField.TYPE_STORED));
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
			doc.add(new Field("preferredLabelNormalized", luceneManager.normalizeText(record.get("preferredLabel")), 
					TextField.TYPE_STORED));
			doc.add(new Field("altLabelsNormalized", luceneManager.normalizeText(record.get("altLabels")), 
					TextField.TYPE_STORED));
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
			doc.add(new Field("preferredLabelNormalized", luceneManager.normalizeText(record.get("preferredLabel")), 
					TextField.TYPE_STORED));
			doc.add(new Field("altLabelsNormalized", luceneManager.normalizeText(record.get("altLabels")), 
					TextField.TYPE_STORED));
			docs.add(doc);
		}
		if(docs.size() > 0) {
			luceneManager.indexDocuments(docs);
		}
		csvParser.close();
		reader.close();
		logger.info("indexOccupationIscoGroups:{}", docs.size());
	}
	
	public void importIscoIstatMap(String csvFilePath) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
		CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';')
				.withHeader("codiceISCO08","nomeISCO08","codiceCP2011","nomeCP2011")
				.withSkipHeaderRecord();
		CSVParser csvParser = new CSVParser(reader, csvFormat);
		for (CSVRecord record : csvParser) {
			String istatCode = record.get("codiceCP2011");
			String istatName = record.get("nomeCP2011").toLowerCase();
			String iscoCode = record.get("codiceISCO08");
			String iscoName = record.get("nomeISCO08").toLowerCase();
			IscoIstat iscoIstat = iscoIstatRepository.findByCompleteIstatCode(istatCode);
			if(iscoIstat != null) {
				continue;
			}
			iscoIstat = new IscoIstat();
			iscoIstat.setIscoCode(iscoCode);
			iscoIstat.setIscoName(iscoName);
			iscoIstat.setIstatCode(istatCode);
			iscoIstat.setIstatName(istatName);
			iscoIstatRepository.save(iscoIstat);
			logger.info("importIscoIstatMap:{}/{}", iscoCode, istatCode);
		}
		csvParser.close();
		reader.close();
	}

}
