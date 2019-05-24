package it.smartcommunitylab.bridge.extsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import it.smartcommunitylab.bridge.common.Const;
import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.lucene.LuceneManager;
import it.smartcommunitylab.bridge.model.Course;
import it.smartcommunitylab.bridge.model.IscoIstat;
import it.smartcommunitylab.bridge.model.JobOffer;
import it.smartcommunitylab.bridge.model.Occupation;
import it.smartcommunitylab.bridge.model.ResourceLink;
import it.smartcommunitylab.bridge.model.TextDoc;
import it.smartcommunitylab.bridge.repository.CourseRepository;
import it.smartcommunitylab.bridge.repository.IscoIstatRepository;
import it.smartcommunitylab.bridge.repository.JobOfferRepository;
import it.smartcommunitylab.bridge.repository.OccupationRepository;

@Component
public class AgenziaLavoroWrapper {
	private static final transient Logger logger = LoggerFactory.getLogger(AgenziaLavoroWrapper.class);
			
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	JobOfferRepository jobOfferRepository;
	@Autowired
	IscoIstatRepository iscoIstatRepository;
	@Autowired
	OccupationRepository occupationRepository;
	@Autowired
	LuceneManager luceneManager;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	public void getCourses() throws Exception {
		URL url = new URL("https://formazionexte.agenzialavoro.tn.it/facetsearch/datatable_search/corso_adl/titolo/tipologia/125?draw=8&query=*&length=200");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		Reader streamReader = null;
		int status = con.getResponseCode();
		if (status > 299) {
	    streamReader = new InputStreamReader(con.getErrorStream());
		} else {
	    streamReader = new InputStreamReader(con.getInputStream());
		}
		BufferedReader in = new BufferedReader(streamReader);
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		if(status > 299) {
			logger.info("getCourses error:{}/{}", status, content.toString());
			return;
		}
		JsonNode jsonNode = Utils.readJsonFromString(content.toString());
		if(jsonNode.hasNonNull("data") && jsonNode.get("data").isArray()) {
			for(JsonNode node : jsonNode.get("data")) {
				if(node.isArray()) {
					ArrayNode arrayNode = (ArrayNode)node;
					if(arrayNode.size() >=  1) {
						try {
							String html = arrayNode.get(0).asText();
							Document document = Jsoup.parse(html);
							Element element = document.selectFirst("a");
							if(element != null) {
								String href = element.attr("href");
								Course course = getCourse(href);
								Course courseDb = courseRepository.findByExtUri(href);
								if(courseDb == null) {
									//TODO get skill
									courseRepository.save(course);
								} else {
									//TODO update course info
								}								
							}
						} catch (Exception e) {
							logger.info("getCourse error:{}", e.getMessage());
						}
					}
				}
			}
		}
	}
	
	public Course getCourse(String href) throws Exception {
		URL url = new URL(href);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		int status = con.getResponseCode();
		if (status > 299) {
			throw new RuntimeException(status + " - " + href);
		}
		Reader streamReader = new InputStreamReader(con.getInputStream());
		BufferedReader in = new BufferedReader(streamReader);
		String inputLine;
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		con.disconnect();
		String title;
		String content;
		String from;
		String to;
		String hours;
		String duration;
		String address;
		Document document = Jsoup.parse(sb.toString());
		Element titleElement = document.selectFirst("div.u-content-title h1");
		title = titleElement.text();
		Elements elements = document.select("div.Prose");
		int count = 1;
		for (Element element : elements) {
			if(count == 1) {
				
			}
			count++;
		}
		return null;
	}
	
	public JobOffer getJobOffer(String href) throws Exception {
		URL url = new URL(href);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		int status = con.getResponseCode();
		if (status > 299) {
			throw new RuntimeException(status + " - " + href);
		}
		Reader streamReader = new InputStreamReader(con.getInputStream());
		BufferedReader in = new BufferedReader(streamReader);
		String inputLine;
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		con.disconnect();
		
		String offerRef = null;
		String title = null;
		String description = null;
		String iscoCode = null;
		String istatCode = null;
		String istatPosition = null;
		String expirationDate = null;
		String sector = null;
		String professionalGroup = null;
		String workContract = null;
		String workPlace = null;
		Document document = Jsoup.parse(sb.toString());
		title = document.selectFirst("h3.white-heading").text().strip();
		offerRef = document.selectFirst("div.right-heading.pull-right").text().strip();
		description = document.selectFirst("div.job-detail p.job-description").text().strip();
		Elements jobInfoElements = document.select("div.job-detail div.col-md-8.left-sec div.job-info div.row");
		for(Element element : jobInfoElements) {
			String name = element.selectFirst("div.col-md-4").text().strip();
			String value = element.selectFirst("div.col-md-8").text().strip();
			switch (name) {
			case "Settore":
				sector = value;
				break;
			case "Qualifica ISTAT":
				istatPosition = value;
				IscoIstat iscoIstat = iscoIstatRepository.findByCompleteIstatName(istatPosition.toLowerCase());
				if(iscoIstat != null) {
					istatCode = iscoIstat.getIstatCode();
					iscoCode = iscoIstat.getIscoCode();
				}
				break;
			case "Contratto":
				workContract = value;
				break;
			case "Luogo di lavoro":
				workPlace = value;
				break;
			default:
				break;
			}
		}
		Elements jobOverviewElements = document.select("div.job-overview-sec.m-b-50 div.overview-box ul.overview-items li");
		for(Element element : jobOverviewElements) {
			String name = element.selectFirst("span").text().strip();
			String value = element.selectFirst("p").text().strip();
			switch (name) {
			case "Scade il":
				expirationDate = value.replace("( ", "").replace(")", "");
				break;
			case "Gruppo professionale":
				professionalGroup = value;
				break;
			default:
				break;
			}
		}
		String javascript = document.selectFirst("script.loadScript").toString();
		javascript = javascript.replace("\t", " ");
		int initLat = javascript.indexOf("centerPositionLat = ");
		int endLat = javascript.indexOf(" ?", initLat);
		int initLon = javascript.indexOf("centerPositionLng = ");
		int endLon = javascript.indexOf(" ?", initLon);
		double latitude = Double.valueOf(javascript.substring(initLat + 20, endLat));
		double longitude = Double.valueOf(javascript.substring(initLon + 20, endLon));
		
		List<String> occupations = new ArrayList<>();
		List<ResourceLink> occupationsLink = new ArrayList<>();
		List<TextDoc> iscoGroupTextList = luceneManager.searchByLabelAndType(title, Const.CONCEPT_ISCO_GROUP, 10, "text");
		List<TextDoc> occupationTextList = luceneManager.searchByLabelAndType(title, Const.CONCEPT_OCCCUPATION, 20, "text");
		List<Occupation> occupationList = occupationRepository.findByIscoCode("^" + iscoCode);
		for (Occupation occupation : occupationList) {
			if(containsUri(iscoGroupTextList, occupation.getUri()) || 
					containsUri(occupationTextList, occupation.getUri())) {
				occupations.add(occupation.getUri());
				ResourceLink link = new ResourceLink();
				link.setUri(occupation.getUri());
				link.setConceptType(occupation.getConceptType());
				link.setPreferredLabel(occupation.getPreferredLabel());
				occupationsLink.add(link);
			}
		}
		
		JobOffer jobOffer = new JobOffer();
		jobOffer.setExtUri(href);
		jobOffer.setTitle(title);
		jobOffer.setOfferRef(offerRef);
		jobOffer.setDescription(description);
		jobOffer.setIscoCode(iscoCode);
		jobOffer.setIstatCode(istatCode);
		jobOffer.setIstatPosition(istatPosition);
		jobOffer.setSector(sector);
		jobOffer.setWorkContract(workContract);
		jobOffer.setWorkPlace(workPlace);
		jobOffer.setProfessionalGroup(professionalGroup);
		if(!StringUtils.isEmpty(expirationDate)) {
			Date expDate = sdf.parse(expirationDate);
			Date now = new Date();
			if(now.after(expDate)) {
				logger.info("getJobOffer expired:{}", href);
				return null;
			}
			jobOffer.setExpirationDate(expDate);
		}
		jobOffer.setGeocoding(new double[] {longitude, latitude});
		jobOffer.setOccupations(occupations);
		jobOffer.setOccupationsLink(occupationsLink);
		return jobOffer;
	}
	
	public int getJobOffers() {
		String href = "https://www.sil.provincia.tn.it/welcomepage/vacancy/view/";
		int stored = 0;
		int endCount = 25350;
		for(int count = 25000; count < endCount; count++) {
			try {
				JobOffer jobOffer = getJobOffer(href + count);
				if(jobOffer != null) {
					JobOffer jobOfferDb = jobOfferRepository.findByExtUri(jobOffer.getExtUri());
					if(jobOfferDb != null) {
						jobOffer.setId(jobOfferDb.getId());
					}
					jobOfferRepository.save(jobOffer);
					logger.info("getJobOffers:{}", href + count);
					stored++;
				}
			} catch (Exception e) {
				logger.info("getJobOffers error:{}", e.getMessage());
			}
		}
		return stored;
	}
	
	private boolean containsUri(List<TextDoc> docList, String uri) {
		for (TextDoc textDoc : docList) {
			if(textDoc.getFields().get("uri").equals(uri)) {
				return true;
			}
		}
		return false;
	}

}
