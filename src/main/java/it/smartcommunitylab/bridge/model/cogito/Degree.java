package it.smartcommunitylab.bridge.model.cogito;

import java.util.ArrayList;
import java.util.List;

public class Degree {
	private List<String> subjects = new ArrayList<>();	
	private List<String> years = new ArrayList<>();
	private String description;
	private List<String> patenti = new ArrayList<>();
	private List<String> organizations = new ArrayList<>();
	
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public List<String> getYears() {
		return years;
	}
	public void setYears(List<String> years) {
		this.years = years;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getPatenti() {
		return patenti;
	}
	public void setPatenti(List<String> patenti) {
		this.patenti = patenti;
	}
	public List<String> getOrganizations() {
		return organizations;
	}
	public void setOrganizations(List<String> organizations) {
		this.organizations = organizations;
	}
	
}
