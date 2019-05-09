package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class Skill {
	@Id
	private String uri;
	private String conceptType;
	private Map<String, String> preferredLabel = new HashMap<>();
	private Map<String, String> altLabels = new HashMap<>();
	private Map<String, String> description = new HashMap<>();
	private List<String> broaderSkill = new ArrayList<String>();
	private List<String> narrowerSkill = new ArrayList<String>();
	private List<String> isEssentialForOccupation = new ArrayList<String>();
	private List<String> isOptionalForOccupation = new ArrayList<String>();

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if (!(obj instanceof Skill)) {
      return false;
    }
		Skill skill = (Skill) obj;
		return this.uri.equals(skill.getUri());
	}
	
	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}

	public String getConceptType() {
		return conceptType;
	}
	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public List<String> getIsEssentialForOccupation() {
		return isEssentialForOccupation;
	}
	public void setIsEssentialForOccupation(List<String> isEssentialForOccupation) {
		this.isEssentialForOccupation = isEssentialForOccupation;
	}
	public List<String> getIsOptionalForOccupation() {
		return isOptionalForOccupation;
	}
	public void setIsOptionalForOccupation(List<String> isOptionalForOccupation) {
		this.isOptionalForOccupation = isOptionalForOccupation;
	}
	public List<String> getBroaderSkill() {
		return broaderSkill;
	}
	public void setBroaderSkill(List<String> broaderSkill) {
		this.broaderSkill = broaderSkill;
	}
	public List<String> getNarrowerSkill() {
		return narrowerSkill;
	}
	public void setNarrowerSkill(List<String> narrowerSkill) {
		this.narrowerSkill = narrowerSkill;
	}

	public Map<String, String> getPreferredLabel() {
		return preferredLabel;
	}

	public void setPreferredLabel(Map<String, String> preferredLabel) {
		this.preferredLabel = preferredLabel;
	}

	public Map<String, String> getAltLabels() {
		return altLabels;
	}

	public void setAltLabels(Map<String, String> altLabels) {
		this.altLabels = altLabels;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
}
