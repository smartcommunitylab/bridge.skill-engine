package it.smartcommunitylab.bridge;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.smartcommunitylab.bridge.csv.CsvManager;
import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.TextDoc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SkillEngineApplicationTests {
	
	@Autowired
	CsvManager csvManager;
	
	@Autowired
	LuceneManager luceneManager;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void importSkills() throws IOException {
		csvManager.importSkills("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\skills_it.csv");
	}
	
	@Test
	public void importSkillGroups() throws IOException {
		csvManager.importSkillGroups("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\skillGroups_it.csv");
	}
	
	@Test
	public void importSkillRelations() throws IOException {
		csvManager.importSkillRelations("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\broaderRelationsSkillPillar.csv");
	}
	
	@Test
	public void importOccupations() throws IOException {
		csvManager.importOccupations("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\occupations_it.csv");
	}
	
	@Test
	public void importOccupationSkillRelations() throws IOException {
		csvManager.importOccupationSkillRelations("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\occupationSkillRelations.csv");
	}
	
	@Test
	public void indexSkills() throws IOException {
		csvManager.indexSkills("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\skills_it.csv");
	}
	
	@Test
	public void indexSkillGroups() throws IOException {
		csvManager.indexSkillGroups("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\skillGroups_it.csv");
	}
	
	@Test
	public void indexOccupations() throws IOException {
		csvManager.indexOccupations("C:\\Users\\micnori\\Documents\\Progetti\\Bridge\\esco\\v1.0.3\\occupations_it.csv");
	}
	
	@Test
	public void searchPreferredLabel() throws ParseException, IOException {
		List<TextDoc> list = luceneManager.searchByLabel("archiviare ordini", 20, "preferredLabel");
		for(TextDoc doc : list) {
			System.out.println(String.format("doc:%s - %s - %s", doc.getScore(), 
					doc.getFields().get("uri"), doc.getFields().get("preferredLabel")));
		}
	}
	
}
