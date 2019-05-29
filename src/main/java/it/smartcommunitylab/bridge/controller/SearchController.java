package it.smartcommunitylab.bridge.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.TextDoc;

@RestController
public class SearchController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	LuceneManager luceneManager;

	@GetMapping(value = "/api/search/label")
	public List<TextDoc> searchByLabel(
			@RequestParam(required = false) String conceptType,
			@RequestParam String text) throws Exception {
		List<TextDoc> result = null;
		text = StringUtils.strip(text);
		if(StringUtils.isEmpty(conceptType)) {
			result = luceneManager.searchByLabel(text, 20, "text");
		} else {
			result = luceneManager.searchByLabelAndType(text, conceptType, 20, "text");
		}
		logger.debug("searchByLabel:{} / {}", result.size(), text);
		return result;
	}
	
}
