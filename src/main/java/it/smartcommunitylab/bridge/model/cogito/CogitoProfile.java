package it.smartcommunitylab.bridge.model.cogito;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class CogitoProfile {
	@Id
	String id;	
	String filename;
	PersonalData personalData;
	List<WorkExperience> workExperiences = new ArrayList<>();
	List<Degree> degrees = new ArrayList<>();
	List<Language> languages = new ArrayList<>();
	List<String> itKnowledges = new ArrayList<>();
	
	public PersonalData getPersonalData() {
		return personalData;
	}
	public void setPersonalData(PersonalData personalData) {
		this.personalData = personalData;
	}
	public List<WorkExperience> getWorkExperiences() {
		return workExperiences;
	}
	public void setWorkExperiences(List<WorkExperience> workExperiences) {
		this.workExperiences = workExperiences;
	}
	public List<Degree> getDegrees() {
		return degrees;
	}
	public void setDegrees(List<Degree> degrees) {
		this.degrees = degrees;
	}
	public List<Language> getLanguages() {
		return languages;
	}
	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}
	public List<String> getItKnowledges() {
		return itKnowledges;
	}
	public void setItKnowledges(List<String> itKnowledges) {
		this.itKnowledges = itKnowledges;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
