package it.smartcommunitylab.bridge.lucene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import eu.fbk.dh.tint.runner.TintPipeline;
import it.smartcommunitylab.bridge.model.TextDoc;

@Component
public class LuceneManager {
	private static final transient Logger logger = LoggerFactory.getLogger(LuceneManager.class);
	
	@Autowired
	@Value("${lucene.index.path}")
	private String indexPath;
	
	private TintPipeline pipeline;

	private Analyzer analyzer;
	private Directory directory;
	private IndexWriterConfig config;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	private IndexWriter iwriter;
	
	@PostConstruct
	public void init() throws IOException {
		analyzer = new ItalianAnalyzer();
		Path path = Paths.get(indexPath);
		if(!Files.exists(path)) {
			Files.createDirectory(path);
		}
    directory = FSDirectory.open(path);
    config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    iwriter = new IndexWriter(directory, config);
    iwriter.commit();
    ireader = DirectoryReader.open(directory);
    isearcher = new IndexSearcher(ireader);
    
    pipeline = new TintPipeline();
    pipeline.loadDefaultProperties();
    pipeline.setProperty("annotators", "ita_toksent, pos, ita_morpho, ita_lemma");
    pipeline.load();
	}
	
	@PreDestroy
	public void close() throws IOException {
		iwriter.close();
		ireader.close();
    directory.close();
	}
	
	public String normalizeText(String...strings) {
		StringBuffer sb = new StringBuffer();
		for (String text : strings) {
			Annotation stanfordAnnotation = pipeline.runRaw(text);
			for(CoreMap sentence : stanfordAnnotation.get(SentencesAnnotation.class)) {
				for(CoreLabel token : sentence.get(TokensAnnotation.class)) {
					if(token.lemma().equalsIgnoreCase("[PUNCT]")) {
						continue;
					}
					sb.append(token.lemma());
					sb.append(" ");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public void indexDocuments(List<Document> docs) throws IOException {
		for(Document doc :  docs) {
			iwriter.addDocument(doc);
		}
		iwriter.commit();
		logger.debug("indexDocuments:{}", docs.size());
	}
	
	public List<TextDoc> searchByLabel(String text, int maxResult, String... field) throws ParseException, IOException {
		QueryParser parser = new MultiFieldQueryParser(field, analyzer);
		Query query = parser.parse(QueryParser.escape(normalizeText(text)));
		ScoreDoc[] hits = isearcher.search(query, maxResult).scoreDocs;
		List<TextDoc> result = new ArrayList<TextDoc>();
		for(ScoreDoc scoreDoc : hits) {
			Document doc = isearcher.doc(scoreDoc.doc);
			TextDoc textDoc = new TextDoc();
			textDoc.setScore(scoreDoc.score);
			textDoc.getFields().put("preferredLabel", doc.get("preferredLabel"));
			textDoc.getFields().put("altLabels", doc.get("altLabels"));
			textDoc.getFields().put("conceptType", doc.get("conceptType"));
			textDoc.getFields().put("iscoGroup", doc.get("iscoGroup"));
			textDoc.getFields().put("uri", doc.get("uri"));
			result.add(textDoc);
		}
		logger.debug("searchPreferredLabel:{} / {}", result.size(), text);
		return result;
	}
	
	public List<TextDoc> searchByLabelAndType(String text, String concetType, 
			int maxResult, String... field) throws ParseException, IOException {
		QueryParser parser = new MultiFieldQueryParser(field, analyzer);
		Query fieldQuery = parser.parse(QueryParser.escape(normalizeText(text)));
		
		SimpleQueryParser simpleParser = new SimpleQueryParser(analyzer, "conceptType");
		Query typeQuery = simpleParser.parse(concetType);
		
		BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(fieldQuery, BooleanClause.Occur.MUST)
				.add(typeQuery, BooleanClause.Occur.MUST)
				.build();
		
		ScoreDoc[] hits = isearcher.search(booleanQuery, maxResult).scoreDocs;
		List<TextDoc> result = new ArrayList<TextDoc>();
		for(ScoreDoc scoreDoc : hits) {
			Document doc = isearcher.doc(scoreDoc.doc);
			TextDoc textDoc = new TextDoc();
			textDoc.setScore(scoreDoc.score);
			textDoc.getFields().put("preferredLabel", doc.get("preferredLabel"));
			textDoc.getFields().put("altLabels", doc.get("altLabels"));
			textDoc.getFields().put("conceptType", doc.get("conceptType"));
			textDoc.getFields().put("iscoGroup", doc.get("iscoGroup"));
			textDoc.getFields().put("uri", doc.get("uri"));
			result.add(textDoc);
		}
		logger.debug("searchByLabelAndType:{} / {}", result.size(), text);
		return result;
	}
	
}
