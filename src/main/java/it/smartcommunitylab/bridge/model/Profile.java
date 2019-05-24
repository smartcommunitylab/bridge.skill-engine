package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Profile {
	@Id
	private String id;
	private String extId;
	private List<String> skills = new ArrayList<>();
	private List<ResourceLink> skillsLink = new ArrayList<>();
	private List<String> occupations = new ArrayList<>();
	private List<ResourceLink> occupationsLink = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExtId() {
		return extId;
	}
	public void setExtId(String extId) {
		this.extId = extId;
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
	public List<String> getOccupations() {
		return occupations;
	}
	public void setOccupations(List<String> occupations) {
		this.occupations = occupations;
	}
	public List<ResourceLink> getOccupationsLink() {
		return occupationsLink;
	}
	public void setOccupationsLink(List<ResourceLink> occupationsLink) {
		this.occupationsLink = occupationsLink;
	}
	
}
