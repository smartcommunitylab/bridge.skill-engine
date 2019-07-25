package it.smartcommunitylab.bridge.model.cogito;

import java.util.ArrayList;
import java.util.List;

public class CogitoProfile {
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
}
