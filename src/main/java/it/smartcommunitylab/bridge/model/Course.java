package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Course {
	@Id
	private String id;
	private String extUri;
	private String desc;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
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

}
