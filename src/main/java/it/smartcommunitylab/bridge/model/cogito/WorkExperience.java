package it.smartcommunitylab.bridge.model.cogito;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.smartcommunitylab.bridge.model.ResourceLink;

public class WorkExperience {
	@JsonProperty("DESCRIPTION")
	private String DESCRIPTION;
	
	@JsonProperty("BEGINNING")
	private String BEGINNING;
	
	@JsonProperty("END")
	private String END;
	
	@JsonProperty("POSITIONS")
	private List<String> POSITIONS = new ArrayList<String>();
	
	@JsonProperty("ORGANIZATIONS")
	private List<String> ORGANIZATIONS = new ArrayList<String>();
	
	@JsonProperty("NON_ORGANIZATION")
	private String NON_ORGANIZATION;
	
	@JsonProperty("SECTOR")
	private String SECTOR;
	
	private List<ResourceLink> occupationsLink = new ArrayList<>();
	
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getBEGINNING() {
		return BEGINNING;
	}
	public void setBEGINNING(String bEGINNING) {
		BEGINNING = bEGINNING;
	}
	public String getEND() {
		return END;
	}
	public void setEND(String eND) {
		END = eND;
	}
	public List<String> getPOSITIONS() {
		return POSITIONS;
	}
	public void setPOSITIONS(List<String> pOSITIONS) {
		POSITIONS = pOSITIONS;
	}
	public List<String> getORGANIZATIONS() {
		return ORGANIZATIONS;
	}
	public void setORGANIZATIONS(List<String> oRGANIZATIONS) {
		ORGANIZATIONS = oRGANIZATIONS;
	}
	public String getNON_ORGANIZATION() {
		return NON_ORGANIZATION;
	}
	public void setNON_ORGANIZATION(String nON_ORGANIZATION) {
		NON_ORGANIZATION = nON_ORGANIZATION;
	}
	public String getSECTOR() {
		return SECTOR;
	}
	public void setSECTOR(String sECTOR) {
		SECTOR = sECTOR;
	}
	public List<ResourceLink> getOccupationsLink() {
		return occupationsLink;
	}
	public void setOccupationsLink(List<ResourceLink> occupationsLink) {
		this.occupationsLink = occupationsLink;
	}
}
