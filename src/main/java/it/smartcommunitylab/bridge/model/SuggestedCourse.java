package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

public class SuggestedCourse {
	private List<ResourceLink> occupationsLink = new ArrayList<>();
	private String title;
	private String content;
	private List<ResourceLink> skillsLink = new ArrayList<>();
	
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
	public List<ResourceLink> getSkillsLink() {
		return skillsLink;
	}
	public void setSkillsLink(List<ResourceLink> skillsLink) {
		this.skillsLink = skillsLink;
	}
	public List<ResourceLink> getOccupationsLink() {
		return occupationsLink;
	}
	public void setOccupationsLink(List<ResourceLink> occupationsLink) {
		this.occupationsLink = occupationsLink;
	}
	
}
