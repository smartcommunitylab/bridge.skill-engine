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
import java.util.Optional;

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
import it.smartcommunitylab.bridge.model.Skill;
import it.smartcommunitylab.bridge.model.TextDoc;
import it.smartcommunitylab.bridge.repository.CourseRepository;
import it.smartcommunitylab.bridge.repository.IscoIstatRepository;
import it.smartcommunitylab.bridge.repository.JobOfferRepository;
import it.smartcommunitylab.bridge.repository.OccupationRepository;
import it.smartcommunitylab.bridge.repository.SkillRepository;

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
	SkillRepository skillRepository;
	@Autowired
	LuceneManager luceneManager;
	@Autowired
	CogitoAnalyzer cogitoAnalyzer;
	
	SimpleDateFormat sdfJobOffer = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat sdfCourse = new SimpleDateFormat("dd/MM/yyyy");

	public int getCourses() throws Exception {
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
			return 0;
		}
		int stored = 0;
		JsonNode jsonNode = Utils.readJsonFromString(content.toString());
		if(jsonNode.hasNonNull("data") && jsonNode.get("data").isArray()) {
			for(JsonNode node : jsonNode.get("data")) {
				if(node.isArray()) {
					ArrayNode arrayNode = (ArrayNode)node;
					if(arrayNode.size() >=  1) {
						String html = arrayNode.get(0).asText();
						Document document = Jsoup.parse(html);
						Element element = document.selectFirst("a");
						if(element != null) {
							String href = element.attr("href");
							try {
								Course course = getCourse(href);
								Course courseDb = courseRepository.findByExtUri(href);
								if(courseDb != null) {
									course.setId(courseDb.getId());
								}
								courseRepository.save(course);
								logger.info("getCourses:{}", href);
								stored++;
							} catch (Exception e) {
								logger.info("getCourse error:{}/{}", href, e.getMessage());
							}
						}
					}
				}
			}
		}
		return stored;
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
		String title = null;
		StringBuffer content = new StringBuffer();
		String from = null;
		String to = null;
		String hours = null;
		String duration = null;
		String address = null;
		Document document = Jsoup.parse(sb.toString());
		title = document.selectFirst("div.u-content-title h1").text();
		Elements elements = document.select("div.Prose p");
		for (Element element : elements) {
			String field = element.selectFirst("b") != null ? element.selectFirst("b").text() : "";
			String value = element.ownText();
			switch (field) {
			case "Durata in ore:":
				duration = value;
				break;
			case "Orario:":
				hours = value;
				break;
			case "Data inizio:":
				from = value;
				break;
			case "Data fine:":
				to = value;
				break;
			default:
				break;
			}
		}
		Element locationElement = document.selectFirst("div.u-content-related-item");
		address = locationElement.ownText();
		Element anchorElement = locationElement.selectFirst("div.u-size1of2 a");
		String anchor = anchorElement.attr("href");
		int initLat = anchor.indexOf("//'");
		int endLat = anchor.indexOf(",", initLat);
		int initLon = anchor.indexOf(",", initLat);
		int endLon = anchor.indexOf("'", initLon);
		double latitude = Double.valueOf(anchor.substring(initLat + 3, endLat));
		double longitude = Double.valueOf(anchor.substring(initLon + 1, endLon));
		
		content.append(title + "\n");
		elements = document.select("div.Prose div.Grid div");
		boolean nextContent = false;
		for (Element element : elements) {
			if(nextContent) {
				nextContent = false;
				Elements paragraphs = element.select("p");
				for(Element paragraph : paragraphs) {
					content.append(paragraph.text() + "\n");
				}
				continue;
			}
			String field = element.selectFirst("b") != null ? element.selectFirst("b").text() : "";
			switch (field) {
				case "Descrizione della modalità di formazione":
					nextContent = true;
					break;
				case "Sintesi dei contenuti":
					nextContent = true;
					break;
			}
		}
		
		List<String> skills = new ArrayList<>();
		List<ResourceLink> skillsLink = new ArrayList<>(); 
		List<TextDoc> skillTextList = luceneManager.searchByFields(title, Const.CONCEPT_SKILL, null, 10);
		for (TextDoc textDoc : skillTextList) {
			if(textDoc.getScore() < 4.0) {
				continue;
			}
			Optional<Skill> optional = skillRepository.findById(textDoc.getFields().get("uri"));
			if(optional.isPresent()) {
				Skill skill = optional.get();
				skills.add(skill.getUri());
				ResourceLink link = new ResourceLink();
				link.setUri(skill.getUri());
				link.setConceptType(skill.getConceptType());
				link.setPreferredLabel(skill.getPreferredLabel());
				skillsLink.add(link);
			}
		}
		
		Course course = new Course();
		course.setExtUri(href);
		course.setTitle(title);
		course.setDuration(Integer.valueOf(duration));
		course.setHours(hours);
		course.setDateFrom(sdfCourse.parse(from));
		course.setDateTo(sdfCourse.parse(to));
		course.setAddress(address);
		course.setGeocoding(new double[] {longitude, latitude});
		course.setContent(content.toString());
		course.setSkills(skills);
		course.setSkillsLink(skillsLink);
		
		cogitoAnalyzer.analyzeCourse(course);
		return course;
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
		List<TextDoc> iscoGroupTextList = luceneManager.searchByFields(title, Const.CONCEPT_ISCO_GROUP, null, 10);
		List<TextDoc> occupationTextList = luceneManager.searchByFields(title, Const.CONCEPT_OCCCUPATION, null, 20);
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
			Date expDate = sdfJobOffer.parse(expirationDate);
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
	
	public int getJobOffers(int startCount, int endCount) {
		String href = "https://www.sil.provincia.tn.it/welcomepage/vacancy/view/";
		int stored = 0;
		for(int count = startCount; count < endCount; count++) {
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
				logger.info("getJobOffers error:{}/{}", href + count, e.getMessage());
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
