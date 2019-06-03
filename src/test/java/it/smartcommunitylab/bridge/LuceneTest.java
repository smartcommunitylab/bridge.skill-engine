package it.smartcommunitylab.bridge;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class LuceneTest {
	@Test
	public void searchIndex() throws Exception {
		Path path = Paths.get("/home/dev/lucene/bridge/skill-engine");
		Directory directory = FSDirectory.open(path);
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		StandardQueryParser parser = new StandardQueryParser(new ItalianAnalyzer());
		Query query = parser.parse("KnowledgeSkillCompetence", "conceptType");
		ScoreDoc[] scoreDocs = isearcher.search(query, 10).scoreDocs;
		System.out.println("StandardQueryParser:" + scoreDocs.length);
		
		TermQuery termQuery = new TermQuery(new Term("conceptType", "KnowledgeSkillCompetence"));
		scoreDocs = isearcher.search(termQuery, 10).scoreDocs;
		System.out.println("TermQuery:" + scoreDocs.length);
		
		QueryParser parserIsco = new QueryParser("iscoGroup", new ItalianAnalyzer());
		Query iscoQuery = parserIsco.parse("12*");
		scoreDocs = isearcher.search(iscoQuery, 10).scoreDocs;
		System.out.println("Regexp QueryParser:" + scoreDocs.length);
		for(ScoreDoc scoreDoc : scoreDocs) {
			Document doc = isearcher.doc(scoreDoc.doc);
			System.out.println(doc.get("uri") + " - " + doc.get("iscoGroup"));
		}
		
		ireader.close();
    directory.close();
	}

}
