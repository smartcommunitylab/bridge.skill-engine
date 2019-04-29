package it.smartcommunitylab.bridge;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

public class TintTest {
	@Test
	public void testTint() throws Exception {
	// Initialize the Tint pipeline
		TintPipeline pipeline = new TintPipeline();

		// Load the default properties
		// see https://github.com/dhfbk/tint/blob/master/tint-runner/src/main/resources/default-config.properties
		pipeline.loadDefaultProperties();

		// Add a custom property
		pipeline.setProperty("annotators", "ita_toksent, pos, ita_morpho, ita_lemma");

		// Load the models
		pipeline.load();

		// Use for example a text in a String
		String text = "I topi non avevano nipoti.\nOggi c'è il sole";

		// Get the original Annotation (Stanford CoreNLP)
		Annotation stanfordAnnotation = pipeline.runRaw(text);

		// **or**

		// Get the JSON
		// (optionally getting the original Stanford CoreNLP Annotation as return value)
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		pipeline.run(stream, System.out, TintRunner.OutputFormat.JSON);
		
		List<CoreMap> list = stanfordAnnotation.get(SentencesAnnotation.class);
		for(CoreMap sentence : list) {
			List<CoreLabel> tokenList = sentence.get(TokensAnnotation.class);
			for(CoreLabel token : tokenList) {
				System.out.println(token.get(LemmaAnnotation.class));
				System.out.println(token.originalText());
			}
			System.out.println();
		}
	}
}
