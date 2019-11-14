package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

public class ProfileResult {
	private String extId;
	private String occupationUri;
	private String iscoCode;
	private List<JobOffer> jobOffers = new ArrayList<>();
	private List<CourseResult> courses = new ArrayList<>();
	
	public String getExtId() {
		return extId;
	}
	public void setExtId(String extId) {
		this.extId = extId;
	}
	public List<JobOffer> getJobOffers() {
		return jobOffers;
	}
	public void setJobOffers(List<JobOffer> jobOffers) {
		this.jobOffers = jobOffers;
	}
	public List<CourseResult> getCourses() {
		return courses;
	}
	public void setCourses(List<CourseResult> courses) {
		this.courses = courses;
	}
	public String getOccupationUri() {
		return occupationUri;
	}
	public void setOccupationUri(String occupationUri) {
		this.occupationUri = occupationUri;
	}
	public String getIscoCode() {
		return iscoCode;
	}
	public void setIscoCode(String iscoCode) {
		this.iscoCode = iscoCode;
	}
}
