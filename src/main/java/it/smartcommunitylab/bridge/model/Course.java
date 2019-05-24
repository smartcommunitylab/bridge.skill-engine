package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Course {
	@Id
	private String id;
	private String extUri;
	private String title;
	private String content;
	private Date dateFrom;
	private Date dateTo;
	private String hours;
	private int duration;
	private String address;
	private double[] geocoding; // lon,lat
	private List<String> skills = new ArrayList<>();
	private List<ResourceLink> skillsLink = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExtUri() {
		return extUri;
	}
	public void setExtUri(String extUri) {
		this.extUri = extUri;
	}
	public List<String> getSkills() {
		return skills;
	}
	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	public List<ResourceLink> getSkillsLink() {
		return skillsLink;
	}
	public void setSkillsLink(List<ResourceLink> skillsLink) {
		this.skillsLink = skillsLink;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}

}
