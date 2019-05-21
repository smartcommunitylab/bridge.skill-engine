package it.smartcommunitylab.bridge.model;

import org.springframework.data.annotation.Id;

public class IscoIstat {
	@Id
	private String id;
	private String istatCode;
	private String istatName;
	private String iscoCode;
	private String iscoName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIstatCode() {
		return istatCode;
	}
	public void setIstatCode(String istatCode) {
		this.istatCode = istatCode;
	}
	public String getIstatName() {
		return istatName;
	}
	public void setIstatName(String istatName) {
		this.istatName = istatName;
	}
	public String getIscoCode() {
		return iscoCode;
	}
	public void setIscoCode(String iscoCode) {
		this.iscoCode = iscoCode;
	}
	public String getIscoName() {
		return iscoName;
	}
	public void setIscoName(String iscoName) {
		this.iscoName = iscoName;
	}
}
