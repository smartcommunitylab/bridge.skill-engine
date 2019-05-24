package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class JobOffer {
	@Id
	private String id;
	private String extUri;
	private String offerRef;
	private String title;
	private String description;
	private String istatCode;
	private String istatPosition;
	private String iscoCode;
	private Date expirationDate;
	private String sector;
	private String professionalGroup;
	private String workContract;
	private String workPlace;
	private double[] geocoding; // lon,lat
	
	private List<String> occupations = new ArrayList<>();
	private List<ResourceLink> occupationsLink = new ArrayList<>();
	
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
	public List<ResourceLink> getOccupationsLink() {
		return occupationsLink;
	}
	public void setOccupationsLink(List<ResourceLink> occupationsLink) {
		this.occupationsLink = occupationsLink;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIstatPosition() {
		return istatPosition;
	}
	public void setIstatPosition(String istatPosition) {
		this.istatPosition = istatPosition;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getProfessionalGroup() {
		return professionalGroup;
	}
	public void setProfessionalGroup(String professionalGroup) {
		this.professionalGroup = professionalGroup;
	}
	public String getWorkContract() {
		return workContract;
	}
	public void setWorkContract(String workContract) {
		this.workContract = workContract;
	}
	public String getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public String getOfferRef() {
		return offerRef;
	}
	public void setOfferRef(String offerRef) {
		this.offerRef = offerRef;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
