package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class JobOffer {
	@Id
	private String id;
	private String extUri;
	private String desc;
	private String istatCode;
	private String iscoCode;
	private List<String> occupations = new ArrayList<>();
	
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
	public String getIstatCode() {
		return istatCode;
	}
	public void setIstatCode(String istatCode) {
		this.istatCode = istatCode;
	}
	public String getIscoCode() {
		return iscoCode;
	}
	public void setIscoCode(String iscoCode) {
		this.iscoCode = iscoCode;
	}
	public List<String> getOccupations() {
		return occupations;
	}
	public void setOccupations(List<String> occupations) {
		this.occupations = occupations;
	}

}
