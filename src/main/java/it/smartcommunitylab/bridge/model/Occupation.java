package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Occupation {
	@Id
	private String uri;
	private String conceptType;
	private String preferredLabel;
	private String altLabels;
	private String description;
	private String broaderIscoGroup;
	private String iscoCode;
	private List<String> hasEssentialSkill = new ArrayList<String>();
	private List<String> hasOptionalSkill = new ArrayList<String>();
	private List<String> totalSkill = new ArrayList<String>();
	private List<String> narrowerOccupation = new ArrayList<String>();
	private List<String> broaderOccupation = new ArrayList<String>();
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if (!(obj instanceof Occupation)) {
      return false;
    }
		Occupation occupation = (Occupation) obj;
		return this.uri.equals(occupation.getUri());
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
	public String getPreferredLabel() {
		return preferredLabel;
	}
	public void setPreferredLabel(String preferredLabel) {
		this.preferredLabel = preferredLabel;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBroaderIscoGroup() {
		return broaderIscoGroup;
	}
	public void setBroaderIscoGroup(String broaderIscoGroup) {
		this.broaderIscoGroup = broaderIscoGroup;
	}
	public List<String> getHasEssentialSkill() {
		return hasEssentialSkill;
	}
	public void setHasEssentialSkill(List<String> hasEssentialSkill) {
		this.hasEssentialSkill = hasEssentialSkill;
	}
	public List<String> getHasOptionalSkill() {
		return hasOptionalSkill;
	}
	public void setHasOptionalSkill(List<String> hasOptionalSkill) {
		this.hasOptionalSkill = hasOptionalSkill;
	}
	public List<String> getNarrowerOccupation() {
		return narrowerOccupation;
	}
	public void setNarrowerOccupation(List<String> narrowerOccupation) {
		this.narrowerOccupation = narrowerOccupation;
	}
	public List<String> getBroaderOccupation() {
		return broaderOccupation;
	}
	public void setBroaderOccupation(List<String> broaderOccupation) {
		this.broaderOccupation = broaderOccupation;
	}
	public String getAltLabels() {
		return altLabels;
	}
	public void setAltLabels(String altLabels) {
		this.altLabels = altLabels;
	}
	public String getIscoCode() {
		return iscoCode;
	}
	public void setIscoCode(String iscoCode) {
		this.iscoCode = iscoCode;
	}

	public List<String> getTotalSkill() {
		return totalSkill;
	}

	public void setTotalSkill(List<String> totalSkill) {
		this.totalSkill = totalSkill;
	}
}
